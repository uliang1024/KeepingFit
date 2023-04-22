package com.example.keepingfit.Firestore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepingfit.R;

import java.util.List;
import java.util.Map;

public class BodyCompositionAdapter extends RecyclerView.Adapter<BodyCompositionViewHolder> {
    private final List<Map<String, String>> data;

    public BodyCompositionAdapter(List<Map<String, String>> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public BodyCompositionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.body_composition_item, parent, false);
        return new BodyCompositionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BodyCompositionViewHolder holder, int position) {
        Map<String, String> item = data.get(position);
        holder.bind(item.get("name"), item.get("value"));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}