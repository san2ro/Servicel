package com.arr.simple.helpers.profile.utils;

import com.arr.simple.helpers.profile.ConvertValues;

public class DataUsage {

    public static double getProgressUsage(String current, String initial, String type) {
        switch (type) {
            case "DATOS":
            case "DATOS LTE":
            case "BONO DATOS":
            case "DATOS NACIONALES":
                long initialBytes = ConvertValues.convertValues(initial);
                long currentBytes = ConvertValues.convertValues(current);
                return calculateProgress(currentBytes, initialBytes);

            case "MINUTOS":
            case "BONO MINUTOS":
                long initialSeconds = ConvertValues.convertValues(initial);
                long currentSeconds = ConvertValues.convertValues(current);
                return calculateProgress(currentSeconds, initialSeconds);

            case "SMS":
            case "BONO SMS":
                int initialSms = Integer.parseInt(initial);
                int currentSms = Integer.parseInt(current);
                return calculateProgress(currentSms, initialSms);

            default:
                throw new IllegalArgumentException("Tipo de uso desconocido: " + type);
        }
    }

    private static double calculateProgress(long current, long initial) {
        if (initial <= 0) {
            return 100;
        }
        double remaining = Math.max(0, initial - current);
        return (remaining / (double) initial) * 100;
    }
}
