package com.example.softnestecommerce;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName;
    private TextView productDescription;
    private TextView productPrice;
    private TextView quantityDisplay;
    private Button decrementButton;
    private Button incrementButton;
    private Button addToCartButton;
    private Button goToCartButton;

    private Button  btnLogout;

    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products_detail);

        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price);
        quantityDisplay = findViewById(R.id.quantity_display);
        decrementButton = findViewById(R.id.decrement_button);
        incrementButton = findViewById(R.id.increment_button);
        addToCartButton = findViewById(R.id.add_to_cart_button);
        goToCartButton = findViewById(R.id.go_to_cart_button);
        btnLogout = findViewById(R.id.Logout);

        // Get product data from intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("productName");
        String description = intent.getStringExtra("productDescription");
        String price = intent.getStringExtra("purchasePrice");
        String imageUrl = intent.getStringExtra("imageUrl");

        // Set product data to views
        productName.setText(name);
        productDescription.setText(description);
        productPrice.setText("$ "+price);
        Glide.with(this).load(imageUrl).into(productImage);

        // Set initial quantity
        quantityDisplay.setText(String.valueOf(quantity));

        btnLogout.setOnClickListener(view -> Logout());

        // Handle increment and decrement actions
        incrementButton.setOnClickListener(v -> {
            quantity++;
            updateQuantityDisplay();
        });

        decrementButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityDisplay();
            }
        });

        // Add click listeners for buttons
        addToCartButton.setOnClickListener(v -> {
            addToCart(name, quantity,price);
        });

        goToCartButton.setOnClickListener(v -> {
            // Navigate to the cart activity
            Intent intent1 = new Intent(this, CartActivity.class);
            startActivity(intent1);
        });
    }

    private void updateQuantityDisplay() {
        quantityDisplay.setText(String.valueOf(quantity));
    }

    private void Logout() {
        Intent loginIntent = new Intent(ProductDetailsActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void addToCart(String productName, int quantity, String price) {
        double productPrice;
        try {
            productPrice = Double.parseDouble(price);
        } catch (NumberFormatException e) {
            Toast.makeText(ProductDetailsActivity.this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save item details in format: productName:quantity:price
        String itemDetails = productName + ":" + quantity + ":" + productPrice;
        editor.putString(productName, itemDetails);
        editor.apply();

        // Show a Toast message
        Toast.makeText(ProductDetailsActivity.this, "Item added to cart!", Toast.LENGTH_SHORT).show();
    }

}
