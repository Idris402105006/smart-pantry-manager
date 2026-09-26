package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.activities.IngredientActivity;
import com.example.smartpantrymanager.activities.RecipesActivity;
import com.example.smartpantrymanager.activities.SettingsActivity;
import com.example.smartpantrymanager.adapters.PantryAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPantry;
    private PantryAdapter pantryAdapter;

    private List<PantryItem> pantryItems;

    private TextView tvItemCount;

    private FloatingActionButton fabAddIngredient;

    private MaterialButton btnSuggestedRecipes;
    private MaterialButton btnSettings;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_main
        );


        initialiseViews();


        databaseHelper =
                new DatabaseHelper(this);

        setupRecyclerView();

        loadPantryItems();


        fabAddIngredient.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    IngredientActivity.class
                            );

                    startActivity(intent);
                }
        );


        btnSuggestedRecipes.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    RecipesActivity.class
                            );

                    startActivity(intent);
                }
        );


        btnSettings.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    SettingsActivity.class
                            );

                    startActivity(intent);
                }
        );
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null
                && pantryAdapter != null) {

            loadPantryItems();
        }
    }


    private void initialiseViews() {

        recyclerViewPantry =
                findViewById(
                        R.id.recyclerViewPantry
                );

        tvItemCount =
                findViewById(
                        R.id.tvItemCount
                );

        fabAddIngredient =
                findViewById(
                        R.id.fabAddIngredient
                );

        btnSuggestedRecipes =
                findViewById(
                        R.id.btnSuggestedRecipes
                );

        btnSettings =
                findViewById(
                        R.id.btnSettings
                );
    }


    private void setupRecyclerView() {

        pantryItems =
                new ArrayList<>();

        pantryAdapter =
                new PantryAdapter(
                        pantryItems,
                        new PantryAdapter
                                .OnPantryItemActionListener() {

                            @Override
                            public void onEdit(
                                    PantryItem item) {

                                openEditIngredient(
                                        item
                                );
                            }

                            @Override
                            public void onDelete(
                                    PantryItem item) {

                                confirmDelete(
                                        item
                                );
                            }
                        }
                );

        recyclerViewPantry.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerViewPantry.setAdapter(
                pantryAdapter
        );
    }


    private void loadPantryItems() {

        pantryItems.clear();

        pantryItems.addAll(
                databaseHelper
                        .getAllPantryItems()
        );

        pantryAdapter.notifyDataSetChanged();

        updateItemCount();
    }


    private void updateItemCount() {

        int count =
                pantryItems.size();

        if (count == 1) {

            tvItemCount.setText(
                    "1 item"
            );

        } else {

            tvItemCount.setText(
                    count + " items"
            );
        }
    }


    private void openEditIngredient(
            PantryItem item) {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        IngredientActivity.class
                );

        intent.putExtra(
                "MODE",
                "EDIT"
        );

        intent.putExtra(
                "ID",
                item.getId()
        );

        intent.putExtra(
                "NAME",
                item.getName()
        );

        intent.putExtra(
                "QUANTITY",
                item.getQuantity()
        );

        intent.putExtra(
                "UNIT",
                item.getUnit()
        );

        intent.putExtra(
                "EXPIRY_DATE",
                item.getExpiryDate()
        );

        startActivity(intent);
    }


    private void confirmDelete(
            PantryItem item) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Delete Ingredient"
                )
                .setMessage(
                        "Are you sure you want to delete "
                                + item.getName()
                                + "?"
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            databaseHelper
                                    .deletePantryItem(
                                            item.getId()
                                    );

                            loadPantryItems();
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }
}