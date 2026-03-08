# 🚀 Hướng dẫn: Build .jar tự động bằng GitHub Actions

## Yêu cầu
- Tài khoản GitHub (miễn phí)
- Git đã cài trên máy (hoặc dùng GitHub Desktop)

---

## Bước 1 — Tạo repository trên GitHub

1. Vào https://github.com/new
2. Đặt tên repo: `amethyst-lantern-mod`
3. Chọn **Private** hoặc **Public** tùy ý
4. **KHÔNG** tick "Add README" (vì đã có rồi)
5. Bấm **Create repository**

---

## Bước 2 — Upload code lên GitHub

### Cách A: Dùng GitHub Desktop (dễ nhất)
1. Tải [GitHub Desktop](https://desktop.github.com/)
2. Chọn **File → Add local repository**
3. Trỏ đến thư mục `amethyst_lantern_mod`
4. Bấm **Publish repository**

### Cách B: Dùng Git command line
```bash
cd amethyst_lantern_mod

git init
git add .
git commit -m "Initial commit - Amethyst Lantern mod"

# Thay YOUR_USERNAME bằng tên GitHub của bạn
git remote add origin https://github.com/YOUR_USERNAME/amethyst-lantern-mod.git
git branch -M main
git push -u origin main
```

---

## Bước 3 — Chờ GitHub Actions build

1. Vào trang repo trên GitHub
2. Bấm tab **Actions** (trên thanh menu)
3. Bạn sẽ thấy workflow **"Build Amethyst Lantern Mod"** đang chạy 🟡
4. Lần đầu mất khoảng **10-15 phút** (tải Forge + dependencies)
5. Lần sau chỉ mất **3-5 phút** (nhờ cache)

---

## Bước 4 — Download file .jar

1. Bấm vào workflow run vừa hoàn thành ✅
2. Kéo xuống phần **Artifacts**
3. Bấm **amethyst-lantern-mod-jar** để download
4. Giải nén → lấy file `amethystlantern-1.20.1-1.0.0.jar`

---

## Bước 5 — Cài vào Minecraft

Bỏ file `.jar` vào thư mục:
```
.minecraft/mods/amethystlantern-1.20.1-1.0.0.jar
```

Đảm bảo cũng có trong thư mục `mods/`:
- `curios-forge-5.3.5+1.20.1.jar`
- `ToughAsNails-1.20.1-4.2.x.x.jar`

---

## Mỗi khi chỉnh sửa code

Chỉ cần **push lên GitHub** là Actions tự động build lại:
```bash
git add .
git commit -m "Mô tả thay đổi"
git push
```

Sau đó vào tab Actions để download jar mới.

---

## Tạo Release có tag (tùy chọn)

Nếu muốn tạo bản release chính thức:
```bash
git tag v1.0.0
git push origin v1.0.0
```
GitHub Actions sẽ tự động tạo Release và đính kèm file `.jar`.
