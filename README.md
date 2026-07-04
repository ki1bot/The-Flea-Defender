# The Flea Defender

The Flea Defender adalah game RPG survival sederhana berbasis GUI desktop menggunakan bahasa pemrograman Java dan Java Swing. Pada game ini, pemain berperan sebagai Defender yang harus bertahan hidup dari serangan Flea selama 300 detik.

Project ini dibuat sebagai implementasi konsep Pemrograman Berorientasi Objek (Object Oriented Programming/OOP), seperti class, object, inheritance, encapsulation, abstraction, polymorphism, dan pemisahan tanggung jawab antar class.

## Deskripsi Game

Dalam game ini, Defender memiliki Health Points (HP) dan Resource Points (RP). Flea akan menyerang Defender secara otomatis setiap 20 detik. Pemain dapat melakukan beberapa aksi melalui tombol GUI, yaitu menyerang Flea, latihan untuk memulihkan HP, membeli vitamin, melewati waktu, atau melakukan restart game.

Tujuan utama permainan adalah bertahan hidup sampai waktu mencapai 300 detik. Jika HP Defender habis sebelum waktu mencapai 300 detik, maka permainan berakhir dengan status Game Over.

## Fitur Program

- Game RPG survival berbasis GUI desktop
- Dibuat menggunakan Java
- Menggunakan Java Swing untuk tampilan antarmuka
- Sistem HP untuk Defender dan Flea
- Sistem Resource Points (RP)
- Flea menyerang otomatis setiap 20 detik
- Defender dapat menyerang Flea
- Defender menerima self-damage saat menyerang
- Flea akan respawn setelah dikalahkan
- Defender mendapatkan RP setelah mengalahkan Flea
- Defender dapat latihan untuk memulihkan HP
- Sistem cooldown agar latihan tidak bisa dilakukan dua kali berturut-turut
- Defender dapat membeli vitamin menggunakan RP
- Tampilan status game menggunakan label dan progress bar
- Log permainan ditampilkan pada area teks
- Tombol Restart Game untuk memulai ulang permainan
- Sistem menang jika berhasil bertahan selama 300 detik
- Sistem kalah jika HP Defender habis
- Ringkasan akhir permainan

## Aturan Permainan

| Komponen                | Nilai           |
| ----------------------- | --------------- |
| HP awal Defender        | 100             |
| HP awal Flea            | 50              |
| RP awal Defender        | 0               |
| Target waktu bertahan   | 300 detik       |
| Interval serangan Flea  | Setiap 20 detik |
| Biaya vitamin           | 20 RP           |
| Pemulihan vitamin       | 30 HP           |
| Reward mengalahkan Flea | 20 RP           |

## Aksi Pemain

### 1. Serang Flea

Defender menyerang Flea dan memberikan damage secara acak. Setelah menyerang, waktu akan bertambah 10 detik. Namun, Defender juga menerima self-damage sebagai risiko dari serangan.

### 2. Latihan

Defender melakukan latihan untuk memulihkan HP. Latihan menambah waktu sebanyak 10 detik. Aksi ini tidak dapat dilakukan dua kali berturut-turut karena terdapat sistem cooldown.

### 3. Beli Vitamin

Defender dapat membeli vitamin jika memiliki RP yang cukup. Vitamin memulihkan HP sebesar 30 poin dan mengurangi RP sebesar 20 poin. Aksi ini tidak menambah waktu permainan.

### 4. Lewati Waktu

Defender melewati waktu selama 10 detik. Aksi ini juga membuat status latihan kembali siap digunakan.

### 5. Restart Game

Mengulang permainan dari awal dengan status Defender, Flea, waktu, dan RP yang dikembalikan ke kondisi awal.

# Cara Menjalankan Program Java

Panduan ini digunakan untuk menjalankan program Java `Main.java`.

## 1. Pastikan Java Sudah Terinstall

Sebelum menjalankan program, pastikan Java Development Kit (JDK) sudah terinstall di komputer.

Cek versi Java dengan perintah berikut :

```bash
java -version
```

## 2. Menjalankan Program Java

Buka terminal atau Command Prompt, lalu masuk ke folder tempat file Main.java disimpan.

Setelah berada di folder yang benar, jalankan perintah berikut untuk melakukan compile program:

```bash
javac -d out src/Main.java src/model/*.java src/game/*.java src/gui/*.java
java -cp out Main
```
