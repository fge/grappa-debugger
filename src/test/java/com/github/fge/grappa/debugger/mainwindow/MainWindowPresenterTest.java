package com.github.fge.grappa.debugger.mainwindow;

import com.github.fge.grappa.debugger.MainWindowFactory;
import com.github.fge.grappa.debugger.legacy.tracetab.LegacyTraceTabPresenter;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class MainWindowPresenterTest
{
    private MainWindowFactory factory;
    private MainWindowView view;
    private MainWindowPresenter presenter;

    @BeforeMethod
    public void init()
    {
        factory = mock(MainWindowFactory.class);
        view = mock(MainWindowView.class);
        presenter = spy(new MainWindowPresenter(factory, view));
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

    @Test
    public void handleLoadFileNoFileTest()
    {
        when(view.chooseFile()).thenReturn(null);

        presenter.handleLoadFile();

        final InOrder inOrder = inOrder(factory, view);

        inOrder.verify(view).chooseFile();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void handleLoadFileWithFileTest()
        throws IOException
    {
        final Path path = mock(Path.class);

        when(view.chooseFile()).thenReturn(path);
        doNothing().when(presenter).loadPresenter(same(path));

        presenter.handleLoadFile();

        verify(view, only()).chooseFile();
        verify(presenter).loadPresenter(same(path));
    }

    @Test(dependsOnMethods = "handleLoadFileWithFileTest")
    public void handleLoadFileTwiceTest()
        throws IOException
    {
        final Path path = mock(Path.class);

        when(view.chooseFile()).thenReturn(path);

        presenter.tabPresenter = mock(LegacyTraceTabPresenter.class);

        final MainWindowPresenter otherPresenter
            = mock(MainWindowPresenter.class);
        doNothing().when(otherPresenter).loadPresenter(same(path));

        when(factory.createWindow()).thenReturn(otherPresenter);

        presenter.handleLoadFile();

        verify(view, only()).chooseFile();
        verify(factory).createWindow();
        verify(otherPresenter).loadPresenter(same(path));
    }
}
