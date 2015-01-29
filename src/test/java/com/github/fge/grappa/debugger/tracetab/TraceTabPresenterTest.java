package com.github.fge.grappa.debugger.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.github.fge.grappa.trace.ParseRunInfo;
import com.github.fge.grappa.trace.TraceEvent;
import com.google.common.util.concurrent.MoreExecutors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class TraceTabPresenterTest
{
    private final BackgroundTaskRunner taskRunner = new BackgroundTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run);

    private MainWindowView parentView;
    private TraceTabModel model;
    private InputBuffer buffer;
    private TraceTabPresenter presenter;
    private TraceTabView view;

    @BeforeMethod
    public void init()
    {
        parentView = mock(MainWindowView.class);
        model = mock(TraceTabModel.class);

        buffer = mock(InputBuffer.class);
        when(model.getInputBuffer()).thenReturn(buffer);

        presenter = spy(new TraceTabPresenter(parentView, taskRunner, model));

        view = mock(TraceTabView.class);
        presenter.setView(view);
    }

    @Test
    public void loadTraceTest()
    {
        final List<TraceEvent> events = Collections.emptyList();
        when(model.getEvents()).thenReturn(events);

        final ParseRunInfo info = new ParseRunInfo(0L, 0, 0, 0);
        when(model.getInfo()).thenReturn(info);

        final ParseNode node = mock(ParseNode.class);
        when(model.getRootNode()).thenReturn(node);

        presenter.loadTrace();

        verify(view).setEvents(same(events));
        verify(view).setInfo(same(info));
        verify(view).setInputText(same(buffer));
        verify(view).setParseTree(same(node));
        verifyNoMoreInteractions(view);
    }

    @Test
    public void handleExpandParseTreeTest()
    {
        presenter.handleExpandParseTree();

        verify(view).expandParseTree();
    }

    @SuppressWarnings("AutoBoxing")
    @Test
    public void handleFailedParseNodeShowTest()
    {
        final ParseNode node = mock(ParseNode.class);
        final int end = 42;

        when(node.isSuccess()).thenReturn(false);
        when(node.getEnd()).thenReturn(end);

        presenter.handleParseNodeShow(node);

        verify(view).showParseNode(same(node));
        verify(view).highlightFailedMatch(eq(end));
        verifyNoMoreInteractions(view);
    }

    @SuppressWarnings("AutoBoxing")
    @Test
    public void handleSuccessParseNodeShowTest()
    {
        final ParseNode node = mock(ParseNode.class);
        final int start = 24;
        final int end = 42;

        when(node.isSuccess()).thenReturn(true);
        when(node.getStart()).thenReturn(start);
        when(node.getEnd()).thenReturn(end);

        presenter.handleParseNodeShow(node);

        verify(view).showParseNode(same(node));
        verify(view).highlightSuccessfulMatch(eq(start), eq(end));
        verifyNoMoreInteractions(view);
    }
}
