package com.github.fge.grappa.debugger.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.trace.ParseRunInfo;
import com.github.fge.grappa.trace.TraceEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface TraceTabView
{
    void setInputText(InputBuffer inputBuffer);

    void setInfo(ParseRunInfo info);

    void setEvents(List<TraceEvent> events);
}
