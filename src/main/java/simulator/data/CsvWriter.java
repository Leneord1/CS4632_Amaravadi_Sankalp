package simulator.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class CsvWriter {

    private CsvWriter() {
    }

    public static void writeTimeSeries(Path path, List<TimeSeriesSample> samples) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(TimeSeriesSample.csvHeader());
            writer.newLine();
            for (TimeSeriesSample sample : samples) {
                writer.write(sample.toCsvRow());
                writer.newLine();
            }
        }
    }

    public static void writeEvents(Path path, List<EventRecord> events) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(EventRecord.CSV_HEADER);
            writer.newLine();
            for (EventRecord event : events) {
                writer.write(event.toCsvRow(escape(event.detail())));
                writer.newLine();
            }
        }
    }

    public static String escape(String field) {
        if (field == null) {
            return "";
        }
        boolean needsQuoting = field.contains(",") || field.contains("\"")
                || field.contains("\n") || field.contains("\r");
        if (!needsQuoting) {
            return field;
        }
        return '"' + field.replace("\"", "\"\"") + '"';
    }
}
