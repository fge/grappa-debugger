package com.github.fge.grappa.debugger.basewindow;

import javafx.event.ActionEvent;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BaseWindowGuiTest
{
    private BaseWindowPresenter presenter;
    private BaseWindowGui gui;
    private ActionEvent event;

    @BeforeClass
    public void init()
    {
        presenter = mock(BaseWindowPresenter.class);
        gui = new BaseWindowGui();
        gui.init(presenter);
        event = mock(ActionEvent.class);
    }

    @Test
    public void closeWindowEventTest()
    {
        gui.closeWindowEvent(event);
        verify(presenter).handleCloseWindow();
    }

    @Test
    public void newWindowEventTest()
    {
        gui.newWindowEvent(event);
        verify(presenter).handleNewWindow();
    }

    @Test
    public void loadFileEventTest()
    {
        gui.loadFileEvent(event);
        verify(presenter).handleLoadFile();
    }
}
