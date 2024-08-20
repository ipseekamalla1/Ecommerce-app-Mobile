package com.example.softnestecommerce;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private LinearLayout cartItemsLayout;
    private Button checkoutButton;
    private TextView totalPriceTextView;

    private double totalPrice = 0.0;
    private static final double TAX_RATE = 0.07; // Example tax rate of 7%

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartItemsLayout = findViewById(R.id.cart_items_layout);
        checkoutButton = findViewById(R.id.checkout_button);
        totalPriceTextView = findViewById(R.id.total_price_text_view);

        // Load and display cart items
        loadCartItems();

        checkoutButton.setOnClickListener(v -> {
            // Navigate to the checkout activity
            Intent intent = new Intent(this, CheckoutActivity.class); // Ensure you have a CheckoutActivity
            startActivity(intent);
        });
    }

    private void loadCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE);
        Map<String, ?> cartItems = sharedPreferences.getAll();

        cartItemsLayout.removeAllViews();
        totalPrice = 0.0;

        for (Map.Entry<String, ?> entry : cartItems.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                String itemDetails = (String) value;
                Log.d("CartActivity", "Item Details: " + itemDetails);
                String[] details = itemDetails.split(":");

                // Ensure itemDetails array has exactly 3 elements
                if (details.length == 3) {
                    String productName = details[0];
                    int quantity;
                    double price;
                    try {
                        quantity = Integer.parseInt(details[1]);
                        price = Double.parseDouble(details[2]);
                    } catch (NumberFormatException e) {
                        // Skip this item if there's an error in parsing
                        continue;
                    }

                    double itemTotal = quantity * price;
                    totalPrice += itemTotal;

                    View cartItemView = getLayoutInflater().inflate(R.layout.cart_item, cartItemsLayout, false);

                    TextView itemNameTextView = cartItemView.findViewById(R.id.item_name);
                    TextView itemQuantityTextView = cartItemView.findViewById(R.id.item_quantity);
                    TextView itemPriceTextView = cartItemView.findViewById(R.id.item_price);
                    TextView itemTotalPriceTextView = cartItemView.findViewById(R.id.item_total_price);

                    Button removeButton = cartItemView.findViewById(R.id.remove_button);
                    Button decrementButton = cartItemView.findViewById(R.id.decrement_button);
                    Button incrementButton = cartItemView.findViewById(R.id.increment_button);

                    itemNameTextView.setText(productName);
                    itemQuantityTextView.setText(String.valueOf(quantity));
                    itemPriceTextView.setText(String.format("$%.2f", price));
                    itemTotalPriceTextView.setText(String.format("$%.2f", itemTotal));

                    removeButton.setOnClickListener(v -> {
                        removeItem(productName);
                    });

                    decrementButton.setOnClickListener(v -> {
                        Log.d("CartActivity", "Decrement button clicked for " + productName);
                        updateQuantity(productName, quantity - 1);
                    });

                    incrementButton.setOnClickListener(v -> {
                        Log.d("CartActivity", "Increment button clicked for " + productName);
                        updateQuantity(productName, quantity + 1);
                    });

                    cartItemsLayout.addView(cartItemView);
                } else {
                    Log.e("CartActivity", "Unexpected format for item: " + itemDetails);
                    Toast.makeText(this, "Error loading item: " + entry.getKey(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("CartActivity", "Expected string but got: " + value.getClass().getName());
                Toast.makeText(this, "Error loading item: " + entry.getKey(), Toast.LENGTH_SHORT).show();
            }
        }

        double totalWithTax = totalPrice + (totalPrice * TAX_RATE);
        totalPriceTextView.setText(String.format("Total: $%.2f", totalWithTax));
    }

    private void removeItem(String productName) {
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(productName);
        editor.apply();

        // Check if the cart is empty now
        if (sharedPreferences.getAll().isEmpty()) {
            editor.clear(); // Clear SharedPreferences if no items left
            editor.apply();
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Item removed from cart!", Toast.LENGTH_SHORT).show();
        }

        // Refresh cart items
        loadCartItems();
    }

    private void updateQuantity(String productName, int newQuantity) {
        if (newQuantity < 1) return;

        SharedPreferences sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE);
        String itemDetails = sharedPreferences.getString(productName, null);

        if (itemDetails != null) {
            String[] details = itemDetails.split(":");
            if (details.length == 3) {
                String price = details[2];

                SharedPreferences.Editor editor = sharedPreferences.edit();
                String updatedItem = productName + ":" + newQuantity + ":" + price;
                editor.putString(productName, updatedItem);
                editor.apply();

                // Refresh cart items and recalculate total price
                loadCartItems();
            }
        }
    }
}
