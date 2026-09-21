package algeo.modules;

public class SPLSolver {
    /**
     * Menyelesaikan SPL menggunakan Eliminasi Gaussian pada matriks augmented SPL.
     * @param s matriks augmented dari persamaan
     * @return solusi berbentuk array 2D bertipe double
     * @throws RuntimeException ketika ditemukan bahwa SPL tidak mungkin punya solusi
     */
    public static double[][] byREF(Matrix s) {
        // inisialisasi
        double[][] r = new double[s.cols-1][s.cols];
        Matrix sp = Matrix.copy(s);
        sp.norm(Matrix.NEPSILON);
        sp.toREF();
        sp.norm(Matrix.NEPSILON);

        // untuk tracking variable apa aja yg udah punya padanan
        boolean[] assigned = new boolean[sp.cols-1];
        int mulc = 1; // ini untuk tracking indeks variabel bebas a_n

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
            assigned[j] = true;
            r[j][mulc] = 1;
            mulc++;
        }
        
        return r;
    }

    public static double[][] byRREF(Matrix s) {
        // inisialisasi
        double[][] r = new double[s.cols-1][s.cols];
        Matrix sp = Matrix.copy(s);
        sp.norm(Matrix.NEPSILON);
        sp.toRREF();
        sp.norm(Matrix.NEPSILON);
      
        // untuk tracking variable apa aja yg udah punya padanan
        boolean[] assigned = new boolean[sp.cols-1];
        int mulc = 1; // ini untuk tracking indeks variabel bebas a_n

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
                    for(int j = nzeroc-1; j >= 1; --j) {
                        // klo variabel itu belum pernah di-assigned apa2
                        if(!assigned[nzeroloc[j]]) {
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
        }

        // lah terus klo ada yg belum diassign gimana?
        // kita ikatkan dia dengan variabel lain
        // kan bentuknya a_n. cara tau indeks n?
        // pake mulc. gw nggak pinter kasih nama....
        for(int j = sp.cols-2; j >= 0; --j) {
            if(assigned[j])
                continue;
            assigned[j] = true;
            r[j][mulc] = 1;
            mulc++;
        }

        return r;
    }
}