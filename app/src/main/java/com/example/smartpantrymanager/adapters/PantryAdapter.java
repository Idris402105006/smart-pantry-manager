package com.example.smartpantrymanager.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.models.PantryItem;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PantryAdapter
        extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private static final String PREFS_NAME =
            "smart_pantry_preferences";

    private static final String KEY_EXPIRY_ALERTS =
            "expiry_alerts_enabled";

    private static final int EXPIRY_WARNING_DAYS = 3;

    private final List<PantryItem> pantryItems;
    private final OnPantryItemActionListener listener;

    public PantryAdapter(
            List<PantryItem> pantryItems,
            OnPantryItemActionListener listener) {

        this.pantryItems = pantryItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_pantry,
                                parent,
                                false
                        );

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder,
            int position) {

        PantryItem item =
                pantryItems.get(position);

        holder.tvIngredientName.setText(
                item.getName()
        );

        holder.tvQuantity.setText(
                item.getQuantity()
                        + " "
                        + item.getUnit()
        );

        String expiryDate =
                item.getExpiryDate();

        if (expiryDate == null
                || expiryDate.trim().isEmpty()) {

            holder.tvExpiryDate.setText(
                    "Expiry: Not set"
            );

        } else {

            holder.tvExpiryDate.setText(
                    "Expiry: " + expiryDate
            );
        }


        SharedPreferences preferences =
                holder.itemView
                        .getContext()
                        .getSharedPreferences(
                                PREFS_NAME,
                                Context.MODE_PRIVATE
                        );

        boolean expiryAlertsEnabled =
                preferences.getBoolean(
                        KEY_EXPIRY_ALERTS,
                        true
                );


        if (expiryAlertsEnabled
                && isExpiringSoon(expiryDate)) {

            holder.tvExpiryWarning.setVisibility(
                    View.VISIBLE
            );

        } else {

            holder.tvExpiryWarning.setVisibility(
                    View.GONE
            );
        }

        holder.btnEdit.setOnClickListener(
                view ->
                        listener.onEdit(item)
        );

        holder.btnDelete.setOnClickListener(
                view ->
                        listener.onDelete(item)
        );
    }


    private boolean isExpiringSoon(
            String expiryDateText) {

        if (expiryDateText == null
                || expiryDateText.trim().isEmpty()) {

            return false;
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                );

        dateFormat.setLenient(false);

        try {

            Date expiryDate =
                    dateFormat.parse(
                            expiryDateText
                    );

            if (expiryDate == null) {
                return false;
            }

            Calendar today =
                    Calendar.getInstance();


            setStartOfDay(today);

            Calendar expiry =
                    Calendar.getInstance();

            expiry.setTime(expiryDate);
            setStartOfDay(expiry);

            Calendar warningLimit =
                    (Calendar) today.clone();

            warningLimit.add(
                    Calendar.DAY_OF_YEAR,
                    EXPIRY_WARNING_DAYS
            );


            if (expiry.before(today)) {
                return false;
            }


            return !expiry.after(
                    warningLimit
            );

        } catch (ParseException exception) {

            return false;
        }
    }

    private void setStartOfDay(
            Calendar calendar) {

        calendar.set(
                Calendar.HOUR_OF_DAY,
                0
        );

        calendar.set(
                Calendar.MINUTE,
                0
        );

        calendar.set(
                Calendar.SECOND,
                0
        );

        calendar.set(
                Calendar.MILLISECOND,
                0
        );
    }

    @Override
    public int getItemCount() {

        return pantryItems.size();
    }

    public static class PantryViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvIngredientName;
        TextView tvQuantity;
        TextView tvExpiryDate;
        TextView tvExpiryWarning;

        MaterialButton btnEdit;
        MaterialButton btnDelete;

        public PantryViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvIngredientName =
                    itemView.findViewById(
                            R.id.tvIngredientName
                    );

            tvQuantity =
                    itemView.findViewById(
                            R.id.tvQuantity
                    );

            tvExpiryDate =
                    itemView.findViewById(
                            R.id.tvExpiryDate
                    );

            tvExpiryWarning =
                    itemView.findViewById(
                            R.id.tvExpiryWarning
                    );

            btnEdit =
                    itemView.findViewById(
                            R.id.btnEdit
                    );

            btnDelete =
                    itemView.findViewById(
                            R.id.btnDelete
                    );
        }
    }

    public interface OnPantryItemActionListener {

        void onEdit(
                PantryItem item
        );

        void onDelete(
                PantryItem item
        );
    }
}