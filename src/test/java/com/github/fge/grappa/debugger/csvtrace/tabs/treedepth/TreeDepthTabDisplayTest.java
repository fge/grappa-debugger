package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import javafx.event.Event;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.stream.Stream;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
    public void changeVisibleLinesEventTest()
    {
        final int nrLines = 42;

        display.changeVisibleLinesEvent(nrLines);

        verify(presenter).handleChangeVisibleLines(nrLines);
    }

    @DataProvider
    public Iterator<Object[]> validLineInputs()
    {
        return Stream.of("1", "788", "2117")
            .map(s -> new Object[] { s, Integer.parseInt(s) })
            .iterator();
    }

    @Test(dataProvider = "validLineInputs")
    public void doChangeStartLineEventValidInputTest(final String input,
        final int startLine)
    {
        display.doChangeStartLineEvent(input);

        verify(presenter).handleChangeStartLine(startLine);
    }

    @DataProvider
    public Iterator<Object[]> invalidLineInputs()
    {
        return Stream.of(null, "", "-1", "abc", "90283098203982093820392803928",
            "0")
            .map(s -> new Object[] { s })
            .iterator();
    }

    @Test(dataProvider = "invalidLineInputs")
    public void doChangeStartLineEventInvalidInputTest(final String input)
    {
        display.doChangeStartLineEvent(input);

        verify(presenter, never()).handleChangeStartLine(anyInt());
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
