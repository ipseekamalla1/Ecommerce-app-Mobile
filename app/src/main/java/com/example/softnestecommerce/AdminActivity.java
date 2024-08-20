package com.example.softnestecommerce;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AdminActivity extends AppCompatActivity {

    private static final int GalleryPick = 1;

    private String categoryName, saveCurrentDate, saveCurrentTime, productRandomKey;
    private Button btnAddProduct, btnLogout;
    private EditText etProductName, etProductDescription, etPurchasePrice;
    private ImageView select_image;
    private Uri imageUri;

    private StorageReference productImagesRef;
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_add_product);

        categoryName = getIntent().getStringExtra("category");
        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        Toast.makeText(this, categoryName, Toast.LENGTH_SHORT).show();

        // Initialize UI components
        etProductName = findViewById(R.id.etProductName);
        etProductDescription = findViewById(R.id.etProductDescription);
        etPurchasePrice = findViewById(R.id.etPurchasePrice);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnLogout = findViewById(R.id.Logout);
        select_image = findViewById(R.id.select_image);

        select_image.setOnClickListener(v -> OpenGallery());
        btnAddProduct.setOnClickListener(view -> ValidateProductData());
        btnLogout.setOnClickListener(view -> Logout());
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            select_image.setImageURI(imageUri);
        }
    }

    private void ValidateProductData() {
        String productName = etProductName.getText().toString();
        String productDescription = etProductDescription.getText().toString();
        String purchasePrice = etPurchasePrice.getText().toString();

        if (imageUri == null) {
            Toast.makeText(this, "Product image is mandatory", Toast.LENGTH_SHORT).show();
        } else if (productName.isEmpty()) {
            Toast.makeText(this, "Product name is mandatory", Toast.LENGTH_SHORT).show();
        } else if (productDescription.isEmpty()) {
            Toast.makeText(this, "Product description is mandatory", Toast.LENGTH_SHORT).show();
        } else if (purchasePrice.isEmpty()) {
            Toast.makeText(this, "Product price is mandatory", Toast.LENGTH_SHORT).show();
        } else {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM_dd_yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH_mm_ss");
        saveCurrentTime = currentTime.format(calendar.getTime());

        // Generate a unique key for the product
        productRandomKey = saveCurrentDate + saveCurrentTime + new Random().nextInt(10000);

        // Reference to the Firebase Storage location
        StorageReference filePath = productImagesRef.child("Product_" + productRandomKey + ".jpg");

        // Upload the image file to Firebase Storage
        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return filePath.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                SaveProductInfoToDatabase(downloadUri.toString());
            } else {
                String message = task.getException().toString();
                Toast.makeText(AdminActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void SaveProductInfoToDatabase(String imageUrl) {
        Map<String, Object> productData = new HashMap<>();
        productData.put("productName", etProductName.getText().toString());
        productData.put("productDescription", etProductDescription.getText().toString());
        productData.put("purchasePrice", etPurchasePrice.getText().toString());
        productData.put("imageUrl", imageUrl);

        productsRef.child(productRandomKey).updateChildren(productData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                    ClearInputFields();
                    // Redirect to CategoryActivity
                    Intent categoryIntent = new Intent(AdminActivity.this, Admin_Category.class);
                    startActivity(categoryIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                });
    }

    private void ClearInputFields() {
        etProductName.setText("");
        etProductDescription.setText("");
        etPurchasePrice.setText("");
        select_image.setImageResource(R.drawable.add_photo);
    }

    private void Logout() {
        Intent loginIntent = new Intent(AdminActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
