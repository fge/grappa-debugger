package com.github.fge.grappa.debugger.mainwindow;

import com.github.fge.grappa.debugger.MainWindowFactory;
import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.google.common.util.concurrent.MoreExecutors;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class MainWindowPresenterTest
{
    private final BackgroundTaskRunner taskRunner = new BackgroundTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run);
    private MainWindowFactory factory;
    private MainWindowView view;
    private MainWindowPresenter presenter;

    @BeforeMethod
    public void init()
    {
        factory = mock(MainWindowFactory.class);
        view = mock(MainWindowView.class);
        presenter = spy(new MainWindowPresenter(factory, taskRunner));
        presenter.setView(view);
    }

    @Test
    public void handleCloseWindowTest()
    {
        presenter.handleCloseWindow();
        verifyZeroInteractions(view);
        verify(factory).close(presenter);
    }

    @Test
    public void handleNewWindowTest()
    {
        presenter.handleNewWindow();
        verifyZeroInteractions(view);
        verify(factory).createWindow();
    }

    @Test
    public void handleLoadFileNoFileTest()
    {
        when(view.chooseFile()).thenReturn(null);

        presenter.handleLoadFile();

        final InOrder inOrder = inOrder(factory, view);

        inOrder.verify(view).chooseFile();
        inOrder.verifyNoMoreInteractions();
    }
}
