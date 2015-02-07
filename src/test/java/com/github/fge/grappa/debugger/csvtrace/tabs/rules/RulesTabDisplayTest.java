package com.github.fge.grappa.debugger.csvtrace.tabs.rules;

import javafx.event.Event;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class RulesTabDisplayTest
{
    private RulesTabPresenter presenter;
    private RulesTabDisplay display;

    @BeforeMethod
    public void init()
    {
        display = spy(new RulesTabDisplay());
        presenter = mock(RulesTabPresenter.class);
        doNothing().when(display).init();
        display.setPresenter(presenter);
    }

    @Test
    public void t()
    {
        display.refreshInvocationStatisticsEvent(mock(Event.class));

        verify(presenter).handleRefreshInvocationStatistics();
    }
}
