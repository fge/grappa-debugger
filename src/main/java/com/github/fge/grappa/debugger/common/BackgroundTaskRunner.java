package com.github.fge.grappa.debugger.common;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public final class BackgroundTaskRunner
{
    private static final int NR_THREADS
        = Runtime.getRuntime().availableProcessors();

    private final ExecutorService executor;
    private final Executor frontExecutor;

    public BackgroundTaskRunner(final String fmt, final Executor frontExecutor)
    {
        Objects.requireNonNull(fmt);
        Objects.requireNonNull(frontExecutor);

        final ThreadFactory factory = new ThreadFactoryBuilder()
            .setNameFormat(fmt).setDaemon(true).build();
        executor = Executors.newFixedThreadPool(NR_THREADS, factory);
        this.frontExecutor = frontExecutor;
    }

    public BackgroundTaskRunner(final ExecutorService executor,
        final Executor frontExecutor)
    {
        this.executor = Objects.requireNonNull(executor);
        this.frontExecutor = frontExecutor;
    }

    public void run(final Runnable task, final Runnable after)
    {
        Objects.requireNonNull(task);
        Objects.requireNonNull(after);
        executor.execute(() -> {
            task.run();
            frontExecutor.execute(after);
        });
    }

    public void run(final Runnable before, final Runnable task,
        final Runnable post)
    {
        Objects.requireNonNull(before);
        Objects.requireNonNull(task);
        Objects.requireNonNull(post);

        frontExecutor.execute(before);

        executor.submit(() -> {
            task.run();
            frontExecutor.execute(post);
        });
    }

    public <T> void run(final Supplier<T> supplier, final Consumer<T> consumer)
    {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(consumer);

        executor.submit(() -> {
            final T t = supplier.get();
            frontExecutor.execute(() -> consumer.accept(t));
        });
    }

    public void runOrFail(final ThrowingRunnable task, final Runnable after,
        final Consumer<Throwable> onError)
    {
        Objects.requireNonNull(task);
        Objects.requireNonNull(after);
        Objects.requireNonNull(onError);

        executor.execute(() -> {
            try {
                task.doRun();
                frontExecutor.execute(after);
            } catch (Throwable throwable) {
                frontExecutor.execute(() -> onError.accept(throwable));
            }
        });
    }

    public void runOrFail(final Runnable before, final ThrowingRunnable task,
        final Runnable after, final Consumer<Throwable> onError)
    {
        Objects.requireNonNull(before);
        Objects.requireNonNull(task);
        Objects.requireNonNull(after);
        Objects.requireNonNull(onError);

        frontExecutor.execute(before);

        executor.execute(() -> {
            try {
                task.doRun();
                frontExecutor.execute(after);
            } catch (Throwable throwable) {
                frontExecutor.execute(() -> onError.accept(throwable));
            }
        });
    }

    public void dispose()
    {
        executor.shutdownNow();
    }
}
