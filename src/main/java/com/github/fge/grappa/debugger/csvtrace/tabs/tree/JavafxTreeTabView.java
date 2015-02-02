package com.github.fge.grappa.debugger.csvtrace.tabs.tree;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.common.JavafxView;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.debugger.csvtrace.newmodel.RuleInfo;
import com.github.fge.grappa.debugger.javafx.JavafxUtils;
import com.github.fge.grappa.debugger.javafx.parsetree.ParseTreeItem;
import com.github.fge.grappa.debugger.stats.TracingCharEscaper;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.google.common.escape.CharEscaper;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.parboiled.support.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@NonFinalForTesting
public class JavafxTreeTabView
    extends JavafxView<TreeTabPresenter, TreeTabDisplay>
    implements TreeTabView
{
    @SuppressWarnings("AutoBoxing")
    private static final Function<Position, String> POS_TO_STRING = pos ->
        String.format("line %d, column %d", pos.getLine(), pos.getColumn());
    private static final CharEscaper ESCAPER = new TracingCharEscaper();

    private final BackgroundTaskRunner taskRunner;

    private InputBuffer buffer;

    public JavafxTreeTabView(final BackgroundTaskRunner taskRunner)
        throws IOException
    {
        super("/tabs/treeTab.fxml");
        this.taskRunner = Objects.requireNonNull(taskRunner);
    }

    @Override
    public void waitForText()
    {
    }

    @Override
    public void loadText(final InputBuffer inputBuffer)
    {
        buffer = inputBuffer;
        final ObservableList<Node> children = display.inputText.getChildren();
        taskRunner.compute(() -> buffer.extract(0, buffer.length()),
            text -> children.setAll(new Text(text)));
    }

    @Override
    public void highlightSuccess(final int start, final int end)
    {
        final int length = buffer.length();
        final int realStart = Math.min(start, length);
        final int realEnd = Math.min(end, length);
        //final List<Text> list = getSuccessfulMatchFragments(length, realStart,
        //    realEnd);

        final ObservableList<Node> nodes = display.inputText.getChildren();

        taskRunner.compute(
            () -> getSuccessfulMatchFragments(length, realStart, realEnd),
            fragments -> {
                nodes.setAll(fragments);
                setScroll(realStart);
            }
        );
    }

    @Override
    public void highlightFailure(final int end)
    {
        final int length = buffer.length();
        final int realEnd = Math.min(end, length);
        final ObservableList<Node> nodes = display.inputText.getChildren();

        taskRunner.compute(() -> getFailedMatchFragments(length, realEnd),
            fragments -> {
                nodes.setAll(fragments);
                setScroll(realEnd);
            });
    }


    @Override
    public void waitForTree()
    {
    }

    @Override
    public void loadTree(final ParseTreeNode rootNode)
    {
        display.parseTree.setRoot(new ParseTreeItem(display, rootNode));
    }

    @Override
    public void showParseTreeNode(final ParseTreeNode node)
    {
        final RuleInfo info = node.getRuleInfo();

        // Pure text, or nearly so
        display.nodeDepth.setText(Integer.toString(node.getLevel()));
        display.nodeRuleName.setText(info.getName());
        display.nodeMatcherType.setText(info.getType().name());
        display.nodeMatcherClass.setText(info.getClassName());

        // Time
        display.nodeTime.setText(JavafxUtils.nanosToString(node.getNanos()));

        // Status
        if (node.isSuccess()) {
            display.nodeStatus.setText("SUCCESS");
            display.nodeStatus.setTextFill(Color.GREEN);
        } else {
            display.nodeStatus.setText("FAILURE");
            display.nodeStatus.setTextFill(Color.RED);
        }

        // Positions
        final Position start = buffer.getPosition(node.getStartIndex());
        final Position end = buffer.getPosition(node.getEndIndex());

        display.nodeStartPos.setText(POS_TO_STRING.apply(start));
        display.nodeEndPos.setText(POS_TO_STRING.apply(end));

        setScroll(node.getStartIndex());
    }

    private List<Text> getFailedMatchFragments(final int length,
        final int realEnd)
    {
        final List<Text> list = new ArrayList<>(3);

        String fragment;
        Text text;

        fragment = buffer.extract(0, realEnd);
        if (!fragment.isEmpty()) {
            text = new Text(fragment);
            text.setFill(Color.GRAY);
            list.add(text);
        }

        text = new Text("\u2612");
        text.setFill(Color.RED);
        text.setUnderline(true);
        list.add(text);

        fragment = buffer.extract(realEnd, length);
        if (!fragment.isEmpty())
            list.add(new Text(fragment));
        return list;
    }

    private List<Text> getSuccessfulMatchFragments(final int length,
        final int realStart, final int realEnd)
    {
        final List<Text> list = new ArrayList<>(3);

        String fragment;
        Text text;

        // Before match
        fragment = buffer.extract(0, realStart);
        if (!fragment.isEmpty()) {
            text = new Text(fragment);
            text.setFill(Color.GRAY);
            list.add(text);
        }

        // Match
        fragment = buffer.extract(realStart, realEnd);
        text = new Text(fragment.isEmpty() ? "\u2205"
            : '\u21fe' + ESCAPER.escape(fragment) + '\u21fd');
        text.setFill(Color.GREEN);
        text.setUnderline(true);
        list.add(text);

        // After match
        fragment = buffer.extract(realEnd, length);
        if (!fragment.isEmpty()) {
            text = new Text(fragment);
            list.add(text);
        }
        return list;
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
}
