package com.github.fge.grappa.debugger.trace.tabs.treedepth;

import com.github.fge.grappa.debugger.model.TraceModel;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public final class TreeDepthInfoTest
{
    private TraceModel model;
    private TreeDepthInfo treeDepthInfo;

    @BeforeMethod
    public void init()
    {
        model = mock(TraceModel.class);
    }

    @Test
    public void initialValuesTest()
    {
        treeDepthInfo = new TreeDepthInfo(42, model);

        assertThat(treeDepthInfo.getStartLine()).isEqualTo(1);
        assertThat(treeDepthInfo.getEndLine()).isEqualTo(25);

        assertThat(treeDepthInfo.hasPreviousLines()).isFalse();
        assertThat(treeDepthInfo.hasNextLines()).isTrue();
    }

    @Test
    public void endLineCropInitTest()
    {
        treeDepthInfo = new TreeDepthInfo(10, model);

        assertThat(treeDepthInfo.getStartLine()).isEqualTo(1);
        assertThat(treeDepthInfo.getEndLine()).isEqualTo(10);

        assertThat(treeDepthInfo.hasPreviousLines()).isFalse();
        assertThat(treeDepthInfo.hasNextLines()).isFalse();
    }

    @Test
    public void nextLinesAdjustTest()
    {
        treeDepthInfo = new TreeDepthInfo(42, model);

        treeDepthInfo.nextLines();

        assertThat(treeDepthInfo.getStartLine()).isEqualTo(18);
        assertThat(treeDepthInfo.getEndLine()).isEqualTo(42);

        assertThat(treeDepthInfo.hasPreviousLines()).isTrue();
        assertThat(treeDepthInfo.hasNextLines()).isFalse();
    }

    @Test
    public void displayedLinesAdjustTest()
    {
        treeDepthInfo = new TreeDepthInfo(42, model);

        treeDepthInfo.setDisplayedLines(50);

        assertThat(treeDepthInfo.getStartLine()).isEqualTo(1);
        assertThat(treeDepthInfo.getEndLine()).isEqualTo(42);

        assertThat(treeDepthInfo.hasPreviousLines()).isFalse();
        assertThat(treeDepthInfo.hasNextLines()).isFalse();
    }

    @Test
    public void startLineAdjustTest()
    {
        treeDepthInfo = new TreeDepthInfo(30, model);

        treeDepthInfo.setStartLine(10);

        assertThat(treeDepthInfo.getStartLine()).isEqualTo(6);
        assertThat(treeDepthInfo.getEndLine()).isEqualTo(30);

        assertThat(treeDepthInfo.hasPreviousLines()).isTrue();
        assertThat(treeDepthInfo.hasNextLines()).isFalse();
    }

    @Test(dependsOnMethods = "startLineAdjustTest")
    public void startLineThenDisplayedLinesAdjustTest()
    {
        treeDepthInfo = new TreeDepthInfo(30, model);

        treeDepthInfo.setStartLine(10);
        treeDepthInfo.setDisplayedLines(50);

        assertThat(treeDepthInfo.getStartLine()).isEqualTo(1);
        assertThat(treeDepthInfo.getEndLine()).isEqualTo(30);

        assertThat(treeDepthInfo.hasPreviousLines()).isFalse();
        assertThat(treeDepthInfo.hasNextLines()).isFalse();
    }

    @Test(dependsOnMethods = "startLineAdjustTest")
    public void startLineThenPreviousLinesAdjustTest()
    {
        treeDepthInfo = new TreeDepthInfo(30, model);

        treeDepthInfo.setStartLine(10);
        treeDepthInfo.previousLines();

        assertThat(treeDepthInfo.getStartLine()).isEqualTo(1);
        assertThat(treeDepthInfo.getEndLine()).isEqualTo(25);

        assertThat(treeDepthInfo.hasPreviousLines()).isFalse();
        assertThat(treeDepthInfo.hasNextLines()).isTrue();
    }
}
