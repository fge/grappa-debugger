package com.github.fge.grappa.debugger.csvtrace.tabs;

import com.github.fge.grappa.debugger.stats.ParseNode;
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
    public void parseNodeShowEventTest()
    {
        final ParseNode node = mock(ParseNode.class);

        display.parseNodeShowEvent(node);

        verify(presenter).handleParseNodeShow(same(node));
    }
}
