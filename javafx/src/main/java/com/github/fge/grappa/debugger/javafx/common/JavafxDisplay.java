package com.github.fge.grappa.debugger.javafx.common;

import com.github.fge.grappa.internal.NonFinalForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public abstract class JavafxDisplay<P>
{
    protected P presenter;

    @NonFinalForTesting
    public void setPresenter(final P presenter)
    {
        this.presenter = Objects.requireNonNull(presenter);
        init();
    }

    public abstract void init();
}
