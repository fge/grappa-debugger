package com.github.fge.grappa.debugger.tracetab;

import com.github.fge.grappa.debugger.legacy.InputTextInfo;
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

public class TraceTabPresenterTest
{
    private TraceTabGuiController guiController;
    private TraceTabModel model;
    private TraceTabPresenter presenter;
    private InputBuffer buffer;

    @SuppressWarnings("unchecked")
    @BeforeMethod
    public void init()
    {
        guiController = mock(TraceTabGuiController.class);
        model = mock(TraceTabModel.class);
        buffer = mock(InputBuffer.class);
        when(model.getInputBuffer()).thenReturn(buffer);
        presenter = new TraceTabPresenter(model);
        presenter.setGuiController(guiController);
    }

    // FIXME: too complicated
    @SuppressWarnings({ "unchecked", "AutoBoxing" })
    @Test
    public void loadTraceTest()
    {
        final long fakeDate = 87238987982713987L;
        final TraceEvent fakeEvent = new TraceEvent(TraceEventType.BEFORE_MATCH,
            0L, 0, "", "", 0);
        final List<TraceEvent> events = Collections.singletonList(fakeEvent);

        final ParsingRunTrace trace = new ParsingRunTrace(fakeDate, events);

        when(model.getTrace()).thenReturn(trace);
        when(model.getTraceEvents()).thenReturn(events);

        presenter.loadTrace();

        verify(guiController).setInputText(anyString());
        verify(guiController).setInputTextInfo(any(InputTextInfo.class));
        verify(guiController).setParseDate(fakeDate);
        verify(guiController).setParseTree(any(ParseNode.class));
        verify(guiController).setStatistics(anyCollection());
        verify(guiController).setTraceEvents(same(events));
        verifyNoMoreInteractions(guiController);
    }

    @Test
    public void handleParseNodeShowTest()
    {
        final ParseNode node = mock(ParseNode.class);

        final Position position = new Position(12, 12);
        when(buffer.getPosition(anyInt())).thenReturn(position);
        when(buffer.extract(anyInt(), anyInt())).thenReturn("");

        presenter.handleParseNodeShow(node);

        verify(guiController).fillParseNodeDetails(same(node),
            any(InputBuffer.class));
        //noinspection unchecked
        verify(guiController).highlightText(anyList(), same(position),
            anyBoolean());
        verifyNoMoreInteractions(guiController);
    }
}
