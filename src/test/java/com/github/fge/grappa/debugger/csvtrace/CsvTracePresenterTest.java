package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.stats.ParseNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CsvTracePresenterTest
{
    private MainWindowView mainView;
    private CsvTraceModel model;
    private CsvTraceView view;
    private CsvTracePresenter presenter;

    @BeforeMethod
    public void init()
    {
        mainView = mock(MainWindowView.class);
        model = mock(CsvTraceModel.class);
        presenter = spy(new CsvTracePresenter(mainView, model));

        view = mock(CsvTraceView.class);
        presenter.setView(view);

        doAnswer(invocation -> {
            ((Runnable) invocation.getArguments()[0]).run();
            return null;
        }).when(presenter).delayRun(any(Runnable.class));
    }

    @Test
    public void loadStatsTest()
        throws IOException
    {
        final ParseNode rootNode = mock(ParseNode.class);
        when(model.getRootNode()).thenReturn(rootNode);

        presenter.loadStats();

        verify(view).loadRootNode(same(rootNode));
    }
}
