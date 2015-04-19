package com.github.fge.grappa.debugger.javafx.main;

import com.github.fge.grappa.debugger.main.MainWindowPresenter;
import javafx.event.ActionEvent;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class MainWindowDisplayTest
{
    private MainWindowPresenter presenter;
    private MainWindowDisplay display;

    @BeforeMethod
    public void init()
    {
        presenter = mock(MainWindowPresenter.class);
        display = spy(new MainWindowDisplay());

        doNothing().when(display).init();

        display.setPresenter(presenter);
    }

    @Test
    public void newWindowEventTest()
    {
        display.newWindowEvent(mock(ActionEvent.class));

        verify(presenter).handleNewWindow();
    }

    @Test
    public void closeWindowEventTest()
    {
        display.closeWindowEvent(mock(ActionEvent.class));

        verify(presenter).handleCloseWindow();
    }

    @Test
    public void loadFileEventTest()
    {
        display.loadFileEvent(mock(ActionEvent.class));

        verify(presenter).handleLoadFile();
    }
}
