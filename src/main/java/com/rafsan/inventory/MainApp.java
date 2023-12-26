package com.rafsan.inventory;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class TeeOutputStream extends OutputStream {
    private OutputStream branch1;
    private OutputStream branch2;
    
    public TeeOutputStream(OutputStream branch1, OutputStream branch2) {
        this.branch1 = branch1;
        this.branch2 = branch2;
    }
    
    @Override
    public void write(int b) throws IOException {
        branch1.write(b);
        branch2.write(b);
    }
    
    @Override
    public void flush() throws IOException {
        branch1.flush();
        branch2.flush();
    }
    
    public void close() throws IOException {
        branch1.close();
        branch2.close();
    }
    
}

class TimestampPrintStream extends PrintStream {
//    private String timeStamp;
    
    public TimestampPrintStream(OutputStream out) {
        super(out);
//        timeStamp = "[" + formatter.format(new Date()) + "] ";
//        this.timeStamp = timeStamp;
    }
    
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    
    @Override
    public void println(String x) {
        String timeStamp = "[" + formatter.format(new Date()) + "] ";
        super.println(timeStamp + x);
    }
}

public class MainApp extends Application {

    private double xOffset = 0;
    private double yOffset = 0;
    private final static String version = "2.0.10";
    
    private static final Logger logger = LogManager.getLogger(MainApp.class);
    
    private static String userDir = System.getProperty("user.dir");
    private static String currentLocation = userDir + File.separator;
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private static File errorDir = new File(currentLocation + "logs" + File.separator + "error");
    private static File logDir = new File(currentLocation + "logs" + File.separator + "output");
    
    private static String outputFileName = logDir + File.separator + "output-" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".txt";
    private static String errorFileName = errorDir + File.separator + "error-" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".txt";
    
    private static File errorFile = new File(errorFileName);
    private static File outputFile = new File(outputFileName);
    
    private static FileOutputStream outFile;
    private static FileOutputStream errFile;
    
    private static ServerSocket socket;
    
    public void showLogin() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        Stage stage = new Stage();
        root.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        Scene scene = new Scene(root);
        stage.setTitle("Billing Mikrotik:: Version 1.0");
        stage.getIcons().add(new Image("/images/logo.png"));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setMaximized(true);
        stage.show();
    }
    
    private static boolean isRunningPID() {
        boolean status = false;
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println(jvmName);
        String pid = jvmName.split("@")[0];
        System.out.println(pid);
        
        List<String> runningPids = new ArrayList<>();
        
        for (String runningPid : runningPids) {
            if (pid.equals(runningPid)) {
                System.out.println("Application is already running");
                status = true;
            }
        }
        return status;
    }
    
    private static boolean isRunningPort() throws UnknownHostException, IOException {
        boolean status = false;
        int port = 666;
        
        try {
            socket = new ServerSocket(port,0,InetAddress.getByAddress(new byte[] {127,0,0,1}));
        } catch (BindException e) {
            System.err.println("Already running");
            status = true;
        }
        
        return status;
    }

    @Override
    public void start(Stage stage) throws Exception {
        core.init();
        windows("/fxml/Dumum.fxml", "Billiard");
    }
    
    private void windows(String path, String title) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.getIcons().add(new Image("/images/logo.png"));
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setOutput() {        
        if (!errorDir.exists()) {
            errorDir.mkdirs();
        }
        
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        
//        String timeStamp = "[" + formatter.format(new Date()) + "] ";
        
        PrintStream consoleOut = System.out;
        PrintStream consoleErr = System.err;
        
        try {
            // Buka file untuk menulis output dari System.out
            outputFile.createNewFile();
            outFile = new FileOutputStream(outputFile, true);
            PrintStream fileOut = new PrintStream(outFile);

            // Alihkan aliran keluaran standar ke file dan konsol menggunakan TeeOutputStream
            System.setOut(new TimestampPrintStream(new TeeOutputStream(consoleOut, fileOut)));

            // Buka file untuk menulis output dari System.err
            errorFile.createNewFile();
            errFile = new FileOutputStream(errorFile, true);
            PrintStream fileErr = new PrintStream(errFile);

            // Alihkan aliran keluaran kesalahan ke file dan konsol menggunakan TeeOutputStream
            System.setErr(new TimestampPrintStream(new TeeOutputStream(consoleErr, fileErr)));
        } catch (FileNotFoundException e) {
            System.err.println("File tidak ditemukan: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Terjadi kesalahan saat menulis ke file.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        setOutput();
        
        if (isRunningPort()) {
            Platform.exit();
            System.exit(0);
        }
        if (HibernateUtil.setSessionFactory()) {
            launch(args);
            HibernateUtil.getSessionFactory().close();
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("An error has occured!");
                alert.setHeaderText("Database Connection Error!");
                alert.setContentText("No Database connection");
                alert.showAndWait();
                try {
//                        serverio.stopServer();
                } catch (Exception ex) {
                    logger.error(ex);
                }
                Platform.exit();
            });
        }
    }
}
