package com.github.parboiled1.grappa.debugger.mainwindow;

import com.github.parboiled1.grappa.debugger.alert.AlertFactory;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class MainWindowTest
{
    private MainWindowView view;
    private MainWindowModel model;

    private MainWindowPresenterBuilder builder;
    private MainWindowPresenter presenter;
    private AlertFactory alertFactory;

    private MainWindowUi ui;

    @BeforeMethod
    public void init()
    {
        view = mock(MainWindowView.class);
        model = mock(MainWindowModel.class);
        alertFactory = spy(new AlertFactory());

        builder = new MainWindowPresenterBuilder().withStage(mock(Stage.class))
            .withView(view).withModel(model).withAlertFactory(alertFactory);
        presenter = spy(builder.build());

        ui = spy(new MainWindowUi());
        ui.init(presenter, view);
    }

    @Test
    public void closeWindowTest()
    {
        final InOrder inOrder = inOrder(presenter, view);

        ui.closeWindow(mock(ActionEvent.class));

        inOrder.verify(presenter).closeWindow();
        inOrder.verify(view).closeWindow();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void traceTest()
    {
        final String trace = "trace";
        presenter.addTrace(trace);
        verify(view).addTrace(trace);
    }

    @Test
    public void runTraceTest()
    {
        final String input = "input";
        when(view.getInputText()).thenReturn(input);

        final InOrder inOrder = inOrder(presenter, view, model);

        ui.runTrace(mock(ActionEvent.class));

        inOrder.verify(presenter).runTrace();
        inOrder.verify(view).getInputText();
        inOrder.verify(model).trace(presenter, input);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void loadFileNoFileTest()
        throws IOException
    {
        // This is the default but let's make it explicit
        // NOTE: when(presenter.getInputFile()).thenReturn(null) doesn't work
        doReturn(null).when(presenter).getInputFile();

        final InOrder inOrder = inOrder(presenter, view, alertFactory);

        ui.loadInput(mock(ActionEvent.class));

        inOrder.verify(presenter).getInputFile();
        inOrder.verifyNoMoreInteractions();
    }

    @Test(dependsOnMethods = "loadFileNoFileTest")
    public void loadFileIOErrorTest()
        throws IOException
    {
        final File file = mock(File.class);
        final IOException exception = new IOException();

        doReturn(file).when(presenter).getInputFile();
        doThrow(exception).when(presenter).getContents(file);
        doNothing().when(alertFactory)
           .showError(anyString(), anyString(), same(exception));

        final InOrder inOrder = inOrder(presenter, view, alertFactory);

        ui.loadInput(mock(ActionEvent.class));

        inOrder.verify(presenter).getInputFile();
        inOrder.verify(presenter).getContents(file);
        inOrder.verify(alertFactory)
            .showError(anyString(), anyString(), same(exception));
        inOrder.verifyNoMoreInteractions();
    }

    @Test(dependsOnMethods = {
        "loadFileNoFileTest",
        "loadFileIOErrorTest"
    })
    public void loadFileOKTest()
        throws IOException
    {
        final File file = mock(File.class);
        final String content = "file contents";

        doReturn(file).when(presenter).getInputFile();
        doReturn(content).when(presenter).getContents(file);

        final InOrder inOrder = inOrder(presenter, view);

        ui.loadInput(mock(ActionEvent.class));
        
        inOrder.verify(presenter).getInputFile();
        inOrder.verify(presenter).getContents(file);
        inOrder.verify(view).setInputText(content);
        inOrder.verifyNoMoreInteractions();
    }
}
