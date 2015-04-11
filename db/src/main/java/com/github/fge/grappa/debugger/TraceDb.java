package com.github.fge.grappa.debugger;

import com.github.fge.grappa.debugger.model.tree.InputText;
import org.jooq.DSLContext;

public interface TraceDb
    extends AutoCloseable
{
    TraceDbLoadStatus getLoadStatus();

    ParseInfo getParseInfo();

    InputText getInputText();

    DSLContext getJooq();
}
