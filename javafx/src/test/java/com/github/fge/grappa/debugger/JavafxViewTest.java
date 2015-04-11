package com.github.fge.grappa.debugger;

import com.github.fge.lambdas.supplier.ThrowingSupplier;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

@Test
public abstract class JavafxViewTest
{
    @BeforeClass
    public void initToolkit()
    {
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            new JFXPanel(); // initializes JavaFX environment
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    protected void javafxDo(final Runnable runnable)
    {
        final CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            runnable.run();
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    protected <T> T javafxGet(final ThrowingSupplier<T> supplier)
    {
        final AtomicReference<T> ref = new AtomicReference<>();

        javafxDo(() -> ref.set(supplier.get()));

        return ref.get();
    }
}
