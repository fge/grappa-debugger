package com.github.fge.grappa.debugger.tracetab.stat.classdetails;

import com.github.fge.grappa.debugger.stats.classdetails.MatcherClassDetails;

import javax.annotation.Nonnull;
import java.util.Map;

public interface ClassDetailsStatsModel
{
    @Nonnull
    Map<String, MatcherClassDetails> getClassDetails();
}
