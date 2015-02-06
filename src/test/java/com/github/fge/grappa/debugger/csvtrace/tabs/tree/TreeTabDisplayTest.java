package com.github.fge.grappa.debugger.csvtrace.tabs.tree;

import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.debugger.javafx.parsetree.ParseTreeItem;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class TreeTabDisplayTest
{
    private TreeTabPresenter presenter;
    private TreeTabDisplay display;

    @BeforeMethod
    public void init()
    {
        display = spy(new TreeTabDisplay());
        doNothing().when(display).init();
        presenter = mock(TreeTabPresenter.class);
        display.setPresenter(presenter);
    }

    @Test
    public void parseTreeNodeShowEventTest()
    {
        final ParseTreeNode node = mock(ParseTreeNode.class);

        display.parseTreeNodeShowEvent(node);

        verify(presenter).handleParseTreeNodeShow(same(node));
    }

    @Test
    public void needChildrenTest()
    {
        final ParseTreeNode node = mock(ParseTreeNode.class);

        final ParseTreeItem item = new ParseTreeItem(display, node);

        display.needChildrenEvent(item);

        verify(presenter).handleNeedChildren(same(node));
    }
}
