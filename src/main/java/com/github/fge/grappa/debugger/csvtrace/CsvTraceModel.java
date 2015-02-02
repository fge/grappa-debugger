package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

public interface CsvTraceModel
{
    InputBuffer getInputBuffer()
        throws IOException;

    void dispose()
        throws IOException;

    @Nonnull
    ParseTreeNode getRootNode()
        throws IOException;

    List<ParseTreeNode> getNodeChildren(int nodeId);
}
