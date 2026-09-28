# NurEngine

Mesin andal dan tangguh yang dapat melakukan kalkulasi aljabar linear secara mandiri menggunakan bahasa pemrograman Java. Spesifikasi program didasarkan pada Spesifikasi Tugas Besar 1 IF2123 Aljabar Linear dan Geometri. Penamaan terinspirasi dari cerita rakyat yang lama beredar di angkatan Pixel.

Filosofi kami adalah *"Keep It Simple"*. Segala algoritma dan tipe data bentukan yang digunakan di program ini dibuat sesederhana mungkin.

## Requirements
Pastikan di sistem Anda sudah terpasang:
- Java 17 atau lebih baru
- Maven 3.6.3 atau lebih baru

Periksa instalasi dengan perintah berikut.
```bash
java --version
mvn --version
```

## Structure
```text
.
├── bin
├── docs
├── src
│   └── main
│       └── java
│           └── algeo
├── test
├── pom.xml
└── README.md
```
Modul berada di `src/main/java/algeo/modules`. Kelas utama program adalah `algeo.App`.

- `bin`: berkas hasil kompilasi atau JAR final
- `docs`: laporan tugas besar beserta asset
- `src`: kode sumber program
- `test`: berkas kasus uji

## Building
Jalankan perintah berikut pada *root directory* untuk melakukan kompilasi.
```bash
mvn clean package
```

Perintah tersebut akan mengotomasi proses kompilasi menggunakan Maven.

## Running
Ada 2 opsi dalam menjalankan program ini, yakni menggunakan bytecode prebuilt lewat java atau menggunakan hasil *build* sendiri lewat Maven.

### Dengan Java
```bash
java -jar ./bin/nurengine-1.0.jar
```

### Dengan Maven
```bash
mvn -q exec:java
```

> Catatan: program tidak menerima argumen dari luar.

## Features
Program ini dapat melakukan hal-hal keren sebagai berikut.
* Menghitung determinan matriks dengan Ekspansi Kofaktor dan Eliminasi Gaussian.
* Menghitung invers matriks dengan Ekspansi kofaktor dan Eliminasi Gaussian.
* Menyelesaikan SPL dengan Eliminasi Gaussian (Gauss dan Gauss-Jordan), Kaidah Cramer, dan metode Invers.
* Melakukan interpolasi polinomial
* Melakukan interpolasi cubic spline (natural)
* Melakukan regresi cubic spline dengan Truncated Power basis.
* Merekonstruksi gambar rusak dengan algoritma Laplacian menggunakan metode Gauss-Seidel.
* Mencetak fungsi hasil interpolasi dan regresi secara cantik.
* Mengevaluasi titik baru dari fungsi hasil interpolasi dan regresi.
* Menyediakan tahapan perhitungan secara rinci untuk perhitungan determinan, invers, dan SPL.
* Meminta input lewat keyboard ataupun file.
* Menyimpan output ke file.
* Memfasilitasi input "copy-paste"!

Silakan lihat `Matrix.java` untuk melihat fitur-fitur mendasar lainnya (seperti Operasi Baris Elementer, pembentukan Matriks Baris Eselon, dsb).

> Catatan: batasan ordo matriks adalah 1200x1200, dan batasan jumlah titik interpolasi/regresi adalah 20.
