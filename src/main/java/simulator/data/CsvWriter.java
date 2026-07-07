package simulator.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Writes buffered time-series and event data to CSV files.
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
            writer.write(EventRecord.csvHeader());
            writer.newLine();
            for (EventRecord event : events) {
                writer.write(event.toCsvRow(escape(event.detail())));
                writer.newLine();
            }
        }
    }

    // Quotes a field when it contains a comma, quote, or newline (RFC 4180 style).
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
