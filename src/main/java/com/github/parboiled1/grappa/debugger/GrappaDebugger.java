package com.github.parboiled1.grappa.debugger;

import com.github.parboiled1.grappa.debugger.presenter.GrappaDebuggerPresenter;
import com.github.parboiled1.grappa.debugger.view.GrappaDebuggerView;
import com.github.parboiled1.grappa.debugger.view.JavafxGrappaDebuggerView;
import javafx.application.Application;
import javafx.stage.Stage;

public final class GrappaDebugger
    extends Application
{
    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set. The primary stage will be embedded in
     * the browser if the application was launched as an applet.
     * Applications may create other stages, if needed, but they will not be
     * primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(final Stage primaryStage)
    {
        final GrappaDebuggerView view
            = new JavafxGrappaDebuggerView(primaryStage);
        final GrappaDebuggerPresenter presenter
            = new GrappaDebuggerPresenter(view);

        presenter.display();
    }

    public static void main(final String... args)
    {
        launch(args);
    }
}
