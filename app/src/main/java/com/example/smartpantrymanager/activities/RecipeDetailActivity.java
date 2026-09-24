package com.example.smartpantrymanager.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.RecipeIngredient;
import com.google.android.material.button.MaterialButton;
import android.widget.Toast;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvDetailRecipeName;
    private TextView tvDetailIngredients;
    private TextView tvDetailInstructions;

    private MaterialButton btnMarkCooked;

    private DatabaseHelper databaseHelper;

    private int recipeId;
    private String recipeName;
    private String instructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        databaseHelper = new DatabaseHelper(this);

        initialiseViews();
        readRecipeFromIntent();
        displayRecipe();
        setupCookButton();
    }

    private void initialiseViews() {

        tvDetailRecipeName =
                findViewById(R.id.tvDetailRecipeName);

        tvDetailIngredients =
                findViewById(R.id.tvDetailIngredients);

        tvDetailInstructions =
                findViewById(R.id.tvDetailInstructions);

        btnMarkCooked =
                findViewById(R.id.btnMarkCooked);
    }

    private void readRecipeFromIntent() {

        recipeId =
                getIntent().getIntExtra(
                        "RECIPE_ID",
                        -1
                );

        recipeName =
                getIntent().getStringExtra(
                        "RECIPE_NAME"
                );

        instructions =
                getIntent().getStringExtra(
                        "RECIPE_INSTRUCTIONS"
                );
    }

    private void displayRecipe() {

        tvDetailRecipeName.setText(recipeName);

        tvDetailInstructions.setText(instructions);

        List<RecipeIngredient> ingredients =
                databaseHelper.getRecipeIngredients(
                        recipeId
                );

        StringBuilder ingredientText =
                new StringBuilder();

        for (RecipeIngredient ingredient : ingredients) {

            ingredientText
                    .append("• ")
                    .append(ingredient.getIngredientName())
                    .append(" — ")
                    .append(ingredient.getQuantity())
                    .append(" ")
                    .append(ingredient.getUnit())
                    .append("\n");
        }

        tvDetailIngredients.setText(
                ingredientText.toString().trim()
        );
    }

    private void setupCookButton() {

        btnMarkCooked.setOnClickListener(
                view -> showCookConfirmation()
        );
    }

    private void showCookConfirmation() {

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Mark as Cooked")
                .setMessage(
                        "Cook "
                                + recipeName
                                + "? The required ingredient quantities "
                                + "will be deducted from your pantry."
                )
                .setPositiveButton(
                        "Cook",
                        (dialog, which) ->
                                cookRecipe()
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    private void cookRecipe() {

        boolean success =
                databaseHelper.cookRecipe(
                        recipeId
                );

        if (success) {

            Toast.makeText(
                    this,
                    recipeName
                            + " marked as cooked",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Unable to cook recipe. "
                            + "Check your pantry quantities.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}