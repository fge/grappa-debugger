package com.github.fge.grappa.debugger;

import javax.annotation.ParametersAreNonnullByDefault;

@FunctionalInterface
@ParametersAreNonnullByDefault
public interface TraceDbFactory<T>
{
    TraceDb create(T arg)
        throws Exception;
}
