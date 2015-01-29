package com.github.fge.grappa.debugger.tracetab;

import com.github.fge.grappa.debugger.stats.ParseNode;
import javafx.event.Event;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class TraceTabDisplayTest
{
    private TraceTabDisplay display;
    private TraceTabPresenter presenter;

    @BeforeMethod
    public void init()
    {
        display = spy(new TraceTabDisplay());
        presenter = mock(TraceTabPresenter.class);

        doNothing().when(display).init();
        display.setPresenter(presenter);
    }

    @Test
    public void expandParseTreeEventTest()
    {
        display.expandParseTreeEvent(mock(Event.class));

        verify(presenter, only()).handleExpandParseTree();
    }

    @Test
    public void parseNodeShowEventTest()
    {
        final ParseNode node = mock(ParseNode.class);
        display.parseNodeShowEvent(node);

        verify(presenter, only()).handleParseNodeShow(same(node));
    }
}
