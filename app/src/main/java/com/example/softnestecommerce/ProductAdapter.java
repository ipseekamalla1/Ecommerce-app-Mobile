package com.example.softnestecommerce;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> productList;
    private final ItemClickListener listener;

    public ProductAdapter(Context context, List<Product> productList, ItemClickListener listener) {
        this.context = context;  // Initialize context with the provided parameter
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_item_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getProductName());
        holder.productPrice.setText("$" + product.getPurchasePrice());
        holder.productDescription.setText(product.getProductDescription());
        Glide.with(context).load(product.getImageUrl()).into(holder.productImage);
        Log.d("ProductAdapter", "Loading image: " + product.getImageUrl());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("productName", product.getProductName());
            intent.putExtra("productDescription", product.getProductDescription());
            intent.putExtra("purchasePrice", product.getPurchasePrice());
            intent.putExtra("quantity", product.getQuantity());
            intent.putExtra("imageUrl", product.getImageUrl());
            Log.d("ProductAdapter", "Loading image from URL: " + product.getImageUrl());

            context.startActivity(intent);
        });

        holder.addToCartButton.setOnClickListener(v -> {
            addToCart(product.getProductName(), 1, product.getPurchasePrice()); // Default quantity is 1
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void addToCart(String productName, int quantity, String price) {
        double productPrice;
        try {
            productPrice = Double.parseDouble(price);
        } catch (NumberFormatException e) {
            Toast.makeText(context, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("Cart", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Create a unique key for each product
        String key = productName + "_" + System.currentTimeMillis();

        // Save item details in format: productName:quantity:price
        String itemDetails = productName + ":" + quantity + ":" + productPrice;
        editor.putString(key, itemDetails);
        editor.apply();

        // Show a Toast message
        Toast.makeText(context, "Item added to cart!", Toast.LENGTH_SHORT).show();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        public Button addToCartButton;
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productDescription;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productDescription = itemView.findViewById(R.id.product_description);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }
    }
}
