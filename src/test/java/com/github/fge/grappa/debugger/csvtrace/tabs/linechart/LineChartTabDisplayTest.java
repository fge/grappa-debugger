package com.github.fge.grappa.debugger.csvtrace.tabs.linechart;

import javafx.event.Event;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class LineChartTabDisplayTest
{
    private LineChartTabPresenter presenter;
    private LineChartTabDisplay display;

    @BeforeMethod
    public void init()
    {
        presenter = mock(LineChartTabPresenter.class);
        display = spy(new LineChartTabDisplay());
        doNothing().when(display).init();
        display.setPresenter(presenter);
    }

    @Test
    public void changeLinesDisplayedEventTest()
    {
        final int theAnswer = 42;

        display.changeLinesDisplayedEvent(theAnswer);

        verify(presenter).handleChangeLinesDisplayed(eq(theAnswer));
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
}
