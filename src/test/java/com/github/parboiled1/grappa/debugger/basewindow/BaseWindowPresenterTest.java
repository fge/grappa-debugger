package com.github.parboiled1.grappa.debugger.basewindow;

import com.github.parboiled1.grappa.debugger.BaseWindowFactory;
import com.github.parboiled1.grappa.debugger.tracetab.TraceTabPresenter;
import javafx.stage.Stage;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
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

    @Test(enabled = false)
    public void handleLoadTabTest()
    {
        presenter.handleLoadTab();
        verify(view).injectTab(any(TraceTabPresenter.class));
        verifyZeroInteractions(factory);
    }

    @Test
    public void handleLoadFileNoFileTest()
    {
        when(view.chooseFile(anyObject())).thenReturn(null);

        final Stage stage = mock(Stage.class);
        when(factory.getStage(presenter)).thenReturn(stage);

        presenter.handleLoadFile();

        final InOrder inOrder = inOrder(factory, view);

        inOrder.verify(factory).getStage(presenter);
        inOrder.verify(view).chooseFile(same(stage));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void handleLoadFileWithFileTest()
        throws IOException
    {
        final File file = mock(File.class);
        final Path path = mock(Path.class);
        final TraceTabPresenter tabPresenter = mock(TraceTabPresenter.class);
        final Stage stage = mock(Stage.class);

        when(view.chooseFile(anyObject())).thenReturn(file);
        when(file.toPath()).thenReturn(path);
        doReturn(tabPresenter).when(presenter).loadFile(same(path));
        when(factory.getStage(presenter)).thenReturn(stage);

        presenter.handleLoadFile();

        final InOrder inOrder = inOrder(factory, view);
        inOrder.verify(factory).getStage(presenter);
        inOrder.verify(view).chooseFile(same(stage));
        inOrder.verify(view).injectTab(same(tabPresenter));
        inOrder.verifyNoMoreInteractions();
    }
}
