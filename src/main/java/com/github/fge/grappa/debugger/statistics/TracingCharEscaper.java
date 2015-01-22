package com.github.fge.grappa.debugger.statistics;

import com.google.common.collect.ImmutableMap;
import com.google.common.escape.ArrayBasedCharEscaper;

import java.util.Map;

public final class TracingCharEscaper
    extends ArrayBasedCharEscaper
{
    private static final Map<Character, String> ESCAPE_MAP
        = ImmutableMap.<Character, String>builder().put('\r', "\\r\r")
        .put('\n', "\\n\n").build();

    public TracingCharEscaper()
    {
        super(ESCAPE_MAP, '\0', Character.MAX_VALUE);
    }

    /**
     * Escapes a {@code char} value that has no direct explicit value in the
     * replacement array and lies outside the stated safe range. Subclasses
     * should
     * override this method to provide generalized escaping for characters.
     * <p>Note that arrays returned by this method must not be modified once
     * they
     * have been returned. However it is acceptable to return the same array
     * multiple times (even for different input characters).
     *
     * @param c the character to escape
     * @return the replacement characters, or {@code null} if no escaping was
     * required
     */
    @Override
    protected char[] escapeUnsafe(final char c)
    {
        return null;
    }
}
