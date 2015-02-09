package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.model.ParseInfo;
import com.google.common.util.concurrent.MoreExecutors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TreeDepthTabPresenterTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run
    );

    private TreeDepthTabView view;
    private ParseInfo info;
    private CsvTraceModel model;
    private MainWindowView mainView;
    private TreeDepthTabPresenter presenter;

    @BeforeMethod
    public void init()
    {
        view = mock(TreeDepthTabView.class);

        info = mock(ParseInfo.class);

        model = mock(CsvTraceModel.class);
        when(model.getParseInfo()).thenReturn(info);

        mainView = mock(MainWindowView.class);

        presenter = spy(new TreeDepthTabPresenter(taskRunner, mainView, model));
        presenter.setView(view);
    }

    @Test
    public void initTest()
    {
        final int theAnswer = 42;
        when(info.getNrLines()).thenReturn(theAnswer);

        presenter.init();

        verify(view).setMaxLines(theAnswer);
    }

    @Test
    public void initialArgumentsTest()
    {
        assertThat(presenter.startLine).isEqualTo(1);
        assertThat(presenter.displayedLines).isEqualTo(25);
    }
}
