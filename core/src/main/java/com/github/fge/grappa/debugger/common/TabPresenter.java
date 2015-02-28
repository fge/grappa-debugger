package com.github.fge.grappa.debugger.common;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public abstract class TabPresenter<V>
    extends BasePresenter<V>
{
    protected final GuiTaskRunner taskRunner;

    protected TabPresenter(final GuiTaskRunner taskRunner)
    {
        this.taskRunner = Objects.requireNonNull(taskRunner);
    }

    public abstract CountDownLatch refresh();
}
