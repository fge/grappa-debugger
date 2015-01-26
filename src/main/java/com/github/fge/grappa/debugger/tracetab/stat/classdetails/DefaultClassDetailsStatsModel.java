package com.github.fge.grappa.debugger.tracetab.stat.classdetails;

import com.github.fge.grappa.debugger.tracetab.TraceTabModel;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class DefaultClassDetailsStatsModel
    implements ClassDetailsStatsModel
{
    public DefaultClassDetailsStatsModel(final TraceTabModel model)
    {
        Objects.requireNonNull(model);
    }
}
