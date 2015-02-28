package com.github.fge.grappa.debugger.javafx.common;

import com.github.fge.grappa.internal.NonFinalForTesting;
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

    @SuppressWarnings("unchecked")
    @NonFinalForTesting
    public <T extends Node> T getNode()
    {
        return (T) node;
    }

    @NonFinalForTesting
    public D getDisplay()
    {
        return display;
    }
}
