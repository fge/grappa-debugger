package com.github.fge.grappa.debugger.csvtrace.tabs.matches;

import com.github.fge.grappa.debugger.common.JavafxView;
import com.github.fge.grappa.debugger.common.db.RuleInvocationStatistics;

import java.io.IOException;
import java.util.List;

public final class JavafxMatchesTabView
    extends JavafxView<MatchesTabPresenter, MatchesTabDisplay>
    implements MatchesTabView
{
    public JavafxMatchesTabView()
        throws IOException
    {
        super("/tabs/matchesTab.fxml");
    }

    @Override
    public void disableTabRefresh()
    {
        display.tabRefresh.setDisable(true);
        display.loadProgressBar.setVisible(true);
    }

    @Override
    public void showMatchesLoadingComplete()
    {
        display.completionBar.setTop(null);
    }

    @Override
    public void showMatchesLoadingIncomplete()
    {
        display.tabRefresh.setDisable(false);
        display.loadProgressBar.setVisible(false);
    }

    @Override
    public void showMatches(final List<RuleInvocationStatistics> stats)
    {
        display.matchesTable.getSortOrder().setAll(display.nrCalls);
        display.matchesTable.getItems().setAll(stats);
        display.matchesTable.sort();
    }
}
