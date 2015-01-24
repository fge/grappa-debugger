package com.github.fge.grappa.debugger.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.trace.ParseRunInfo;
import com.github.fge.grappa.trace.TraceEvent;

import javax.annotation.Nonnull;
import java.util.List;

public interface TraceTabModel
{
    @Nonnull
    InputBuffer getInputBuffer();

    @Nonnull
    ParseRunInfo getInfo();

    @Nonnull
    List<TraceEvent> getEvents();
}
