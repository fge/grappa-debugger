package com.github.fge.grappa.debugger.main;

import com.github.fge.grappa.debugger.trace.TracePresenter;

import javax.annotation.Nullable;
import java.nio.file.Path;

public interface MainWindowView
{
    void showError(String header, String message, Throwable throwable);

    @Nullable
    Path chooseFile();

    void attachPresenter(TracePresenter presenter);

    void setWindowTitle(String title);
}
