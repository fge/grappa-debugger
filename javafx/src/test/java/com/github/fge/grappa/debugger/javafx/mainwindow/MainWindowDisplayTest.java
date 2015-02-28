package com.github.fge.grappa.debugger.javafx.mainwindow;

import com.github.fge.grappa.debugger.mainwindow.MainWindowPresenter;
import javafx.event.ActionEvent;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MainWindowDisplayTest
{
    private MainWindowPresenter presenter;
    private MainWindowDisplay display;
    private ActionEvent event;

    @BeforeClass
    public void init()
    {
        presenter = mock(MainWindowPresenter.class);
        display = new MainWindowDisplay();
        display.setPresenter(presenter);
        event = mock(ActionEvent.class);
    }

    @Test
    public void closeWindowEventTest()
    {
        display.closeWindowEvent(event);
        verify(presenter).handleCloseWindow();
    }

    @Test
    public void newWindowEventTest()
    {
        display.newWindowEvent(event);
        verify(presenter).handleNewWindow();
    }

    @Test
    public void loadFileEventTest()
    {
        display.loadFileEvent(event);
        verify(presenter).handleLoadFile();
    }
}
