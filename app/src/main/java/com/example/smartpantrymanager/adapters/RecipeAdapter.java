package com.example.smartpantrymanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

import java.util.List;

public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final List<Recipe> recipes;
    private final DatabaseHelper databaseHelper;
    private final OnRecipeClickListener listener;


    public RecipeAdapter(
            List<Recipe> recipes,
            DatabaseHelper databaseHelper,
            OnRecipeClickListener listener) {

        this.recipes = recipes;
        this.databaseHelper = databaseHelper;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_recipe,
                                parent,
                                false
                        );

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecipeViewHolder holder,
            int position) {

        Recipe recipe =
                recipes.get(position);


        holder.tvRecipeName.setText(
                recipe.getName()
        );


        holder.tvInstructions.setText(
                recipe.getInstructions()
        );


        List<RecipeIngredient> ingredients =
                databaseHelper
                        .getRecipeIngredients(
                                recipe.getId()
                        );

        StringBuilder ingredientText =
                new StringBuilder();

        for (RecipeIngredient ingredient :
                ingredients) {

            ingredientText
                    .append("• ")
                    .append(
                            ingredient
                                    .getIngredientName()
                    )
                    .append(" — ")
                    .append(
                            ingredient
                                    .getQuantity()
                    )
                    .append(" ")
                    .append(
                            ingredient
                                    .getUnit()
                    )
                    .append("\n");
        }

        holder.tvRecipeIngredients.setText(
                ingredientText
                        .toString()
                        .trim()
        );


        holder.itemView.setOnClickListener(
                view ->
                        listener.onRecipeClick(
                                recipe
                        )
        );
    }

    @Override
    public int getItemCount() {

        return recipes.size();
    }


    public static class RecipeViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvRecipeName;
        TextView tvRecipeIngredients;
        TextView tvInstructions;

        public RecipeViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvRecipeName =
                    itemView.findViewById(
                            R.id.tvRecipeName
                    );

            tvRecipeIngredients =
                    itemView.findViewById(
                            R.id.tvRecipeIngredients
                    );

            tvInstructions =
                    itemView.findViewById(
                            R.id.tvInstructions
                    );
        }
    }


    public interface OnRecipeClickListener {

        void onRecipeClick(
                Recipe recipe
        );
    }
}