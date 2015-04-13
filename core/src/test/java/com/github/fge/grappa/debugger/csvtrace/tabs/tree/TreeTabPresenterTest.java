package com.github.fge.grappa.debugger.csvtrace.tabs.tree;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.GrappaDebuggerException;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.model.tabs.tree.ParseTreeNode;
import com.google.common.util.concurrent.MoreExecutors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TreeTabPresenterTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
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
        doNothing().when(presenter).loadInputBuffer();
        doNothing().when(presenter).loadRootNode();

        presenter.load();

        verify(presenter).loadInputBuffer();
        verify(presenter).loadRootNode();
    }

    @Test
    public void loadRootNodeSuccessTest()
        throws GrappaDebuggerException
    {
        final ParseTreeNode rootNode = mock(ParseTreeNode.class);
        when(model.getRootNode()).thenReturn(rootNode);

        presenter.loadRootNode();

        verify(model).getRootNode();
        verify(view).loadRootNode(same(rootNode));
    }

    @Test
    public void loadRootNodeFailureTest()
        throws GrappaDebuggerException
    {
        final GrappaDebuggerException exception
            = new GrappaDebuggerException(new Exception());
        when(model.getRootNode()).thenThrow(exception);

        presenter.loadRootNode();

        verify(model).getRootNode();
        verify(view, never()).loadRootNode(any(ParseTreeNode.class));
        verify(presenter).handleLoadParseTreeError(same(exception));
    }

    @Test
    public void loadInputBufferTest()
    {
        final InputBuffer buffer = mock(InputBuffer.class);
        when(model.getInputBuffer()).thenReturn(buffer);

        presenter.loadInputBuffer();

        verify(model).getInputBuffer();
        verify(view).loadInputBuffer(same(buffer));
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
