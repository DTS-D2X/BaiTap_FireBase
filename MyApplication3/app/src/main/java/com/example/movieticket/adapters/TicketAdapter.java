package com.example.movieticket.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticket.R;
import com.example.movieticket.models.Ticket;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<Ticket> ticketList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public TicketAdapter(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);
        holder.tvSeat.setText("Seat: " + ticket.getSeatNumber());
        
        // Fetch showtime and movie title to display
        db.collection("showtimes").document(ticket.getShowtimeId()).get()
                .addOnSuccessListener(showtimeDoc -> {
                    String time = showtimeDoc.getString("time");
                    String movieId = showtimeDoc.getString("movieId");
                    holder.tvTime.setText("Time: " + time);
                    
                    if (movieId != null) {
                        db.collection("movies").document(movieId).get()
                                .addOnSuccessListener(movieDoc -> {
                                    holder.tvMovieTitle.setText(movieDoc.getString("title"));
                                });
                    }
                });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieTitle, tvTime, tvSeat;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieTitle = itemView.findViewById(R.id.tvTicketMovieTitle);
            tvTime = itemView.findViewById(R.id.tvTicketTime);
            tvSeat = itemView.findViewById(R.id.tvTicketSeat);
        }
    }
}