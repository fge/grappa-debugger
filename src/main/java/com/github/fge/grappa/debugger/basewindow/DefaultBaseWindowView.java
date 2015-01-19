package com.github.fge.grappa.debugger.basewindow;

import com.github.fge.grappa.debugger.alert.AlertFactory;
import com.github.fge.grappa.debugger.tracetab.DefaultTraceTabView;
import com.github.fge.grappa.debugger.tracetab.TraceTabPresenter;
import com.github.fge.grappa.debugger.tracetab.TraceTabUi;
import com.github.fge.grappa.debugger.tracetab.TraceTabView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public final class DefaultBaseWindowView
    implements BaseWindowView
{
    private static final ExtensionFilter ZIP_FILES
        = new ExtensionFilter("ZIP files", "*.zip");
    private static final URL TRACE_TAB_FXML;

    static {
        TRACE_TAB_FXML = BaseWindowView.class.getResource("/traceTab.fxml");
        if (TRACE_TAB_FXML == null)
            throw new ExceptionInInitializerError("failed to load tab fxml");
    }

    private final Stage stage;
    private final AlertFactory alertFactory;
    private final BaseWindowUi ui;

    public DefaultBaseWindowView(final Stage stage,
        final AlertFactory alertFactory, final BaseWindowUi ui)
    {
        this.stage = stage;
        this.alertFactory = alertFactory;
        this.ui = ui;
    }

    @Override
    public void injectTab(final TraceTabPresenter presenter)
    {
        final FXMLLoader loader = new FXMLLoader(TRACE_TAB_FXML);
        final Node pane;
        try {
            pane = loader.load();
        } catch (IOException oops) {
            alertFactory.showError("Tab creation error",
                "Unable to create tab", oops);
            return;
        }
        ui.pane.setCenter(pane);
        final TraceTabUi tabUi = loader.getController();
        final TraceTabView view = new DefaultTraceTabView(tabUi);
        presenter.setView(view);
        tabUi.init(presenter);
    }

    @Override
    public File chooseFile()
    {
        final FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(ZIP_FILES);
        return chooser.showOpenDialog(stage);
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
}
