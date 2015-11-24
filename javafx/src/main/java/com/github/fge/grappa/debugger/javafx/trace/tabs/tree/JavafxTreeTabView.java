package com.github.fge.grappa.debugger.javafx.trace.tabs.tree;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.ParseInfo;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.common.TracingCharEscaper;
import com.github.fge.grappa.debugger.javafx.common.JavafxUtils;
import com.github.fge.grappa.debugger.javafx.common.JavafxView;
import com.github.fge.grappa.debugger.javafx.common.highlight.MatchHighlightText;
import com.github.fge.grappa.debugger.model.tree.ParseTreeNode;
import com.github.fge.grappa.debugger.model.tree.RuleInfo;
import com.github.fge.grappa.debugger.trace.tabs.tree.TreeTabPresenter;
import com.github.fge.grappa.debugger.trace.tabs.tree.TreeTabView;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.github.fge.grappa.support.Position;
import com.google.common.escape.CharEscaper;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.CodeArea;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@NonFinalForTesting
@ParametersAreNonnullByDefault
public class JavafxTreeTabView
    extends JavafxView<TreeTabPresenter, TreeTabDisplay>
    implements TreeTabView
{
    @SuppressWarnings("AutoBoxing")
    private static final Function<Position, String> POS_TO_STRING = pos ->
        String.format("line %d, column %d", pos.getLine(), pos.getColumn());
    private static final CharEscaper ESCAPER = new TracingCharEscaper();

    private final GuiTaskRunner taskRunner;

    private InputBuffer buffer;

    public JavafxTreeTabView(final GuiTaskRunner taskRunner)
        throws IOException
    {
        super("/javafx/tabs/tree.fxml");
        this.taskRunner = Objects.requireNonNull(taskRunner);
    }

    @SuppressWarnings("AutoBoxing")
    @Override
    public void displayInfo(final ParseInfo info)
    {
        display.treeInfo.setText(String.format("Tree: %d nodes; depth %d",
            info.getNrNodes(), info.getTreeDepth()));
        display.textInfo.setText(String.format("Input text: %d lines, %d "
            + "characters, %d code points", info.getNrLines(),
            info.getNrChars(), info.getNrCodePoints()));
    }

    @Override
    public void loadInputBuffer(final InputBuffer buffer)
    {
        this.buffer = buffer;

        taskRunner.compute(() -> buffer.extract(0, buffer.length()), text -> {
            display.inputText.appendText(text);
            display.inputText.moveTo(0);
        });
    }

    @Override
    public void highlightSuccess(final int start, final int end)
    {
        final int length = buffer.length();
        final int realStart = Math.min(start, length);
        final int realEnd = Math.min(end, length);
        final boolean emptyMatch = realStart == realEnd;

        taskRunner.compute(
            () -> emptyMatch
                ? MatchHighlightText.emptyMatch(buffer, realStart)
                : MatchHighlightText.nonemptyMatch(buffer, realStart, realEnd),
            this::processHighlight
        );
    }

    @Override
    public void highlightFailure(final int end)
    {
        final int length = buffer.length();
        final int realEnd = Math.min(end, length);

        taskRunner.compute(
            () -> MatchHighlightText.failedMatch(buffer, realEnd),
            this::processHighlight
        );
    }

    private void processHighlight(final MatchHighlightText text)
    {
        final CodeArea area = display.inputText;

        area.clear();
        area.appendText(text.fullText());
        area.setStyleSpans(0, text.getStyleSpans());
        area.moveTo(text.matchStartIndex());
    }

    @Override
    public void displayTree(final ParseTreeNode rootNode)
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
    }

    @Override
    public void waitForChildren()
    {
        display.currentItem.loadingProperty().setValue(true);
    }

    @Override
    public void setTreeChildren(final List<ParseTreeNode> children)
    {
        final List<ParseTreeItem> items = children.stream()
            .map(node -> new ParseTreeItem(display, node))
            .collect(Collectors.toList());
        display.currentItem.getChildren().setAll(items);
        display.currentItem.loadingProperty().setValue(false);
    }
}
