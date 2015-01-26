package com.github.fge.grappa.debugger.tracetab.stat.classdetails;

import com.github.fge.grappa.debugger.stats.classdetails.MatcherClassDetails;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClassDetailsStatsPresenterTest
{
    private ClassDetailsStatsModel model;
    private ClassDetailsStatsPresenter presenter;
    private ClassDetailsStatsView view;

    @BeforeMethod
    public void init()
    {
        model = mock(ClassDetailsStatsModel.class);
        presenter = new ClassDetailsStatsPresenter(model);
        view = mock(ClassDetailsStatsView.class);
        presenter.setView(view);
    }

    @Test
    public void loadStatsTest()
    {
        @SuppressWarnings("unchecked")
        final Map<String, MatcherClassDetails> classDetails = mock(Map.class);

        when(model.getClassDetails()).thenReturn(classDetails);

        presenter.loadStats();

        verify(view, only()).loadClassDetails(same(classDetails));
    }

    @Test
    public void showClassDetailsTest()
    {
        final MatcherClassDetails details = mock(MatcherClassDetails.class);

        presenter.handleShowClassDetails(details);

        verify(view).showClassDetails(same(details));
    }
}
