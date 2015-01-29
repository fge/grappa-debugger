package com.github.fge.grappa.debugger.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.common.JavafxView;
import com.github.fge.grappa.debugger.javafx.JavafxStatsTabFactory;
import com.github.fge.grappa.debugger.javafx.Utils;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.github.fge.grappa.debugger.stats.StatsType;
import com.github.fge.grappa.debugger.stats.TracingCharEscaper;
import com.github.fge.grappa.debugger.tracetab.stat.classdetails.ClassDetailsStatsDisplay;
import com.github.fge.grappa.debugger.tracetab.stat.classdetails.ClassDetailsStatsPresenter;
import com.github.fge.grappa.debugger.tracetab.stat.classdetails.ClassDetailsStatsView;
import com.github.fge.grappa.debugger.tracetab.stat.classdetails.JavafxClassDetailsStatsView;
import com.github.fge.grappa.debugger.tracetab.stat.global.GlobalStatsDisplay;
import com.github.fge.grappa.debugger.tracetab.stat.global.GlobalStatsPresenter;
import com.github.fge.grappa.debugger.tracetab.stat.global.GlobalStatsView;
import com.github.fge.grappa.debugger.tracetab.stat.global.JavafxGlobalStatsView;
import com.github.fge.grappa.trace.ParseRunInfo;
import com.github.fge.grappa.trace.TraceEvent;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.escape.CharEscaper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.parboiled.support.Position;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@ParametersAreNonnullByDefault
public final class JavafxTraceTabView
    extends JavafxView<TraceTabPresenter, TraceTabDisplay>
    implements TraceTabView
{
    private static final ThreadFactory THREAD_FACTORY
        = new ThreadFactoryBuilder().setDaemon(true)
        .setNameFormat("tree-expand-%d").build();
    private static final CharEscaper ESCAPER = new TracingCharEscaper();

    private final ExecutorService executor
        = Executors.newSingleThreadExecutor(THREAD_FACTORY);

    private final MainWindowView parentView;

    private InputBuffer buffer;

    @VisibleForTesting
    JavafxStatsTabFactory tabFactory = new JavafxStatsTabFactory();

    public JavafxTraceTabView(final MainWindowView parentView)
        throws IOException
    {
        super("/traceTab.fxml");
        this.parentView = parentView;
    }

    @Override
    public void setInputText(final InputBuffer inputBuffer)
    {
        buffer = Objects.requireNonNull(inputBuffer);
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
        final String message  = String.format(
            "Input: %d lines, %d characters," + " %d code points", nrLines,
            nrChars, nrCodePoints);
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

    @Override
    public void expandParseTree()
    {
        final TreeItem<ParseNode> root = display.parseTree.getRoot();
        final ParseNode node = root.getValue();
        final Button button = display.treeExpand;

        button.setDisable(true);
        button.setText("Please wait...");

        executor.submit(() -> {
            final TreeItem<ParseNode> newRoot = buildTree(node, true);
            Platform.runLater(() -> {
                display.parseTree.setRoot(newRoot);
                button.setText("Expand tree");
                button.setDisable(false);
            });
        });
    }

    @SuppressWarnings("AutoBoxing")
    @Override
    public void showParseNode(final ParseNode node)
    {
        final boolean success = node.isSuccess();
        Position position;

        display.parseNodeLevel.setText(String.valueOf(node.getLevel()));
        display.parseNodeRuleName.setText(node.getRuleName());
        display.parseNodeMatcherType.setText(node.getMatcherType().toString());
        display.parseNodeMatcherClass.setText(node.getMatcherClass());

        if (success) {
            display.parseNodeStatus.setText("SUCCESS");
            display.parseNodeStatus.setTextFill(Color.GREEN);
        } else {
            display.parseNodeStatus.setText("FAILURE");
            display.parseNodeStatus.setTextFill(Color.RED);
        }

        position = buffer.getPosition(node.getStart());
        display.parseNodeStart.setText(String.format("line %d, column %d",
            position.getLine(), position.getColumn()));
        position = buffer.getPosition(node.getEnd());
        display.parseNodeEnd.setText(String.format("line %d, column %d",
            position.getLine(), position.getColumn()));

        display.parseNodeTime.setText(Utils.nanosToString(node.getNanos()));
    }

    @Override
    public void highlightFailedMatch(final int failedIndex)
    {
        final int length = buffer.length();
        final List<Text> list = new ArrayList<>(3);

        String fragment;
        Text text;

        fragment = buffer.extract(0, failedIndex);
        if (!fragment.isEmpty()) {
            text = new Text(fragment);
            text.setFill(Color.GRAY);
            list.add(text);
        }

        text = new Text("\u2612");
        text.setFill(Color.RED);
        text.setUnderline(true);
        list.add(text);

        fragment = buffer.extract(failedIndex, length);
        if (!fragment.isEmpty())
            list.add(new Text(fragment));

        display.inputText.getChildren().setAll(list);
        setScroll(failedIndex);
    }

    @Override
    public void highlightSuccessfulMatch(final int startIndex,
        final int endIndex)
    {
        final int length = buffer.length();
        final List<Text> list = new ArrayList<>(3);

        String fragment;
        Text text;

        // Before match
        fragment = buffer.extract(0, startIndex);
        if (!fragment.isEmpty()) {
            text = new Text(fragment);
            text.setFill(Color.GRAY);
            list.add(text);
        }

        // Match
        fragment = buffer.extract(startIndex, endIndex);
        text = new Text(fragment.isEmpty() ? "\u2205"
            : '\u21fe' + ESCAPER.escape(fragment) + '\u21fd');
        text.setFill(Color.GREEN);
        text.setUnderline(true);
        list.add(text);

        // After match
        fragment = buffer.extract(endIndex, length);
        if (!fragment.isEmpty()) {
            text = new Text(fragment);
            list.add(text);
        }

        display.inputText.getChildren().setAll(list);
        setScroll(startIndex);
    }

    @Override
    public void loadGlobalStats(final GlobalStatsPresenter presenter)
    {
        final FXMLLoader loader = tabFactory.getLoader(StatsType.GLOBAL);
        final Node node;
        try {
            node = loader.load();
        } catch (IOException e) {
            parentView.showError("Stats loading error",
                "Unable to load statistics", e);
            return;
        }
        final GlobalStatsDisplay statsDisplay = loader.getController();
        final GlobalStatsView view = new JavafxGlobalStatsView(statsDisplay);
        statsDisplay.setPresenter(presenter);
        presenter.setView(view);
        presenter.loadStats();
        display.globalStatsTab.setContent(node);
    }

    @Override
    public void loadClassDetailsStats(
        final ClassDetailsStatsPresenter presenter)
    {
        final FXMLLoader loader = tabFactory.getLoader(StatsType.CLASS_DETAILS);
        final Node node;
        try {
            node = loader.load();
        } catch (IOException e) {
            parentView.showError("Stats loading error",
                "Unable to load statistics", e);
            return;
        }
        final ClassDetailsStatsDisplay statsDisplay = loader.getController();
        final ClassDetailsStatsView view
            = new JavafxClassDetailsStatsView(statsDisplay);
        statsDisplay.setPresenter(presenter);
        presenter.setView(view);
        presenter.loadStats();
        display.classStatsTab.setContent(node);
    }

    private void setScroll(final int index)
    {
        final Position position = buffer.getPosition(index);
        double line = position.getLine();
        final double nrLines = buffer.getLineCount();
        if (line != nrLines)
            line--;
        display.inputTextScroll.setVvalue(line / nrLines);
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
