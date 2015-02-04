package com.github.fge.grappa.debugger.common;

import com.github.fge.lambdas.suppliers.ThrowingSupplier;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import javafx.application.Platform;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.*;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A GUI-neutral background task executor
 *
 * <p>A very common scenario when programming with GUI toolkits is the need to
 * perform operations in the background without blocking the "UI thread" (this
 * would be the infamous EDT, or Event Dispatch Thread, with Swing, or the
 * application thread with JavaFX).</p>
 *
 * <p>Such toolkits, however, always provide a means to postpone tasks to be
 * executed on this UI thread; for Swing, that would be {@link
 * SwingUtilities#invokeLater(Runnable)}, and for JavaFX, {@link
 * Platform#runLater(Runnable)}. What they do not always provide is a tool to
 * bind a task to be executed in the background to a related task to be executed
 * in the UI thread.</p>
 *
 * <p>And this is where this class comes in. An instance of this class allows
 * you to perform, in a single method call, both the act of invoking the
 * background task and schedule the related UI task when the background task is
 * done. Example:</p>
 *
 * <pre>
 *     // If you use JavaFX...
 *     final GuiTaskRunner taskRunner
 *         = new GuiTaskRunner("myapp-%d", Platform::runLater);
 *     // If you use Swing...
 *     final GuiTaskRunner taskRunner
 *         = new GuiTaskRunner("myapp-%d", SwingUtilities::invokeLater);
 *
 *     // ...
 *
 *     taskRunner.run(
 *         () -> { my(); background(); task(); here(); },
 *         () -> { postponed(); ui(); update(); here(); }
 *     );
 * </pre>
 *
 * <p>You can also bind a frontend task to consume a value produced by the given
 * backgound task; for instance, if you have two methods:</p>
 *
 * <pre>
 *     public Foo backgroundProducer()
 *     {
 *         // procude a Foo
 *     }
 *
 *     public void frontendConsumer(final Foo foo)
 *     {
 *         // consume a Foo
 *     }
 * </pre>
 *
 * <p>you will then be able to invoke:</p>
 *
 * <pre>
 *     taskRunner.compute(
 *         () -> this::backgroundProducer,
 *         () -> this::frontendConsumer
 *     );
 * </pre>
 *
 * <p>Those are the two basic mechanisms. Three variants of each of these exist:
 * </p>
 *
 * <ul>
 *     <li>a variant which also accepts a task to execute on the UI thread
 *     before the background task;</li>
 *     <li>a variant which allows to use tasks which throw exceptions, with an
 *     exception handler;</li>
 *     <li>a variant which accepts both of the above.</li>
 * </ul>
 *
 * <p>For the two latter variants, this library makes use of <a
 * href="https://github.com/fge/throwing-lambdas">throwing-lambdas</a>.</p>
 *
 * <p>Note that the constructors and methods of this class do not accept null
 * arguments; if a null argument is passed, a {@link NullPointerException} will
 * be thrown.</p>
 *
 * @see ExecutorService
 * @see Executor
 * @see ThrowingRunnable
 * @see ThrowingSupplier
 */
@ParametersAreNonnullByDefault
public final class GuiTaskRunner
{
    private static final int NR_CPUS
        = Runtime.getRuntime().availableProcessors();
    private final ExecutorService executor;
    private final Executor frontExecutor;

    /**
     * Main constructor
     *
     * <p>This will build a {@link Executors#newCachedThreadPool(ThreadFactory)
     * cache thread pool executor} whose threads are {@link
     * Thread#setDaemon(boolean) daemon threads}.</p>
     *
     * @param fmt the thread name format string
     * @param frontExecutor the frontend executor
     *
     * @see ThreadFactoryBuilder#setNameFormat(String)
     * @see ThreadFactoryBuilder#setDaemon(boolean)
     */
    public GuiTaskRunner(final String fmt, final Executor frontExecutor)
    {
        Objects.requireNonNull(fmt);
        Objects.requireNonNull(frontExecutor);

        final ThreadFactory factory = new ThreadFactoryBuilder()
            .setNameFormat(fmt).setDaemon(true).build();
        executor = Executors.newFixedThreadPool(NR_CPUS, factory);
        this.frontExecutor = frontExecutor;
    }

    /**
     * Alternate constructor
     *
     * <p>This constructor is useful if, for instance, you want to test your
     * application interactions without actually creating new threads. An
     * example of using such a constructor would be:</p>
     *
     * <pre>
     *     private final ExecutorService executor
     *         = MoreExecutors.newDirectExecutorService();
     *     private final GuiTaskRunner testTaskRunner
     *         = new GuiTaskRunner(executor, Runnable::run);
     * </pre>
     *
     * @param executor the executor
     * @param frontExecutor the frontend executor
     */
    public GuiTaskRunner(final ExecutorService executor,
        final Executor frontExecutor)
    {
        this.executor = Objects.requireNonNull(executor);
        this.frontExecutor = Objects.requireNonNull(frontExecutor);
    }

    public void executeFront(final Runnable runnable)
    {
        Objects.requireNonNull(runnable);
        frontExecutor.execute(runnable);
    }

    public void executeBackground(final Runnable runnable)
    {
        Objects.requireNonNull(runnable);
        executor.submit(runnable);
    }

    /**
     * Run a task in the background; schedule a task to run on the ui thread
     * after the background task completes
     *
     * @param task the background task
     * @param after the task to run on the ui thread
     */
    public void run(final Runnable task, final Runnable after)
    {
        Objects.requireNonNull(task);
        Objects.requireNonNull(after);

        executor.execute(() -> {
            task.run();
            frontExecutor.execute(after);
        });
    }

    /**
     * Run a task in the background producing a value; schedule a task consuming
     * that value to run on the UI thread
     *
     * @param supplier the background task producing a value
     * @param consumer the UI thread task consuming that value
     * @param <T> type parameter of the produced/consume value
     */
    public <T> void compute(final Supplier<? extends T> supplier,
        final Consumer<? super T> consumer)
    {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(consumer);

        executor.submit(() -> {
            final T t = supplier.get();
            frontExecutor.execute(() -> consumer.accept(t));
        });
    }

    /**
     * Run a preliminary task on the UI thread; run a background task; schedule
     * a task to run on the UI thread after the background task completes
     *
     * @param before the preliminary task
     * @param task the background task
     * @param after the task to run on the ui thread
     */
    public void run(final Runnable before, final Runnable task,
        final Runnable after)
    {
        Objects.requireNonNull(before);
        Objects.requireNonNull(task);
        Objects.requireNonNull(after);

        frontExecutor.execute(before);

        executor.submit(() -> {
            task.run();
            frontExecutor.execute(after);
        });
    }

    /**
     * Run a preliminary task on the UI thread; run a task in the background
     * producing a value; schedule a task consuming that value on the UI thread
     *
     * @param before the preliminary task
     * @param supplier the background task producing a value
     * @param consumer the UI thread task consuming that value
     * @param <T> type parameter of the produced/consume value
     */
    public <T> void compute(final Runnable before,
        final Supplier<? extends T> supplier,
        final Consumer<? super T> consumer)
    {
        Objects.requireNonNull(before);
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(consumer);

        frontExecutor.execute(before);

        executor.submit(() -> {
            final T t = supplier.get();
            frontExecutor.execute(() -> consumer.accept(t));
        });
    }

    /**
     * Run a task on the background thread potentially throwing an exception;
     * schedule a task to run on the UI thread when the background task
     * completes successfully; specify an exception handler in the event of a
     * failure
     *
     * <p>Note that if the background task fails to complete, the UI thread task
     * will <em>not</em> be run.</p>
     *
     * @param task the potentially failing background task
     * @param after the task to run on the UI thread on success
     * @param onError the exception handler
     *
     * @see ThrowingRunnable#doRun()
     */
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

    /**
     * Run a potentially failing producing task in the background; schedule a
     * consuming task to run on the UI thread on success; specify an exception
     * handler on failure
     *
     * <p>Note that if the background task fails to complete, the scheduled UI
     * thread task will <em>not</em> be run.</p>
     *
     * @param supplier the potentially failing producing task
     * @param consumer the consumer task to run on the UI thread
     * @param onError the exception handler
     * @param <T> type parameter of the produced/consumed value
     */
    public <T> void computeOrFail(
        final ThrowingSupplier<? extends T> supplier,
        final Consumer<? super T> consumer, final Consumer<Throwable> onError)
    {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(onError);

        executor.submit(() -> {
            try {
                final T t = supplier.doGet();
                frontExecutor.execute(() -> consumer.accept(t));
            } catch (Throwable throwable) {
                frontExecutor.execute(() -> onError.accept(throwable));
            }
        });
    }

    /**
     * Schedule a task on the UI thread to run before the background task; run
     * a potentially failing background task; schedule a task to run on
     * successful completion of the background task; specify an exception
     * handler
     *
     * <p>Note that if the background task fails, the scheduled task to run on
     * the UI thread will <em>not</em> be executed.</p>
     *
     * @param before the task to schedule on the UI thread before the background
     * task
     * @param task the background task
     * @param after the task to schedule on the UI thread on successful
     * completion of the background task
     * @param onError the exception handler
     *
     * @see ThrowingRunnable#doRun()
     */
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

    /**
     * Schedule a task on the UI thread to run before the background task; run
     * a background task producing a value; schedule a task to run on the UI
     * thread to consume that value on successful completion of the background
     * task; specify an exception handler
     *
     * <p>Note that if the background task fails to complete, the consuming task
     * will <em>not</em> be executed.</p>
     *
     * @param before task to be executed on the UI thread prior to scheduling
     * the background task
     * @param supplier background task producing a value
     * @param consumer UI thread task consuming the value on successful
     * completion of the background task
     * @param onError the exception handler
     * @param <T> parameter type of the produced/consumed value
     */
    public <T> void computeOrFail(final Runnable before,
        final ThrowingSupplier<? extends T> supplier,
        final Consumer<? super T> consumer, final Consumer<Throwable> onError)
    {
        Objects.requireNonNull(before);
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(onError);

        frontExecutor.execute(before);

        executor.submit(() -> {
            try {
                final T t = supplier.doGet();
                frontExecutor.execute(() -> consumer.accept(t));
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
