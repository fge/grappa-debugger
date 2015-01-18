package com.github.parboiled1.grappa.debugger.tracetab;

import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.github.parboiled1.grappa.debugger.tracetab.statistics.InputTextInfo;
import com.github.parboiled1.grappa.debugger.tracetab.statistics.ParseNode;
import com.github.parboiled1.grappa.debugger.tracetab.statistics.RuleStatistics;
import com.github.parboiled1.grappa.trace.ParsingRunTrace;
import com.github.parboiled1.grappa.trace.TraceEvent;
import com.github.parboiled1.grappa.trace.TraceEventType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class TraceTabPresenterTest
{
    private TraceTabView view;

    @SuppressWarnings("unchecked")
    @BeforeMethod
    public void init()
    {
        view = mock(TraceTabView.class);
    }

    @SuppressWarnings({ "unchecked", "AutoBoxing" })
    @Test
    public void loadTraceWithEmptyEvents()
    {
        final List<TraceEvent> events = mock(List.class);
        when(events.isEmpty()).thenReturn(true);

        final TraceTabModel model = mock(TraceTabModel.class);
        when(model.getTraceEvents()).thenReturn(events);

        final TraceTabPresenter presenter = new TraceTabPresenter(model);

        presenter.loadTrace();

        verifyZeroInteractions(view);
    }

    @SuppressWarnings({ "unchecked", "AutoBoxing" })
    @Test
    public void loadTraceWithNonEmptyEvents()
    {
        final long fakeDate = 87238987982713987L;
        final String fakeText = "moo";
        final TraceEvent fakeEvent = new TraceEvent(TraceEventType.BEFORE_MATCH,
            0L, 0, "", "", 0);
        final List<TraceEvent> events = Collections.singletonList(fakeEvent);

        final ParsingRunTrace trace = new ParsingRunTrace(fakeDate, events);
        final InputBuffer buffer = mock(InputBuffer.class);
        final InputTextInfo textInfo = mock(InputTextInfo.class);
        final ParseNode parseNode = mock(ParseNode.class);
        final Collection<RuleStatistics> ruleStats = mock(Collection.class);

        when(buffer.extract(anyInt(), anyInt())).thenReturn(fakeText);

        final TraceTabModel model = mock(TraceTabModel.class);
        when(model.getTrace()).thenReturn(trace);
        when(model.getTraceEvents()).thenReturn(events);
        when(model.getInputBuffer()).thenReturn(buffer);
        when(model.getInputTextInfo()).thenReturn(textInfo);
        when(model.getParseTreeRoot()).thenReturn(parseNode);
        when(model.getRuleStats()).thenReturn(ruleStats);

        final TraceTabPresenter presenter = new TraceTabPresenter(model);
        presenter.setView(view);

        presenter.loadTrace();

        verify(view).setInputText(same(fakeText));
        verify(view).setInputTextInfo(same(textInfo));
        verify(view).setParseDate(fakeDate);
        verify(view).setParseTree(same(parseNode));
        verify(view).setStatistics(same(ruleStats));
        verify(view).setTraceEvents(same(events));
        verifyNoMoreInteractions(view);
    }
}
