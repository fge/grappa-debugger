package com.github.fge.grappa.debugger;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.model.TraceModel;
import org.jooq.DSLContext;

public interface TraceDb
    extends AutoCloseable
{
    DSLContext getJooq();

    TraceDbLoadStatus getLoadStatus();

    ParseInfo getParseInfo();

    InputBuffer getInputBuffer();

    TraceModel getModel();
}
