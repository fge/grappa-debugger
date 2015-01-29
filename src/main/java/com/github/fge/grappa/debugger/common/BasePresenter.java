package com.github.fge.grappa.debugger.common;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public abstract class BasePresenter<V>
{
    protected V view;

    public final void setView(final V view)
    {
        this.view = Objects.requireNonNull(view);
    }
}
