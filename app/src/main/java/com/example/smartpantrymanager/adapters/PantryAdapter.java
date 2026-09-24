package com.example.smartpantrymanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.models.PantryItem;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PantryAdapter
        extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

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

        View view = LayoutInflater.from(
                        parent.getContext()
                )
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

        // Display ingredient name.
        holder.tvIngredientName.setText(
                item.getName()
        );

        // Display quantity and unit.
        holder.tvQuantity.setText(
                item.getQuantity()
                        + " "
                        + item.getUnit()
        );

        // Display expiry date if one exists.
        if (item.getExpiryDate() == null
                || item.getExpiryDate().isEmpty()) {

            holder.tvExpiryDate.setText(
                    "Expiry: Not set"
            );

        } else {

            holder.tvExpiryDate.setText(
                    "Expiry: "
                            + item.getExpiryDate()
            );
        }

        // Notify MainActivity when Edit is selected.
        holder.btnEdit.setOnClickListener(
                view -> listener.onEdit(item)
        );

        // Notify MainActivity when Delete is selected.
        holder.btnDelete.setOnClickListener(
                view -> listener.onDelete(item)
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

            // Connect the Java variables
            // to the buttons in item_pantry.xml.
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

        void onEdit(PantryItem item);

        void onDelete(PantryItem item);
    }
}