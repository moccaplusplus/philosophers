package smb.philosophers.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class FormLabel extends VBox {

    private final Label label = new Label();
    private final Label subLabel = new Label();

    public FormLabel() {
        label.setFont(new Font(16));
        subLabel.setFont(new Font(10));
        subLabel.setWrapText(true);
        subLabel.setTextAlignment(TextAlignment.LEFT);
        getChildren().add(label);
        getChildren().add(subLabel);
    }

    public String getText() {
        return label.getText();
    }

    public void setText(String value) {
        label.setText(value);
    }

    public String getSubText() {
        return subLabel.getText();
    }

    public void setSubText(String value) {
        subLabel.setText(value);
    }
}
