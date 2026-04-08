package com.example.movieticket.utils;

import com.example.movieticket.models.Movie;
import com.example.movieticket.models.Showtime;
import com.example.movieticket.models.Theater;
import com.google.firebase.firestore.FirebaseFirestore;

public class SeedData {
    public static void seed(FirebaseFirestore db) {
        // 1. Add Theaters
        Theater t1 = new Theater("theater_1", "CGV Vincom Center", "72 Le Thanh Ton, Dist 1");
        Theater t2 = new Theater("theater_2", "Lotte Cinema", "123 Cong Hoa, Tan Binh");
        Theater t3 = new Theater("theater_3", "BHD Star Cineplex", "3C 3 Thang 2, Dist 10");
        
        db.collection("theaters").document(t1.getId()).set(t1);
        db.collection("theaters").document(t2.getId()).set(t2);
        db.collection("theaters").document(t3.getId()).set(t3);

        // 2. Add Movies with high-quality images
        Movie m1 = new Movie("movie_1", "Avengers: Endgame", 
                "After the devastating events of Infinity War, the universe is in ruins. With the help of remaining allies, the Avengers assemble once more.", 
                "https://image.tmdb.org/t/p/w500/or06vSneyLc5rqZPqcbiO9uAD1p.jpg", "Action/Sci-Fi", 181);
        
        Movie m2 = new Movie("movie_2", "Joker", 
                "In Gotham City, mentally troubled comedian Arthur Fleck is disregarded and mistreated by society. He then embarks on a downward spiral of revolution.", 
                "https://image.tmdb.org/t/p/w500/udDclKVUZRUueQ367hg9q6UluWp.jpg", "Crime/Drama", 122);

        Movie m3 = new Movie("movie_3", "Spider-Man: No Way Home", 
                "With Spider-Man's identity now revealed, Peter asks Doctor Strange for help. When a spell goes wrong, dangerous foes from other worlds start to appear.", 
                "https://image.tmdb.org/t/p/w500/1g0zzvWwsasvYMRSfnq6S6Fyhll.jpg", "Action/Adventure", 148);

        Movie m4 = new Movie("movie_4", "Interstellar", 
                "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.", 
                "https://image.tmdb.org/t/p/w500/gEU2QniE6E7oBvP6pFLo2vYgQ3z.jpg", "Sci-Fi/Drama", 169);

        Movie m5 = new Movie("movie_5", "The Dark Knight", 
                "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological tests.", 
                "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDp9aqSCbtpPk4u2ZAc.jpg", "Action/Crime", 152);

        Movie m6 = new Movie("movie_6", "Avatar: Way of Water", 
                "Jake Sully lives with his newfound family formed on the extrasolar moon Pandora. Once a familiar threat returns, Jake must work with Neytiri.", 
                "https://image.tmdb.org/t/p/w500/t6RUi1Y2AhS6ChJeqKJ69SllGMD.jpg", "Sci-Fi/Action", 192);

        db.collection("movies").document(m1.getId()).set(m1);
        db.collection("movies").document(m2.getId()).set(m2);
        db.collection("movies").document(m3.getId()).set(m3);
        db.collection("movies").document(m4.getId()).set(m4);
        db.collection("movies").document(m5.getId()).set(m5);
        db.collection("movies").document(m6.getId()).set(m6);

        // 3. Add Showtimes for all movies
        addShowtimes(db, "movie_1", "theater_1", "19:30", 15.0);
        addShowtimes(db, "movie_2", "theater_2", "20:00", 12.0);
        addShowtimes(db, "movie_3", "theater_3", "18:00", 14.0);
        addShowtimes(db, "movie_4", "theater_1", "21:00", 13.0);
        addShowtimes(db, "movie_5", "theater_2", "22:30", 12.5);
        addShowtimes(db, "movie_6", "theater_3", "17:30", 16.0);
    }

    private static void addShowtimes(FirebaseFirestore db, String mId, String tId, String time, double price) {
        String sId = "s_" + mId + "_" + tId.substring(theater_.length());
        Showtime s = new Showtime(sId, mId, tId, "2024-12-25 " + time, price);
        db.collection("showtimes").document(sId).set(s);
    }
    
    private static final String theater_ = "theater_";
}