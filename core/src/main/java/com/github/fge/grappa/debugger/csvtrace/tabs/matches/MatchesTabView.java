package com.github.fge.grappa.debugger.csvtrace.tabs.matches;

import com.github.fge.grappa.debugger.model.db.MatchStatistics;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface MatchesTabView
{
    void showMatches(List<MatchStatistics> stats);

    void showMatchesStats(int nonEmpty, int empty, int failures);

    void showTopOne(@Nullable Integer topOne, int total);

    void showTopFive(@Nullable Integer topFive, int total);

    void showTopTen(@Nullable Integer topTen, int total);
}
