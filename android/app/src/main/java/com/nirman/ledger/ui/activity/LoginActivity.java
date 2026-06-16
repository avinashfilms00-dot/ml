package com.nirman.ledger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.nirman.ledger.R;
import com.nirman.ledger.api.ApiService;
import com.nirman.ledger.api.RetrofitClient;
import com.nirman.ledger.model.AuthResponse;
import com.nirman.ledger.util.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etEmail, etMobile;
    private TextInputLayout tilEmail, tilMobile;
    private LinearLayout llRole;
    private Spinner spRole;
    private Button btnSubmit;
    private TextView tvTitle, tvToggle;

    private boolean isLoginMode = true;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // If already logged in, go directly to main
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etEmail = findViewById(R.id.et_email);
        etMobile = findViewById(R.id.et_mobile);
        tilEmail = findViewById(R.id.til_email);
        tilMobile = findViewById(R.id.til_mobile);
        llRole = findViewById(R.id.ll_role);
        spRole = findViewById(R.id.sp_role);
        btnSubmit = findViewById(R.id.btn_submit);
        tvTitle = findViewById(R.id.tv_title);
        tvToggle = findViewById(R.id.tv_toggle);

        // Setup role spinner
        String[] roles = {"ROLE_CONTRACTOR", "ROLE_OWNER"};
        String[] roleLabels = {"Contractor", "Owner"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roleLabels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(adapter);

        btnSubmit.setOnClickListener(v -> {
            if (isLoginMode) {
                performLogin();
            } else {
                performRegister(roles);
            }
        });

        tvToggle.setOnClickListener(v -> toggleMode());
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            tvTitle.setText("Sign In");
            btnSubmit.setText("Sign In");
            tvToggle.setText("Don't have an account? Sign Up");
            tilEmail.setVisibility(View.GONE);
            tilMobile.setVisibility(View.GONE);
            llRole.setVisibility(View.GONE);
        } else {
            tvTitle.setText("Create Account");
            btnSubmit.setText("Sign Up");
            tvToggle.setText("Already have an account? Sign In");
            tilEmail.setVisibility(View.VISIBLE);
            tilMobile.setVisibility(View.VISIBLE);
            llRole.setVisibility(View.VISIBLE);
        }
    }

    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);

        ApiService api = RetrofitClient.getApi(this);
        api.login(body).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnSubmit.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    handleAuthSuccess(response.body());
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed. Check credentials.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnSubmit.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performRegister(String[] roles) {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || mobile.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedRole = roles[spRole.getSelectedItemPosition()];

        btnSubmit.setEnabled(false);
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        body.put("email", email);
        body.put("mobile", mobile);
        body.put("role", selectedRole);

        ApiService api = RetrofitClient.getApi(this);
        api.register(body).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnSubmit.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    handleAuthSuccess(response.body());
                } else {
                    Toast.makeText(LoginActivity.this, "Registration failed. Try different credentials.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnSubmit.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleAuthSuccess(AuthResponse auth) {
        sessionManager.saveToken(auth.getToken());
        sessionManager.saveUser(
                auth.getId(),
                auth.getUsername(),
                auth.getEmail(),
                auth.getMobile(),
                auth.getRole().name()
        );
        // Reset retrofit so the new token is picked up
        RetrofitClient.reset();

        Toast.makeText(this, "Welcome, " + auth.getUsername() + "!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
