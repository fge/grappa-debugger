package com.github.fge.grappa.debugger.tracetab.stat.global;

import com.github.fge.grappa.debugger.stats.global.GlobalParseInfo;
import com.github.fge.grappa.internal.NonFinalForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@NonFinalForTesting
public class GlobalStatsPresenter
{
    private final GlobalStatsModel model;

    private GlobalStatsView view;

    public GlobalStatsPresenter(final GlobalStatsModel model)
    {
        this.model = Objects.requireNonNull(model);
    }

    public void setView(final GlobalStatsView view)
    {
        this.view = Objects.requireNonNull(view);
    }

    public void loadStats()
    {
        final GlobalParseInfo parseInfo = model.getGlobalParseInfo();
        view.loadStats(model.getRuleStats());
        view.loadInfo(model.getParseRunInfo(),parseInfo.getTotalMatches());
        view.loadParseInfo(parseInfo);
    }
}
