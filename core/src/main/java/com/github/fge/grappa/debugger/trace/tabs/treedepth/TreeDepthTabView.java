package com.github.fge.grappa.debugger.trace.tabs.treedepth;

import com.github.fge.grappa.debugger.trace.tabs.TabView;

public interface TreeDepthTabView
    extends TabView
{
    void disableTreeDepthToolbar();

    void displayTreeDepthInfo(TreeDepthInfo treeDepthInfo);
}
