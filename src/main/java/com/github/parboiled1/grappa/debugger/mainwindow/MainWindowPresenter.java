package com.github.parboiled1.grappa.debugger.mainwindow;

import com.github.parboiled1.grappa.debugger.alert.AlertFactory;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Non final for testing
@ParametersAreNonnullByDefault
public class MainWindowPresenter
{
    private final Stage stage;
    private final MainWindowView view;
    private final MainWindowModel model;
    private final AlertFactory alertFactory;

    public MainWindowPresenter(final MainWindowPresenterBuilder builder)
    {
        stage = builder.stage;
        view = builder.view;
        model = builder.model;
        alertFactory = builder.alertFactory;
    }

    public void loadInput()
    {
        view.setInputText("hello");
    }

    public void closeWindow()
    {
        view.closeWindow();
    }

    public void addTrace(final String trace)
    {
        view.addTrace(trace);
    }

    public void runTrace()
    {
        final String text = view.getInputText();
        model.trace(this, text);
    }

    public void loadFile()
    {
        final FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a file to load");
        chooser.setInitialDirectory(Paths.get("").toAbsolutePath().toFile());

        final File file = chooser.showOpenDialog(stage);

        if (file == null)
            return;

        final Path path = file.toPath();

        final char[] buf = new char[2048];
        final StringBuilder sb = new StringBuilder();

        try (
            final BufferedReader reader = Files.newBufferedReader(path,
                StandardCharsets.UTF_8);
        ) {
            int nrChars;
            while ((nrChars = reader.read(buf)) != -1)
                sb.append(buf, 0, nrChars);
            view.setInputText(sb.toString());
            throw new IOException("pwet");
        } catch (IOException e) {
            final Alert alert = alertFactory.newError("Problem!",
                "Unable to load file contents", e);
            alert.showAndWait();
        }
    }
}
