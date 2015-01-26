package com.github.fge.grappa.debugger.tracetab.stat.classdetails;

import com.github.fge.grappa.debugger.stats.classdetails.MatcherClassDetails;

import java.util.Map;

public interface ClassDetailsStatsView
{
    void loadClassDetails(Map<String, MatcherClassDetails> classDetails);
}
