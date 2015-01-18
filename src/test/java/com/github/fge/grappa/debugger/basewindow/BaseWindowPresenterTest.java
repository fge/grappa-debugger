package com.github.fge.grappa.debugger.basewindow;

import com.github.fge.grappa.debugger.BaseWindowFactory;
import com.github.fge.grappa.debugger.tracetab.TraceTabPresenter;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

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
        presenter = spy(new BaseWindowPresenter(factory, view));
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
        final File file = mock(File.class);
        final Path path = mock(Path.class);
        final TraceTabPresenter tabPresenter = mock(TraceTabPresenter.class);

        when(view.chooseFile()).thenReturn(file);
        when(file.toPath()).thenReturn(path);
        doReturn(tabPresenter).when(presenter).loadFile(same(path));

        presenter.handleLoadFile();

        final InOrder inOrder = inOrder(factory, view, tabPresenter);
        inOrder.verify(view).chooseFile();
        inOrder.verify(view).injectTab(same(tabPresenter));
        inOrder.verify(tabPresenter).loadTrace();
        inOrder.verify(view).setWindowTitle(anyString());
        inOrder.verifyNoMoreInteractions();
    }
}
