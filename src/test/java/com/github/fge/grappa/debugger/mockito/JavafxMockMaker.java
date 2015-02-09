package com.github.fge.grappa.debugger.mockito;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.mockito.internal.creation.cglib.CglibMockMaker;
import org.mockito.invocation.MockHandler;
import org.mockito.mock.MockCreationSettings;
import org.mockito.plugins.MockMaker;

import javax.swing.SwingUtilities;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("ProhibitedExceptionThrown")
public final class JavafxMockMaker
    implements MockMaker
{
    private final MockMaker wrapped = new CglibMockMaker();
    private boolean jfxIsSetup = false;

    @Override
    public <T> T createMock(final MockCreationSettings<T> settings,
        final MockHandler handler)
    {
        final AtomicReference<T> result = new AtomicReference<>();
        final Runnable run
            = () -> result.set(wrapped.createMock(settings, handler));
        doOnJavaFXThread(run);
        return result.get();
    }

    @Override
    public MockHandler getHandler(final Object mock)
    {
        final AtomicReference<MockHandler> result = new AtomicReference<>();
        final Runnable run = () -> result.set(wrapped.getHandler(mock));
        doOnJavaFXThread(run);
        return result.get();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void resetMock(final Object mock, final MockHandler newHandler,
        final MockCreationSettings settings)
    {
        final Runnable run
            = () -> wrapped.resetMock(mock, newHandler, settings);
        doOnJavaFXThread(run);
    }

    private void doOnJavaFXThread(final Runnable runnable)
    {
        if (!jfxIsSetup) {
            setupJavaFX();
            jfxIsSetup = true;
        }
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            runnable.run();
            countDownLatch.countDown();
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupJavaFX()
    {
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            new JFXPanel(); // initializes JavaFX environment
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
