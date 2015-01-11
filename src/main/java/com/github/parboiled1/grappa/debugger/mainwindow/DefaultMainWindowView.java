package com.github.parboiled1.grappa.debugger.mainwindow;

import javafx.stage.Stage;
import javafx.stage.Window;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class DefaultMainWindowView
    implements MainWindowView
{
    private final MainWindowUi ui;

    public DefaultMainWindowView(final MainWindowUi ui)
    {
        this.ui = ui;
    }

    @Override
    public void setInputText(final String inputText)
    {
        ui.inputText.setText(Objects.requireNonNull(inputText));
    }

    @Override
    public void closeWindow()
    {
        // Source: http://stackoverflow.com/a/13602324
        final Window window = ui.pane.getScene().getWindow();
        ((Stage) window).close();
    }

    @Override
    public void addTraceText(final String trace)
    {
        ui.traceText.appendText(trace);
    }

    @Override
    public String getInputText()
    {
        return ui.inputText.getText();
    }
}
