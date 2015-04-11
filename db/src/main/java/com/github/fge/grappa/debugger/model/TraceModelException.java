package com.github.fge.grappa.debugger.model;

public final class TraceModelException
    extends RuntimeException
{
    public TraceModelException()
    {
    }

    public TraceModelException(final String message)
    {
        super(message);
    }

    public TraceModelException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public TraceModelException(final Throwable cause)
    {
        super(cause);
    }

    public TraceModelException(final String message, final Throwable cause,
        final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
