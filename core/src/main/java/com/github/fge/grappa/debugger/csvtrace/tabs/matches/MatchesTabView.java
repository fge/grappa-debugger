package com.github.fge.grappa.debugger.csvtrace.tabs.matches;

import com.github.fge.grappa.debugger.model.tabs.matches.MatchesData;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface MatchesTabView
{
    void displayMatchesData(MatchesData data);
}
