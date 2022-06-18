package smb.philosophers.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import smb.philosophers.Settings;
import smb.philosophers.canteen.Distribution;

import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.observableArrayList;
import static smb.philosophers.gui.Gui.initComponent;

public class SettingsForm extends GridPane {

    private static final String LAYOUT = "/settings_form.fxml";

    private static final TextFormatter<?> INT_TEXT_FORMATTER = new TextFormatter<>(change -> change.getControlNewText().matches("^\\d*$") ? change : null);

    private static final TextFormatter<?> DOUBLE_TEXT_FORMATTER = new TextFormatter<>(change -> change.getControlNewText().matches("^\\d*|\\d+\\.\\d*$") ? change : null);

    private static final StringConverter<Double> DOUBLE_STRING_CONVERTER = new StringConverter<>() {

        @Override
        public String toString(Double d) {
            return d == null ? "" : String.valueOf(d);
        }

        @Override
        public Double fromString(String string) {
            try {
                return Double.parseDouble(string);
            } catch (Exception e) {
                return null;
            }
        }
    };

    @FXML
    private TextField timeField;

    @FXML
    private TextField miField;

    @FXML
    private ComboBox<Distribution> distributionCombo;

    @FXML
    private ListView<Double> lambdaList;

    private Settings settings;

    public SettingsForm() {
        initComponent(this, LAYOUT);
        timeField.setTextFormatter(INT_TEXT_FORMATTER);
        miField.setTextFormatter(DOUBLE_TEXT_FORMATTER);
        lambdaList.setItems(observableArrayList());
        lambdaList.setCellFactory(TextFieldListCell.forListView(DOUBLE_STRING_CONVERTER));
        lambdaList.setOnEditCommit(event -> {
            final var items = lambdaList.getItems();
            final var index = event.getIndex();
            final var isLast = index == items.size() - 1;
            if (event.getNewValue() == null) {
                if (!isLast) items.remove(index);
                return;
            }
            items.set(index, event.getNewValue());
            if (isLast) items.add(null);
        });
        lambdaList.setOnKeyPressed(event -> {
            final var code = event.getCode();
            if (code.equals(KeyCode.BACK_SPACE) || code.equals(KeyCode.DELETE)) {
                final var selected = lambdaList.getSelectionModel().getSelectedIndices();
                final var items = lambdaList.getItems();
                selected.forEach(i -> items.remove((int) i));
                if (items.size() == 0 || items.get(items.size() - 1) != null) {
                    items.add(null);
                }
            }
        });
    }

    public Settings getSettings() {
        return Settings.builder()
                .time(Integer.parseInt(timeField.getText()))
                .mi(Double.parseDouble(miField.getText()))
                .distribution(distributionCombo.getValue())
                .lambda(lambdaList.getItems().stream().filter(Objects::nonNull).collect(toList()))
                .build();
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
        timeField.setText(String.valueOf(settings.getTime()));
        miField.setText(String.valueOf(settings.getMi()));
        distributionCombo.setValue(settings.getDistribution());
        final var items = lambdaList.getItems();
        items.clear();
        items.addAll(settings.getLambda());
        items.add(null);
    }
}
