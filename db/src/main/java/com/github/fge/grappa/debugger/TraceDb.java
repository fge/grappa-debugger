package com.github.fge.grappa.debugger;

import com.github.fge.grappa.buffers.InputBuffer;
import org.jooq.DSLContext;

public interface TraceDb
    extends AutoCloseable
{
    TraceDbLoadStatus getLoadStatus();

    ParseInfo getParseInfo();

    InputBuffer getInputBuffer();

    DSLContext getJooq();
}
