package com.example.keepingfit.Firestore;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.keepingfit.R;

public class BodyCompositionViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameTextView;
    private final TextView valueTextView;

    public BodyCompositionViewHolder(View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.name);
        valueTextView = itemView.findViewById(R.id.value);
    }

    public void bind(String name, String value) {
        nameTextView.setText(name);
        valueTextView.setText(value);
    }
}