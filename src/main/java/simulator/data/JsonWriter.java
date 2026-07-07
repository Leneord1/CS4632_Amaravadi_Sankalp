package simulator.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class JsonWriter {

    private JsonWriter() {
    }

    public static JsonObject object() {
        return new JsonObject();
    }

    public static void write(Path path, String json) throws IOException {
        Files.writeString(path, json, StandardCharsets.UTF_8);
    }

    static String quote(String value) {
        if (value == null) {
            return "null";
        }
        StringBuilder builder = new StringBuilder(value.length() + 2);
        builder.append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"' -> builder.append("\\\"");
                case '\\' -> builder.append("\\\\");
                case '\n' -> builder.append("\\n");
                case '\r' -> builder.append("\\r");
                case '\t' -> builder.append("\\t");
                default -> builder.append(c);
            }
        }
        builder.append('"');
        return builder.toString();
    }

    public static final class JsonObject {
        private final Map<String, String> entries = new LinkedHashMap<>();

        public JsonObject put(String key, String value) {
            entries.put(key, quote(value));
            return this;
        }

        public JsonObject put(String key, double value) {
            entries.put(key, Double.toString(value));
            return this;
        }

        public JsonObject put(String key, long value) {
            entries.put(key, Long.toString(value));
            return this;
        }

        public JsonObject put(String key, int value) {
            entries.put(key, Integer.toString(value));
            return this;
        }

        public JsonObject put(String key, boolean value) {
            entries.put(key, Boolean.toString(value));
            return this;
        }

        public JsonObject put(String key, JsonObject nested) {
            entries.put(key, nested.toJson(0));
            return this;
        }

        public String toJson(int indentLevel) {
            String pad = "  ".repeat(indentLevel + 1);
            String closePad = "  ".repeat(indentLevel);
            StringBuilder builder = new StringBuilder("{\n");
            int index = 0;
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                builder.append(pad)
                        .append(quote(entry.getKey()))
                        .append(": ")
                        .append(entry.getValue());
                if (++index < entries.size()) {
                    builder.append(',');
                }
                builder.append('\n');
            }
            builder.append(closePad).append('}');
            return builder.toString();
        }

        @Override
        public String toString() {
            return toJson(0);
        }
    }
}
