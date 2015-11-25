package com.github.fge.grappa.debugger;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.model.TraceModel;

public interface TraceDb
    extends AutoCloseable
{
    TraceDbLoadStatus getLoadStatus();

    ParseInfo getParseInfo();

    InputBuffer getInputBuffer();

    TraceModel getModel();
}
