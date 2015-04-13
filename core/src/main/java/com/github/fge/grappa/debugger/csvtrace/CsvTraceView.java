package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.csvtrace.tabs.matches.MatchesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.rules.RulesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.treedepth.TreeDepthTabPresenter;
import com.github.fge.grappa.debugger.model.db.DbLoadStatus;

public interface CsvTraceView
{
    void reportProgress(DbLoadStatus status);

    void loadComplete();

    void loadAborted();

    void loadTreeTab(TreeTabPresenter tabPresenter);

    void loadRulesTab(RulesTabPresenter tabPresenter);

    void loadMatchesTab(MatchesTabPresenter tabPresenter);

    void loadTreeDepthTab(TreeDepthTabPresenter tabPresenter);

    void showLoadComplete();

    void disableTabsRefresh();

    void enableTabsRefresh();

}
