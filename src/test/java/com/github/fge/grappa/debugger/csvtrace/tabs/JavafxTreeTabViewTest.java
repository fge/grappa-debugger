package com.github.fge.grappa.debugger.csvtrace.tabs;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.google.common.util.concurrent.MoreExecutors;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public final class JavafxTreeTabViewTest
{
    private final Node node = mock(Node.class);
    private final BackgroundTaskRunner taskRunner = new BackgroundTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run
    );

    private JavafxTreeTabView view;
    private TreeTabDisplay display;
    private InputBuffer buffer;

    @BeforeMethod
    public void init()
    {
        display = new TreeTabDisplay();
        buffer = mock(InputBuffer.class);
        view = spy(new JavafxTreeTabView(taskRunner, node, display, buffer));
    }

    // TODO: fails with JavaFX thread context exception, wtf?
    @Test(enabled = false)
    public void loadTreeTest()
    {
        final ParseNode rootNode = mock(ParseNode.class);
        final TreeItem<ParseNode> item = mock(TreeItem.class);

        display.parseTree = mock(TreeView.class);
        display.treeExpand = mock(Button.class);

        doReturn(item).when(view).buildTree(same(rootNode));

        view.loadTree(rootNode);


        verify(display.parseTree).setRoot(same(item));
        verify(display.treeExpand).setDisable(false);
    }
}
