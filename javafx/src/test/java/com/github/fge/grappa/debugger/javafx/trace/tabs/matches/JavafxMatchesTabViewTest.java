package com.github.fge.grappa.debugger.javafx.trace.tabs.matches;

import com.github.fge.grappa.debugger.JavafxViewTest;
import com.github.fge.grappa.debugger.model.matches.MatchStatistics;
import com.github.fge.grappa.debugger.model.matches.MatchesData;
import javafx.scene.text.Text;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JavafxMatchesTabViewTest
    extends JavafxViewTest
{
    private JavafxMatchesTabView view;
    private MatchesTabDisplay display;

    @BeforeMethod
    public void init()
        throws IOException
    {
        view = spy(new JavafxMatchesTabView());
        display = view.getDisplay();
    }

    @SuppressWarnings({ "AutoBoxing", "unchecked" })
    @Test
    public void displayMatchesDataTest()
    {
        final MatchesData data = mock(MatchesData.class);

        final List<MatchStatistics> stats = mock(List.class);
        when(data.getAllStats()).thenReturn(stats);

        final int nonEmpty = 1;
        final int empty = 2;
        final int failures = 3;

        when(data.getNonEmptyMatches()).thenReturn(nonEmpty);
        when(data.getEmptyMatches()).thenReturn(empty);
        when(data.getFailedMatches()).thenReturn(failures);

        final Random random = new Random(System.currentTimeMillis());

        final Double topOne = random.nextDouble();
        final Double topFive = random.nextDouble();
        final Double topTen = random.nextDouble();

        when(data.getTopOne()).thenReturn(topOne);
        when(data.getTopFive()).thenReturn(topFive);
        when(data.getTopTen()).thenReturn(topTen);

        doNothing().when(view).showMatches(anyList());
        doNothing().when(view).showMatchesStats(anyInt(), anyInt(), anyInt());

        doNothing().when(view).showTopN(any(Text.class), anyDouble());

        view.displayMatchesData(data);

        verify(view).showMatches(same(stats));
        verify(view).showMatchesStats(nonEmpty, empty, failures);

        verify(view).showTopN(display.topRulePct, topOne);
        verify(view).showTopN(display.topFiveRulePct, topFive);
        verify(view).showTopN(display.topTenRulePct, topTen);
    }
}
