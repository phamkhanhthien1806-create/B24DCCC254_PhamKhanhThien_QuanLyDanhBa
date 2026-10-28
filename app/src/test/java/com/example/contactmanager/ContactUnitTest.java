package com.example.contactmanager;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import com.example.contactmanager.model.Contact;

/**
 * Bộ kiểm thử toàn diện các tính năng và logic nghiệp vụ của ứng dụng Quản lý danh bạ.
 */
public class ContactUnitTest {

    // --- 1. KIỂM THỬ MODEL CONTACT VÀ TIỆN ÍCH ---

    @Test
    public void testAvatarLetterExtraction() {
        // Tên tiếng Việt 3 từ
        Contact c1 = new Contact("Nguyễn Văn An", "0901234567", "Bạn bè", "Nam", false, "");
        assertEquals("A", c1.getAvatarLetter());

        Contact c2 = new Contact("Trần Thị Bình", "0912345678", "Gia đình", "Nữ", true, "");
        assertEquals("B", c2.getAvatarLetter());

        Contact c3 = new Contact("Lê Minh Châu", "0987654321", "Công việc", "Nam", false, "");
        assertEquals("C", c3.getAvatarLetter());

        // Tên đơn
        Contact c4 = new Contact("David", "0911111111", "Khác", "Nam", false, "");
        assertEquals("D", c4.getAvatarLetter());

        // Tên có nhiều khoảng trắng thừa
        Contact c5 = new Contact("  Phạm   Đức   Hải  ", "0922222222", "Bạn bè", "Nam", false, "");
        assertEquals("H", c5.getAvatarLetter());

        // Tên rỗng hoặc null
        Contact c6 = new Contact("", "0933333333", "Khác", "Nam", false, "");
        assertEquals("?", c6.getAvatarLetter());

        Contact c7 = new Contact(null, "0944444444", "Khác", "Nam", false, "");
        assertEquals("?", c7.getAvatarLetter());
    }

    @Test
    public void testFormattedPhone() {
        // Số điện thoại đủ 10 số -> Định dạng dạng 0901 234 567
        Contact c1 = new Contact("Nguyễn Văn An", "0901234567", "Bạn bè", "Nam", false, "");
        assertEquals("0901 234 567", c1.getFormattedPhone());

        Contact c2 = new Contact("Trần Thị Bình", "0912345678", "Gia đình", "Nữ", true, "");
        assertEquals("0912 345 678", c2.getFormattedPhone());

        // Trường hợp không đủ 10 số (trả về chuỗi gốc)
        Contact c3 = new Contact("Test", "123", "Khác", "Nam", false, "");
        assertEquals("123", c3.getFormattedPhone());
    }

    @Test
    public void testInitialContacts() {
        // Kiểm tra đúng 3 liên hệ mẫu theo đề bài
        ArrayList<Contact> contacts = Contact.getInitialContacts();
        assertEquals(3, contacts.size());

        // Liên hệ 1
        assertEquals("Nguyễn Văn An", contacts.get(0).getName());
        assertEquals("0901234567", contacts.get(0).getPhone());
        assertEquals("Bạn bè", contacts.get(0).getGroup());
        assertFalse(contacts.get(0).isFavorite());

        // Liên hệ 2 (Yêu thích: Có)
        assertEquals("Trần Thị Bình", contacts.get(1).getName());
        assertEquals("0912345678", contacts.get(1).getPhone());
        assertEquals("Gia đình", contacts.get(1).getGroup());
        assertTrue(contacts.get(1).isFavorite());

        // Liên hệ 3
        assertEquals("Lê Minh Châu", contacts.get(2).getName());
        assertEquals("0987654321", contacts.get(2).getPhone());
        assertEquals("Công việc", contacts.get(2).getGroup());
        assertFalse(contacts.get(2).isFavorite());
    }

    // --- 2. KIỂM THỬ LOGIC VALIDATION MÀN HÌNH ĐĂNG NHẬP (LoginActivity) ---

    @Test
    public void testLoginValidation() {
        String validUser = "admin";
        String validPass = "123456";

        // Trường hợp đăng nhập thành công
        assertTrue(isValidLogin("admin", "123456", validUser, validPass));

        // Trường hợp để trống username hoặc password
        assertFalse(isValidLogin("", "123456", validUser, validPass));
        assertFalse(isValidLogin("admin", "", validUser, validPass));
        assertFalse(isValidLogin("   ", "   ", validUser, validPass));

        // Trường hợp sai tài khoản / mật khẩu
        assertFalse(isValidLogin("admin", "wrongpass", validUser, validPass));
        assertFalse(isValidLogin("user", "123456", validUser, validPass));
        assertFalse(isValidLogin("admin1", "123456", validUser, validPass));
    }

    private boolean isValidLogin(String inputUser, String inputPass, String expectedUser, String expectedPass) {
        if (inputUser == null || inputPass == null) return false;
        String u = inputUser.trim();
        String p = inputPass.trim();
        if (u.isEmpty() || p.isEmpty()) return false;
        return expectedUser.equals(u) && expectedPass.equals(p);
    }

    // --- 3. KIỂM THỬ LOGIC VALIDATION MÀN HÌNH THÊM LIÊN HỆ (AddContactActivity) ---

    @Test
    public void testPhoneValidationRegex() {
        String phoneRegex = "^0\\d{9}$";

        // Số điện thoại hợp lệ: đủ 10 số và bắt đầu bằng số 0
        assertTrue("0901234567".matches(phoneRegex));
        assertTrue("0912345678".matches(phoneRegex));
        assertTrue("0987654321".matches(phoneRegex));
        assertTrue("0323456789".matches(phoneRegex));
        assertTrue("0868123456".matches(phoneRegex));

        // Không hợp lệ: Không bắt đầu bằng số 0
        assertFalse("1901234567".matches(phoneRegex));
        assertFalse("9901234567".matches(phoneRegex));

        // Không hợp lệ: Không đủ 10 số (< 10)
        assertFalse("090123456".matches(phoneRegex));
        assertFalse("09012".matches(phoneRegex));
        assertFalse("0".matches(phoneRegex));

        // Không hợp lệ: Vượt quá 10 số (> 10)
        assertFalse("09012345678".matches(phoneRegex));

        // Không hợp lệ: Chứa chữ hoặc ký tự đặc biệt
        assertFalse("090123456a".matches(phoneRegex));
        assertFalse("090-123456".matches(phoneRegex));
        assertFalse("0901 23456".matches(phoneRegex));
    }

    @Test
    public void testDuplicatePhoneCheck() {
        ArrayList<String> existingPhones = new ArrayList<>();
        existingPhones.add("0901234567");
        existingPhones.add("0912345678");
        existingPhones.add("0987654321");

        // Số điện thoại đã tồn tại
        assertTrue(existingPhones.contains("0901234567"));
        assertTrue(existingPhones.contains("0912345678"));
        assertTrue(existingPhones.contains("0987654321"));

        // Số điện thoại mới chưa có trong danh bạ
        assertFalse(existingPhones.contains("0933333333"));
        assertFalse(existingPhones.contains("0944444444"));
    }

    @Test
    public void testNameEmptyValidation() {
        assertTrue(isNameEmpty(""));
        assertTrue(isNameEmpty("   "));
        assertFalse(isNameEmpty("Nguyễn Văn A"));
        assertFalse(isNameEmpty("  Trần B  "));
    }

    private boolean isNameEmpty(String name) {
        return name == null || name.trim().isEmpty();
    }

    // --- 4. KIỂM THỬ KHỐI THỐNG KÊ (MainActivity & ContactDetailActivity) ---

    @Test
    public void testStatisticsCalculation() {
        // Ban đầu có 3 liên hệ mẫu
        ArrayList<Contact> contacts = Contact.getInitialContacts();
        assertEquals(3, contacts.size());

        // Kiểm tra thống kê ban đầu: 3 tổng liên hệ, 1 yêu thích
        int[] stats = calculateStats(contacts);
        assertEquals(3, stats[0]); // Tổng
        assertEquals(1, stats[1]); // Yêu thích

        // Thao tác 1: Thêm liên hệ mới có yêu thích
        Contact newContactFav = new Contact("Hoàng Văn Dũng", "0934567890", "Bạn bè", "Nam", true, "");
        contacts.add(newContactFav);
        stats = calculateStats(contacts);
        assertEquals(4, stats[0]); // Tổng: 4
        assertEquals(2, stats[1]); // Yêu thích: 2

        // Thao tác 2: Thêm liên hệ mới không yêu thích
        Contact newContactNormal = new Contact("Vũ Thị Mai", "0945678901", "Gia đình", "Nữ", false, "");
        contacts.add(newContactNormal);
        stats = calculateStats(contacts);
        assertEquals(5, stats[0]); // Tổng: 5
        assertEquals(2, stats[1]); // Yêu thích: 2

        // Thao tác 3: Đổi trạng thái yêu thích của 1 liên hệ (false -> true)
        contacts.get(0).setFavorite(true); // Nguyễn Văn An
        stats = calculateStats(contacts);
        assertEquals(5, stats[0]);
        assertEquals(3, stats[1]); // Yêu thích tăng lên 3

        // Thao tác 4: Xóa một liên hệ yêu thích (Trần Thị Bình ở index 1)
        contacts.remove(1);
        stats = calculateStats(contacts);
        assertEquals(4, stats[0]); // Tổng giảm xuống 4
        assertEquals(2, stats[1]); // Yêu thích giảm xuống 2
    }

    private int[] calculateStats(ArrayList<Contact> list) {
        int total = list.size();
        int fav = 0;
        for (Contact c : list) {
            if (c.isFavorite()) {
                fav++;
            }
        }
        return new int[]{total, fav};
    }
}
