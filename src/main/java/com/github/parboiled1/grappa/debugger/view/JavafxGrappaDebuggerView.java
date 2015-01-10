package com.github.parboiled1.grappa.debugger.view;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public final class JavafxGrappaDebuggerView
    implements GrappaDebuggerView
{
    private final Stage stage;
    private final StackPane root;

    public JavafxGrappaDebuggerView(final Stage stage)
    {
        this.stage = stage;
        root = new StackPane();

        final Scene scene = new Scene(root, 300, 200);

        stage.setTitle("Grappa debugger");
        stage.setScene(scene);
    }


    @Override
    public void display()
    {
        stage.show();
    }

    @Override
    public void close()
    {
        stage.close();
    }
}
