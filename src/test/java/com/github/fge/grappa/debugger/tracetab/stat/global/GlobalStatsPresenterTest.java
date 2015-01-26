package com.github.fge.grappa.debugger.tracetab.stat.global;

import com.github.fge.grappa.debugger.stats.global.GlobalParseInfo;
import com.github.fge.grappa.debugger.stats.global.RuleMatchingStats;
import com.github.fge.grappa.trace.ParseRunInfo;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Collections;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GlobalStatsPresenterTest
{
    private final Collection<RuleMatchingStats> stats
        = Collections.emptyList();
    private final ParseRunInfo info = new ParseRunInfo(0L, 0, 0, 0);

    private GlobalStatsModel model;
    private GlobalStatsView view;
    private GlobalStatsPresenter presenter;

    @BeforeMethod
    public void init()
    {
        view = mock(GlobalStatsView.class);
        model = mock(GlobalStatsModel.class);
        presenter = new GlobalStatsPresenter(model);
        presenter.setView(view);
    }

    @SuppressWarnings("AutoBoxing")
    @Test
    public void loadStatsTest()
    {
        final GlobalParseInfo parseInfo = mock(GlobalParseInfo.class);
        final int theAnswer = 42;
        when(parseInfo.getTotalMatches()).thenReturn(theAnswer);

        when(model.getRuleStats()).thenReturn(stats);
        when(model.getParseRunInfo()).thenReturn(info);
        when(model.getGlobalParseInfo()).thenReturn(parseInfo);
        presenter.loadStats();

        verify(view).loadStats(same(stats));
        verify(view).loadInfo(same(info), eq(theAnswer));
        verify(view).loadParseInfo(same(parseInfo));
    }
}
