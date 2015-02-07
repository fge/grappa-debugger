package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.csvtrace.tabs.matches.MatchesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.rules.RulesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabPresenter;

public interface CsvTraceView
{
    void loadTreeTab(TreeTabPresenter presenter);

    void loadRulesTab(RulesTabPresenter presenter);

    void loadMatchesTab(MatchesTabPresenter presenter);
}
