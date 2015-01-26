package com.github.fge.grappa.debugger.mainwindow;

import com.github.fge.grappa.debugger.javafx.AlertFactory;
import com.github.fge.grappa.debugger.legacy.tracetab.JavafxLegacyTraceTabView;
import com.github.fge.grappa.debugger.legacy.tracetab.LegacyTraceTabDisplay;
import com.github.fge.grappa.debugger.legacy.tracetab.LegacyTraceTabPresenter;
import com.github.fge.grappa.debugger.legacy.tracetab.LegacyTraceTabView;
import com.github.fge.grappa.debugger.tracetab.JavafxTraceTabView;
import com.github.fge.grappa.debugger.tracetab.TraceTabDisplay;
import com.github.fge.grappa.debugger.tracetab.TraceTabPresenter;
import com.github.fge.grappa.debugger.tracetab.TraceTabView;
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
    private static final Class<MainWindowView> MYSELF = MainWindowView.class;
    private static final ExtensionFilter ZIP_FILES
        = new ExtensionFilter("ZIP files", "*.zip");
    private static final URL LEGACY_TAB_FXML;
    private static final URL TAB_FXML;

    static {
        LEGACY_TAB_FXML = MYSELF.getResource("/legacyTraceTab.fxml");
        if (LEGACY_TAB_FXML == null)
            throw new ExceptionInInitializerError("failed to load legacy tab "
                + "fxml");
        TAB_FXML = MYSELF.getResource("/traceTab.fxml");
        if (TAB_FXML == null)
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
    public void injectLegacyTab(final LegacyTraceTabPresenter presenter)
    {
        final FXMLLoader loader = new FXMLLoader(LEGACY_TAB_FXML);
        final Node node;
        try {
            node = loader.load();
        } catch (IOException oops) {
            showError("Tab creation error", "Unable to create tab", oops);
            return;
        }
        display.pane.setCenter(node);
        final LegacyTraceTabDisplay tabDisplay = loader.getController();
        final LegacyTraceTabView view =
            new JavafxLegacyTraceTabView(tabDisplay);
        presenter.setView(view);
        tabDisplay.init(presenter);
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
    public void injectTab(final TraceTabPresenter presenter)
    {
        final FXMLLoader loader = new FXMLLoader(TAB_FXML);
        final Node node;
        try {
            node = loader.load();
        } catch (IOException oops) {
            showError("Tab creation error", "Unable to create tab", oops);
            return;
        }
        final TraceTabDisplay tabDisplay = loader.getController();
        final TraceTabView view = new JavafxTraceTabView(this, tabDisplay);
        presenter.setView(view);
        tabDisplay.setPresenter(presenter);
        display.pane.setCenter(node);
    }
}
