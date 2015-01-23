package com.github.fge.grappa.debugger.mainwindow;

import com.github.fge.grappa.debugger.legacy.tracetab.LegacyTraceTabPresenter;

import java.nio.file.Path;

public interface MainWindowView
{
    void injectTab(LegacyTraceTabPresenter presenter);

    Path chooseFile();

    void setWindowTitle(String windowTitle);

    void showError(String header, String message, Throwable throwable);

    void setLabelText(String text);
}
