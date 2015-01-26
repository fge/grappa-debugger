package com.github.fge.grappa.debugger.tracetab.stat.classdetails;

import com.github.fge.grappa.debugger.stats.classdetails.MatcherClassDetails;
import com.github.fge.grappa.debugger.stats.classdetails
    .MatcherClassDetailsProcessor;
import com.github.fge.grappa.debugger.tracetab.TraceTabModel;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class DefaultClassDetailsStatsModel
    implements ClassDetailsStatsModel
{
    private final Map<String, MatcherClassDetails> classDetails;

    public DefaultClassDetailsStatsModel(final TraceTabModel model)
    {
        Objects.requireNonNull(model);

        final MatcherClassDetailsProcessor processor
            = new MatcherClassDetailsProcessor();

        model.getEvents().forEach(processor::process);

        classDetails = processor.getClassDetails();
    }

    @Nonnull
    @Override
    public Map<String, MatcherClassDetails> getClassDetails()
    {
        return Collections.unmodifiableMap(classDetails);
    }
}
