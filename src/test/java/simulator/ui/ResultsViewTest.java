package simulator.ui;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import org.junit.jupiter.api.Test;
import simulator.SimulationEngine;
import simulator.config.SimulationConfig;
import simulator.data.DataRecorder;
import simulator.metrics.MetricsReport;
import simulator.ui.view.ConfigView;
import simulator.ui.view.LandingView;
import simulator.ui.view.ResultsView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultsViewTest extends JavaFxTestBase {

    private static RunResult sampleRun() {
        SimulationConfig config = SimulationConfig.builder()
                .simulationHorizonHours(3.0)
                .customerCount(10)
                .randomSeed(11L)
                .build();
        DataRecorder recorder = new DataRecorder();
        SimulationEngine engine = new SimulationEngine(config, recorder);
        engine.run();
        MetricsReport report = engine.getMetrics().buildReport();
        return new RunResult(config, report, recorder, 5L);
    }

    private static List<Button> contentButtons(ResultsView view) {
        Parent content = (Parent) ((ScrollPane) view.getRoot()).getContent();
        return findButtons(content);
    }

    @Test
    void buildsChartsSummaryAndSupportsExportAndNavigation() throws IOException {
        Path resultsRoot = Path.of("results");
        boolean preexisting = Files.exists(resultsRoot);
        RunResult run = sampleRun();
        RecordingNavigator navigator = new RecordingNavigator();

        AtomicReference<ResultsView> ref = new AtomicReference<>();
        runFx(() -> ref.set(new ResultsView(navigator, run)));
        ResultsView view = ref.get();
        assertNotNull(view.getRoot());

        try {
            runFx(() -> contentButtons(view).get(0).fire());
            assertTrue(Files.exists(resultsRoot));

            runFx(() -> contentButtons(view).get(1).fire());
            assertTrue(navigator.lastView instanceof ConfigView);

            runFx(() -> contentButtons(view).get(2).fire());
            assertTrue(navigator.lastView instanceof LandingView);
        } finally {
            if (!preexisting) {
                deleteRecursively(resultsRoot);
            }
        }
    }

    private static void deleteRecursively(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(root)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // Best-effort cleanup of generated export artifacts.
                }
            });
        }
    }
}
