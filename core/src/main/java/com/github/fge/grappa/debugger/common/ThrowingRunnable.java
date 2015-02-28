package com.github.fge.grappa.debugger.common;

import com.github.fge.lambdas.ThrownByLambdaException;

@FunctionalInterface
public interface ThrowingRunnable
    extends Runnable
{
    void doRun()
        throws Throwable;

    @Override
    default void run()
    {
        try {
            doRun();
        } catch (Error | RuntimeException e) {
            throw e;
        } catch (Throwable tooBad) {
            throw new ThrownByLambdaException(tooBad);
        }
    }
}
