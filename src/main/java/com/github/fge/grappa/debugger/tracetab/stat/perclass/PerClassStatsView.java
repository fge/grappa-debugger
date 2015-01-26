package com.github.fge.grappa.debugger.tracetab.stat.perclass;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public interface PerClassStatsView
{
    void loadPerClass(Map<String, Long> perClass);
}
