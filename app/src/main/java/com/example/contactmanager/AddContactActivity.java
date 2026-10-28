package com.example.contactmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.contactmanager.model.Contact;

import java.util.ArrayList;

/**
 * Màn hình 3: Thêm liên hệ (AddContactActivity)
 * Cho phép nhập thông tin liên hệ mới, kiểm tra hợp lệ và trả về cho MainActivity.
 */
public class AddContactActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText edtName;
    private EditText edtPhone;
    private Spinner spnGroup;
    private RadioGroup rgGender;
    private CheckBox cbFavorite;
    private EditText edtNotes;
    private Button btnSaveContact;

    private ArrayList<String> existingPhones;
    private static final String[] GROUPS = {"Gia đình", "Bạn bè", "Công việc", "Khác"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        initViews();
        setupSpinner();
        loadExistingPhones();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        spnGroup = findViewById(R.id.spnGroup);
        rgGender = findViewById(R.id.rgGender);
        cbFavorite = findViewById(R.id.cbFavorite);
        edtNotes = findViewById(R.id.edtNotes);
        btnSaveContact = findViewById(R.id.btnSaveContact);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                GROUPS
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGroup.setAdapter(adapter);

        // Mặc định chọn nhóm "Bạn bè" theo mockup Hình 5
        spnGroup.setSelection(1);
    }

    private void loadExistingPhones() {
        existingPhones = getIntent().getStringArrayListExtra(MainActivity.EXTRA_EXISTING_PHONES);
        if (existingPhones == null) {
            existingPhones = new ArrayList<>();
        }
    }

    private void setupListeners() {
        // Nút quay lại trên thanh tiêu đề
        btnBack.setOnClickListener(v -> finish());

        // Nút Lưu liên hệ
        btnSaveContact.setOnClickListener(v -> saveContact());
    }

    private void saveContact() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        // 1. Kiểm tra họ và tên không được để trống
        if (name.isEmpty()) {
            edtName.setError(getString(R.string.error_empty_name));
            edtName.requestFocus();
            return;
        }

        // 2. Kiểm tra số điện thoại: 10 chữ số và bắt đầu bằng số 0
        if (!phone.matches("^0\\d{9}$")) {
            edtPhone.setError(getString(R.string.error_invalid_phone));
            edtPhone.requestFocus();
            return;
        }

        // 3. Kiểm tra số điện thoại đã tồn tại trong danh bạ chưa
        if (existingPhones.contains(phone)) {
            edtPhone.setError(getString(R.string.error_duplicate_phone));
            edtPhone.requestFocus();
            return;
        }

        // 4. Lấy các thông tin còn lại
        String group = spnGroup.getSelectedItem().toString();

        String gender = "Nam";
        int checkedGenderId = rgGender.getCheckedRadioButtonId();
        if (checkedGenderId == R.id.rbFemale) {
            gender = "Nữ";
        } else if (checkedGenderId == R.id.rbOther) {
            gender = "Khác";
        }

        boolean isFavorite = cbFavorite.isChecked();
        String notes = edtNotes.getText().toString().trim();

        // 5. Tạo đối tượng Contact mới và trả về MainActivity
        Contact newContact = new Contact(name, phone, group, gender, isFavorite, notes);

        Intent resultIntent = new Intent();
        resultIntent.putExtra(MainActivity.EXTRA_CONTACT, newContact);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
