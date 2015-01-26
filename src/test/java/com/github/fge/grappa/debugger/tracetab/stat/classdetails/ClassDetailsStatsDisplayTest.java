package com.github.fge.grappa.debugger.tracetab.stat.classdetails;

import com.github.fge.grappa.debugger.stats.classdetails.MatcherClassDetails;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ClassDetailsStatsDisplayTest
{
    private ClassDetailsStatsPresenter presenter;
    private ClassDetailsStatsDisplay display;

    @BeforeClass
    public void init()
    {
        presenter = mock(ClassDetailsStatsPresenter.class);
        display = spy(new ClassDetailsStatsDisplay());
        doNothing().when(display).init();
        display.setPresenter(presenter);
    }

    @Test
    public void showClassDetailsEventTest()
    {
        final MatcherClassDetails details = mock(MatcherClassDetails.class);

        display.showClassDetailsEvent(details);

        verify(presenter).handleShowClassDetails(same(details));
    }
}
