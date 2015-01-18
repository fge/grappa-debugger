package com.github.fge.grappa.debugger.basewindow;

import com.github.fge.grappa.debugger.tracetab.TraceTabPresenter;
import com.github.fge.grappa.debugger.tracetab.TraceTabUi;
import com.github.fge.grappa.debugger.tracetab.TraceTabView;
import com.github.fge.grappa.debugger.tracetab.DefaultTraceTabView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public final class DefaultBaseWindowView
    implements BaseWindowView
{
    private static final ExtensionFilter ZIP_FILES
        = new ExtensionFilter("ZIP files", "*.zip");
    private final BaseWindowUi ui;
    private final URL traceTabFxml;

    public DefaultBaseWindowView(final BaseWindowUi ui)
    {
        this.ui = ui;
        traceTabFxml = BaseWindowPresenter.class.getResource("/traceTab.fxml");
        if (traceTabFxml == null)
            throw new RuntimeException("cannot load tab fxml");
    }

    @Override
    public void injectTab(final TraceTabPresenter presenter)
    {
        final FXMLLoader loader = new FXMLLoader(traceTabFxml);
        final Node pane;
        try {
            pane = loader.load();
        } catch (IOException oops) {
            throw new RuntimeException("cannot create tab", oops);
        }
        ui.pane.setCenter(pane);
        final TraceTabUi tabUi = loader.getController();
        final TraceTabView view = new DefaultTraceTabView(tabUi);
        presenter.setView(view);
        tabUi.init(presenter);
    }

    @Override
    public File chooseFile(final Object object)
    {
        final FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(ZIP_FILES);
        return chooser.showOpenDialog((Window) object);
    }
}
