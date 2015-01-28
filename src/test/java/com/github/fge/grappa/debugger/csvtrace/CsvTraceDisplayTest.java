package com.github.fge.grappa.debugger.csvtrace;

import javafx.event.Event;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class CsvTraceDisplayTest
{
    private CsvTraceDisplay display;
    private CsvTracePresenter presenter;

    @BeforeMethod
    public void init()
    {
        display = spy(new CsvTraceDisplay());
        doNothing().when(display).init();

        presenter = mock(CsvTracePresenter.class);
        display.setPresenter(presenter);
    }

    @Test
    public void expandTreeEventTest()
    {
        display.expandParseTreeEvent(mock(Event.class));

        verify(presenter, only()).handleExpandParseTree();
    }
}
