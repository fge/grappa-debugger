package com.github.fge.grappa.debugger.csvtrace.newmodel;

import com.github.fge.grappa.buffers.CharSequenceInputBuffer;
import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.matchers.MatcherType;
import com.github.fge.lambdas.functions.ThrowingFunction;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class NewCsvTraceModel
    implements CsvTraceModel
{
    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private static final Pattern SEMICOLON = Pattern.compile(";");

    private static final Map<String, ?> ENV
        = Collections.singletonMap("readonly", "true");
    private static final String NODE_PATH = "/nodes.csv";
    private static final String MATCHERS_PATH = "/matchers.csv";
    private static final String INPUT_TEXT_PATH = "/input.txt";
    private static final String INFO_PATH = "/info.csv";

    private final FileSystem zipfs;

    private final ParseInfo info;
    private InputBuffer inputBuffer = null;
    private RuleInfo[] ruleInfos = null;

    private long[] nodeIndices = null;
    private List<Integer>[] childrenLists = null;

    public NewCsvTraceModel(final Path zipPath)
        throws IOException
    {
        final URI uri = URI.create("jar:" + zipPath.toUri());

        zipfs = FileSystems.newFileSystem(uri, ENV);
        info = readInfo();
    }

    @Nonnull
    @Override
    public ParseInfo getParseInfo()
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public InputText getInputText()
        throws IOException
    {
        if (inputBuffer == null)
            inputBuffer = readInputBuffer();
        return new InputText(info.getNrLines(), info.getNrChars(),
            info.getNrCodePoints(), inputBuffer);
    }

    @Nonnull
    @Override
    public ParseTree getParseTree()
        throws IOException
    {
        return new ParseTree(readNode(0), info.getNrInvocations(),
            info.getTreeDepth());
    }

    @Nonnull
    @Override
    public List<ParseTreeNode> getNodeChildren(final int nodeId)
    {
        final ThrowingFunction<Integer, ParseTreeNode> f
            = this::readNode;

        return childrenLists[nodeId].stream().map(f)
            .collect(Collectors.toList());
    }

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    private void computeIndices()
        throws IOException
    {
        final Path path = zipfs.getPath(NODE_PATH);

        final StringBuilder sb = new StringBuilder();
        final int total = info.getNrInvocations();
        nodeIndices = new long[total];
        //noinspection unchecked
        childrenLists = (List<Integer>[]) new List[total];

        try (
            final BufferedReader reader = Files.newBufferedReader(path, UTF8);
        ) {
            long previousIndex = 0;
            long index = 0;
            int found = 0;
            char c;
            String[] elements;
            int parentId;
            int nodeId;
            List<Integer> children;

            while (found < total) {
                index++;
                c = (char) reader.read();
                if (c != '\n') {
                    sb.append(c);
                    continue;
                }
                found++;
                elements = SEMICOLON.split(sb, 3);
                sb.setLength(0);
                parentId = Integer.parseInt(elements[0]);
                nodeId = Integer.parseInt(elements[1]);
                nodeIndices[nodeId] = previousIndex;
                previousIndex = index;
                if (parentId == -1) // root node; normally the last one as well
                    continue;
                children = childrenLists[parentId];
                if (children == null) {
                    children = new ArrayList<>();
                    childrenLists[parentId] = children;
                }
                children.add(nodeId);
            }
        }
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

    private void readRuleInfos()
        throws IOException
    {
        final int nrRules = info.getNrMatchers();
        final Path path = zipfs.getPath(MATCHERS_PATH);
        final List<String> lines = Files.readAllLines(path, UTF8);

        ruleInfos = new RuleInfo[nrRules];
        lines.parallelStream().forEach(line -> {
            final String[] elements = SEMICOLON.split(line, 4);
            final int index = Integer.parseInt(elements[0]);
            final MatcherType type = MatcherType.valueOf(elements[2]);
            ruleInfos[index] = new RuleInfo(elements[1], type, elements[3]);
        });
    }

    private ParseTreeNode readNode(final int nodeIndex)
        throws IOException
    {
        if (nodeIndices == null)
            computeIndices();
        if (ruleInfos == null)
            readRuleInfos();

        final Path path = zipfs.getPath(NODE_PATH);

        try (
            final BufferedReader reader = Files.newBufferedReader(path, UTF8);
        ) {
            reader.skip(nodeIndices[nodeIndex]);
            final String[] elements = SEMICOLON.split(reader.readLine());
            final int parentId = Integer.parseInt(elements[0]);
            final int nodeId = Integer.parseInt(elements[1]);
            final int level = Integer.parseInt(elements[2]);
            final boolean success = elements[3].charAt(0) == '1';
            final int matcherId = Integer.parseInt(elements[4]);
            final int startIndex = Integer.parseInt(elements[5]);
            final int endIndex = Integer.parseInt(elements[6]);
            final long time = Long.parseLong(elements[7]);
            return new ParseTreeNode(parentId, nodeId, level, success,
                ruleInfos[matcherId], startIndex, endIndex, time,
                childrenLists[nodeId] != null);
        }
    }

    @Override
    public void dispose()
        throws IOException
    {
        zipfs.close();
    }
}
