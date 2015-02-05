package com.github.fge.grappa.debugger.javafx;

import com.github.fge.grappa.debugger.common.db.RuleInvocationStatistics;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.function.Supplier;

public final class CallGraphTableCell
    extends TableCell<RuleInvocationStatistics, RuleInvocationStatistics>
{
    // FIXME: unfortunately I cannot use Rectangle::new...
    // If I do, the rectangle is not visible :/
    private static final Supplier<Rectangle> RECTANGLE_SUPPLIER
        = () -> new Rectangle(0.0, 20.0);

    private final ReadOnlyDoubleProperty columnWidth;

    public CallGraphTableCell(
        final TableColumn<RuleInvocationStatistics, RuleInvocationStatistics>
            column)
    {
        columnWidth = column.widthProperty();
    }

    @Override
    protected void updateItem(final RuleInvocationStatistics item,
        final boolean empty)
    {
        super.updateItem(item, empty);

        setText(null);

        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        final int nonEmptyMatches = item.getNonEmptyMatches();
        final int emptyMatches = item.getEmptyMatches();
        final int failedMatches = item.getFailedMatches();
        final int total = nonEmptyMatches + emptyMatches + failedMatches;

        // We must account for this...
        if (total == 0) {
            setGraphic(new Text("N/A"));
            return;
        }

        final double nonEmptyRatio = (double) nonEmptyMatches / total;
        final double emptyRatio = (double) emptyMatches / total;
        final double failureRatio = (double) failedMatches / total;

        final HBox hbox = new HBox();
        final ObservableList<Node> nodes = hbox.getChildren();

        DoubleExpression expr;
        Rectangle r;

        r = RECTANGLE_SUPPLIER.get();
        expr = columnWidth.multiply(nonEmptyRatio);
        r.widthProperty().bind(expr);
        r.setFill(Color.rgb(78, 164, 78));
        nodes.add(r);

        r = RECTANGLE_SUPPLIER.get();
        expr = columnWidth.multiply(emptyRatio);
        r.widthProperty().bind(expr);
        r.setFill(Color.rgb(234, 160, 41));
        nodes.add(r);

        r = RECTANGLE_SUPPLIER.get();
        expr = columnWidth.multiply(failureRatio);
        r.widthProperty().bind(expr);
        r.setFill(Color.rgb(224, 97, 49));
        nodes.add(r);

        setGraphic(hbox);
    }
}
