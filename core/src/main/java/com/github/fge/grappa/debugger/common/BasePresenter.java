package com.github.fge.grappa.debugger.common;

import com.github.fge.grappa.internal.NonFinalForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public abstract class BasePresenter<V>
{
    protected V view;

    @NonFinalForTesting
    public void setView(final V view)
    {
        this.view = Objects.requireNonNull(view);
        init();
    }

    /// TODO: get rid of that
    protected void init()
    {
    }

    public abstract void load();
}
