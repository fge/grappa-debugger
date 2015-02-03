package com.github.fge.grappa.debugger.mainwindow;

import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.common.JavafxView;
import com.github.fge.grappa.debugger.csvtrace.CsvTracePresenter;
import com.github.fge.grappa.debugger.csvtrace.JavafxCsvTraceView;
import com.github.fge.grappa.debugger.javafx.AlertFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public final class JavafxMainWindowView
    extends JavafxView<MainWindowPresenter, MainWindowDisplay>
    implements MainWindowView
{
    private static final ExtensionFilter ZIP_FILES
        = new ExtensionFilter("ZIP files", "*.zip");

    private final Stage stage;
    private final BackgroundTaskRunner taskRunner;
    private final AlertFactory alertFactory;

    public JavafxMainWindowView(final Stage stage,
        final BackgroundTaskRunner taskRunner, final AlertFactory alertFactory)
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
            view = new JavafxCsvTraceView(taskRunner, this);
        } catch (IOException e) {
            showError("Tab creation error", "Unable to create tab", e);
            return;
        }
        presenter.setView(view);
        view.getDisplay().setPresenter(presenter);
        display.pane.setCenter(view.getNode());
    }
}
