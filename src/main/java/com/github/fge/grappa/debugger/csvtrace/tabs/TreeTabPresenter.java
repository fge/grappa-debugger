package com.github.fge.grappa.debugger.csvtrace.tabs;

import com.github.fge.grappa.debugger.common.BasePresenter;
import com.github.fge.grappa.debugger.stats.ParseNode;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class TreeTabPresenter
    extends BasePresenter<TreeTabView>
{
    private final ParseNode rootNode;

    public TreeTabPresenter(final ParseNode rootNode)
    {
        this.rootNode = Objects.requireNonNull(rootNode);
    }

    public void load()
    {
        view.loadTree(rootNode);
    }

    void parseNodeShowEvent(final ParseNode node)
    {
    }
}
