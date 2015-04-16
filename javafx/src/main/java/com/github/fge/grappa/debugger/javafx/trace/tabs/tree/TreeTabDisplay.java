package com.github.fge.grappa.debugger.javafx.trace.tabs.tree;

import com.github.fge.grappa.debugger.javafx.common.JavafxDisplay;
import com.github.fge.grappa.debugger.model.tree.ParseTreeNode;
import com.github.fge.grappa.debugger.trace.tabs.tree.TreeTabPresenter;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.util.function.IntFunction;

public class TreeTabDisplay
    extends JavafxDisplay<TreeTabPresenter>
{
    /*
     * Tree
     */
    @FXML
    protected Label treeInfo;

    @FXML
    protected TreeView<ParseTreeNode> parseTree;

    /*
     * Node detail
     */
    @FXML
    protected Label nodeDepth;

    @FXML
    protected Label nodeRuleName;

    @FXML
    protected Label nodeMatcherType;

    @FXML
    protected Label nodeMatcherClass;

    @FXML
    protected Label nodeStatus;

    @FXML
    protected Label nodeStartPos;

    @FXML
    protected Label nodeEndPos;

    @FXML
    protected Label nodeTime;

    /*
     * Text
     */
    @FXML
    protected Label textInfo;

    @FXML
    protected CodeArea inputText;

    protected ParseTreeItem currentItem;

    @Override
    public void init()
    {
        parseTree.setCellFactory(param -> new ParseTreeNodeCell(this));
        final IntFunction<Node> intFunction = LineNumberFactory.get(inputText);
        inputText.setParagraphGraphicFactory(intFunction);
    }

    public void parseTreeNodeShowEvent(final ParseTreeNode node)
    {
        presenter.handleParseTreeNodeShow(node);
    }

    public void needChildrenEvent(final ParseTreeItem parseTreeItem)
    {
        currentItem = parseTreeItem;
        presenter.handleNeedChildren(currentItem.getValue());
    }
}
