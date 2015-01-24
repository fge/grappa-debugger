package com.github.fge.grappa.debugger.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.statistics.ParseNode;
import com.github.fge.grappa.trace.ParseRunInfo;
import com.github.fge.grappa.trace.TraceEvent;
import javafx.collections.FXCollections;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.text.Text;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class JavafxTraceTabView
    implements TraceTabView
{
    private final TraceTabDisplay display;

    public JavafxTraceTabView(final TraceTabDisplay display)
    {
        this.display = display;
    }

    @Override
    public void setInputText(final InputBuffer inputBuffer)
    {
        Objects.requireNonNull(inputBuffer);
        final String inputText = inputBuffer.extract(0, inputBuffer.length());
        display.inputText.getChildren().setAll(new Text(inputText));
    }

    @SuppressWarnings("AutoBoxing")
    @Override
    public void setInfo(final ParseRunInfo info)
    {
        Objects.requireNonNull(info);
        final int nrLines = info.getNrLines();
        final int nrChars = info.getNrChars();
        final int nrCodePoints = info.getNrCodePoints();
        final String message  = String.format("Input: %d lines, %d characters,"
            + " %d code points", nrLines, nrChars, nrCodePoints);
        display.textInfo.setText(message);
    }

    @SuppressWarnings("AutoBoxing")
    @Override
    public void setEvents(final List<TraceEvent> events)
    {
        display.events.getItems().setAll(events);
        final Tab tab = display.eventsTab;
        final int size = events.size();
        final String newText = String.format("%s (%d)", tab.getText(), size);

        tab.setText(newText);
    }

    @Override
    public void setParseTree(final ParseNode rootNode)
    {
        display.parseTree.setRoot(buildTree(rootNode));
    }

    private TreeItem<ParseNode> buildTree(final ParseNode root)
    {
        return buildTree(root, false);
    }

    private TreeItem<ParseNode> buildTree(final ParseNode root,
        final boolean expanded)
    {
        final TreeItem<ParseNode> ret = new TreeItem<>(root);

        addChildren(ret, root, expanded);

        return ret;
    }

    private void addChildren(final TreeItem<ParseNode> item,
        final ParseNode parent, final boolean expanded)
    {
        TreeItem<ParseNode> childItem;
        final List<TreeItem<ParseNode>> childrenItems
            = FXCollections.observableArrayList();

        for (final ParseNode node: parent.getChildren()) {
            childItem = new TreeItem<>(node);
            addChildren(childItem, node, expanded);
            childrenItems.add(childItem);
        }

        item.getChildren().setAll(childrenItems);
        item.setExpanded(expanded);
    }
}
