package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapters.PantryAdapter;
import com.example.smartpantrymanager.models.PantryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.smartpantrymanager.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPantry;
    private PantryAdapter pantryAdapter;
    private List<PantryItem> pantryItems;
    private TextView tvItemCount;
    private FloatingActionButton fabAddIngredient;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseViews();

        databaseHelper = new DatabaseHelper(this);

        setupRecyclerView();
        loadPantryItems();
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

    private void loadPantryItems() {

        pantryItems.clear();

        pantryItems.addAll(
                databaseHelper.getAllPantryItems()
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