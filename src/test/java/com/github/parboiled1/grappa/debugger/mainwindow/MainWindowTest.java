package com.github.parboiled1.grappa.debugger.mainwindow;

import com.github.parboiled1.grappa.debugger.alert.AlertFactory;
import com.github.parboiled1.grappa.debugger.parser.MatchResult;
import javafx.event.ActionEvent;
import javafx.scene.control.TreeItem;
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
import static org.mockito.Mockito.when;

public final class MainWindowTest
{
    private MainWindowView view;
    private MainWindowModel model;

    private MainWindowPresenterBuilder builder;
    private MainWindowPresenter presenter;
    private AlertFactory alertFactory;

    private MainWindowUi ui;
    private ActionEvent event;

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
        ui.init(presenter);
        event = mock(ActionEvent.class);
    }

    @Test
    public void closeWindowEventTest()
    {
        final InOrder inOrder = inOrder(presenter, view);

        ui.closeWindowEvent(event);

        inOrder.verify(presenter).handleCloseWindow();
        inOrder.verify(view).closeWindow();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void loadFileEventNoFileTest()
    {
        // This is the default but let's make it explicit
        // NOTE: when(presenter.getInputFile()).thenReturn(null) doesn't work
        doReturn(null).when(presenter).getInputFile();

        final InOrder inOrder = inOrder(presenter, view, alertFactory);

        ui.loadFileEvent(event);

        inOrder.verify(presenter).handleLoadFile();
        inOrder.verify(presenter).getInputFile();
        inOrder.verifyNoMoreInteractions();
    }

    @Test(dependsOnMethods = "loadFileEventNoFileTest")
    public void loadFileEventIOErrorTest()
        throws IOException
    {
        final File file = mock(File.class);
        final IOException exception = new IOException();

        doReturn(file).when(presenter).getInputFile();
        doThrow(exception).when(presenter).getContents(file);
        doNothing().when(alertFactory)
           .showError(anyString(), anyString(), same(exception));

        final InOrder inOrder = inOrder(presenter, view, alertFactory);

        ui.loadFileEvent(event);

        inOrder.verify(presenter).handleLoadFile();
        inOrder.verify(presenter).getInputFile();
        inOrder.verify(presenter).getContents(file);
        inOrder.verify(alertFactory)
            .showError(anyString(), anyString(), same(exception));
        inOrder.verifyNoMoreInteractions();
    }

    @Test(dependsOnMethods = {
        "loadFileEventNoFileTest",
        "loadFileEventIOErrorTest"
    })
    public void loadFileEventOKTest()
        throws IOException
    {
        final File file = mock(File.class);
        final String content = "file contents";

        doReturn(file).when(presenter).getInputFile();
        doReturn(content).when(presenter).getContents(file);

        final InOrder inOrder = inOrder(presenter, view);

        ui.loadFileEvent(event);

        inOrder.verify(presenter).handleLoadFile();
        inOrder.verify(presenter).getInputFile();
        inOrder.verify(presenter).getContents(file);
        inOrder.verify(view).setInputText(content);
        inOrder.verifyNoMoreInteractions();
    }

    // TODO: this test is probably too complicated
    @Test
    public void parseEventTest()
    {
        final String inputText = "meh";
        @SuppressWarnings("unchecked")
        final TreeItem<MatchResult> item = mock(TreeItem.class);

        when(view.getInputText()).thenReturn(inputText);
        when(model.runTrace(same(inputText))).thenReturn(item);

        final InOrder inOrder = inOrder(presenter, model, view);

        ui.parseEvent(event);

        inOrder.verify(presenter).handleParse();
        inOrder.verify(view).getInputText();
        inOrder.verify(model).runTrace(same(inputText));
        inOrder.verify(view).setParseTree(same(item));
        inOrder.verifyNoMoreInteractions();
    }
}
