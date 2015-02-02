package com.github.fge.grappa.debugger.csvtrace.tabs.tree;

import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.google.common.util.concurrent.MoreExecutors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
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
    public void loadParseTreeTest()
        throws IOException
    {
        final ParseTreeNode rootNode = mock(ParseTreeNode.class);
        when(model.getRootNode()).thenReturn(rootNode);

        presenter.loadParseTree();

        verify(view).loadTree(same(rootNode));
    }

    @SuppressWarnings("AutoBoxing")
    @Test
    public void handleParseTreeNodeShowFailureTest()
    {
        final int end = 42;

        final ParseTreeNode node = mock(ParseTreeNode.class);
        when(node.isSuccess()).thenReturn(false);
        when(node.getEndIndex()).thenReturn(end);

        presenter.handleParseTreeNodeShow(node);

        verify(view).showParseTreeNode(same(node));
        verify(view).highlightFailure(end);
    }

    @SuppressWarnings("AutoBoxing")
    @Test
    public void handleParseTreeNodeShowSuccessTest()
    {
        final int start = 24;
        final int end = 42;

        final ParseTreeNode node = mock(ParseTreeNode.class);
        when(node.isSuccess()).thenReturn(true);
        when(node.getStartIndex()).thenReturn(start);
        when(node.getEndIndex()).thenReturn(end);

        presenter.handleParseTreeNodeShow(node);

        verify(view).showParseTreeNode(same(node));
        verify(view).highlightSuccess(start, end);
    }
}
