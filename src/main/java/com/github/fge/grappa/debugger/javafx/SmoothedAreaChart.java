package com.github.fge.grappa.debugger.javafx;

import javafx.beans.NamedArg;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.util.Pair;

/**
 * An {@link AreaChart} with smoothed lines
 *
 * <p>Code totally salvaged from <a
 * href="http://fxexperience.com/2012/01/curve-fitting-and-styling-areachart/">here</a>
 * </p>
 *
 * @param <X> x axis data type
 * @param <Y> y axis data type
 */
public class SmoothedAreaChart<X, Y>
    extends AreaChart<X, Y>
{
    public SmoothedAreaChart(@NamedArg("xAxis")final Axis<X> xAxis,
        @NamedArg("yAxis") final Axis<Y> yAxis,
        @NamedArg("data") final ObservableList<Series<X, Y>> data)
    {
        super(xAxis, yAxis, data);
    }

    public SmoothedAreaChart(@NamedArg("xAxis") final Axis<X> xAxis,
        @NamedArg("yAxis") final Axis<Y> yAxis)
    {
        super(xAxis, yAxis);
    }

    @Override
    protected void layoutPlotChildren()
    {
        super.layoutPlotChildren();

        XYChart.Series<X, Y> series;
        Path seriesLine;
        Path fillPath;

        for (int seriesIndex = 0; seriesIndex < getDataSize(); seriesIndex++) {
            series = getData().get(seriesIndex);
            seriesLine = (Path) ((Group) series.getNode())
                .getChildren().get(1);
            fillPath = (Path) ((Group) series.getNode())
                .getChildren().get(0);
            smooth(seriesLine.getElements(), fillPath.getElements());
        }
    }

    private int getDataSize()
    {
        final ObservableList<XYChart.Series<X, Y>> data = getData();
        return data != null ? data.size() : 0;
    }

    private static void smooth(final ObservableList<PathElement> strokeElements,
        final ObservableList<PathElement> fillElements)
    {
        if (fillElements.isEmpty())
            return;
        // as we do not have direct access to the data, first recreate the
        // list of all the data points we have
        final Point2D[] dataPoints = new Point2D[strokeElements.size()];

        PathElement element;

        for (int i = 0; i < strokeElements.size(); i++) {
            element = strokeElements.get(i);
            if (element instanceof MoveTo) {
                final MoveTo move = (MoveTo) element;
                dataPoints[i] = new Point2D(move.getX(), move.getY());
            } else if (element instanceof LineTo) {
                final LineTo line = (LineTo) element;
                final double x = line.getX();
                final double y = line.getY();
                dataPoints[i] = new Point2D(x, y);
            }
        }

        // next we need to know the zero Y value
        final double zeroY = ((MoveTo) fillElements.get(0)).getY();

        // now clear and rebuild elements
        strokeElements.clear();
        fillElements.clear();

        final Pair<Point2D[], Point2D[]> result
            = calcCurveControlPoints(dataPoints);
        final Point2D[] firstControlPoints = result.getKey();
        final Point2D[] secondControlPoints = result.getValue();

        // start both paths
        strokeElements.add(new MoveTo(dataPoints[0].getX(),
            dataPoints[0].getY()));
        fillElements.add(new MoveTo(dataPoints[0].getX(), zeroY));
        fillElements.add(new LineTo(dataPoints[0].getX(),
            dataPoints[0].getY()));

        // add curves
        int ci;
        CubicCurveTo cubicCurve;
        for (int i = 1; i < dataPoints.length; i++) {
            ci = i - 1;
            cubicCurve = new CubicCurveTo(
                firstControlPoints[ci].getX(), firstControlPoints[ci].getY(),
                secondControlPoints[ci].getX(), secondControlPoints[ci].getY(),
                dataPoints[i].getX(), dataPoints[i].getY()
            );
            strokeElements.add(cubicCurve);
            fillElements.add(cubicCurve);
        }

        // end the paths
        fillElements.add(new LineTo(dataPoints[dataPoints.length - 1].getX(),
            zeroY));
        fillElements.add(new ClosePath());
    }

    /**
     * Calculate open-ended Bezier Spline Control Points.
     *
     * @param dataPoints Input data Bezier spline points.
     */
    @SuppressWarnings("MethodCanBeVariableArityMethod")
    private static Pair<Point2D[], Point2D[]> calcCurveControlPoints(
        final Point2D[] dataPoints)
    {
        final Point2D[] firstControlPoints;
        final Point2D[] secondControlPoints;
        final int n = dataPoints.length - 1;

        if (n == 1) { // Special case: Bezier curve should be a straight line.
            firstControlPoints = new Point2D[1];
            // 3P1 = 2P0 + P3
            firstControlPoints[0] = new Point2D(
                (2 * dataPoints[0].getX() + dataPoints[1].getX()) / 3,
                (2 * dataPoints[0].getY() + dataPoints[1].getY()) / 3
            );

            secondControlPoints = new Point2D[1];
            // P2 = 2P1 – P0
            secondControlPoints[0] = new Point2D(
                2 * firstControlPoints[0].getX() - dataPoints[0].getX(),
                2 * firstControlPoints[0].getY() - dataPoints[0].getY()
            );

            return new Pair<>(firstControlPoints, secondControlPoints);
        }

        // Calculate first Bezier control points
        // Right hand side vector
        final double[] rhs = new double[n];

        // Set right hand side X values
        for (int i = 1; i < n - 1; ++i)
            rhs[i] = 4 * dataPoints[i].getX() + 2 * dataPoints[i + 1].getX();

        rhs[0] = dataPoints[0].getX() + 2 * dataPoints[1].getX();

        rhs[n - 1] = (8 * dataPoints[n - 1].getX() + dataPoints[n].getX())
            / 2.0;

        // Get first control points X-values
        final double[] x = getFirstControlPoints(rhs);

        // Set right hand side Y values
        for (int i = 1; i < n - 1; ++i)
            rhs[i] = 4 * dataPoints[i].getY() + 2 * dataPoints[i + 1].getY();

        rhs[0] = dataPoints[0].getY() + 2 * dataPoints[1].getY();

        rhs[n - 1] = (8 * dataPoints[n - 1].getY() + dataPoints[n].getY())
            / 2.0;

        // Get first control points Y-values
        final double[] y = getFirstControlPoints(rhs);

        // Fill output arrays.
        firstControlPoints = new Point2D[n];
        secondControlPoints = new Point2D[n];

        for (int i = 0; i < n; ++i) {
            // First control point
            firstControlPoints[i] = new Point2D(x[i], y[i]);
            // Second control point
            secondControlPoints[i] = i < n - 1
                ? new Point2D(2 * dataPoints[i + 1].getX() - x[i + 1],
                    2 * dataPoints[i + 1].getY() - y[i + 1])
                : new Point2D((dataPoints[n].getX() + x[n - 1]) / 2,
                    (dataPoints[n].getY() + y[n - 1]) / 2);
        }

        return new Pair<>(firstControlPoints, secondControlPoints);
    }

    /**
     * Solves a tridiagonal system for one of coordinates (x or y)
     * of first Bezier control points.
     *
     * @param rhs Right hand side vector.
     * @return Solution vector.
     */
    @SuppressWarnings("MethodCanBeVariableArityMethod")
    private static double[] getFirstControlPoints(final double[] rhs)
    {
        final int n = rhs.length;
        final double[] ret = new double[n]; // Solution vector.
        final double[] tmp = new double[n]; // Temp workspace.

        double b = 2.0;

        ret[0] = rhs[0] / b;

        for (int i = 1; i < n; i++) {
            // Decomposition and forward substitution.
            tmp[i] = 1 / b;
            b = (i < n - 1 ? 4.0 : 3.5) - tmp[i];
            ret[i] = (rhs[i] - ret[i - 1]) / b;
        }

        // Backsubstitution.
        for (int i = 1; i < n; i++)
            ret[n - i - 1] -= tmp[n - i] * ret[n - i];

        return ret;
    }
}
