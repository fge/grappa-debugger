package com.github.fge.grappa.debugger.javafx;

import com.github.fge.grappa.debugger.stats.StatsType;
import com.github.fge.grappa.internal.NonFinalForTesting;
import javafx.fxml.FXMLLoader;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@NonFinalForTesting
@ParametersAreNonnullByDefault
public class JavafxStatsTabFactory
{
    private static final Class<?> MYSELF = JavafxStatsTabFactory.class;
    private static final Map<StatsType, URL> STAT_TABS_FXML_FILES;

    static {
        STAT_TABS_FXML_FILES = new EnumMap<>(StatsType.class);

        StatsType type;
        String location;
        URL url;

        type = StatsType.GLOBAL;
        location = "/stats/global.fxml";
        url = MYSELF.getResource(location);
        if (url == null)
            throw new ExceptionInInitializerError("resource " + location
                + " not found");
        STAT_TABS_FXML_FILES.put(type, url);

        type = StatsType.PER_CLASS;
        location = "/stats/perClass.fxml";
        url = MYSELF.getResource(location);
        if (url == null)
            throw new ExceptionInInitializerError("resource " + location
                + " not found");
        STAT_TABS_FXML_FILES.put(type, url);

        type = StatsType.CLASS_DETAILS;
        location = "/stats/classDetails.fxml";
        url = MYSELF.getResource(location);
        if (url == null)
            throw new ExceptionInInitializerError("resource " + location
                + " not found");
        STAT_TABS_FXML_FILES.put(type, url);
    }

    @Nonnull
    public FXMLLoader getLoader(final StatsType type)
    {
        Objects.requireNonNull(type);
        final URL url = STAT_TABS_FXML_FILES.get(type);
        if (url == null)
            throw new UnsupportedOperationException("no tab found for " + type
                + " stats");
        return new FXMLLoader(url);
    }
}
