package com.github.fge.grappa.debugger.javafx.mainwindow;

import com.github.fge.grappa.debugger.JavafxViewTest;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTracePresenter;
import com.github.fge.grappa.debugger.javafx.common.AlertFactory;
import com.github.fge.grappa.debugger.javafx.csvtrace.CsvTraceDisplay;
import com.github.fge.grappa.debugger.javafx.csvtrace.JavafxCsvTraceView;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class JavafxMainWindowViewTest
    extends JavafxViewTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
        mock(ExecutorService.class), Runnable::run
    );

    private Stage stage;
    private AlertFactory alertFactory;
    private JavafxMainWindowView view;
    private MainWindowDisplay display;

    @BeforeMethod
    public void init()
    {
        stage = mock(Stage.class);
        alertFactory = mock(AlertFactory.class);
        view = javafxGet(() -> spy(new JavafxMainWindowView(stage, taskRunner,
            alertFactory)));
        display = view.getDisplay();
    }

    @Test
    public void setWindowTitleTest()
    {
        final String expected = "meh";

        view.setWindowTitle(expected);

        final String actual = stage.getTitle();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void showErrorTest()
    {
        final String title = "title";
        final String text = "text";
        final Throwable throwable = new Throwable();

        view.showError(title, text, throwable);

        verify(alertFactory).showError(same(title), same(text),
            same(throwable));
    }

    @Test
    public void setLabelTextTest()
    {
        final String expected = "meh";

        view.setLabelText(expected);

        assertThat(display.label.getText()).isEqualTo(expected);
    }

    @Test
    public void attachTraceSuccessTest()
        throws IOException
    {
        final CsvTracePresenter tracePresenter = mock(CsvTracePresenter.class);

        final JavafxCsvTraceView traceView
            = spy(new JavafxCsvTraceView(taskRunner, view));

        final CsvTraceDisplay traceDisplay = mock(CsvTraceDisplay.class);

        doReturn(traceDisplay).when(traceView).getDisplay();

        doReturn(traceView).when(view).loadTraceView();

        view.attachTrace(tracePresenter);

        final Node node = traceView.getNode();

        final InOrder inOrder
            = inOrder(traceDisplay, tracePresenter);

        inOrder.verify(tracePresenter).setView(same(traceView));
        inOrder.verify(traceDisplay).setPresenter(same(tracePresenter));

        assertThat(display.pane.getCenter()).isSameAs(node);
    }

    @Test(enabled = false)
    public void reportProgressTest()
    {
        // TODO
    }
}
