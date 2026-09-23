package algeo.modules;

import java.util.Arrays;

public class Interpolation {
    /**
     * Mengonstruksi polinomial berderajat {@code N} dari {@code N+1} data points.
     * @param pt data points
     * @return nilai konstanta tiap suku dalam polinom
     * @throws IllegalArgumentException ketika jumlah data points kurang dari 2
     * @throws RuntimeException ketika ditemukan bahwa tidak ditemukan solusi non-parametrik
     */
    public static double[] Polynomial(double[][] pt) {
        // ayoyooo kurang data woi, serius lah camen!
        if(pt.length < 2)
            throw new IllegalArgumentException("Interpolation.Polynomial: Butuh setidaknya 2 data point!");
        
        // membuat spl dalam matriks
        Matrix s = new Matrix(pt.length, pt.length+1);
        double accu = 1;
        for(int i = 0; i < s.rows; ++i) {
            for(int j = 0; j < s.cols; ++j) {
                if(j == s.cols-1) {
                    s.src[i][j] = pt[i][1];
                    continue;
                }
                
                s.src[i][j] = accu;
                accu *= pt[i][0];
            }
            accu = 1;
        }

        // yach, selesaikan
        double[][] sol = SPLSolver.byRREF(s);

        // cek data point yang kembar
        // alias ngecek apakah ada baris yg semuanya 0
        for(int i = 0; i < s.cols-1; ++i)
            for(int j = 1; j < s.cols; ++j)
                if(sol[i][j] != 0)
                    throw new RuntimeException("Interpolation.Polynomial: Ada data points yang kembar!");

        // sesuaikan mas mbak
        double[] r = new double[pt.length];
        for(int i = 0; i < pt.length; ++i)
            r[i] = sol[i][0];

        return r;
    }

    /**
     * Mengembalikan kumpulan knots yang dipakai dalam konstruksi interpolasi splinal kubik.
     * @param pt data points
     * @return kumpulan knots
     * @throws IllegalArgumentException ketika jumlah data points kurang dari 2
     * @throws IllegalArgumentException ketika ada data point dengan absis sama atau sangat berdekatan
     */
    public static double[] CubicSplinal(double[][] pt) {
        // https://medium.com/data-science/numerical-interpolation-natural-cubic-spline-52c1157b98ac
        // special thengks
        // ayoyooo kurang data woi, serius lah camen!
        if(pt.length < 3)
            throw new IllegalArgumentException("Interpolation.Polynomial: Butuh setidaknya 3 data point!");

        // tapi sebelum itu sort dulu yak data pointnya
        // terus kita safety cekkk
        Arrays.sort(pt, (a,b) -> a[0] > b[0] ? 1 : -1);
        for(int i = 0; i < pt.length-1; ++i)
            if(Matrix.swithin(pt[i+1][0] - pt[i][0], Matrix.CEPSILON))
                throw new IllegalArgumentException("Interpolation.Polynomial: Ada data point dengan absis sangat dekat!");
        
        // nyiapin spl untuk nyari knot
        Matrix kl = new Matrix(pt.length-2, pt.length-2); // ruas kiri
        Matrix kr = new Matrix(pt.length-2, 1); // ruas kanan
        for(int i = 0; i < kl.rows; ++i) {
            // ini untuk ruas kiri
            if(i > 0) kl.src[i][i-1] = pt[i][0] - pt[i+1][0];
            kl.src[i][i] = 2 * (pt[i][0] - pt[i+2][0]);
            if(i < kl.rows-1) kl.src[i][i+1] = pt[i+1][0] - pt[i+2][0];
            
            // ini untuk ruas kanan
            double ml = (pt[i][1] - pt[i+1][1]) / (pt[i][0] - pt[i+1][0]);
            double mr = (pt[i+1][1] - pt[i+2][1]) / (pt[i+1][0] - pt[i+2][0]);
            kr.src[i][0] = 6 * (ml - mr);
        }

        // temukan nilai knotnya!
        // terus sesuaikan lah mas mbak
        Matrix kmatrix = Matrix.aug(kl, kr);
        double[][] k = SPLSolver.byREF(kmatrix);
        double[] r = new double[k.length+2];
        for(int i = 0; i < k.length; ++i)
            r[i+1] = k[i][0];

        return r;
    }

    /**
     * Mengevaluasi nilai {@code x} dari fungsi hasil Interpolasi Natural Cubic Spline.
     * @param pt data points
     * @param x nilai masukan
     * @return nilai keluran
     * @throws IllegalArgumentException nilai masukan berada di luar domain
     */
    public static double CubicSplinalEvaluate(double[][] pt, double x) {
        // cari rentang dan validasi
        // jujur maaf ya gw nulisnya begini...
        int i = 0;
        boolean o = false;
        Arrays.sort(pt, (a,b) -> a[0] > b[0] ? 1 : -1);
        if(x < pt[0][0] || x > pt[pt.length-1][0]) o = true;
        for(; i < pt.length && !o; ++i) if(x < pt[i][0]) break;
        i--;

        // seriusssss lah camen
        if(o) throw new IllegalArgumentException("Interpolation.CubicSplinalEvaluate: nilai x berada di luar rentang!");
        // System.out.println(i);

        // saatnya hitung
        double[] k = CubicSplinal(pt);
        double t11 = Math.pow(x - pt[i+1][0], 3) / (pt[i][0] - pt[i+1][0]);
        double t12 = (x - pt[i+1][0]) * (pt[i][0] - pt[i+1][0]);
        double t1 = k[i] / 6 * (t11 - t12);
        double t21 = Math.pow(x - pt[i][0], 3) / (pt[i][0] - pt[i+1][0]);
        double t22 = (x - pt[i][0]) * (pt[i][0] - pt[i+1][0]);
        double t2 = k[i+1] / 6 * (t21 - t22);
        double t31 = pt[i][1] * (x - pt[i+1][0]) - pt[i+1][1] * (x - pt[i][0]);
        double t3 = t31 / (pt[i][0] - pt[i+1][0]);
        double r = t1 - t2 + t3;

        return r;
    }
}