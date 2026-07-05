# The Flea Defender

The Flea Defender adalah game RPG survival sederhana berbasis GUI desktop menggunakan bahasa pemrograman Java dan Java Swing. Pada game ini, pemain berperan sebagai Defender yang harus bertahan hidup dari serangan Flea sampai waktu permainan mencapai 300 detik.

Game ini dibuat sebagai implementasi konsep Pemrograman Berorientasi Objek atau Object Oriented Programming (OOP), seperti class, object, inheritance, encapsulation, abstraction, polymorphism, pemisahan tanggung jawab antar class, serta pengelolaan GUI, animasi, asset gambar, dan sound effect.

## Deskripsi Game

Dalam game ini, Defender memiliki HP atau Health Points dan RP atau Resource Points. Flea akan muncul secara berkala, menyerang Defender secara otomatis, dan harus dikalahkan agar Defender mendapatkan RP.

Pemain dapat melakukan beberapa aksi melalui tombol GUI, yaitu menyerang Flea, latihan, membeli vitamin, bertahan, mengaktifkan atau mematikan suara, dan melakukan restart game.

Tujuan utama permainan adalah bertahan hidup sampai waktu mencapai 300 detik. Jika HP Defender habis sebelum waktu mencapai 300 detik, maka permainan berakhir dengan status Game Over.

## Fitur Utama

- Game RPG survival berbasis GUI desktop.
- Dibuat menggunakan Java.
- Menggunakan Java Swing untuk antarmuka.
- Menggunakan konsep Object Oriented Programming.
- Sistem HP untuk Defender dan Flea.
- Sistem Resource Points atau RP.
- Flea muncul setiap 10 detik.
- Flea aktif menyerang Defender secara otomatis setiap detik.
- Flea memiliki beberapa tipe dengan HP, damage, dan reward berbeda.
- Defender dapat menyerang Flea.
- Defender menerima self-damage saat menyerang.
- Defender mendapatkan RP setelah mengalahkan Flea.
- Defender dapat latihan untuk memulihkan HP.
- Latihan memiliki sistem cooldown agar tidak bisa dilakukan terus-menerus.
- Defender dapat membeli vitamin menggunakan RP.
- Vitamin hanya dapat digunakan jika RP cukup dan HP belum penuh.
- Defender dapat bertahan untuk mengurangi damage serangan Flea berikutnya.
- Sistem guard aktif sampai serangan Flea berikutnya.
- Animasi karakter Defender dan Flea.
- Animasi idle, menyerang, bertahan, latihan, beli vitamin, heal, muncul, dan mati.
- Sprite sheet Defender dan Flea menggunakan 7 frame.
- Sound effect untuk setiap animasi utama.
- Tombol suara ON/OFF.
- Tampilan status game menggunakan label dan progress bar.
- Log permainan ditampilkan pada area teks.
- Tombol Restart Game untuk memulai ulang permainan.
- Sistem menang jika berhasil bertahan selama 300 detik.
- Sistem kalah jika HP Defender habis.
- Ringkasan akhir permainan.

## Aturan Permainan

| Komponen | Nilai |
|---|---:|
| HP awal Defender | 100 |
| RP awal Defender | 0 |
| Target waktu bertahan | 300 detik |
| Spawn Flea pertama | Detik ke-10 |
| Interval spawn Flea | Setiap 10 detik |
| Serangan Flea aktif | Setiap 1 detik |
| Biaya vitamin | 20 RP |
| Pemulihan vitamin | 30 HP |
| Pengurangan damage saat bertahan | Damage dibagi 4 |

## Tipe Flea

| Tipe Flea | HP | Damage | Reward RP |
|---|---:|---:|---:|
| Flea Lemah | 20 | 1 - 2 | 8 RP |
| Flea Normal | 45 | 2 - 4 | 15 RP |
| Flea Kuat | 75 | 4 - 6 | 25 RP |

Setiap Flea yang muncul dipilih secara acak. Jika Flea berhasil dikalahkan, Defender akan mendapatkan RP sesuai tipe Flea tersebut.

## Aksi Pemain

### 1. Serang Flea

Defender menyerang Flea aktif dan memberikan damage secara acak. Jika Flea mati, Defender mendapatkan RP. Namun, setiap serangan juga memberikan self-damage kepada Defender sebagai risiko.

Tombol ini hanya aktif jika ada Flea yang sedang muncul.

### 2. Latihan

Defender melakukan latihan untuk memulihkan HP. Latihan memiliki animasi khusus menggunakan pose pedang Defender.

Aksi latihan tidak dapat dilakukan dua kali berturut-turut karena terdapat sistem cooldown. Setelah melakukan aksi lain, latihan dapat digunakan kembali.

### 3. Beli Vitamin

Defender dapat membeli dan menggunakan vitamin jika memiliki RP yang cukup. Vitamin memulihkan HP sebesar 30 poin dan mengurangi RP sebesar 20 poin.

Animasi vitamin hanya muncul jika vitamin benar-benar berhasil digunakan. Jika RP tidak cukup atau HP sudah penuh, hanya log informasi yang ditampilkan dan animasi vitamin tidak dijalankan.

### 4. Bertahan

Defender memasang guard untuk menahan serangan Flea berikutnya. Saat guard aktif, damage serangan Flea akan dikurangi besar.

Guard hanya berlaku untuk satu serangan Flea berikutnya. Setelah berhasil menahan satu serangan, status guard akan otomatis tidak aktif.

### 5. Suara ON/OFF

Tombol suara digunakan untuk mengaktifkan atau mematikan sound effect pada animasi. Jika suara dimatikan, semua sound effect akan berhenti diputar.

### 6. Restart Game

Restart Game digunakan untuk mengulang permainan dari awal. Status Defender, Flea, waktu, RP, log, animasi, dan kondisi permainan akan dikembalikan ke kondisi awal.

## Sistem Animasi

Game menggunakan sprite sheet untuk menampilkan animasi karakter. Sprite Defender dan Flea diletakkan di folder `src/assets`.

Sprite Defender:

```text
src/assets/defender.png
```

Sprite Flea:

```text
src/assets/flea.png
```

Setiap sprite sheet menggunakan 7 frame dalam satu baris horizontal.

Urutan frame Defender:

| Frame | Fungsi |
|---:|---|
| 0 | Idle / posisi siap |
| 1 | Attack wind-up / persiapan serangan |
| 2 | Attack / thrust |
| 3 | Defend / shield |
| 4 | Stagger / terkena efek mati awal |
| 5 | Falling / jatuh |
| 6 | Dead / mati |

Urutan frame Flea:

| Frame | Fungsi |
|---:|---|
| 0 | Idle / berjalan |
| 1 | Crouch / merunduk |
| 2 | Attack / leap |
| 3 | Attack / pose serang |
| 4 | Hurt / mulai mati |
| 5 | Collapse / menggulung |
| 6 | Dead / mati |

## Penjelasan Package

### 1. Package `model`

Package `model` berisi class yang merepresentasikan karakter dalam game.

| Class | Fungsi |
|---|---|
| GameCharacter | Class abstrak dasar untuk karakter |
| Defender | Class untuk karakter pemain |
| Flea | Class untuk musuh |

### 2. Package `game`

Package `game` berisi logic utama permainan.

| Class | Fungsi |
|---|---|
| GameBalance | Menyimpan nilai balance game seperti waktu, damage, HP, reward, dan biaya vitamin |
| FleaFactory | Membuat Flea secara acak berdasarkan tipe |
| GameActionResult | Menyimpan hasil aksi dan event animasi |
| GameEngine | Mengatur alur permainan, waktu, serangan, guard, RP, vitamin, latihan, menang, dan kalah |

### 3. Package `gui`

Package `gui` berisi tampilan, panel, renderer, animasi, sprite, dan audio.

| Class | Fungsi |
|---|---|
| GameFrame | Frame utama game |
| GameStatusPanel | Panel status HP, RP, waktu, Flea, dan guard |
| GameLogPanel | Panel log permainan |
| GameControlPanel | Panel tombol aksi |
| BattlePanel | Panel arena pertarungan |
| BattleRenderer | Renderer utama arena, Defender, dan Flea |
| BackgroundRenderer | Renderer background dan efek daun |
| EffectRenderer | Renderer efek slash, guard, heal, vitamin, dust, dan death particle |
| BattleAnimation | Enum daftar animasi |
| BattleRenderState | Menyimpan state render animasi |
| BattleAssets | Menyimpan asset gambar |
| AssetLoader | Memuat gambar dari folder assets |
| SpriteSheet | Membaca dan menggambar sprite sheet |
| SoundEffect | Enum daftar sound effect |
| SoundManager | Memuat dan memutar file audio `.wav` |

## Konsep OOP yang Digunakan

Project ini menerapkan beberapa konsep OOP, yaitu:

### 1. Class dan Object

Setiap bagian game dibuat dalam bentuk class, seperti `Defender`, `Flea`, `GameEngine`, `GameFrame`, dan `BattlePanel`. Object dibuat dari class tersebut untuk menjalankan game.

### 2. Encapsulation

Data penting seperti HP, RP, damage, dan status game disimpan dalam field private. Akses data dilakukan melalui method getter, setter, atau method khusus.

### 3. Inheritance

Class `Defender` dan `Flea` mewarisi class dasar `GameCharacter`.

### 4. Abstraction

Class `GameCharacter` digunakan sebagai class abstrak untuk karakter dalam game. Method umum seperti `attack()` dapat diimplementasikan oleh class turunan.

### 5. Polymorphism

Method `attack()` dapat memiliki implementasi berbeda antara Defender dan Flea.

### 6. Separation of Concerns

Project dipisahkan menjadi beberapa package agar tanggung jawab setiap class lebih jelas. Logic game berada di package `game`, data karakter berada di package `model`, dan tampilan berada di package `gui`.

## Cara Menjalankan Program

### 1. Pastikan JDK Sudah Terinstall

Cek versi Java dengan perintah berikut:

```bash
java -version
```

Cek compiler Java dengan perintah berikut:

```bash
javac -version
```

Jika belum tersedia, install JDK terlebih dahulu.

### 2. Clone Repository

Gunakan perintah berikut:

```bash
git clone https://github.com/ki1bot/The-Flea-Defender.git
```

Masuk ke folder project:

```bash
cd The-Flea-Defender
```

### 3. Compile Program

Jalankan perintah berikut dari folder utama project:

```bash
javac -d out src/Main.java src/model/*.java src/game/*.java src/gui/*.java
```

### 4. Jalankan Program

Setelah compile berhasil, jalankan program dengan perintah:

```bash
java -cp out Main
```

## Cara Menjalankan di VS Code

1. Buka folder `The-Flea-Defender` di VS Code.
2. Pastikan extension Java sudah terpasang.
3. Pastikan semua file asset berada di folder `src/assets`.
4. Buka terminal di VS Code.
5. Jalankan perintah compile:

```bash
javac -d out src/Main.java src/model/*.java src/game/*.java src/gui/*.java
```

6. Jalankan game:

```bash
java -cp out Main
```

## Alur Permainan

1. Game dimulai dengan Defender memiliki HP penuh dan RP 0.
2. Flea pertama muncul pada detik ke-10.
3. Flea aktif menyerang Defender setiap detik.
4. Pemain dapat menyerang Flea untuk mengalahkannya.
5. Pemain mendapatkan RP setelah Flea mati.
6. RP dapat digunakan untuk membeli vitamin.
7. Pemain dapat latihan untuk memulihkan HP.
8. Pemain dapat bertahan untuk mengurangi damage serangan Flea berikutnya.
9. Jika Defender bertahan sampai 300 detik, pemain menang.
10. Jika HP Defender habis sebelum 300 detik, pemain kalah.

## Kondisi Menang dan Kalah

Pemain menang jika:

```text
Waktu permainan mencapai 300 detik dan Defender masih hidup.
```

Pemain kalah jika:

```text
HP Defender mencapai 0 sebelum waktu mencapai 300 detik.
```

## Teknologi yang Digunakan

- Java
- Java Swing
- Java AWT
- Java Sound API
- Object Oriented Programming
- Sprite sheet animation
- Timer-based game loop

## Author

Project dibuat oleh:

```text
Rifqi
```

Repository:

```text
https://github.com/ki1bot/The-Flea-Defender
```
