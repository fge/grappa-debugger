package com.github.fge.grappa.debugger.tracetab.stat.classdetails;

import com.github.fge.grappa.debugger.stats.classdetails.MatcherClassDetails;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class JavafxClassDetailsStatsView
    implements ClassDetailsStatsView
{
    private final ClassDetailsStatsDisplay display;

    public JavafxClassDetailsStatsView(final ClassDetailsStatsDisplay display)
    {
        this.display = Objects.requireNonNull(display);
    }

    @Override
    public void loadClassDetails(
        final Map<String, MatcherClassDetails> classDetails)
    {
        Objects.requireNonNull(classDetails);
        display.classNames.getItems().setAll(classDetails.values());
    }

    @Override
    public void showClassDetails(final MatcherClassDetails details)
    {
        display.matcherType.setText(details.getMatcherType().toString());
    }
}
