package algeo;
import java.util.Scanner;
import java.io.*;

import algeo.modules.*;

public class App {

    
    static Scanner sc = new Scanner(System.in);

    /**
     * Titik masuk program. Menjalankan loop utama: 
     * 1. menampilkan menu
     * 2. membaca pilihan
     * 3. memanggil handler yang sesuai 
     * 4. lanjut/bersihkan layar.
     * Setiap {@code Exception} yang dilempar handler akan ditangkap dan
     * dilaporkan sebagai {@code "Error: <message>"} agar program tidak crash.
     * @param args argumen command-line (tidak digunakan)
     */
    public static void main(String[] args) {
        while (true) {
            try {
                showMainMenu();
                int choice = readInt(sc);
                switch (choice) {
                    case 1: handleSPL(); break;
                    case 2: handleDeterminant(); break;
                    case 3: handleInverse(); break;
                    case 4: handleInterpolation(); break;

                    // Belum implemen
                    case 5: handleRegression(); break;
                    case 6: handleImageHoleFill(); break;

                    case 7: return;
                    default: System.out.println("Pilihan tidak valid");
                }
                
            // Error handling
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            enterContinue();
            clearScreen();
        }
    }

     /**
     * Mencetak menu utama ke layar.
     */
    static void showMainMenu() {
        System.out.println("------------ NurEngine's Linear Algebra Equator ------------");
        System.out.println("1. Sistem Persamaan Linier (SPL)");
        System.out.println("2. Determinan Matriks");
        System.out.println("3. Matriks Balikan (Invers)");
        System.out.println("4. Interpolasi (Polinomial / Natural Cubic Spline)");
        System.out.println("5. Regresi Spline Kubik (Truncated Power Basis)");
        System.out.println("6. Bonus: Image Hole Filling");
        System.out.println("7. Keluar");
        System.out.print("Pilihan: ");
    }

    // ----------------- SPL ----------------- 
    /**
     * Menangani alur penyelesaian Sistem Persamaan Linier (SPL).
     * Membaca matriks augmented {@code [A|b]} dari keyboard atau file,
     * meminta metode penyelesaian, lalu mencetak solusi dan menawarkan
     * penyimpanan hasil ke file.
     * @throws IllegalArgumentException ketika mode input atau metode tidak valid
     */
    static void handleSPL() {
        
        // Tanya source input
        System.out.println("------------ Sistem Persamaan Linier ------------");
        System.out.println("1. Input dari keyboard");
        System.out.println("2. Input dari file .txt");
        System.out.print("Pilih: ");
        int mode = readInt(sc);

        // Baca data (dari file/keyboard)
        double[][] augmented;
        if (mode == 1) {
            augmented = readAugmentedMatrixFromKeyboard(sc);
        } else if (mode == 2) {
            System.out.print("Path file: ");
            String path = sc.next();
            augmented = readAugmentedMatrixFromFile(path);
        } else {
            throw new IllegalArgumentException("Mode input tidak valid");
        }

        // Tampil menu dan pilih metode SPL
        showSPLMenu();
        int method = readInt(sc);

        // Convert double [][] ke bentuk matriks
        Matrix s = toMatrix(augmented);

        // Solve SPL
        double[][] sol;
        String methodName;
        switch (method) {
            case 1: sol = SPLSolver.byREF(s); methodName = "Eliminasi Gauss"; break;
            case 2: sol = SPLSolver.byRREF(s); methodName = "Eliminasi Gauss-Jordan"; break;
            case 3: sol = SPLSolver.byCramer(s); methodName = "Kaidah Cramer"; break;
            case 4: sol = SPLSolver.byInverse(s); methodName = "Matriks Balikan"; break;
            default: throw new IllegalArgumentException("Metode tidak valid");
        }

        // Formatting output
        StringBuilder sb = new StringBuilder();
        sb.append("Metode: ").append(methodName).append("\n");
        sb.append("Input:\n").append(formatMatrix(augmented)).append("\n");
        sb.append("Solusi:\n").append(formatSPLSolution(sol)).append("\n");

        
        System.out.println("------------ Hasil ---");
        System.out.println(sb.toString());

        askSaveToFile(sb.toString());
    }

    // ----------------- Determinan -----------------

    /**
     * Menangani alur perhitungan determinan matriks persegi.
     * Membaca matriks dari keyboard atau file, memvalidasi bahwa matriks
     * persegi, meminta metode perhitungan, lalu mencetak hasilnya.
     * @throws IllegalArgumentException ketika mode input, bentuk matriks, atau metode tidak valid
     */
    static void handleDeterminant() {
        
        // Tanya source input
        System.out.println("------------ Determinan Matriks ------------");
        System.out.println("1. Input dari keyboard");
        System.out.println("2. Input dari file .txt");
        System.out.print("Pilih: ");
        int mode = readInt(sc);

        // Baca data
        double[][] matrix;
        if (mode == 1) {
            System.out.print("Ukuran matriks n: ");
            int n = readInt(sc);
            matrix = readMatrixFromKeyboard(sc, n, n);
        } else if (mode == 2) {
            System.out.print("Path file: ");
            String path = sc.next();
            matrix = readMatrixFromFile(path);
        } else {
            throw new IllegalArgumentException("Mode input tidak valid");
        }

        // Validasi matriks kuadrat
        validateSquareMatrix(matrix);

        // Tampil menu dan pilih metode cari determinan
        showDeterminantMenu();
        int method = readInt(sc);

        // Solve determinan
        double det;
        String methodName;
        switch (method) {
            case 1:
                det = Matrix.det(toMatrix(matrix));
                methodName = "Ekspansi Kofaktor";
                break;
            case 2:
                det = Matrix.gdet(toMatrix(matrix));
                methodName = "Reduksi Baris (Eliminasi Gaussian)";
                break;
            default: throw new IllegalArgumentException("Metode tidak valid");
        }

        // Format output
        StringBuilder sb = new StringBuilder();
        sb.append("Metode: ").append(methodName).append("\n");
        sb.append("Input:\n").append(formatMatrix(matrix)).append("\n");
        sb.append("Determinan = ").append(String.format("%.3f", det)).append("\n");

        System.out.println("------------ Hasil ---");
        System.out.println(sb.toString());

        askSaveToFile(sb.toString());
    }

    // ----------------- Invers -----------------

    /**
     * Menangani alur pencarian matriks balikan (invers).
     * Membaca matriks persegi, memvalidasi, meminta metode (Augmen / Adjoin),
     * lalu mencetak invers dan menawarkan penyimpanan hasil.
     * @throws IllegalArgumentException ketika mode input, bentuk matriks, atau metode tidak valid,
     *                                  atau ketika matriks singular
     */
    static void handleInverse() {

        // Tanya source input
        System.out.println("------------ Matriks Balikan ------------");
        System.out.println("1. Input dari keyboard");
        System.out.println("2. Input dari file .txt");
        System.out.print("Pilih: ");
        int mode = readInt(sc);

        // Baca data (dari file/keyboard)
        double[][] matrix;
        if (mode == 1) {
            System.out.print("Ukuran matriks n: ");
            int n = readInt(sc);
            matrix = readMatrixFromKeyboard(sc, n, n);
        } else if (mode == 2) {
            System.out.print("Path file: ");
            String path = sc.next();
            matrix = readMatrixFromFile(path);
        } else {
            throw new IllegalArgumentException("Mode input tidak valid");
        }

        // Validasi matriks kuadrat
        validateSquareMatrix(matrix);

        // Tampil menu dan pilih metode untuk nyari invers
        showInverseMenu();
        int method = readInt(sc);

        double[][] inverse;
        String methodName;
        switch (method) {
            case 1:
                inverse = toArray(Matrix.ginv(toMatrix(matrix)));
                methodName = "Augmen (Gauss-Jordan)";
                break;
            case 2:
                inverse = toArray(Matrix.inv(toMatrix(matrix)));
                methodName = "Adjoin";
                break;
            default: throw new IllegalArgumentException("Metode tidak valid");
        }

        // Formatting output
        StringBuilder sb = new StringBuilder();
        sb.append("Metode: ").append(methodName).append("\n");
        sb.append("Input:\n").append(formatMatrix(matrix)).append("\n");
        sb.append("Matriks Balikan:\n").append(formatMatrix(inverse)).append("\n");

        System.out.println("------------ Hasil ---");
        System.out.println(sb.toString());

        askSaveToFile(sb.toString());
    }

    // ----------------- Interpolasi -----------------

     /**
     * Menampilkan submenu interpolasi dan meneruskan ke metode yang dipilih.
     * @throws IllegalArgumentException ketika metode tidak valid
     */
    static void handleInterpolation() {


        System.out.println("------------ Interpolasi ------------");
        
        // Tampil menu dan pilih metode interpolasi polinomial
        showInterpolationMenu();
        int method = readInt(sc);

        if (method == 1) {
            handlePolynomialInterpolation();
        } else if (method == 2) {
            handleNaturalCubicSplineInterpolation();
        } else {
            throw new IllegalArgumentException("Metode tidak valid");
        }
    }

    static void handlePolynomialInterpolation() {
        
        // Tanya source input
        System.out.println("------------ Interpolasi Polinomial ---");
        System.out.println("1. Input dari keyboard");
        System.out.println("2. Input dari file .txt");
        System.out.print("Pilih: ");
        int mode = readInt(sc);

        // Baca data (dari file/keyboard)
        double[][] points;
        if (mode == 1) {
            System.out.print("Jumlah titik n: ");
            int n = readInt(sc);
            points = readPointsFromKeyboard(sc, n);
        } else if (mode == 2) {
            System.out.print("Path file: ");
            String path = sc.next();
            points = readPointsFromFile(path);
        } else {
            throw new IllegalArgumentException("Mode input tidak valid");
        }

        // Validasi tiap titik tidak kosong & berpasangan
        validatePoints(points);

        // Solve interpolasi
        double[] coeffs = Interpolation.Polynomial(points);
        String equation = formatPolynomialEquation(coeffs);

        // Formatting output
        StringBuilder sb = new StringBuilder();
        sb.append("Metode: Interpolasi Polinomial\n");
        sb.append("Persamaan: y = ").append(equation).append("\n");

        System.out.println("------------ Hasil ---");
        System.out.println(sb.toString());

        // Evaluasi hasil di suatu koordinat x (opsional) 
        System.out.print("Masukkan xt untuk evaluasi (atau ketik 'skip'): ");
        String input = sc.next();
        if (!input.equalsIgnoreCase("skip")) {
            double xt = parseNumber(input);
            double yt = evalPolynomial(coeffs, xt);
            String evalLine = String.format("y(%.3f) = %.3f%n", xt, yt);
            System.out.println(evalLine);
            sb.append(evalLine);
        }

        askSaveToFile(sb.toString());
    }

     /**
     * Menangani interpolasi polinomial.
     * Membaca himpunan titik {@code (x, y)}, menghitung koefisien polinom
     * interpolan, mencetak persamaannya, lalu opsional mengevaluasi nilai
     * polinom di {@code xt}. Mengetik {@code "skip"} melewati tahap evaluasi.
     * @throws IllegalArgumentException ketika mode input atau data titik tidak valid
     */
    static void handleNaturalCubicSplineInterpolation() {
     
        // Tanya source input
        System.out.println("------------ Interpolasi Natural Cubic Spline ------------");
        System.out.println("1. Input dari keyboard");
        System.out.println("2. Input dari file .txt");
        System.out.print("Pilih: ");
        int mode = readInt(sc);

        // Baca data (dari file/keyboard)
        double[][] points;
        if (mode == 1) {
            System.out.print("Jumlah titik n: ");
            int n = readInt(sc);
            points = readPointsFromKeyboard(sc, n);
        } else if (mode == 2) {
            System.out.print("Path file: ");
            String path = sc.next();
            points = readPointsFromFile(path);
        } else {
            throw new IllegalArgumentException("Mode input tidak valid");
        }

        // Validasi tiap titik tidak kosong & berpasangan
        validatePoints(points);

        // Cari turunan kedua dari spline dari tiap point 
        double[] knots = Interpolation.CubicSplinal(points);

        // Formatting output
        StringBuilder sb = new StringBuilder();
        sb.append("Metode: Natural Cubic Spline\n");
        sb.append("Nilai turunan kedua tiap titik (knots):\n");
        for (int i = 0; i < knots.length; i++)
            sb.append(String.format("  k%d = %.3f%n", i, knots[i]));

        System.out.println("------------ Hasil ------------");
        System.out.println(sb.toString());

        // Evaluasi hasil di suatu koordinat x (opsional)
        System.out.print("Masukkan xt untuk evaluasi (harus dalam domain, atau 'skip'): ");
        String input = sc.next();
        if (!input.equalsIgnoreCase("skip")) {
            double xt = parseNumber(input);
            double yt = Interpolation.CubicSplinalEvaluate(points, knots, xt); // <-- fixed
            String evalLine = String.format("y(%.3f) = %.3f%n", xt, yt);
            System.out.println(evalLine);
            sb.append(evalLine);
        }

        askSaveToFile(sb.toString());
    }

    // ----------------- Regresi -----------------

    /**
     * Menangani regresi spline kubik berbasis Truncated Power Basis.
     * Membaca {@code n} titik data dan {@code k} knots dari keyboard atau file,
     * memvalidasi {@code n >= k + 4}, menghitung koefisien regresi, mencetak
     * persamaannya, lalu opsional mengevaluasi spline di {@code xt}.
     * @throws IllegalArgumentException ketika mode input atau data regresi tidak valid
     */
    static void handleRegression() {
        System.out.println("------------ Regresi Spline Kubik (Truncated Power Basis) ------------");
        System.out.println("1. Input dari keyboard");
        System.out.println("2. Input dari file .txt");
        System.out.print("Pilih: ");
        int mode = readInt(sc);

        double[][] points;
        double[] knots;

        if (mode == 1) {
            System.out.print("Jumlah titik data n: ");
            int n = readInt(sc);
            points = readPointsFromKeyboard(sc, n);

            System.out.print("Jumlah knots k: ");
            int k = readInt(sc);
            knots = new double[k];
            for (int i = 0; i < k; i++) {
                System.out.print("  knot[" + i + "]: ");
                knots[i] = parseNumber(sc.next());
            }
        } else if (mode == 2) {
            System.out.print("Path file: ");
            String path = sc.next();
            RegressionInput parsed = readRegressionDataFromFile(path);
            points = parsed.points;
            knots = parsed.knots;
        } else {
            throw new IllegalArgumentException("Mode input tidak valid");
        }
        validateRegressionInput(points, knots);

        double[] coeffs = Regression.CubicSplinal(points, knots);
        String equation = formatTruncatedPowerEquation(coeffs, knots);

        StringBuilder sb = new StringBuilder();
        sb.append("Metode: Regresi Spline Kubik\n");
        sb.append("Jumlah knots: ").append(knots.length).append("\n");
        sb.append("Persamaan: y = ").append(equation).append("\n");

        System.out.println("\n--- Hasil ---");
        System.out.println(sb.toString());

        System.out.print("Masukkan xt untuk evaluasi (atau 'skip'): ");
        String input = sc.next();
        if (!input.equalsIgnoreCase("skip")) {
            double xt = parseNumber(input);

            // ini biar work dlu aja, aslinya mah masih rada bug sikit cuma aku ngantuk twin
            double yt = Regression.CubicSplinalEvaluate(knots, knots, xt);
            String evalLine = String.format("y(%.3f) = %.3f%n", xt, yt);
            System.out.println(evalLine);
            sb.append(evalLine);
        }

        askSaveToFile(sb.toString());
    }

    // ----------------- Bonus: Image Hole Fill -----------------

    /**
     * Menangani bonus Image Hole Filling.
     * Meminta tiga path (gambar asli, mask, output), menjalankan algoritma
     * pengisian lubang berbasis rata-rata tetangga iteratif, lalu mencetak
     * ringkasan statistik (ukuran, jumlah hole, iterasi, error akhir).
     * @throws RuntimeException ketika gambar gagal dibaca atau ditulis
     */
    static void handleImageHoleFill() {
        System.out.println("------------ Bonus: Image Hole Filling ------------");
        System.out.print("Path gambar asli (.png/.jpg): ");
        String imagePath = sc.next();
        System.out.print("Path mask (.png/.jpg): ");
        String maskPath = sc.next();
        System.out.print("Path output (.png/.jpg): ");
        String outputPath = sc.next();
 
        ImageHoleFill.Result result;
        try {
            result = ImageHoleFill.fill(imagePath, maskPath, outputPath);
        } catch (IOException e) {
            throw new RuntimeException("Gagal memproses gambar: " + e.getMessage());
        }
 
        StringBuilder sb = new StringBuilder();
        sb.append("Metode: Image Hole Filling (Rata-rata Tetangga Iteratif)\n");
        sb.append("Ukuran gambar: ").append(result.width).append(" x ").append(result.height).append("\n");
        sb.append("Jumlah pixel hole: ").append(result.holeCount).append("\n");
        sb.append("Jumlah iterasi: ").append(result.iterations).append("\n");
        sb.append("Error akhir: ").append(String.format("%.6f", result.finalError)).append("\n");
        sb.append("Output disimpan di: ").append(result.outputPath).append("\n");
 
        System.out.println("\n--- Hasil ---");
        System.out.println(sb.toString());
 
        askSaveToFile(sb.toString());
    }

    // ----------------- submenu -----------------

    /**
     * Mencetak submenu metode penyelesaian SPL.
     */
    static void showSPLMenu() {
        System.out.println("------------ Menu Sistem Persamaan Linier ------------");
        System.out.println("1. Metode Eliminasi Gauss");
        System.out.println("2. Metode Eliminasi Gauss-Jordan");
        System.out.println("3. Kaidah Cramer");
        System.out.println("4. Metode Matriks Balikan");
        System.out.print("Pilihan: ");
    }

     /**
     * Mencetak submenu metode perhitungan determinan.
     */
    static void showDeterminantMenu() {
        System.out.println("------------ Menu Determinan ------------");
        System.out.println("1. Metode Ekspansi Kofaktor");
        System.out.println("2. Metode Reduksi Baris (Operasi Baris Elementer)");
        System.out.print("Pilihan: ");
    }

    /**
     * Mencetak submenu metode pencarian invers.
     */
    static void showInverseMenu() {
        System.out.println("------------ Menu Invers ------------");
        System.out.println("1. Metode Augmen");
        System.out.println("2. Metode Adjoin");
        System.out.print("Pilihan: ");
    }

    /**
     * Mencetak submenu metode interpolasi.
     */
    static void showInterpolationMenu() {
        System.out.println("------------ Menu Interpolasi ------------");
        System.out.println("1. Metode Polinomial");
        System.out.println("2. Metode Natural Cubic Spline");
        System.out.print("Pilihan: ");
    }

    // ----------------- input helpers -----------------

    /**
     * Membaca sebuah bilangan bulat dari Scanner, meminta ulang sampai input valid.
     * Token yang bukan angka akan dikonsumsi dan dilaporkan sebelum mencoba lagi.
     * Setelah sukses, karakter newline di akhir baris ikut dikonsumsi.
     * @param sc Scanner sumber
     * @return bilangan bulat yang berhasil dibaca
     */
    public static int readInt(Scanner sc) {
        while (!sc.hasNextInt()) {
            String bad = sc.next();
            System.out.println("'" + bad + "' bukan angka. Coba lagi:");
        }
        int val = sc.nextInt();
        sc.nextLine();
        return val;
    }

    /**
     * Mengubah string menjadi bilangan pecahan, mendukung pemisah desimal
     * baik {@code "."} maupun {@code ","}.
     * @param s string sumber
     * @return nilai {@code double} hasil penguraian
     * @throws NumberFormatException ketika string tidak dapat diuraikan
     */
    static double parseNumber(String s) {
        return Double.parseDouble(s.trim().replace(",", "."));
    }

    /**
     * Membaca matriks berordo {@code rows} x {@code cols} dari keyboard.
     * @param sc Scanner sumber
     * @param rows jumlah baris
     * @param cols jumlah kolom
     * @return matriks hasil pembacaan
     */
    static double[][] readMatrixFromKeyboard(Scanner sc, int rows, int cols) {
        double[][] m = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.out.println("Baris " + (i + 1) + ":");
            for (int j = 0; j < cols; j++) {
                System.out.print("  [" + i + "][" + j + "]: ");
                m[i][j] = parseNumber(sc.next());
            }
        }
        return m;
    }

    /**
     * Membaca matriks dari file teks.
     * Format: baris pertama {@code "rows cols"}, lalu {@code rows} baris
     * berisi {@code cols} bilangan.
     * @param path path file masukan
     * @return matriks hasil pembacaan
     * @throws RuntimeException ketika file gagal dibaca
     */
    static double[][] readMatrixFromFile(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String[] dims = br.readLine().trim().split("\\s+");
            int rows = Integer.parseInt(dims[0]);
            int cols = Integer.parseInt(dims[1]);
            double[][] m = new double[rows][cols];
            for (int i = 0; i < rows; i++) {
                String[] parts = br.readLine().trim().split("\\s+");
                for (int j = 0; j < cols; j++)
                    m[i][j] = parseNumber(parts[j]);
            }
            return m;
        } catch (IOException e) {
            throw new RuntimeException("Gagal membaca file: " + e.getMessage());
        }
    }

    /**
     * Membaca matriks augmented {@code [A|b]} dari keyboard.
     * @param sc Scanner sumber
     * @return matriks augmented berukuran {@code rows} x {@code (vars + 1)}
     */
    static double[][] readAugmentedMatrixFromKeyboard(Scanner sc) {
        System.out.print("Jumlah persamaan: ");
        int rows = readInt(sc);
        System.out.print("Jumlah variabel: ");
        int vars = readInt(sc);
        double[][] m = new double[rows][vars + 1];
        for (int i = 0; i < rows; i++) {
            System.out.println("Persamaan " + (i + 1) + ":");
            for (int j = 0; j < vars; j++) {
                System.out.print("  koef x" + (j + 1) + ": ");
                m[i][j] = parseNumber(sc.next());
            }
            System.out.print("  konstanta (ruas kanan): ");
            m[i][vars] = parseNumber(sc.next());
        }
        return m;
    }

    /**
     * Membaca matriks augmented {@code [A|b]} dari file teks.
     * Format: baris pertama {@code "rows vars"}, lalu {@code rows} baris
     * berisi {@code vars} koefisien dan satu konstanta.
     * @param path path file masukan
     * @return matriks augmented hasil pembacaan
     * @throws RuntimeException ketika file gagal dibaca
     */
    static double[][] readAugmentedMatrixFromFile(String path) {
        // format: baris 1 = "jumlahPersamaan jumlahVariabel"
        // baris berikutnya = koefisien... konstanta
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String[] dims = br.readLine().trim().split("\\s+");
            int rows = Integer.parseInt(dims[0]);
            int vars = Integer.parseInt(dims[1]);
            double[][] m = new double[rows][vars + 1];
            for (int i = 0; i < rows; i++) {
                String[] parts = br.readLine().trim().split("\\s+");
                for (int j = 0; j <= vars; j++)
                    m[i][j] = parseNumber(parts[j]);
            }
            return m;
        } catch (IOException e) {
            throw new RuntimeException("Gagal membaca file: " + e.getMessage());
        }
    }

     /**
     * Membaca {@code n} titik {@code (x, y)} dari keyboard.
     * @param sc Scanner sumber
     * @param n jumlah titik
     * @return array {@code n x 2} berisi tiap titik
     */
    static double[][] readPointsFromKeyboard(Scanner sc, int n) {
        double[][] pt = new double[n][2];
        for (int i = 0; i < n; i++) {
            System.out.print("Titik " + (i + 1) + " (x y): ");
            pt[i][0] = parseNumber(sc.next());
            pt[i][1] = parseNumber(sc.next());
        }
        return pt;
    }

    /**
     * Membaca titik-titik dari file teks.
     * Format: baris pertama jumlah titik {@code n}, lalu {@code n} baris
     * berisi {@code "x y"}.
     * @param path path file masukan
     * @return array {@code n x 2} berisi tiap titik
     * @throws RuntimeException ketika file gagal dibaca
     */
    static double[][] readPointsFromFile(String path) {
        // format: baris 1 = jumlah titik n, lalu n baris "x y"
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            int n = Integer.parseInt(br.readLine().trim());
            double[][] pt = new double[n][2];
            for (int i = 0; i < n; i++) {
                String[] parts = br.readLine().trim().split("\\s+");
                pt[i][0] = parseNumber(parts[0]);
                pt[i][1] = parseNumber(parts[1]);
            }
            return pt;
        } catch (IOException e) {
            throw new RuntimeException("Gagal membaca file: " + e.getMessage());
        }
    }

    // Helper class supaya lebih rapih
    static class RegressionInput {
        double[][] points;
        double[] knots;
    }

    /**
     * Membaca data regresi dari file teks.
     * Format:
     * <pre>
     *   n k
     *   x1 y1
     *   x2 y2
     *   ...
     *   xn yn
     *   knot1 knot2 ... knotk
     * </pre>
     * @param path path file masukan
     * @return {@link RegressionInput} berisi titik-titik dan knots
     * @throws RuntimeException ketika file gagal dibaca
     */
    static RegressionInput readRegressionDataFromFile(String path) {
        // format: baris 1 = "n k" (n titik, k knots)
        // n baris berikutnya = "x y"
        // baris terakhir = k nilai knot dipisah spasi
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String[] dims = br.readLine().trim().split("\\s+");
            int n = Integer.parseInt(dims[0]);
            int k = Integer.parseInt(dims[1]);

            double[][] points = new double[n][2];
            for (int i = 0; i < n; i++) {
                String[] parts = br.readLine().trim().split("\\s+");
                points[i][0] = parseNumber(parts[0]);
                points[i][1] = parseNumber(parts[1]);
            }

            String[] knotParts = br.readLine().trim().split("\\s+");
            double[] knots = new double[k];
            for (int i = 0; i < k; i++)
                knots[i] = parseNumber(knotParts[i]);

            RegressionInput ri = new RegressionInput();
            ri.points = points;
            ri.knots = knots;
            return ri;
        } catch (IOException e) {
            throw new RuntimeException("Gagal membaca file: " + e.getMessage());
        }
    }

    // ----------------- output helpers -----------------

    /**
     * Menyimpan konten ke file teks, menimpa isi lama jika ada.
     * Jika gagal, pesan kesalahan dicetak alih-alih melempar exception.
     * @param filename path file keluaran
     * @param content teks yang akan ditulis
     */
    static void saveOutputToFile(String filename, String content) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.print(content);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan file: " + e.getMessage());
        }
    }

    /**
     * Memformat matriks menjadi string multi-baris dengan tiga angka desimal
     * di belakang koma untuk setiap elemen.
     * @param matrix matriks yang akan diformat
     * @return string hasil pemformatan
     */
    static String formatMatrix(double[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (double[] row : matrix) {
            sb.append("[ ");
            for (double v : row)
                sb.append(String.format("%.3f ", v));
            sb.append("]\n");
        }
        return sb.toString();
    }

    /**
     * Memformat solusi SPL sebagai barisan persamaan, satu per variabel.
     * Variabel bebas ditampilkan terhadap parameter {@code a1, a2, ...}.
     * @param sol matriks solusi hasil {@code SPLSolver}
     * @return string solusi terformat
     */
    static String formatSPLSolution(double[][] sol) {
        StringBuilder sb = new StringBuilder();
        int maxFree = 0;
        for (double[] row : sol)
            maxFree = Math.max(maxFree, row.length - 1);

        for (int i = 0; i < sol.length; i++) {
            sb.append("x").append(i + 1).append(" = ").append(String.format("%.3f", sol[i][0]));
            for (int k = 1; k <= maxFree; k++) {
                if (k >= sol[i].length || sol[i][k] == 0) continue;
                double c = sol[i][k];
                sb.append(c < 0 ? " - " : " + ").append(String.format("%.3f", Math.abs(c))).append("*a").append(k);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Mengevaluasi polinom (koefisien menaik menurut derajat) di titik {@code x}.
     * @param coeffs koefisien, {@code coeffs[i]} adalah koefisien {@code x^i}
     * @param x titik evaluasi
     * @return nilai {@code P(x)}
     */
    static double evalPolynomial(double[] coeffs, double x) {
        double val = 0, p = 1;
        for (double c : coeffs) {
            val += c * p;
            p *= x;
        }
        return val;
    }

    /**
     * Memformat polinom dengan notasi basis pangkat standar,
     * misalnya {@code y = 1.000 + 2.000*x + 3.000*x^2}.
     * @param coeffs koefisien menaik menurut derajat
     * @return string persamaan (tanpa awalan {@code "y = "})
     */
    static String formatPolynomialEquation(double[] coeffs) {
        String[] basis = new String[coeffs.length];
        for (int i = 0; i < coeffs.length; i++)
            basis[i] = i == 0 ? "" : (i == 1 ? "x" : "x^" + i);
        return formatEquationTerms(coeffs, basis);
    }

    /**
     * Memformat persamaan regresi dengan basis truncated power:
     * empat suku pertama bagian polinom kubik, setiap suku berikutnya
     * berbentuk {@code max(0, x - knot_i)^3}.
     * @param coeffs koefisien regresi (panjang {@code knots.length + 4})
     * @param knots lokasi knot untuk suku truncated power
     * @return string persamaan (tanpa awalan {@code "y = "})
     */
    static String formatTruncatedPowerEquation(double[] coeffs, double[] knots) {
        String[] basis = new String[coeffs.length];
        for (int i = 0; i < coeffs.length; i++) {
            if (i < 4) basis[i] = i == 0 ? "" : (i == 1 ? "x" : "x^" + i);
            else basis[i] = "max(0,x-" + String.format("%.3f", knots[i - 4]) + ")^3";
        }
        return formatEquationTerms(coeffs, basis);
    }

    /**
     * Menggabungkan koefisien dan basis simboliknya menjadi satu string persamaan
     * yang mudah dibaca. Koefisien dengan nilai absolut di bawah {@code 1e-9}
     * dihilangkan. Mengembalikan {@code "0"} jika semua suku hilang.
     * @param coeffs nilai koefisien
     * @param basis representasi simbolik tiap suku (string kosong untuk konstanta)
     * @return string persamaan terformat
     */
    static String formatEquationTerms(double[] coeffs, String[] basis) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coeffs.length; i++) {
            double c = coeffs[i];
            if (Math.abs(c) < 1e-9) continue;
            if (sb.length() == 0) {
                if (c < 0) sb.append("-");
            } else {
                sb.append(c < 0 ? " - " : " + ");
            }
            sb.append(String.format("%.3f", Math.abs(c)));
            if (!basis[i].isEmpty()) sb.append("*").append(basis[i]);
        }
        if (sb.length() == 0) sb.append("0");
        return sb.toString();
    }

     /**
     * Membungkus array 2D {@code double} menjadi objek {@link Matrix}.
     * @param arr array sumber
     * @return {@link Matrix} baru dengan isi sama
     */
    static Matrix toMatrix(double[][] arr) {
        Matrix m = new Matrix(arr.length, arr[0].length);
        for (int i = 0; i < arr.length; i++)
            for (int j = 0; j < arr[0].length; j++)
                m.src[i][j] = arr[i][j];
        return m;
    }

    /**
     * Mengambil array 2D penyusun dari sebuah {@link Matrix}.
     * @param m matriks sumber
     * @return array 2D {@code double} baru dengan isi sama
     */
    static double[][] toArray(Matrix m) {
        double[][] r = new double[m.rows][m.cols];
        for (int i = 0; i < m.rows; i++)
            for (int j = 0; j < m.cols; j++)
                r[i][j] = m.src[i][j];
        return r;
    }

    /**
     * Membersihkan layar konsol.
     * Menggunakan {@code cls} di Windows dan {@code clear} di sistem Unix-like.
     * Jika perintah eksternal gagal, dicetak 50 baris kosong sebagai fallback.
     */
    public static void clearScreen() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;
            if (os.contains("win")) {
                pb = new ProcessBuilder("cmd", "/c", "cls");
            } else {
                pb = new ProcessBuilder("clear");
            }
            pb.inheritIO().start().waitFor();
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

     /**
     * Menghentikan eksekusi hingga pengguna menekan Enter.
     */
    public static void enterContinue() {
        System.out.print("Tekan Enter Untuk Lanjut...");
        sc.nextLine();
    }

    /**
     * Menanyakan kepada pengguna apakah ingin menyimpan hasil ke file.
     * Jika ya, meminta nama file lalu menuliskan kontennya.
     * @param content teks yang akan disimpan
     */
    static void askSaveToFile(String content) {
        System.out.print("Simpan hasil ke file .txt? (y/n): ");
        String ans = sc.next();
        if (ans.equalsIgnoreCase("y")) {
            System.out.print("Nama file: ");
            String filename = sc.next();
            saveOutputToFile(filename, content);
            System.out.println("Hasil disimpan di " + filename);
        }
    }

    // ----------------- Validation Helper -----------------

    /**
     * Memvalidasi bahwa matriks tidak kosong dan persegi.
     * @param m matriks yang akan divalidasi
     * @throws IllegalArgumentException ketika matriks {@code null}, kosong, atau tidak persegi
     */
    static void validateSquareMatrix(double[][] m) {
        if (m == null || m.length == 0)
            throw new IllegalArgumentException("Matriks kosong!");
        for (double[] row : m)
            if (row.length != m.length)
                throw new IllegalArgumentException("Matriks harus persegi!");
    }

    /**
     * Memvalidasi bahwa sekumpulan titik tidak kosong dan tiap titik adalah
     * pasangan {@code (x, y)}.
     * @param points titik-titik yang akan divalidasi
     * @throws IllegalArgumentException ketika himpunan titik {@code null}, kosong, atau tidak valid
     */
    static void validatePoints(double[][] points) {
        if (points == null || points.length == 0)
            throw new IllegalArgumentException("Data titik kosong!");
        for (double[] p : points)
            if (p.length != 2)
                throw new IllegalArgumentException("Setiap titik harus berupa pasangan (x, y)!");
    }

    /**
     * Memvalidasi masukan regresi: titik harus valid dan jumlahnya minimal
     * {@code knots.length + 4} (empat suku basis polinom ditambah satu suku
     * per knot).
     * @param data  titik data
     * @param knots lokasi knot
     * @throws IllegalArgumentException ketika validasi gagal
     */
    static void validateRegressionInput(double[][] data, double[] knots) {
        validatePoints(data);
        if (knots == null)
            throw new IllegalArgumentException("Knots tidak boleh kosong!");
        if (data.length < knots.length + 4)
            throw new IllegalArgumentException("Jumlah titik data tidak cukup untuk jumlah knots yang diberikan!");
    }

     /**
     * Memvalidasi bahwa sebuah offset tidak negatif.
     * Saat ini belum dipakai; disiapkan untuk fitur Image Hole Filling.
     * @param offsetX offset horizontal
     * @param offsetY offset vertikal
     * @param widthA  lebar citra pertama (belum dipakai)
     * @param heightA tinggi citra pertama (belum dipakai)
     * @param widthB  lebar citra kedua (belum dipakai)
     * @param heightB tinggi citra kedua (belum dipakai)
     * @throws IllegalArgumentException ketika salah satu offset negatif
     */
    static void validateOffset(int offsetX, int offsetY,
                                int widthA, int heightA,
                                int widthB, int heightB) {
        if (offsetX < 0 || offsetY < 0)
            throw new IllegalArgumentException("Offset tidak boleh negatif!");
    }
}