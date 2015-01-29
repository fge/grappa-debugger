package com.github.fge.grappa.debugger.common;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.URL;

@ParametersAreNonnullByDefault
public abstract class JavafxView<P, D extends JavafxDisplay<P>>
{
    protected final Node node;
    protected final D display;

    protected JavafxView(final String fxmlLocation)
        throws IOException
    {
        final URL url = JavafxView.class.getResource(fxmlLocation);
        if (url == null)
            throw new IOException(fxmlLocation + ": resource not found");
        final FXMLLoader loader = new FXMLLoader(url);
        node = loader.load();
        display = loader.getController();
    }

    public final Node getNode()
    {
        return node;
    }

    public final void attachPresenter(final P presenter)
    {
        display.setPresenter(presenter);
    }
}
