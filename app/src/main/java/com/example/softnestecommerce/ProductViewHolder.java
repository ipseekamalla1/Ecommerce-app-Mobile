// ProductViewHolder.java
package com.example.softnestecommerce;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public ImageView productImage;
    public TextView productName;
    public TextView productPrice;
    public TextView productDescription;
    public Button addToCartButton;

    private ItemClickListener itemClickListener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        // Initialize the views
        productImage = itemView.findViewById(R.id.product_image);
        productName = itemView.findViewById(R.id.product_name);
        productPrice = itemView.findViewById(R.id.product_price);
        productDescription = itemView.findViewById(R.id.product_description);
        addToCartButton = itemView.findViewById(R.id.add_to_cart_button);

        // Set onClickListener for the whole itemView and addToCartButton
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        addToCartButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(view, getAdapterPosition(), false);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(view, getAdapterPosition(), true);
            return true;
        }
        return false;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
