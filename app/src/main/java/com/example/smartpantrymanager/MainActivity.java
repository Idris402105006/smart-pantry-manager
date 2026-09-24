package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapters.PantryAdapter;
import com.example.smartpantrymanager.models.PantryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPantry;
    private PantryAdapter pantryAdapter;
    private List<PantryItem> pantryItems;
    private TextView tvItemCount;
    private FloatingActionButton fabAddIngredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseViews();
        setupRecyclerView();
        loadTemporaryPantryItems();
    }

    private void initialiseViews() {
        recyclerViewPantry = findViewById(R.id.recyclerViewPantry);
        tvItemCount = findViewById(R.id.tvItemCount);
        fabAddIngredient = findViewById(R.id.fabAddIngredient);
    }

    private void setupRecyclerView() {
        pantryItems = new ArrayList<>();

        pantryAdapter = new PantryAdapter(pantryItems);

        recyclerViewPantry.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerViewPantry.setAdapter(pantryAdapter);
    }

    private void loadTemporaryPantryItems() {

        pantryItems.add(
                new PantryItem(
                        1,
                        "Tomatoes",
                        4,
                        "pieces",
                        "30/09/2026"
                )
        );

        pantryItems.add(
                new PantryItem(
                        2,
                        "Eggs",
                        6,
                        "pieces",
                        "28/09/2026"
                )
        );

        pantryItems.add(
                new PantryItem(
                        3,
                        "Milk",
                        1,
                        "litre",
                        "26/09/2026"
                )
        );

        pantryAdapter.notifyDataSetChanged();

        updateItemCount();
    }

    private void updateItemCount() {
        int count = pantryItems.size();

        if (count == 1) {
            tvItemCount.setText("1 item");
        } else {
            tvItemCount.setText(count + " items");
        }
    }
}