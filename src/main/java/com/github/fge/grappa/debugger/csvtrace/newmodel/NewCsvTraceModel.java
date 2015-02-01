package com.github.fge.grappa.debugger.csvtrace.newmodel;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.github.fge.grappa.exceptions.GrappaException;
import com.github.fge.grappa.matchers.MatcherType;
import com.github.fge.grappa.trace.ParseRunInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    private static final String MATCHER_DESCRIPTOR_CSV_HEAD
        = "id;className;type;name";
    private static final String NODE_CSV_HEAD
        = "parent;id;level;success;matcherId;start;end;time";
    private static final String INFO_CSV_HEAD
        = "startDate;treeDepth;nrMatchers;nrLines;nrChars;nrCodePoints;"
        + "nrNodes";

    private static final Map<String, ?> ENV
        = Collections.singletonMap("readonly", "true");
    private static final String NODE_PATH = "/nodes.csv";
    private static final String MATCHERS_PATH = "/matchers.csv";
    private static final String INPUT_TEXT_PATH = "/input.txt";
    private static final String INFO_PATH = "/info.csv";

    private final ParseInfo info;
    private final RuleInfo[] ruleInfos;

    public NewCsvTraceModel(final Path zipPath)
        throws IOException
    {
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
            ruleInfos = readRuleInfos(zipfs, info.getNrMatchers());
        }
    }

    private ParseInfo readInfo(final FileSystem zipfs)
        throws IOException
    {
        final Path path = zipfs.getPath(INFO_PATH);

        try (
            final BufferedReader reader = Files.newBufferedReader(path, UTF8);
        ) {
            if (!INFO_CSV_HEAD.equals(reader.readLine()))
                throw new GrappaException("unrecognized trace file: info.csv "
                    + "has an incorrect format");
            final String[] elements = SEMICOLON.split(reader.readLine());

            final long epoch = Long.parseLong(elements[0]);
            final LocalDateTime time = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(epoch), ZoneId.systemDefault());

            final int treeDepth = Integer.parseInt(elements[1]);
            final int nrMatchers = Integer.parseInt(elements[2]);
            final int nrLines = Integer.parseInt(elements[3]);
            final int nrChars = Integer.parseInt(elements[4]);
            final int nrCodePoints = Integer.parseInt(elements[4]);
            final int nrInvocations = Integer.parseInt(elements[4]);

            return new ParseInfo(time, treeDepth, nrMatchers, nrLines, nrChars,
                nrCodePoints, nrInvocations);
        }
    }

    private RuleInfo[] readRuleInfos(final FileSystem zipfs, final int nrRules)
        throws IOException
    {
        final Path path = zipfs.getPath(MATCHERS_PATH);
        final List<String> lines = Files.readAllLines(path, UTF8);

        if (!MATCHER_DESCRIPTOR_CSV_HEAD.equals(lines.remove(0)))
            throw new GrappaException("unrecognized trace file: matchers file "
                + "has an illegal format");

        final RuleInfo[] ret = new RuleInfo[nrRules];
        lines.parallelStream().forEach(line -> {
            final String[] elements = SEMICOLON.split(line, 4);
            final int index = Integer.parseInt(elements[0]);
            final MatcherType type = MatcherType.valueOf(elements[2]);
            ret[index] = new RuleInfo(elements[1], type, elements[3]);
        });

        return ret;
    }

    @Override
    public ParseRunInfo getParseRunInfo()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ParseNode getRootNode()
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputBuffer getInputBuffer()
        throws IOException
    {
        // TODO
        return null;
    }

    @Override
    public void dispose()
        throws IOException
    {
    }
}
