package com.github.parboiled1.grappa.debugger.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface GrappaDebuggerView
{
    void display();

    void setButtonAction(EventHandler<ActionEvent> handler);

    void close();
}
