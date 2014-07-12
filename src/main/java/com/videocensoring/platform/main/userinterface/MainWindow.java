/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videocensoring.platform.main.userinterface;

import com.videocensoring.platform.main.core.MainProcessor;
import com.videocensoring.platform.main.core.featureextract.video.VideoProcessor;
import java.awt.Frame;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.ListModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 *
 * @author Kushan
 */
public class MainWindow extends javax.swing.JFrame {

    private File selectFile;
    private NormalPlayWindow playWindow;

    //Main Processor For All
    private MainProcessor mp;

    private boolean isNotPlaying = true;

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        mp = new MainProcessor();// A Video Processing object is initialized
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        window = this;

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * //Select file from the Directory
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jfcSelectFiles = new javax.swing.JFileChooser();
        jToolBar1 = new javax.swing.JToolBar();
        btLoad = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        btSkinDetection = new javax.swing.JButton();
        btPlay = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jlSkinCount = new javax.swing.JList();
        lbFilePath = new javax.swing.JLabel();
        jdpViewPort = new javax.swing.JDesktopPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar1.setRollover(true);

        btLoad.setText("Load");
        btLoad.setFocusable(false);
        btLoad.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btLoad.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLoadActionPerformed(evt);
            }
        });
        jToolBar1.add(btLoad);

        btSkinDetection.setText("Detect content");
        btSkinDetection.setEnabled(false);
        btSkinDetection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSkinDetectionActionPerformed(evt);
            }
        });

        btPlay.setText("Play");
        btPlay.setEnabled(false);
        btPlay.setFocusable(false);
        btPlay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btPlay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPlayActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(jlSkinCount);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btSkinDetection, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btPlay, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(btPlay, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btSkinDetection, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE))
        );

        jdpViewPort.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                onKeyPress(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 884, Short.MAX_VALUE)
            .addComponent(lbFilePath, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jdpViewPort))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jdpViewPort)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLoadActionPerformed
        int returnVal = jfcSelectFiles.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectFile = jfcSelectFiles.getSelectedFile();
            lbFilePath.setText(selectFile.getName());

        } else {
            System.out.println("File access cancelled by user.");
        }

        btPlay.setEnabled(true);

    }//GEN-LAST:event_btLoadActionPerformed
    /**
     * This method is used to play the detected file. The selected file will be
     * played
     *
     * @param evt
     */
    private void btPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPlayActionPerformed
        if (isNotPlaying) { //If another videos is not playing
            mp.process(selectFile); //The selected whole file will be added for audio and video processing
            playWindow = new NormalPlayWindow(mp.getJp());// The processed viedo(Detected and Blurred) will be added to the JFrame to be played
            jdpViewPort.add(playWindow);// The added JFrame will be added to the Desktop frame "Player"
            playWindow.setTitle("Player");// The video frame title
            playWindow.show();//show the player. Set the player to be visible

           

            // The video will be directly closed and all the work will be stopped
            playWindow.addInternalFrameListener(new InternalFrameAdapter() {

                @Override
                public void internalFrameClosed(InternalFrameEvent e) {

                    mp.stop();

                    JInternalFrame[] allFrames = MainWindow.this.jdpViewPort.getAllFrames();
                    for (JInternalFrame jInternalFrame : allFrames) {
                        try {
                            jInternalFrame.setClosed(true);
                        } catch (PropertyVetoException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    super.internalFrameClosed(e);
                }

                @Override
                public void internalFrameClosing(InternalFrameEvent e) {
                    super.internalFrameClosing(e);
                }

            });
            btPlay.setText("Stop");
            isNotPlaying = false;

            btSkinDetection.setEnabled(true);
        } else {
            btPlay.setText("Play");
            isNotPlaying = true;

            try {
                playWindow.setClosed(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }

            btSkinDetection.setEnabled(false);
            window.jlSkinCount.setModel(new DefaultListModel());

        }


    }//GEN-LAST:event_btPlayActionPerformed

    
    private void btSkinDetectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSkinDetectionActionPerformed
        VideoProcessor videoProcessor = new VideoProcessor();// a VideoProcessor object is initialized
        mp.applyVideoProcessor(videoProcessor); //the VideoProcessor object is passed to the MainProcessor
    }//GEN-LAST:event_btSkinDetectionActionPerformed

    private void onKeyPress(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_onKeyPress
        if (mp != null) {
            mp.onKeyAction(evt);
        }
    }//GEN-LAST:event_onKeyPress

    private static MainWindow window;

    /**
     * Get Skin Result Showing window
     *
     * @param number
     * @param percentage
     */
    public static void addSkinCountForFrame(String number, String percentage) {

        // Get Currunt Model
        ListModel model = window.jlSkinCount.getModel();

        //For Add List Model
        DefaultListModel dlm = new DefaultListModel();

        for (int i = 0; i < model.getSize(); i++) {

            //Add Element to List Model
            dlm.addElement(model.getElementAt(i));
        }

        //Set List Model
        dlm.addElement("Frame : " + number + " => " + percentage);

        //Set Default Model 
        window.jlSkinCount.setModel(dlm);
    }

    /**
     * Add New Internal Frame to Desktop Pane
     *
     * @param internalFrame
     */
    public static void addNewInternalFrame(JInternalFrame internalFrame) {

        if (window != null && window.jdpViewPort != null) {
            window.jdpViewPort.add(internalFrame);
            internalFrame.setVisible(true);
        }

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btLoad;
    private javax.swing.JButton btPlay;
    private javax.swing.JButton btSkinDetection;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JDesktopPane jdpViewPort;
    private javax.swing.JFileChooser jfcSelectFiles;
    private javax.swing.JList jlSkinCount;
    private javax.swing.JLabel lbFilePath;
    // End of variables declaration//GEN-END:variables
}
