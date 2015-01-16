package com.github.parboiled1.grappa.debugger.tracetab;

import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.github.parboiled1.grappa.trace.TraceEvent;

import java.util.List;

public interface TraceTabModel
{
    List<TraceEvent> getTraceEvents();

    InputBuffer getInputText();
}
