package simulator.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import simulator.ui.view.LandingView;
import simulator.ui.view.View;

import java.util.Objects;

// JavaFX entry point. Owns the primary stage and swaps view scenes.
public class SimulatorApp extends Application implements Navigator {
    private static final int WINDOW_WIDTH = 960;
    private static final int WINDOW_HEIGHT = 640;
    private static final String STYLESHEET = "/ui/global.css";

    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        primaryStage.setTitle("Service Department Optimization Simulator");
        primaryStage.setMinWidth(WINDOW_WIDTH);
        primaryStage.setMinHeight(WINDOW_HEIGHT);
        navigateTo(new LandingView(this));
        primaryStage.show();
    }

    @Override
    public void navigateTo(View view) {
        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(view.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(STYLESHEET)).toExternalForm());
            stage.setScene(scene);
        } else {
            scene.setRoot(view.getRoot());
        }
    }

    @Override
    public void exit() {
        stage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
