package com.github.parboiled1.grappa.debugger.tracetab;

import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.github.parboiled1.grappa.trace.ParsingRunTrace;

import javax.annotation.Nonnull;

public interface TraceTabModel
{
    @Nonnull
    ParsingRunTrace getTrace();

    @Nonnull
    InputBuffer getInputText();
}
