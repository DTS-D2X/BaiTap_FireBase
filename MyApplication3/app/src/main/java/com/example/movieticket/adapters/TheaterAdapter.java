package com.example.movieticket.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticket.R;
import com.example.movieticket.models.Theater;

import java.util.List;

public class TheaterAdapter extends RecyclerView.Adapter<TheaterAdapter.TheaterViewHolder> {

    private List<Theater> theaterList;

    public TheaterAdapter(List<Theater> theaterList) {
        this.theaterList = theaterList;
    }

    @NonNull
    @Override
    public TheaterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theater, parent, false);
        return new TheaterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TheaterViewHolder holder, int position) {
        Theater theater = theaterList.get(position);
        holder.tvName.setText(theater.getName());
        holder.tvLocation.setText(theater.getLocation());
    }

    @Override
    public int getItemCount() {
        return theaterList.size();
    }

    static class TheaterViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLocation;

        public TheaterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTheaterName);
            tvLocation = itemView.findViewById(R.id.tvTheaterLocation);
        }
    }
}