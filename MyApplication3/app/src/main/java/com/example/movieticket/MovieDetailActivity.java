package com.example.movieticket;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieticket.adapters.ShowtimeAdapter;
import com.example.movieticket.models.Movie;
import com.example.movieticket.models.Showtime;
import com.example.movieticket.models.Ticket;
import com.example.movieticket.services.NotificationReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
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
    private String movieTitle;
    private Showtime selectedShowtime;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    if (selectedShowtime != null) {
                        bookTicket(selectedShowtime);
                    }
                } else {
                    Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        db = FirebaseFirestore.getInstance();
        movieId = getIntent().getStringExtra("movie_id");
        movieTitle = getIntent().getStringExtra("movie_title");

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
                    }
                });
    }

    @Override
    public void onBookClick(Showtime showtime) {
        selectedShowtime = showtime;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                bookTicket(showtime);
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            bookTicket(showtime);
        }
    }

    private void bookTicket(Showtime showtime) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        
        Ticket ticket = new Ticket(null, userId, showtime.getId(), "A1", showtime.getPrice(), timestamp);
        
        db.collection("tickets").add(ticket)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Ticket Booked Successfully!", Toast.LENGTH_LONG).show();
                    scheduleNotification(showtime);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Booking Failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void scheduleNotification(Showtime showtime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date showDate = sdf.parse(showtime.getTime());
            if (showDate != null) {
                // Schedule 15 minutes before showtime
                long triggerTime = showDate.getTime() - (15 * 60 * 1000); 
                
                // For demo: schedule for 10 seconds from now if the time is in the past
                if (triggerTime < System.currentTimeMillis()) {
                    triggerTime = System.currentTimeMillis() + 10000;
                }

                Intent intent = new Intent(this, NotificationReceiver.class);
                intent.putExtra("title", "Movie Reminder!");
                intent.putExtra("message", "Your movie '" + movieTitle + "' starts at " + showtime.getTime());
                
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);
                
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                        // For Android 12+, just set an inexact alarm if we don't have exact permission
                        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                    }
                    Toast.makeText(this, "Reminder set for 15 mins before showtime", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}