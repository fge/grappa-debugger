package com.github.fge.grappa.debugger.legacy.tracetab;

import com.github.fge.grappa.debugger.statistics.ParseNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

public class LegacyTraceTabDisplayTest
{
    private LegacyTraceTabPresenter presenter;
    private LegacyTraceTabDisplay display;

    @BeforeMethod
    public void init()
    {
        presenter = mock(LegacyTraceTabPresenter.class);
        display = new LegacyTraceTabDisplay();
        display.init(presenter);
    }

    @Test
    public void parseNodeShowEventTest()
    {
        final ParseNode node = mock(ParseNode.class);

        display.parseNodeShowEvent(node);
        verify(presenter, only()).handleParseNodeShow(same(node));
    }
}
