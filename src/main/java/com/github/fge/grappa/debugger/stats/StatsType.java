package com.github.fge.grappa.debugger.stats;

import javax.annotation.Nonnull;

public enum StatsType
{
    GLOBAL("Global"),
    PER_CLASS("Per class"),
    CLASS_DETAILS("Class details"),
    ;

    private final String desc;

    StatsType(final String desc)
    {
        this.desc = desc;
    }

    @Nonnull
    @Override
    public String toString()
    {
        return desc;
    }
}
