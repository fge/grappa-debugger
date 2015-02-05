package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.GrappaDebuggerException;
import com.github.fge.grappa.debugger.common.db.RuleInvocationStatistics;
import com.github.fge.grappa.debugger.csvtrace.newmodel.InputText;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseInfo;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTree;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.matchers.MatcherType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public interface CsvTraceModel
{
    @Nonnull
    ParseInfo getParseInfo();

    @Nonnull
    InputText getInputText()
        throws GrappaDebuggerException;

    void waitForNodes()
        throws GrappaDebuggerException;

    void waitForMatchers()
        throws GrappaDebuggerException;

    @Nonnull
    ParseTree getParseTree()
        throws GrappaDebuggerException;

    @Nonnull
    List<ParseTreeNode> getNodeChildren(int nodeId)
        throws GrappaDebuggerException;

    // TODO: replace with better exception
    void dispose()
        throws GrappaDebuggerException;

    @Nonnull
    ParseTreeNode getNodeById(int id)
        throws GrappaDebuggerException;

    @Nonnull
    Map<MatcherType, Integer> getMatchersByType()
        throws GrappaDebuggerException;

    boolean isLoadComplete();

    @Nonnull
    List<RuleInvocationStatistics> getRuleInvocationStatistics();
}
