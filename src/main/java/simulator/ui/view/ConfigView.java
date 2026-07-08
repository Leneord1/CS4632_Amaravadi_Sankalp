package simulator.ui.view;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import simulator.SimulationEngine;
import simulator.config.SimulationConfig;
import simulator.data.DataRecorder;
import simulator.ui.Navigator;
import simulator.ui.RunResult;

public class ConfigView implements View {
    private final Navigator navigator;
    private final VBox root;

    private final TextField horizonField = new TextField();
    private final TextField arrivalField = new TextField();
    private final TextField technicianField = new TextField();
    private final TextField serviceField = new TextField();
    private final TextField advisorField = new TextField();
    private final TextField seedField = new TextField();
    private final Label statusLabel = new Label();
    private final Button runButton = new Button("Run Simulation");

    public ConfigView(Navigator navigator) {
        this.navigator = navigator;
        this.root = build();
    }

    private VBox build() {
        SimulationConfig defaults = SimulationConfig.defaults();
        horizonField.setText(Double.toString(defaults.getSimulationHorizonHours()));
        arrivalField.setText(Double.toString(defaults.getArrivalRate()));
        technicianField.setText(Integer.toString(defaults.getTechnicianCount()));
        serviceField.setText(Double.toString(defaults.getServiceRatePerTechnician()));
        advisorField.setText(Integer.toString(defaults.getAdvisorCount()));
        seedField.setText(Long.toString(defaults.getRandomSeed()));

        Label title = new Label("Configure Simulation");
        title.getStyleClass().add("app-title");

        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(12);
        form.setAlignment(Pos.CENTER);
        addRow(form, 0, "Horizon (hours)", horizonField);
        addRow(form, 1, "Arrival rate (lambda, /h)", arrivalField);
        addRow(form, 2, "Technicians", technicianField);
        addRow(form, 3, "Service rate (mu, /h)", serviceField);
        addRow(form, 4, "Advisors", advisorField);
        addRow(form, 5, "Random seed", seedField);

        runButton.getStyleClass().addAll("btn", "primary");
        runButton.setOnAction(e -> runSimulation());

        Button back = new Button("Back");
        back.getStyleClass().add("btn");
        back.setOnAction(e -> navigator.navigateTo(new LandingView(navigator)));

        HBox actions = new HBox(16, runButton, back);
        actions.setAlignment(Pos.CENTER);

        statusLabel.getStyleClass().add("app-subtitle");

        VBox layout = new VBox(22, title, form, actions, statusLabel);
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().addAll("app-root", "config-root");
        return layout;
    }

    private void addRow(GridPane grid, int row, String label, TextField field) {
        Label caption = new Label(label);
        caption.getStyleClass().add("form-label");
        field.setPrefWidth(160);
        field.getStyleClass().add("form-field");
        grid.add(caption, 0, row);
        grid.add(field, 1, row);
    }

    private void runSimulation() {
        SimulationConfig config;
        try {
            config = buildConfig();
        } catch (NumberFormatException ex) {
            statusLabel.setText("Enter valid numbers for every field.");
            return;
        } catch (IllegalArgumentException ex) {
            statusLabel.setText("Invalid parameters: " + ex.getMessage());
            return;
        }

        runButton.setDisable(true);
        statusLabel.setText("Running simulation...");

        Task<RunResult> task = new Task<>() {

            @Override
            protected RunResult call() {
                DataRecorder recorder = new DataRecorder();
                long start = System.nanoTime();
                SimulationEngine engine = new SimulationEngine(config, recorder);
                engine.run();
                long millis = (System.nanoTime() - start) / 1_000_000L;
                return new RunResult(config, engine.getMetrics().buildReport(), recorder, millis);
            }
        };
        task.setOnSucceeded(e -> navigator.navigateTo(new ResultsView(navigator, task.getValue())));
        task.setOnFailed(e -> {
            runButton.setDisable(false);
            Throwable error = task.getException();
            statusLabel.setText("Run failed: " + (error == null ? "unknown error" : error.getMessage()));
        });

        Thread worker = new Thread(task, "simulation-run");
        worker.setDaemon(true);
        worker.start();
    }

    private SimulationConfig buildConfig() {
        return SimulationConfig.builder()
                .simulationHorizonHours(Double.parseDouble(horizonField.getText().trim()))
                .arrivalRate(Double.parseDouble(arrivalField.getText().trim()))
                .technicianCount(Integer.parseInt(technicianField.getText().trim()))
                .serviceRatePerTechnician(Double.parseDouble(serviceField.getText().trim()))
                .advisorCount(Integer.parseInt(advisorField.getText().trim()))
                .randomSeed(Long.parseLong(seedField.getText().trim()))
                .build();
    }

    @Override
    public Parent getRoot() {
        return root;
    }
}
