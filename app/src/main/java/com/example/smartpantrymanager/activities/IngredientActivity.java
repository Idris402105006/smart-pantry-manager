package com.example.smartpantrymanager.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

public class IngredientActivity extends AppCompatActivity {

    private TextInputLayout layoutIngredientName;
    private TextInputLayout layoutQuantity;
    private TextInputLayout layoutUnit;

    private TextInputEditText etIngredientName;
    private TextInputEditText etQuantity;
    private TextInputEditText etExpiryDate;

    private AutoCompleteTextView actvUnit;
    private MaterialButton btnSaveIngredient;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        databaseHelper = new DatabaseHelper(this);

        initialiseViews();
        setupUnitDropdown();
        setupDatePicker();
        setupSaveButton();
    }

    private void initialiseViews() {

        layoutIngredientName =
                findViewById(R.id.layoutIngredientName);

        layoutQuantity =
                findViewById(R.id.layoutQuantity);

        layoutUnit =
                findViewById(R.id.layoutUnit);

        etIngredientName =
                findViewById(R.id.etIngredientName);

        etQuantity =
                findViewById(R.id.etQuantity);

        etExpiryDate =
                findViewById(R.id.etExpiryDate);

        actvUnit =
                findViewById(R.id.actvUnit);

        btnSaveIngredient =
                findViewById(R.id.btnSaveIngredient);
    }

    private void setupUnitDropdown() {

        String[] units = {
                "pieces",
                "grams",
                "kilograms",
                "millilitres",
                "litres",
                "cups",
                "tablespoons",
                "teaspoons"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        units
                );

        actvUnit.setAdapter(adapter);
    }

    private void setupDatePicker() {

        etExpiryDate.setOnClickListener(view -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(
                            this,
                            (datePicker, selectedYear,
                             selectedMonth, selectedDay) -> {

                                String selectedDate =
                                        String.format(
                                                Locale.getDefault(),
                                                "%02d/%02d/%04d",
                                                selectedDay,
                                                selectedMonth + 1,
                                                selectedYear
                                        );

                                etExpiryDate.setText(selectedDate);
                            },
                            year,
                            month,
                            day
                    );

            datePickerDialog.show();
        });
    }

    private void setupSaveButton() {

        btnSaveIngredient.setOnClickListener(
                view -> saveIngredient()
        );
    }

    private void saveIngredient() {

        clearErrors();

        String name =
                getText(etIngredientName);

        String quantityText =
                getText(etQuantity);

        String unit =
                actvUnit.getText().toString().trim();

        String expiryDate =
                getText(etExpiryDate);

        boolean isValid = true;

        if (name.isEmpty()) {
            layoutIngredientName.setError(
                    "Ingredient name is required"
            );

            isValid = false;
        }

        if (quantityText.isEmpty()) {
            layoutQuantity.setError(
                    "Quantity is required"
            );

            isValid = false;
        }

        if (unit.isEmpty()) {
            layoutUnit.setError(
                    "Please select a unit"
            );

            isValid = false;
        }

        if (!isValid) {
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException exception) {

            layoutQuantity.setError(
                    "Enter a valid quantity"
            );

            return;
        }

        if (quantity <= 0) {
            layoutQuantity.setError(
                    "Quantity must be greater than 0"
            );

            return;
        }

        PantryItem pantryItem =
                new PantryItem(
                        0,
                        name,
                        quantity,
                        unit,
                        expiryDate
                );

        long result =
                databaseHelper.addPantryItem(pantryItem);

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Ingredient added",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Unable to add ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private String getText(
            TextInputEditText editText) {

        if (editText.getText() == null) {
            return "";
        }

        return editText
                .getText()
                .toString()
                .trim();
    }

    private void clearErrors() {

        layoutIngredientName.setError(null);
        layoutQuantity.setError(null);
        layoutUnit.setError(null);
    }
}