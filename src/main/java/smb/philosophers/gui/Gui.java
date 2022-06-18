package smb.philosophers.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import smb.philosophers.Settings;

import java.io.IOException;

import static java.lang.System.exit;
import static smb.philosophers.Messages.getBundle;
import static smb.philosophers.Messages.msg;

public class Gui extends Application {

    public static Settings settings;

    public static <T> T initComponent(Region component, String layout) {
        final var fxmlLoader = new FXMLLoader(component.getClass().getResource(layout), getBundle());
        fxmlLoader.setRoot(component);
        fxmlLoader.setController(component);
        try {
            return fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void start(Stage stage) {
        final var mainScreen = new MainScreen(settings, getHostServices());
        final var scene = new Scene(mainScreen);
        stage.setScene(scene);
        stage.setTitle(msg("philosophers.program.title"));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        exit(0);
    }
}
