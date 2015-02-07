package com.github.fge.grappa.debugger.csvtrace.tabs.linechart;

import com.github.fge.grappa.debugger.common.JavafxView;

import java.io.IOException;

public final class JavafxLineChartTabView
    extends JavafxView<LineChartTabPresenter, LineChartTabDisplay>
    implements LineChartTabView
{
    public JavafxLineChartTabView()
        throws IOException
    {
        super("/tabs/lineChartTab.fxml");
    }
}
