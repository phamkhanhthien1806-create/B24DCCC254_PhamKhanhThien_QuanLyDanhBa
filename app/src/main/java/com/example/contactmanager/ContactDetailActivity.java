package com.example.contactmanager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.contactmanager.model.Contact;

/**
 * Màn hình 4: Chi tiết liên hệ (ContactDetailActivity)
 * Hiển thị đầy đủ thông tin của liên hệ đã chọn.
 * Cho phép gọi điện (ACTION_DIAL), đổi trạng thái yêu thích hoặc xóa liên hệ.
 */
public class ContactDetailActivity extends AppCompatActivity {

    private ImageButton btnBackTop;
    private TextView tvAvatarDetail;
    private TextView tvNameDetail;
    private TextView tvPhoneDetail;
    private TextView tvGroupDetail;
    private TextView tvGenderDetail;
    private TextView tvNotesDetail;
    private CheckBox cbFavoriteDetail;

    private Button btnCall;
    private Button btnBackBottom;
    private Button btnDelete;

    private Contact contact;
    private int contactPosition = -1;

    private final int[] avatarColors = {
            R.color.avatar_an,
            R.color.avatar_binh,
            R.color.avatar_chau,
            R.color.avatar_other1,
            R.color.avatar_other2,
            R.color.avatar_other3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        initViews();
        loadDataFromIntent();
        bindDataToViews();
        setupListeners();
    }

    private void initViews() {
        btnBackTop = findViewById(R.id.btnBackTop);
        tvAvatarDetail = findViewById(R.id.tvAvatarDetail);
        tvNameDetail = findViewById(R.id.tvNameDetail);
        tvPhoneDetail = findViewById(R.id.tvPhoneDetail);
        tvGroupDetail = findViewById(R.id.tvGroupDetail);
        tvGenderDetail = findViewById(R.id.tvGenderDetail);
        tvNotesDetail = findViewById(R.id.tvNotesDetail);
        cbFavoriteDetail = findViewById(R.id.cbFavoriteDetail);

        btnCall = findViewById(R.id.btnCall);
        btnBackBottom = findViewById(R.id.btnBackBottom);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void loadDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            contact = IntentCompat.getSerializableExtra(intent, MainActivity.EXTRA_CONTACT, Contact.class);
            contactPosition = intent.getIntExtra(MainActivity.EXTRA_POSITION, -1);
        }
    }

    private void bindDataToViews() {
        if (contact == null) {
            Toast.makeText(this, "Không tìm thấy thông tin liên hệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tên và SĐT định dạng chuẩn
        tvNameDetail.setText(contact.getName());
        tvPhoneDetail.setText(contact.getFormattedPhone());

        // Ký tự Avatar và màu sắc đồng bộ (Pastel sang trọng)
        String letter = contact.getAvatarLetter();
        tvAvatarDetail.setText(letter);

        int colorRes;
        switch (letter) {
            case "A":
                colorRes = R.color.avatar_an;
                break;
            case "B":
                colorRes = R.color.avatar_binh;
                break;
            case "C":
                colorRes = R.color.avatar_chau;
                break;
            default:
                int index = Math.abs(contact.getName().hashCode()) % avatarColors.length;
                colorRes = avatarColors[index];
                break;
        }

        Drawable bgDrawable = ContextCompat.getDrawable(this, R.drawable.bg_avatar_circle);
        if (bgDrawable != null) {
            Drawable wrapped = DrawableCompat.wrap(bgDrawable.mutate());
            DrawableCompat.setTint(wrapped, ContextCompat.getColor(this, colorRes));
            tvAvatarDetail.setBackground(wrapped);
        }

        // Nhóm và Giới tính
        tvGroupDetail.setText(contact.getGroup() != null ? contact.getGroup() : "");
        tvGenderDetail.setText(contact.getGender() != null ? contact.getGender() : "");

        // Ghi chú
        String notes = contact.getNotes();
        if (notes != null && !notes.trim().isEmpty()) {
            tvNotesDetail.setText(notes);
        } else {
            tvNotesDetail.setText(R.string.notes_empty);
        }

        // Yêu thích CheckBox
        cbFavoriteDetail.setChecked(contact.isFavorite());
    }

    private void setupListeners() {
        // Lắng nghe thay đổi trạng thái yêu thích ngay trên màn hình chi tiết
        cbFavoriteDetail.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (contact != null) {
                contact.setFavorite(isChecked);
            }
        });

        // 1. Nút Gọi điện: Mở màn hình quay số bằng Implicit Intent (ACTION_DIAL)
        btnCall.setOnClickListener(v -> makePhoneCall());

        // 2. Nút Quay lại (trên header và dưới cùng)
        btnBackTop.setOnClickListener(v -> finishWithResult());
        btnBackBottom.setOnClickListener(v -> finishWithResult());

        // Bắt nút back hệ thống (nút back vật lý hoặc cử chỉ vuốt)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishWithResult();
            }
        });

        // 3. Nút Xóa liên hệ: Hiển thị dialog xác nhận trước khi xóa
        btnDelete.setOnClickListener(v -> confirmDeleteContact());
    }

    /**
     * Dùng Intent ngầm định (ACTION_DIAL) để mở màn hình quay số với số điện thoại của liên hệ.
     */
    private void makePhoneCall() {
        if (contact == null || contact.getPhone() == null || contact.getPhone().isEmpty()) {
            Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + contact.getPhone().trim()));
        startActivity(dialIntent);
    }

    /**
     * Xác nhận xóa liên hệ và trả kết quả về MainActivity.
     */
    private void confirmDeleteContact() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setNegativeButton(R.string.dialog_btn_cancel, null)
                .setPositiveButton(R.string.dialog_btn_confirm, (dialog, which) -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(MainActivity.EXTRA_IS_DELETED, true);
                    resultIntent.putExtra(MainActivity.EXTRA_POSITION, contactPosition);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .show();
    }

    /**
     * Đóng màn hình và trả đối tượng Contact (đã cập nhật trạng thái yêu thích nếu có) về MainActivity.
     */
    private void finishWithResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(MainActivity.EXTRA_CONTACT, contact);
        resultIntent.putExtra(MainActivity.EXTRA_POSITION, contactPosition);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
