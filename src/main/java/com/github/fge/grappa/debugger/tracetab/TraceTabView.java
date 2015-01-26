package com.github.fge.grappa.debugger.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.github.fge.grappa.debugger.tracetab.stat.classdetails
    .ClassDetailsStatsPresenter;
import com.github.fge.grappa.debugger.tracetab.stat.global.GlobalStatsPresenter;
import com.github.fge.grappa.debugger.tracetab.stat.perclass
    .PerClassStatsPresenter;
import com.github.fge.grappa.trace.ParseRunInfo;
import com.github.fge.grappa.trace.TraceEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;

@ParametersAreNonnullByDefault
public interface TraceTabView
{
    void setInputText(InputBuffer inputBuffer);

    void setInfo(ParseRunInfo info);

    void setEvents(List<TraceEvent> events);

    void setParseTree(ParseNode rootNode);

    void expandParseTree();

    void showParseNode(ParseNode node);

    void highlightFailedMatch(int failedIndex);

    void highlightSuccessfulMatch(int startIndex, int endIndex);

    void loadGlobalStats(GlobalStatsPresenter presenter)
        throws IOException;

    void loadPerClassStats(PerClassStatsPresenter presenter)
        throws IOException;

    void loadClassDetailsStats(ClassDetailsStatsPresenter presenter)
        throws IOException;
}
