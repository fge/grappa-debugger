package com.github.fge.grappa.debugger.javafx.parsetree;

import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabDisplay;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public final class ParseTreeNodeCell
    extends TreeCell<ParseTreeNode>
{
    private final Text text = new Text();
    private final ProgressBar bar = new ProgressBar();
    final HBox hBox = new HBox();

    public ParseTreeNodeCell(final TreeTabDisplay display)
    {
        setEditable(false);
        selectedProperty().addListener(new ChangeListener<Boolean>()
        {
            @SuppressWarnings("AutoUnboxing")
            @Override
            public void changed(
                final ObservableValue<? extends Boolean> observable,
                final Boolean oldValue, final Boolean newValue)
            {
                if (!newValue)
                    return;
                final ParseTreeNode node = getItem();
                if (node != null)
                    display.parseTreeNodeShowEvent(node);
            }
        });

        final ChangeListener<TreeItem<ParseTreeNode>> listener
            = new ChangeListener<TreeItem<ParseTreeNode>>()
        {
            @Override
            public void changed(
                final ObservableValue<? extends TreeItem<ParseTreeNode>> unused,
                final TreeItem<ParseTreeNode> oldValue,
                final TreeItem<ParseTreeNode> newValue)
            {
                // FIXME: this cast is needed, unfortunately :(
                final ParseTreeItem treeItem = (ParseTreeItem) newValue;

                setText(null);
                
                if (treeItem == null) {
                    setGraphic(null);
                    return;
                }

                final ParseTreeNode item = treeItem.getValue();

                final String msg = String.format("%s (%s)",
                    item.getRuleInfo().getName(),
                    item.isSuccess() ? "SUCCESS" : "FAILURE");
                text.setText(msg);

                final ObservableList<Node> children = hBox.getChildren();
                children.clear();
                children.add(text);

                final boolean loadInProgress
                    = treeItem.waitingChildrenProperty().get();
                if (loadInProgress)
                    children.add(bar);
                setGraphic(hBox);
            }
        };

        treeItemProperty().addListener(listener);
    }

    /**
     * The updateItem method should not be called by developers, but it is the
     * best method for developers to override to allow for them to customise the
     * visuals of the cell. To clarify, developers should never call this method
     * in their code (they should leave it up to the UI control, such as the
     * {@link ListView} control) to call this method. However,
     * the purpose of having the updateItem method is so that developers, when
     * specifying custom cell factories (again, like the ListView
     * {@link ListView#cellFactoryProperty() cell factory}),
     * the updateItem method can be overridden to allow for complete
     * customisation
     * of the cell.
     * <p>It is <strong>very important</strong> that subclasses
     * of Cell override the updateItem method properly, as failure to do so will
     * lead to issues such as blank cells or cells with unexpected content
     * appearing within them. Here is an example of how to properly override the
     * updateItem method:
     * <pre>
     * protected void updateItem(T item, boolean empty) {
     *     super.updateItem(item, empty);
     *     if (empty || item == null) {
     *         setText(null);
     *         setGraphic(null);
     *     } else {
     *         setText(item.toString());
     *     }
     * }
     * </pre>
     * <p>Note in this code sample two important points:
     * <ol>
     * <li>We call the super.updateItem(T, boolean) method. If this is not
     * done, the item and empty properties are not correctly set, and you are
     * likely to end up with graphical issues.</li>
     * <li>We test for the <code>empty</code> condition, and if true, we
     * set the text and graphic properties to null. If we do not do this,
     * it is almost guaranteed that end users will see graphical artifacts
     * in cells unexpectedly.</li>
     * </ol>
     *
     * @param item The new item for the cell.
     * @param empty whether or not this cell represents data from the list.
     * If it
     * is empty, then it does not represent any domain data, but is a cell
     * being used to render an "empty" row.
     */
    @Override
    protected void updateItem(final ParseTreeNode item, final boolean empty)
    {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        }
    }
}

