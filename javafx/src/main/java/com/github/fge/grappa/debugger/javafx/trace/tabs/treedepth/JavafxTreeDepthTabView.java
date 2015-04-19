package com.github.fge.grappa.debugger.javafx.trace.tabs.treedepth;

import com.github.fge.grappa.debugger.ParseInfo;
import com.github.fge.grappa.debugger.javafx.common.JavafxView;
import com.github.fge.grappa.debugger.trace.tabs.treedepth.TreeDepthInfo;
import com.github.fge.grappa.debugger.trace.tabs.treedepth
    .TreeDepthTabPresenter;
import com.github.fge.grappa.debugger.trace.tabs.treedepth.TreeDepthTabView;

import java.io.IOException;

public final class JavafxTreeDepthTabView
    extends JavafxView<TreeDepthTabPresenter, TreeDepthTabDisplay>
    implements TreeDepthTabView
{
    public JavafxTreeDepthTabView()
        throws IOException
    {
        super("/javafx/tabs/treeDepth.fxml");
    }

    @Override
    public void disableTreeDepthToolbar()
    {
        // TODO

    }

    @Override
    public void displayTreeDepthInfo(final TreeDepthInfo treeDepthInfo)
    {
        // TODO

    }

    @Override
    public void displayInfo(final ParseInfo info)
    {
        // TODO

    }
}
