package com.github.fge.grappa.debugger.javafx.mainwindow;

import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTracePresenter;
import com.github.fge.grappa.debugger.javafx.common.AlertFactory;
import com.github.fge.grappa.debugger.javafx.common.JavafxView;
import com.github.fge.grappa.debugger.javafx.csvtrace.JavafxCsvTraceView;
import com.github.fge.grappa.debugger.mainwindow.MainWindowPresenter;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.google.common.annotations.VisibleForTesting;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@NonFinalForTesting
public class JavafxMainWindowView
    extends JavafxView<MainWindowPresenter, MainWindowDisplay>
    implements MainWindowView
{
    private static final ExtensionFilter ZIP_FILES
        = new ExtensionFilter("ZIP files", "*.zip");

    private final Stage stage;
    private final GuiTaskRunner taskRunner;
    private final AlertFactory alertFactory;

    public JavafxMainWindowView(final Stage stage,
        final GuiTaskRunner taskRunner, final AlertFactory alertFactory)
        throws IOException
    {
        super("/mainWindow.fxml");
        this.stage = stage;
        this.taskRunner = taskRunner;
        this.alertFactory = alertFactory;
    }

    @Override
    public Path chooseFile()
    {
        final FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(ZIP_FILES);
        final File file = chooser.showOpenDialog(stage);
        return file == null ? null : file.toPath();
    }

    @Override
    public void setWindowTitle(final String windowTitle)
    {
        stage.setTitle(windowTitle);
    }

    @Override
    public void showError(final String header, final String message,
        final Throwable throwable)
    {
        alertFactory.showError(header, message, throwable);
    }

    @Override
    public void setLabelText(final String text)
    {
        display.label.setText(text);
    }

    @Override
    public void attachTrace(final CsvTracePresenter presenter)
    {
        final JavafxCsvTraceView view;
        try {
            view = loadTraceView();
        } catch (IOException e) {
            showError("Tab creation error", "Unable to create tab", e);
            return;
        }
        presenter.setView(view);
        view.getDisplay().setPresenter(presenter);
        display.pane.setCenter(view.getNode());
    }

    @VisibleForTesting
    JavafxCsvTraceView loadTraceView()
        throws IOException
    {
        return new JavafxCsvTraceView(taskRunner, this);
    }
}
