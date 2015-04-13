package com.github.fge.grappa.debugger;

import com.github.fge.grappa.debugger.mainwindow.MainWindowPresenter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MainWindowFactory
{
    @Nullable
    MainWindowPresenter createWindow(ZipTraceDbFactory factory);

    void close(@Nonnull MainWindowPresenter presenter);
}
