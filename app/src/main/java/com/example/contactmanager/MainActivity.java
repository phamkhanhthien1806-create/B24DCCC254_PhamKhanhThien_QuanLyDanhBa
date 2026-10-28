package com.example.contactmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactmanager.adapter.ContactAdapter;
import com.example.contactmanager.model.Contact;

import java.util.ArrayList;

/**
 * Màn hình 2: Danh sách liên hệ (MainActivity)
 * Quản lý danh sách liên hệ bằng ArrayList<Contact>, hiển thị khối thống kê
 * và xử lý thêm, xem chi tiết, xóa, cập nhật trạng thái yêu thích.
 */
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACT = "contact";
    public static final String EXTRA_POSITION = "contact_position";
    public static final String EXTRA_EXISTING_PHONES = "existing_phones";
    public static final String EXTRA_IS_DELETED = "is_deleted";

    private TextView tvTotalCount;
    private TextView tvFavoriteCount;
    private RecyclerView rcvContacts;
    private Button btnAddContact;

    private ArrayList<Contact> contactList;
    private ContactAdapter contactAdapter;

    private ActivityResultLauncher<Intent> addContactLauncher;
    private ActivityResultLauncher<Intent> detailContactLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupData();
        setupLaunchers();
        setupListeners();
        updateStatistics();
    }

    private void initViews() {
        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvFavoriteCount = findViewById(R.id.tvFavoriteCount);
        rcvContacts = findViewById(R.id.rcvContacts);
        btnAddContact = findViewById(R.id.btnAddContact);
    }

    private void setupData() {
        // Khởi tạo 3 dữ liệu mẫu ban đầu theo yêu cầu đề bài
        contactList = Contact.getInitialContacts();

        contactAdapter = new ContactAdapter(this, contactList, (contact, position) -> {
            // Mở màn hình chi tiết khi nhấn vào một dòng liên hệ
            Intent intent = new Intent(MainActivity.this, ContactDetailActivity.class);
            intent.putExtra(EXTRA_CONTACT, contact);
            intent.putExtra(EXTRA_POSITION, position);
            detailContactLauncher.launch(intent);
        });

        rcvContacts.setLayoutManager(new LinearLayoutManager(this));
        rcvContacts.setAdapter(contactAdapter);
    }

    private void setupLaunchers() {
        // Đón nhận kết quả khi thêm mới từ AddContactActivity
        addContactLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Contact newContact = IntentCompat.getSerializableExtra(result.getData(), EXTRA_CONTACT, Contact.class);
                        if (newContact != null) {
                            contactList.add(newContact);
                            contactAdapter.notifyItemInserted(contactList.size() - 1);
                            rcvContacts.scrollToPosition(contactList.size() - 1);
                            updateStatistics();
                            Toast.makeText(this, R.string.msg_contact_added, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Đón nhận kết quả cập nhật / xóa từ ContactDetailActivity
        detailContactLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() != null) {
                        Intent data = result.getData();
                        int position = data.getIntExtra(EXTRA_POSITION, -1);
                        boolean isDeleted = data.getBooleanExtra(EXTRA_IS_DELETED, false);

                        if (position >= 0 && position < contactList.size()) {
                            if (isDeleted) {
                                // Xóa liên hệ
                                contactList.remove(position);
                                contactAdapter.notifyItemRemoved(position);
                                contactAdapter.notifyItemRangeChanged(position, contactList.size() - position);
                                updateStatistics();
                                Toast.makeText(this, R.string.msg_contact_deleted, Toast.LENGTH_SHORT).show();
                            } else if (data.hasExtra(EXTRA_CONTACT)) {
                                // Cập nhật trạng thái liên hệ (ví dụ: đổi trạng thái yêu thích)
                                Contact updatedContact = IntentCompat.getSerializableExtra(data, EXTRA_CONTACT, Contact.class);
                                if (updatedContact != null) {
                                    contactList.set(position, updatedContact);
                                    contactAdapter.notifyItemChanged(position);
                                    updateStatistics();
                                }
                            }
                        }
                    }
                }
        );
    }

    private void setupListeners() {
        // Mở màn hình Thêm liên hệ
        btnAddContact.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
            // Gửi danh sách số điện thoại đã tồn tại để kiểm tra trùng lặp
            ArrayList<String> existingPhones = new ArrayList<>();
            for (Contact c : contactList) {
                if (c.getPhone() != null) {
                    existingPhones.add(c.getPhone().trim());
                }
            }
            intent.putStringArrayListExtra(EXTRA_EXISTING_PHONES, existingPhones);
            addContactLauncher.launch(intent);
        });
    }

    /**
     * Cập nhật khối thống kê tổng liên hệ và số liên hệ yêu thích.
     */
    public void updateStatistics() {
        int total = contactList.size();
        int favorites = 0;
        for (Contact contact : contactList) {
            if (contact.isFavorite()) {
                favorites++;
            }
        }
        tvTotalCount.setText(String.valueOf(total));
        tvFavoriteCount.setText(String.valueOf(favorites));
    }
}
