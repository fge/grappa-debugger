package com.github.fge.grappa.debugger.javafx.common;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class AlertFactory
{
    public void unhandledError(final Throwable throwable)
    {
        showError("Bug!", "You have encountered a bug! Please report it"
            + " to the project page!", throwable);
    }

    public void showError(final String title, final String header,
        final Throwable throwable)
    {
        final Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);

        final Pane pane = getExceptionPane(throwable);

        alert.getDialogPane().setContent(pane);
        alert.show();
    }

    private Pane getExceptionPane(final Throwable ex)
    {
        final TextArea textArea = new TextArea();

        try (
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
        ) {
            ex.printStackTrace(pw);
            textArea.setText(sw.toString());
        } catch (IOException ignored) {
            // Can't happen
        }

        textArea.setEditable(false);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        final GridPane pane = new GridPane();

        final Label label = new Label("Stack trace:");

        pane.setMaxWidth(Double.MAX_VALUE);
        pane.add(label, 0, 0);
        pane.add(textArea, 0, 1);

        return pane;
    }
}
