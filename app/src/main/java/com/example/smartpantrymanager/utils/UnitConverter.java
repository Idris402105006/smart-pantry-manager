package com.example.smartpantrymanager.utils;

import java.util.Locale;

public class UnitConverter {

    private UnitConverter() {
        // Utility class - prevent instantiation.
    }

    /**
     * Determines whether two units belong
     * to the same measurement family.
     */
    public static boolean areCompatible(
            String firstUnit,
            String secondUnit) {

        String first =
                normaliseUnit(firstUnit);

        String second =
                normaliseUnit(secondUnit);

        if (first.equals(second)) {
            return true;
        }

        return isMassUnit(first)
                && isMassUnit(second)
                || isVolumeUnit(first)
                && isVolumeUnit(second);
    }

    /**
     * Converts a quantity into a common base unit.
     *
     * Mass:
     * kilograms -> grams
     *
     * Volume:
     * litres      -> millilitres
     * cups        -> millilitres
     * tablespoons -> millilitres
     * teaspoons   -> millilitres
     *
     * Count:
     * pieces remains pieces
     */
    public static double toBaseUnit(
            double quantity,
            String unit) {

        String normalised =
                normaliseUnit(unit);

        switch (normalised) {

            case "kilograms":
                return quantity * 1000.0;

            case "grams":
                return quantity;

            case "litres":
                return quantity * 1000.0;

            case "millilitres":
                return quantity;

            case "cups":
                return quantity * 250.0;

            case "tablespoons":
                return quantity * 15.0;

            case "teaspoons":
                return quantity * 5.0;

            case "pieces":
                return quantity;

            default:
                return quantity;
        }
    }

    /**
     * Compare an available pantry quantity
     * against a required recipe quantity.
     */
    public static boolean hasEnough(
            double pantryQuantity,
            String pantryUnit,
            double requiredQuantity,
            String requiredUnit) {

        if (!areCompatible(
                pantryUnit,
                requiredUnit)) {

            return false;
        }

        double available =
                toBaseUnit(
                        pantryQuantity,
                        pantryUnit
                );

        double required =
                toBaseUnit(
                        requiredQuantity,
                        requiredUnit
                );

        return available >= required;
    }

    private static boolean isMassUnit(
            String unit) {

        return unit.equals("grams")
                || unit.equals("kilograms");
    }

    private static boolean isVolumeUnit(
            String unit) {

        return unit.equals("millilitres")
                || unit.equals("litres")
                || unit.equals("cups")
                || unit.equals("tablespoons")
                || unit.equals("teaspoons");
    }

    private static String normaliseUnit(
            String unit) {

        if (unit == null) {
            return "";
        }

        String normalised =
                unit.trim()
                        .toLowerCase(Locale.ROOT);

        switch (normalised) {

            case "piece":
            case "pieces":
                return "pieces";

            case "gram":
            case "grams":
            case "g":
                return "grams";

            case "kilogram":
            case "kilograms":
            case "kg":
                return "kilograms";

            case "millilitre":
            case "millilitres":
            case "ml":
                return "millilitres";

            case "litre":
            case "litres":
            case "l":
                return "litres";

            case "cup":
            case "cups":
                return "cups";

            case "tablespoon":
            case "tablespoons":
            case "tbsp":
                return "tablespoons";

            case "teaspoon":
            case "teaspoons":
            case "tsp":
                return "teaspoons";

            default:
                return normalised;
        }
    }
    public static double convert(
            double quantity,
            String fromUnit,
            String toUnit) {

        if (!areCompatible(
                fromUnit,
                toUnit)) {

            throw new IllegalArgumentException(
                    "Incompatible units: "
                            + fromUnit
                            + " and "
                            + toUnit
            );
        }

        double baseQuantity =
                toBaseUnit(
                        quantity,
                        fromUnit
                );

        String target =
                normaliseUnit(toUnit);

        switch (target) {

            case "kilograms":
                return baseQuantity / 1000.0;

            case "grams":
                return baseQuantity;

            case "litres":
                return baseQuantity / 1000.0;

            case "millilitres":
                return baseQuantity;

            case "cups":
                return baseQuantity / 250.0;

            case "tablespoons":
                return baseQuantity / 15.0;

            case "teaspoons":
                return baseQuantity / 5.0;

            case "pieces":
                return baseQuantity;

            default:
                return baseQuantity;
        }
    }
}