package com.github.fge.grappa.debugger.javafx.csvtrace.tabs.tree;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.common.TracingCharEscaper;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabView;
import com.github.fge.grappa.debugger.javafx.common.JavafxUtils;
import com.github.fge.grappa.debugger.javafx.common.JavafxView;
import com.github.fge.grappa.debugger.javafx.custom.ParseTreeItem;
import com.github.fge.grappa.debugger.model.common.RuleInfo;
import com.github.fge.grappa.debugger.model.tabs.tree.InputText;
import com.github.fge.grappa.debugger.model.tabs.tree.ParseTree;
import com.github.fge.grappa.debugger.model.tabs.tree.ParseTreeNode;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.google.common.escape.CharEscaper;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.CodeArea;
import org.parboiled.support.Position;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
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
        super("/tabs/treeTab.fxml");
        this.taskRunner = Objects.requireNonNull(taskRunner);
    }

    @SuppressWarnings("AutoBoxing")
    @Override
    public void loadInputText(final InputText inputText)
    {
        buffer = inputText.getInputBuffer();
        display.textInfo.setText(String.format("Input text: %d lines, %d "
            + "characters, %d code points", inputText.getNrLines(),
            inputText.getNrChars(), inputText.getNrCodePoints()));

        taskRunner.compute(() -> buffer.extract(0, buffer.length()),
            display.inputText::appendText);
    }

    @Override
    public void highlightSuccess(final int start, final int end)
    {
        final int length = buffer.length();
        final int realStart = Math.min(start, length);
        final int realEnd = Math.min(end, length);

        taskRunner.compute(
            () -> getSuccessfulMatchFragments(length, realStart, realEnd),
            fragments -> {
                appendSuccessfulMatchFragments(fragments);
                setScroll(realStart);
            }
        );
    }

    @Override
    public void highlightFailure(final int end)
    {
        final int length = buffer.length();
        final int realEnd = Math.min(end, length);

        taskRunner.compute(() -> getFailedMatchFragments(length, realEnd),
            fragments -> {
                appendFailedMatchFragments(fragments);
                setScroll(realEnd);
            });
    }

    @SuppressWarnings("AutoBoxing")
    @Override
    public void loadParseTree(@Nullable final ParseTree parseTree)
    {
        if (parseTree == null) {
            display.treeInfo.setText("Load error");
            return;
        }

        final ParseTreeNode rootNode = parseTree.getRootNode();
        display.parseTree.setRoot(new ParseTreeItem(display, rootNode));
        display.treeInfo.setText(String.format("Tree: %d nodes; depth %d",
            parseTree.getNrInvocations(), parseTree.getTreeDepth()));
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

    private MatchFragments getFailedMatchFragments(final int length,
        final int realEnd)
    {
        final String beforeMatch = buffer.extract(0, realEnd);
        final String match = "\u2612";
        final String afterMatch = buffer.extract(realEnd, length);

        return new MatchFragments(beforeMatch, match, afterMatch);
    }

    private MatchFragments getSuccessfulMatchFragments(final int length,
        final int realStart, final int realEnd)
    {
        final String beforeMatch = buffer.extract(0, realStart);
        final String afterMatch = buffer.extract(realEnd, length);

        final String match = realStart == realEnd ? "\u2205"
            : '\u21fe' + ESCAPER.escape(buffer.extract(realStart, realEnd))
                + '\u21fd';

        return new MatchFragments(beforeMatch, match, afterMatch);
    }

    private void setScroll(final int index)
    {
        final Position position = buffer.getPosition(index);
        double line = position.getLine();
        final double nrLines = buffer.getLineCount();
        if (line != nrLines)
            line--;
        //display.inputTextScroll.setVvalue(line / nrLines);
    }

    private void appendSuccessfulMatchFragments(final MatchFragments fragments)
    {
        final CodeArea inputText = display.inputText;

        final StringBuilder sb = new StringBuilder(fragments.beforeMatch)
            .append(fragments.match).append(fragments.afterMatch);

        inputText.clear();
        inputText.appendText(sb.toString());

        int start;
        String fragment;
        int end;

        start = 0;
        fragment = fragments.beforeMatch;
        end = start + fragment.length();
        inputText.setStyle(start, end, JavafxUtils.STYLE_BEFOREMATCH);

        start = end;
        fragment = fragments.match;
        end = start + fragment.length();
        inputText.setStyle(start, end, JavafxUtils.STYLE_MATCHSUCCESS);

        start = end;
        fragment = fragments.afterMatch;
        end = start + fragment.length();
        inputText.setStyle(start, end, JavafxUtils.STYLE_AFTERMATCH);

        final int pos = fragments.beforeMatch.length();
        inputText.selectRange(pos, pos);
    }

    private void appendFailedMatchFragments(final MatchFragments fragments)
    {
        final CodeArea inputText = display.inputText;

        final StringBuilder sb = new StringBuilder(fragments.beforeMatch)
            .append(fragments.match).append(fragments.afterMatch);

        inputText.clear();
        inputText.appendText(sb.toString());

        int start;
        String fragment;
        int end;

        start = 0;
        fragment = fragments.beforeMatch;
        end = start + fragment.length();
        inputText.setStyle(start, end, JavafxUtils.STYLE_BEFOREMATCH);

        start = end;
        fragment = fragments.match;
        end = start + fragment.length();
        inputText.setStyle(start, end, JavafxUtils.STYLE_MATCHFAILURE);

        start = end;
        fragment = fragments.afterMatch;
        end = start + fragment.length();
        inputText.setStyle(start, end, JavafxUtils.STYLE_AFTERMATCH);

        final int pos = fragments.beforeMatch.length();
        inputText.selectRange(pos, pos);
    }

    @Immutable
    private static final class MatchFragments
    {
        private final String beforeMatch;
        private final String match;
        private final String afterMatch;

        private MatchFragments(final String beforeMatch, final String match,
            final String afterMatch)
        {
            this.beforeMatch = beforeMatch;
            this.match = match;
            this.afterMatch = afterMatch;
        }
    }
}
