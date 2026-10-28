package com.example.contactmanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Màn hình 1: Đăng nhập (LoginActivity)
 * Màn hình đầu tiên khi mở ứng dụng.
 * Kiểm tra tài khoản cố định admin / 123456 và chuyển sang MainActivity.
 */
public class LoginActivity extends AppCompatActivity {

    // Tài khoản mẫu cố định theo yêu cầu đề bài
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "123456";

    private EditText edtUsername;
    private EditText edtPassword;
    private TextView tvError;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        tvError = findViewById(R.id.tvError);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void setupListeners() {
        // Ẩn thông báo lỗi khi người dùng bắt đầu nhập lại
        TextWatcher clearErrorWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tvError.getVisibility() == View.VISIBLE) {
                    tvError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        edtUsername.addTextChangedListener(clearErrorWatcher);
        edtPassword.addTextChangedListener(clearErrorWatcher);

        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // 1. Kiểm tra trường hợp để trống
        if (username.isEmpty() || password.isEmpty()) {
            tvError.setText(R.string.error_empty_credentials);
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        // 2. Kiểm tra thông tin tài khoản cố định
        if (DEFAULT_USERNAME.equals(username) && DEFAULT_PASSWORD.equals(password)) {
            tvError.setVisibility(View.GONE);

            // Chuyển sang MainActivity bằng Intent
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

            // Gọi finish() để nút Back từ MainActivity không quay lại màn hình Login
            finish();
        } else {
            // Sai tên đăng nhập hoặc mật khẩu
            tvError.setText(R.string.error_invalid_credentials);
            tvError.setVisibility(View.VISIBLE);
        }
    }
}
