package com.github.fge.grappa.debugger.model;

import com.github.fge.grappa.debugger.ParseInfo;
import com.github.fge.grappa.debugger.model.matches.MatchesData;
import com.github.fge.grappa.debugger.model.rules.PerClassStatistics;
import com.github.fge.grappa.debugger.model.tree.InputText;
import com.github.fge.grappa.debugger.model.tree.ParseTree;
import com.github.fge.grappa.debugger.model.tree.ParseTreeNode;
import com.github.fge.grappa.matchers.MatcherType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public interface TraceModel
{
    @Nonnull
    ParseInfo getParseInfo();

    @Nonnull
    InputText getInputText();

    @Nonnull
    ParseTree getParseTree();

    @Nonnull
    List<ParseTreeNode> getNodeChildren(int nodeId);

    @Nonnull
    ParseTreeNode getNodeById(int id);

    @Nonnull
    Map<MatcherType, Integer> getMatchersByType();

    @Nonnull
    List<PerClassStatistics> getRulesByClass();

    @Nonnull
    Map<Integer, Integer> getDepthMap(int startLine, int wantedLines);

    @Nonnull
    MatchesData getMatchesData();
}
