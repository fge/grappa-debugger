package com.github.fge.grappa.debugger;

import org.jooq.DSLContext;

public interface TraceDb
{
    TraceDbLoadStatus getLoadStatus();

    ParseInfo getParseInfo();

    DSLContext getJooq();
}
