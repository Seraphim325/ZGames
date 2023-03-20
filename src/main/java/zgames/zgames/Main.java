package zgames.zgames;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/start.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("css/style.css")).toExternalForm());
        initStage(stage, scene);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static void initStage(Stage stage, Scene scene) {
        stage.setTitle("Сводки МО РФ");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}