package com.github.fge.grappa.debugger.tracetab.stat.global;

import com.github.fge.grappa.debugger.stats.global.GlobalParseInfo;
import com.github.fge.grappa.debugger.stats.global.RuleMatchingStats;
import com.github.fge.grappa.trace.ParseRunInfo;

import javax.annotation.Nonnull;
import java.util.Collection;

public interface GlobalStatsModel
{
    @Nonnull
    GlobalParseInfo getGlobalParseInfo();

    @Nonnull
    ParseRunInfo getParseRunInfo();

    @Nonnull
    Collection<RuleMatchingStats> getRuleStats();
}
