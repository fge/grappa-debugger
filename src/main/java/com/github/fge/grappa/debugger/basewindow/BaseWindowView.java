package com.github.fge.grappa.debugger.basewindow;

import com.github.fge.grappa.debugger.tracetab.TraceTabPresenter;

import java.io.File;

public interface BaseWindowView
{
    void injectTab(TraceTabPresenter presenter);

    File chooseFile();

    void setWindowTitle(String windowTitle);

    void showError(String header, String message, Throwable throwable);
}
