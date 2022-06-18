package smb.philosophers.gui;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import smb.philosophers.Settings;
import smb.philosophers.canteen.Canteen;
import smb.philosophers.canteen.report.Reporter;

import static smb.philosophers.gui.Gui.initComponent;

public class MainScreen extends SplitPane {

    private static final String LAYOUT = "/main_screen.fxml";
    private static final String EXT_FILTER = "*.json";
    private static final String REPORT_PATH = "./report.pdf";

    private final Object syncLock = new Object();

    private final HostServices hostServices;

    private final Settings settings;

    @FXML
    private SettingsForm form;

    @FXML
    private TextArea console;

    @FXML
    private Button resetButton;

    @FXML
    private Button startButton;

    @FXML
    private Button openReportButton;

    public MainScreen(Settings settings, HostServices hostServices) {
        initComponent(this, LAYOUT);
        this.settings = settings;
        this.hostServices = hostServices;
        form.setSettings(settings);
        openReportButton.setDisable(true);
        openReportButton.setOnAction(this::openReport);
        resetButton.setOnAction(this::resetForm);
        startButton.setOnAction(this::execute);
    }

    private void resetForm(ActionEvent e) {
        form.setSettings(settings);
    }

    private void openReport(ActionEvent e) {
        hostServices.showDocument(Reporter.getReportPath().toString());
    }

    private void execute(ActionEvent e) {
        clear();
        startButton.setDisable(true);
        openReportButton.setDisable(true);
        final var settings = form.getSettings();
        final var worker = new Thread(() -> {
            try {
                settings.validate();
                Canteen.run(settings, this::logLine);
                runOnFxApplicationThread(() -> openReportButton.setDisable(false));
            } catch (Exception error) {
                logError(error);
            }
            runOnFxApplicationThread(() -> startButton.setDisable(false));
        });
        worker.setDaemon(true);
        worker.start();
    }

    private void logError(Throwable error) {
        final var msg = error.getMessage();
        logLine(msg == null || msg.isEmpty() || msg.isBlank() ? error.getClass().getName() : msg);
    }

    private void logLine(String line) {
        log(line + "\n");
    }

    private void log(String text) {
        runOnFxApplicationThread(() -> {
            synchronized (syncLock) {
                console.appendText(text);
            }
        });
    }

    private void clear() {
        runOnFxApplicationThread(() -> {
            synchronized (syncLock) {
                console.clear();
            }
        });
    }

    private void runOnFxApplicationThread(Runnable runnable) {
        if (Platform.isFxApplicationThread()) runnable.run();
        else Platform.runLater(runnable);
    }
}