package smb.philosophers.canteen.report;

import org.jfree.chart.ChartTheme;
import org.jfree.chart.StandardChartTheme;
import smb.philosophers.Settings;
import smb.philosophers.canteen.event.ClockEvent;
import smb.philosophers.canteen.event.EventBus;
import smb.philosophers.canteen.event.MealEvent;

import java.awt.Font;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static smb.philosophers.Messages.msg;
import static smb.philosophers.canteen.event.MealEvent.Type.FINISHED;
import static smb.philosophers.canteen.event.MealEvent.Type.REJECTED;
import static smb.philosophers.canteen.event.MealEvent.Type.STARTED;

public class Reporter {

    private static final String OUTPUT_PATH = "./report.pdf";
    private static final String FONT_FAMILY = "Times New Roman";
    private static final String DEFAULT_THEME_NAME = "DEFAULT_THEME";

    public static Path getReportPath() {
        return Paths.get(OUTPUT_PATH).toAbsolutePath().normalize();
    }

    private final Settings settings;
    private final Logger logger;
    private final Plotter plotter;
    private final Printer printer = new Printer();
    private final List<MealEvent> events = new ArrayList<>();
    private final List<String> logs = new ArrayList<>();

    public Reporter(Settings settings, EventBus eventBus, Logger logger) {
        this.settings = settings;
        this.logger = logger;
        this.plotter = new Plotter(settings, chartTheme());
        eventBus.addListener(ClockEvent.class, this::onClockEvent);
        eventBus.addListener(1, MealEvent.class, this::onMealEvent);
    }

    private ChartTheme chartTheme() {
        final var theme = new StandardChartTheme(DEFAULT_THEME_NAME);
        theme.setExtraLargeFont(rewriteFont(theme.getExtraLargeFont()));
        theme.setLargeFont(rewriteFont(theme.getLargeFont()));
        theme.setRegularFont(rewriteFont(theme.getRegularFont()));
        theme.setSmallFont(rewriteFont(theme.getSmallFont()));
        return theme;
    }

    private Font rewriteFont(Font source) {
        return new Font(FONT_FAMILY, source.getStyle(), source.getSize());
    }

    private void onClockEvent(ClockEvent event) {
        switch (event.getType()) {
            case CLOCK_START:
                logSettings();
                log(msg("philosophers.log.clock.started", event.getTimestamp()));
                break;
            case CLOCK_END:
                log(msg("philosophers.log.clock.stopped", event.getTimestamp()));
                printReport();
                break;
        }
    }

    private void onMealEvent(MealEvent event) {
        events.add(event);
        switch (event.getType()) {
            case REQUESTED:
                log(msg("philosophers.log.meal.requested", event.getTimestamp(), event.getTarget() + 1));
                break;
            case STARTED:
                log(msg("philosophers.log.meal.started", event.getTimestamp(), event.getTarget() + 1));
                break;
            case REJECTED:
                log(msg("philosophers.log.meal.rejected", event.getTimestamp(), event.getTarget() + 1));
                break;
            case FINISHED:
                log(msg("philosophers.log.meal.finished", event.getTimestamp(), event.getTarget() + 1));
                break;
        }
    }

    private void logSettings() {
        logger.log(msg("philosophers.program.title"));
        logger.log("-----");
        logger.log(msg("philosophers.settings.n.log", settings.getCount()));
        logger.log(msg("philosophers.settings.t.log", settings.getTime()));
        logger.log(msg("philosophers.settings.mi.log", settings.getMi()));
        logger.log(msg("philosophers.settings.lambda.log",
                settings.getLambda().stream().map(String::valueOf).collect(joining(", ", "[", "]"))));
        logger.log(msg("philosophers.settings.distribution.log", settings.getDistribution()));
        logger.log("-----");
    }

    private void printReport() {
        logger.log("-----");
        logger.log(msg("philosophers.log.report.writing"));
        try {
            final var reportPath = getReportPath();
            printer.print(reportPath, prepareContext());
            logger.log(msg("philosophers.log.report.created", reportPath));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Map<String, Object> prepareContext() {
        final var context = new HashMap<String, Object>();
        final var stats = buildStats();
        context.put("settings", settings);
        context.put("plot", plotter.plot(events));
        context.put("logs", logs);
        context.put("stats", stats);
        context.put("total", totalStats(stats));
        context.put("fontFamily", FONT_FAMILY);
        return context;
    }

    private Map<Integer, Map<String, Object>> buildStats() {
        final var stats = new HashMap<Integer, Map<String, Object>>();
        events.stream().collect(groupingBy(MealEvent::getTarget)).forEach((target, byTarget) -> {
            final var statsEntry = new HashMap<String, Object>();
            final var byType = byTarget.stream().collect(groupingBy(MealEvent::getType));
            final var rejected = byType.get(REJECTED);
            final var started = byType.get(STARTED);
            if (rejected != null) {
                statsEntry.put("rejected", rejected.size());
                statsEntry.put("firstRejected", rejected.get(0).getTimestamp());
            }
            if (started != null) {
                statsEntry.put("accepted", started.size());
                statsEntry.put("firstAccepted", started.get(0).getTimestamp());
                final var finished = byType.get(FINISHED);
                if (finished != null) {
                    statsEntry.put("mealTime",
                            finished.stream().mapToDouble(MealEvent::getTimestamp).sum()
                                    - started.stream().mapToDouble(MealEvent::getTimestamp).sum()
                                    + (finished.size() < started.size() ? settings.getTime() : 0.0));
                }
            }
            stats.put(target, statsEntry);
        });
        return stats;
    }

    private Map<String, Object> totalStats(Map<Integer, Map<String, Object>> stats) {
        final Function<String, Stream<Object>> mapper = key -> stats.values().stream().map(e -> e.get(key)).filter(Objects::nonNull);
        return Map.of(
                "rejected", mapper.apply("rejected").mapToInt(Integer.class::cast).sum(),
                "accepted", mapper.apply("accepted").mapToInt(Integer.class::cast).sum(),
                "mealTime", mapper.apply("mealTime").mapToDouble(Double.class::cast).sum(),
                "firstRejected", mapper.apply("firstRejected").mapToDouble(Double.class::cast).min().orElse(0),
                "firstAccepted", mapper.apply("firstAccepted").mapToDouble(Double.class::cast).min().orElse(0));
    }

    private void log(String msg) {
        logger.log(msg);
        logs.add(msg);
    }
}
