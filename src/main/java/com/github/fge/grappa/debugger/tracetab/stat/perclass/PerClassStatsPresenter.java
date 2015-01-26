package com.github.fge.grappa.debugger.tracetab.stat.perclass;

import com.github.fge.grappa.debugger.tracetab.TraceTabModel;
import com.github.fge.grappa.trace.TraceEvent;
import com.github.fge.grappa.trace.TraceEventType;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class PerClassStatsPresenter
{
    private final Map<String, Long> perClass;

    private PerClassStatsView view;

    public PerClassStatsPresenter(final TraceTabModel model)
    {
        perClass = model.getEvents().stream()
            .filter(event -> event.getType() == TraceEventType.BEFORE_MATCH)
            .collect(Collectors.groupingBy(TraceEvent::getMatcherClass,
                Collectors.counting()));
    }

    @VisibleForTesting
    PerClassStatsPresenter()
    {
        perClass = Collections.emptyMap();
    }

    public void setView(final PerClassStatsView view)
    {
        this.view = Objects.requireNonNull(view);
    }

    public void loadStats()
    {
        view.loadPerClass(perClass);
    }
}
