package algeo.modules;

// buat rgb
import java.awt.Color;
// proses gambar
import java.awt.image.BufferedImage;
//proses file
import java.io.File;
// exception
import java.io.FileNotFoundException;
// exception buat read write
import java.io.IOException;
// read write gambar
import javax.imageio.ImageIO;

/**
 * Implementasi pembenahan gambar rusak dengan mask (Image Hole Filling)
 * menggunakan Algoritma Gauss-Seidel.
 * Berikut adalah daftar fungsinya:
 * <ul>
 *  <li>{@link ImageHoleFill#fill static ImageHoleFill.fill}</li>
 * </ul>
 * 
 * @author Rionaldo Casey Panditha (aseyy)
 * @since 25/10/2026
 */
public class ImageHoleFill {
    private static final int MAX_SIZE = 512; // sesuai spek
    private static final double TOLERANCE = 0.001; // asumsi batas error dah cukup kecil
    
    /**
     * Tipe bentukan baru untuk menampung hasil proses filling.
     */
    public static class Result {
        /** Lebar gambar. */
        final public int width;
        
        /** Tinggi gambar. */
        final public int height;

        /** Jumlah piksel yang di-masking. */
        final public int holeCount; 
        
        /** Jumlah iterasi. */
        final public int iterations;

        /** Galat akumulasi akhir. */
        final public double finalError;

        /** Lokasi gambar rekonstruksi. */
        final public String outputPath;
        
        /**
         * Membentuk tipe bentukan untuk menampung hasil proses filling.
         * @param width lebar gambar
         * @param height tinggi gambar
         * @param holeCount jumlah piksel yang di-mask
         * @param iterations jumlah iterasi
         * @param finalError galat akhir
         * @param outputPath lokasi keluaran
         */
        public Result(int width, int height, int holeCount, int iterations, double finalError, String outputPath) {
            // Simpan param ke result
            this.width = width;
            this.height = height;
            this.holeCount = holeCount;
            this.iterations = iterations;
            this.finalError = finalError;
            this.outputPath = outputPath;
        }
    }
    
    /**
     * Merekonstruksi gambar rusak dengan bantuan mask menggunakan algoritma Gauss-Seidel.
     * @param imagePath letak gambar rusak berada
     * @param maskPath letak mask gambar berada
     * @param outputPath letak gambar rekonstruksi berada
     * @return objek bertipe Result
     * @throws IOException ketika ada berkas yang tidak bisa dibaca
     * @throws IllegalArgumentException ketika mask gambar seluruhnya putih
     */
    public static Result fill(String imagePath, String maskPath, String outputPath) throws IOException {
        // cek ekstensi foto
        checkInputFormat(imagePath);
        checkInputFormat(maskPath);
        checkOutputFormat(outputPath);

        //simpen jdi buffered, namanya image & mask
        BufferedImage image = readOriginalImage(imagePath);
        BufferedImage mask = readMaskImage(maskPath);

        // ambil size gambar
        int width = image.getWidth();
        int height = image.getHeight();
        
        // pastiin ukuran sama
        checkSameSize(image, mask);

        // gboleh lebihin bates max
        checkMaxSize(image);

        // mapping pixel putih
        boolean[][] hole = makeHoleMap(mask);

        // hitung pixel putih
        int holeCount = countHole(hole);

        // harusnya mask gaboleh putih semua
        if(holeCount == width * height) {
            throw new IllegalArgumentException("ImageHoleFill.fill: Mask tidak boleh putih semua");
        }

        // nandain ada hole apa kga
        boolean hasHole = false;
        if(holeCount > 0) {
            hasHole = true;
        }

        // kalo mask hitam semua, lgsg save gambar asli aja ke output
        if(!hasHole) {
            saveImage(image, outputPath);
            return new Result(width, height, 0, 0, 0.0, outputPath);
        }

        // pisahin rgb
        double[][] red = getRedChannel(image);
        double[][] green = getGreenChannel(image);
        double[][] blue = getBlueChannel(image);

        // pixel hole dijadiin 0 dlu di awal
        setRedHoleToZero(red, hole);
        setGreenHoleToZero(green, hole);
        setBlueHoleToZero(blue, hole);
        
        int iteration = 0;
        // default aja buat while awal pake angka random > tolerance
        double maxError = 676767;
        while(maxError > TOLERANCE) {
            maxError = 0.0;
            for(int i = 0; i < height; i++) {
                for(int j = 0; j< width; j++) {
                    if(hole[i][j] == false) {
                        continue; // kalo pixel bukan hole, sekif
                    }

                    //simpen nilai asalnya buat ngitung error
                    double oldRed = red[i][j];
                    double oldGreen = green[i][j];
                    double oldBlue = blue[i][j];

                    // cari nilai dri avg tetangga, nanti nilai barunya lgsg dipake buat next pixel
                    double newRed = getAverage(red, i, j);
                    red[i][j] = newRed;
                    double newGreen = getAverage(green, i, j);
                    green[i][j] = newGreen;
                    double newBlue = getAverage(blue, i, j);
                    blue[i][j] = newBlue;

                    // hitung error
                    double redError = newRed - oldRed;
                    if(redError < 0) {
                        redError = redError * -1; // fungsi abs ala ala (gatau jir boleh dipake apa kgak)
                    }
                    double greenError = newGreen - oldGreen;
                    if(greenError < 0) {
                        greenError = greenError * -1;
                    }
                    double blueError = newBlue - oldBlue;
                    if(blueError <0) {
                        blueError = blueError * -1;
                    }

                    // cari max error buat pixel skrg
                    double pixelError = redError;
                    if(greenError > pixelError) {
                        pixelError = greenError;
                    }
                    if(blueError > pixelError) {
                        pixelError = blueError;
                    }

                    //nyari max error dari semua pixel 1x iterasi
                    if(pixelError > maxError) {
                        maxError = pixelError;
                    }
                }
            }
            iteration = iteration + 1;
        }

        // ubah jdi bufferedimg pas udh beres
        BufferedImage output = makeOutputImage(image, hole, red, green, blue);
        saveImage(output, outputPath); //save

        // result buat ditampilin di app ntar
        Result result = new Result(
            width,
            height,
            holeCount,
            iteration,
            maxError,
            outputPath
        );
        
        return result;
    }

    /* ========= PROPERTI DAN FUNGSI PEMBANTU =========== */
    // baca gambar aseli
    private static BufferedImage readOriginalImage(String path) throws IOException {
        // bikin file
        File file = new File(path);
        if(file.exists() == false) {
            throw new FileNotFoundException("ImageHoleFill.readOriginalImage: file tidak ditemukan di " +path); // barangkali filenya gaada
        }

        BufferedImage image = ImageIO.read(file);
        if(image == null) {
            throw new IOException("ImageHoleFill.readOriginalImage: gambar asli gagal dibaca"); // barangkali gambarnya gabisa dibaca
        }

        return image;
    }

    // sama aja kyk fungsi atas ini, cuma buat mask
    private static BufferedImage readMaskImage(String path) throws IOException {
        File file = new File(path);
        if(file.exists() == false) {
            throw new FileNotFoundException("ImageHoleFill.readMaskImage: file tidak ditemukan di " + path);
        }

        BufferedImage mask = ImageIO.read(file);
        if(mask == null) {
            throw new IOException("ImageHoleFill.readMaskImage: mask gagal dibaca");
        }

        return mask;
    }

    // mastiin ukuran gambar asli sama mask sama
    private static void checkSameSize(BufferedImage image, BufferedImage mask) {
        // ukuran img asli
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        // ukuran mask
        int maskWidth = mask.getWidth();
        int maskHeight = mask.getHeight();

        boolean sameWidth = imageWidth == maskWidth;
        boolean sameHeight = imageHeight == maskHeight;
        if(sameWidth == false || sameHeight == false) {
            throw new IllegalArgumentException("ImageHoleFill.checkSameSize: ukuran gambar dan mask beda");
        }
    }

    // cek batas max
    private static void checkMaxSize(BufferedImage image) {
        // ukuran img
        int width= image.getWidth();
        int height = image.getHeight();

        // klo salah satunya aja >512, gaboleh
        if(width >MAX_SIZE || height > MAX_SIZE) {
            throw new IllegalArgumentException("ImageHoleFill.checkMaxSize: ukuran gambar maksimal 512 x 512px");
        }
    }

    // ubah mask jd boolean
    private static boolean[][] makeHoleMap(BufferedImage mask) {
        //ukuran mask
        int width = mask.getWidth();
        int height = mask.getHeight();

        // array 2d
        boolean[][] hole = new boolean[height][width];
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width ; j++) {
                // getrgb ngasilin integer info warna, bungkus pake color buat misahin rgb
                Color color = new Color(mask.getRGB(j, i));
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                if(red == 255 && green == 255 && blue == 255) { // putih
                    hole[i][j] = true;
                }
                else {
                    hole[i][j] = false;
                }
            }
        }

        return hole;
    }

    // hitung hole
    private static int countHole(boolean[][] hole) {
        int count = 0;
        for(int i = 0; i < hole.length; i++) {
            for(int j = 0; j < hole[i].length; j++) {
                if(hole[i][j] == true) {
                    count = count + 1;
                }
            }
        }

        return count;
    }

    // masukin warna ke matrix warna masing"
    private static double[][] getRedChannel(BufferedImage image) {
        // ukuran img
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] red = new double[height][width];

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {

                // ambil warna (x=j, y=i)
                Color color = new Color(image.getRGB(j, i));
                // ambil merahnya aja
                int value = color.getRed();
                // valuenya jd bentuk double
                red[i][j] = value;
            }
        }

        return red;
    }

    // buat ijo
    private static double[][] getGreenChannel(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] green = new double[height][width];

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                Color color = new Color(image.getRGB(j, i));
                int value = color.getGreen();
                green[i][j] = value;
            }
        }

        return green;
    }

    // buat biru
    private static double[][] getBlueChannel(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] blue = new double[height][width];

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                Color color = new Color(image.getRGB(j, i));
                int value = color.getBlue();
                blue[i][j] = value;
            }
        }

        return blue;
    }

    // rgb buat hole diubah ke 0 dlu buat awalannya
    private static void setRedHoleToZero(double[][] red, boolean[][] hole) {
        for(int i = 0; i < red.length; i++) {
            for(int j = 0; j < red[i].length; j++) {
                if(hole[i][j] == true) {
                    red[i][j] = 0.0;
                }
            }
        }
    }

    // ini versi hijau
    private static void setGreenHoleToZero(double[][] green, boolean[][] hole) {
        for(int i = 0; i < green.length; i++) {
            for(int j = 0; j < green[i].length; j++) {
                if(hole[i][j] == true) {
                    green[i][j] = 0.0;
                }
            }
        }
    }

    // ini versi biru
    private static void setBlueHoleToZero(double[][] blue, boolean[][] hole) {
        for(int i = 0; i < blue.length; i++) {
            for(int j = 0; j < blue[i].length; j++) {
                if(hole[i][j] == true) {
                    blue[i][j] = 0.0;
                }
            }
        }
    }

    // hitung rata rata tetangga pixel
    private static double getAverage(double[][] channel, int row, int col) {
        double top = 0.0; //atas
        double bottom = 0.0; // bawah
        double left = 0.0; // kiri
        double right = 0.0; // kanan

        // buat ngecek tetangga mana aja yg ada
        boolean hasTop = false;
        boolean hasBottom = false;
        boolean hasLeft = false;
        boolean hasRight = false;

        // cek atas
        if(row - 1 >= 0) {
            top = channel[row-1][col];
            hasTop = true;
        }
        // cek bawah
        if(row + 1 < channel.length) {
            bottom = channel[row+1][col];
            hasBottom = true;
        }
        //cek kiri
        if(col - 1 >= 0) {
            left = channel[row][col-1];
            hasLeft = true;
        }
        // cek kanan
        if(col + 1 <channel[row].length) {
            right = channel[row][col+1];
            hasRight = true;
        }

        // jumlah nilai semua tetangga valid
        double total = 0.0;

        // tetangga yg valid brp
        int neighborCount = 0;

        if(hasTop) {
            total = total + top;
            neighborCount = neighborCount + 1;
        }
        if(hasBottom) {
            total = total + bottom;
            neighborCount = neighborCount + 1;
        }
        if(hasLeft) {
            total = total + left;
            neighborCount = neighborCount + 1;
        }
        if(hasRight) {
            total = total + right;
            neighborCount = neighborCount + 1;
        }

        // jaga jaga takutnya dikasih gambar 1x1 px
        if(neighborCount== 0) {
            return channel[row][col];
        }

        double average = total / neighborCount; //rataratain
        return average;
    }

    // ngegabungin rgb jd bufferedimg
    private static BufferedImage makeOutputImage(BufferedImage original, boolean[][] hole, double[][] red, double[][] green, double[][] blue) {
        // ukuran img
        int width = original.getWidth();
        int height = original.getHeight();
        // bikin gambar kosong pke ukuran img
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                //klo bukan hole, pke rgb ori
                if(hole[i][j] == false) {
                    int originalRGB = original.getRGB(j, i);
                    output.setRGB(j, i, originalRGB);
                }
                else {
                    //klo hole, pake hasil yg udh diproses gauss seidel
                    double redDouble = red[i][j];
                    double greenDouble = green[i][j];
                    double blueDouble = blue[i][j];

                    // ngebuang desimal tp dibuletin dlu ke int terdekat, jadi tambahin 0,5 biar ya begitulah
                    int redInt = (int)(redDouble + 0.5);
                    int greenInt = (int)(greenDouble + 0.5);
                    int blueInt = (int)(blueDouble + 0.5);
                    // color baru dr rgb
                    Color newColor = new Color(redInt, greenInt, blueInt);
                    int rgb = newColor.getRGB();
                    output.setRGB(j, i, rgb);
                }
            }
        }

        return output;
    }

    // save bufferedimg jd png /jpg
    private static void saveImage(BufferedImage image, String path) throws IOException {
        // ambik format
        String format = getFormat(path);
        // path output
        File outputFile = new File(path);
        // save gambar
        boolean saved = ImageIO.write(image, format, outputFile); //true kalo bisa diwrite, false klo gagal
        if(saved == false) {
            throw new IOException("ImageHoleFill.saveImage: gambar gagal disimpan");
        }
    }

    // validasi ekstensi input
    private static void checkInputFormat(String path) {
        // ambil ekstensi
        String format = getFormat(path);

        // cuma boleh png / jpg
        if(format.equals("png") == false && format.equals("jpg") == false) {
            throw new IllegalArgumentException("ImageHoleFill.checkInputFormat: format gambar harus png / jpg");
        }
    }

    //sama kayak yg atas, cuma buat output
    private static void checkOutputFormat(String path) {
        String format = getFormat(path);

        if(format.equals("png") == false && format.equals("jpg") == false) {
            throw new IllegalArgumentException("ImageHoleFill.checkOutputFormat: format output harus png atau jpg");
        }
    }

    // ambil ekstensi
    private static String getFormat(String path) {
        // cari titik terakhir dri string
        int dot = path.lastIndexOf('.');
        // gaada titik di index = gaada ekstensi
        if(dot == -1) {
            throw new IllegalArgumentException("ImageHoleFill.getFormat: file harus ada ekstensi");
        }
        // kalo last char titik, brarti gaada ekstensi juga
        if(dot == path.length()-1) {
            throw new IllegalArgumentException("ImageHoleFill.getFormat: ekstensi file kosong!");
        }

        // ambil string abis titik
        String format = path.substring(dot+1);
        // bikin toLower semua, soalnya kadang ada ekstensi ".JPG" etc.
        format = format.toLowerCase();
        // jpeg = jpg
        if(format.equals("jpeg")) {
            format = "jpg";
        }

        return format;
    }
}
