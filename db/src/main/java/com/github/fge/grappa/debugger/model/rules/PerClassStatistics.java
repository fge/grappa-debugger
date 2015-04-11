package com.github.fge.grappa.debugger.model.rules;

public final class PerClassStatistics
{
    private final String className;
    private final int nrRules;
    private final int nrCalls;

    public PerClassStatistics(final String className, final int nrRules,
        final int nrCalls)
    {
        this.className = className;
        this.nrRules = nrRules;
        this.nrCalls = nrCalls;
    }

    public String getClassName()
    {
        return className;
    }

    public int getNrRules()
    {
        return nrRules;
    }

    public int getNrCalls()
    {
        return nrCalls;
    }
}
