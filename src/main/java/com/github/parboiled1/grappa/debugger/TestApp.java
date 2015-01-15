package com.github.parboiled1.grappa.debugger;

import javafx.application.Application;
import javafx.stage.Stage;

public final class TestApp
    extends Application
{
    private final BaseWindowFactory factory = new DefaultBaseWindowFactory();

    @Override
    public void start(final Stage primaryStage)
        throws Exception
    {
        factory.createWindow(primaryStage);
    }

    public static void main(final String... args)
    {
        launch(args);
    }
}
