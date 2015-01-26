package com.github.fge.grappa.debugger.tracetab.stat.perclass;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PerClassStatsPresenterTest
{
    private PerClassStatsView view;
    private PerClassStatsPresenter presenter;
    
    @BeforeMethod
    public void init()
    {
        view = mock(PerClassStatsView.class);
        presenter = new PerClassStatsPresenter();
        presenter.setView(view);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void loadStatsTest()
    {
        presenter.loadStats();

        verify(view).loadPerClass(any(Map.class));
    }
}
