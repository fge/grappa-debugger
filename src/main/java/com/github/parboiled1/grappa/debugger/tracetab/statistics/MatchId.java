package com.github.parboiled1.grappa.debugger.tracetab.statistics;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class MatchId
{
    private final String path;
    private final int level;
    private final int startIndex;

    public MatchId(final String path, final int level,
        final int startIndex)
    {
        this.path = path;
        this.level = level;
        this.startIndex = startIndex;
    }

    @Override
    public int hashCode()
    {
        return startIndex ^ level ^ path.hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final MatchId other = (MatchId) obj;
        return startIndex == other.startIndex
            && level == other.level
            && Objects.equals(path, other.path);
    }

    @Nonnull
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
            .add("level", level)
            .add("startIndex", startIndex)
            .add("path", path)
            .toString();
    }
}
