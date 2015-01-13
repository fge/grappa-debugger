package com.github.parboiled1.grappa.debugger.mainwindow;

import com.github.parboiled1.grappa.debugger.mainwindow.parsetree.ParseNodeCellFactory;
import com.github.parboiled1.grappa.debugger.parser.MatchResult;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class DefaultMainWindowView
    implements MainWindowView
{
    private final MainWindowUi ui;

    public DefaultMainWindowView(final MainWindowUi ui)
    {
        this.ui = ui;
        ui.traceTree.setCellFactory(new ParseNodeCellFactory(ui));
    }

    @Override
    public void setInputText(final String inputText)
    {
        final TextFlow widget = ui.inputText;
        widget.getChildren().clear();
        widget.getChildren().add(new Text(inputText));
    }

    @Override
    public void closeWindow()
    {
        // Source: http://stackoverflow.com/a/13602324
        final Window window = ui.pane.getScene().getWindow();
        ((Stage) window).close();
    }

    @Override
    public String getInputText()
    {
        final TextFlow widget = ui.inputText;
        final StringBuilder sb = new StringBuilder();
        widget.getChildren().stream().map(node -> ((Text) node).getText())
            .forEach(sb::append);
        return sb.toString();
    }

    @Override
    public void setParseTree(final TreeItem<MatchResult> root)
    {
        final TreeView<MatchResult> tree = ui.traceTree;
        final TreeItem<MatchResult> oldRoot = tree.getRoot();
        if (oldRoot != null)
            oldRoot.getChildren().clear();
        tree.setRoot(root);
    }

    @Override
    public void setTraceDetail(final String text)
    {
        ui.traceDetail.setText(text);
    }
}
