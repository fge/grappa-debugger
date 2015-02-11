package com.github.fge.grappa.debugger.javafx;

import java.util.concurrent.CountDownLatch;

public abstract class TabPresenter<V>
    extends BasePresenter<V>
{
    public abstract CountDownLatch refresh();
}
