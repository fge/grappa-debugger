package com.github.fge.grappa.debugger.main;

import com.github.fge.grappa.debugger.MainWindowFactory2;
import com.github.fge.grappa.debugger.ZipTraceDbFactory;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.trace.TracePresenter;
import com.google.common.util.concurrent.MoreExecutors;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Path;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("InstanceVariableMayNotBeInitialized")
public class MainWindowPresenterTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run);
    private ZipTraceDbFactory dbFactory;
    private MainWindowFactory2 factory;
    private MainWindowView view;
    private MainWindowPresenter presenter;

    @BeforeMethod
    public void init()
    {
        factory = mock(MainWindowFactory2.class);
        view = mock(MainWindowView.class);
        dbFactory = mock(ZipTraceDbFactory.class);
        presenter = spy(new MainWindowPresenter(factory, taskRunner,
            dbFactory));
        presenter.setView(view);
    }

    @Test
    public void handleCloseWindowNoPresenterTest()
    {
        presenter.handleCloseWindow();
        verify(factory).close(presenter);
    }

    @Test
    public void handleCloseWindowWithPresenterTest()
    {
        presenter.tracePresenter = mock(TracePresenter.class);

        presenter.handleCloseWindow();
        verify(presenter.tracePresenter).close();
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

    @Test
    public void handleLoadFileTest()
        throws Exception
    {
        doNothing().when(presenter).loadTab(any(Path.class));
        final Path path = mock(Path.class);

        when(view.chooseFile()).thenReturn(path);

        presenter.handleLoadFile();

        verify(presenter).loadTab(same(path));
    }

    @Test
    public void handleLoadFileOtherWindowTest()
    {
        presenter.tracePresenter = mock(TracePresenter.class);

        final MainWindowPresenter otherPresenter
            = mock(MainWindowPresenter.class);
        when(factory.createWindow()).thenReturn(otherPresenter);

        final Path path = mock(Path.class);
        when(view.chooseFile()).thenReturn(path);

        presenter.handleLoadFile();

        verify(presenter, never()).loadTab(any(Path.class));
        verify(otherPresenter).loadTab(same(path));
    }

    @Test
    public void loadTabTest()
        throws Exception
    {
        final Path path = mock(Path.class);

        final TracePresenter tracePresenter = mock(TracePresenter.class);

        doReturn(tracePresenter).when(presenter).createPresenter(same(path));

        presenter.loadTab(path);

        verify(presenter).attachPresenter(same(tracePresenter));
    }

    @Test
    public void loadTabFailureTest()
        throws Exception
    {
        final Path path = mock(Path.class);

        final Exception exception = new Exception();

        doThrow(exception).when(presenter).createPresenter(any(Path.class));

        presenter.loadTab(path);

        verify(presenter, never()).attachPresenter(any(TracePresenter.class));

        verify(presenter).handleLoadError(same(exception));
    }
}
