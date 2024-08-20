package com.example.softnestecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Admin_Category extends AppCompatActivity {
    private ImageView hoodies, accessories;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_category);

        // Initialize the ImageView widgets
        hoodies = findViewById(R.id.hoodies);
        accessories = findViewById(R.id.accessories);
        logoutButton = findViewById(R.id.logoutButton);

        // Set onClick listener for hoodies
        hoodies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_Category.this, AdminActivity.class);
                intent.putExtra("category", "hoodies");
                startActivity(intent);
            }
        });

        // Set onClick listener for accessories
        accessories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_Category.this, AdminActivity.class);
                intent.putExtra("category", "accessories");
                startActivity(intent);
            }
        });

        // Set onClick listener for logoutButton
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity
                Intent intent = new Intent(Admin_Category.this, MainActivity.class);
                startActivity(intent);
                // Optionally finish this activity
                finish();
            }
        });
    }
}
