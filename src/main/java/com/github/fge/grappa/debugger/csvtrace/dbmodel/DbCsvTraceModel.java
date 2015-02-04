package com.github.fge.grappa.debugger.csvtrace.dbmodel;

import com.github.fge.grappa.buffers.CharSequenceInputBuffer;
import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.common.db.DbLoader;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.csvtrace.newmodel.InputText;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseInfo;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTree;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.debugger.csvtrace.newmodel.RuleInfo;
import com.github.fge.grappa.matchers.MatcherType;
import com.github.fge.lambdas.functions.ThrowingFunction;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.impl.DSL;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

import static com.github.fge.grappa.debugger.jooq.Tables.MATCHERS;
import static com.github.fge.grappa.debugger.jooq.Tables.NODES;

public class DbCsvTraceModel
    implements CsvTraceModel
{
    // Keep jooq quiet
    static {
        LogManager.getLogManager().reset();
    }

    private static final Charset UTF8 = StandardCharsets.UTF_8;

    private static final String INPUT_TEXT_PATH = "/input.txt";

    private static final ThreadFactory THREAD_FACTORY
        = new ThreadFactoryBuilder().setNameFormat("db-loader-%d")
        .setDaemon(true).build();

    private final ExecutorService executor
        = Executors.newSingleThreadExecutor(THREAD_FACTORY);

    private final FileSystem zipfs;
    private final DbLoader loader;
    private final DSLContext jooq;
    private final ParseInfo info;
    private final Future<DSLContext> future;

    private InputBuffer inputBuffer = null;

    public DbCsvTraceModel(final FileSystem zipfs, final DbLoader loader,
        final ParseInfo info)
    {
        this.zipfs = Objects.requireNonNull(zipfs);
        this.info = Objects.requireNonNull(info);
        this.loader = Objects.requireNonNull(loader);

        jooq = loader.getJooq();
        future = executor.submit(loader::loadAll);
    }

    @Override
    public InputText getInputText()
        throws IOException
    {
        if (inputBuffer == null)
            inputBuffer = readInputBuffer();
        return new InputText(info.getNrLines(), info.getNrChars(),
            info.getNrCodePoints(), inputBuffer);
    }

    @SuppressWarnings("AutoUnboxing")
    @Nullable
    @Override
    public ParseTree getParseTree()
        throws ExecutionException
    {
        final ParseTreeNode node = getParseTreeNodeFromId(0);

        return node == null ? null
            : new ParseTree(node, info.getNrInvocations(), info.getTreeDepth());
    }

    @Nullable
    @SuppressWarnings("AutoUnboxing")
    private ParseTreeNode getParseTreeNodeFromId(final Integer id)
        throws ExecutionException
    {
        final DSLContext dsl;
        try {
            dsl = future.get();
        } catch (InterruptedException | CancellationException ignored) {
            return null;
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

    private InputBuffer readInputBuffer()
        throws IOException
    {
        final Path path = zipfs.getPath(INPUT_TEXT_PATH);

        try (
            final BufferedReader reader = Files.newBufferedReader(path, UTF8);
        ) {
            final CharBuffer buf = CharBuffer.allocate(info.getNrChars());
            reader.read(buf);
            buf.flip();
            return new CharSequenceInputBuffer(buf);
        }
    }

    @Override
    public void dispose()
        throws IOException, SQLException
    {
        IOException exception = null;
        try {
            zipfs.close();
        } catch (IOException e) {
            exception = e;
        }

        future.cancel(true);

        try {
            loader.close();
        } catch (SQLException e) {
            if (exception != null)
                exception.addSuppressed(e);
            throw e;
        }

        if (exception != null)
            throw exception;
    }
}
