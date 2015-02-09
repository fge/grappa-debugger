package com.github.fge.grappa.debugger.mainwindow;

import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.javafx.AlertFactory;
import javafx.stage.Stage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class JavafxMainWindowViewTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
        mock(ExecutorService.class), Runnable::run
    );

    private Stage stage;
    private AlertFactory alertFactory;
    private JavafxMainWindowView view;

    @BeforeMethod
    public void init()
        throws IOException
    {
        stage = mock(Stage.class);
        alertFactory = mock(AlertFactory.class);
        view = spy(new JavafxMainWindowView(stage, taskRunner, alertFactory));
    }

    @Test
    public void setWindowTitleTest()
    {
        final String title = "meh";

        view.setWindowTitle(title);

        final String actual = stage.getTitle();

        assertThat(actual).isEqualTo(title);
    }
}
