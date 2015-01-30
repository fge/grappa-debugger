package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.stats.ParseNode;

import java.io.IOException;

public interface CsvTraceModel
{
    ParseNode getRootNode()
        throws IOException;

    void dispose()
        throws IOException;
}
