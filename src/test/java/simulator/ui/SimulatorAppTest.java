package simulator.ui;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import simulator.ui.view.ConfigView;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimulatorAppTest extends JavaFxTestBase {

    @Test
    void startCreatesSceneAndNavigationSwapsRoots() {
        runFx(() -> {
            SimulatorApp app = new SimulatorApp();
            Stage stage = new Stage();

            app.start(stage);
            assertNotNull(stage.getScene());

            app.navigateTo(new ConfigView(app));
            assertNotNull(stage.getScene().getRoot());

            app.exit();
        });
    }
}
