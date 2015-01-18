package com.github.fge.grappa.debugger.basewindow;

import com.github.fge.grappa.debugger.basewindow.BaseWindowPresenter;
import com.github.fge.grappa.debugger.basewindow.BaseWindowUi;
import javafx.event.ActionEvent;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BaseWindowUiTest
{
    private BaseWindowPresenter presenter;
    private BaseWindowUi ui;
    private ActionEvent event;

    @BeforeClass
    public void init()
    {
        presenter = mock(BaseWindowPresenter.class);
        ui = new BaseWindowUi();
        ui.init(presenter);
        event = mock(ActionEvent.class);
    }

    @Test
    public void closeWindowEventTest()
    {
        ui.closeWindowEvent(event);
        verify(presenter).handleCloseWindow();
    }

    @Test
    public void newWindowEventTest()
    {
        ui.newWindowEvent(event);
        verify(presenter).handleNewWindow();
    }

    @Test
    public void loadFileEventTest()
    {
        ui.loadFileEvent(event);
        verify(presenter).handleLoadFile();
    }
}
