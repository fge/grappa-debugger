package com.github.parboiled1.grappa.debugger.mainwindow;

import javafx.event.ActionEvent;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class MainWindowTest
{
    private MainWindowUi ui;
    private MainWindowView view;
    private MainWindowPresenter presenter;
    private MainWindowModel model;

    @BeforeMethod
    public void init()
    {
        view = mock(MainWindowView.class);
        ui = spy(new MainWindowUi());
        model = mock(MainWindowModel.class);
        presenter = spy(new MainWindowPresenter(ui, view, model));
        ui.init(presenter, view);
    }

    @Test
    public void loadInputTest()
    {
        final InOrder inOrder = inOrder(presenter, view);

        ui.loadInput(mock(ActionEvent.class));

        inOrder.verify(presenter).loadInput();
        inOrder.verify(view).setInputText(anyString());
        inOrder.verifyNoMoreInteractions();
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
