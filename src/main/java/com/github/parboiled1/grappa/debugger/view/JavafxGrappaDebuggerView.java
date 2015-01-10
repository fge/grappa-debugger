package com.github.parboiled1.grappa.debugger.view;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class JavafxGrappaDebuggerView
    implements GrappaDebuggerView
{
    private final Stage stage;
    private final StackPane root;
    private final Button button;

    public JavafxGrappaDebuggerView(final Stage stage)
    {
        this.stage = stage;

        root = new StackPane();

        final ObservableList<Node> children = root.getChildren();

        button = new Button("clickme");
        children.add(button);

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
    public void setButtonAction(final EventHandler<ActionEvent> handler)
    {
        Objects.requireNonNull(handler);
        button.setOnAction(handler);
    }

    @Override
    public void close()
    {
        stage.close();
    }
}
