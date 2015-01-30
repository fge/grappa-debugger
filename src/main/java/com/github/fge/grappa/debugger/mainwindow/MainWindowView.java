package com.github.fge.grappa.debugger.mainwindow;

import com.github.fge.grappa.debugger.csvtrace.CsvTracePresenter;
import com.github.fge.grappa.debugger.tracetab.TraceTabPresenter;

import java.nio.file.Path;

public interface MainWindowView
{
    Path chooseFile();

    void setWindowTitle(String windowTitle);

    void showError(String header, String message, Throwable throwable);

    void setLabelText(String text);

    void injectTab(TraceTabPresenter tabPresenter);

    void attachTrace(CsvTracePresenter presenter);
}
