package com.example.movieticket;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieticket.adapters.ShowtimeAdapter;
import com.example.movieticket.models.Movie;
import com.example.movieticket.models.Showtime;
import com.example.movieticket.models.Ticket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MovieDetailActivity extends AppCompatActivity implements ShowtimeAdapter.OnShowtimeClickListener {

    private ImageView ivPoster;
    private TextView tvTitle, tvGenre, tvDescription;
    private RecyclerView rvShowtimes;
    private ShowtimeAdapter adapter;
    private List<Showtime> showtimeList;
    private FirebaseFirestore db;
    private String movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        db = FirebaseFirestore.getInstance();
        movieId = getIntent().getStringExtra("movie_id");

        ivPoster = findViewById(R.id.ivDetailPoster);
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvGenre = findViewById(R.id.tvDetailGenre);
        tvDescription = findViewById(R.id.tvDetailDescription);
        rvShowtimes = findViewById(R.id.rvShowtimes);

        rvShowtimes.setLayoutManager(new LinearLayoutManager(this));
        showtimeList = new ArrayList<>();
        adapter = new ShowtimeAdapter(showtimeList, this);
        rvShowtimes.setAdapter(adapter);

        fetchMovieDetails();
        fetchShowtimes();
    }

    private void fetchMovieDetails() {
        db.collection("movies").document(movieId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Movie movie = documentSnapshot.toObject(Movie.class);
                    if (movie != null) {
                        tvTitle.setText(movie.getTitle());
                        tvGenre.setText(movie.getGenre());
                        tvDescription.setText(movie.getDescription());
                        Glide.with(this).load(movie.getImageUrl()).into(ivPoster);
                    }
                });
    }

    private void fetchShowtimes() {
        db.collection("showtimes")
                .whereEqualTo("movieId", movieId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showtimeList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Showtime showtime = document.toObject(Showtime.class);
                            showtime.setId(document.getId());
                            showtimeList.add(showtime);
                        }
                        adapter.notifyDataSetChanged();
                        
                        // If no showtimes, add a sample one for demo
                        if (showtimeList.isEmpty()) {
                             addSampleShowtime();
                        }
                    }
                });
    }

    private void addSampleShowtime() {
        Showtime s = new Showtime(null, movieId, "theater_1", "2023-12-25 20:00", 12.50);
        db.collection("showtimes").add(s).addOnSuccessListener(documentReference -> fetchShowtimes());
    }

    @Override
    public void onBookClick(Showtime showtime) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        
        Ticket ticket = new Ticket(null, userId, showtime.getId(), "A1", showtime.getPrice(), timestamp);
        
        db.collection("tickets").add(ticket)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Ticket Booked Successfully!", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Booking Failed", Toast.LENGTH_SHORT).show();
                });
    }
}