package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.google.common.util.concurrent.MoreExecutors;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class CsvTracePresenterTest
{
    private final BackgroundTaskRunner taskRunner = new BackgroundTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run);

    private MainWindowView mainView;
    private CsvTraceModel model;
    private CsvTraceView view;
    private CsvTracePresenter presenter;
    private InputBuffer buffer;

    @BeforeMethod
    public void init()
        throws IOException
    {
        model = mock(CsvTraceModel.class);
        buffer = mock(InputBuffer.class);
        when(model.getInputBuffer()).thenReturn(buffer);

        mainView = mock(MainWindowView.class);
        presenter = spy(new CsvTracePresenter(mainView, taskRunner, model));

        view = mock(CsvTraceView.class);
        presenter.setView(view);
    }
}
