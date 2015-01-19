package com.github.fge.grappa.debugger.tracetab;

import com.github.fge.grappa.debugger.tracetab.statistics.InputTextInfo;
import com.github.fge.grappa.debugger.tracetab.statistics.ParseNode;
import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.github.parboiled1.grappa.trace.ParsingRunTrace;
import com.github.parboiled1.grappa.trace.TraceEvent;
import com.github.parboiled1.grappa.trace.TraceEventType;
import org.parboiled.support.Position;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class TraceTabPresenterTest
{
    private TraceTabView view;
    private TraceTabModel model;
    private TraceTabPresenter presenter;
    private InputBuffer buffer;

    @SuppressWarnings("unchecked")
    @BeforeMethod
    public void init()
    {
        view = mock(TraceTabView.class);
        model = mock(TraceTabModel.class);
        buffer = mock(InputBuffer.class);
        when(model.getInputBuffer()).thenReturn(buffer);
        presenter = new TraceTabPresenter(model);
        presenter.setView(view);
    }

    @SuppressWarnings({ "unchecked", "AutoBoxing" })
    @Test
    public void loadTraceWithEmptyEvents()
    {
        final List<TraceEvent> events = mock(List.class);
        when(events.isEmpty()).thenReturn(true);

        when(model.getTraceEvents()).thenReturn(events);

        presenter.loadTrace();

        verifyZeroInteractions(view);
    }

    @SuppressWarnings({ "unchecked", "AutoBoxing" })
    @Test
    public void loadTraceWithNonEmptyEvents()
    {
        final long fakeDate = 87238987982713987L;
        final TraceEvent fakeEvent = new TraceEvent(TraceEventType.BEFORE_MATCH,
            0L, 0, "", "", 0);
        final List<TraceEvent> events = Collections.singletonList(fakeEvent);

        final ParsingRunTrace trace = new ParsingRunTrace(fakeDate, events);

        when(model.getTrace()).thenReturn(trace);
        when(model.getTraceEvents()).thenReturn(events);

        presenter.loadTrace();

        verify(view).setInputText(anyString());
        verify(view).setInputTextInfo(any(InputTextInfo.class));
        verify(view).setParseDate(fakeDate);
        verify(view).setParseTree(any(ParseNode.class));
        verify(view).setStatistics(anyCollection());
        verify(view).setTraceEvents(same(events));
        verifyNoMoreInteractions(view);
    }

    @Test
    public void handleParseNodeShowTest()
    {
        final ParseNode node = mock(ParseNode.class);

        final Position position = new Position(12, 12);
        when(buffer.getPosition(anyInt())).thenReturn(position);
        when(buffer.extract(anyInt(), anyInt())).thenReturn("");

        presenter.handleParseNodeShow(node);

        //noinspection unchecked
        verify(view).highlightText(anyList(), same(position));
    }
}
