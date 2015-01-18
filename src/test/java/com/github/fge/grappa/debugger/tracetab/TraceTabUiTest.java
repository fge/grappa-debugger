package com.github.fge.grappa.debugger.tracetab;

import com.github.fge.grappa.debugger.tracetab.statistics.ParseNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

public class TraceTabUiTest
{
    private TraceTabPresenter presenter;
    private TraceTabUi ui;

    @BeforeMethod
    public void init()
    {
        presenter = mock(TraceTabPresenter.class);
        ui = new TraceTabUi();
        ui.init(presenter);
    }

    @Test
    public void parseNodeShowEventTest()
    {
        final ParseNode node = mock(ParseNode.class);

        ui.parseNodeShowEvent(node);
        verify(presenter, only()).handleParseNodeShow(same(node));
    }
}
