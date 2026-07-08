package simulator.ui;

import javafx.scene.control.TextField;
import org.junit.jupiter.api.Test;
import simulator.ui.view.ConfigView;
import simulator.ui.view.LandingView;
import simulator.ui.view.ResultsView;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigViewTest extends JavaFxTestBase {

    private static ConfigView buildView(RecordingNavigator navigator) {
        AtomicReference<ConfigView> ref = new AtomicReference<>();
        runFx(() -> ref.set(new ConfigView(navigator)));
        return ref.get();
    }

    @Test
    void runButtonWithDefaultsProducesResultsView() throws InterruptedException {
        RecordingNavigator navigator = new RecordingNavigator();
        ConfigView view = buildView(navigator);

        runFx(() -> findButtons(view.getRoot()).get(0).fire());

        long deadline = System.currentTimeMillis() + 15_000;
        while (!(navigator.lastView instanceof ResultsView) && System.currentTimeMillis() < deadline) {
            Thread.sleep(50);
        }
        assertTrue(navigator.lastView instanceof ResultsView);
    }

    @Test
    void invalidNumberShowsErrorWithoutNavigating() {
        RecordingNavigator navigator = new RecordingNavigator();
        ConfigView view = buildView(navigator);

        runFx(() -> {
            List<TextField> fields = findTextFields(view.getRoot());
            fields.get(0).setText("not-a-number");
            findButtons(view.getRoot()).get(0).fire();
        });

        assertEquals(0, navigator.navigateCount);
    }

    @Test
    void invalidParametersShowErrorWithoutNavigating() {
        RecordingNavigator navigator = new RecordingNavigator();
        ConfigView view = buildView(navigator);

        runFx(() -> {
            List<TextField> fields = findTextFields(view.getRoot());
            fields.get(2).setText("0");
            findButtons(view.getRoot()).get(0).fire();
        });

        assertEquals(0, navigator.navigateCount);
    }

    @Test
    void backButtonNavigatesToLanding() {
        RecordingNavigator navigator = new RecordingNavigator();
        ConfigView view = buildView(navigator);

        runFx(() -> findButtons(view.getRoot()).get(1).fire());

        assertTrue(navigator.lastView instanceof LandingView);
    }
}
