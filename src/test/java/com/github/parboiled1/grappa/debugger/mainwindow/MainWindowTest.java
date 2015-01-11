package com.github.parboiled1.grappa.debugger.mainwindow;

import javafx.event.ActionEvent;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public final class MainWindowTest
{
    private MainWindowUi ui;
    private MainWindowView view;
    private MainWindowPresenter presenter;

    @BeforeMethod
    public void init()
    {
        view = mock(MainWindowView.class);
        ui = spy(new MainWindowUi());
        presenter = spy(new MainWindowPresenter(ui, view));
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
}
