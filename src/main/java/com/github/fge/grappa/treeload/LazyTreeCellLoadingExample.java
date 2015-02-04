package com.github.fge.grappa.treeload;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class LazyTreeCellLoadingExample
    extends Application
{

    // Executor for background tasks:
    private static final ExecutorService EXECUTOR
        = Executors.newCachedThreadPool(runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

    @Override
    public void start(final Stage primaryStage)
    {
        final TreeView<Long> tree = new TreeView<>();
        tree.setRoot(new LazyTreeItem(1L));

        // cell factory that displays progress bar when item is loading
        // children:
        tree.setCellFactory(new CustomCellFactory());

        final Button debugButton = new Button("Debug");
        debugButton.setOnAction(evt -> {
            dumpData(tree.getRoot(), 0);
        });

        primaryStage.setScene(new Scene(new BorderPane(tree, null, null,
            debugButton, null), 400, 250));
        primaryStage.show();
    }

    private void dumpData(final TreeItem<Long> node, final int depth)
    {
        for (int i = 0; i < depth; i++)
            System.out.print(" ");
        System.out.println(node.getValue());
        for (final TreeItem<Long> child : node.getChildren())
            dumpData(child, depth + 1);
    }

    // TreeItem subclass that lazily loads children in background
    // Exposes an observable property specifying current load status of children
    private static final class LazyTreeItem
        extends TreeItem<Long>
    {

        // possible load statuses:
        enum ChildrenLoadedStatus
        {
            NOT_LOADED, LOADING, LOADED
        }

        // observable property for current load status:
        private final ObjectProperty<ChildrenLoadedStatus> childrenLoadedStatus
            = new SimpleObjectProperty<>(ChildrenLoadedStatus.NOT_LOADED);

        public LazyTreeItem(final Long value)
        {
            super(value);
        }

        // getChildren() method loads children lazily:
        @Override
        public ObservableList<TreeItem<Long>> getChildren()
        {
            if (getChildrenLoadedStatus() == ChildrenLoadedStatus.NOT_LOADED) {
                lazilyLoadChildren();
            }
            return super.getChildren();
        }

        // load child nodes in background, updating status accordingly:
        private void lazilyLoadChildren()
        {

            // change current status to "loading":
            setChildrenLoadedStatus(ChildrenLoadedStatus.LOADING);
            final long value = getValue();

            // background task to load children:
            final Task<List<LazyTreeItem>> loadTask = new Task<List<LazyTreeItem>>()
            {

                @Override
                protected List<LazyTreeItem> call()
                    throws Exception
                {
                    final List<LazyTreeItem> children = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        children.add(new LazyTreeItem(10 * value + i));
                    }

                    // for testing (loading is so lazy it falls asleep)
                    Thread.sleep(3000);
                    return children;
                }

            };

            // when loading is complete:
            // 1. set actual child nodes to loaded nodes
            // 2. update status to "loaded"
            loadTask.setOnSucceeded(event -> {
                super.getChildren().setAll(loadTask.getValue());
                setChildrenLoadedStatus(ChildrenLoadedStatus.LOADED);
            });

            // execute task in backgroun
            EXECUTOR.submit(loadTask);
        }

        // is leaf is true only if we *know* there are no children
        // i.e. we've done the loading and still found nothing
        @Override
        public boolean isLeaf()
        {
            return getChildrenLoadedStatus() == ChildrenLoadedStatus.LOADED
                && super.getChildren().isEmpty();
        }

        // normal property accessor methods:

        public final ObjectProperty<ChildrenLoadedStatus>
        childrenLoadedStatusProperty()
        {
            return childrenLoadedStatus;
        }

        public final LazyTreeCellLoadingExample.LazyTreeItem
            .ChildrenLoadedStatus getChildrenLoadedStatus()
        {
            return childrenLoadedStatusProperty().get();
        }

        public final void setChildrenLoadedStatus(
            final LazyTreeCellLoadingExample.LazyTreeItem.ChildrenLoadedStatus childrenLoadedStatus)
        {
            childrenLoadedStatusProperty().set(childrenLoadedStatus);
        }
    }

    private static final class CustomCellFactory
        implements Callback<TreeView<Long>, TreeCell<Long>>
    {

        /**
         * The <code>call</code> method is called when required, and is given a
         * single argument of type P, with a requirement that an object of
         * type R
         * is returned.
         *
         * @param param The single argument upon which the returned value
         * should be
         * determined.
         * @return An object of type R that may be determined based on the
         * provided
         * parameter value.
         */
        @Override
        public TreeCell<Long> call(final TreeView<Long> param)
        {
            // the cell:
            final TreeCell<Long> cell = new TreeCell<>();

            // progress bar to display when needed:
            final ProgressBar progressBar = new ProgressBar();

            // listener to observe *current* tree item's child loading status:
            final ChangeListener<LazyTreeItem.ChildrenLoadedStatus> listener = (obs,
                oldStatus, newStatus) -> {
                if (newStatus == LazyTreeItem.ChildrenLoadedStatus.LOADING) {
                    cell.setGraphic(progressBar);
                } else {
                    cell.setGraphic(null);
                }
            };

            // listener for tree item property
            // ensures that listener above is attached to current tree item:
            cell.treeItemProperty().addListener((obs, oldItem, newItem) -> {

                // if we were displaying an item, (and no longer are...),
                // stop observing its child loading status:
                if (oldItem != null) {
                    ((LazyTreeItem) oldItem).childrenLoadedStatusProperty()
                        .removeListener(listener);
                }

                // if there is a new item the cell is displaying:
                if (newItem != null) {

                    // update graphic to display progress bar if needed:
                    final LazyTreeItem lazyTreeItem = (LazyTreeItem) newItem;
                    if (lazyTreeItem.getChildrenLoadedStatus()
                        == LazyTreeItem.ChildrenLoadedStatus.LOADING) {
                        cell.setGraphic(progressBar);
                    } else {
                        cell.setGraphic(null);
                    }

                    // observe loaded status of current item in case it changes
                    // while we are still displaying this item:
                    lazyTreeItem.childrenLoadedStatusProperty().addListener(
                        listener);
                }
            });

            // change text if item changes:
            cell.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem == null) {
                    cell.setText(null);
                    cell.setGraphic(null);
                } else {
                    cell.setText(newItem.toString());
                }
            });

            return cell;
        }
    }

    public static void main(final String[] args)
    {
        launch(args);
    }
}
