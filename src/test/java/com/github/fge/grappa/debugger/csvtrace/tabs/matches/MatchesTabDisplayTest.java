package com.github.fge.grappa.debugger.csvtrace.tabs.matches;

import javafx.event.Event;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class MatchesTabDisplayTest
{
    private MatchesTabPresenter presenter;
    private MatchesTabDisplay display;

    @BeforeMethod
    public void init()
    {
        presenter = mock(MatchesTabPresenter.class);
        display = spy(new MatchesTabDisplay());
        doNothing().when(display).init();
        display.setPresenter(presenter);
    }

    @Test
    public void refreshStatisticsEventTest()
    {
        display.refreshStatisticsEvent(mock(Event.class));

        verify(presenter).handleRefreshStatisticsEvent();
    }
}
