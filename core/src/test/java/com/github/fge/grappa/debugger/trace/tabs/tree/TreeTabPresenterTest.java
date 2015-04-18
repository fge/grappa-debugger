package com.github.fge.grappa.debugger.trace.tabs.tree;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.main.MainWindowView;
import com.github.fge.grappa.debugger.model.TraceModel;
import com.github.fge.grappa.debugger.model.tree.ParseTreeNode;
import com.google.common.util.concurrent.MoreExecutors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TreeTabPresenterTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run);

    private MainWindowView mainView;
    private TraceDb traceDb;
    private TraceModel model;

    private TreeTabView view;

    private TreeTabPresenter presenter;

    @BeforeMethod
    public void init()
    {
        mainView = mock(MainWindowView.class);
        traceDb = mock(TraceDb.class);

        model = mock(TraceModel.class);

        when(traceDb.getModel()).thenReturn(model);

        view = mock(TreeTabView.class);

        presenter = spy(new TreeTabPresenter(taskRunner, mainView, traceDb));

        presenter.setView(view);
    }

    @Test
    public void loadTest()
    {
        doNothing().when(presenter).loadParseTree();
        doNothing().when(presenter).loadInputBuffer();

        presenter.load();

        verify(presenter).loadParseTree();
        verify(presenter).loadInputBuffer();
    }

    @Test
    public void loadInputBufferTest()
    {
        final InputBuffer buffer = mock(InputBuffer.class);

        when(traceDb.getInputBuffer()).thenReturn(buffer);

        presenter.loadInputBuffer();

        verify(traceDb).getInputBuffer();
        verify(view).loadInputBuffer(same(buffer));
    }

    @Test
    public void loadParseTreeSuccessTest()
        throws InterruptedException
    {
        final ParseTreeNode node = mock(ParseTreeNode.class);

        doReturn(node).when(presenter).getRootNode();

        presenter.loadParseTree();

        verify(presenter).getRootNode();
        verify(view).displayTree(same(node));
        verify(presenter, never()).handleLoadTreeError(any(Throwable.class));
    }

    @Test
    public void loadParseTreeErrorTest()
        throws InterruptedException
    {
        final InterruptedException exception = new InterruptedException();

        doThrow(exception).when(presenter).getRootNode();

        presenter.loadParseTree();

        verify(presenter).getRootNode();
        verify(view, never()).displayTree(any(ParseTreeNode.class));
        verify(presenter).handleLoadTreeError(same(exception));
    }

    @SuppressWarnings("AutoBoxing")
    @Test
    public void handleParseTreeNodeShowSuccessTest()
    {
        final ParseTreeNode node = mock(ParseTreeNode.class);

        when(node.isSuccess()).thenReturn(true);

        final int startIndex = 24;
        final int endIndex = 42;

        when(node.getStartIndex()).thenReturn(startIndex);
        when(node.getEndIndex()).thenReturn(endIndex);

        presenter.handleParseTreeNodeShow(node);

        verify(view).showParseTreeNode(node);
        verify(view).highlightSuccess(startIndex, endIndex);
    }

    @SuppressWarnings("AutoBoxing")
    @Test
    public void handleParseTreeNodeShowFailureTest()
    {
        final ParseTreeNode node = mock(ParseTreeNode.class);

        when(node.isSuccess()).thenReturn(false);

        final int endIndex = 42;

        when(node.getEndIndex()).thenReturn(endIndex);

        presenter.handleParseTreeNodeShow(node);

        verify(view).showParseTreeNode(node);
        verify(view).highlightFailure(endIndex);
    }

    @SuppressWarnings("AutoBoxing")
    @Test
    public void handleNeedChildrenTest()
    {
        final ParseTreeNode node = mock(ParseTreeNode.class);

        final int id = 42;

        when(node.getId()).thenReturn(id);

        @SuppressWarnings("unchecked")
        final List<ParseTreeNode> children = mock(List.class);

        when(model.getNodeChildren(anyInt())).thenReturn(children);

        presenter.handleNeedChildren(node);

        verify(view).waitForChildren();
        verify(model).getNodeChildren(id);
        verify(view).setTreeChildren(same(children));
    }
}
