package com.example.movieticket;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticket.adapters.TheaterAdapter;
import com.example.movieticket.models.Theater;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TheatersActivity extends AppCompatActivity {

    private RecyclerView rvTheaters;
    private TheaterAdapter adapter;
    private List<Theater> theaterList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theaters);

        Toolbar toolbar = findViewById(R.id.toolbarTheaters);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Cinema Theaters");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        rvTheaters = findViewById(R.id.rvTheaters);
        rvTheaters.setLayoutManager(new LinearLayoutManager(this));

        theaterList = new ArrayList<>();
        adapter = new TheaterAdapter(theaterList);
        rvTheaters.setAdapter(adapter);

        fetchTheaters();
    }

    private void fetchTheaters() {
        db.collection("theaters")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        theaterList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Theater theater = document.toObject(Theater.class);
                            theater.setId(document.getId());
                            theaterList.add(theater);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error fetching theaters", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}