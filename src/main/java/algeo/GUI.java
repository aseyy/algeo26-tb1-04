package algeo;
import algeo.modules.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GUI extends Application {
    // window utama
    Stage stage;
    // tempat input sama hasil
    TextArea input;
    TextArea hasil;
    @Override
    public void start(Stage s) {
        stage = s;
        // input
        Label inputLabel = new Label("Input");
        input = new TextArea();

        // tombol
        Button upload = new Button("Upload File .txt");
        Button spl = new Button("SPL");
        Button det = new Button("Determinan");
        Button inv = new Button("Invers");
        Button polinom = new Button("Interpolasi Polinomial");
        Button spline = new Button("Natural Cubic Spline");
        Button regresi  = new Button("Regresi Spline");
        Button image = new Button("Image Hole Filling");
        // hasil
        Label hasilLabel = new Label("Hasil");
        hasil = new TextArea();
        hasil.setEditable(false);

        // sambungin tombol
        // error handlingny ngikut di App
        upload.setOnAction(e -> {try {uploadFile();} catch(Exception err) {hasil.setText("Error: " + err.getMessage());}});
        spl.setOnAction(e -> {try {spl();} catch(Exception err) {hasil.setText("Error: " + err.getMessage());}});
        det.setOnAction(e -> {try {determinan();} catch(Exception err) {hasil.setText("Error: " + err.getMessage());}});
        inv.setOnAction(e -> {try {invers();} catch(Exception err) {hasil.setText("Error: " + err.getMessage());}});
        polinom.setOnAction(e -> {try {interpolasi();} catch(Exception err) {hasil.setText("Error: " + err.getMessage());}});
        spline.setOnAction(e -> {try {cubicSpline();} catch(Exception err) {hasil.setText("Error: " + err.getMessage());}});
        regresi.setOnAction(e -> {try {regresi();} catch(Exception err) {hasil.setText("Error: " + err.getMessage());}});
        image.setOnAction(e -> {try {imageHoleFill();} catch(Exception err) {hasil.setText("Error: " + err.getMessage());}});

        // layout
        VBox root = new VBox();
        root.setSpacing(5);
        root.getChildren().add(inputLabel);
        root.getChildren().add(input);
        root.getChildren().add(upload);
        root.getChildren().add(spl);
        root.getChildren().add(det);
        root.getChildren().add(inv);
        root.getChildren().add(polinom);
        root.getChildren().add(spline);
        root.getChildren().add(regresi);
        root.getChildren().add(image);
        root.getChildren().add(hasilLabel);
        root.getChildren().add(hasil);
        // bikin window
        Scene scene = new Scene(root, 600, 750);
        stage.setTitle("NurEngine Goated");
        stage.setScene(scene);
        stage.show();
    }

    private void uploadFile() {
        // pilih file txt
        FileChooser fc = new FileChooser();
        fc.setTitle("Pilih file input");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Text File", "*.txt");

        fc.getExtensionFilters().add(filter);
        File file = fc.showOpenDialog(stage);
        if(file == null) {
            return;
        }

        // dari App
        double[][] data = App.readMatrixFromFile(file.getAbsolutePath());
        // masukin ke textbox
        input.setText(App.formatMatrix(data));
    }

    private double[][] readInput() {
        // ngikutin readMatrixFromFile
        double[][] m = new double[App.maxOrdo][App.maxOrdo];
        String[] lines = input.getText().split("\\R");
        int row = 0, col = 0, colp = 0;
        boolean b = false;
        // baca per baris
        for(int i = 0;i < lines.length; i++) {
            String l = lines[i];


            String[] e = l.trim().split("\\s+");
            if(e.length == 0 || e[0].isEmpty()) continue;
            if(!b) {
                col = e.length;
                colp = e.length;
                b = true;
            }
            else {
                colp = col;
                col = e.length;
            }
            // copas dari App
            if(col != colp)throw new IllegalArgumentException("Input tidak konsisten!");

            for(int j = 0; j < col; j++) {
                m[row][j] = App.parseNumber(e[j]);
            }
            row++;
        }

        double[][] r = new double[row][col];
        for(int i = 0; i < row; ++i)
            for(int j = 0; j < col; ++j)
                r[i][j] = m[i][j];
        return r;
    }

    private void spl() {
        // pilih metode
        ChoiceDialog<String> pilih = new ChoiceDialog<String>("Eliminasi Gauss", "Eliminasi Gauss", "Eliminasi Gauss-Jordan", "Kaidah Cramer", "Matriks Balikan");
        pilih.setTitle("SPL");
        pilih.setHeaderText(null);
        pilih.setContentText("Metode:");
    
        String metode = pilih.showAndWait().get();
        // baca data
        double[][] augmented = readInput();

        // Convert double [][] ke bentuk matriks
        Matrix s = App.toMatrix(augmented);

        // Solve SPL
        double[][] sol = null;
        String methodName = "";

        // Penentuan metode
        if(metode.equals("Eliminasi Gauss")) {
            sol = SPLSolver.byREF(s, true);
            methodName = "Eliminasi Gauss";
        }
        else if(metode.equals("Eliminasi Gauss-Jordan")) {
            sol = SPLSolver.byRREF(s, true);
            methodName = "Eliminasi Gauss-Jordan";
        }
        else if(metode.equals("Kaidah Cramer")) {
            sol = SPLSolver.byCramer(s, true);
            methodName = "Kaidah Cramer";
        }
        else if(metode.equals("Matriks Balikan")) {
            sol = SPLSolver.byInverse(s, true);
            methodName = "Matriks Balikan";
        }

        // Format output
        StringBuilder sb = new StringBuilder();
        StringBuilder sbt = new StringBuilder();
        sb.append("Metode:\n").append(methodName).append("\n\n");
        sbt.append("Metode:\n").append(methodName).append("\n\n");
        sb.append("Input:\n").append(App.formatMatrix(augmented)).append("\n");

        if(s.rows > 50 && s.cols > 25)
            sbt.append("Input:\n").append("(Ordo matrix melebihi 50x25. Hanya bisa dilihat di file!)\n\n");
        else
            sbt.append("Input:\n").append(App.formatMatrix(augmented)).append("\n");

        sb.append("Solusi:\n").append(App.formatSPLSolution(sol));

        if(s.cols > 25)
            sbt.append("Solusi:\n").append("(Jumlah variabel melebihi 24. Hanya bisa dilihat di file!)\n\n");
        else
            sbt.append("Solusi:\n").append(App.formatSPLSolution(sol));
        // tampilin yang versi layar
        hasil.setText(sbt.toString());
    }

    private void determinan() {
        // pilih metode
        ChoiceDialog<String> pilih = new ChoiceDialog<String>("Ekspansi Kofaktor", "Ekspansi Kofaktor", "Reduksi Baris");

        pilih.setTitle("Determinan");
        pilih.setHeaderText(null);
        pilih.setContentText("Metode:");

        String metode = pilih.showAndWait().get();

        // Baca data
        double[][] matrix = readInput();

        // Validasi matriks kuadrat
        App.validateSquareMatrix(matrix);

        // Solve determinan
        double det = 0;
        String methodName = "";

        // Penentuan metode
        if(metode.equals("Ekspansi Kofaktor")) {
            det = Matrix.det(App.toMatrix(matrix), true);
            methodName = "Ekspansi Kofaktor";
        }
        else if(metode.equals("Reduksi Baris")) {
            det = Matrix.gdet(App.toMatrix(matrix), true);
            methodName = "Reduksi Baris";
        }
        // Format output
        StringBuilder sb = new StringBuilder();
        StringBuilder sbt = new StringBuilder();
        sb.append("Metode:\n").append(methodName).append("\n\n");
        sbt.append("Metode:\n").append(methodName).append("\n\n");
        sb.append("Input:\n").append(App.formatMatrix(matrix)).append("\n");

        if(matrix.length > 25)
            sbt.append("Input:\n").append("(Ordo matrix melebihi 25x25. Hanya bisa dilihat di file!)\n\n");
        else
            sbt.append("Input:\n").append(App.formatMatrix(matrix)).append("\n");

        sb.append("Determinan:\n").append(String.format("%.3f", det)).append("\n");
        sbt.append("Determinan:\n").append(String.format("%.3f", det)).append("\n");

        // tampilin yang versi layar
        hasil.setText(sbt.toString());
    }

    private void invers() {
        // pilih metode
        ChoiceDialog<String> pilih =
            new ChoiceDialog<String>("Augmen", "Augmen", "Adjoin");
        pilih.setTitle("Invers");
        pilih.setHeaderText(null);
        pilih.setContentText("Metode:");
        String metode = pilih.showAndWait().get();

        // Baca data
        double[][] matrix = readInput();
        // Validasi matriks kuadrat
        App.validateSquareMatrix(matrix);

        double[][] inverse = null;
        String methodName = "";

        // Penentuan metode
        if(metode.equals("Augmen")) {
            inverse = App.toArray(Matrix.ginv(App.toMatrix(matrix), true)); 
            methodName = "Augmen";
        }
        else if(metode.equals("Adjoin")) {
            inverse = App.toArray(Matrix.inv(App.toMatrix(matrix),true));
            methodName = "Adjoin";
        }

        // Format output
        StringBuilder sb = new StringBuilder();
        StringBuilder sbt = new StringBuilder();
        sb.append("Metode:\n").append(methodName).append("\n\n");
        sbt.append("Metode:\n").append(methodName).append("\n\n");
        sb.append("Input:\n").append(App.formatMatrix(matrix)).append("\n");

        if(matrix.length > 25)
            sbt.append("Input:\n").append("(Ordo matrix melebihi 25x25. Hanya bisa dilihat di file!)\n\n");
        else
            sbt.append("Input:\n").append(App.formatMatrix(matrix)).append("\n");

        sb.append("Matriks Balikan:\n").append(App.formatMatrix(inverse)).append("\n");

        if(matrix.length > 25)
            sbt.append("Matriks Balikan:\n").append("(Ordo matrix melebihi 25x25. Hanya bisa dilihat di file!)\n\n");
        else
            sbt.append("Matriks Balikan:\n").append(App.formatMatrix(inverse)).append("\n");

        // tampilin yang versi layar
        hasil.setText(sbt.toString());
    }

    private void interpolasi() {
        // baca titik
        double[][] points = readInput();

        // Validasi tiap titik tidak kosong & berpasangan
        App.validatePoints(points);

        // Solve interpolasi
        double[] coeffs = Interpolation.Polynomial(points);
        String equation = App.formatPolynomialEquation(coeffs);
        Arrays.sort(points,(a,b) -> a[0] > b[0] ? 1 : -1);
        // Format
        StringBuilder sb = new StringBuilder();
        sb.append("Metode:\nInterpolasi Polinomial\n\n");
        sb.append("Domain:\n").append(String.format("[%.3f, %.3f]\n\n", points[0][0], points[coeffs.length-1][0]));
        sb.append("Persamaan:\ny = ").append(equation).append("\n");
        // tampilin
        hasil.setText(sb.toString());
    }

    private void cubicSpline() {
        // baca titik
        double[][] points = readInput();
        // Validasi tiap titik tidak kosong & berpasangan
        App.validatePoints(points);
        // Cari turunan kedua dari spline dari tiap point
        double[] knots = Interpolation.CubicSplinal(points);
        // Formatting output, copas dari App
        StringBuilder sb = new StringBuilder();
        sb.append("Metode:\nNatural Cubic Spline\n\n");
        sb.append("Persamaan setiap segmen:\n");
        for(int i = 0; i < points.length-1; ++i)
            sb.append(App.formatSegmentInterpolation(points, knots, i));

        sb.append("\nDomain setiap segmen:\n");
        for(int i = 0; i <  points.length-1; i++) {
            if(i < points.length-2)
                sb.append(String.format("- D{%d,%d} = [%.3f, %.3f)\n", i, i+1, points[i][0], points[i+1][0]));
            else
                sb.append(String.format("- D{%d,%d} = [%.3f, %.3f]\n", i, i+1, points[i][0], points[i+1][0]));
        }

        sb.append("\nNilai turunan kedua tiap titik:\n");

        for(int i = 0; i < knots.length; i++)
            sb.append(String.format("- k%d = %.3f%n", i, knots[i]));

        // tampilin
        hasil.setText(sb.toString());
    }

    private void regresi() {
        // baca titik
        double[][] points = readInput();

        // input knot
        TextInputDialog inputKnot = new TextInputDialog("0");

        inputKnot.setTitle("Regresi Cubic Spline");
        inputKnot.setHeaderText(null);
        inputKnot.setContentText("Masukkan knot, pisahkan pakai spasi:");
        String textKnot = inputKnot.showAndWait().get();
        String[] splitKnot = textKnot.trim().split("\\s+");
        double[] knots = new double[splitKnot.length];
        // alur input knot dari App
        for(int i = 0; i < splitKnot.length; i++) {
            knots[i] = App.parseNumber(splitKnot[i]);
        }
        // Validasi poin dan knot
        App.validateRegressionInput(points, knots);
        // copas dari App
        double[] coeffs = Regression.CubicSplinal(points,knots);
        String equation = App.formatTruncatedPowerEquation( coeffs, knots);
        // output
        StringBuilder sb = new StringBuilder();
        sb.append("Koefisien Regresi:\n");
        for(int i = 0; i < coeffs.length; ++i)
            sb.append(
                String.format("- B%d = %.3f\n",i,coeffs[i]));

        sb.append("\nPosisi knot:\n");
        for(int i = 0; i < knots.length; ++i)
            sb.append(
                String.format("- k%d = (%.3f, %.3f)\n",i,knots[i],Regression.CubicSplinalEvaluate(coeffs,knots,knots[i])));
        sb.append("\nPersamaan:\ny = ").append(equation).append("\n");

        // tampilin
        hasil.setText(sb.toString());
    }

    private void imageHoleFill() {
        // pilih gambar asli
        FileChooser pilihGambar = new FileChooser();
        pilihGambar.setTitle("Pilih gambar asli");

        File image = pilihGambar.showOpenDialog(stage);
        if(image == null) {
            return;
        }
        // pilih mask
        FileChooser pilihMask = new FileChooser();

        pilihMask.setTitle("Pilih mask");
        File mask = pilihMask.showOpenDialog(stage);
        if(mask == null) {
            return;
        }

        // pilih tempat output
        FileChooser pilihOutput = new FileChooser();
        pilihOutput.setTitle("Simpan hasil");
        pilihOutput.setInitialFileName("output.png");
        File output = pilihOutput.showSaveDialog(stage);
        if(output == null) {
            return;
        }
        // copas
        ImageHoleFill.Result result;

        try {
            result = ImageHoleFill.fill(image.getAbsolutePath(), mask.getAbsolutePath(), output.getAbsolutePath());
        }
        catch(IOException e) {
            throw new RuntimeException("Gagal memproses gambar: " +e.getMessage()
            );
        }

        //  output
        StringBuilder sb = new StringBuilder();
        sb.append("Metode: Image Hole Filling (Rata-rata Tetangga Iteratif)\n");
        sb.append("Ukuran gambar: ").append(result.width).append(" x ").append(result.height).append("\n");
        sb.append("Jumlah pixel hole: ").append(result.holeCount).append("\n");
        sb.append("Jumlah iterasi: ").append(result.iterations).append("\n");
        sb.append("Error akhir: ").append(String.format("%.3f", result.finalError)).append("\n");
        sb.append("Output disimpan di: ").append(result.outputPath).append("\n");
        hasil.setText(sb.toString());
    }
    public static void main(String[] args) {
        launch(args);
    }
}
