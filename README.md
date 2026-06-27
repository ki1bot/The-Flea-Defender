# The Flea Defender

The Flea Defender adalah game RPG berbasis teks menggunakan bahasa pemrograman Java dan konsep Object Oriented Programming (OOP). Pada game ini, pemain berperan sebagai Defender yang harus bertahan hidup dari serangan Flea selama 300 detik.

Game ini dibuat sebagai tugas akhir mata kuliah Pemrograman Berorientasi Objek dengan menerapkan konsep class, object, inheritance, encapsulation, method, dan interaksi antar object.

## Deskripsi Game

Dalam game ini, Defender memiliki Health Points (HP) dan Resource Points (RP). Flea akan menyerang Defender secara otomatis setiap 20 detik. Pemain dapat memilih beberapa aksi seperti menyerang Flea, melakukan latihan untuk memulihkan HP, membeli vitamin, atau melewati waktu.

Tujuan utama game adalah bertahan hidup sampai waktu mencapai 300 detik. Jika HP Defender habis sebelum waktu mencapai 300 detik, maka permainan berakhir dengan status Game Over.

## Fitur Program

- Game RPG berbasis teks
- Menggunakan bahasa Java
- Menggunakan konsep OOP
- Sistem HP untuk Defender dan Flea
- Sistem Resource Points (RP)
- Serangan otomatis dari Flea setiap 20 detik
- Defender dapat menyerang Flea
- Defender dapat melakukan latihan untuk memulihkan HP
- Defender dapat membeli vitamin menggunakan RP
- Sistem menang jika berhasil bertahan selama 300 detik
- Sistem kalah jika HP Defender habis
- Ringkasan akhir permainan

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

javac Main.java

Jika tidak ada pesan error, berarti proses compile berhasil.

Setelah itu, jalankan program dengan perintah berikut:

java Main

Jadi, perintah lengkap untuk menjalankan program adalah:

javac Main.java
java Main
