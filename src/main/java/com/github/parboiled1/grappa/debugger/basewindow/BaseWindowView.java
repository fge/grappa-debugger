package com.github.parboiled1.grappa.debugger.basewindow;

import com.github.parboiled1.grappa.debugger.tracetab.TraceTabPresenter;

import java.io.File;

public interface BaseWindowView
{
    void injectTab(TraceTabPresenter presenter);

    // TODO: not very nice, but...
    File chooseFile(Object object);
}
