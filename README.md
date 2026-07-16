# SweetCup Delivery App

Aplikasi Android native untuk SweetCup Delivery.

## Cara Build APK (Tanpa Android Studio)

### Langkah 1: Buat Akun GitHub
Buka https://github.com dan daftar akun gratis.

### Langkah 2: Buat Repository Baru
1. Login ke GitHub
2. Klik tombol "+" → "New repository"
3. Isi nama: `SweetCupApp`
4. Pilih **Public**
5. Klik "Create repository"

### Langkah 3: Push ke GitHub
Buka terminal/PowerShell, masuk ke folder project:

```bash
cd /storage/emulated/0/SweetCupApp
git remote add origin https://github.com/YOUR_USERNAME/SweetCupApp.git
git branch -M main
git add .
git commit -m "Initial commit"
git push -u origin main
```

Ganti `YOUR_USERNAME` dengan username GitHub kamu.

### Langkah 4: Build APK di Cloud
1. Buka repository di GitHub
2. Klik tab **"Actions"**
3. Workflow akan otomatis jalan
4. Tunggu sampai selesai (≈ 5-10 menit)
5. Klik workflow yang berhasil ✅
6. Scroll ke bawah, klik **"SweetCup-debug"** atau **"SweetCup-release"**
7. Download ZIP, extract, dapat file APK

### Langkah 5: Install APK
1. Transfer APK ke HP
2. Buka file APK
3. Izinkan install dari sumber tidak dikenal
4. Install

## Tech Stack
- Kotlin + Jetpack Compose
- Material 3
- MVVM Architecture
- Retrofit + OkHttp + Gson
- Coil (Image Loading)
- DataStore (Local Storage)

## Fitur
- Login & Register
- Browse produk (filter kategori + search + pagination)
- Detail produk dengan review
- Keranjang lokal
- Checkout (COD / E-Wallet / QRIS)
- Riwayat pesanan
- Wishlist
- Edit profil + logout

## Base URL
API: https://cybershop.co-id.id/api/
