package com.github.fge.grappa.debugger.csvtrace.tabs;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.stats.ParseNode;
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
        final InputBuffer buffer = mock(InputBuffer.class);
        when(model.getInputBuffer()).thenReturn(buffer);

        presenter.loadInputText();

        verify(view).loadText(same(buffer));
    }

    @Test
    public void failedLoadInputText()
        throws IOException
    {
        final IOException exception = new IOException();
        when(model.getInputBuffer()).thenThrow(exception);

        presenter.loadInputText();

        verify(mainView).showError(anyString(), anyString(), same(exception));
        verifyZeroInteractions(view);
    }

    @Test
    public void handleParseNodeShowTest()
    {
        final ParseNode node = mock(ParseNode.class);

        presenter.handleParseNodeShow(node);

        verify(view).showParseNode(same(node));
    }
}
