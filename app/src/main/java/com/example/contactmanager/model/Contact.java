package com.example.contactmanager.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Model class đại diện cho một liên hệ trong danh bạ.
 * Implement Serializable để có thể truyền qua Intent giữa các Activity.
 */
public class Contact implements Serializable {
    private String name;        // Họ và tên
    private String phone;       // Số điện thoại (10 chữ số, bắt đầu bằng 0)
    private String group;       // Nhóm (Gia đình, Bạn bè, Công việc, Khác)
    private String gender;      // Giới tính (Nam, Nữ, Khác)
    private boolean isFavorite; // Trạng thái yêu thích
    private String notes;       // Ghi chú

    public Contact() {
    }

    public Contact(String name, String phone, String group, String gender, boolean isFavorite, String notes) {
        this.name = name;
        this.phone = phone;
        this.group = group;
        this.gender = gender;
        this.isFavorite = isFavorite;
        this.notes = notes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Lấy chữ cái in hoa đại diện từ tên chính (từ cuối cùng) của liên hệ.
     * Ví dụ: "Nguyễn Văn An" -> "A", "Trần Thị Bình" -> "B"
     */
    public String getAvatarLetter() {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }
        String trimmed = name.trim();
        String[] parts = trimmed.split("\\s+");
        String lastWord = parts[parts.length - 1];
        if (!lastWord.isEmpty()) {
            return lastWord.substring(0, 1).toUpperCase();
        }
        return "?";
    }

    /**
     * Định dạng số điện thoại theo định dạng chuẩn hiển thị (vd: 0901 234 567).
     */
    public String getFormattedPhone() {
        if (phone != null && phone.length() == 10) {
            return phone.substring(0, 4) + " " + phone.substring(4, 7) + " " + phone.substring(7);
        }
        return phone != null ? phone : "";
    }

    /**
     * Tạo 3 liên hệ mẫu ban đầu theo đúng yêu cầu đề bài.
     */
    public static ArrayList<Contact> getInitialContacts() {
        ArrayList<Contact> sampleList = new ArrayList<>();
        sampleList.add(new Contact("Nguyễn Văn An", "0901234567", "Bạn bè", "Nam", false, ""));
        sampleList.add(new Contact("Trần Thị Bình", "0912345678", "Gia đình", "Nữ", true, "Chị gái, hay gọi buổi tối"));
        sampleList.add(new Contact("Lê Minh Châu", "0987654321", "Công việc", "Nam", false, ""));
        return sampleList;
    }
}
