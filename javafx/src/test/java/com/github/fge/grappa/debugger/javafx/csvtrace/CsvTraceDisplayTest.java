package com.github.fge.grappa.debugger.javafx.csvtrace;

import com.github.fge.grappa.debugger.csvtrace.CsvTracePresenter;
import javafx.event.Event;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class CsvTraceDisplayTest
{
    private CsvTracePresenter presenter;
    private CsvTraceDisplay display;

    @BeforeMethod
    public void init()
    {
        presenter = mock(CsvTracePresenter.class);
        display = spy(new CsvTraceDisplay());
        doNothing().when(display).init();
        display.setPresenter(presenter);
    }

    @Test
    public void tabsRefreshEventTest()
    {
        display.tabsRefreshEvent(mock(Event.class));

        verify(presenter).handleTabsRefreshEvent();
    }
}
