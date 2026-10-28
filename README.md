# Ứng Dụng Quản Lý Danh Bạ Liên Hệ (ContactManager)

<p align="center">
  <b>Bài tập thực hành Môn: Phát triển ứng dụng cho các thiết bị di động</b><br>
  Học viện Công nghệ Bưu chính Viễn thông (PTIT)
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?logo=android" alt="Android" />
  <img src="https://img.shields.io/badge/Language-Java-orange?logo=openjdk" alt="Java" />
  <img src="https://img.shields.io/badge/Min%20SDK-24-blue" alt="Min SDK" />
  <img src="https://img.shields.io/badge/Compile%20SDK-37-blue" alt="Compile SDK" />
  <img src="https://img.shields.io/badge/UI-XML%20%7C%20Material%203-navy" alt="Material 3" />
  <img src="https://img.shields.io/badge/Unit%20Tests-8%2F8%20Passed-brightgreen" alt="Tests" />
</p>

## 📖 Giới Thiệu Dự Án

**ContactManager** là ứng dụng Android native được xây dựng hoàn toàn bằng **Java** và **XML Layout**, giúp người dùng lưu trữ, quản lý và thao tác với danh bạ cá nhân một cách tiện lợi, trực quan.

Ứng dụng đáp ứng 100% các tiêu chí trong tài liệu đặc tả đề tài (`De_bai_Quan_ly_danh_ba.docx`), đồng thời được nâng cấp bộ nhận diện thị giác sang phong cách **Classic Sapphire Navy (`#1E3A8A`)** sang trọng, hiện đại:

- Giữ nguyên 100% cấu trúc, vị trí và các thành phần giao diện theo đề tài.
- Sử dụng cơ chế truyền nhận dữ liệu `Intent` và `ActivityResultLauncher` hai chiều.
- Lưu trữ dữ liệu in-memory qua `ArrayList<Contact>` kèm dữ liệu mẫu ban đầu.
- Định dạng số điện thoại đẹp mắt (`0901 234 567`) và tự động trích xuất ký tự chữ cái đầu của tên đại diện tiếng Việt (_An $\rightarrow$ A, Bình $\rightarrow$ B, Châu $\rightarrow$ C_).

---

## 🧭 Kiến Trúc & Sơ Đồ Điều Hướng (Navigation Flow)

Ứng dụng gồm **4 Activity** chuyển đổi linh hoạt theo mô hình điều hướng phân tầng:

```
                  [ Mở ứng dụng ]
                         │
                         ▼
             ┌─────────────────────────┐
             │      LoginActivity      │
             │   (Tài khoản: admin)    │
             └───────────┬─────────────┘
                         │ Đăng nhập thành công (gọi finish())
                         ▼
             ┌─────────────────────────┐
             │       MainActivity      │ ◄─────────────────────────┐
             │  • Khối thống kê        │                           │
             │  • Danh sách danh bạ    │                           │
             └─────┬─────────────┬─────┘                           │
                   │             │                                 │
     Nhấn "+ Thêm" │             │ Nhấn vào 1 liên hệ              │
                   ▼             ▼                                 │
┌────────────────────────┐  ┌───────────────────────────┐          │
│   AddContactActivity   │  │   ContactDetailActivity   │          │
│ • Nhập thông tin form  │  │ • Xem chi tiết            │          │
│ • Kiểm tra hợp lệ/trùng│  │ • Bật/tắt Yêu thích       │          │
│ • Trả Contact mới      │  │ • Gọi điện (ACTION_DIAL)  │          │
└──────────┬─────────────┘  │ • Xóa liên hệ (Dialog)    │          │
           │                └─────────────┬─────────────┘          │
           └──────────────────────────────┴────────────────────────┘
                    Đồng bộ dữ liệu cập nhật về MainActivity
```

---

## ✨ Chi Tiết Các Tính Năng Trên 4 Màn Hình

### 1. Màn hình 1: Đăng nhập (`LoginActivity`)

- **Khởi động đầu tiên:** Được cấu hình thuộc tính `LAUNCHER` trong `AndroidManifest.xml`.
- **Tài khoản cố định:** `admin` / `123456`.
- **Kiểm tra hợp lệ:**
  - Bỏ trống tên đăng nhập hoặc mật khẩu $\rightarrow$ Báo lỗi _"Vui lòng nhập tên đăng nhập và mật khẩu"_.
  - Nhập sai thông tin $\rightarrow$ Báo lỗi _"Sai tên đăng nhập hoặc mật khẩu"_ (chữ đỏ Ruby `#DC2626`).
  - Tự động ẩn thông báo lỗi khi người dùng gõ lại ký tự.
- **Chuyển màn hình:** Đăng nhập thành công chuyển sang `MainActivity` và gọi `finish()` để nút Back không quay lại màn hình Login.

### 2. Màn hình 2: Danh sách liên hệ (`MainActivity`)

- **Khối thống kê (Dashboard Card):** Thiết kế thẻ bo góc 16dp màu Navy (#1E3A8A) gồm 2 cột:
  - **Tổng liên hệ:** Số lượng hiển thị to đậm (32sp bold).
  - **Yêu thích:** Số lượng liên hệ được đánh dấu yêu thích.
  - Tự động tính toán và cập nhật thông qua hàm `updateStatistics()` mỗi khi có sự kiện Thêm, Sửa hoặc Xóa.
- **Danh sách hiển thị (`RecyclerView`):**
  - Từng dòng liên hệ được bao bọc trong thẻ Card nền trắng bo tròn 14dp viền mỏng `#E2E8F0`.
  - **Avatar tròn:** Tự động hiển thị chữ cái in hoa của tên chính với bảng màu Pastel sang trọng (_A: Xanh ngọc, B: Cam, C: Tím..._).
  - **Họ tên & SĐT:** Chữ màu Slate 900 `#0F172A` đậm rõ ràng, SĐT định dạng chuẩn `0901 234 567`.
  - **Badge Nhóm:** Nền xám nhạt `#F1F5F9`, viền `#E2E8F0`, chữ `#334155`.
  - **Ngôi sao yêu thích:** Vàng hổ phách sáng `#D97706` khi liên hệ được tick yêu thích.
- **Dữ liệu mẫu ban đầu (3 liên hệ):**
  1. _Nguyễn Văn An_ – `0901234567` – Bạn bè – Nam – Yêu thích: Không
  2. _Trần Thị Bình_ – `0912345678` – Gia đình – Nữ – Yêu thích: Có – Ghi chú: "Chị gái, hay gọi buổi tối"
  3. _Lê Minh Châu_ – `0987654321` – Công việc – Nam – Yêu thích: Không
- **Nút "+ Thêm liên hệ":** Dạng viên thuốc màu xanh Navy ở đáy màn hình mở `AddContactActivity`.

### 3. Màn hình 3: Thêm liên hệ (`AddContactActivity`)

- **Đầy đủ 7 thành phần form nhập liệu:**
  1. _Họ và tên:_ `EditText`, bắt buộc nhập.
  2. _Số điện thoại:_ `EditText`, bắt buộc đúng định dạng.
  3. _Nhóm:_ `Spinner` gồm 4 mục (_Gia đình, Bạn bè, Công việc, Khác_), mặc định chọn _"Bạn bè"_.
  4. _Giới tính:_ `RadioGroup` gồm 3 `RadioButton` (_Nam, Nữ, Khác_), mặc định chọn _"Nam"_.
  5. _Yêu thích:_ `CheckBox` _"Thêm vào yêu thích"_.
  6. _Ghi chú:_ `EditText` nhiều dòng (`minLines="3"`), không bắt buộc nhập.
  7. _Nút Lưu liên hệ:_ Nút bo tròn màu xanh Navy.
- **Thuật toán kiểm tra tính hợp lệ (Validation):**
  - Bỏ trống họ tên $\rightarrow$ `edtName.setError("Vui lòng nhập họ và tên")`.
  - Số điện thoại không hợp lệ $\rightarrow$ Kiểm tra bằng biểu thức chính quy `^0\d{9}$` (đủ 10 chữ số và bắt đầu bằng số 0), nếu sai $\rightarrow$ `edtPhone.setError(...)`.
  - Trùng lặp số điện thoại $\rightarrow$ Tiếp nhận danh sách SĐT đã có từ `MainActivity` qua Intent, nếu trùng $\rightarrow$ `edtPhone.setError("Số điện thoại đã tồn tại trong danh bạ")`.
- **Kết quả:** Đóng gói đối tượng `Contact`, trả về qua Intent kèm `setResult(RESULT_OK, resultIntent)` và `finish()`.

### 4. Màn hình 4: Chi tiết liên hệ (`ContactDetailActivity`)

- **Thẻ thông tin chi tiết:** Nền trắng bo góc viền nhẹ:
  - Avatar lớn (64dp) đồng bộ màu sắc.
  - Tên liên hệ to đậm, Số điện thoại màu xanh Navy nổi bật.
  - Hàng 2 cột hiển thị Nhóm và Giới tính.
  - Khối hiển thị Ghi chú (hoặc `(Không có ghi chú)`).
  - `CheckBox` **"Yêu thích"**: Người dùng có thể bấm trực tiếp để đổi trạng thái yêu thích.
- **Cụm 3 nút hành động phân tách rõ ràng:**
  1. **Nút "Gọi điện" (`#059669` Emerald):** Kích hoạt Intent ngầm định (`Intent.ACTION_DIAL`) với Uri `tel:<số_điện_thoại>` để mở ứng dụng quay số của hệ điều hành.
  2. **Nút "Quay lại" (Outlined button nền trắng viền Navy):** Đóng màn hình, đồng bộ trạng thái yêu thích đã thay đổi về `MainActivity`. Hỗ trợ cả nút Back toolbar, nút Quay lại ở dưới và nút Back vật lý / cử chỉ vuốt (`OnBackPressedCallback`).
  3. **Nút "Xóa liên hệ" (`#DC2626` Ruby):** Hiển thị hộp thoại `AlertDialog` xác nhận _"Bạn có chắc chắn muốn xóa liên hệ này?"_. Nếu đồng ý thì gửi cờ `is_deleted = true` về `MainActivity` để xóa khỏi danh bạ và làm mới thống kê.

---

## 🎨 Hệ Thống Bảng Màu (Design System - Classic Sapphire Navy)

| Tên màu               | Mã HEX                          | Vai trò trong giao diện                                                       |
| :-------------------- | :------------------------------ | :---------------------------------------------------------------------------- |
| `primary`             | `#1E3A8A`                       | Xanh Sapphire Navy: TopBar, nút chính, viền nút Outlined, tint Radio/CheckBox |
| `primary_dark`        | `#172554`                       | Navy tối: Thanh trạng thái StatusBar trên cùng                                |
| `bg_screen`           | `#F8FAFC`                       | Xám khói nhạt dịu mắt: Nền root layout của cả 4 màn hình                      |
| `card_surface`        | `#FFFFFF`                       | Trắng tinh: Nền Card danh bạ, Card chi tiết, ô nhập liệu                      |
| `border_stroke`       | `#E2E8F0`                       | Đường viền mảnh trang nhã                                                     |
| `text_primary`        | `#0F172A`                       | Đen than đậm (Slate 900): Họ tên, tiêu đề lớn                                 |
| `text_secondary`      | `#64748B`                       | Xám khói (Slate 500): Số điện thoại, nhãn phụ                                 |
| `color_star`          | `#D97706`                       | Vàng hổ phách: Ngôi sao đánh dấu liên hệ yêu thích                            |
| `btn_call`            | `#059669`                       | Xanh lá Emerald: Nút Gọi điện tích cực                                        |
| `btn_delete`          | `#DC2626`                       | Đỏ Ruby cảnh báo: Nút Xóa liên hệ, dòng text báo lỗi                          |
| `tag_bg` / `tag_text` | `#F1F5F9` / `#334155`           | Chip phân loại nhóm                                                           |
| **Avatar Palette**    | `#0D9488`, `#EA580C`, `#4F46E5` | Bảng màu Pastel tròn cho chữ cái đại diện Avatar                              |

---

## 📂 Cấu Trúc Thư Mục Dự Án

```
ContactManager/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/contactmanager/
│   │   │   │   ├── adapter/
│   │   │   │   │   └── ContactAdapter.java           # Adapter quản lý hiển thị RecyclerView
│   │   │   │   ├── model/
│   │   │   │   │   └── Contact.java                  # Model dữ liệu (Serializable)
│   │   │   │   ├── AddContactActivity.java           # Màn hình 3: Thêm liên hệ
│   │   │   │   ├── ContactDetailActivity.java        # Màn hình 4: Chi tiết liên hệ
│   │   │   │   ├── LoginActivity.java                # Màn hình 1: Đăng nhập (Launcher)
│   │   │   │   └── MainActivity.java                 # Màn hình 2: Danh sách liên hệ
│   │   │   ├── res/
│   │   │   │   ├── drawable/                         # Bộ Vector & Shape drawables
│   │   │   │   │   ├── bg_avatar_circle.xml          # Nền tròn cho avatar
│   │   │   │   │   ├── bg_badge_group.xml            # Badge chip cho nhóm
│   │   │   │   │   ├── bg_contact_card.xml           # Thẻ Card liên hệ bo góc 14dp
│   │   │   │   │   ├── bg_input_field.xml            # Ô nhập liệu bo góc 8dp viền nhạt
│   │   │   │   │   ├── bg_spinner.xml                # Khung viền kèm mũi tên dropdown
│   │   │   │   │   ├── bg_stat_card.xml              # Khối thống kê Navy bo góc 16dp
│   │   │   │   │   ├── btn_call.xml                  # Nút Gọi điện xanh Emerald
│   │   │   │   │   ├── btn_danger.xml                # Nút Xóa đỏ Ruby
│   │   │   │   │   ├── btn_outline.xml               # Nút Quay lại nền trắng viền Navy
│   │   │   │   │   ├── btn_primary.xml               # Nút chính Navy bo góc 24dp
│   │   │   │   │   ├── ic_arrow_back.xml             # Icon mũi tên quay lại
│   │   │   │   │   ├── ic_phone_logo.xml             # Logo điện thoại đăng nhập
│   │   │   │   │   └── ic_star_filled.xml            # Ngôi sao vàng yêu thích
│   │   │   │   ├── layout/                           # XML Layouts cho 4 màn hình
│   │   │   │   │   ├── activity_add_contact.xml      # Layout Thêm liên hệ
│   │   │   │   │   ├── activity_contact_detail.xml   # Layout Chi tiết liên hệ
│   │   │   │   │   ├── activity_login.xml            # Layout Đăng nhập
│   │   │   │   │   ├── activity_main.xml             # Layout Danh sách chính
│   │   │   │   │   ├── item_contact.xml              # Layout từng dòng danh bạ
│   │   │   │   │   └── spinner_item.xml              # Layout mục trong Spinner
│   │   │   │   └── values/
│   │   │   │       ├── colors.xml                    # Bảng màu Classic Sapphire Navy
│   │   │   │       ├── strings.xml                   # Chuỗi hiển thị tiếng Việt
│   │   │   │       └── themes.xml                    # Cấu hình Theme Material 3
│   │   │   └── AndroidManifest.xml                   # Khai báo quyền, Activity, Launcher
│   │   └── test/java/com/example/contactmanager/
│   │       └── ContactUnitTest.java                  # Bộ Unit Test toàn diện (8 test cases)
│   └── build.gradle.kts                              # Cấu hình Gradle module app
├── B24DCCC254_Phạm Khánh Thiện_BaoCao.docx            # Báo cáo Word hoàn chỉnh (5 hình minh họa)
├── HUONG_DAN_NOP_BAI_VA_BAO_CAO.md                   # Hướng dẫn nộp bài và các lệnh Git
├── README.md                                         # Tài liệu mô tả dự án
├── build.gradle.kts                                  # Cấu hình Gradle root
└── settings.gradle.kts                               # Cấu hình settings
```

---

## 🧪 Kết Quả Kiểm Thử Tự Động (Unit Testing)

Tất cả các tính năng và logic nghiệp vụ được kiểm tra nghiêm ngặt thông qua file [`ContactUnitTest.java`](app/src/test/java/com/example/contactmanager/ContactUnitTest.java):

```
✓ testNameEmptyValidation           PASSED (Validate họ tên không để trống hoặc chỉ có khoảng trắng)
✓ testAvatarLetterExtraction        PASSED (Trích xuất chuẩn chữ cái đầu tên tiếng Việt: An->A, Bình->B, Châu->C)
✓ testDuplicatePhoneCheck           PASSED (Phát hiện chính xác số điện thoại đã tồn tại)
✓ testPhoneValidationRegex          PASSED (Kiểm tra regex ^0\d{9}$, chặn số thiếu, thừa, sai đầu số, có chữ)
✓ testInitialContacts               PASSED (Khởi tạo đúng 3 liên hệ mẫu theo đề bài)
✓ testStatisticsCalculation         PASSED (Tính toán khối thống kê: thêm, xóa, đổi yêu thích)
✓ testFormattedPhone                PASSED (Format số điện thoại đẹp mắt: 0901234567 -> 0901 234 567)
✓ testLoginValidation               PASSED (Xác thực tài khoản admin/123456 và bắt lỗi)

Results: 8 passed, 0 failed, 0 skipped
BUILD SUCCESSFUL in 2s
```

---

## 🚀 Hướng Dẫn Cài Đặt & Chạy Ứng Dụng

### 1. Yêu cầu môi trường

- **Android Studio:** Jellyfish, Koala, Ladybug hoặc mới hơn.
- **JDK:** Java 17, 21 hoặc JBR đi kèm Android Studio.
- **Android SDK:** Compile SDK 37, Min SDK 24 (Android 7.0 trở lên).

### 2. Các bước mở và chạy trên Android Studio

1. Mở **Android Studio** $\rightarrow$ chọn **Open** $\rightarrow$ điều hướng đến thư mục `D:\ContactManager`.
2. Chờ Android Studio đồng bộ Gradle (**Sync Project with Gradle Files**).
3. Chọn thiết bị chạy (Máy ảo Emulator hoặc Điện thoại thật bật chế độ USB Debugging).
4. Nhấn nút **Run** (phím tắt `Shift + F10`) để cài đặt và khởi chạy ứng dụng.

### 3. Cài đặt trực tiếp từ file APK

Tệp APK hoàn chỉnh đã được biên dịch sẵn tại:
👉 `app/build/outputs/apk/debug/app-debug.apk`  
Bạn có thể copy file này vào điện thoại Android và cài đặt trực tiếp.
