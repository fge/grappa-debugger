package com.github.fge.grappa.debugger.csvtrace.tabs;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.common.JavafxView;
import com.github.fge.grappa.debugger.javafx.JavafxUtils;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.google.common.annotations.VisibleForTesting;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.parboiled.support.Position;

import java.io.IOException;
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

    private final BackgroundTaskRunner taskRunner;

    private InputBuffer buffer;

    public JavafxTreeTabView(final BackgroundTaskRunner taskRunner)
        throws IOException
    {
        super("/tabs/treeTab.fxml");
        this.taskRunner = taskRunner;
    }

    // TODO: only for tests, which don't work :(
    JavafxTreeTabView(final BackgroundTaskRunner taskRunner,
        final Node node, final TreeTabDisplay display)
    {
        super(node, display);
        this.taskRunner = taskRunner;
    }


    @Override
    public void loadTree(final ParseNode rootNode)
    {
        taskRunner.compute(() -> buildTree(rootNode), value -> {
            display.parseTree.setRoot(value);
            display.treeToolbar.getItems().setAll(display.treeExpand);
            display.treeExpand.setDisable(false);
        });
    }

    @Override
    public void loadText(final InputBuffer buffer)
    {
        this.buffer = Objects.requireNonNull(buffer);

        final String text = buffer.extract(0, buffer.length());
        display.inputText.getChildren().setAll(new Text(text));
    }

    @Override
    public void showParseNode(final ParseNode node)
    {
        final int realStart = Math.min(buffer.length(), node.getStart());
        final Position start = buffer.getPosition(realStart);
        final int realEnd = Math.min(buffer.length(), node.getEnd());
        final Position end = buffer.getPosition(realEnd);

        // Pure text, or nearly so
        display.nodeDepth.setText(Integer.toString(node.getLevel()));
        display.nodeRuleName.setText(node.getRuleName());
        display.nodeMatcherType.setText(node.getMatcherType().name());
        display.nodeMatcherClass.setText(node.getMatcherClass());

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
        display.nodeStartPos.setText(POS_TO_STRING.apply(start));
        display.nodeEndPos.setText(POS_TO_STRING.apply(end));
    }

    @VisibleForTesting
    TreeItem<ParseNode> buildTree(final ParseNode root)
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
