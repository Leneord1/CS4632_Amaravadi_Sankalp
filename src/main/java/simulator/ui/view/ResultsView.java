package simulator.ui.view;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import simulator.config.SimulationConfig;
import simulator.data.RunResultWriter;
import simulator.data.TimeSeriesSample;
import simulator.metrics.MetricsReport;
import simulator.model.RepairBay;
import simulator.model.Technician;
import simulator.ui.Navigator;
import simulator.ui.RunResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ResultsView implements View {
    private static final Path RESULTS_ROOT = Path.of("results");

    private final Navigator navigator;
    private final RunResult result;
    private final Label exportStatus = new Label();
    private final ScrollPane root;

    public ResultsView(Navigator navigator, RunResult result) {
        this.navigator = navigator;
        this.result = result;
        this.root = build();
    }

    private ScrollPane build() {
        Label title = new Label("Simulation Results");
        title.getStyleClass().add("app-title");

        VBox layout =
                new VBox(
                        22,
                        title,
                        buildSummary(),
                        buildUtilizationChart(),
                        buildQueueChart(),
                        buildActions(),
                        exportStatus);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.getStyleClass().addAll("app-root", "results-root");

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("results-scroll");
        return scroll;
    }

    private GridPane buildSummary() {
        MetricsReport report = result.report();
        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(8);
        grid.setAlignment(Pos.CENTER);

        int row = 0;
        row =
                addMetric(
                        grid,
                        row,
                        "Jobs completed",
                        Integer.toString(report.getJobsCompletedPerDay()));
        row =
                addMetric(
                        grid,
                        row,
                        "Avg customer wait (h)",
                        fmt(report.getAverageCustomerWaitTime()));
        row = addMetric(grid, row, "Avg service time (h)", fmt(report.getAverageServiceTime()));
        row =
                addMetric(
                        grid,
                        row,
                        "Shop technician utilization",
                        fmt(report.getSimulatedShopTechnicianUtilization()));
        row =
                addMetric(
                        grid,
                        row,
                        "Shop bay utilization",
                        fmt(report.getSimulatedShopBayUtilization()));
        row = addMetric(grid, row, "Analytical rho", fmt(report.getAnalyticalSystemUtilization()));
        row =
                addMetric(
                        grid,
                        row,
                        "Analytical queue wait Wq (h)",
                        fmt(report.getAnalyticalQueueWait()));
        addMetric(grid, row, "Run time (ms)", Long.toString(result.wallClockMillis()));
        return grid;
    }

    private int addMetric(GridPane grid, int row, String label, String value) {
        Label caption = new Label(label);
        caption.getStyleClass().add("metric-label");
        Label figure = new Label(value);
        figure.getStyleClass().add("metric-value");
        grid.add(caption, 0, row);
        grid.add(figure, 1, row);
        return row + 1;
    }

    private BarChart<String, Number> buildUtilizationChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Resource");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Utilization");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Resource Utilization");
        chart.setLegendVisible(true);
        chart.setPrefHeight(280);

        XYChart.Series<String, Number> techSeries = new XYChart.Series<>();
        techSeries.setName("Technicians");
        for (Map.Entry<Technician, Double> entry :
                sortedByKey(
                        result.report().getTechnicianUtilization(),
                        e -> e.getKey().getTechnicianId())) {
            techSeries
                    .getData()
                    .add(
                            new XYChart.Data<>(
                                    "T" + entry.getKey().getTechnicianId(), entry.getValue()));
        }

        XYChart.Series<String, Number> baySeries = new XYChart.Series<>();
        baySeries.setName("Bays");
        for (Map.Entry<RepairBay, Double> entry :
                sortedByKey(result.report().getBayUtilization(), e -> e.getKey().getBayId())) {
            baySeries
                    .getData()
                    .add(new XYChart.Data<>("B" + entry.getKey().getBayId(), entry.getValue()));
        }

        chart.getData().add(techSeries);
        chart.getData().add(baySeries);
        return chart;
    }

    private LineChart<Number, Number> buildQueueChart() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Simulation time (h)");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Job queue depth");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Queue Depth Over Time");
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
        chart.setPrefHeight(280);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (TimeSeriesSample sample : result.recorder().getSamples()) {
            series.getData().add(new XYChart.Data<>(sample.simTimeHours(), sample.jobQueueDepth()));
        }
        chart.getData().add(series);
        return chart;
    }

    private HBox buildActions() {
        Button export = new Button("Export CSV/JSON");
        export.getStyleClass().addAll("btn", "primary");
        export.setOnAction(e -> exportRun());

        Button newRun = new Button("New Run");
        newRun.getStyleClass().add("btn");
        newRun.setOnAction(e -> navigator.navigateTo(new ConfigView(navigator)));

        Button home = new Button("Home");
        home.getStyleClass().add("btn");
        home.setOnAction(e -> navigator.navigateTo(new LandingView(navigator)));

        HBox actions = new HBox(16, export, newRun, home);
        actions.setAlignment(Pos.CENTER);
        return actions;
    }

    private void exportRun() {
        try {
            RunResultWriter writer = RunResultWriter.createSession(RESULTS_ROOT);
            writer.writeRun(
                    1,
                    "UI run",
                    describeParams(result.config()),
                    result.wallClockMillis(),
                    result.config(),
                    result.report(),
                    result.recorder());
            exportStatus.setText("Exported to " + writer.getSessionDir().toAbsolutePath());
        } catch (IOException ex) {
            exportStatus.setText("Export failed: " + ex.getMessage());
        }
    }

    private static String describeParams(SimulationConfig config) {
        return String.format(
                Locale.US,
                "horizon=%.1f, lambda=%.2f, technicians=%d, mu=%.2f, advisors=%d, seed=%d",
                config.getSimulationHorizonHours(),
                config.getArrivalRate(),
                config.getTechnicianCount(),
                config.getServiceRatePerTechnician(),
                config.getAdvisorCount(),
                config.getRandomSeed());
    }

    private static <K> List<Map.Entry<K, Double>> sortedByKey(
            Map<K, Double> map,
            java.util.function.ToIntFunction<Map.Entry<K, Double>> keyExtractor) {
        List<Map.Entry<K, Double>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Comparator.comparingInt(keyExtractor));
        return entries;
    }

    private static String fmt(double value) {
        return String.format(Locale.US, "%.3f", value);
    }

    @Override
    public Parent getRoot() {
        return root;
    }
}
