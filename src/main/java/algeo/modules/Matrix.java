package algeo.modules;

/**
 * Representasi matriks matematika yang performant dan floating-point safe.
 * Memuat fungsi dasar matriks, operasi baris elementer matriks, dan bentukan eselon. 
 * Berikut adalah daftar fungsi dan metodenya:
 * <ul>
 *  <li>{@link Matrix#print() Matrix.print}</li>
 *  <li>{@link Matrix#norm() Matrix.norm}</li>
 *  <li>{@link Matrix#copy() static Matrix.copy}</li>
 *  <li>{@link Matrix#add(Matrix, Matrix) static Matrix.add}</li>
 *  <li>{@link Matrix#mul(Matrix, Matrix) static Matrix.mul}</li>
 *  <li>{@link Matrix#tr(Matrix) static Matrix.tr}</li>
 *  <li>{@link Matrix#sub(Matrix, int, int) static Matrix.sub (2 param)}</li>
 *  <li>{@link Matrix#sub(Matrix, int, int, int, int) static Matrix.sub (4 param)}</li>
 *  <li>{@link Matrix#det(Matrix) static Matrix.det}</li>
 *  <li>{@link Matrix#cof(Matrix) static Matrix.cof}</li>
 *  <li>{@link Matrix#adj(Matrix) static Matrix.adj}</li>
 *  <li>{@link Matrix#inv(Matrix) static Matrix.inv}</li>
 *  <li>{@link Matrix#idt(int) static Matrix.idt}</li>
 *  <li>{@link Matrix#aug(Matrix, Matrix) static Matrix.aug}</li>
 *  <li>{@link Matrix#rswp(int, int) Matrix.rswp}</li>
 *  <li>{@link Matrix#rmul(int, double) Matrix.rmul}</li>
 *  <li>{@link Matrix#radd(int, int, double) Matrix.radd}</li>
 *  <li>{@link Matrix#toREF() Matrix.toREF}</li>
 *  <li>{@link Matrix#toRREF() Matrix.toRREF}</li>
 *  <li>{@link Matrix#gdet(Matrix) static Matrix.gdet}</li>
 *  <li>{@link Matrix#ginv(Matrix) static Matrix.ginv}</li>
 * </ul>
 * 
 * @author Fachry Azriel Fajdwani (rabsed1)
 * @since 16/09/2026
 */
public class Matrix {
    /** Jumlah baris dalam matriks. */
    final public int rows;

    /** Jumlah kolom dalam matriks. */
    final public int cols;

    /** Menunjukkan apakah matriks sebuah matriks persegi */
    final public boolean square;

    /** Sumber berbentuk array of array bertipe {@code double}. */
    final public double[][] src;

    /** Menyimpan scale untuk partial pivoting */
    public double[] scale;

    /** Menyimpan kunci pembuatan scale */
    public boolean scaleLock;

    /** 
     * Mengonstruksi matriks null (<i>semua elemen bernilai 0</i>) statis berordo {@code rows} x {@code cols}.
     * @param rows jumlah baris matriks
     * @param cols jumlah kolom matriks
     * @return Matriks null berordo {@code rows} x {@code cols}
    */
    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.src = new double[rows][cols];
        
        this.square = rows == cols;
        this.scaleLock = false;
    }

    /** 
     * Mencetak matriks dengan format yang menyesuaikan 
     * nilai elemen di baris-{@code i} kolom-{@code j}.
     */
    public void print() {
        for (int i = 0; i < this.rows; i++) {
            System.out.print("[ ");
            for (int j = 0; j < this.cols; j++) {
                double val = this.src[i][j];
                System.out.printf("%17.15g ", val);
            }
            System.out.println("]");
        }
    }
    
    /**
     * Menormalisasi seluruh elemen matriks, yakni membulatkan angka desimal yang punya galat tertentu.
     * @param e nilai toleransi galats
     */
    public void norm(double e) {
        for(int i = 0; i < this.rows; ++i)
            for(int j = 0; j < this.cols; ++j)
                this.src[i][j] = snorm(this.src[i][j], e);
    }

    /** 
     * Menyalin sebuah matriks.
     * @param m matrik asal
     * @return matriks salinan
    */
    public static Matrix copy(Matrix m) {
        Matrix r = new Matrix(m.rows, m.cols);
        for(int i = 0; i < r.rows; ++i) 
            for(int j = 0; j < r.cols; ++j) 
                r.src[i][j] = m.src[i][j];

        return r;
    }


    /** 
     * Menambahkan dua buah matriks berordo sama.
     * @param m1 matriks pertama
     * @param m2 matriks kedua
     * @return matriks baru penjumlahan kedua matriks
     * @throws IllegalArgumentException ketika ordo kedua matriks tidak sama
    */
    public static Matrix add(Matrix m1, Matrix m2) {
        if(m1.rows != m2.rows || m1.cols != m2.cols)
            throw new IllegalArgumentException("Matrix.add: ordo matriks tidak sama!");
        
        Matrix r = new Matrix(m1.rows, m1.cols);
        for(int i = 0; i < m1.rows; ++i) {
            for(int j = 0; j < m2.cols; ++j) {
                double val1 = m1.src[i][j];
                double val2 = m2.src[i][j];
                r.src[i][j] = val1 + val2;
            }
        }

        return r;
    }

    /** 
     * Mengalikan dua buah matriks berordo {@code m}x{@code n} dan {@code n}x{@code l}.
     * @param m1 matriks pertama berordo {@code m}x{@code n}
     * @param m2 matriks kedua berordo {@code n}x{@code l}
     * @return matriks baru perkalian kedua matriks
     * @throws IllegalArgumentException ketika jumlah kolom {@code m1} tidak sama dengan jumlah baris {@code m2} 
    */
    public static Matrix mul(Matrix m1, Matrix m2) {
        if(m1.cols != m2.rows)
            throw new IllegalArgumentException("Matrix.mul: baris matriks tidak sama dengan kolom matriks");
    
        Matrix r = new Matrix(m1.rows, m2.cols);
        int kMax = m1.cols;
        for(int i = 0; i < m1.rows; ++i) {
            for(int k = 0; k < kMax; ++k) {
                double val1 = m1.src[i][k];
                for(int j = 0; j < m2.cols; ++j) {
                    double val2 = m2.src[k][j];
                    r.src[i][j] += val1 * val2;
                }
            }
        }

        return r;
    }
    
    /** 
     * Mentranspose sebuah matriks, yakni mengubah setiap kolom menjadi baris dan sebaliknya.
     * @param m matriks sumber
     * @return matriks sumber yang telah di-transpose
    */
    public static Matrix tr(Matrix m) {
        Matrix r = new Matrix(m.cols, m.rows);
        for(int i = 0; i < m.rows; ++i)
            for(int j = 0; j < m.cols; ++j)
                r.src[j][i] = m.src[i][j];

        return r;
    }

    /** 
     * Mengambil upamatriks "kofaktor" ke-{@code in,jn}, yakni submatriks dari m tanpa baris ke-{@code in} dan kolom ke-{@code jn}.
     * @param m matriks sumber
     * @param in baris yang ingin dihapus
     * @param jn kolom yang ingin dihapus
     * @return upamatriks baru, yakni matriks sumber tanpa baris ke-{@code in} dan kolom ke-{@code jn}
     * @throws IllegalArgumentException ketika {@code in} tidak berada dalam {@code (0, m.rows]}
     * @throws IllegalArgumentException ketika {@code jn} tidak berada dalam {@code (0, m.cols]}
    */
    public static Matrix sub(Matrix m, int in, int jn) {
        if(in < 0 || in >= m.rows)
            throw new IllegalArgumentException("Matrix.sub: nilai i-n tidak valid!");
        if(jn < 0 || jn >= m.cols )
            throw new IllegalArgumentException("Matrix.sub: nilai j-n tidak valid!");

        Matrix r = new Matrix(m.rows - 1, m.cols - 1);
        int sr = 0, sc = 0;
        for(int i = 0; i < m.rows; ++i) {
            if(i == in) { 
                sr = 1;
                continue;
            }
        
            for(int j = 0; j < m.cols; ++j) {
                if(j == jn) sc = 1;
                else r.src[i-sr][j-sc] = m.src[i][j];
            }

            sc = 0;
        }

        return r;
    }

    /** 
     * Mengambil upamatriks dari {@code (i0, j0)} sampai {@code (ip, jp)}.
     * @param m matriks sumber
     * @param i0 baris awal
     * @param j0 kolom awal
     * @param ip baris akhir
     * @param jp kolom akhir
     * @return upamatriks baru, potongan matriks dari {@code (i0, j0)} sampai {@code (ip, jp)}}
     * @throws IllegalArgumentException ketika {@code i0} atau {@code ip} tidak berada dalam {@code (0, m.rows]}
     * @throws IllegalArgumentException ketika {@code j0} atau {@code jp} tidak berada dalam {@code (0, m.cols]}
     * @throws IllegalArgumentException ketika {@code i0 > ip}
     * @throws IllegalArgumentException ketika {@code j0 > jp}
    */
    public static Matrix sub(Matrix m, int i0, int j0, int ip, int jp) {
        if(i0 < 0 || i0 >= m.rows || ip < 0 || ip >= m.rows)
            throw new IllegalArgumentException("Matrix.sub: nilai i-nol atau i-prime tidak valid!");
        if(j0 < 0 || j0 >= m.cols || jp < 0 || jp >= m.cols)
            throw new IllegalArgumentException("Matrix.sub: nilai j-nol atau j-prime tidak valid!");

        if(ip < i0)
            throw new IllegalArgumentException("Matrix.sub: nilai i-prime kurang dari i-nol!");
        if(jp < j0)
            throw new IllegalArgumentException("Matrix.sub: nilai j-prime kurang dari j-nol!");

        Matrix r = new Matrix(ip - i0 + 1, jp - j0 + 1);
        for(int i = i0; i <= ip; ++i)
            for(int j = j0; j <= jp; ++j)
                r.src[i-i0][j-j0] = m.src[i][j];
        
        return r;
    }

    /** 
     * Menghitung determinan matriks persegi.
     * @param m matriks sumber
     * @return determinan matriks sumber
     * @throws IllegalArgumentException ketika matriks sumber bukan matriks persegi
    */
    public static double det(Matrix m, boolean dbg) {
        if(!m.square)
            throw new IllegalArgumentException("Matrix.det: matriks yang diberikan bukanlah matriks persegi!");

        // basis
        switch (m.rows) {
            case 0: return 1;
            case 1: return m.src[0][0];
            case 2: return m.src[0][0]*m.src[1][1] - m.src[0][1]*m.src[1][0];
        }
        
        // mencari baris atau kolom dengan 0 terbanyak
        int maxZeroR = 0, locR = 0;
        int maxZeroC = 0, locC = 0;
        for(int i = 0; i < m.rows; ++i) {
            int zeros = 0;
            for(int j = 0; j < m.cols; ++j)
                zeros += (m.src[i][j] == 0) ? 1 : 0;

            if(zeros > maxZeroR) {
                maxZeroR = zeros;
                locR = i;
            }
        }

        for(int i = 0; i < m.rows; ++i) {
            int zeros = 0;
            for(int j = 0; j < m.cols; ++j)
                zeros += (m.src[j][i] == 0) ? 1 : 0;

            if(zeros > maxZeroC) {
                maxZeroC = zeros;
                locC = i;
            }
        }

        // lakukan perhitungan det
        if(dbg) System.out.println("- (Menghitung determinan)");
        double val = 0;
        if(maxZeroR >= maxZeroC) {
            // jika 0 terbanyak ada secara baris
            if(dbg) System.out.printf("  - Memilih baris ke-%d\n", locR);
            for(int j = 0; j < m.cols; ++j) {
                if(m.src[locR][j] == 0)
                    continue;
                
                Matrix sub = Matrix.sub(m, locR, j);
                double c = m.src[locR][j];
                double cf = (locR+j) % 2 == 0 ? 1 : -1;
                double idet = Matrix.det(sub, false);
                val += idet * c * cf;
                if(dbg) System.out.printf("  - Mengali %f dengan determinan C{%d,%d} = %f\n", c, locR, j, idet);
            }
        } else {
            // jika 0 terbanyak ada secara kolom
            if(dbg) System.out.printf("  - Memilih kolom ke-%d\n", locC);
            for(int i = 0; i < m.rows; ++i) {
                if(m.src[i][locC] == 0)
                    continue;
                
                Matrix sub = Matrix.sub(m, i, locC);
                double c = m.src[i][locC];
                double cf = (i+locC) % 2 == 0 ? 1 : -1;
                double idet = Matrix.det(sub, false);
                val += idet * c * cf;
                if(dbg) System.out.printf("  - Mengali %f dengan determinan C{%d,%d} = %f\n", c, i, locC, idet);
            }
        }
       
        return val;
    }

    /** 
     * Mengonstruksi matriks kofaktor.
     * @param m matriks sumber
     * @return matriks kofaktor dari matriks sumber
     * @throws IllegalArgumentException ketika matriks sumber bukan matriks persegi
    */
    public static Matrix cof(Matrix m, boolean dbg) {
        if(!m.square)
            throw new IllegalArgumentException("Matrix.cof: matriks yang diberikan bukanlah matriks persegi!");

        if(dbg) System.out.println("- (Membentuk matriks kofaktor)");
        Matrix r = new Matrix(m.rows, m.cols);
        for(int i = 0; i < m.rows; ++i) {
            for(int j = 0; j < m.cols; ++j) {
                if(dbg) System.out.printf("  - Membuat C{%d,%d}\n", i, j);
                Matrix sub = Matrix.sub(m, i, j);
                double cf = (i+j) % 2 == 0 ? 1 : -1;
                r.src[i][j] = Matrix.det(sub, false) * cf;
            }
        }

        return r;
    }

    /** 
     * Mengonstruksi matriks adjoint.
     * @param m matriks sumber
     * @return matriks adjoint dari matriks sumber
     * @throws IllegalArgumentException ketika matriks sumber bukan matriks persegi
    */
    public static Matrix adj(Matrix m, boolean dbg) {
        if(!m.square)
            throw new IllegalArgumentException("Matrix.adj: matriks yang diberikan bukanlah matriks persegi!");

        Matrix r = Matrix.tr(Matrix.cof(m, dbg));
        System.out.println("- (Membentuk matriks adjoin)");
        return r;
    }

    /** 
     * Mengonstruksi matriks invers.
     * @param m matriks sumber
     * @return matriks invers dari matriks sumber
     * @throws IllegalArgumentException ketika matriks sumber bukan matriks persegi
     * @throws IllegalArgumentException ketika determinan matriks persegi bernilai 0
    */
    public static Matrix inv(Matrix m, boolean dbg) {
        if(!m.square)
            throw new IllegalArgumentException("Matrix.inv: matriks yang diberikan bukanlah matriks persegi!");
        
        double det = Matrix.det(m, dbg);
        if(det == 0)
            throw new IllegalArgumentException("Matrix.inv: determinan matriks bernilai 0");
        
        Matrix r = Matrix.adj(m, dbg);
        for(int i = 0; i < m.rows; ++i)
            for(int j = 0; j < m.cols; ++j)
                r.src[i][j] = 1/det * r.src[i][j];
        if(dbg) System.out.println("- (Membentuk matriks invers)");
        
        return r;
    }

    /**
     * Membentuk matriks identitas berordo {@code n} x {@code n}
     * @param n ordo matriks
     * @return matriks identitas
     */
    public static Matrix idt(int n) {
        Matrix r = new Matrix(n, n);
        for(int k = 0; k < n; ++k)
            r.src[k][k] = 1;
        
        return r;
    }

    /**
     * Membentuk matrix augmented dari 2 matriks.
     * @param m1 matriks pertama
     * @param m2 matriks kedua
     * @return matriks augmented berbentuk {@code [m1|m2]}
     * @throws IllegalArgumentException jumlah baris kedua matriks tidak sama
     */
    public static Matrix aug(Matrix m1, Matrix m2) {
        if(m1.rows != m2.rows)
            throw new IllegalArgumentException("Matrix.aug: jumlah baris kedua matriks tidak sama!");

        Matrix r = new Matrix(m1.rows, m1.cols + m2.cols);
        for(int i = 0; i < r.rows; ++i) {
            for(int j = 0; j < r.cols; ++j) {
                if(j < m1.cols) r.src[i][j] = m1.src[i][j];
                else r.src[i][j] = m2.src[i][j - m1.cols];
            }
        }
        
        return r;
    }

    /** 
     * Menukar posisi dua baris dalam matriks.
     * @param r1 baris pertama
     * @param r2 baris kedua
     * @throws IllegalArgumentException ketika {@code r1} atau {@code r2} tidak berada dalam {@code (0, rows]}
     */
    public void rswp(int r1, int r2) {
        if(r1 < 0 || r1 >= this.rows || r2 < 0 || r2 >= this.rows)
            throw new IllegalArgumentException("MatrixInstance.rswp: nilai r1 atau r2 tidak valid!");

        for(int j = 0; j < this.cols; ++j) {
            double buf = this.src[r1][j];
            this.src[r1][j] = this.src[r2][j];
            this.src[r2][j] = buf;
        }
    }

    /** 
     * Mengali suatu baris dalam matriks dengan konstanta non-0.
     * @param r baris sumber
     * @param c konstanta pengali non-0
     * @throws IllegalArgumentException ketika {@code r} tidak berada dalam {@code (0, rows]}
     * @throws IllegalArgumentException ketika konstanta bernilai 0
     */
    public void rmul(int r, double c) {
        if(r < 0 || r >= this.rows)
            throw new IllegalArgumentException("MatrixInstance.rmul: nilai r tidak valid!");
        if(c == 0)
            throw new IllegalArgumentException("MatrixInstance.rmul: konstanta bernilai 0!");

        for(int j = 0; j < this.cols; ++j) {
            double val = c * this.src[r][j];
            this.src[r][j] = val;
        }
    }

    /** 
     * Menambah baris sumber dengan kelipatan baris lain dalam matriks.
     * @param rs baris sumber
     * @param rm baris modifier
     * @throws IllegalArgumentException ketika {@code rs} atau {@code rm} tidak berada dalam {@code (0, rows]}
     * @throws IllegalArgumentException ketika {@code rs = rm}
     */
    public void radd(int rs, int rm, double c) {
        if(rs < 0 || rs >= this.rows || rm < 0 || rm >= this.rows)
            throw new IllegalArgumentException("MatrixInstance.radd: nilai r-source atau r-modifier tidak valid!");
        if(rs == rm)
            throw new IllegalArgumentException("MatrixInstance.radd: nilai r-source dan r-modifier sama!");
        if(c == 0)
            return;

        for(int j = 0; j < this.cols; ++j) {
            // double val = this.src[rs][j] + c * this.src[rm][j];
            double val = Math.fma(c, this.src[rm][j], this.src[rs][j]);
            this.src[rs][j] = val;
        }
    }

    /** 
     * Memodifikasi matriks menjadi Matriks Eselon Baris (MEB).
     * @return jumlah terjadinya pertukaran baris
     */
    public int toREF(boolean dbg) {
        int swapc = 0;

        // data nilai absolut terbesar per baris
        if(dbg) System.out.println("- (Mengubah matriks menjadi Matriks Baris Eselon)");
        if(!scaleLock) {
            scale = new double[this.rows];
            for(int i = 0; i < this.rows; ++i) {
                for(int j = 0; j < this.cols; ++j) {
                    double aval = Math.abs(this.src[i][j]);
                    if(aval > scale[i])
                        scale[i] = aval;
                }
            }

            scaleLock = true;
        }  

        // fase eliminasi
        int j = 0;
        for(int i = 0; i < this.rows - 1; ++i) {
            if(i >= this.cols)
                break;

            // melakukan partial pivoting dulu
            for(; j < this.cols; ++j) {
                // mencari pivot yang lebih baik
                double bestr = 0;
                int locr = i;
                for(int ip = i; ip < this.rows; ++ip) {
                    double r = scale[ip] == 0 ? 0 : Math.abs(this.src[ip][j]) / scale[ip];
                    if(r > bestr) {
                        bestr = r;
                        locr = ip;
                    } else if (swithin(r - bestr, CEPSILON)) {
                        if(Math.abs(this.src[ip][j]) > Math.abs(this.src[locr][j])) {
                            bestr = r;
                            locr = ip;
                        }
                    }
                }

                // jika ada yang lebih baik, tukar
                if(locr > i) {
                    if(dbg) System.out.printf("  - Menukar baris %d dan %d\n", i, locr);
                    this.rswp(i, locr);
                    double buf = scale[locr];
                    scale[locr] = scale[i];
                    scale[i] = buf;
                    swapc++;

                    // jika setelah ditukar, masih bernilai sangat kecil
                    // jadikan dia 0, dan geser ke kolom sebelah
                    if(swithin(this.src[i][j], CEPSILON * scale[i])) {
                        this.src[i][j] = 0;
                        continue;
                    }

                    break;
                }

                // jika tidak terjadi apa-apa, henti
                if(locr == i && !swithin(this.src[i][j], CEPSILON * scale[i]))      
                    break;
                
                // stop klo ada baris yg semua 0
                // karena klo di baris itu semua 0, maka baris2 di bawahnya juga semua 0
                if(j == this.cols-1)
                    return swapc;
            }

            if(j == this.cols)
                return swapc;

            // membentuk 0 semua di bawah [i][j]
            for(int ip = i+1; ip < this.rows; ++ip) {
                double cz = -this.src[ip][j] / this.src[i][j];
                if(dbg) System.out.printf("  - Menjumlahkan baris %d dengan baris %d yang telah dikali %f\n", ip, i, cz);
                this.radd(ip, i, cz);
                this.src[ip][j] = 0;
            }
        }

        // if(norm) {
        //     for(int i = 0; i < this.rows; ++i) {
        //         double e = this.scale[i] * NEPSILON;
        //         for(int jz = 0; jz < this.cols; ++jz)
        //             this.src[i][jz] = snorm(this.src[i][jz], e);
        //     }
        // }

        return swapc;
    }

    /** 
     * Memodifikasi matriks menjadi Matriks Eselon Baris Tereduksi (MEBR).
    */
    public void toRREF(boolean dbg) {
        // fase maju
        if(!this.scaleLock)
            this.toREF(dbg);
        
        if(dbg) System.out.println("- (Mengubah matriks menjadi Matriks Baris Eselon Tereduksi)");
        // fase mundur dan pembentukan 1-utama
        for(int i = 0; i < this.rows; ++i) {
            // mencari angka non-0 paling kiri
            int j = 0;
            for(; j < this.cols; ++j) {
                if(!swithin(this.src[i][j], CEPSILON * scale[i]))
                    break;
                if(j == this.cols-1)
                    return;
            }

            // membentuk 1-utama
            double c = 1/this.src[i][j];
            if(dbg) System.out.printf("  - Mengali baris %d dengan %f (membentuk 1-utama)\n", i, c);
            this.rmul(i, c);
            
            // membentuk 0 semua di atas [i][j]
            for(int ip = i-1; ip >= 0; --ip) {
                double cb = -this.src[ip][j];
                if(dbg) System.out.printf("  - Menjumlahkan baris %d dengan baris %d yang telah dikali %f\n", ip, i, cb);
                this.radd(ip, i, cb);
                this.src[ip][j] = 0;
            }
        }
    }

    /** 
     * Menghitung determinan matriks persegi dengan Eliminasi Gaussian.
     * @param m matriks sumber
     * @return determinan matriks sumber
     * @throws IllegalArgumentException ketika matriks sumber bukan matriks persegi
    */
    public static double gdet(Matrix m, boolean dbg) {
        if(!m.square)
            throw new IllegalArgumentException("Matrix.gdet: matriks yang diberikan bukanlah matriks persegi!");

        // biar mempermudah hidup
        switch (m.rows) {
            case 0: return 1;
            case 1: return m.src[0][0];
            case 2: return m.src[0][0]*m.src[1][1] - m.src[0][1]*m.src[1][0];
        }

        // perhitungan berat ya guys ya
        // ini ngubah jadi segitiga, lalu normalisasi
        Matrix r = Matrix.copy(m);
        int swapc = r.toREF(dbg);
        for(int i = 0; i < r.rows; ++i)
            for(int j = 0; j < r.cols; ++j)
                r.src[i][j] = snorm(r.src[i][j], r.scale[i] * NEPSILON);
        
        if(dbg) System.out.println("- (Menghitung determinan dengan mengali seluruh diagonal)");
        // ini ngitung determinan matriks segitiga tadi
        double val = 1;
        for(int k = 0; k < m.rows; ++k)
            val *= r.src[k][k];

        return val * (swapc % 2 == 0 ? 1 : -1);
    }

    /** 
     * Mengonstruksi matriks invers dengan Eliminasi Gauss-Jordan pada matriks augmented.
     * @param m matriks sumber
     * @return matriks invers dari matriks sumber
     * @throws IllegalArgumentException ketika matriks sumber bukan matriks persegi
     * @throws IllegalArgumentException ketika matriks augmented tidak berbentuk {@code [I|A^-1]}
    */
    public static Matrix ginv(Matrix m, boolean dbg) {
        if(!m.square)
            throw new IllegalArgumentException("Matrix.ginv: matriks yang diberikan bukanlah matriks persegi!");

        // Aish tambahin cek invers di awal
        double det = Matrix.gdet(m, false);
        if(swithin(det, 1e-9))
            throw new RuntimeException("Matrix.ginv: Matriks tidak punya invers!");

        // Bikin matriks augmented
        // Bagian kanan matriksnya, bagian kiri matriks identitas seukuran
        if(dbg) System.out.println("- (Membentuk matriks [A|I])");
        Matrix idt = Matrix.idt(m.rows);
        Matrix aug = Matrix.aug(m, idt);

        // Bentuk RREF, lalu saring bagian kanannya.
        aug.toRREF(dbg);
        Matrix r = new Matrix(m.rows, m.cols);
        for(int i = 0; i < r.rows; ++i)
            for(int j = 0; j < r.cols; ++j)
                r.src[i][j] = aug.src[i][j+r.cols];

        // cek apakah matriks identitas terbentuk di kiri
        // kalau nggak ada, brrti matriks nggak punya invers
        for(int k = 0; k < r.rows; ++k)
            if(!swithin(aug.src[k][k] - 1, CEPSILON))
                throw new RuntimeException("Matrix.ginv: Matriks tidak punya invers!");

        if(dbg) System.out.println("- (Mengambil matriks A^-1 dari [I|A^-1])");

        return r;
    }
    
    /* ========= PROPERTI DAN FUNGSI PEMBANTU =========== */
    // untuk operasi yang berhubungan dengan tipe data double
    
    /** Toleransi galat untuk komputasi. */
    public final static double CEPSILON = 1e-12;

    /** Toleransi galat untuk normalisasi. */
    public final static double NEPSILON = 1e-12;

    /** 
     * Menghitung apakah nilai absolut {@code n} kurang dari atau sama dengan {@code r}
     * @param n terbanding
     * @param r pembanding
     * @return boolean
     */
    public static boolean swithin(double n, double e) {
        return Math.abs(n) <= e;
    }

    /**
     * Menormalisasi angka sangat kecil menjadi 0
     * @param n angka masukan
     * @return angka keluaran
     */
    public static double snorm(double n, double e) {
        double rn = Math.round(n);
        return swithin(n - rn, e) ? rn : n;
    }
}