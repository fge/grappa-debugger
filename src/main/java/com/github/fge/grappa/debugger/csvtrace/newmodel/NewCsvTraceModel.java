package com.github.fge.grappa.debugger.csvtrace.newmodel;

import com.github.fge.grappa.buffers.CharSequenceInputBuffer;
import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.exceptions.GrappaException;
import com.github.fge.grappa.matchers.MatcherType;
import com.github.fge.grappa.trace.ParseRunInfo;
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
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private final Path zipPath;

    private final ParseInfo info;
    private final InputBuffer inputBuffer;
    private final RuleInfo[] ruleInfos;
    //private final ParseTreeNode[] parseTreeNodes;

    private final long[] nodeIndices;
    private final List<Integer>[] childrenLists;

    public NewCsvTraceModel(final Path zipPath)
        throws IOException
    {
        this.zipPath = zipPath;

        final URI uri = URI.create("jar:" + zipPath.toUri());

        try (
            final FileSystem zipfs = FileSystems.newFileSystem(uri, ENV);
        ) {
            final List<String> missing = Stream.of(NODE_PATH, MATCHERS_PATH,
                INPUT_TEXT_PATH, INFO_PATH).map(zipfs::getPath)
                .filter(path -> !Files.exists(path)).map(Object::toString)
                .collect(Collectors.toList());
            if (!missing.isEmpty())
                throw new GrappaException("unrecognized trace file: "
                    + "missing files (" + String.join(", ", missing));

            info = readInfo(zipfs);
            inputBuffer = readInputBuffer(zipfs, info.getNrChars());
            ruleInfos = readRuleInfos(zipfs, info.getNrMatchers());

            final int nrInvocations = info.getNrInvocations();
            nodeIndices = new long[nrInvocations];
            //noinspection unchecked
            childrenLists = (List<Integer>[]) new List[nrInvocations];
            computeIndices(zipfs, nodeIndices, childrenLists);
            //parseTreeNodes = readParseTreeNodes(zipfs, nrInvocations);
        }
    }

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    private void computeIndices(final FileSystem zipfs,
        final long[] nodeIndices, final List<Integer>[] childrenLists)
        throws IOException
    {
        final Path path = zipfs.getPath(NODE_PATH);

        final StringBuilder sb = new StringBuilder();
        final int total = nodeIndices.length;

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
                if (found % 25000 == 0)
                    System.out.println(found + " elements processed");
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

    private InputBuffer readInputBuffer(final FileSystem zipfs,
        final int nrChars)
        throws IOException
    {
        final Path path = zipfs.getPath(INPUT_TEXT_PATH);

        try (
            final BufferedReader reader = Files.newBufferedReader(path, UTF8);
        ) {
            final CharBuffer buf = CharBuffer.allocate(nrChars);
            reader.read(buf);
            buf.flip();
            return new CharSequenceInputBuffer(buf);
        }
    }

    private ParseInfo readInfo(final FileSystem zipfs)
        throws IOException
    {
        final Path path = zipfs.getPath(INFO_PATH);

        try (
            final BufferedReader reader = Files.newBufferedReader(path, UTF8);
        ) {
            final String[] elements = SEMICOLON.split(reader.readLine());

            final long epoch = Long.parseLong(elements[0]);
            final LocalDateTime time = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(epoch), ZoneId.systemDefault());

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

    private RuleInfo[] readRuleInfos(final FileSystem zipfs, final int nrRules)
        throws IOException
    {
        final Path path = zipfs.getPath(MATCHERS_PATH);
        final List<String> lines = Files.readAllLines(path, UTF8);

        final RuleInfo[] ret = new RuleInfo[nrRules];
        lines.parallelStream().forEach(line -> {
            final String[] elements = SEMICOLON.split(line, 4);
            final int index = Integer.parseInt(elements[0]);
            final MatcherType type = MatcherType.valueOf(elements[2]);
            ret[index] = new RuleInfo(elements[1], type, elements[3]);
        });

        return ret;
    }

    private ParseTreeNode readNode(final int nodeIndex)
        throws IOException
    {
        final URI uri = URI.create("jar:" + zipPath.toUri());

        try (
            final FileSystem zipfs = FileSystems.newFileSystem(uri, ENV);
            final BufferedReader reader
                = Files.newBufferedReader(zipfs.getPath(NODE_PATH), UTF8);
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

//    private ParseTreeNode[] readParseTreeNodes(final FileSystem zipfs,
//        final int nrNodes)
//        throws IOException
//    {
//        final Path path = zipfs.getPath(NODE_PATH);
//
//        final ParseTreeNode[] ret = new ParseTreeNode[nrNodes];
//
//        final AtomicInteger count = new AtomicInteger(0);
//        // Read all nodes
//        try (
//            final Stream<String> lines = Files.lines(path, UTF8).parallel();
//        ) {
//            lines.peek(ignored -> {
//                final int i = count.incrementAndGet();
//                if (i % 25000 == 0)
//                    System.out.println(i + " events processed");
//            })
//                .forEach(line -> {
//                final String[] elements = SEMICOLON.split(line);
//                final int parentId = Integer.parseInt(elements[0]);
//                final int nodeId = Integer.parseInt(elements[1]);
//                final int level = Integer.parseInt(elements[2]);
//                final boolean success = elements[3].charAt(0) == '1';
//                final int matcherId = Integer.parseInt(elements[4]);
//                final int startIndex = Integer.parseInt(elements[5]);
//                final int endIndex = Integer.parseInt(elements[6]);
//                final long time = Long.parseLong(elements[7]);
//                ret[nodeId] = new ParseTreeNode(parentId, nodeId, level,
//                    success, ruleInfos[matcherId], startIndex, endIndex, time);
//            });
//        }
//
//        // Now attach children. Skip first node, it will always have no parent
//        ParseTreeNode node;
//
//        for (int i = 1; i < nrNodes; i++) {
//            node = ret[i];
//            ret[node.getParentId()].addChild(node);
//        }
//
//        return ret;
//    }

    @Override
    public ParseRunInfo getParseRunInfo()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputBuffer getInputBuffer()
        throws IOException
    {
        return inputBuffer;
    }

    @Override
    public void dispose()
        throws IOException
    {
    }

    @Nonnull
    @Override
    public ParseTreeNode getRootNode2()
        throws IOException
    {
        return readNode(0);
    }

    @Override
    public List<ParseTreeNode> getNodeChildren(final int nodeId)
    {
        final ThrowingFunction<Integer, ParseTreeNode> f
            = this::readNode;

        return childrenLists[nodeId].stream().map(f)
            .collect(Collectors.toList());
    }

    public static void main(final String... args)
        throws IOException
    {
        final Path zip = Paths.get("/home/fge/tmp/foobar.zip");
        final NewCsvTraceModel model = new NewCsvTraceModel(zip);
        final URI uri = URI.create("jar:" + zip.toUri());
        final FileSystem fs = FileSystems.newFileSystem(uri, ENV);
        final BufferedReader reader
            = Files.newBufferedReader(fs.getPath(NODE_PATH));
        final long index = model.nodeIndices[0];
        reader.skip(index);
        System.out.println(reader.readLine());
    }
}
