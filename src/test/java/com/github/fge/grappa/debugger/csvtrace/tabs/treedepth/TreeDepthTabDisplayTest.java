package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import javafx.event.Event;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class TreeDepthTabDisplayTest
{
    private TreeDepthTabPresenter presenter;
    private TreeDepthTabDisplay display;

    @BeforeMethod
    public void init()
    {
        presenter = mock(TreeDepthTabPresenter.class);
        display = spy(new TreeDepthTabDisplay());
        doNothing().when(display).init();
        display.setPresenter(presenter);
    }

    @Test
    public void displayLinesEvent()
    {
        final int nrLines = 42;

        display.displayLinesEvent(nrLines);

        verify(presenter).handleDisplayedLines(nrLines);
    }

    @Test
    public void previousLinesEventTest()
    {
        display.previousLinesEvent(mock(Event.class));

        verify(presenter).handlePreviousLines();
    }

    @Test
    public void nextLinesEventTest()
    {
        display.nextLinesEvent(mock(Event.class));

        verify(presenter).handleNextLines();
    }

    @Test
    public void chartRefreshEventTest()
    {
        display.chartRefreshEvent(mock(Event.class));

        verify(presenter).handleChartRefreshEvent();
    }
}
