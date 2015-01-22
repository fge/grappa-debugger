package com.github.fge.grappa.debugger;

import com.github.fge.grappa.debugger.basewindow.BaseWindowPresenter;

import javax.annotation.Nullable;

public interface BaseWindowFactory
{
    @Nullable
    BaseWindowPresenter createWindow();

    void close(BaseWindowPresenter presenter);
}
