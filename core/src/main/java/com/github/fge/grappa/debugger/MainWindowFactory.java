package com.github.fge.grappa.debugger;


import com.github.fge.grappa.debugger.main.MainWindowPresenter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MainWindowFactory
{
    @Nullable
    MainWindowPresenter createWindow();

    void close(@Nonnull MainWindowPresenter presenter);
}
