package com.github.fge.grappa.debugger.zip2db;

import com.google.common.base.Function;
import com.google.common.escape.ArrayBasedCharEscaper;

import java.util.Collections;
import java.util.Map;

public final class TraceCsvEscaper
    extends ArrayBasedCharEscaper
{
    private static final Map<Character, String> ESCAPE_MAP
        = Collections.singletonMap('"', "\\\"");

    public static final Function<String, String> ESCAPER
        = new TraceCsvEscaper().asFunction();

    private TraceCsvEscaper()
    {
        super(ESCAPE_MAP, Character.MIN_VALUE, Character.MAX_VALUE);
    }

    @Override
    protected char[] escapeUnsafe(final char c)
    {
        throw new IllegalStateException();
    }
}
