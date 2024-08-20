package com.example.softnestecommerce;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CheckoutActivity extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText addressEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText cardNumberEditText;
    private EditText expiryDateEditText;
    private EditText cvvEditText;
    private RadioGroup paymentOptionsGroup;
    private View creditCardDetailsLayout;
    private Button submitOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.last_name);
        addressEditText = findViewById(R.id.address);
        emailEditText = findViewById(R.id.email);
        phoneEditText = findViewById(R.id.phone);
        cardNumberEditText = findViewById(R.id.card_number);
        expiryDateEditText = findViewById(R.id.expiry_date);
        cvvEditText = findViewById(R.id.cvv);
        paymentOptionsGroup = findViewById(R.id.payment_options);
        creditCardDetailsLayout = findViewById(R.id.credit_card_details_layout);
        submitOrderButton = findViewById(R.id.submit_order_button);

        paymentOptionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.credit_card_option) {
                creditCardDetailsLayout.setVisibility(View.VISIBLE);
            } else {
                creditCardDetailsLayout.setVisibility(View.GONE);
            }
        });

        submitOrderButton.setOnClickListener(v -> handleOrderSubmission());
    }

    private void handleOrderSubmission() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Phone Number (assuming it should be 10 digits)
        if (!phone.matches("\\d{10}")) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedPaymentId = paymentOptionsGroup.getCheckedRadioButtonId();
        RadioButton selectedPaymentOption = findViewById(selectedPaymentId);

        if (selectedPaymentOption == null) {
            Toast.makeText(this, "Please select a payment option", Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentOption = selectedPaymentOption.getText().toString();

        if (paymentOption.equals("Credit Card")) {
            String cardNumber = cardNumberEditText.getText().toString().trim();
            String expiryDate = expiryDateEditText.getText().toString().trim();
            String cvv = cvvEditText.getText().toString().trim();

            // Validate Credit Card Number (basic check)
            if (cardNumber.length() < 13 || cardNumber.length() > 19 || !cardNumber.matches("\\d+")) {
                Toast.makeText(this, "Invalid card number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate Expiry Date (MM/YY format)
            if (!expiryDate.matches("\\d{2}/\\d{2}")) {
                Toast.makeText(this, "Invalid expiry date format. Use MM/YY", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate CVV (3 or 4 digits)
            if (cvv.length() < 3 || cvv.length() > 4 || !cvv.matches("\\d+")) {
                Toast.makeText(this, "Invalid CVV", Toast.LENGTH_SHORT).show();
                return;
            }

            // Process credit card payment securely
            // Use a secure method to handle card information, like tokenization
        }

        // Handle the order submission (e.g., send data to server or save locally)
        Toast.makeText(this, "Order submitted!\nPayment Option: " + paymentOption, Toast.LENGTH_SHORT).show();

        // Clear the cart
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate to the Thank You page
        Intent intent = new Intent(CheckoutActivity.this, ThankYouActivity.class);
        startActivity(intent);
        finish();
    }
}
