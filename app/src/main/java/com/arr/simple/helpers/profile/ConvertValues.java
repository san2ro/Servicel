package com.arr.simple.helpers.profile;

public class ConvertValues {

    // Tipos de datos que manejamos
    private enum DataType {
        DATA, // Para GB, MB, KB, B
        TIME, // Para HH:MM:SS
        COUNT // Para valores numéricos simples (SMS)
    }

    /**
     * Convierte diferentes formatos de valores a una representación numérica comparable
     *
     * @param value El valor a convertir (ej: "2.5 GB", "06:38:27", "432")
     * @return El valor convertido en la unidad base adecuada (bytes, segundos o cantidad)
     */
    public static long convertValues(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0L;
        }

        DataType type = determineDataType(value);

        try {
            return switch (type) {
                case DATA -> parseDataValue(value);
                case TIME -> parseTimeValue(value);
                case COUNT -> parseCountValue(value);
            };
        } catch (Exception e) {
            return 0L; // Manejo seguro de errores
        }
    }

    private static DataType determineDataType(String value) {
        if (value.contains(":")) return DataType.TIME;
        if (value.matches("^\\d+$")) return DataType.COUNT;
        if (value.matches(".*\\s+[GMK]?B$")) return DataType.DATA;
        throw new IllegalArgumentException("Formato no reconocido: " + value);
    }

    private static long parseDataValue(String dataValue) {
        String[] parts = dataValue.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato de datos incorrecto: " + dataValue);
        }

        double number = Double.parseDouble(parts[0]);
        String unit = parts[1].toUpperCase();

        return switch (unit) {
            case "GB" -> (long) (number * 1024 * 1024 * 1024);
            case "MB" -> (long) (number * 1024 * 1024);
            case "KB" -> (long) (number * 1024);
            case "B" -> (long) number;
            default -> throw new IllegalArgumentException("Unidad de datos desconocida: " + unit);
        };
    }

    private static long parseTimeValue(String timeValue) {
        if (timeValue == null || timeValue.trim().isEmpty()) {
            return 0;
        }
        try {
            // Divide el tiempo en partes
            String[] parts = timeValue.split(":");

            // Valida las partes y convierte según el formato
            if (parts.length == 2) {
                // Formato MM:SS
                long minutes = parseLongSafe(parts[0]);
                long seconds = parseLongSafe(parts[1]);
                return minutes * 60 + seconds;
            } else if (parts.length == 3) {
                // Formato HH:MM:SS
                long hours = parseLongSafe(parts[0]);
                long minutes = parseLongSafe(parts[1]);
                long seconds = parseLongSafe(parts[2]);
                return hours * 3600 + minutes * 60 + seconds;
            }
        } catch (Exception e) {
            // Registra el error para depuración
            System.err.println(
                    "Error al convertir el tiempo: " + timeValue + " - " + e.getMessage());
        }
        return 0;
    }

    private static long parseLongSafe(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return 0; // Valor predeterminado si no se puede convertir
        }
    }

    private static long parseCountValue(String countValue) {
        return Long.parseLong(countValue);
    }
}

/*
public static long convertToSeconds(String time) {
        // Verifica si la entrada es nula o vacía
        if (time == null || time.trim().isEmpty()) {
            return 0; // Devuelve un valor predeterminado
        }

        try {
            // Divide el tiempo en partes
            String[] parts = time.split(":");

            // Valida las partes y convierte según el formato
            if (parts.length == 2) {
                // Formato MM:SS
                long minutes = parseLongSafe(parts[0]);
                long seconds = parseLongSafe(parts[1]);
                return minutes * 60 + seconds;
            } else if (parts.length == 3) {
                // Formato HH:MM:SS
                long hours = parseLongSafe(parts[0]);
                long minutes = parseLongSafe(parts[1]);
                long seconds = parseLongSafe(parts[2]);
                return hours * 3600 + minutes * 60 + seconds;
            }
        } catch (Exception e) {
            // Registra el error para depuración
            System.err.println("Error al convertir el tiempo: " + time + " - " + e.getMessage());
        }

        // Devuelve un valor predeterminado en caso de error
        return 0;
    }
    */
