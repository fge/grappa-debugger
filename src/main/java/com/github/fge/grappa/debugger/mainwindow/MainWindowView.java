package com.github.fge.grappa.debugger.mainwindow;

import com.github.fge.grappa.debugger.legacy.tracetab.LegacyTraceTabPresenter;
import com.github.fge.grappa.debugger.tracetab.TraceTabPresenter;

import java.nio.file.Path;

public interface MainWindowView
{
    void injectLegacyTab(LegacyTraceTabPresenter presenter);

    Path chooseFile();

    void setWindowTitle(String windowTitle);

    void showError(String header, String message, Throwable throwable);

    void setLabelText(String text);

    void injectTab(TraceTabPresenter tabPresenter);
}
