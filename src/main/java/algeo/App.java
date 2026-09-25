package algeo;
import java.util.Scanner;
import java.io.*;

import algeo.modules.*;

public class App {
    // Buat saty scanner yang kepake buat keseluruhan fungsi
    static Scanner sc = new Scanner(System.in);

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
    static void handleRegression() {
        
    }

    // ----------------- Bonus: Image Hole Fill -----------------
    static void handleImageHoleFill() {
        
    }

    // ----------------- submenu -----------------
    static void showSPLMenu() {
        System.out.println("------------ Menu Sistem Persamaan Linier ------------");
        System.out.println("1. Metode Eliminasi Gauss");
        System.out.println("2. Metode Eliminasi Gauss-Jordan");
        System.out.println("3. Kaidah Cramer");
        System.out.println("4. Metode Matriks Balikan");
        System.out.print("Pilihan: ");
    }
    static void showDeterminantMenu() {
        System.out.println("------------ Menu Determinan ------------");
        System.out.println("1. Metode Ekspansi Kofaktor");
        System.out.println("2. Metode Reduksi Baris (Operasi Baris Elementer)");
        System.out.print("Pilihan: ");
    }
    static void showInverseMenu() {
        System.out.println("------------ Menu Invers ------------");
        System.out.println("1. Metode Augmen");
        System.out.println("2. Metode Adjoin");
        System.out.print("Pilihan: ");
    }
    static void showInterpolationMenu() {
        System.out.println("------------ Menu Interpolasi ------------");
        System.out.println("1. Metode Polinomial");
        System.out.println("2. Metode Natural Cubic Spline");
        System.out.print("Pilihan: ");
    }

    // ----------------- input helpers -----------------
    public static int readInt(Scanner sc) {
        while (!sc.hasNextInt()) {
            String bad = sc.next();
            System.out.println("'" + bad + "' bukan angka. Coba lagi:");
        }
        int val = sc.nextInt();
        sc.nextLine();
        return val;
    }

    static double parseNumber(String s) {
        return Double.parseDouble(s.trim().replace(",", "."));
    }

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

    static double[][] readPointsFromKeyboard(Scanner sc, int n) {
        double[][] pt = new double[n][2];
        for (int i = 0; i < n; i++) {
            System.out.print("Titik " + (i + 1) + " (x y): ");
            pt[i][0] = parseNumber(sc.next());
            pt[i][1] = parseNumber(sc.next());
        }
        return pt;
    }

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


    // ----------------- output helpers -----------------
    static void saveOutputToFile(String filename, String content) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.print(content);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan file: " + e.getMessage());
        }
    }

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

    static double evalPolynomial(double[] coeffs, double x) {
        double val = 0, p = 1;
        for (double c : coeffs) {
            val += c * p;
            p *= x;
        }
        return val;
    }

    static String formatPolynomialEquation(double[] coeffs) {
        String[] basis = new String[coeffs.length];
        for (int i = 0; i < coeffs.length; i++)
            basis[i] = i == 0 ? "" : (i == 1 ? "x" : "x^" + i);
        return formatEquationTerms(coeffs, basis);
    }

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

    static Matrix toMatrix(double[][] arr) {
        Matrix m = new Matrix(arr.length, arr[0].length);
        for (int i = 0; i < arr.length; i++)
            for (int j = 0; j < arr[0].length; j++)
                m.src[i][j] = arr[i][j];
        return m;
    }

    static double[][] toArray(Matrix m) {
        double[][] r = new double[m.rows][m.cols];
        for (int i = 0; i < m.rows; i++)
            for (int j = 0; j < m.cols; j++)
                r[i][j] = m.src[i][j];
        return r;
    }

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

    public static void enterContinue() {
        System.out.print("Tekan Enter Untuk Lanjut...");
        sc.nextLine();
    }

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
    static void validateSquareMatrix(double[][] m) {
        if (m == null || m.length == 0)
            throw new IllegalArgumentException("Matriks kosong!");
        for (double[] row : m)
            if (row.length != m.length)
                throw new IllegalArgumentException("Matriks harus persegi!");
    }

    static void validatePoints(double[][] points) {
        if (points == null || points.length == 0)
            throw new IllegalArgumentException("Data titik kosong!");
        for (double[] p : points)
            if (p.length != 2)
                throw new IllegalArgumentException("Setiap titik harus berupa pasangan (x, y)!");
    }
}