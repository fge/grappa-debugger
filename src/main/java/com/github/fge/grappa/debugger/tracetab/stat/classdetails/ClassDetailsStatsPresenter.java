package com.github.fge.grappa.debugger.tracetab.stat.classdetails;

import java.util.Objects;

public class ClassDetailsStatsPresenter
{
    private ClassDetailsStatsView view;

    public void setView(final ClassDetailsStatsView view)
    {
        this.view = Objects.requireNonNull(view);
    }
}
