package com.example.softnestecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button loginAccount;
    private EditText loginUsername, loginPassword;
    private DatabaseReference RootRef;
    private TextView AdminLink, NotAdmin, textViewSignUp;
    private String parentDBname = "Users"; // Default value for normal users

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); // Ensure this matches your layout file name

        loginAccount = findViewById(R.id.loginAccount);
        loginUsername = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);
        AdminLink = findViewById(R.id.AdminLink);
        NotAdmin = findViewById(R.id.NotAdmin);
        textViewSignUp = findViewById(R.id.textViewSignUp);

        // Initialize Firebase
        RootRef = FirebaseDatabase.getInstance().getReference();

        loginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentDBname = "Admins"; // Set to Admin database
                Toast.makeText(LoginActivity.this, "Switched to Admin login", Toast.LENGTH_SHORT).show();
                AdminLink.setVisibility(View.GONE); // Hide Admin link after selection
                NotAdmin.setVisibility(View.VISIBLE); // Show NotAdmin link
            }
        });

        NotAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentDBname = "Users"; // Set to Users database
                Toast.makeText(LoginActivity.this, "Switched to User login", Toast.LENGTH_SHORT).show();
                NotAdmin.setVisibility(View.GONE); // Hide NotAdmin link after selection
                AdminLink.setVisibility(View.VISIBLE); // Show Admin link
            }
        });

        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class); // Navigate to RegisterActivity
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String username = loginUsername.getText().toString();
        String password = loginPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        validateUser(username, password);
    }

    private void validateUser(final String username, final String password) {
        RootRef.child(parentDBname).orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String storedPassword = userSnapshot.child("password").getValue(String.class);
                        if (password.equals(storedPassword)) {
                            // Redirect to the appropriate activity based on user type
                            if (parentDBname.equals("Admins")) {
                                Intent intent = new Intent(LoginActivity.this, Admin_Category.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.i(TAG, "Redirecting to Products Activity");
                                Intent intent = new Intent(LoginActivity.this, Products_Activity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
