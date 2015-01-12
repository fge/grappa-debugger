package com.github.parboiled1.grappa.debugger.mainwindow;

import com.github.parboiled1.grappa.debugger.alert.AlertFactory;
import com.github.parboiled1.grappa.debugger.parser.MatchResult;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
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

    public void handleLoadFile()
    {
        /*
         * TODO: cannot reliably read text content...
         *
         * The FileChooser can only select files; you cannot tell it, for
         * instance, that you want to read them using a given encoding, which
         * sucks.
         */
        final File file = getInputFile();

        if (file == null)
            return;

        final String content;

        try {
            content = getContents(file);
        } catch (IOException e) {
            alertFactory.showError("Problem!", "Unable to load file contents",
                e);
            return;
        }

        view.setInputText(content);
    }

    // Visible for testing
    File getInputFile()
    {
        final FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a file to load");
        chooser.setInitialDirectory(Paths.get("").toAbsolutePath().toFile());

        return chooser.showOpenDialog(stage);
    }

    // Visible for testing
    String getContents(final File file)
        throws IOException
    {
        final Path path = file.toPath();

        final char[] buf = new char[2048];
        final StringBuilder sb = new StringBuilder();

        try (
            // TODO: ugly! Depending on the default encoding...
            final BufferedReader reader = Files.newBufferedReader(path);
        ) {
            int nrChars;
            while ((nrChars = reader.read(buf)) != -1)
                sb.append(buf, 0, nrChars);
        }

        return sb.toString();
    }

    public void handleParse()
    {
        final String inputText = view.getInputText();
        final TreeItem<MatchResult> root = model.runTrace(inputText);
        view.setParseTree(root);
    }

    public void handleCloseWindow()
    {
        view.closeWindow();
    }

    public void handleMatchResultShow(final TreeItem<MatchResult> item)
    {
        // TODO: unlike what IDEA says, this CAN happen that item is null
        //noinspection ConstantConditions
        if (item == null)
            return;
        view.setTraceDetail(item.getValue().toString());
    }
}
