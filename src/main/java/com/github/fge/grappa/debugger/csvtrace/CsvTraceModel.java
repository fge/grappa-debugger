package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.github.fge.grappa.trace.ParseRunInfo;

import java.io.IOException;

public interface CsvTraceModel
{
    ParseRunInfo getParseRunInfo()
        throws IOException;

    ParseNode getRootNode()
        throws IOException;

    InputBuffer getInputBuffer()
        throws IOException;

    void dispose()
        throws IOException;
}
