package com.github.fge.grappa.debugger.csvtrace.tabs.matches;

import com.github.fge.grappa.debugger.JavafxViewTest;
import com.github.fge.grappa.debugger.model.db.MatchStatistics;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class JavafxMatchesTabViewTest
    extends JavafxViewTest
{
    private JavafxMatchesTabView view;
    private MatchesTabDisplay display;

    @BeforeMethod
    public void init()
    {
        view = javafxGet(JavafxMatchesTabView::new);
        display = view.getDisplay();
    }

    @Test
    public void showMatchesTest()
    {
        final List<MatchStatistics> oldStats = Arrays.asList(
            mock(MatchStatistics.class),
            mock(MatchStatistics.class)
        );

        final List<MatchStatistics> newStats = Arrays.asList(
            mock(MatchStatistics.class),
            mock(MatchStatistics.class)
        );

        final TableView<MatchStatistics> tableView
            = javafxGet(() -> spy(new TableView<>()));
        display.matchesTable = tableView;

        final ObservableList<MatchStatistics> tableData = tableView.getItems();
        final ObservableList<TableColumn<MatchStatistics, ?>> sortOrder
            = tableView.getSortOrder();

        tableData.setAll(oldStats);
        sortOrder.clear();

        view.showMatches(newStats);

        assertThat(tableData).containsExactlyElementsOf(newStats);
        assertThat(sortOrder).containsExactly(display.nrCalls);
        verify(tableView).sort();
    }
}
