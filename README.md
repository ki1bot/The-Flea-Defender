# The Flea Defender

The Flea Defender adalah game RPG sederhana berbasis GUI menggunakan bahasa pemrograman Java. Game ini dibuat dengan konsep Object Oriented Programming (OOP) dan menggunakan Java Swing sebagai tampilan antarmuka.

Pada game ini, pemain berperan sebagai Defender yang harus bertahan hidup dari serangan Flea selama 300 detik. Pemain dapat menyerang Flea, melakukan latihan untuk memulihkan HP, membeli vitamin menggunakan Resource Points (RP), atau melewati waktu.

## Deskripsi Game

Defender memiliki Health Points (HP) dan Resource Points (RP). Flea akan menyerang Defender secara otomatis setiap 20 detik. Pemain harus menjaga HP Defender agar tidak habis sebelum waktu permainan mencapai 300 detik.

Jika Defender berhasil bertahan sampai 300 detik, maka pemain menang. Jika HP Defender habis sebelum waktu mencapai 300 detik, maka permainan berakhir dengan status Game Over.

## Perubahan dari CLI ke GUI

Project ini awalnya dibuat sebagai game berbasis CLI atau terminal. Versi saat ini sudah diubah menjadi aplikasi GUI menggunakan Java Swing.

Perubahan utama yang dilakukan:

- Input angka melalui terminal diganti menjadi tombol GUI.
- Output `System.out.println()` diganti menjadi log permainan pada `JTextArea`.
- Status HP, RP, waktu, dan status latihan ditampilkan langsung pada window.
- HP Defender, HP Flea, dan waktu permainan divisualisasikan menggunakan `JProgressBar`.
- Logic permainan dipisahkan dari tampilan GUI agar struktur program lebih rapi.
- Program tidak lagi menggunakan `Scanner` untuk input terminal.

## Fitur Program

- Game RPG sederhana berbasis Java GUI.
- Menggunakan Java Swing.
- Menggunakan konsep Object Oriented Programming.
- Sistem HP untuk Defender dan Flea.
- Sistem Resource Points (RP).
- Flea menyerang otomatis setiap 20 detik.
- Defender dapat menyerang Flea.
- Defender mendapat RP setelah mengalahkan Flea.
- Flea akan respawn setelah dikalahkan.
- Defender dapat melakukan latihan untuk memulihkan HP.
- Defender tidak dapat latihan dua kali berturut-turut.
- Defender dapat membeli vitamin menggunakan RP.
- Vitamin membutuhkan 20 RP dan memulihkan 30 HP.
- Sistem menang jika Defender bertahan selama 300 detik.
- Sistem kalah jika HP Defender habis.
- Ringkasan akhir permainan ditampilkan di log GUI.
- Tombol restart untuk memulai ulang game.

## Konsep OOP yang Digunakan

Program ini menggunakan beberapa konsep utama dalam OOP, yaitu:

1. Class dan Object  
   Program memiliki beberapa class seperti Main, Game, GameCharacter, Defender, dan Flea.

2. Encapsulation  
   Atribut seperti hp, maxHp, name, dan resourcePoint dibuat private dan diakses melalui method getter atau method khusus.

3. Inheritance  
   Class Defender dan Flea merupakan turunan dari class GameCharacter.

4. Abstraction  
   Class GameCharacter dibuat sebagai abstract class karena digunakan sebagai class dasar untuk karakter dalam game.

5. Method  
   Setiap class memiliki method sesuai tanggung jawabnya, seperti attack(), heal(), takeDamage(), train(), buyVitamin(), dan start().

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
