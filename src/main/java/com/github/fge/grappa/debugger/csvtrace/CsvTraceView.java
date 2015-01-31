package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabPresenter;

public interface CsvTraceView
{
    void loadTree(TreeTabPresenter presenter, InputBuffer buffer);
}
