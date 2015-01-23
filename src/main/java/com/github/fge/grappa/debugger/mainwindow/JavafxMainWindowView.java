package com.github.fge.grappa.debugger.mainwindow;

import com.github.fge.grappa.debugger.alert.AlertFactory;
import com.github.fge.grappa.debugger.legacy.tracetab.JavafxLegacyTraceTabView;
import com.github.fge.grappa.debugger.legacy.tracetab.LegacyTraceTabDisplay;
import com.github.fge.grappa.debugger.legacy.tracetab.LegacyTraceTabPresenter;
import com.github.fge.grappa.debugger.legacy.tracetab.LegacyTraceTabView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public final class JavafxMainWindowView
    implements MainWindowView
{
    private static final Class<MainWindowView> MYSELF
        = MainWindowView.class;
    private static final ExtensionFilter ZIP_FILES
        = new ExtensionFilter("ZIP files", "*.zip");
    private static final URL TRACE_TAB_FXML;

    static {
        TRACE_TAB_FXML = MYSELF.getResource("/legacyTraceTab.fxml");
        if (TRACE_TAB_FXML == null)
            throw new ExceptionInInitializerError("failed to load tab fxml");
    }

    private final Stage stage;
    private final AlertFactory alertFactory;
    private final MainWindowDisplay display;

    public JavafxMainWindowView(final Stage stage,
        final AlertFactory alertFactory, final MainWindowDisplay display)
    {
        this.stage = stage;
        this.alertFactory = alertFactory;
        this.display = display;
    }

    @Override
    public void injectTab(final LegacyTraceTabPresenter presenter)
    {
        final FXMLLoader loader = new FXMLLoader(TRACE_TAB_FXML);
        final Node pane;
        try {
            pane = loader.load();
        } catch (IOException oops) {
            alertFactory.showError("Tab creation error", "Unable to create tab",
                oops);
            return;
        }
        display.pane.setCenter(pane);
        final LegacyTraceTabDisplay tabUi = loader.getController();
        final LegacyTraceTabView view = new JavafxLegacyTraceTabView(tabUi);
        presenter.setView(view);
        tabUi.init(presenter);
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
}
