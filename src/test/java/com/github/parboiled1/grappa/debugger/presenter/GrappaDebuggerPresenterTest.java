package com.github.parboiled1.grappa.debugger.presenter;

import com.github.parboiled1.grappa.debugger.view.GrappaDebuggerView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

public class GrappaDebuggerPresenterTest
{
    private GrappaDebuggerView view;
    private GrappaDebuggerPresenter presenter;

    @BeforeMethod
    public void init()
    {
        view = mock(GrappaDebuggerView.class);
        presenter = new GrappaDebuggerPresenter(view);
    }

    @Test
    public void displayTest()
    {
        presenter.display();

        verify(view, only()).display();
    }

    @Test
    public void closeTest()
    {
        presenter.close();

        verify(view, only()).close();
    }
}