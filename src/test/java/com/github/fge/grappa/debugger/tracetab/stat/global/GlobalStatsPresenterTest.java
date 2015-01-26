package com.github.fge.grappa.debugger.tracetab.stat.global;

import com.github.fge.grappa.debugger.stats.RuleMatchingStats;
import com.github.fge.grappa.trace.ParseRunInfo;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Collections;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GlobalStatsPresenterTest
{
    private final Collection<RuleMatchingStats> stats
        = Collections.emptyList();
    private final ParseRunInfo info = new ParseRunInfo(0L, 0, 0, 0);

    private GlobalStatsView view;
    private GlobalStatsPresenter presenter;

    @BeforeMethod
    public void init()
    {
        view = mock(GlobalStatsView.class);
        presenter = new GlobalStatsPresenter(stats, info);
        presenter.setView(view);
    }
    @Test
    public void loadStatsTest()
    {
        presenter.loadStats();

        verify(view).loadStats(same(stats));
        verify(view).loadInfo(same(info), anyInt(), anyInt(), anyLong());
        verify(view).loadPieChart(anyInt(), anyInt(), anyInt());
    }
}
