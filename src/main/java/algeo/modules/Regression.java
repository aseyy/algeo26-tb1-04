package algeo.modules;

import java.util.Arrays;

/**
 * Regresi sederhana menggunakan algoritma yang memanfaatkan SPLSolver.
 * Berikut adalah daftar fungsinya:
 * <ul>
 *  <li>{@link Regression#CubicSplinal() static Regression.CubicSplinal}</li>
 *  <li>{@link Regression#CubicSplinalEvaluate() static Regression.CubicSplinalEvaluate}</li>
 * </ul>
 * @author Fachry Azriel Fajdwani (rabsed1)
 * @since 23/10/2026
 */
public class Regression {
    /**
     * Membentuk fungsi regresi Cubic Spline dengan Truncated Power Basis.
     * @param pt data points
     * @param kn knots
     * @return koefisien untuk tiap suku
     * @throws IllegalArgumentException ketika jumlah data point tidak mumpuni
     * @throws IllegalArgumentException ketika ada knots yang kembar
     * @throws IllegalArgumentException ketika ada setidaknya 2 data point yang memiliki absis yang sama atau sangat dekat
     */
    public static double[] CubicSplinal(double[][] pt, double[] kn) {
        // biar mempermudah hidup aja, init
        int K = kn.length;
        int N = pt.length;
        if(N < K+4)
            throw new IllegalArgumentException("Regression.CubicSplinal: Data points tidak cukup!");

        // sort dulu bosq knotsnya
        // terus cek validitasnya
        Arrays.sort(kn);
        for(int i = 0; i < K - 1; ++i)
            if(Matrix.swithin(kn[i+1] - kn[i], Matrix.CEPSILON))
                throw new IllegalArgumentException("Regression.CubicSplinal: Ada knots yang kembar!");
        
        // sort dulu array-pointnya
        // dan cek juga validitasnya
        Arrays.sort(pt, (a,b) -> a[0] >= b[0] ? -1 : 1);
        for(int i = 0; i < pt.length-1; ++i)
            if(Matrix.swithin(pt[i+1][0] - pt[i][0], Matrix.CEPSILON))
                throw new IllegalArgumentException("Regression.CubicSplinal: Ada data points dengan absis sangat dekat!");
        
        // bikin matriks persamaan
        Matrix X = new Matrix(N, K+4);
        Matrix Y = new Matrix(N, 1);
        for(int i = 0; i < N; ++i) {
            double accu = 1;
            for(int j = 0; j < X.cols; ++j) {
                if(j < 4) {
                    X.src[i][j] = accu;
                    accu *= pt[i][0];
                    continue;
                }
                if(pt[i][0] <= kn[j-4]) X.src[i][j] = 0;
                else X.src[i][j] = Math.pow(pt[i][0] - kn[j-4], 3);
            }
            Y.src[i][0] = pt[i][1];
        }

        // bikin matriks... A dan b
        Matrix XT = Matrix.tr(X);
        Matrix A = Matrix.mul(XT, X);
        Matrix b = Matrix.mul(XT, Y);
        Matrix s = Matrix.aug(A, b);

        // normalization helps deh
        // jujur ini nih formula gampang bgt meledak
        // errornya gila men, > 1e-5
        s.toREF();
        for(int i = 0; i < s.rows; ++i)
            for(int j = 0; j < s.cols; ++j)
                s.src[i][j] = Matrix.snorm(s.src[i][j], 1e-12);
        
        // cari koefisien! lalu sesuaikan!
        double[][] sol = SPLSolver.byRREF(s);
        double[] r = new double[sol.length];
        for(int i = 0; i < r.length; ++i)
            r[i] = sol[i][0];

        return r;
    }

    /**
     * Mengevaluasi (mengekstrapolasi) suatu nilai x dengan fungsi regresi Cubic Splinal.
     * @param pt data points
     * @param kn knots
     * @param x nilai masukan
     * @return nilai keluaran
     */
    public static double CubicSplinalEvaluate(double[][] pt, double[] kn, double x) {
        double[] coeff = CubicSplinal(pt, kn);

        // evaluasiiii
        double val = 0;
        for(int i = 0; i < coeff.length; ++i) {
            if(i < 4) val += coeff[i] * Math.pow(x, i);
            else if(pt[i][0] <= kn[i-4]) val += 0;
            else val += coeff[i] * Math.pow(x - kn[i-4], 3);
        }

        return val;
    }
}