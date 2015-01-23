package com.github.fge.grappa.debugger.legacy.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.legacy.InputTextInfo;
import com.github.fge.grappa.debugger.legacy.LegacyTraceEvent;
import com.github.fge.grappa.debugger.legacy.RuleStatistics;
import com.github.fge.grappa.debugger.statistics.ParseNode;
import org.parboiled.support.Position;

import java.util.Collection;
import java.util.List;

public interface LegacyTraceTabView
{
    void setTraceEvents(List<LegacyTraceEvent> events);

    void setStatistics(Collection<RuleStatistics> values);

    void setParseDate(long startDate);

    void setInputTextInfo(InputTextInfo textInfo);

    void setInputText(String inputText);

    void setParseTree(ParseNode node);

    void fillParseNodeDetails(ParseNode node, InputBuffer buffer);

    void highlightText(List<String> fragments, Position position,
        boolean success);

    void expandParseTree();
}
