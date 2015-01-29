package com.github.fge.grappa.debugger.common;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public abstract class JavafxDisplay<P>
{
    protected P presenter;

    public final void setPresenter(final P presenter)
    {
        this.presenter = Objects.requireNonNull(presenter);
        init();
    }

    public abstract void init();
}
