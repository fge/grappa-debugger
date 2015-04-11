package com.github.fge.grappa.debugger.model;

import com.github.fge.grappa.buffers.CharSequenceInputBuffer;
import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.GrappaDebuggerException;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.model.common.ParseInfo;
import com.github.fge.grappa.debugger.model.common.RuleInfo;
import com.github.fge.grappa.debugger.model.db.DbLoadStatus;
import com.github.fge.grappa.debugger.model.db.DbLoader;
import com.github.fge.grappa.debugger.model.tabs.matches.MatchStatisticsMapper;
import com.github.fge.grappa.debugger.model.tabs.matches.MatchesData;
import com.github.fge.grappa.debugger.model.tabs.rules.PerClassStatistics;
import com.github.fge.grappa.debugger.model.tabs.rules.PerClassStatisticsMapper;
import com.github.fge.grappa.debugger.model.tabs.tree.InputText;
import com.github.fge.grappa.debugger.model.tabs.tree.ParseTree;
import com.github.fge.grappa.debugger.model.tabs.tree.ParseTreeNode;
import com.github.fge.grappa.matchers.MatcherType;
import com.github.fge.grappa.support.IndexRange;
import com.github.fge.lambdas.functions.ThrowingFunction;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.jooq.CaseConditionStep;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.impl.DSL;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.fge.grappa.debugger.jooq.Tables.MATCHERS;
import static com.github.fge.grappa.debugger.jooq.Tables.NODES;

@SuppressWarnings({ "AutoBoxing", "AutoUnboxing" })
public class DbCsvTraceModel
    implements CsvTraceModel
{
    private static final Condition EMPTY_MATCHES_CONDITION = NODES.SUCCESS.eq(1)
        .and(NODES.START_INDEX.equal(NODES.END_INDEX));
    private static final Condition NONEMPTY_MATCHES_CONDITION
        = NODES.SUCCESS.eq(1).and(NODES.START_INDEX.ne(NODES.END_INDEX));
    private static final Condition FAILED_MATCHES_CONDITION
        = NODES.SUCCESS.eq(0);

    private static final MatchStatisticsMapper MATCH_STATISTICS_MAPPER
        = new MatchStatisticsMapper();

    private static final Pattern SEMICOLON = Pattern.compile(";");
    private static final Charset UTF8 = StandardCharsets.UTF_8;

    private static final String INPUT_TEXT_PATH = "/input.txt";
    private static final String INFO_PATH = "/info.csv";

    private static final ThreadFactory THREAD_FACTORY
        = new ThreadFactoryBuilder().setNameFormat("db-loader-%d")
        .setDaemon(true).build();

    private final ExecutorService executor
        = Executors.newSingleThreadExecutor(THREAD_FACTORY);

    private final FileSystem zipfs;
    private final DbLoader loader;
    private final DSLContext jooq;
    private final ParseInfo info;
    private final DbLoadStatus status;
    private final Future<DSLContext> future;

    private InputBuffer inputBuffer = null;

    public DbCsvTraceModel(final FileSystem zipfs, final DbLoader loader)
        throws IOException
    {
        this.zipfs = Objects.requireNonNull(zipfs);
        this.loader = Objects.requireNonNull(loader);

        info = readInfo();
        jooq = loader.getJooq();
        future = executor.submit(loader::loadAll);
        loader.createStatus(info);
        status = loader.getStatus();
    }

    @Override
    public boolean isLoadComplete()
    {
        try {
            return status.waitReady(1L, TimeUnit.MICROSECONDS);
        } catch (InterruptedException ignored) {
            return false;
        }
    }

    @Override
    public void waitForMatchers()
        throws GrappaDebuggerException
    {
        try {
            status.waitForMatchers();
        } catch (InterruptedException e) {
            throw new GrappaDebuggerException(e);
        }
    }

    @Nonnull
    @Override
    public ParseInfo getParseInfo()
    {
        return info;
    }

    @Nonnull
    @Override
    public InputText getInputText()
        throws GrappaDebuggerException
    {
        loadInputBuffer();
        return new InputText(info.getNrLines(), info.getNrChars(),
            info.getNrCodePoints(), inputBuffer);
    }

    @Nonnull
    @Override
    public ParseTree getParseTree()
        throws GrappaDebuggerException
    {
        final ParseTreeNode node = getParseTreeNodeFromId(0);

        return new ParseTree(node, info.getNrInvocations(),
            info.getTreeDepth());
    }

    @Nonnull
    @Override
    public List<ParseTreeNode> getNodeChildren(final int nodeId)
    {
        final Result<Record1<Integer>> result = jooq.select(NODES.ID)
            .from(NODES).where(NODES.PARENT_ID.equal(nodeId)).fetch();

        final ThrowingFunction<Integer, ParseTreeNode> function
            = this::getParseTreeNodeFromId;

        return result.stream()
            .map(Record1::value1)
            .map(function)
            .collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public ParseTreeNode getNodeById(final int id)
        throws GrappaDebuggerException
    {
        return getParseTreeNodeFromId(id);
    }

    @Nonnull
    @Override
    public Map<MatcherType, Integer> getMatchersByType()
        throws GrappaDebuggerException
    {
        waitForMatchers();
        final Map<MatcherType, Integer> ret = new EnumMap<>(MatcherType.class);

        jooq.select(MATCHERS.MATCHER_TYPE, DSL.count())
            .from(MATCHERS)
            .groupBy(MATCHERS.MATCHER_TYPE)
            .forEach(r -> ret.put(MatcherType.valueOf(r.value1()), r.value2()));

        return ret;
    }

    @Nonnull
    @Override
    public List<PerClassStatistics> getRulesByClass()
        throws GrappaDebuggerException
    {
        waitForMatchers();

        return jooq.select(MATCHERS.CLASS_NAME, DSL.count().as("nrCalls"),
            DSL.countDistinct(NODES.MATCHER_ID).as("nrRules"))
            .from(MATCHERS, NODES)
            .where(NODES.MATCHER_ID.eq(MATCHERS.ID))
            .groupBy(MATCHERS.CLASS_NAME)
            .fetch()
            .map(new PerClassStatisticsMapper());
    }

    @Nonnull
    @Override
    public MatchesData getMatchesData()
    {
        final Field<Integer> emptyMatches = DSL.decode()
            .when(EMPTY_MATCHES_CONDITION, 1).otherwise(0);
        final Field<Integer> nonEmptyMatches = DSL.decode()
            .when(NONEMPTY_MATCHES_CONDITION, 1).otherwise(0);
        final Field<Integer> failedMatches = DSL.decode()
            .when(FAILED_MATCHES_CONDITION, 1).otherwise(0);

        return jooq.select(MATCHERS.NAME, MATCHERS.MATCHER_TYPE,
            MATCHERS.CLASS_NAME,
            DSL.sum(emptyMatches).as("emptyMatches"),
            DSL.sum(nonEmptyMatches).as("nonEmptyMatches"),
            DSL.sum(failedMatches).as("failedMatches"))
            .from(MATCHERS, NODES)
            .where(MATCHERS.ID.eq(NODES.MATCHER_ID))
            .groupBy(MATCHERS.NAME, MATCHERS.MATCHER_TYPE, MATCHERS.CLASS_NAME)
            .fetch().stream()
            .map(MATCH_STATISTICS_MAPPER::map)
            .collect(MatchesData.asCollector());
    }

    @Override
    public void dispose()
        throws GrappaDebuggerException
    {
        Exception exception = null;
        try {
            zipfs.close();
        } catch (IOException e) {
            exception = e;
        }

        future.cancel(true);
        executor.shutdownNow();

        try {
            loader.close();
        } catch (SQLException | IOException e) {
            if (exception != null)
                exception.addSuppressed(e);
            else
                exception = e;
        }

        if (exception != null)
            throw new GrappaDebuggerException(exception);
    }

    private ParseInfo readInfo()
        throws IOException
    {
        final Path path = zipfs.getPath(INFO_PATH);

        try (
            final BufferedReader reader = Files.newBufferedReader(path, UTF8);
        ) {
            final String[] elements = SEMICOLON.split(reader.readLine());

            final long epoch = Long.parseLong(elements[0]);
            final Instant instant = Instant.ofEpochMilli(epoch);
            final ZoneId zone = ZoneId.systemDefault();
            final LocalDateTime time = LocalDateTime.ofInstant(instant, zone);

            final int treeDepth = Integer.parseInt(elements[1]);
            final int nrMatchers = Integer.parseInt(elements[2]);
            final int nrLines = Integer.parseInt(elements[3]);
            final int nrChars = Integer.parseInt(elements[4]);
            final int nrCodePoints = Integer.parseInt(elements[5]);
            final int nrInvocations = Integer.parseInt(elements[6]);

            return new ParseInfo(time, treeDepth, nrMatchers, nrLines, nrChars,
                nrCodePoints, nrInvocations);
        }
    }

    @Nonnull
    private ParseTreeNode getParseTreeNodeFromId(final Integer id)
        throws GrappaDebuggerException
    {
        final DSLContext dsl;
        try {
            dsl = future.get();
        } catch (InterruptedException | CancellationException
            | ExecutionException e) {
            throw new GrappaDebuggerException(e);
        }

        final Record nodeRecord = dsl.select(NODES.fields())
            .from(NODES)
            .where(NODES.ID.equal(id))
            .fetchOne();
        final Integer matcherId = nodeRecord.getValue(NODES.MATCHER_ID);
        final RuleInfo ruleInfo = getRuleInfoFromId(matcherId);
        final int nrChildren = dsl.select(DSL.count())
            .from(NODES)
            .where(NODES.PARENT_ID.equal(id))
            .fetchOne()
            .value1();
        return new ParseTreeNode(
            nodeRecord.getValue(NODES.PARENT_ID),
            nodeRecord.getValue(NODES.ID),
            nodeRecord.getValue(NODES.LEVEL),
            nodeRecord.getValue(NODES.SUCCESS) == 1,
            ruleInfo,
            nodeRecord.getValue(NODES.START_INDEX),
            nodeRecord.getValue(NODES.END_INDEX),
            nodeRecord.getValue(NODES.TIME),
            nrChildren != 0
        );
    }

    private RuleInfo getRuleInfoFromId(final Integer matcherId)
    {
        final Record matcherRecord = jooq.select(MATCHERS.fields())
            .from(MATCHERS)
            .where(MATCHERS.ID.equal(matcherId))
            .fetchOne();
        return new RuleInfo(matcherRecord.getValue(MATCHERS.CLASS_NAME),
            MatcherType.valueOf(matcherRecord.getValue(MATCHERS.MATCHER_TYPE)),
            matcherRecord.getValue(MATCHERS.NAME));
    }

    private synchronized void loadInputBuffer()
        throws GrappaDebuggerException
    {
        if (inputBuffer != null)
            return;

        final Path path = zipfs.getPath(INPUT_TEXT_PATH);

        try (
            final BufferedReader reader = Files.newBufferedReader(path, UTF8);
        ) {
            final CharBuffer buf = CharBuffer.allocate(info.getNrChars());
            reader.read(buf);
            buf.flip();
            inputBuffer = new CharSequenceInputBuffer(buf);
        } catch (IOException e) {
            throw new GrappaDebuggerException(e);
        }
    }

    @Nonnull
    @Override
    public Map<Integer, Integer> getDepthMap(final int startLine,
        final int wantedLines)
        throws GrappaDebuggerException
    {
        loadInputBuffer();

        final List<IndexRange> ranges
            = IntStream.range(startLine, startLine + wantedLines)
            .mapToObj(inputBuffer::getLineRange)
            .collect(Collectors.toList());

        final int startIndex = ranges.get(0).start;
        final int endIndex = ranges.get(ranges.size() - 1).end;
        final Condition indexCondition = NODES.START_INDEX.lt(endIndex)
            .and(NODES.END_INDEX.ge(startIndex));

        final Field<Integer> lineField = getLineField(startLine, ranges);

        final Map<Integer, Integer> ret = new HashMap<>();

        jooq.select(lineField, DSL.max(NODES.LEVEL))
            .from(NODES)
            .where(indexCondition)
            .groupBy(lineField)
            .forEach(r -> ret.put(r.value1(), r.value2() + 1));

        IntStream.range(startLine, startLine + wantedLines)
            .forEach(line -> ret.putIfAbsent(line, 0));

        return ret;
    }

    private Field<Integer> getLineField(final int startLine,
        final List<IndexRange> ranges)
    {
        CaseConditionStep<Integer> step = DSL.decode()
            .when(activeThisRange(ranges.get(0)), startLine);

        final int size = ranges.size();

        for (int i = 1; i < size; i++)
            step = step.when(activeThisRange(ranges.get(i)), startLine + i);

        return step.as("line");
    }

    private static Condition activeThisRange(final IndexRange range)
    {
        return NODES.START_INDEX.lt(range.end)
            .and(NODES.END_INDEX.ge(range.start));
    }
}
