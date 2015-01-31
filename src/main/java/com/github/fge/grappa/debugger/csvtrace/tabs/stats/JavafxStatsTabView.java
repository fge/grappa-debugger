package com.github.fge.grappa.debugger.csvtrace.tabs.stats;

import com.github.fge.grappa.debugger.common.JavafxView;

import java.io.IOException;

public final class JavafxStatsTabView
    extends JavafxView<StatsTabPresenter, StatsTabDisplay>
    implements StatsTabView
{
    public JavafxStatsTabView()
        throws IOException
    {
        super("/tab/statsTab.fxml");
    }
}
