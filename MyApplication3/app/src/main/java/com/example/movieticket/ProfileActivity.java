package com.example.movieticket;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.movieticket.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfilePic;
    private TextView tvName, tvEmail;
    private Button btnSelect, btnSave;
    private Uri selectedImageUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        ivProfilePic = findViewById(R.id.ivProfilePic);
        tvName = findViewById(R.id.tvProfileName);
        tvEmail = findViewById(R.id.tvProfileEmail);
        btnSelect = findViewById(R.id.btnSelectImage);
        btnSave = findViewById(R.id.btnSaveProfile);

        loadUserData();

        btnSelect.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

        btnSave.setOnClickListener(v -> uploadImageToFirebase());
    }

    private void loadUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null) {
                tvName.setText(user.getName());
                tvEmail.setText(user.getEmail());
                // If user has a profile pic URL in Firestore, load it (requires adding a field to User model)
                // For now, we just display name and email
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ivProfilePic.setImageURI(selectedImageUri);
        }
    }

    private void uploadImageToFirebase() {
        if (selectedImageUri != null) {
            String userId = mAuth.getCurrentUser().getUid();
            String fileName = "profile_" + userId + ".jpg";
            StorageReference ref = storageReference.child("profile_pics/" + fileName);

            ref.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        // Save URL to User profile in Firestore
                        db.collection("users").document(userId)
                                .update("profileImageUrl", downloadUrl)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(ProfileActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                                });
                    }))
                    .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
        }
    }
}