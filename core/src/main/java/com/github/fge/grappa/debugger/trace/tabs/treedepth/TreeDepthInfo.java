package com.github.fge.grappa.debugger.trace.tabs.treedepth;

import com.github.fge.grappa.debugger.model.TraceModel;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;

import java.util.Collections;
import java.util.Map;

@SuppressWarnings({ "AutoBoxing", "AutoUnboxing" })
public final class TreeDepthInfo
{
    private static final long INITIAL_START_LINE = 1L;
    private static final long INITIAL_DISPLAYED_LINES = 25L;

    private static final DiscreteDomain<Long> LONGS
        = DiscreteDomain.longs();

    private final long nrLines;
    private final Range<Long> lines;
    private final TraceModel model;

    private long startLine = INITIAL_START_LINE;
    private long displayedLines = INITIAL_DISPLAYED_LINES;

    private boolean hasPrevious = false;
    private boolean hasNext = false;

    private long endLine;

    private Map<Integer, Integer> depths;

    public TreeDepthInfo(final int nrLines, final TraceModel model)
    {
        this.nrLines = nrLines;
        this.model = model;

        lines = Range.closed(1L, this.nrLines);

        update();
    }

    public int getStartLine()
    {
        return (int) startLine;
    }

    public int getEndLine()
    {
        return (int) endLine;
    }

    public boolean hasPreviousLines()
    {
        return hasPrevious;
    }

    public boolean hasNextLines()
    {
        return hasNext;
    }

    public void setStartLine(final int startLine)
    {
        this.startLine = startLine;
        update();
    }

    public void setDisplayedLines(final int displayedLines)
    {
        this.displayedLines = displayedLines;
        update();
    }

    public Map<Integer, Integer> getDepths()
    {
        return Collections.unmodifiableMap(depths);
    }

    private void update()
    {
        if (startLine > nrLines - displayedLines + 1)
            startLine = nrLines - displayedLines + 1;
        if (startLine < 1)
            startLine = 1;

        final Range<Long> range
            = Range.closedOpen(startLine, startLine + displayedLines)
            .intersection(lines).canonical(LONGS);

        endLine = range.upperEndpoint() - 1;

        hasPrevious = startLine > 1;
        hasNext = endLine < nrLines;

        // TODO: change prototype of method?
        // We need +2 because the end is exclusive
        depths = model.getDepthMap((int) startLine,
            (int) (endLine - startLine + 2));
    }

    public void nextLines()
    {
        startLine += displayedLines;
        update();
    }

    public void previousLines()
    {
        startLine -= displayedLines;
        update();
    }
}
