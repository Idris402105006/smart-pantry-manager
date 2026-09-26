package com.example.smartpantrymanager.utils;

import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.RecipeIngredient;

import java.util.List;
import java.util.Locale;

public class IngredientMatcher {

    private IngredientMatcher() {
        // Prevent this utility class from being instantiated.
    }


    public static boolean canMakeRecipe(
            List<PantryItem> pantryItems,
            List<RecipeIngredient> requiredIngredients) {

        if (requiredIngredients == null
                || requiredIngredients.isEmpty()) {

            return false;
        }

        for (RecipeIngredient required :
                requiredIngredients) {

            PantryItem matchingPantryItem =
                    findMatchingPantryItem(
                            pantryItems,
                            required
                    );

            // Missing ingredient.
            if (matchingPantryItem == null) {
                return false;
            }

            // Unit must match.
            if (!UnitConverter.hasEnough(
                    matchingPantryItem.getQuantity(),
                    matchingPantryItem.getUnit(),
                    required.getQuantity(),
                    required.getUnit())) {

                return false;
            }
        }

        // Every requirement passed.
        return true;
    }


    private static PantryItem findMatchingPantryItem(
            List<PantryItem> pantryItems,
            RecipeIngredient required) {

        if (pantryItems == null) {
            return null;
        }

        String requiredName =
                normaliseIngredientName(
                        required.getIngredientName()
                );

        for (PantryItem pantryItem : pantryItems) {

            String pantryName =
                    normaliseIngredientName(
                            pantryItem.getName()
                    );

            if (pantryName.equals(requiredName)) {
                return pantryItem;
            }
        }

        return null;
    }


    private static String normaliseIngredientName(
            String name) {

        if (name == null) {
            return "";
        }

        String normalised =
                name.trim()
                        .toLowerCase(Locale.ROOT);

        switch (normalised) {

            case "egg":
            case "eggs":
                return "egg";

            case "tomato":
            case "tomatoes":
                return "tomato";

            case "potato":
            case "potatoes":
                return "potato";

            case "banana":
            case "bananas":
                return "banana";

            default:
                break;
        }

        if (normalised.endsWith("s")
                && normalised.length() > 1) {

            return normalised.substring(
                    0,
                    normalised.length() - 1
            );
        }

        return normalised;
    }

}