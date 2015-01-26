package com.github.fge.grappa.debugger.tracetab.stat.classdetails;

import com.github.fge.grappa.debugger.stats.classdetails.MatcherClassDetails;
import com.github.fge.grappa.debugger.stats.classdetails.RuleInvocationDetails;
import com.google.common.annotations.VisibleForTesting;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Supplier;

import static com.github.fge.grappa.debugger.javafx.JavafxUtils.setColumnValue;

@ParametersAreNonnullByDefault
public class ClassDetailsStatsDisplay
{
    private ClassDetailsStatsPresenter presenter;

    /*
     * List
     */
    @FXML
    ListView<MatcherClassDetails> classNames;

    /*
     * Info
     */
    @FXML
    Label matcherType;

    @FXML
    Label nrRules;

    @FXML
    Label nrInvocations;

    @FXML
    Label invPct;

    /*
     * Pie chart
     */
    @FXML
    PieChart pieChart;

    @FXML
    public TableView<RuleInvocationDetails> ruleTable;

    @FXML
    TableColumn<RuleInvocationDetails, String> ruleName;

    @FXML
    TableColumn<RuleInvocationDetails, Integer> ruleInvTotal;

    @FXML
    TableColumn<RuleInvocationDetails, String> ruleInvDetail;

    @FXML
    TableColumn<RuleInvocationDetails, RuleInvocationDetails> ruleInvGraph;

    public void setPresenter(final ClassDetailsStatsPresenter presenter)
    {
        this.presenter = Objects.requireNonNull(presenter);
        init();
    }

    @SuppressWarnings("AutoBoxing")
    @VisibleForTesting
    void init()
    {
        classNames.setCellFactory(param -> new MatcherClassCell(this));

        setColumnValue(ruleName, RuleInvocationDetails::getRuleName);
        setColumnValue(ruleInvTotal, detail -> detail.getFailedMatches()
            + detail.getEmptyMatches() + detail.getNonEmptyMatches());
        setColumnValue(ruleInvDetail, detail -> String.format(
            "%d / %d / %d", detail.getNonEmptyMatches(),
            detail.getEmptyMatches(), detail.getFailedMatches()
        ));

        ruleInvGraph.setCellValueFactory(
            param -> new SimpleObjectProperty<RuleInvocationDetails>()
            {
                @Override
                public RuleInvocationDetails get()
                {
                    return param.getValue();
                }
            });

        ruleInvGraph.setCellFactory(param ->
            new TableCell<RuleInvocationDetails, RuleInvocationDetails>()
            {
                @Override
                protected void updateItem(final RuleInvocationDetails item,
                    final boolean empty)
                {
                    super.updateItem(item, empty);

                    setText(null);

                    if (empty) {
                        setGraphic(null);
                        return;
                    }

                    final ReadOnlyDoubleProperty cellWidth
                        = param.widthProperty();

                    final int nonEmptyMatches = item.getNonEmptyMatches();
                    final int emptyMatches = item.getEmptyMatches();
                    final int failedMatches = item.getFailedMatches();
                    final int total = nonEmptyMatches + emptyMatches
                        + failedMatches;

                    final double nonEmptyRatio = (double) nonEmptyMatches
                        / total;
                    final double emptyRatio = (double) emptyMatches / total;
                    final double failureRatio = (double) failedMatches / total;

                    // FIXME: unfortunately I cannot use Rectangle::new...
                    // If I do, the rectangle is not visible :/
                    final Supplier<Rectangle> supplier
                        = () -> new Rectangle(0.0, 20.0);

                    final HBox hbox = new HBox();
                    final ObservableList<Node> nodes = hbox.getChildren();

                    DoubleExpression expr;
                    Rectangle r;

                    r = supplier.get();
                    expr = cellWidth.multiply(nonEmptyRatio);
                    r.widthProperty().bind(expr);
                    r.setStyle("-fx-fill: CHART_COLOR_3");
                    nodes.add(r);

                    r = supplier.get();
                    expr = cellWidth.multiply(emptyRatio);
                    r.widthProperty().bind(expr);
                    r.setStyle("-fx-fill: CHART_COLOR_2");
                    nodes.add(r);

                    r = supplier.get();
                    expr = cellWidth.multiply(failureRatio);
                    r.widthProperty().bind(expr);
                    r.setStyle("-fx-fill: CHART_COLOR_1");
                    nodes.add(r);

                    setGraphic(hbox);
                }
            });
    }

    @VisibleForTesting
    void showClassDetailsEvent(final MatcherClassDetails details)
    {
        Objects.requireNonNull(details);
        presenter.handleShowClassDetails(details);
    }

    private static final class MatcherClassCell
        extends ListCell<MatcherClassDetails>
    {
        private MatcherClassCell(final ClassDetailsStatsDisplay display)
        {
            setEditable(false);
            selectedProperty().addListener(new ChangeListener<Boolean>()
            {
                @SuppressWarnings("AutoUnboxing")
                @Override
                public void changed(
                    final ObservableValue<? extends Boolean> observable,
                    final Boolean oldValue, final Boolean newValue)
                {
                    if (oldValue == newValue)
                        return;
                    if (!newValue)
                        return;
                    final MatcherClassDetails details = getItem();
                    display.showClassDetailsEvent(details);
                }
            });
        }

        @Override
        protected void updateItem(final MatcherClassDetails item,
            final boolean empty)
        {
            super.updateItem(item, empty);
            if (empty)
                return;
            setText(item == null ? null : item.getClassName());
        }
    }
}
