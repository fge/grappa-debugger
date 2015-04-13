package com.github.fge.grappa.debugger.mainwindow;

import com.github.fge.grappa.debugger.MainWindowFactory;
import com.github.fge.grappa.debugger.ZipTraceDbFactory;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.csvtrace.CsvTracePresenter;
import com.google.common.util.concurrent.MoreExecutors;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
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
    private MainWindowFactory factory;
    private MainWindowView view;
    private MainWindowPresenter presenter;
    private ZipTraceDbFactory dbFactory;

    @BeforeMethod
    public void init()
    {
        factory = mock(MainWindowFactory.class);
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
        presenter.tracePresenter = mock(CsvTracePresenter.class);

        presenter.handleCloseWindow();
        verify(presenter.tracePresenter).dispose();
        verify(factory).close(presenter);
    }

    @Test
    public void handleNewWindowTest()
    {
        presenter.handleNewWindow();
        verifyZeroInteractions(view);
        verify(factory).createWindow(dbFactory);
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
        throws IOException, SQLException
    {
        final Path path = mock(Path.class);
        final CsvTraceModel model = mock(CsvTraceModel.class);
        final CsvTracePresenter tracePresenter
            = mock(CsvTracePresenter.class);

        when(view.chooseFile()).thenReturn(path);
        doReturn(model).when(presenter).getModel(same(path));
        doReturn(tracePresenter).when(presenter)
            .createTabPresenter(same(model));

        presenter.handleLoadFile();

        verify(view).attachTrace(tracePresenter);
        verify(tracePresenter).load();

        assertThat(presenter.tracePresenter).isSameAs(tracePresenter);
    }

    @Test
    public void handleLoadFileOtherWindowTest()
    {
        presenter.tracePresenter = mock(CsvTracePresenter.class);

        final MainWindowPresenter otherPresenter
            = mock(MainWindowPresenter.class);
        when(factory.createWindow(dbFactory)).thenReturn(otherPresenter);

        final Path path = mock(Path.class);
        when(view.chooseFile()).thenReturn(path);

        presenter.handleLoadFile();

        verify(presenter, never()).loadTab(any(Path.class));
        verify(otherPresenter).loadTab(same(path));
    }

    @Test
    public void handleLoadFileErrorTest()
        throws IOException, SQLException
    {
        final Path path = mock(Path.class);
        when(view.chooseFile()).thenReturn(path);

        final SQLException exception = new SQLException();
        doThrow(exception).when(presenter).getModel(same(path));

        presenter.handleLoadFile();

        verify(presenter).handleLoadFileError(same(exception));
        verify(presenter, never()).createTabPresenter(any());
        assertThat(presenter.tracePresenter).isNull();
    }
}
