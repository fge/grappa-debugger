package com.github.fge.grappa.debugger.trace.tabs.treedepth;

import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.main.MainWindowView;
import com.google.common.util.concurrent.MoreExecutors;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class TreeDepthTabPresenterTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run);

    private MainWindowView mainView;
    private TraceDb traceDb;
    private TreeDepthInfo treeDepthInfo;

    private TreeDepthTabView view;

    private TreeDepthTabPresenter presenter;

    @BeforeMethod
    public void init()
    {
        mainView = mock(MainWindowView.class);
        view = mock(TreeDepthTabView.class);
        traceDb = mock(TraceDb.class);

        treeDepthInfo = mock(TreeDepthInfo.class);

        doNothing().when(treeDepthInfo).update();

        presenter = spy(new TreeDepthTabPresenter(taskRunner, mainView, traceDb,
            treeDepthInfo));

        presenter.setView(view);
    }

    @Test
    public void loadTest()
    {
        presenter.load();

        verify(presenter).doUpdate(any(Runnable.class));
        verifyZeroInteractions(treeDepthInfo);
    }

    @Test
    public void refreshTest()
    {
        doNothing().when(presenter).doRefresh(any(CountDownLatch.class));

        final ArgumentCaptor<CountDownLatch> captor
            = ArgumentCaptor.forClass(CountDownLatch.class);

        final CountDownLatch latch = presenter.refresh();

        verify(presenter).doRefresh(captor.capture());

        assertThat(captor.getValue()).isSameAs(latch);
    }

    @Test
    public void doRefreshTest()
    {
        final CountDownLatch latch = mock(CountDownLatch.class);

        presenter.doRefresh(latch);

        verify(presenter).doUpdate(any(Runnable.class));

        final InOrder inOrder = inOrder(treeDepthInfo, latch);

        inOrder.verify(treeDepthInfo).update();
        inOrder.verify(latch).countDown();
    }

    @DataProvider
    public Iterator<Object[]> invalidStartLines()
    {
        return Stream.of(null, "hello", "2a", "99999999999999999999999999999")
            .map(input -> new Object[] { input })
            .iterator();
    }

    @Test(dataProvider = "invalidStartLines")
    public void invalidStartLinesTest(final String input)
    {
        presenter.handleChangeStartLine(input);

        verify(presenter, never()).doChangeStartLine(anyInt());
    }

    @DataProvider
    public Iterator<Object[]> validStartLines()
    {
        return Stream.of("1", "32", "-10", "3929")
            .map(input -> new Object[] { input })
            .iterator();
    }

    @Test(dataProvider = "validStartLines")
    public void validStartLinesTest(final String input)
    {
        final int startLine = Integer.parseInt(input);

        doNothing().when(presenter).doChangeStartLine(anyInt());
        presenter.handleChangeStartLine(input);

        verify(presenter).doChangeStartLine(startLine);
    }

    @Test
    public void doChangeStartLineTest()
    {
        final int startLine = 42;

        presenter.doChangeStartLine(startLine);

        verify(presenter).doUpdate(any(Runnable.class));

        verify(treeDepthInfo).setStartLine(startLine);
    }

    @Test
    public void handlePreviousLinesTest()
    {
        presenter.handlePreviousLines();

        verify(presenter).doUpdate(any(Runnable.class));

        verify(treeDepthInfo).previousLines();
    }

    @Test
    public void handleNextLinesTest()
    {
        presenter.handleNextLines();

        verify(presenter).doUpdate(any(Runnable.class));

        verify(treeDepthInfo).nextLines();
    }

    @Test
    public void handleChangeDisplayedLinesTest()
    {
        final int displayedLines = 30;

        presenter.handleChangedDisplayedLines(displayedLines);

        verify(presenter).doUpdate(any(Runnable.class));

        verify(treeDepthInfo).setDisplayedLines(displayedLines);
    }

    @Test
    public void doUpdateTest()
    {
        final Runnable runnable = mock(Runnable.class);

        presenter.doUpdate(runnable);

        final InOrder inOrder = inOrder(view, runnable);

        inOrder.verify(view).disableTreeDepthToolbar();
        inOrder.verify(runnable).run();
        inOrder.verify(view).displayTreeDepthInfo(same(treeDepthInfo));
    }
}
