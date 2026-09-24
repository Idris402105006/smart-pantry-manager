package com.example.smartpantrymanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.adapters.RecipeAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.Recipe;

import java.util.List;

public class RecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRecipes;
    private TextView tvRecipeCount;
    private TextView tvNoRecipes;

    private DatabaseHelper databaseHelper;
    private RecipeAdapter recipeAdapter;

    private List<Recipe> suggestedRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_recipes
        );

        databaseHelper =
                new DatabaseHelper(this);

        initialiseViews();
        loadSuggestedRecipes();
    }

    private void initialiseViews() {

        recyclerViewRecipes =
                findViewById(
                        R.id.recyclerViewRecipes
                );

        tvRecipeCount =
                findViewById(
                        R.id.tvRecipeCount
                );

        tvNoRecipes =
                findViewById(
                        R.id.tvNoRecipes
                );

        recyclerViewRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );
    }

    private void loadSuggestedRecipes() {

        suggestedRecipes =
                databaseHelper
                        .getSuggestedRecipes();

        recipeAdapter =
                new RecipeAdapter(
                        suggestedRecipes,
                        databaseHelper
                );

        recyclerViewRecipes.setAdapter(
                recipeAdapter
        );

        updateRecipeCount();
        updateEmptyState();
    }

    private void updateRecipeCount() {

        int count =
                suggestedRecipes.size();

        if (count == 1) {

            tvRecipeCount.setText(
                    "1 recipe"
            );

        } else {

            tvRecipeCount.setText(
                    count + " recipes"
            );
        }
    }

    private void updateEmptyState() {

        if (suggestedRecipes.isEmpty()) {

            recyclerViewRecipes.setVisibility(
                    View.GONE
            );

            tvNoRecipes.setVisibility(
                    View.VISIBLE
            );

        } else {

            recyclerViewRecipes.setVisibility(
                    View.VISIBLE
            );

            tvNoRecipes.setVisibility(
                    View.GONE
            );
        }
    }
}