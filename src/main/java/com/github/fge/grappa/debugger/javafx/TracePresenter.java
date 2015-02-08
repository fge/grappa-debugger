package com.github.fge.grappa.debugger.javafx;

public abstract class TracePresenter<V>
    extends BasePresenter<V>
{
    public abstract void loadTrace();

    public abstract void dispose();
}
