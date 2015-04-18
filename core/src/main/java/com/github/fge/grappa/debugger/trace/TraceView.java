package com.github.fge.grappa.debugger.trace;

import com.github.fge.grappa.debugger.trace.tabs.matches.MatchesTabPresenter;
import com.github.fge.grappa.debugger.trace.tabs.rules.RulesTabPresenter;
import com.github.fge.grappa.debugger.trace.tabs.tree.TreeTabPresenter;

public interface TraceView
{
    void showLoadToolbar();

    void reportStatus(int total, int loaded);

    void showLoadComplete();

    void hideLoadToolbar();

    void disableTabRefresh();

    void enableTabRefresh();

    void loadTreeTab(TreeTabPresenter tabPresenter);

    void loadMatchesTab(MatchesTabPresenter tabPresenter);

    void loadRulesTab(RulesTabPresenter tabPresenter);
}
