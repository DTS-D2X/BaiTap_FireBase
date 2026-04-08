package com.example.movieticket.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticket.R;
import com.example.movieticket.models.Showtime;

import java.util.List;

public class ShowtimeAdapter extends RecyclerView.Adapter<ShowtimeAdapter.ShowtimeViewHolder> {

    private List<Showtime> showtimeList;
    private OnShowtimeClickListener listener;

    public interface OnShowtimeClickListener {
        void onBookClick(Showtime showtime);
    }

    public ShowtimeAdapter(List<Showtime> showtimeList, OnShowtimeClickListener listener) {
        this.showtimeList = showtimeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShowtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_showtime, parent, false);
        return new ShowtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowtimeViewHolder holder, int position) {
        Showtime showtime = showtimeList.get(position);
        holder.tvTime.setText(showtime.getTime());
        holder.tvPrice.setText(String.format("$%.2f", showtime.getPrice()));
        holder.btnBook.setOnClickListener(v -> listener.onBookClick(showtime));
    }

    @Override
    public int getItemCount() {
        return showtimeList.size();
    }

    static class ShowtimeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvPrice;
        Button btnBook;

        public ShowtimeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvShowtime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnBook = itemView.findViewById(R.id.btnBook);
        }
    }
}