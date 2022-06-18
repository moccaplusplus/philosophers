package smb.philosophers.canteen.report;

import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.ViewBox;
import smb.philosophers.Settings;
import smb.philosophers.canteen.event.MealEvent;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;

import static java.awt.Color.blue;
import static java.awt.Color.gray;
import static java.awt.Color.green;
import static java.awt.Color.lightGray;
import static java.awt.Color.red;
import static java.awt.Color.white;
import static java.lang.String.format;
import static org.jfree.chart.JFreeChart.DEFAULT_TITLE_FONT;
import static org.jfree.chart.plot.PlotOrientation.HORIZONTAL;
import static smb.philosophers.Messages.msg;
import static smb.philosophers.canteen.event.MealEvent.Type.FINISHED;
import static smb.philosophers.canteen.event.MealEvent.Type.REJECTED;
import static smb.philosophers.canteen.event.MealEvent.Type.REQUESTED;
import static smb.philosophers.canteen.event.MealEvent.Type.STARTED;

public class Plotter {

    private static final int CHART_WIDTH = 700;
    private static final int CHART_ROW_HEIGHT = 75;
    private static final double CHART_ROWS_MARGIN = 0.75;
    private static final double ROW_DIV_RATIO = 0.2;
    private static final double CHART_CONTINUE_UNCLOSED = 0.1;

    private final Settings settings;
    private final ChartTheme chartTheme;

    public Plotter(Settings settings, ChartTheme chartTheme) {
        this.settings = settings;
        this.chartTheme = chartTheme;
    }

    public String plot(List<MealEvent> events) {
        final var eventSeries = eventSeries(events);
        final var durationSeries = durationSeries(events);
        final var chart = createChart(eventSeries, durationSeries);
        return toSvg(chart);
    }

    private XYSeriesCollection durationSeries(List<MealEvent> events) {
        final var seriesCollection = new XYSeriesCollection();
        final var seriesMap = new HashMap<Integer, XYSeries>();
        events.forEach(e -> {
            if (e.getType() == STARTED) {
                final var which = e.getTarget() + 1;
                final var series = new XYSeries(format("%d: %f", which, e.getTimestamp()), false);
                series.add(which - ROW_DIV_RATIO, e.getTimestamp());
                series.add(which - 2 * ROW_DIV_RATIO, e.getTimestamp());
                seriesMap.put(which, series);
            } else if (e.getType() == FINISHED) {
                final var which = e.getTarget() + 1;
                final var series = seriesMap.remove(which);
                series.add(which - 2 * ROW_DIV_RATIO, e.getTimestamp());
                seriesCollection.addSeries(series);
            }
        });
        seriesMap.values().forEach(series -> {
            series.add(series.getX(1), settings.getTime() + CHART_CONTINUE_UNCLOSED);
            seriesCollection.addSeries(series);
        });
        return seriesCollection;
    }

    private XYSeriesCollection eventSeries(List<MealEvent> events) {
        final var seriesCollection = new XYSeriesCollection();
        for (final var type : MealEvent.Type.values()) seriesCollection.addSeries(new XYSeries(type));
        for (final var event : events) {
            final var series = seriesCollection.getSeries(event.getType());
            series.add(getY(event), event.getTimestamp());
        }
        return seriesCollection;
    }

    private double getY(MealEvent event) {
        final var which = event.getTarget() + 1;
        switch (event.getType()) {
            case REJECTED:
            case STARTED:
                return which - ROW_DIV_RATIO;
            case FINISHED:
                return which - 2 * ROW_DIV_RATIO;
            case REQUESTED:
                return which + ROW_DIV_RATIO;
        }
        throw new IllegalStateException();
    }

    private String toSvg(JFreeChart chart) {
        final var height = settings.getCount() * CHART_ROW_HEIGHT;
        final var svg2d = new SVGGraphics2D(CHART_WIDTH, height);
        chart.draw(svg2d, new Rectangle(0, 0, CHART_WIDTH, height));
        return svg2d.getSVGElement(null, false, new ViewBox(0, 0, CHART_WIDTH, height), null, null);
    }

    public JFreeChart createChart(XYSeriesCollection eventSeries, XYSeriesCollection durationSeries) {

        final var xAxis = new NumberAxis(msg("philosophers.chart.timeAxisLabel"));
        final var yAxis = new NumberAxis(msg("philosophers.chart.categoryAxisLabel"));
        xAxis.setTickUnit(new NumberTickUnit(1));
        yAxis.setTickUnit(new NumberTickUnit(1));
        yAxis.setRange(1 - CHART_ROWS_MARGIN, settings.getCount() + CHART_ROWS_MARGIN);

        final var plot = new XYPlot(null, yAxis, xAxis, null);
        plot.setOrientation(HORIZONTAL);
        final var chart = new JFreeChart(null, DEFAULT_TITLE_FONT, plot, true);
        chartTheme.apply(chart);
        chart.getLegend().setFrame(BlockBorder.NONE);

        plot.setDataset(0, eventSeries);
        plot.setDataset(1, durationSeries);
        plot.setRenderer(0, shapeRenderer());
        plot.setRenderer(1, lineRenderer());

        plot.setOrientation(HORIZONTAL);
        plot.setDomainGridlinePaint(lightGray);
        plot.setRangeGridlinePaint(lightGray);
        plot.setBackgroundPaint(white);
        plot.setOutlinePaint(gray);

        return chart;
    }

    private XYLineAndShapeRenderer shapeRenderer() {
        final var renderer = new XYLineAndShapeRenderer(false, true);
        renderer.setSeriesPaint(REQUESTED.ordinal(), blue);
        renderer.setSeriesPaint(REJECTED.ordinal(), red);
        renderer.setSeriesPaint(STARTED.ordinal(), green);
        renderer.setSeriesPaint(FINISHED.ordinal(), gray);
        final var shapes = DefaultDrawingSupplier.createStandardSeriesShapes();
        renderer.setSeriesShape(REQUESTED.ordinal(), shapes[1]);
        renderer.setSeriesShape(REJECTED.ordinal(), shapes[2]);
        renderer.setSeriesShape(STARTED.ordinal(), shapes[1]);
        renderer.setSeriesShape(FINISHED.ordinal(), shapes[3]);
        return renderer;
    }

    private XYLineAndShapeRenderer lineRenderer() {
        final var renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setBaseSeriesVisibleInLegend(false);
        renderer.setAutoPopulateSeriesPaint(false);
        renderer.setBasePaint(green);
        return renderer;
    }
}
