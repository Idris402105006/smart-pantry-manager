package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;
import com.example.smartpantrymanager.utils.IngredientMatcher;
import com.example.smartpantrymanager.utils.UnitConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 3;

    // Pantry table
    private static final String TABLE_PANTRY = "pantry_items";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_UNIT = "unit";
    private static final String COLUMN_EXPIRY_DATE = "expiry_date";
    // Recipe table
    private static final String TABLE_RECIPES =
            "recipes";

    private static final String COLUMN_RECIPE_ID =
            "id";

    private static final String COLUMN_RECIPE_NAME =
            "name";

    private static final String COLUMN_INSTRUCTIONS =
            "instructions";


    // Recipe ingredients table
    private static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";

    private static final String COLUMN_RECIPE_INGREDIENT_ID =
            "id";

    private static final String COLUMN_RECIPE_ID_FK =
            "recipe_id";

    private static final String COLUMN_INGREDIENT_NAME =
            "ingredient_name";

    private static final String COLUMN_REQUIRED_QUANTITY =
            "quantity";

    private static final String COLUMN_REQUIRED_UNIT =
            "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        createPantryTable(db);
        createRecipeTables(db);

        seedInitialRecipes(db);
        seedAdditionalRecipes(db);
    }
    private void createPantryTable(
            SQLiteDatabase db) {

        String createPantryTable =
                "CREATE TABLE " + TABLE_PANTRY + " (" +
                        COLUMN_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COLUMN_NAME +
                        " TEXT NOT NULL, " +

                        COLUMN_QUANTITY +
                        " REAL NOT NULL, " +

                        COLUMN_UNIT +
                        " TEXT NOT NULL, " +

                        COLUMN_EXPIRY_DATE +
                        " TEXT" +
                        ")";

        db.execSQL(createPantryTable);
    }

    private void createRecipeTables(
            SQLiteDatabase db) {

        String createRecipesTable =
                "CREATE TABLE " + TABLE_RECIPES + " (" +

                        COLUMN_RECIPE_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COLUMN_RECIPE_NAME +
                        " TEXT NOT NULL, " +

                        COLUMN_INSTRUCTIONS +
                        " TEXT NOT NULL" +

                        ")";

        db.execSQL(createRecipesTable);


        String createRecipeIngredientsTable =
                "CREATE TABLE "
                        + TABLE_RECIPE_INGREDIENTS
                        + " (" +

                        COLUMN_RECIPE_INGREDIENT_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COLUMN_RECIPE_ID_FK +
                        " INTEGER NOT NULL, " +

                        COLUMN_INGREDIENT_NAME +
                        " TEXT NOT NULL, " +

                        COLUMN_REQUIRED_QUANTITY +
                        " REAL NOT NULL, " +

                        COLUMN_REQUIRED_UNIT +
                        " TEXT NOT NULL, " +

                        "FOREIGN KEY(" +
                        COLUMN_RECIPE_ID_FK +
                        ") REFERENCES " +
                        TABLE_RECIPES +
                        "(" +
                        COLUMN_RECIPE_ID +
                        ")" +

                        ")";

        db.execSQL(
                createRecipeIngredientsTable
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        if (oldVersion < 2) {

            createRecipeTables(db);
            seedInitialRecipes(db);
        }

        if (oldVersion < 3) {

            seedAdditionalRecipes(db);
        }
    }


    // CREATE
    public long addPantryItem(PantryItem item) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_UNIT, item.getUnit());
        values.put(COLUMN_EXPIRY_DATE, item.getExpiryDate());

        return db.insert(
                TABLE_PANTRY,
                null,
                values
        );
    }

    // READ
    public List<PantryItem> getAllPantryItems() {

        List<PantryItem> pantryItems = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                COLUMN_NAME + " ASC"
        );

        if (cursor.moveToFirst()) {

            do {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(COLUMN_ID)
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_NAME)
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_UNIT)
                );

                String expiryDate = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_EXPIRY_DATE)
                );

                PantryItem item = new PantryItem(
                        id,
                        name,
                        quantity,
                        unit,
                        expiryDate
                );

                pantryItems.add(item);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return pantryItems;
    }

    // UPDATE
    public int updatePantryItem(PantryItem item) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_UNIT, item.getUnit());
        values.put(COLUMN_EXPIRY_DATE, item.getExpiryDate());

        return db.update(
                TABLE_PANTRY,
                values,
                COLUMN_ID + " = ?",
                new String[]{
                        String.valueOf(item.getId())
                }
        );
    }

    // DELETE
    public int deletePantryItem(int id) {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_PANTRY,
                COLUMN_ID + " = ?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }
    private long insertRecipe(
            SQLiteDatabase db,
            String name,
            String instructions) {

        ContentValues values =
                new ContentValues();

        values.put(
                COLUMN_RECIPE_NAME,
                name
        );

        values.put(
                COLUMN_INSTRUCTIONS,
                instructions
        );

        return db.insert(
                TABLE_RECIPES,
                null,
                values
        );
    }
    private void insertRecipeIngredient(
            SQLiteDatabase db,
            long recipeId,
            String ingredientName,
            double quantity,
            String unit) {

        ContentValues values =
                new ContentValues();

        values.put(
                COLUMN_RECIPE_ID_FK,
                recipeId
        );

        values.put(
                COLUMN_INGREDIENT_NAME,
                ingredientName
        );

        values.put(
                COLUMN_REQUIRED_QUANTITY,
                quantity
        );

        values.put(
                COLUMN_REQUIRED_UNIT,
                unit
        );

        db.insert(
                TABLE_RECIPE_INGREDIENTS,
                null,
                values
        );
    }
    private void seedInitialRecipes(
            SQLiteDatabase db) {

        // 1. Scrambled Eggs
        long scrambledEggsId =
                insertRecipe(
                        db,
                        "Scrambled Eggs",
                        "Beat the eggs with the milk. "
                                + "Cook gently in a pan while stirring "
                                + "until the eggs are soft and cooked."
                );

        insertRecipeIngredient(
                db,
                scrambledEggsId,
                "eggs",
                2,
                "pieces"
        );

        insertRecipeIngredient(
                db,
                scrambledEggsId,
                "milk",
                50,
                "millilitres"
        );


        // 2. Tomato Omelette
        long tomatoOmeletteId =
                insertRecipe(
                        db,
                        "Tomato Omelette",
                        "Beat the eggs. Chop the tomato and add it "
                                + "to the eggs. Cook the mixture in "
                                + "a pan until set."
                );

        insertRecipeIngredient(
                db,
                tomatoOmeletteId,
                "eggs",
                2,
                "pieces"
        );

        insertRecipeIngredient(
                db,
                tomatoOmeletteId,
                "tomatoes",
                1,
                "pieces"
        );


        // 3. Buttered Toast
        long butteredToastId =
                insertRecipe(
                        db,
                        "Buttered Toast",
                        "Toast the bread until golden and spread "
                                + "the butter evenly over each slice."
                );

        insertRecipeIngredient(
                db,
                butteredToastId,
                "bread",
                2,
                "pieces"
        );

        insertRecipeIngredient(
                db,
                butteredToastId,
                "butter",
                20,
                "grams"
        );


        // 4. Cheese Toast
        long cheeseToastId =
                insertRecipe(
                        db,
                        "Cheese Toast",
                        "Place the cheese on the bread and toast "
                                + "until the bread is crisp and the "
                                + "cheese has melted."
                );

        insertRecipeIngredient(
                db,
                cheeseToastId,
                "bread",
                2,
                "pieces"
        );

        insertRecipeIngredient(
                db,
                cheeseToastId,
                "cheese",
                50,
                "grams"
        );


        // 5. Tomato Sandwich
        long tomatoSandwichId =
                insertRecipe(
                        db,
                        "Tomato Sandwich",
                        "Slice the tomato and place it between "
                                + "the slices of bread. Serve immediately."
                );

        insertRecipeIngredient(
                db,
                tomatoSandwichId,
                "bread",
                2,
                "pieces"
        );

        insertRecipeIngredient(
                db,
                tomatoSandwichId,
                "tomatoes",
                1,
                "pieces"
        );
    }
    private void seedAdditionalRecipes(
            SQLiteDatabase db) {

        // 6. Egg Sandwich
        long eggSandwichId =
                insertRecipe(
                        db,
                        "Egg Sandwich",
                        "Cook the eggs and place them between "
                                + "the slices of bread. Serve warm."
                );

        insertRecipeIngredient(
                db, eggSandwichId,
                "eggs", 2, "pieces"
        );

        insertRecipeIngredient(
                db, eggSandwichId,
                "bread", 2, "pieces"
        );


        // 7. Cheese Omelette
        long cheeseOmeletteId =
                insertRecipe(
                        db,
                        "Cheese Omelette",
                        "Beat the eggs and pour them into a heated pan. "
                                + "Add the cheese and cook until the eggs "
                                + "are set and the cheese has melted."
                );

        insertRecipeIngredient(
                db, cheeseOmeletteId,
                "eggs", 2, "pieces"
        );

        insertRecipeIngredient(
                db, cheeseOmeletteId,
                "cheese", 50, "grams"
        );


        // 8. Tomato and Cheese Sandwich
        long tomatoCheeseSandwichId =
                insertRecipe(
                        db,
                        "Tomato and Cheese Sandwich",
                        "Slice the tomato and layer it with the cheese "
                                + "between two slices of bread."
                );

        insertRecipeIngredient(
                db, tomatoCheeseSandwichId,
                "bread", 2, "pieces"
        );

        insertRecipeIngredient(
                db, tomatoCheeseSandwichId,
                "tomatoes", 1, "pieces"
        );

        insertRecipeIngredient(
                db, tomatoCheeseSandwichId,
                "cheese", 40, "grams"
        );


        // 9. Boiled Eggs
        long boiledEggsId =
                insertRecipe(
                        db,
                        "Boiled Eggs",
                        "Place the eggs in boiling water and cook "
                                + "until they reach the desired firmness."
                );

        insertRecipeIngredient(
                db, boiledEggsId,
                "eggs", 2, "pieces"
        );


        // 10. Warm Milk
        long warmMilkId =
                insertRecipe(
                        db,
                        "Warm Milk",
                        "Pour the milk into a saucepan and heat gently "
                                + "until warm. Do not allow it to boil."
                );

        insertRecipeIngredient(
                db, warmMilkId,
                "milk", 250, "millilitres"
        );


        // 11. Buttered Potatoes
        long butteredPotatoesId =
                insertRecipe(
                        db,
                        "Buttered Potatoes",
                        "Boil the potatoes until tender. Drain them "
                                + "and mix with butter before serving."
                );

        insertRecipeIngredient(
                db, butteredPotatoesId,
                "potatoes", 3, "pieces"
        );

        insertRecipeIngredient(
                db, butteredPotatoesId,
                "butter", 30, "grams"
        );


        // 12. Cheesy Potatoes
        long cheesyPotatoesId =
                insertRecipe(
                        db,
                        "Cheesy Potatoes",
                        "Cook the potatoes until tender, top with "
                                + "cheese and heat until the cheese melts."
                );

        insertRecipeIngredient(
                db, cheesyPotatoesId,
                "potatoes", 3, "pieces"
        );

        insertRecipeIngredient(
                db, cheesyPotatoesId,
                "cheese", 60, "grams"
        );


        // 13. Tomato and Egg Scramble
        long tomatoEggScrambleId =
                insertRecipe(
                        db,
                        "Tomato and Egg Scramble",
                        "Chop the tomato and cook it briefly in a pan. "
                                + "Add beaten eggs and stir until cooked."
                );

        insertRecipeIngredient(
                db, tomatoEggScrambleId,
                "tomatoes", 1, "pieces"
        );

        insertRecipeIngredient(
                db, tomatoEggScrambleId,
                "eggs", 2, "pieces"
        );


        // 14. Banana Milk
        long bananaMilkId =
                insertRecipe(
                        db,
                        "Banana Milk",
                        "Peel the banana and blend it with the milk "
                                + "until smooth."
                );

        insertRecipeIngredient(
                db, bananaMilkId,
                "bananas", 1, "pieces"
        );

        insertRecipeIngredient(
                db, bananaMilkId,
                "milk", 250, "millilitres"
        );


        // 15. Banana Toast
        long bananaToastId =
                insertRecipe(
                        db,
                        "Banana Toast",
                        "Toast the bread, slice the banana and arrange "
                                + "the slices over the toast."
                );

        insertRecipeIngredient(
                db, bananaToastId,
                "bread", 2, "pieces"
        );

        insertRecipeIngredient(
                db, bananaToastId,
                "bananas", 1, "pieces"
        );


        // 16. Cheese and Egg Toast
        long cheeseEggToastId =
                insertRecipe(
                        db,
                        "Cheese and Egg Toast",
                        "Cook the egg, place it on toasted bread and "
                                + "top with cheese while still warm."
                );

        insertRecipeIngredient(
                db, cheeseEggToastId,
                "bread", 2, "pieces"
        );

        insertRecipeIngredient(
                db, cheeseEggToastId,
                "eggs", 1, "pieces"
        );

        insertRecipeIngredient(
                db, cheeseEggToastId,
                "cheese", 40, "grams"
        );


        // 17. Mashed Potatoes
        long mashedPotatoesId =
                insertRecipe(
                        db,
                        "Mashed Potatoes",
                        "Boil the potatoes until soft. Mash them with "
                                + "milk and butter until smooth."
                );

        insertRecipeIngredient(
                db, mashedPotatoesId,
                "potatoes", 3, "pieces"
        );

        insertRecipeIngredient(
                db, mashedPotatoesId,
                "milk", 100, "millilitres"
        );

        insertRecipeIngredient(
                db, mashedPotatoesId,
                "butter", 30, "grams"
        );


        // 18. Tomato Cheese Omelette
        long tomatoCheeseOmeletteId =
                insertRecipe(
                        db,
                        "Tomato Cheese Omelette",
                        "Beat the eggs, add chopped tomato and pour "
                                + "into a pan. Add cheese and cook "
                                + "until the omelette is set."
                );

        insertRecipeIngredient(
                db, tomatoCheeseOmeletteId,
                "eggs", 2, "pieces"
        );

        insertRecipeIngredient(
                db, tomatoCheeseOmeletteId,
                "tomatoes", 1, "pieces"
        );

        insertRecipeIngredient(
                db, tomatoCheeseOmeletteId,
                "cheese", 40, "grams"
        );


        // 19. Buttered Bread
        long butteredBreadId =
                insertRecipe(
                        db,
                        "Buttered Bread",
                        "Spread the butter evenly over the bread "
                                + "and serve."
                );

        insertRecipeIngredient(
                db, butteredBreadId,
                "bread", 2, "pieces"
        );

        insertRecipeIngredient(
                db, butteredBreadId,
                "butter", 20, "grams"
        );


        // 20. Creamy Scrambled Eggs
        long creamyScrambledEggsId =
                insertRecipe(
                        db,
                        "Creamy Scrambled Eggs",
                        "Beat the eggs with milk. Melt the butter in "
                                + "a pan, add the egg mixture and stir "
                                + "gently until soft and cooked."
                );

        insertRecipeIngredient(
                db, creamyScrambledEggsId,
                "eggs", 2, "pieces"
        );

        insertRecipeIngredient(
                db, creamyScrambledEggsId,
                "milk", 50, "millilitres"
        );

        insertRecipeIngredient(
                db, creamyScrambledEggsId,
                "butter", 10, "grams"
        );
    }

    public List<Recipe> getAllRecipes() {

        List<Recipe> recipes = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                COLUMN_RECIPE_NAME + " ASC"
        );

        if (cursor.moveToFirst()) {

            do {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                COLUMN_RECIPE_ID
                        )
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                COLUMN_RECIPE_NAME
                        )
                );

                String instructions = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                COLUMN_INSTRUCTIONS
                        )
                );

                Recipe recipe =
                        new Recipe(
                                id,
                                name,
                                instructions
                        );

                recipes.add(recipe);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return recipes;
    }
    public List<RecipeIngredient> getRecipeIngredients(
            int recipeId) {

        List<RecipeIngredient> ingredients =
                new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                COLUMN_RECIPE_ID_FK + " = ?",
                new String[]{
                        String.valueOf(recipeId)
                },
                null,
                null,
                COLUMN_INGREDIENT_NAME + " ASC"
        );

        if (cursor.moveToFirst()) {

            do {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                COLUMN_RECIPE_INGREDIENT_ID
                        )
                );

                int storedRecipeId = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                COLUMN_RECIPE_ID_FK
                        )
                );

                String ingredientName = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                COLUMN_INGREDIENT_NAME
                        )
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                COLUMN_REQUIRED_QUANTITY
                        )
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                COLUMN_REQUIRED_UNIT
                        )
                );

                RecipeIngredient ingredient =
                        new RecipeIngredient(
                                id,
                                storedRecipeId,
                                ingredientName,
                                quantity,
                                unit
                        );

                ingredients.add(ingredient);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return ingredients;
    }

    public List<Recipe> getSuggestedRecipes() {

        List<Recipe> suggestedRecipes =
                new ArrayList<>();

        List<PantryItem> pantryItems =
                getAllPantryItems();

        List<Recipe> allRecipes =
                getAllRecipes();

        for (Recipe recipe : allRecipes) {

            List<RecipeIngredient>
                    requiredIngredients =
                    getRecipeIngredients(
                            recipe.getId()
                    );

            boolean canMakeRecipe =
                    IngredientMatcher.canMakeRecipe(
                            pantryItems,
                            requiredIngredients
                    );

            if (canMakeRecipe) {
                suggestedRecipes.add(recipe);
            }
        }

        return suggestedRecipes;
    }
    public boolean cookRecipe(int recipeId) {

        SQLiteDatabase db = getWritableDatabase();

        List<RecipeIngredient> requiredIngredients =
                getRecipeIngredients(recipeId);

        List<PantryItem> pantryItems =
                getAllPantryItems();

        /*
         * Re-check that the recipe can still be made.
         * This protects us in case the pantry changed
         * after the recipe screen was opened.
         */
        if (!IngredientMatcher.canMakeRecipe(
                pantryItems,
                requiredIngredients)) {

            return false;
        }

        db.beginTransaction();

        try {

            for (RecipeIngredient required :
                    requiredIngredients) {

                PantryItem matchingItem =
                        findPantryItemForRecipeIngredient(
                                pantryItems,
                                required
                        );

                if (matchingItem == null) {
                    return false;
                }

                double requiredInPantryUnit =
                        UnitConverter.convert(
                                required.getQuantity(),
                                required.getUnit(),
                                matchingItem.getUnit()
                        );

                double remainingQuantity =
                        matchingItem.getQuantity()
                                - requiredInPantryUnit;

                /*
                 * If nothing remains, remove the
                 * ingredient from the pantry.
                 */
                if (remainingQuantity <= 0) {

                    int deletedRows =
                            db.delete(
                                    TABLE_PANTRY,
                                    COLUMN_ID + " = ?",
                                    new String[]{
                                            String.valueOf(
                                                    matchingItem.getId()
                                            )
                                    }
                            );

                    if (deletedRows == 0) {
                        return false;
                    }

                } else {

                    /*
                     * Otherwise update the pantry
                     * with the remaining quantity.
                     */
                    ContentValues values =
                            new ContentValues();

                    values.put(
                            COLUMN_QUANTITY,
                            remainingQuantity
                    );

                    int updatedRows =
                            db.update(
                                    TABLE_PANTRY,
                                    values,
                                    COLUMN_ID + " = ?",
                                    new String[]{
                                            String.valueOf(
                                                    matchingItem.getId()
                                            )
                                    }
                            );

                    if (updatedRows == 0) {
                        return false;
                    }
                }
            }

            db.setTransactionSuccessful();

            return true;

        } finally {

            db.endTransaction();
        }
    }
    private PantryItem findPantryItemForRecipeIngredient(
            List<PantryItem> pantryItems,
            RecipeIngredient required) {

        for (PantryItem pantryItem :
                pantryItems) {

            if (ingredientNamesMatch(
                    pantryItem.getName(),
                    required.getIngredientName())
                    && UnitConverter.areCompatible(
                    pantryItem.getUnit(),
                    required.getUnit())) {

                return pantryItem;
            }
        }

        return null;
    }
    private boolean ingredientNamesMatch(
            String pantryName,
            String requiredName) {

        if (pantryName == null
                || requiredName == null) {

            return false;
        }

        return normaliseIngredientName(
                pantryName
        ).equals(
                normaliseIngredientName(
                        requiredName
                )
        );
    }
    private String normaliseIngredientName(
            String name) {

        String normalised =
                name.trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

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
    private boolean unitsMatch(
            String pantryUnit,
            String requiredUnit) {

        if (pantryUnit == null
                || requiredUnit == null) {

            return false;
        }

        return pantryUnit
                .trim()
                .equalsIgnoreCase(
                        requiredUnit.trim()
                );
    }
}