package com.github.fge.grappa.debugger.javafx.csvtrace;

import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTracePresenter;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceView;
import com.github.fge.grappa.debugger.csvtrace.tabs.matches.MatchesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.rules.RulesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.treedepth.TreeDepthTabPresenter;
import com.github.fge.grappa.debugger.javafx.common.JavafxView;
import com.github.fge.grappa.debugger.javafx.csvtrace.tabs.matches.JavafxMatchesTabView;
import com.github.fge.grappa.debugger.javafx.csvtrace.tabs.rules.JavafxRulesTabView;
import com.github.fge.grappa.debugger.javafx.csvtrace.tabs.tree.JavafxTreeTabView;
import com.github.fge.grappa.debugger.javafx.csvtrace.tabs.treedepth.JavafxTreeDepthTabView;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.model.db.DbLoadStatus;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

@ParametersAreNonnullByDefault
@NonFinalForTesting
public class JavafxCsvTraceView
    extends JavafxView<CsvTracePresenter, CsvTraceDisplay>
    implements CsvTraceView
{
    private final GuiTaskRunner taskRunner;
    private final MainWindowView parentView;

    private int lastLoaded = 0;

    public JavafxCsvTraceView(final GuiTaskRunner taskRunner,
        final MainWindowView parentView)
        throws IOException
    {
        super("/csvTrace.fxml");
        this.taskRunner = Objects.requireNonNull(taskRunner);
        this.parentView = Objects.requireNonNull(parentView);
    }

    @Override
    public void reportProgress(final DbLoadStatus status)
    {
        final int total = status.getTotal();
        final int loaded = status.getCurrent();

        final int loadedThisRound = loaded - lastLoaded;
        lastLoaded = loaded;

        final double pct = (double) loaded / total;
        display.progressBar.setProgress(pct);

        final StringBuilder sb = new StringBuilder()
            .append(String.format("%.02f%%", pct * 100));

        if (loadedThisRound != 0) {
            sb.append(", ");

            // Rough estimate; it works since we are polled every second
            int estimate = (total - loaded) / loadedThisRound;

            final int seconds = estimate % 60;
            estimate /= 60;

            if (estimate > 0)
                sb.append(estimate).append(" min ");
            sb.append(seconds).append(" s remaining");
        }

        display.progressMessage.setText(sb.toString());
    }

    @Override
    public void loadComplete()
    {
        display.progressMessage.setText("load complete");
    }

    @Override
    public void loadAborted()
    {
        display.progressMessage.setText("load aborted");
    }

    @Override
    public void loadTreeTab(final TreeTabPresenter tabPresenter)
    {
        Objects.requireNonNull(tabPresenter);

        final JavafxTreeTabView tabView;
        try {
            tabView = getTreeTabView();
        } catch (IOException e) {
            parentView.showError("Load error", "Unable to load tree tab", e);
            return;
        }
        tabView.getDisplay().setPresenter(tabPresenter);
        tabPresenter.setView(tabView);
        display.treeTab.setContent(tabView.getNode());
    }

    @VisibleForTesting
    JavafxTreeTabView getTreeTabView()
        throws IOException
    {
        return new JavafxTreeTabView(taskRunner);
    }

    @Override
    public void loadRulesTab(final RulesTabPresenter tabPresenter)
    {
        Objects.requireNonNull(tabPresenter);

        final JavafxRulesTabView tabView;
        try {
            tabView = getRulesTabView();
        } catch (IOException e) {
            parentView.showError("Load error", "Unable to load rules tab", e);
            return;
        }
        tabView.getDisplay().setPresenter(tabPresenter);
        tabPresenter.setView(tabView);
        display.rulesTab.setContent(tabView.getNode());
    }

    @VisibleForTesting
    JavafxRulesTabView getRulesTabView()
        throws IOException
    {
        return new JavafxRulesTabView();
    }

    @Override
    public void loadMatchesTab(final MatchesTabPresenter tabPresenter)
    {
        Objects.requireNonNull(tabPresenter);

        final JavafxMatchesTabView tabView;
        try {
            tabView = getMatchesTabView();
        } catch (IOException e) {
            parentView.showError("Load error", "Unable to load matches tab", e);
            return;
        }
        tabView.getDisplay().setPresenter(tabPresenter);
        tabPresenter.setView(tabView);
        display.matchesTab.setContent(tabView.getNode());
    }

    @VisibleForTesting
    JavafxMatchesTabView getMatchesTabView()
        throws IOException
    {
        return new JavafxMatchesTabView();
    }

    @Override
    public void loadTreeDepthTab(final TreeDepthTabPresenter tabPresenter)
    {
        Objects.requireNonNull(tabPresenter);

        final JavafxTreeDepthTabView tabView;
        try {
            tabView = getTreeDepthTabView();
        } catch (IOException e) {
            parentView.showError("Load error", "Unable to load statistics", e);
            return;
        }
        tabView.getDisplay().setPresenter(tabPresenter);
        tabPresenter.setView(tabView);
        display.treeDepthTab.setContent(tabView.getNode());
    }

    @VisibleForTesting
    JavafxTreeDepthTabView getTreeDepthTabView()
        throws IOException
    {
        return new JavafxTreeDepthTabView();
    }

    @Override
    public void showLoadComplete()
    {
        display.pane.setTop(null);
    }

    @Override
    public void disableTabsRefresh()
    {
        display.refresh.setDisable(true);
    }

    @Override
    public void enableTabsRefresh()
    {
        display.refresh.setDisable(false);
    }
}
