package com.github.fge.grappa.debugger.trace.tabs.matches;

import com.github.fge.grappa.debugger.model.matches.MatchesData;
import com.github.fge.grappa.debugger.trace.tabs.TabView;

public interface MatchesTabView
    extends TabView
{
    void displayMatchesData(MatchesData data);
}
