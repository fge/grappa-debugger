package com.github.fge.grappa.debugger.tracetab.statistics;

public final class Utils
{
    @SuppressWarnings("ProhibitedExceptionThrown")
    private Utils()
    {
        throw new Error("nice try!");
    }

    @SuppressWarnings("AutoBoxing")
    public static String nanosToString(final long nanos)
    {
        long value = nanos;
        final long nrNanoseconds = value % 1000;
        value /= 1000;
        final long nrMicroseconds = value % 1000;
        value /= 1000;

        return String.format("%d ms, %03d.%03d µs", value, nrMicroseconds,
            nrNanoseconds);
    }
}
