package simulator.ui;

import javafx.scene.control.Button;
import org.junit.jupiter.api.Test;
import simulator.ui.view.ConfigView;
import simulator.ui.view.LandingView;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LandingViewTest extends JavaFxTestBase {

    @Test
    void newSimulationButtonNavigatesToConfigAndExitButtonExits() {
        RecordingNavigator navigator = new RecordingNavigator();
        runFx(() -> {
            LandingView view = new LandingView(navigator);
            assertNotNull(view.getRoot());

            List<Button> buttons = findButtons(view.getRoot());
            Button newRun = buttons.get(0);
            Button exit = buttons.get(1);

            newRun.fire();
            assertTrue(navigator.lastView instanceof ConfigView);

            exit.fire();
            assertTrue(navigator.exitCalled);
        });
    }
}
