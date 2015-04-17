package com.github.fge.grappa.debugger.javafx.trace;

import com.github.fge.grappa.debugger.trace.TracePresenter;
import javafx.event.Event;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class TraceDisplayTest
{
    private TraceDisplay display;
    private TracePresenter presenter;

    @BeforeMethod
    public void init()
    {
        presenter = mock(TracePresenter.class);
        display = spy(new TraceDisplay());
        doNothing().when(display).init();

        display.setPresenter(presenter);
    }

    @Test
    public void tabsRefreshEventTest()
    {
        doNothing().when(presenter).handleTabsRefreshEvent();

        display.tabsRefreshEvent(mock(Event.class));

        verify(presenter).handleTabsRefreshEvent();
    }
}
