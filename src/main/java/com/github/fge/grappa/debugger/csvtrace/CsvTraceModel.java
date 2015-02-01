package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.debugger.csvtrace.newmodel.RuleInfo;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.github.fge.grappa.trace.ParseRunInfo;

import javax.annotation.Nonnull;
import java.io.IOException;

public interface CsvTraceModel
{
    ParseRunInfo getParseRunInfo();

    ParseNode getRootNode()
        throws IOException;

    InputBuffer getInputBuffer()
        throws IOException;

    void dispose()
        throws IOException;

    @Nonnull
    ParseTreeNode getRootNode2();

    @Nonnull
    RuleInfo getRuleInfo(int matcherId);
}
