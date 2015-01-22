package com.github.fge.grappa.debugger.legacy.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.legacy.InputTextInfo;
import com.github.fge.grappa.debugger.legacy.LegacyTraceEvent;
import com.github.fge.grappa.debugger.legacy.ParsingRunTrace;
import com.github.fge.grappa.debugger.statistics.ParseNode;
import com.github.fge.grappa.trace.TraceEventType;
import org.parboiled.support.Position;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class LegacyTraceTabPresenterTest
{
    private LegacyTraceTabView view;
    private LegacyTraceTabModel model;
    private LegacyTraceTabPresenter presenter;
    private InputBuffer buffer;

    @SuppressWarnings("unchecked")
    @BeforeMethod
    public void init()
    {
        view = mock(LegacyTraceTabView.class);
        model = mock(LegacyTraceTabModel.class);
        buffer = mock(InputBuffer.class);
        when(model.getInputBuffer()).thenReturn(buffer);
        presenter = new LegacyTraceTabPresenter(model);
        presenter.setView(view);
    }

    // FIXME: too complicated
    @SuppressWarnings({ "unchecked", "AutoBoxing" })
    @Test
    public void loadTraceTest()
    {
        final long fakeDate = 87238987982713987L;
        final LegacyTraceEvent fakeEvent =
            new LegacyTraceEvent(TraceEventType.BEFORE_MATCH, 0L, 0, "", "", 0);
        final List<LegacyTraceEvent> events
            = Collections.singletonList(fakeEvent);

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

        verify(view).fillParseNodeDetails(same(node),
            any(InputBuffer.class));
        //noinspection unchecked
        verify(view).highlightText(anyList(), same(position),
            anyBoolean());
        verifyNoMoreInteractions(view);
    }
}
