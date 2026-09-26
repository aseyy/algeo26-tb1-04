package algeo.modules;

/**
 * Modul penyelesaian Sistem Persamaan Linear (SPL) berbasis matriks.
 * Berikut adalah daftar metodenya:
 * <ul>
 *  <li>{@link SPLSolver#byREF() static SPLSolver.byREF}</li>
 *  <li>{@link SPLSolver#byRREF() static SPLSolver.byRREF}</li>
 *  <li>{@link SPLSolver#byCramer() static SPLSolver.byCramer}</li>
 *  <li>{@link SPLSolver#byInverse() static SPLSolver.byInverse}</li>
 *  <li>{@link SPLSolver#presentate() static SPLSolver.presentate}</li>
 * </ul>
 * @author Fachry Azriel Fajdwani (rabsed1)
 * @since 20/09/2026
 */
public class SPLSolver {
    /**
     * Menyelesaikan SPL menggunakan Eliminasi Gaussian pada matriks augmented SPL.
     * @param s matriks augmented dari persamaan
     * @return solusi berbentuk array 2D bertipe double
     * @throws RuntimeException ketika ditemukan bahwa SPL tidak mungkin punya solusi
     */
    public static double[][] byREF(Matrix s, boolean dbg) {
        // inisialisasi
        double[][] r = new double[s.cols-1][s.cols];
        Matrix sp = Matrix.copy(s);
        sp.norm(Matrix.NEPSILON);
        sp.toREF(dbg);
        sp.norm(Matrix.NEPSILON);

        // untuk tracking variable apa aja yg udah punya padanan
        boolean[] assigned = new boolean[sp.cols-1];
        int mulc = 1; // ini untuk tracking indeks variabel bebas a_n
        
        if(dbg) System.out.println("- (Melakukan back-substitution)");
        // buat solusi
        for(int i = sp.rows-1; i >= 0; --i) {
            int[] nzeroloc = new int[sp.cols-1];
            int nzeroc = 0;

            // cari indeks dan jumlah non-zero
            for(int j = 0; j < sp.cols - 1; ++j) {
                if(sp.src[i][j] == 0)
                    continue;
                nzeroloc[nzeroc] = j;
                nzeroc++;
            }

            // cek berdasarkan jumlah variable non-0
            switch(nzeroc) {
                // semua 0
                // kasus paling sederhana i love it
                case 0: {
                    if(sp.src[i][sp.cols-1] != 0)
                        throw new RuntimeException("SPLSolver.byREF: Sistem tidak punya solusi!");

                    continue;
                }
                
                // cuma 1
                case 1: {
                    // cek apakah variabel ini sudah terikat variabel lain
                    boolean free = true;
                    for(int k = 0; k < sp.cols; ++k)
                        if(r[nzeroloc[0]][k] != 0)
                            free = false;
                    
                    // klo nggak terikat tapi udah keassigned....
                    // brrti ada 2 persamaan dengan nilai variabel yg berbeda...
                    // nggak mungkin!
                    if(assigned[nzeroloc[0]] && free)
                        throw new RuntimeException("SPLSolver.byREF: Sistem tidak punya solusi!");
                    
                    // yh lgsg assigned saja
                    r[nzeroloc[0]][0] += sp.src[i][sp.cols-1];
                    assigned[nzeroloc[0]] = true;
                    break;
                }

                // ada banyak
                default: {
                    // yg paling kiri jadi pivot
                    // lgsg assigned boss
                    r[nzeroloc[0]][0] += sp.src[i][sp.cols-1];
                    assigned[nzeroloc[0]] = true;

                    // untuk variabel2 yg nggak jadi pivot
                    // bakal pindah ruas!
                    for(int j = 1; j < nzeroc; ++j) {
                        // klo variabel itu belum pernah di-assigned apa2
                        if(!assigned[nzeroloc[j]]) {
                            if(dbg) System.out.printf("  - Memasangkan x%d dengan parametrik a%d\n", nzeroloc[j], mulc-1);
                            assigned[nzeroloc[j]] = true;
                            r[nzeroloc[j]][mulc] = 1;
                            mulc++;
                        }

                        // pindah ruas, lalu ekspansi variabel tersebut
                        // misal x1 (pivot) + 4x2 = 2
                        // nanti ini bakal menghitung x1 = 2 - 4a_n, dengan x2 = a_n.
                        double mul = -sp.src[i][nzeroloc[j]];
                        for(int k = 0; k < sp.cols; ++k)
                            r[nzeroloc[0]][k] += r[nzeroloc[j]][k] * mul;
                    }
                }
            }      

            if(dbg) System.out.printf("  - Substitusi x%d\n", nzeroloc[0]);
            // bakal ngebagi dengan konstanta pivot
            for(int k = 0; k < sp.cols; ++k)
                r[nzeroloc[0]][k] /= sp.src[i][nzeroloc[0]];
        }

        // lah terus klo ada yg belum diassign gimana?
        // kita ikatkan dia dengan variabel lain
        // kan bentuknya a_n. cara tau indeks n?
        // pake mulc. gw nggak pinter kasih nama....
        for(int j = sp.cols-2; j >= 0; --j) {
            if(assigned[j])
                continue;
            if(dbg) System.out.printf("  - Memasangkan x%d dengan parametrik a%d\n", j, mulc-1);
            assigned[j] = true;
            r[j][mulc] = 1;
            mulc++;
        }
        
        return r;
    }

    /**
     * Menyelesaikan SPL menggunakan Eliminasi Gauss-Jordan pada matriks augmented SPL.
     * @param s matriks augmented dari persamaan
     * @return solusi berbentuk array 2D bertipe double
     * @throws RuntimeException ketika ditemukan bahwa SPL tidak mungkin punya solusi
     */
    public static double[][] byRREF(Matrix s, boolean dbg) {
        // inisialisasi
        double[][] r = new double[s.cols-1][s.cols];
        Matrix sp = Matrix.copy(s);
        sp.norm(Matrix.NEPSILON);
        sp.toRREF(dbg);
        sp.norm(Matrix.NEPSILON);
      
        
        // untuk tracking variable apa aja yg udah punya padanan
        boolean[] assigned = new boolean[sp.cols-1];
        int mulc = 1; // ini untuk tracking indeks variabel bebas a_n

        if(dbg) System.out.println("- (Melakukan back-substitution)");
        // buat solusi
        for(int i = sp.rows-1; i >= 0; --i) {
            int[] nzeroloc = new int[sp.cols-1];
            int nzeroc = 0;

            // cari indeks dan jumlah non-zero
            for(int j = 0; j < sp.cols - 1; ++j) {
                if(sp.src[i][j] == 0)
                    continue;
                nzeroloc[nzeroc] = j;
                nzeroc++;
            }

            // cek berdasarkan jumlah variable non-0
            switch(nzeroc) {
                // semua 0
                // kasus paling sederhana i love it
                case 0: {
                    if(sp.src[i][sp.cols-1] != 0)
                        throw new RuntimeException("SPLSolver.byRREF: Sistem tidak punya solusi!");

                    continue;
                }
                
                // cuma 1
                case 1: {
                    // cek apakah variabel ini sudah terikat variabel lain
                    boolean free = true;
                    for(int k = 0; k < sp.cols; ++k)
                        if(r[nzeroloc[0]][k] != 0)
                            free = false;
                    
                    // klo nggak terikat tapi udah keassigned....
                    // brrti ada 2 persamaan dengan nilai variabel yg berbeda...
                    // nggak mungkin!
                    if(assigned[nzeroloc[0]] && free)
                        throw new RuntimeException("SPLSolver.byRREF: Sistem tidak punya solusi!");
                    
                    // yh lgsg assigned saja
                    r[nzeroloc[0]][0] += sp.src[i][sp.cols-1];
                    assigned[nzeroloc[0]] = true;
                    break;
                }

                // ada banyak
                default: {
                    // yg paling kiri jadi pivot
                    // lgsg assigned boss
                    r[nzeroloc[0]][0] += sp.src[i][sp.cols-1];
                    assigned[nzeroloc[0]] = true;

                    // untuk variabel2 yg nggak jadi pivot
                    // bakal pindah ruas!
                    for(int j = nzeroc-1; j >= 1; --j) {
                        // klo variabel itu belum pernah di-assigned apa2
                        if(!assigned[nzeroloc[j]]) {
                            if(dbg) System.out.printf("  - Memasangkan x%d dengan parametrik a%d\n", nzeroloc[j], mulc-1);
                            assigned[nzeroloc[j]] = true;
                            r[nzeroloc[j]][mulc] = 1;
                            mulc++;
                        }

                        // pindah ruas, lalu ekspansi variabel tersebut
                        // misal x1 (pivot) + 4x2 = 2
                        // nanti ini bakal menghitung x1 = 2 - 4a_n, dengan x2 = a_n.
                        double mul = -sp.src[i][nzeroloc[j]];
                        if(dbg) System.out.printf("  - Substitusi x%d\n", nzeroloc[0]);
                        for(int k = 0; k < sp.cols; ++k)
                            r[nzeroloc[0]][k] += r[nzeroloc[j]][k] * mul;
                    }
                }
            }      
        }

        // lah terus klo ada yg belum diassign gimana?
        // kita ikatkan dia dengan variabel lain
        // kan bentuknya a_n. cara tau indeks n?
        // pake mulc. gw nggak pinter kasih nama....
        for(int j = sp.cols-2; j >= 0; --j) {
            if(assigned[j])
                continue;
            if(dbg) System.out.printf("  - Memasangkan x%d dengan parametrik a%d\n", j, mulc-1);
            assigned[j] = true;
            r[j][mulc] = 1;
            mulc++;
        }

        return r;
    }

    /**
     * Menyelesaikan SPL yang memiliki jumlah persamaan dan variabel yang sama menggunakan Kaidah Cramer.
     * @param s matriks augmented dari persamaan
     * @return solusi berbentuk array 2D bertipe double
     * @throws IllegalArgumentException ketika jumlah persamaan tidak sama dengan jumlah variabel
     * @throws RuntimeException ketika matriks persamaan non-augmented utama tidak memiliki determinan
     */
    public static double[][] byCramer(Matrix s, boolean dbg) {
        if(s.cols-1 != s.rows)
            throw new IllegalArgumentException("SPLSolver.byCramer: Jumlah persamaan tidak sama dengan jumlah variabel!");
        
        if(dbg) System.out.println("- (Memisah matriks A dan b dari S=[A|b])");
        // misah matriks A dan b dari matriks sistem s=[A|b]
        Matrix A = new Matrix(s.rows, s.cols-1);
        Matrix b = new Matrix(s.rows, 1);
        for(int i = 0; i < s.rows; ++i) {
            for(int j = 0; j < s.cols; ++j) {
                if(j == s.cols-1) b.src[i][0] = s.src[i][j];
                else A.src[i][j] = s.src[i][j];
            }
        }

        // hitung determinan utama dan cek
        if(dbg) System.out.println("- (Menghitung determinan matriks A)");
        double mdet = Matrix.gdet(A, true);
        if(mdet == 0)
            throw new RuntimeException("SPLSolver.byCramer: Matriks persamaan tidak mempunyai determinan!");

        // cari solusi
        if(dbg) System.out.println("- (Menghitung solusi)");
        double[][] r = new double[A.cols][A.cols+1];
        for(int j = 0; j < A.cols; ++j) {
            // simpen elemen kolom A utama sementara
            double[] t = new double[A.rows];
            for(int i = 0; i < A.rows; ++i) {
                t[i] = A.src[i][j];
                A.src[i][j] = b.src[i][0];
            }
           
            // hitung solusi
            double cdet = Matrix.gdet(A, false);
            if(dbg) System.out.printf("  - Menghitung determinan A%d\n", j);
            if(dbg) System.out.printf("  - Menghitung solusi x%d\n", j);
            r[j][0] = cdet/mdet;

            // kembaliin A utama
            for(int i = 0; i < A.rows; ++i)
                A.src[i][j] = t[i];
        }

        return r;
    }

    /**
     * Menyelesaikan SPL yang memiliki jumlah persamaan dan variabel yang sama menggunakan perkalian inverse, mengikuti {@code x=A^-1*b}.
     * @param s matriks augmented dari persamaan
     * @return solusi berbentuk array 2D bertipe double
     * @throws IllegalArgumentException ketika jumlah persamaan tidak sama dengan jumlah variabel
     */
    public static double[][] byInverse(Matrix s, boolean dbg) {
        if(s.cols-1 != s.rows)
            throw new IllegalArgumentException("SPLSolver.byInverse: Jumlah persamaan tidak sama dengan jumlah variabel!");
        
        if(dbg) System.out.println("- (Memisah matriks A dan b dari S=[A|b])");
        // misah matriks A dan b dari matriks sistem s=[A|b]
        Matrix A = new Matrix(s.rows, s.cols-1);
        Matrix b = new Matrix(s.rows, 1);
        for(int i = 0; i < s.rows; ++i) {
            for(int j = 0; j < s.cols; ++j) {
                if(j == s.cols-1) b.src[i][0] = s.src[i][j];
                else A.src[i][j] = s.src[i][j];
            }
        }

        // hitung inverse, tergantung ordo
        if(dbg) System.out.println("- (Menghitung invers matriks A)");
        Matrix Ap;
        if(A.cols < 6) Ap = Matrix.inv(A, dbg);
        else Ap = Matrix.ginv(A, dbg);

        if(dbg) System.out.println("- (Mengali A^-1 dengan b)");
        // ngitung x=A^-1*b
        Matrix sol = Matrix.mul(Ap, b);
        double[][] r = new double[A.cols][A.cols+1];
        for(int i = 0; i < sol.rows; ++i)
            r[i][0] = sol.src[i][0];

        return r;
    }
}