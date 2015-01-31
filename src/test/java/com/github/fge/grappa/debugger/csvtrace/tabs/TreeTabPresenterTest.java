package com.github.fge.grappa.debugger.csvtrace.tabs;

import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.github.fge.grappa.trace.ParseRunInfo;
import com.google.common.util.concurrent.MoreExecutors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class TreeTabPresenterTest
{
    private final BackgroundTaskRunner taskRunner = new BackgroundTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run
    );

    private MainWindowView mainView;
    private CsvTraceModel model;
    private TreeTabPresenter presenter;
    private TreeTabView view;

    @BeforeMethod
    public void init()
    {
        mainView = mock(MainWindowView.class);
        model = mock(CsvTraceModel.class);
        presenter = spy(new TreeTabPresenter(taskRunner, mainView, model));

        view = mock(TreeTabView.class);
        presenter.setView(view);
    }

    @Test
    public void loadTest()
    {
        doNothing().when(presenter).loadParseTree();
        doNothing().when(presenter).loadInputText();

        presenter.load();

        verify(presenter).loadParseTree();
        verify(presenter).loadInputText();
        verify(presenter).loadParseRunInfo();
    }

    @Test
    public void successfulLoadParseTreeTest()
        throws IOException
    {
        final ParseNode rootNode = mock(ParseNode.class);
        when(model.getRootNode()).thenReturn(rootNode);

        presenter.loadParseTree();

        verify(view).loadTree(same(rootNode));
    }

    @Test
    public void failedLoadParseTreeTest()
        throws IOException
    {
        final IOException exception = new IOException();
        when(model.getRootNode()).thenThrow(exception);

        presenter.loadParseTree();

        verify(mainView).showError(anyString(), anyString(), same(exception));
        verifyZeroInteractions(view);
    }

    @Test
    public void successfulLoadInputText()
        throws IOException
    {
        presenter.loadInputText();

        verify(view).loadText();
    }

    @Test
    public void successfulLoadParseRunInfo()
        throws IOException
    {
        final ParseRunInfo info = new ParseRunInfo(0L, 0, 0, 0);
        when(model.getParseRunInfo()).thenReturn(info);

        presenter.loadParseRunInfo();

        verify(view).loadParseRunInfo(same(info));
    }

    @SuppressWarnings("AutoBoxing")
    @Test
    public void handleParseNodeShowSuccessTest()
    {
        final ParseNode node = mock(ParseNode.class);
        final int start = 31;
        final int end = 61;

        when(node.isSuccess()).thenReturn(true);
        when(node.getStart()).thenReturn(start);
        when(node.getEnd()).thenReturn(end);

        presenter.handleParseNodeShow(node);

        verify(view).showParseNode(same(node));
        verify(view).highlightSuccess(start, end);
    }

    @SuppressWarnings("AutoBoxing")
    @Test
    public void handleParseNodeShowFailureTest()
    {
        final ParseNode node = mock(ParseNode.class);
        final int start = 31;
        final int end = 61;

        when(node.isSuccess()).thenReturn(false);
        when(node.getStart()).thenReturn(start);
        when(node.getEnd()).thenReturn(end);

        presenter.handleParseNodeShow(node);

        verify(view).showParseNode(same(node));
        verify(view).highlightFailure(end);
    }
}
