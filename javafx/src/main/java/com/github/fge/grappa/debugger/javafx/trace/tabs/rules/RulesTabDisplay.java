package com.github.fge.grappa.debugger.javafx.trace.tabs.rules;

import com.github.fge.grappa.debugger.javafx.common.JavafxDisplay;
import com.github.fge.grappa.debugger.model.rules.PerClassStatistics;
import com.github.fge.grappa.debugger.trace.tabs.rules.RulesTabPresenter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import static com.github.fge.grappa.debugger.javafx.common.JavafxUtils
    .setColumnValue;


public class RulesTabDisplay
    extends JavafxDisplay<RulesTabPresenter>
{
    /*
     * General match information
     */

    @FXML
    protected Label parseDate;

    @FXML
    protected Label parseTime;

    @FXML
    protected Label treeDepth;

    @FXML
    protected Label nrRules;

    @FXML
    protected Label invPerLine;

    @FXML
    protected Label invPerChar;

    /*
     * Rules pie chart
     */

    @FXML
    protected PieChart rulesChart;

    protected final PieChart.Data terminalsPie = new PieChart.Data("", 0.0);
    protected final PieChart.Data compositesPie = new PieChart.Data("", 0.0);
    protected final PieChart.Data predicatesPie = new PieChart.Data("", 0.0);
    protected final PieChart.Data actionsPie = new PieChart.Data("", 0.0);

    /*
     * Rules table
     */

    @FXML
    protected TableView<PerClassStatistics> rulesTable;

    @FXML
    protected TableColumn<PerClassStatistics, String> ruleClass;

    @FXML
    protected TableColumn<PerClassStatistics, Integer> ruleCount;

    @FXML
    protected TableColumn<PerClassStatistics, String> rulePct;

    @FXML
    protected TableColumn<PerClassStatistics, Integer> invCount;

    @FXML
    protected TableColumn<PerClassStatistics, String> invPct;

    @SuppressWarnings("AutoBoxing")
    @Override
    public void init()
    {
        /*
         * Rules pie chart
         */
        final ObservableList<PieChart.Data> list
            = FXCollections.observableArrayList();

        list.addAll(terminalsPie, compositesPie, predicatesPie, actionsPie);

        rulesChart.setData(list);

        /*
         * Rules table
         */
        setColumnValue(ruleClass, PerClassStatistics::getClassName);
        setColumnValue(ruleCount, PerClassStatistics::getNrRules);
        setColumnValue(invCount, PerClassStatistics::getNrCalls);
        rulePct.setCellValueFactory(param -> {
            final int total = param.getTableView().getItems().stream()
                .mapToInt(PerClassStatistics::getNrRules).sum();
            final int thisRule = param.getValue().getNrRules();
            final String s = String.format("%.02f%%", 100.0 * thisRule / total);
            return new SimpleStringProperty(s);
        });
        invPct.setCellValueFactory(param -> {
            final int total = param.getTableView().getItems().stream()
                .mapToInt(PerClassStatistics::getNrCalls).sum();
            final int thisRule = param.getValue().getNrCalls();
            final String s = String.format("%.02f%%", 100.0 * thisRule / total);
            return new SimpleStringProperty(s);
        });
        rulesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}
