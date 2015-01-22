package com.github.fge.grappa.debugger.basewindow;

import javafx.event.ActionEvent;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BaseWindowDisplayTest
{
    private BaseWindowPresenter presenter;
    private BaseWindowDisplay display;
    private ActionEvent event;

    @BeforeClass
    public void init()
    {
        presenter = mock(BaseWindowPresenter.class);
        display = new BaseWindowDisplay();
        display.init(presenter);
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
