package com.github.fge.grappa.debugger.tracetab.stat.global;

import com.github.fge.grappa.debugger.internal.NotFXML;
import com.github.fge.grappa.debugger.javafx.JavafxUtils;
import com.github.fge.grappa.debugger.stats.RuleMatchingStats;
import com.github.fge.grappa.matchers.MatcherType;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class GlobalStatsDisplay
{
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private GlobalStatsPresenter presenter;

    /*
     * Text stats
     */
    @FXML
    Label parseDate;

    @FXML
    Label totalParseTime;

    @FXML
    Label treeDepth;

    @FXML
    Label nrRules;

    @FXML
    Label invPerLine;

    @FXML
    Label invPerChar;

    /*
     * Pie chart
     */
    @FXML
    PieChart matchChart;

    /*
     * Table
     */
    @FXML
    TableView<RuleMatchingStats> stats;

    @FXML
    private TableColumn<RuleMatchingStats, String> ruleName;

    @FXML
    private TableColumn<RuleMatchingStats, String> ruleClass;

    @FXML
    private TableColumn<RuleMatchingStats, MatcherType> ruleType;

    @FXML
    private TableColumn<RuleMatchingStats, Integer> nrCalls;

    @FXML
    private TableColumn<RuleMatchingStats, String> callDetail;

    @FXML
    private TableColumn<RuleMatchingStats, RuleMatchingStats> callGraph;

    @NotFXML
    public void setPresenter(final GlobalStatsPresenter presenter)
    {
        this.presenter = Objects.requireNonNull(presenter);
        init();
    }

    protected void init()
    {
        /*
         * Stats table
         */
        JavafxUtils.setColumnValue(ruleName, RuleMatchingStats::getRuleName);

        JavafxUtils.setColumnValue(ruleClass,
            RuleMatchingStats::getMatcherClass);

        JavafxUtils.setColumnValue(ruleType, RuleMatchingStats::getMatcherType);

        JavafxUtils.setColumnValue(nrCalls,
            stats -> stats.getNonEmptyMatches() + stats.getEmptyMatches()
                + stats.getFailures());

        //noinspection AutoBoxing
        JavafxUtils.setColumnValue(callDetail,
            stats -> String.format("%d / %d / %d", stats.getNonEmptyMatches(),
                stats.getEmptyMatches(), stats.getFailures()));

        callGraph.setCellValueFactory(
            param -> new SimpleObjectProperty<RuleMatchingStats>()
            {
                @Override
                public RuleMatchingStats get()
                {
                    return param.getValue();
                }
            }
        );

        callGraph.setCellFactory(
            param -> new TableCell<RuleMatchingStats, RuleMatchingStats>()
            {
                @Override
                protected void updateItem(final RuleMatchingStats item,
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
                    final int failedMatches = item.getFailures();
                    final int total = nonEmptyMatches + emptyMatches
                        + failedMatches;

                    final double nonEmptyRatio
                        = (double) nonEmptyMatches / total;
                    final double emptyRatio
                        = (double) emptyMatches / total;
                    final  double failureRatio
                        = (double) failedMatches / total;

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
            }
        );
    }
}
