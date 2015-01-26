package com.github.fge.grappa.debugger.tracetab.stat.classdetails;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class ClassDetailsStatsDisplay
{
    private ClassDetailsStatsPresenter presenter;

    public void setPresenter(final ClassDetailsStatsPresenter presenter)
    {
        this.presenter = Objects.requireNonNull(presenter);
        init();
    }

    private void init()
    {
    }
}
