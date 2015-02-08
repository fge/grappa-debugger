package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import com.github.fge.grappa.debugger.javafx.JavafxView;

import java.io.IOException;

public final class JavafxTreeDepthTabView
    extends JavafxView<TreeDepthTabPresenter, TreeDepthTabDisplay>
    implements TreeDepthTabView
{
    public JavafxTreeDepthTabView()
        throws IOException
    {
        super("/tabs/treeDepthTab.fxml");
    }

    @Override
    public void enableTabRefresh()
    {
        display.tabRefresh.setDisable(false);
    }

    @Override
    public void disableRefresh()
    {
        display.refreshBox.getChildren().remove(display.tabRefresh);
    }

    @Override
    public void enablePrevious()
    {
        display.prevLines.setDisable(false);
    }

    @Override
    public void enableNext()
    {
        display.nextLines.setDisable(false);
    }
}
