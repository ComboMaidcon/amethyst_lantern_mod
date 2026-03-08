# 🏮 Amethyst Lantern Mod

Mod Minecraft 1.20.1 (Forge) thêm vào **Đèn Lồng Thạch Anh Tím** - một bùa hộ mệnh huyền bí
giữ thân nhiệt người chơi ổn định khi đang đeo.

---

## 📦 Dependencies (Bắt buộc)

| Mod | Phiên bản | Link |
|-----|-----------|------|
| Minecraft Forge | 47.2.20+ | [files.minecraftforge.net](https://files.minecraftforge.net) |
| **Curios API** | 5.3.5+1.20.1 | [Modrinth](https://modrinth.com/mod/curios) |
| **Tough As Nails** | 4.2.0.5+ | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/tough-as-nails) |

---

## ✨ Tính năng

### Đèn Lồng Thạch Anh (Amethyst Lantern)
- **Hình dạng**: Đèn lồng màu tím với lõi phát sáng amethyst
- **Slot trang bị**: Charm (Curios API)
- **Chức năng**: Ổn định thân nhiệt người chơi trong vùng an toàn

### Cơ chế nhiệt độ
Tough As Nails dùng thang nhiệt độ từ **0** (cực lạnh) đến **14** (cực nóng).

Khi đeo Đèn Lồng Thạch Anh:
- ✅ Vùng an toàn: **4 – 10** (giữ nguyên)
- 🔵 Quá lạnh (< 4): Tăng nhẹ về ngưỡng 4
- 🔴 Quá nóng (> 10): Giảm nhẹ về ngưỡng 10
- ⚡ Điều chỉnh từ từ (1 đơn vị/tick) - không teleport nhiệt độ đột ngột

### Công thức chế tạo
```
  A
 ALA
  A

A = Amethyst Shard (Mảnh Thạch Anh)
L = Soul Lantern  (Đèn Hồn)
```

---

## 🔧 Cài đặt cho Developer

### Yêu cầu
- JDK 17
- Gradle 8+

### Build
```bash
./gradlew build
# Output: build/libs/amethystlantern-1.20.1-1.0.0.jar
```

### Chạy thử trong game
```bash
./gradlew runClient
```

---

## 📁 Cấu trúc project

```
src/main/
├── java/com/example/amethystlantern/
│   ├── AmethystLanternMod.java          # Entry point
│   ├── registry/
│   │   └── ModItems.java                # Đăng ký items
│   └── item/
│       ├── AmethystLanternItem.java     # Item chính + Curios interface
│       └── ToughAsNailsHelper.java      # TAN temperature API wrapper
└── resources/
    ├── META-INF/mods.toml               # Mod metadata & dependencies
    ├── assets/amethystlantern/
    │   ├── models/item/                 # Item model JSON
    │   ├── textures/item/               # Texture PNG 16x16
    │   └── lang/en_us.json              # Tên & tooltip
    └── data/amethystlantern/
        ├── curios/entities/player.json  # Curios slot config
        └── recipes/                     # Công thức chế tạo
```

---

## 🔌 API Integration

### Curios API
Item implement interface `ICurioItem`:
```java
public class AmethystLanternItem extends Item implements ICurioItem {
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // Gọi mỗi tick khi đang đeo
    }
}
```

### Tough As Nails
Sử dụng `TANCapabilities.TEMPERATURE` để đọc/ghi nhiệt độ:
```java
player.getCapability(TANCapabilities.TEMPERATURE).ifPresent(tempCap -> {
    int currentTemp = tempCap.getTemperature().getRawValue();
    tempCap.setTemperature(new Temperature(newTemp));
});
```

---

## 📝 License
MIT License
