package com.mth.photobackup2.GUI;

import com.mth.photobackup2.Config.ServerParameters;
import com.mth.photobackup2.Tools.JsonManipulation;
import com.mth.photobackup2.Tools.ServerParametersManipulation;
import com.mth.photobackup2.ServerThread;
import com.mth.photobackup2.Tools.Crypto;
import com.mth.photobackup2.Tools.DatabaseTool;
import com.mth.photobackup2.Tools.LogTool;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

//GUI was challenging to make
//Used a side project to create the UI, and then import the code here.
public class GUI extends JFrame implements ActionListener {

    private javax.swing.JTextField backupFolderText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel settingsPanel;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel localIPText;
    private javax.swing.JButton photoLibraryBtn;
    private javax.swing.JLabel publicIPText;
    private javax.swing.JButton saveBtn;
    private javax.swing.JTextField serverPortText;
    private javax.swing.JLabel serverStatusText;
    private javax.swing.JButton startServerBtn;
    private javax.swing.JButton stopServerBtn;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JButton rebuildDbBtn;

    private ServerParametersManipulation spm;
    private ServerParameters sp;
    private ServerThread s;
    private LogTool logTool = new LogTool();

    public GUI() {

    }

    public void startGUI() {
        logTool.logToFile("GUI starting");
        spm = new ServerParametersManipulation();
        spm.createSaveLocation();
        sp = spm.getParameters();

        System.out.println("--------------" + sp.toString());
        buildGui();

        backupFolderText.setText(sp.getFolder());
        serverPortText.setText(String.valueOf(sp.getPort()));

        IPRequests ipr = new IPRequests("IPReq", localIPText, publicIPText);
        ipr.start();

        createLibraryFolders();
    }

    private void createLibraryFolders() {

        Path pictures = Paths.get(sp.getFolder() + "\\Pictures\\");
        Path temp = Paths.get(sp.getFolder() + "\\temp\\");
        Path thumbnails = Paths.get(sp.getFolder() + "\\Thumbnails\\");

        File picturesFolder = pictures.toFile();
        File tempFolder = temp.toFile();
        File thumbnailsFolder = thumbnails.toFile();

        try {
            if (!picturesFolder.exists()) {
                Files.createDirectory(pictures);
            }
            if (!tempFolder.exists()) {
                Files.createDirectory(temp);
            }
            if (!thumbnailsFolder.exists()) {
                Files.createDirectory(thumbnails);
            }
        } catch (Exception e) {
            logTool.logToFile("createLibraryFolders Error: " + e.getMessage());
            System.out.println("-----Error creating library folders");
        }
    }

    public void buildGui() {

        JFrame frame = new JFrame("PhotoBackup");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeProgram(frame);
            }
        });
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new FlowLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        jLabel6 = new javax.swing.JLabel();
        serverStatusText = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        localIPText = new javax.swing.JLabel();
        publicIPText = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        settingsPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        backupFolderText = new javax.swing.JTextField();
        serverPortText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        saveBtn = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        photoLibraryBtn = new javax.swing.JButton();
        startServerBtn = new javax.swing.JButton();
        stopServerBtn = new javax.swing.JButton();
        passwordField = new javax.swing.JPasswordField();
        rebuildDbBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel6.setText("Server status:");

        serverStatusText.setText("Not Running");

        jPanel1.setBackground(new java.awt.Color(100, 100, 100));

        jLabel5.setText("Current local IP");

        localIPText.setText("xxx.xxx.xxx.xxx");

        publicIPText.setText("xxx.xxx.xxx.xxx");

        jLabel4.setText("Current public IP");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap(12, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(35, 35, 35)
                                                .addComponent(localIPText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(35, 35, 35)
                                                .addComponent(publicIPText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(localIPText))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(publicIPText)
                                        .addComponent(jLabel4))
                                .addGap(22, 22, 22))
        );

        settingsPanel.setBackground(new java.awt.Color(100, 100, 100));

        jLabel1.setText("Backup Folder");

        backupFolderText.setText("C:\\...");

        serverPortText.setText("8080");

        jLabel2.setText("Server port");

        jLabel3.setText("Password");

        saveBtn.setText("Save");

        passwordField.setText("password");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(settingsPanel);
        settingsPanel.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel3))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(backupFolderText, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                        .addComponent(serverPortText, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                        .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(passwordField))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addContainerGap(15, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(backupFolderText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(serverPortText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(25, 25, 25)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(32, 32, 32)
                                .addComponent(saveBtn)
                                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(100, 100, 100));

        photoLibraryBtn.setText("Open Photo Library");

        startServerBtn.setText("Start Server");

        stopServerBtn.setText("Stop Server");

        rebuildDbBtn.setText("Rebuild database");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(photoLibraryBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(startServerBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(stopServerBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(rebuildDbBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addContainerGap(8, Short.MAX_VALUE)
                                .addComponent(photoLibraryBtn)
                                .addGap(18, 18, 18)
                                .addComponent(startServerBtn)
                                .addGap(19, 19, 19)
                                .addComponent(stopServerBtn)
                                .addGap(18, 18, 18)
                                .addComponent(rebuildDbBtn)
                                .addGap(8, 8, 8))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(contentPane);
        contentPane.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jLabel6)
                                        .addGap(30, 30, 30)
                                        .addComponent(serverStatusText, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                        .addGap(45, 45, 45)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addComponent(settingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(43, 43, 43)
                                                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap(56, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jLabel6)
                                                .addComponent(serverStatusText)))
                                .addGroup(layout.createSequentialGroup()
                                        .addGap(84, 84, 84)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(settingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(69, 69, 69))
        );

        photoLibraryBtn.addActionListener(this::photoLibraryBtnClick);
        startServerBtn.addActionListener(this::startSeverBtnClick);
        stopServerBtn.addActionListener(this::stopServerBtnClick);
        saveBtn.addActionListener(this::saveBtnClick);
        rebuildDbBtn.addActionListener(this::rebuildDbBtnClick);

        frame.setMaximumSize(new Dimension(800, 600));
        frame.setVisible(true);
        frame.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void photoLibraryBtnClick(ActionEvent e) {
        System.out.println("------------------photoLibraryBtnClick!");
        try {
            Desktop.getDesktop().open(new File(backupFolderText.getText()));
        } catch (IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startSeverBtnClick(ActionEvent e) {
        startServer();
        System.out.println("------------------startSeverBtnClick!");
    }

    private void stopServerBtnClick(ActionEvent e) {
        stopServer();
        System.out.println("------------------stopServerBtnClick!");
    }

    private void saveBtnClick(ActionEvent e) { //Add try catch and error message on GUI
        System.out.println("------------------saveBtnClick!");
        JsonManipulation jm = new JsonManipulation();
        Crypto c = new Crypto();
        String folder = backupFolderText.getText();
        String password = passwordField.getText();
        int port = Integer.parseInt(serverPortText.getText());
        sp.setFolder(folder);
        sp.setPassword(password);
        sp.setPort(port);
        System.out.println("------sp to string: " + sp.toString());

        createBackupFolder(folder);

        jm.saveSettings(sp);
    }

    public void rebuildDbBtnClick(ActionEvent e) {
        if (s != null) {
            stopServer();
        }
        DatabaseTool dbTool = new DatabaseTool("DBTool", serverStatusText);
        dbTool.start();
    }

    
    private void startServer() {
        s = new ServerThread(sp.getPort(), "Server");
        System.out.println("------------------ServerThread ready to start");
        s.start();
        serverStatusText.setText("Server running");
    }

    private void stopServer() {
        s.stopServer();
        s.interrupt();
        serverStatusText.setText("Server not running");
    }

    private void createBackupFolder(String backupFolder) {
        File file = new File(backupFolder);
        System.out.println("-------backupFolder exists: " + file.exists() + backupFolder);

        File filePic = new File(backupFolder + "\\Pictures");
        File fileThumb = new File(backupFolder + "\\Thumbnails");

        if (!file.exists()) {
            try {
                Path path = Paths.get(backupFolder);
                Files.createDirectories(path);

            } catch (IOException ex) {
                System.out.println("-------Error creating folder: ");
                ex.printStackTrace();
            }
        }

        if (!filePic.exists()) {
            try {
                Path pictures = Paths.get(filePic.toString());
                Files.createDirectories(pictures);
            } catch (IOException ex) {
                Logger.getLogger(GUI.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (!fileThumb.exists()) {
            try {
                Path thumbnails = Paths.get(fileThumb.toString());
                Files.createDirectories(thumbnails);
            } catch (IOException ex) {
                Logger.getLogger(GUI.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void closeProgram(JFrame frame) {
        logTool.logToFile("Closing program");
        try {
            stopServer();
        } catch (Exception e) {
        logTool.logToFile("Closing program - Error stopping server: " + e.getMessage());
        }
        frame.dispose();
        System.exit(0);
    }

}
