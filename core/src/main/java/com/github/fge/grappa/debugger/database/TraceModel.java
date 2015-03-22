package com.github.fge.grappa.debugger.database;

import com.github.fge.grappa.debugger.model.common.ParseInfo;
import com.github.fge.grappa.debugger.model.tabs.tree.InputText;
import org.jooq.DSLContext;

public interface TraceModel
    extends AutoCloseable
{
    DSLContext getJooq();

    TraceLoadStatus getLoadStatus();

    // TODO: replace somewhat and delay count of characters/code points?
    InputText getInputText();

    ParseInfo getParseInfo();
}
