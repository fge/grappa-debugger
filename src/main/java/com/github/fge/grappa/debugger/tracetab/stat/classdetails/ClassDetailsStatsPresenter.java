package com.github.fge.grappa.debugger.tracetab.stat.classdetails;

import com.github.fge.grappa.debugger.stats.classdetails.MatcherClassDetails;
import com.github.fge.grappa.internal.NonFinalForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@NonFinalForTesting
public class ClassDetailsStatsPresenter
{
    private final ClassDetailsStatsModel model;

    private ClassDetailsStatsView view;

    public ClassDetailsStatsPresenter(final ClassDetailsStatsModel model)
    {
        this.model = Objects.requireNonNull(model);
    }

    public void setView(final ClassDetailsStatsView view)
    {
        this.view = Objects.requireNonNull(view);
    }

    public void loadStats()
    {
        view.loadClassDetails(model.getClassDetails());
    }

    void handleShowClassDetails(final MatcherClassDetails details)
    {
        view.showClassDetails(details);
    }
}
