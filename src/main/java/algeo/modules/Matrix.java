package algeo.modules;

public class Matrix {
    final static double EPSILON = 1e-9;

    /**
     * === METODE DAN PROPERTI DASAR MATRIKS ===
     * MatrixInstance.rows: Memuat jumlah baris matriks
     * MatrixInstance.cols: Memuat jumlah kolom matriks
     * MatrixInstance.src: Tempat elemen disimpan, bertipe "double"
     * Matrix.Matrix(): Konstuktor
     * MatrixInstance.print(): Mencetak matriks secara rapih dengan padding
     */
    
    /** MatrixInstance.rows */
    public int rows;
    /** MatrixInstance.columnss */
    public int cols;
    /** MatrixInstance.source */
    public double[][] src;

    /** Konstruktor */
    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.src = new double[rows][cols];
    }

    /** MatrixInstance.print */
    public void print() {
        for (int i = 0; i < this.rows; i++) {
            System.out.print("[ ");
            for (int j = 0; j < this.cols; j++) {
                double val = this.src[i][j];
                System.out.printf("%8.3f ", val);
            }
            System.out.println("]");
        }
    }

    /** 
     * === FUNGSI DASAR MATRIKS ===
     * Matrix.add(): Menambahkan 2 buah matriks
     * Matrix.mul(): Mengalikan 2 buah matriks
     * Matrix.tr(): Mentranspose matriks, yakni menukar setiap baris menjadi kolom dan sebaliknya
     * Matrix.sb(): 
        - 2 parameter: Membuat upamatriks dari m tanpa kolom in, dan jn
        - 4 parameter: Membuat upamatriks dari m, dari titik (i0,j0) sampai (ip,jp)
     * Matrix.det(): Mencari determinan matriks persegi dengan ekspansi kofaktor
     * Matrix.cof(): Mengonstruksi matriks kofaktor suatu matriks
     * Matrix.adj(): Mengonstruksi matriks adjoint suatu matriks, yakni transpose dari matriks kofaktornya
     * Matrix.inv(): Mengonstruksi matriks inverse dengan perhitungan determinan ekspansi kofaktor
    */

    /** Matrix.add */
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

    /** Matrix.multiply */
    public static Matrix mul(Matrix m1, Matrix m2) {
        if(m1.cols != m2.rows)
            throw new IllegalArgumentException("Matrix.mul: baris matriks tidak sama dengan kolom matriks");
    
        Matrix r = new Matrix(m1.rows, m2.cols);
        int kMax = m1.cols;
        for(int i = 0; i < m1.rows; ++i) {
            for(int j = 0; j < m2.cols; ++j) {
                double total = 0;
                for(int k = 0; k < kMax; ++k) {
                    double val1 = m1.src[i][k];
                    double val2 = m2.src[k][j];
                    total += val1 * val2;
                }

                r.src[i][j] = total;
            }
        }

        return r;
    }
    
    /** Matrix.transpose */
    public static Matrix tr(Matrix m) {
        Matrix r = new Matrix(m.cols, m.rows);
        for(int i = 0; i < m.rows; ++i)
            for(int j = 0; j < m.cols; ++j)
                r.src[j][i] = m.src[i][j];

        return r;
    }

    /** Matrix.submatrix */
    public static Matrix sb(Matrix m, int in, int jn) {
        if(in < 0 || in >= m.rows)
            throw new IllegalArgumentException("Matrix.sb: nilai i-n tidak valid!");
        if(jn < 0 || jn >= m.cols )
            throw new IllegalArgumentException("Matrix.sb: nilai j-n tidak valid!");

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

    /** Matrix.submatrix */
    public static Matrix sb(Matrix m, int i0, int j0, int ip, int jp) {
        if(i0 < 0 || i0 >= m.rows || ip < 0 || ip >= m.rows)
            throw new IllegalArgumentException("Matrix.sb: nilai i-nol atau i-prime tidak valid!");
        if(j0 < 0 || j0 >= m.cols || jp < 0 || jp >= m.cols)
            throw new IllegalArgumentException("Matrix.sb: nilai j-nol atau j-prime tidak valid!");

        if(ip < i0)
            throw new IllegalArgumentException("Matrix.sb: nilai i-prime kurang dari i-nol!");
        if(jp < j0)
            throw new IllegalArgumentException("Matrix.sb: nilai j-prime kurang dari j-nol!");

        Matrix r = new Matrix(ip - i0 + 1, jp - j0 + 1);
        for(int i = i0; i <= ip; ++i)
            for(int j = j0; j <= jp; ++j)
                r.src[i-i0][j-j0] = m.src[i][j];
        
        return r;
    }

    /** Matrix.determinant */
    public static double det(Matrix m) {
        if(m.rows != m.cols)
            throw new IllegalArgumentException("Matrix.det: matriks yang diberikan bukanlah matriks persegi!");

        // basis rekursi (nggak juga sih)
        // basis yang asli ketika ordonya bernilai 2x2
        switch (m.rows) {
            case 0: return 1;
            case 1: return m.src[0][0];
            case 2: return m.src[0][0]*m.src[1][1] - m.src[0][1]*m.src[1][0];
        }
        
        // mencari 
        int maxZeroR = 0, locR = 0;
        int maxZeroC = 0, locC = 0;
        // cari baris dengan elemen 0 terbanyak
        for(int i = 0; i < m.rows; ++i) {
            int zeros = 0;
            for(int j = 0; j < m.cols; ++j)
                zeros += (m.src[i][j] == 0) ? 1 : 0;

            if(zeros > maxZeroR) {
                maxZeroR = zeros;
                locR = i;
            }
        }

        // cari kolom dengan 0 terbanyak
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
        double val = 0;
        // jika 0 terbanyak ada secara baris,
        // atau 0 terbanyak secara baris dan secara kolom sama
        if(maxZeroR >= maxZeroC) {
            for(int j = 0; j < m.cols; ++j) {
                if(m.src[locR][j] == 0)
                    continue;
                
                Matrix sub = Matrix.sb(m, locR, j);
                double c = m.src[locR][j];
                double cf = (locR+j) % 2 == 0 ? 1 : -1;
                val += Matrix.det(sub) * c * cf;
            }
        // jika 0 terbanyak ada secara kolom
        } else {
            for(int i = 0; i < m.rows; ++i) {
                if(m.src[i][locC] == 0)
                    continue;
                
                Matrix sub = Matrix.sb(m, i, locC);
                double c = m.src[i][locC];
                double cf = (i+locC) % 2 == 0 ? 1 : -1;
                val += Matrix.det(sub) * c * cf;
            }
        }
       
        return val;
    }

    /** Matrix.cofactor */
    public static Matrix cof(Matrix m) {
        if(m.rows != m.cols)
            throw new IllegalArgumentException("Matrix.cof: matriks yang diberikan bukanlah matriks persegi!");

        Matrix r = new Matrix(m.rows, m.cols);
        for(int i = 0; i < m.rows; ++i) {
            for(int j = 0; j < m.cols; ++j) {
                Matrix sub = Matrix.sb(m, i, j);
                double cf = (i+j) % 2 == 0 ? 1 : -1;
                r.src[i][j] = Matrix.det(sub) * cf;
            }
        }

        return r;
    }

    /** Matrix.adjoint */
    public static Matrix adj(Matrix m) {
        return Matrix.tr(Matrix.cof(m));
    }

    /** Matrix.inverse */
    public static Matrix inv(Matrix m) {
        if(m.rows != m.cols)
            throw new IllegalArgumentException("Matrix.inv: matriks yang diberikan bukanlah matriks persegi!");
        double det = Matrix.det(m);
        if(det == 0)
            throw new IllegalArgumentException("Matrix.inv: determinan matriks bernilai 0");
        
        Matrix r = Matrix.adj(m);
        for(int i = 0; i < m.rows; ++i)
            for(int j = 0; j < m.cols; ++j)
                r.src[i][j] = 1/det * r.src[i][j];
        
        return r;
    }

    /**
     * === METODE OPERASI BARIS ELEMENTER (OBE) Matriks ===
     * MatrixInstance.swapR(): menukar posisi 2 buah baris
     * MatrixInstance.mulR(): mengali sebuah baris dengan sebuah konstanta tidak 0
     * MatrixInstance.addR(): menambah suatu baris dengan kelipatan baris lainnya
     */

    /** MatrixInstance.swapRow */
    public void swapR(int r1, int r2) {
        if(r1 < 0 || r1 >= this.rows || r2 < 0 || r2 >= this.rows)
            throw new IllegalArgumentException("MatrixInstance.swapR: nilai r1 atau r2 tidak valid!");

        for(int j = 0; j < this.cols; ++j) {
            double buf = this.src[r1][j];
            this.src[r1][j] = this.src[r2][j];
            this.src[r2][j] = buf;
        }
    }

    /** MatrixInstance.multiplyRow */
    public void mulR(int r, double c) {
        if(r < 0 || r >= this.rows)
            throw new IllegalArgumentException("MatrixInstance.mulR: nilai r tidak valid!");
        if(Math.abs(c) < Matrix.EPSILON)
            throw new IllegalArgumentException("MatrixInstance.mulR: konstanta bernilai 0 atau mendekati 0!");

        for(int j = 0; j < this.cols; ++j) {
            double val = c * this.src[r][j];
            if(Math.abs(val) < Matrix.EPSILON)
                this.src[r][j] = 0;
            else
                this.src[r][j] = val;
        }
    }

    /** MatrixInstance.addRow */
    public void addR(int rs, int rm, double c) {
        if(rs < 0 || rs >= this.rows || rm < 0 || rm >= this.rows)
            throw new IllegalArgumentException("MatrixInstance.addR: nilai r-source atau r-multiplier tidak valid!");
        if(rs == rm)
            throw new IllegalArgumentException("MatrixInstance.addR: nilai r-source dan r-multiplier sama!");

        if(Math.abs(c) < Matrix.EPSILON)
            return;

        for(int j = 0; j < this.cols; ++j) {
            double val = this.src[rs][j] + c * this.src[rm][j];
            if(Math.abs(val) < Matrix.EPSILON)
                this.src[rs][j] = 0;
            else
                this.src[rs][j] = val;
        }
    }

    /**
     * === METODE PEMBENTUKAN MATRIKS BARIS ESELON (MBE) dan tereduksinya (MBER) ===
     * static Matrix.toREF(): Membentuk Matrix Baris Eselon (MBE)
     * static Matrix.toRREF(): Membentuk Matriks Baris Eselon Reduksi (MBER)
     */

    /** MatrixInstance.toRowEchelonForm */
    public int toREF() {
        int swapCount = 0;
        for(int i = 0; i < this.rows; ++i) {
            // kalau matriks punya rows > cols
            // stop iterasi di saat i >= cols
            if(i >= this.cols)
                break;

            // melakukan partial pivoting di non-0 pertama
            // cari nilai absolut terbesar
            int j = 0;
            for(; j < this.cols; ++j) {
                double bestVal = 0;
                int locVal = i;
                for(int ip = i; ip < this.rows; ++ip) {
                    double val = Math.abs(this.src[ip][j]);
                    // if val 
                    if(val > bestVal) {
                        bestVal = val;
                        locVal = ip;
                    }
                }

                // jika ada yang lebih besar, tukar-OBE dan henti
                if(locVal != i) {
                    this.swapR(i, locVal);
                    swapCount++;
                    break;
                }

                // jika tidak terjadi apa-apa, henti
                if(locVal == i && this.src[i][j] != 0)
                    break;

                if(j == this.cols-1)
                    return swapCount;
            }

            // membentuk 0 semua di bawah [i][j] dengan membentuk partial pivoting
            for(int ip = i+1; ip < this.rows; ++ip) {
                double c = -this.src[ip][j] / this.src[i][j];
                this.addR(ip, i, c);
            }
        }

        return swapCount;
    }

    /** MatrixInstance.toReducedRowEchelonForm */
    public void toRREF() {
        // fase maju
        this.toREF();

        // fase mundur dan pembentukan 1-utama
        for(int i = 0; i < this.rows; ++i) {
            // mencari angka non-0 paling kiri
            int j = 0;
            for(; j < this.cols; ++j) {
                if(this.src[i][j] != 0)
                    break;
                if(j == this.cols-1)
                    return;
            }

            // membentuk 1-utama
            double c = 1/this.src[i][j];
            this.mulR(i, c);

            // membentuk 0 semua di atas [i][j] dengan membentuk partial pivoting
            for(int ip = i-1; ip >= 0; --ip) {
                double cb = -this.src[ip][j];
                this.addR(ip, i, cb);
            }
        }
    }

    /**
     * === FUNGSI DETERMINAN DAN INVERS MATRIKS DENGAN OBE ===
     * static Matrix.detERO(): Menghitung determinan matriks persegi dengan OBE
     * static Matrix.invERO(): Mengonstruksi invers matriks persegi dengan OBE
     */
    
    /** Matrix.determinantWithElementaryRowOperations*/
    public static double detERO(Matrix m) {
        if(m.rows != m.cols)
            throw new IllegalArgumentException("Matrix.detERO: matriks yang diberikan bukanlah matriks persegi!");

        // biar mempermudah hidup
        switch (m.rows) {
            case 0: return 1;
            case 1: return m.src[0][0];
            case 2: return m.src[0][0]*m.src[1][1] - m.src[0][1]*m.src[1][0];
        }

        // perhitungan berat ya guys ya
        // basically: ngubah m jadi matriks segitiga pake OBE
        // ini nge-copy
        Matrix r = new Matrix(m.rows, m.cols);
        for(int i = 0; i < r.rows; ++i)
            for(int j = 0; j < r.cols; ++j)
                r.src[i][j] = m.src[i][j];
        
        // ini ngubah jadi segitiga, lalu hitung determinannya
        int swapCount = r.toREF();
        double val = 1;
        for(int k = 0; k < m.rows; ++k)
            val *= r.src[k][k];

        return val * (swapCount % 2 == 0 ? 1 : -1);
    }

    /** Matrix.inverseWithElementaryRowOperations */
    public static Matrix invERO(Matrix m) {
        if(m.rows != m.cols)
            throw new IllegalArgumentException("Matrix.invERO: matriks yang diberikan bukanlah matriks persegi!");

        // Bikin matriks augmented
        // Bagian kanan matriksnya, bagian kiri matriks identitas seukuran
        Matrix aug = new Matrix(m.rows, m.cols*2);
        for(int i = 0; i < aug.rows; ++i) {
            for(int j = 0; j < aug.cols; ++j) {
                if(i < m.rows && j < m.cols)
                    aug.src[i][j] = m.src[i][j];
                else if(i == j - m.cols)
                    aug.src[i][j] = 1;
                else
                    aug.src[i][j] = 0;
            }
        }

        // Lakukan RREF, lalu saring bagian kanannya
        aug.toRREF();
        Matrix r = new Matrix(m.rows, m.cols);
        for(int i = 0; i < r.rows; ++i)
            for(int j = 0; j < r.cols; ++j)
                r.src[i][j] = aug.src[i][j+r.cols];

        // cek apakah matriks identitas terbentuk di kiri
        // kalau nggak ada, brrti matriks nggak punya invers
        for(int k = 0; k < r.rows; ++k)
            if(Math.abs(aug.src[k][k] - 1) > Matrix.EPSILON)
                throw new RuntimeException("Matrix.invERO: Matriks tidak punya invers!");

        return r;
    }
}