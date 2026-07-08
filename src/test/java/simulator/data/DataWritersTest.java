package simulator.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import simulator.SimulationEngine;
import simulator.config.SimulationConfig;
import simulator.metrics.MetricsReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Coverage for CSV/JSON serialization and run-result persistence.
class DataWritersTest {

    @Test
    void csvEscapeHandlesNullPlainAndSpecialCharacters() {
        assertEquals("", CsvWriter.escape(null));
        assertEquals("plain", CsvWriter.escape("plain"));
        assertEquals("\"a,b\"", CsvWriter.escape("a,b"));
        assertEquals("\"say \"\"hi\"\"\"", CsvWriter.escape("say \"hi\""));
        assertEquals("\"line1\nline2\"", CsvWriter.escape("line1\nline2"));
    }

    @Test
    void writeTimeSeriesProducesHeaderAndRows(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("ts.csv");
        TimeSeriesSample sample = new TimeSeriesSample(1000L, 1.5, 2, 3, 1, 1, 20, 5);
        CsvWriter.writeTimeSeries(file, List.of(sample));

        List<String> lines = Files.readAllLines(file);
        assertEquals(TimeSeriesSample.csvHeader(), lines.get(0));
        assertEquals(sample.toCsvRow(), lines.get(1));
    }

    @Test
    void writeEventsEscapesDetailField(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("events.csv");
        EventRecord event = new EventRecord(2.0, "ARRIVAL", 7, "note,with,comma");
        CsvWriter.writeEvents(file, List.of(event));

        List<String> lines = Files.readAllLines(file);
        assertEquals(EventRecord.CSV_HEADER, lines.get(0));
        assertTrue(lines.get(1).contains("\"note,with,comma\""));
    }

    @Test
    void timeSeriesSampleRowMatchesFieldOrder() {
        TimeSeriesSample sample = new TimeSeriesSample(10L, 0.25, 1, 2, 3, 4, 5, 6);
        assertEquals("10,0.25,1,2,3,4,5,6", sample.toCsvRow());
    }

    @Test
    void eventRecordRowUsesEscapedDetail() {
        EventRecord event = new EventRecord(1.0, "SNAPSHOT", -1, "raw");
        assertEquals("1.0,SNAPSHOT,-1,raw", event.toCsvRow("raw"));
    }

    @Test
    void jsonQuoteEscapesControlCharactersAndNull() {
        assertEquals("null", JsonWriter.quote(null));
        assertEquals(
                "\"tab\\tnewline\\nquote\\\"slash\\\\\"",
                JsonWriter.quote("tab\tnewline\nquote\"slash\\"));
        assertTrue(JsonWriter.quote("carriage\r").contains("\\r"));
    }

    @Test
    void jsonObjectSerializesAllValueTypesAndNesting(@TempDir Path dir) throws IOException {
        JsonWriter.JsonObject nested = JsonWriter.object().put("flag", true);
        JsonWriter.JsonObject root =
                JsonWriter.object()
                        .put("name", "engine")
                        .put("ratio", 0.5)
                        .put("count", 3)
                        .put("id", 42L)
                        .put("child", nested);

        String json = root.toString();
        assertTrue(json.contains("\"name\": \"engine\""));
        assertTrue(json.contains("\"ratio\": 0.5"));
        assertTrue(json.contains("\"count\": 3"));
        assertTrue(json.contains("\"id\": 42"));
        assertTrue(json.contains("\"flag\": true"));

        Path file = dir.resolve("out.json");
        JsonWriter.write(file, json);
        assertEquals(json, Files.readString(file));
    }

    @Test
    void dataRecorderTracksSamplesAndEvents() {
        DataRecorder recorder = new DataRecorder();
        recorder.recordSample(new TimeSeriesSample(1L, 0.0, 0, 0, 0, 0, 0, 0));
        recorder.recordEvent(new EventRecord(0.0, "ARRIVAL", 1, "x"));

        assertEquals(1, recorder.getSampleCount());
        assertEquals(1, recorder.getEventCount());
        assertEquals(1, recorder.getSamples().size());
        assertEquals(1, recorder.getEvents().size());
    }

    @Test
    void createSessionAndWriteRunPersistAllArtifacts(@TempDir Path root) throws IOException {
        SimulationConfig config =
                SimulationConfig.builder()
                        .simulationHorizonHours(3.0)
                        .customerCount(10)
                        .randomSeed(11L)
                        .build();
        DataRecorder recorder = new DataRecorder();
        SimulationEngine engine = new SimulationEngine(config, recorder);
        engine.run();
        MetricsReport report = engine.getMetrics().buildReport();

        RunResultWriter writer = RunResultWriter.createSession(root);
        assertTrue(Files.isDirectory(writer.getSessionDir()));

        writer.writeRun(1, "baseline", "none", 1500L, config, report, recorder);

        Path base = writer.getSessionDir();
        assertTrue(Files.exists(base.resolve("run_001_timeseries.csv")));
        assertTrue(Files.exists(base.resolve("run_001_events.csv")));
        assertTrue(Files.exists(base.resolve("run_001_summary.json")));
        assertTrue(Files.exists(base.resolve("run_001_config.json")));

        List<String> masterLines = Files.readAllLines(base.resolve("master_index.csv"));
        assertEquals(2, masterLines.size());
        assertTrue(masterLines.get(1).startsWith("1,baseline,none,"));

        String summary = Files.readString(base.resolve("run_001_summary.json"));
        assertTrue(summary.contains("\"runId\": 1"));
        assertTrue(summary.contains("validation"));
    }
}
