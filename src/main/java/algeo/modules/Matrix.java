package algeo.modules;

public class Matrix {
    /**
     * === METODE DAN PROPERTI DASAR MATRIKS ===
     * Matrix.rows: Memuat jumlah baris matriks
     * Matrix.cols: Memuat jumlah kolom matriks
     * Matrix.src: Tempat elemen disimpan, bertipe "double"
     * Matrix.Matrix(): Konstuktor
     * Matrix.print(): Mencetak matriks secara rapih dengan padding
     */
    
    public int rows;
    public int cols;
    public double[][] src;


    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.src = new double[rows][cols];
    }

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

    /** === FUNGSI DASAR MATRIKS ===
     * static Matrix.add(): Menambahkan 2 buah matriks
     * static Matrix.mul(): Mengalikan 2 buah matriks
     * static Matrix.tr(): Mentranspose matriks, yakni menukar setiap baris menjadi kolom dan sebaliknya
     * static Matrix.sb(): 
        - 2 parameter: Membuat upamatriks dari m tanpa kolom in, dan jn
        - 4 parameter: Membuat upamatriks dari m, dari titik (i0,j0) sampai (ip,jp)
     * static Matrix.det(): Mencari determinan matriks persegi dengan ekspansi kofaktor
     * static Matrix.cof(): Mengonstruksi matriks kofaktor suatu matriks
     * static Matrix.adj(): Mengonstruksi matriks adjoint suatu matriks, yakni transpose dari matriks kofaktornya
     * static Matrix.inv(): Mengonstruksi matriks inverse dengan perhitungan determinan ekspansi kofaktor
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

    public static double det(Matrix m) {
        if(m.rows != m.cols)
            throw new IllegalArgumentException("Matrix.det: matriks yang diberikan bukanlah matriks persegi!");

        switch (m.rows) {
            case 0: return 1;
            case 1: return m.src[0][0];
            case 2: return m.src[0][0]*m.src[1][1] - m.src[0][1]*m.src[1][0];
        }
            
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

        double val = 0;
        if(maxZeroR >= maxZeroC) {
            for(int j = 0; j < m.cols; ++j) {
                if(m.src[locR][j] == 0)
                    continue;
                
                Matrix sub = Matrix.sb(m, locR, j);
                double c = m.src[locR][j];
                double cf = (locR+j) % 2 == 0 ? 1 : -1;
                val += Matrix.det(sub) * c * cf;
            }
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
        double det = Matrix.det(m);
        if(det == 0)
            throw new IllegalArgumentException("Matrix.inv: determinan matriks bernilai 0");
        
        Matrix r = Matrix.adj(m);
        for(int i = 0; i < m.rows; ++i)
            for(int j = 0; j < m.cols; ++j)
                r.src[i][j] = 1/det * r.src[i][j];
        
        return r;
    }

    // // == operasi baris elementer matriks ==
    // public int swapR(int r1, int r2) {}
    // public int mulR(int r, int c) {}
    // public int addR(int r1, int r2, int c) {}

    // // fungsi pembentuk baris eselon (dan tereduksinya)
    // public Matrix toREF() {}
    // public Matrix toRREF() {}

    // // fungsi invers dan determinan khusus (dengan OBE)
    // public int detERO() {}
    // public Matrix invERO() {}
}