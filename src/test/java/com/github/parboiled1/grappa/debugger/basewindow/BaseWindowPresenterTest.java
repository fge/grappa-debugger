package com.github.parboiled1.grappa.debugger.basewindow;

import com.github.parboiled1.grappa.debugger.BaseWindowFactory;
import com.github.parboiled1.grappa.debugger.tracetab.TraceTabPresenter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class BaseWindowPresenterTest
{
    private BaseWindowFactory factory;
    private BaseWindowView view;
    private BaseWindowPresenter presenter;

    @BeforeMethod
    public void init()
    {
        factory = mock(BaseWindowFactory.class);
        view = mock(BaseWindowView.class);
        presenter = new BaseWindowPresenter(factory, view);
    }

    @Test
    public void handleCloseWindowTest()
    {
        presenter.handleCloseWindow();
        verifyZeroInteractions(view);
        verify(factory).close(presenter);
    }

    @Test
    public void handleNewWindowTest()
    {
        presenter.handleNewWindow();
        verifyZeroInteractions(view);
        verify(factory).createWindow();
    }

    @Test(enabled = false)
    public void handleLoadTabTest()
    {
        presenter.handleLoadTab();
        verify(view).injectTab(any(TraceTabPresenter.class));
        verifyZeroInteractions(factory);
    }
}