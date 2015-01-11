package com.github.parboiled1.grappa.debugger.mainwindow;

import com.github.parboiled1.grappa.debugger.alert.AlertFactory;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
}
