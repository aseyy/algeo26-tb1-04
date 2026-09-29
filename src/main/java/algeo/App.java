package algeo;

import algeo.modules.*;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;

/**
 * Produly present ke para asisten, 
 * program TB1 Algeo yang tertulis dari jerih payah 3 orang Pixel.
 * Jerih payah yang timbul berkat dilarangnya penggunaan generative AI.
 *
 * @author Rafi Fauzi Hermawan (wequra)
 * @author Rionaldo Casey Panditha (aseyy)
 * @author Fachry Azriel Fajdwani (rabsed1)
 * @since 26/09/2026
 */
public class App {
    static Scanner sc = new Scanner(System.in);
    static double e = Matrix.NEPSILON;

    // spesifikasi ukuran statis
    final static int maxOrdo = 1200;
    final static int maxPt = 20;

    // konstanta lain
    final static String cwd = System.getProperty("user.dir");

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
                clearScreen();
                showMainMenu();

                int choice = readInt(sc);
                switch (choice) {
                    case 1: handleSPL(); break;
                    case 2: handleDeterminant(); break;
                    case 3: handleInverse(); break;
                    case 4: handlePolynomialInterpolation(); break;
                    case 5: handleNaturalCubicSplineInterpolation(); break;
                    case 6: handleRegression(); break;
                    case 7: handleImageHoleFill(); break;
                    case 8: clearScreen(); return;
                    default: throw new IllegalArgumentException("Pilihan tidak valid.");
                }

                enterContinue();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                sc = new Scanner(System.in);
                enterContinue();
            }
        }
    }

     /**
     * Mencetak menu utama ke layar.
     */
    static void showMainMenu() {
        System.out.println("------------ NurEngine sang Raja Algeo ------------");
        System.out.println("1. Sistem Persamaan Linier");
        System.out.println("2. Determinan Matriks");
        System.out.println("3. Invers Matriks");
        System.out.println("4. Interpolasi Polinomial");
        System.out.println("5. Interpolasi Cubic Spline");
        System.out.println("6. Regresi Cubic Spline");
        System.out.println("7. Image Hole Filling");
        System.out.println("8. Keluar");
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
        // Presentasikan dengan bagus dan bijak ygy
        clearScreen();
        System.out.println("------------ Sistem Persamaan Linier ------------");
        System.out.println("[Jenis Input]");
        System.out.println("1. Input dari keyboard");
        System.out.println("2. Input dari file .txt");
        System.out.print("Pilihan: ");
        int mode = readInt(sc);

        // Baca data (dari file/keyboard)
        double[][] augmented;
        if (mode == 1) {
            System.out.println("\n[Input SPL]");
            augmented = readAugmentedMatrixFromKeyboard(sc);
        } else if (mode == 2) {
            System.out.println("\n[Input SPL]");
            System.out.println("CWD: " + cwd);
            System.out.print("Path file: ");
            String path = sc.nextLine();
            augmented = readMatrixFromFile(path);
            System.out.println("File terbaca!");
        } else {
            throw new IllegalArgumentException("Mode input tidak valid.");
        }

        // Tampil menu dan pilih metode SPL
        showSPLMenu();
        int method = readInt(sc);

        // Convert double [][] ke bentuk matriks
        Matrix s = toMatrix(augmented);

        // Solve SPL
        double[][] sol = null;
        String methodName = "";
        
        clearScreen();
        System.out.println("------------ Sistem Persamaan Linier ------------");
        System.out.println("Langkah:");

        // Penentuan metode
        switch(method) {
            case 1: sol = SPLSolver.byREF(s, true); methodName = "Eliminasi Gauss"; break;
            case 2: sol = SPLSolver.byRREF(s, true); methodName = "Eliminasi Gauss-Jordan"; break;
            case 3: sol = SPLSolver.byCramer(s, true); methodName = "Kaidah Cramer"; break;
            case 4: sol = SPLSolver.byInverse(s, true); methodName = "Matriks Balikan"; break;
            default: throw new IllegalArgumentException("Metode tidak valid");
        }

        // Formatting output
        StringBuilder sb = new StringBuilder();
        sb.append("Metode:\n").append(methodName).append("\n\n");
        sb.append("Input:\n").append(formatMatrix(augmented)).append("\n");
        sb.append("Solusi:\n").append(formatSPLSolution(sol));
        
        // Print output dulu ke terminal
        System.out.println();
        String out = sb.toString();
        System.out.println(out);

        // Simpan
        askSaveToFile(out);
    }

    // ----------------- Determinan -----------------

    /**
     * Menangani alur perhitungan determinan matriks persegi.
     * Membaca matriks dari keyboard atau file, memvalidasi bahwa matriks
     * persegi, meminta metode perhitungan, lalu mencetak hasilnya.
     * @throws IllegalArgumentException ketika mode input, bentuk matriks, atau metode tidak valid
     */
    static void handleDeterminant() {
        // Presentasikan dengan bagus dan bijak ygy
        clearScreen();
        System.out.println("------------ Determinan Matriks ------------");
        System.out.println("[Jenis Input]");
        System.out.println("1. Input dari keyboard");
        System.out.println("2. Input dari file .txt");
        System.out.print("Pilihan: ");
        int mode = readInt(sc);

        // Baca data
        double[][] matrix;
        if (mode == 1) {
            System.out.println("\n[Input Matriks]");
            System.out.print("Ukuran matriks n: ");
            int n = readInt(sc);
            matrix = readMatrixFromKeyboard(sc, n, n);
        } else if (mode == 2) {
            System.out.println("\n[Input Matriks]");
            System.out.println("CWD: " + cwd);
            System.out.print("Path file: ");
            String path = sc.nextLine().trim();
            matrix = readMatrixFromFile(path);
            System.out.println("File terbaca!");
        } else {
            throw new IllegalArgumentException("Mode input tidak valid");
        }

        // Validasi matriks kuadrat
        validateSquareMatrix(matrix);

        // Tampil menu dan pilih metode cari determinan
        showDeterminantMenu();
        int method = readInt(sc);

        // Solve determinan
        double det = 0;
        String methodName = "";
        
        // handle yg tidak diinginkan

        clearScreen();
        System.out.println("------------ Determinan Matriks ------------");
        System.out.println("Langkah:");

        // Penentuan metode
        switch (method) {
            case 1:
                det = Matrix.det(toMatrix(matrix), true);
                methodName = "Ekspansi Kofaktor";
                break;
            case 2:
                det = Matrix.gdet(toMatrix(matrix), true);
                methodName = "Reduksi Baris";
                break;
            default:
                throw new IllegalArgumentException("Metode tidak valid");
        }

        // Format output
        StringBuilder sb = new StringBuilder();
        sb.append("Metode:\n").append(methodName).append("\n\n");
        sb.append("Input:\n").append(formatMatrix(matrix)).append("\n");
        sb.append("Determinan:\n").append(String.format("%.3f", det)).append("\n");
        
        // Print output dulu ke terminal
        System.out.println();
        String out = sb.toString();
        System.out.println(out);

        // Simpan
        askSaveToFile(out);
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
        // Presentasikan dengan bagus dan bijak ygy
        clearScreen();
        System.out.println("------------ Invers Matriks ------------");
        System.out.println("[Jenis Input]");
        System.out.println("1. Input dari keyboard");
        System.out.println("2. Input dari file .txt");
        System.out.print("Pilihan: ");
        int mode = readInt(sc);

        // Baca data (dari file/keyboard)
        double[][] matrix;
        if (mode == 1) {
            System.out.println("\n[Input Matriks]");
            System.out.print("Ukuran matriks n: ");
            int n = readInt(sc);
            matrix = readMatrixFromKeyboard(sc, n, n);
        } else if (mode == 2) {
            System.out.println("\n[Input Matriks]");
            System.out.println("CWD: " + cwd);
            System.out.print("Path file: ");
            String path = sc.nextLine().trim();
            matrix = readMatrixFromFile(path);
            System.out.println("File terbaca!");
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

        clearScreen();
        System.out.println("------------ Invers Matriks ------------");
        System.out.println("Langkah:");

        switch(method) {
            case 1:
                inverse = toArray(Matrix.ginv(toMatrix(matrix), true));
                methodName = "Augmen";
                break;
            case 2:
                inverse = toArray(Matrix.inv(toMatrix(matrix), true));
                methodName = "Adjoin";
                break;
            default:
                throw new IllegalArgumentException("Metode tidak valid");
        }
    
        // Formatting output
        StringBuilder sb = new StringBuilder();
        sb.append("Metode:\n").append(methodName).append("\n\n");
        sb.append("Input:\n").append(formatMatrix(matrix)).append("\n");
        sb.append("Matriks Balikan:\n").append(formatMatrix(inverse)).append("\n");
       
        // Print output dulu ke terminal
        System.out.println();
        String out = sb.toString();
        System.out.println(out);

        // Simpan
        askSaveToFile(out);
    }

    // ----------------- Interpolasi -----------------

    static void handlePolynomialInterpolation() {
        // Presentasikan dengan bagus dan bijak ygy
        clearScreen();
        System.out.println("------------ Interpolasi Polinomial ------------");
        System.out.println("[Jenis Input]");
        System.out.println("1. Input dari keyboard");
        System.out.println("2. Input dari file .txt");
        System.out.print("Pilihan: ");
        int mode = readInt(sc);

        // Baca data (dari file/keyboard)
        double[][] points = null;
        if (mode == 1) {
            System.out.println("\n[Input Titik]");
            System.out.print("Jumlah titik n: ");
            int n = readInt(sc);
            points = readPointsFromKeyboard(sc, n);
        } else if (mode == 2) {
            System.out.println("\n[Input Titik]");
            System.out.println("CWD: " + cwd);
            System.out.print("Path file: ");
            String path = sc.nextLine().trim();
            points = readPointsFromFile(path);
            System.out.println("File terbaca!");
        } else {
            throw new IllegalArgumentException("Mode input tidak valid");
        }

        // Validasi tiap titik tidak kosong & berpasangan
        validatePoints(points);

        // Solve interpolasi
        double[] coeffs = Interpolation.Polynomial(points);
        String equation = formatPolynomialEquation(coeffs);
        Arrays.sort(points, (a,b) -> a[0] > b[0] ? 1 : -1);

        // Formatting output
        StringBuilder sb = new StringBuilder();
        sb.append("Metode:\nInterpolasi Polinomial\n\n");
        sb.append("Domain:\n").append(String.format("[%.3f, %.3f]\n\n", points[0][0], points[coeffs.length-1][0]));
        sb.append("Persamaan:\ny = ").append(equation).append("\n");

        clearScreen();
        System.out.println("------------ Interpolasi Polinomial ------------");
        System.out.println("[Hasil]");
        System.out.println(sb.toString());

        // Evaluasi hasil di suatu koordinat x (opsional) 
        System.out.print("Masukkan xt untuk evaluasi (harus dalam domain, atau ketik 'skip'): ");
        String input = sc.nextLine();
        if (!input.equalsIgnoreCase("skip")) {
            double xt = parseNumber(input);
            if(xt < points[0][0] || xt > points[coeffs.length-1][0])
                throw new IllegalArgumentException("Nilai tidak berada dalam rentang!");
            double yt = evalPolynomial(coeffs, xt);
            String evalLine = String.format("y(%.3f) = %.3f%n", xt, yt);
            System.out.println(evalLine);
            sb.append(evalLine);
        }

        // Simpan
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
        // Presentasikan dengan bagus dan bijak ygy
        clearScreen();
        System.out.println("------------ Interpolasi Cubic Spline ------------");
        System.out.println("[Jenis Input]");
        System.out.println("1. Input dari keyboard");
        System.out.println("2. Input dari file .txt");
        System.out.print("Pilihan: ");
        int mode = readInt(sc);

        // Baca data (dari file/keyboard)
        double[][] points;
        if (mode == 1) {
            System.out.println("\n[Input Titik]");
            System.out.print("Jumlah titik n: ");
            int n = readInt(sc);
            points = readPointsFromKeyboard(sc, n);
        } else if (mode == 2) {
            System.out.println("\n[Input Titik]");
            System.out.println("CWD: " + cwd);
            System.out.print("Path file: ");
            String path = sc.nextLine().trim();
            points = readPointsFromFile(path);
            System.out.println("File terbaca!");
        } else {
            throw new IllegalArgumentException("Mode input tidak valid");
        }

        // Validasi tiap titik tidak kosong & berpasangan
        validatePoints(points);

        // Cari turunan kedua dari spline dari tiap point 
        double[] knots = Interpolation.CubicSplinal(points);

        // Formatting output
        StringBuilder sb = new StringBuilder();
        sb.append("Metode:\nNatural Cubic Spline\n\n");
        sb.append("Persamaan setiap segmen:\n");
        for(int i = 0; i < points.length-1; ++i)
            sb.append(formatSegmentInterpolation(points, knots, i));
           
        sb.append("\nDomain setiap segmen:\n");
        for (int i = 0; i < points.length-1; i++) {
            if(i < points.length-2) sb.append(String.format("- D{%d,%d} = [%.3f, %.3f)\n", i, i+1, points[i][0], points[i+1][0]));
            else sb.append(String.format("- D{%d,%d} = [%.3f, %.3f]\n", i, i+1, points[i][0], points[i+1][0]));
        }

        sb.append("\nNilai turunan kedua tiap titik:\n");
        for (int i = 0; i < knots.length; i++)
            sb.append(String.format("- k%d = %.3f%n", i, knots[i]));

        clearScreen();
        System.out.println("------------ Interpolasi Cubic Spline ------------");
        System.out.println("[Hasil]");
        System.out.println(sb.toString());

        // Evaluasi hasil di suatu koordinat x (opsional)
        System.out.print("Masukkan xt untuk evaluasi (harus dalam domain, atau ketik 'skip'): ");
        String input = sc.nextLine();
        if (!input.equalsIgnoreCase("skip")) {
            double xt = parseNumber(input);
            double yt = Interpolation.CubicSplinalEvaluate(points, knots, xt); // <-- fixed
            String evalLine = String.format("y(%.3f) = %.3f%n", xt, yt);
            System.out.println(evalLine);
            sb.append(evalLine);
        }

        // Simpan
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
        // Presentasikan dengan bagus dan bijak ygy
        clearScreen();
        System.out.println("------------ Regresi Cubic Spline ------------");
        System.out.println("[Jenis Input]");
        System.out.println("1. Input dari keyboard");
        System.out.println("2. Input dari file .txt");
        System.out.print("Pilihan: ");
        int mode = readInt(sc);

        double[][] points = null;
        double[] knots = null;

        // Input titik dan knots
        if (mode == 1) {
            System.out.println("\n[Input Titik]");
            System.out.print("Jumlah titik n: ");
            int n = readInt(sc);
            points = readPointsFromKeyboard(sc, n);

            System.out.println("\n[Input Knot]");
            System.out.print("Jumlah knot k: ");
            int k = readInt(sc);
            knots = new double[k];
            for (int i = 0; i < k; i++) {
                System.out.print("  knot[" + i + "]: ");
                knots[i] = readNumber(sc);
            }
        } else if (mode == 2) {
            System.out.println("\n[Input Titik]");
            System.out.println("CWD: " + cwd);
            System.out.print("Path file: ");
            String path = sc.nextLine().trim();
            points = readPointsFromFile(path);
            System.out.println("File terbaca!");
            
            System.out.println("\n[Input Knot]");
            System.out.print("Jumlah knot k: ");
            int k = readInt(sc);
            knots = new double[k];
            for (int i = 0; i < k; i++) {
                System.out.print("  knot[" + i + "]: ");
                knots[i] = readNumber(sc);
            }
        } else {
            throw new IllegalArgumentException("Mode input tidak valid");
        }

        // Validasi poin dan knot
        validateRegressionInput(points, knots);

        double[] coeffs = Regression.CubicSplinal(points, knots);
        String equation = formatTruncatedPowerEquation(coeffs, knots);

        // Ngeoutput
        StringBuilder sb = new StringBuilder();
        sb.append("Koefisien Regresi:\n");
        for(int i = 0; i < coeffs.length; ++i)
            sb.append(String.format("- B%d = %.3f\n", i, coeffs[i]));
        sb.append("\nPosisi knot:\n");
        for(int i = 0; i < knots.length; ++i)
            sb.append(String.format("- k%d = (%.3f, %.3f)\n", i, knots[i], Regression.CubicSplinalEvaluate(coeffs, knots, knots[i])));
        sb.append("\nPersamaan:\ny = ").append(equation).append("\n");

        // Presentate hasilnya secara beaotipul
        System.out.println("------------ Regresi Cubic Spline ------------");
        System.out.println("[Hasil]");
        System.out.println(sb.toString());

        // evaluasi kah bos?
        System.out.print("Masukkan xt untuk evaluasi (atau 'skip'): ");
        if(sc.hasNextLine()) sc.nextLine();
        String input = sc.nextLine();
        if (!input.equalsIgnoreCase("skip")) {
            double xt = parseNumber(input);
            // acc twin
            double yt = Regression.CubicSplinalEvaluate(knots, knots, xt);
            String evalLine = String.format("y(%.3f) = %.3f%n", xt, yt);
            System.out.println(evalLine);
            sb.append(evalLine);
        }

        // Simpan
        askSaveToFile(sb.toString());
    }

    // ----------------- Image Hole Fill -----------------

    /**
     * Menangani bonus Image Hole Filling.
     * Meminta tiga path (gambar asli, mask, output), menjalankan algoritma
     * pengisian lubang berbasis rata-rata tetangga iteratif, lalu mencetak
     * ringkasan statistik (ukuran, jumlah hole, iterasi, error akhir).
     * @throws RuntimeException ketika gambar gagal dibaca atau ditulis
     */
    static void handleImageHoleFill() {
        clearScreen();
        System.out.println("------------ Image Hole Filling ------------");
        System.out.println("[Input Path]");
        System.out.println("(Sangat disarankan mask berformat lossless (.png))");
        System.out.println("CWD: " + cwd);
        System.out.print("Path gambar asli (.png/.jpg): ");
        String imagePath = sc.next();
        System.out.print("Path mask (.png/.jpg): ");
        String maskPath = sc.next();
        System.out.print("Path output (.png/.jpg): ");
        String outputPath = sc.next();
 
        // Sok panggil fungsina Alddoo
        ImageHoleFill.Result result;
        try {
            result = ImageHoleFill.fill(imagePath, maskPath, outputPath);
        } catch (IOException e) {
            throw new RuntimeException("Gagal memproses gambar: " + e.getMessage());
        }
 
        // Konstruksi output
        StringBuilder sb = new StringBuilder();
        sb.append("Metode: Image Hole Filling (Rata-rata Tetangga Iteratif)\n");
        sb.append("Ukuran gambar: ").append(result.width).append(" x ").append(result.height).append("\n");
        sb.append("Jumlah pixel hole: ").append(result.holeCount).append("\n");
        sb.append("Jumlah iterasi: ").append(result.iterations).append("\n");
        sb.append("Error akhir: ").append(String.format("%.3f", result.finalError)).append("\n");
        sb.append("Output disimpan di: ").append(result.outputPath).append("\n");
 
        // Tampil output
        clearScreen();
        System.out.println("------------ Image Hole Filling ------------");
        System.out.println("[Hasil]");
        System.out.println(sb.toString());
 
        // As usual
        if(sc.hasNextLine()) sc.nextLine();
        askSaveToFile(sb.toString());
    }

    // ----------------- Submenu -----------------

    /**
     * Mencetak submenu metode penyelesaian SPL.
     */
    static void showSPLMenu() {
        System.out.println("\n[Metode Penyelesaian]");
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
        System.out.println("\n[Metode Penyelesaian]");
        System.out.println("1. Metode Ekspansi Kofaktor");
        System.out.println("2. Metode Reduksi Baris");
        System.out.print("Pilihan: ");
    }

    /**
     * Mencetak submenu metode pencarian invers.
     */
    static void showInverseMenu() {
        System.out.println("\n[Metode Penyelesaian]");
        System.out.println("1. Metode Augmen");
        System.out.println("2. Metode Adjoin");
        System.out.print("Pilihan: ");
    }

    // ----------------- Input Helpers -----------------

    /**
     * Membaca sebuah bilangan bulat dari Scanner.
     * @param sc Scanner sumber
     * @return bilangan bulat yang berhasil dibaca
     */
    public static int readInt(Scanner sc) {
        if(!sc.hasNextInt())
            throw new IllegalArgumentException("Input bukan bilangan bulat!");

        String input = sc.nextLine().trim();
        int val = Integer.parseInt(input);
        return val;
    }

    /**
     * Membaca sebuah bilangan riil dari Scanner.
     * Mendukung pemisal desimal {@code "."} maupun {@code ","}.
     * @param sc Scanner sumber
     * @return bilangan bulat yang berhasil dibaca
     */
    public static double readNumber(Scanner sc) {
        String input = sc.next().trim();
        if(!input.matches("-?\\d+([.,]\\d+)?"))
            throw new IllegalArgumentException("Input bukan bilangan!");
        
        double val = parseNumber(input);
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
        String t = s.trim().replace(",", ".");
        
        // Cek validitas
        for(int i = 0, c = 0; i < t.length(); ++i) {
            if(t.charAt(i) == '.') c++;
            if(c > 1) throw new IllegalArgumentException("Bilangan tidak bisa di-parse!");
        }

        return Double.parseDouble(t);
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
                m[i][j] = readNumber(sc);
            }
            sc.nextLine();
        }
        return m;
    }

    /**
     * Membaca matriks ukuran sembarang dari file teks.
     * @param path path file masukan
     * @return matriks augmented hasil pembacaan
     * @throws RuntimeException ketika file gagal dibaca
     */
    static double[][] readMatrixFromFile(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            double[][] m = new double[maxOrdo][maxOrdo];
            String l = "";
            int row = 0, col = 0, colp = 0;
            boolean b = false;
            
            // baca sampe eof
            while((l = br.readLine()) != null) {
                String[] e = l.trim().split("\\s+");
                if(e.length == 0 || e[0].isEmpty()) continue;

                if(!b) { col = e.length; colp = e.length; b = true; }
                else { colp = col; col = e.length; }

                if(col != colp) throw new IllegalArgumentException("Input tidak konsisten!");

                for(int j = 0; j < col; ++j)
                    m[row][j] = parseNumber(e[j]);
                row++;
            }

            // disalin, tapi kali ini ukurannya sesuai ygy
            double[][] r = new double[row][col];
            for(int i = 0; i < row; ++i)
                for(int j = 0; j < col; ++j)
                    r[i][j] = m[i][j];
                    
            return r;
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
        if(rows <= 0) throw new IllegalArgumentException("Jumlah persamaan tidak valid");
        if(vars <= 0) throw new IllegalArgumentException("Jumlah variabel tidak valid");
        double[][] m = new double[rows][vars + 1];
        for (int i = 0; i < rows; i++) {
            System.out.println("Persamaan " + (i + 1) + ":");
            for (int j = 0; j < vars; j++) {
                System.out.print("  koef x" + (j + 1) + ": ");
                m[i][j] = readNumber(sc);
            }
            System.out.print("  konstanta (ruas kanan): ");
            m[i][vars] = readNumber(sc);
            sc.nextLine();
        }
        return m;
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
            pt[i][0] = readNumber(sc);
            pt[i][1] = readNumber(sc);
            sc.nextLine();
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
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            double[][] pt = new double[maxPt][2];
            String l = "";
            int len = 0;
            
            // baca sampe eof
            while((l = br.readLine()) != null) {
                String[] e = l.trim().split("\\s+");
                if(e.length == 0 || e[0].isEmpty()) continue;
                if(e.length != 2) throw new IllegalArgumentException("Input tidak konsisten!");

                pt[len][0] = parseNumber(e[0]);
                pt[len][1] = parseNumber(e[1]);
                len++;
            }

            // disalin, tapi kali ini ukurannya sesuai ygy
            double[][] r = new double[len][2];
            for(int i = 0; i < len; ++i) {
                r[i][0] = pt[i][0];
                r[i][1] = pt[i][1];
            } 
                    
            return r;
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
            for (double v : row)
                sb.append(String.format("%.3f ", v));
            sb.append("\n");
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
            sb.append("x").append(i).append(" = ");
            boolean free = true;
            for(int j = 1; j < sol[0].length; ++j)
                if(!Matrix.swithin(sol[i][j],e))
                    free = false;
            if(!Matrix.swithin(sol[i][0],e) || free) sb.append(String.format("%.3f", sol[i][0]));
            for (int k = 1; k <= maxFree; k++) {
                if (k >= sol[i].length || sol[i][k] == 0) continue;
                double c = sol[i][k];
                boolean cw0 = Matrix.swithin(c, e);
                boolean cw1 = Matrix.swithin(Math.abs(c)-1, e);
                if(!Matrix.swithin(sol[i][k-1], e)) sb.append(c < 0 ? " - " : " + ");
                if(!cw0 && !cw1) sb.append(String.format("%.3f", Math.abs(c)));
                if(!cw0) sb.append("a").append(k-1);
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
        
        return formatEquationTerms(coeffs, basis, e);
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
            else basis[i] = String.format("max(0,x-%.3f)^3", knots[i-4]);
        }
        return formatEquationTerms(coeffs, basis, 1e-3);
    }

    /**
     * Menggabungkan koefisien dan basis simboliknya menjadi satu string persamaan
     * yang mudah dibaca. Koefisien dengan nilai absolut di bawah {@code 1e-9}
     * dihilangkan. Mengembalikan {@code "0"} jika semua suku hilang.
     * @param coeffs nilai koefisien
     * @param basis representasi simbolik tiap suku (string kosong untuk konstanta)
     * @return string persamaan terformat
     */
    static String formatEquationTerms(double[] coeffs, String[] basis, double ep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coeffs.length; i++) {
            double c = coeffs[i];
            double ac = Math.abs(c);
            if (Matrix.swithin(c, ep)) 
                continue;
            
            if(!basis[i].isEmpty()) {
                sb.append(c < 0 ? (sb.length() == 0  ? '-' : " - ") : (sb.length() == 0  ? "" : " + "));
                sb.append(!Matrix.swithin(ac-1, ep) ? String.format("%.3f", ac) : "");
                sb.append(!Matrix.swithin(ac-1, ep) && !basis[i].isEmpty() ? "*" : "");
                sb.append(basis[i]);
            } else {
                sb.append(c < 0 ? (sb.length() == 0  ? '-' : " - ") : (sb.length() == 0  ? "" : " + "));
                sb.append(String.format("%.3f", ac));
            }
        }

        if (sb.length() == 0) sb.append("0");

        return sb.toString();
    }

    /**
     * Mengonstruksi string persamaan fungsi segmen Interpolasi Cubic Spline ke-{@code {i,i+1}}.
     * @param pt data points
     * @param kn knots
     * @param i indeks
     * @return string persamaan
     */
    static String formatSegmentInterpolation(double[][] pt, double[] kn, int i) {
        StringBuilder r = new StringBuilder();
        
        // global lah ya
        double px0 = pt[i][0], pxp = pt[i+1][0];
        double py0 = pt[i][1], pyp = pt[i+1][1];
        double apx0 = Math.abs(px0), apxp = Math.abs(pxp);
        // double apy0 = Math.abs(py0), apyp = Math.abs(pyp);
        double dx = px0 - pxp, adx = Math.abs(dx);
        double k1 = kn[i] / 6, ak1 = Math.abs(k1);
        double k2 = kn[i+1] / 6, ak2 = Math.abs(k2);
        double k3 = py0 / dx, ak3 = Math.abs(k3);
        double k4 = -pyp / dx, ak4 = Math.abs(k4);
        
        // global boolean
        boolean k1NOL = Matrix.swithin(k1, e);
        boolean k2NOL = Matrix.swithin(k2, e);
        boolean k3NOL = Matrix.swithin(k3, e);
        boolean k4NOL = Matrix.swithin(k4, e);

        r.append(String.format("- f{%d,%d}(x) = ", i, i+1));

        // term pertama
        if(!k1NOL) {
            char k1s = k1 < 0 ? '-' : '\0';
            char pxps = pxp < 0 ? '+' : '-';
            char dxs = dx < 0 ? '-' : '\0';
            char dxso = dx < 0 ? '+' : '\0';
            String adxg = Matrix.swithin(adx-1, e) ? "" : String.format("/%.3f", adx);
            String ak1gz = Matrix.swithin(ak1-1, e) ? "" : String.format("%.3f*", ak1);
            String adxgz = Matrix.swithin(adx-1, e) ? "" : String.format("%.3f*", adx);

            r.append(String.format(
                "%c%s(%c(x%c%.3f)^3%s%c%s(x%c%.3f))",
                k1s, ak1gz, dxs, pxps, apxp, adxg, dxso, adxgz, pxps, apxp
            ));
        }

        // operator antara term 1 dan 2
        if(!k1NOL && !k2NOL) {
            if(k2 < 0) r.append(" + ");
            else if(k2 > 0) r.append(" - "); // takut klo pake else doang
        }

        // term kedua
        if(!k2NOL) {
            char k2s = (k2 > 0 && k1NOL) ? '-' : (k1NOL ? '+' : '\0');
            char px0s = px0 < 0 ? '+' : '-';
            char dxs = dx < 0 ? '-' : '\0';
            char dxso = dx < 0 ? '+' : '\0';
            String adxg = Matrix.swithin(adx-1, e) ? "" : String.format("/%.3f", adx);
            String ak2gz = Matrix.swithin(ak2-1, e) ? "" : String.format("%.3f*", ak2);
            String adxgz = Matrix.swithin(adx-1, e) ? "" : String.format("%.3f*", adx);

            r.append(String.format(
                "%c%s(%c(x%c%.3f)^3%s%c%s(x%c%.3f))",
                k2s, ak2gz, dxs, px0s, apx0, adxg, dxso, adxgz, px0s, apx0
            ));
        }

        // operator antara term 2 dan 3
        if((!k1NOL && !k3NOL) || (!k2NOL && !k3NOL)) {
            if(k3 < 0) r.append(" - ");
            else if(k3 > 0) r.append(" + "); // takut klo pake else doang
        }

        // term ketiga
        if(!k3NOL) {
            char k3s = (k3 < 0 && k1NOL && k2NOL) ? '-' : '\0';
            char pxps = pxp < 0 ? '+' : '-';
            String ak3gz = Matrix.swithin(ak3-1, e) ? "" : String.format("%.3f*", ak3);

            r.append(String.format(
                "%c%s(x%c%.3f)",
                k3s, ak3gz, pxps, apxp
            ));
        }

        // operator antara term 3 dan 4
        if((!k1NOL && !k4NOL) || (!k2NOL && !k4NOL) || (!k3NOL && !k4NOL)) {
            if(k4 < 0) r.append(" - ");
            else if(k4 > 0) r.append(" + "); // takut klo pake else doang
        }

        // term keempat
        if(!k4NOL) {
            char k4s = (k4 < 0 && k1NOL && k2NOL && k3NOL) ? '-' : '\0';
            char px0s = px0 < 0 ? '+' : '-';
            String ak4gz = Matrix.swithin(ak4-1, e) ? "" : String.format("%.3f*", ak4);

            r.append(String.format(
                "%c%s(x%c%.3f)",
                k4s, ak4gz, px0s, apx0
            ));
        }

        r.append("\n");
        return r.toString();
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
        System.out.print("Tekan enter untuk lanjut...");
        sc.nextLine();
    }

    /**
     * Menanyakan kepada pengguna apakah ingin menyimpan hasil ke file.
     * Jika ya, meminta nama file lalu menuliskan kontennya.
     * @param content teks yang akan disimpan
     */
    static void askSaveToFile(String content) {
        System.out.print("Simpan hasil ke file .txt? (y/n): ");
        String ans = sc.nextLine();
        if (ans.equalsIgnoreCase("y")) {
            System.out.print("Nama file: ");
            String filename = sc.nextLine();
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