package com.example.softnestecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Products_Activity extends AppCompatActivity {

    private static final String TAG = "ProductsActivity";

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private DatabaseReference productsRef;

    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_page);
        logoutButton = findViewById(R.id.logoutBtn);

        Log.i(TAG, "Activity Created");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList, new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position, boolean isLongClick) {
                // Handle item click
                if (isLongClick) {
                    // Handle long click event if needed
                } else {
                    // Handle normal click event
                }
            }
        });

        recyclerView.setAdapter(adapter);

        // Initialize Firebase Database reference
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        // Fetch data from Firebase
        fetchProductsFromDatabase();
    }

    private void fetchProductsFromDatabase() {
        Log.i(TAG, "Fetching data from database");
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                Log.i(TAG, "DataSnapshot received");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                        // Log product details
                        Log.d(TAG, "Product Name: " + product.getProductName());
                        Log.d(TAG, "Product Price: " + product.getPurchasePrice());
                        Log.d(TAG, "Product Description: " + product.getProductDescription());
                        Log.d(TAG, "Product Image URL: " + product.getImageUrl());
                    } else {
                        Log.d(TAG, "Product is null");
                    }
                }
                adapter.notifyDataSetChanged(); // Notify the adapter that data has changed
                Log.i(TAG, "Adapter notified of data change");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity
                Intent intent = new Intent(Products_Activity.this, MainActivity.class);
                startActivity(intent);
                // Optionally finish this activity
                finish();
            }
        });
    }
}
