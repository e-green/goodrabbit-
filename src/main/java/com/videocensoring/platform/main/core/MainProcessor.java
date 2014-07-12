/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videocensoring.platform.main.core;

import com.videocensoring.platform.main.userinterface.MainWindow;
import com.videocensoring.platform.main.userinterface.ResultPanel;
import org.openimaj.audio.AudioEventAdapter;
import org.openimaj.audio.AudioEventListener;
import org.openimaj.audio.AudioPlayer;
import org.openimaj.audio.SampleChunk;
import org.openimaj.audio.features.MFCC;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.video.Video;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.xuggle.XuggleAudio;
import org.openimaj.video.xuggle.XuggleVideo;
import org.openimaj.vis.audio.AudioWaveform;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kushan All the analyzing is done in the MainProcessor
 *
 */
public class MainProcessor {

    private static Document doc;

    private Video<MBFImage> video;

    private XuggleAudio audioStream;
    private JPanel jp;
    private AudioPlayer audioPlayer;
    private boolean saveAudioSample = false;
    private AudioWaveform vis;

    //MFCC extracted Data Featured of the stored samples
    private static List<List<double[]>> audioData;
    private static int noDetectCounts[];
    private static int detectionCounts[];

//    private static int noDetectedCount = 0;
//    private static int detection_count = 0;
    //
    private VideoDisplay<MBFImage> display;

    //
    private static String path;

    public MainProcessor() {

    }

    public void process(File selectFile) {

        path = selectFile.getName() + "_" + Calendar.getInstance().getTimeInMillis();

        jp = new JPanel(new GridLayout());
        video = new XuggleVideo(selectFile);
        audioStream = new XuggleAudio(selectFile);
        jp.setSize(video.getWidth(), video.getHeight());

        display = VideoDisplay.createVideoDisplay(video, jp);
        vis = new AudioWaveform(400, 400);
        vis.setVisible(false);

        final ResultPanel newInstance = ResultPanel.getNewInstance("HSV Detection");
        final BufferedImage buffImage = null;
        final DisplayUtilities.ImageComponent imgComponent;
        MainWindow.addNewInternalFrame(newInstance);

        //frame.setVisible(true);
        imgComponent = new DisplayUtilities.ImageComponent();
        newInstance.getContentPane().add(imgComponent);
        newInstance.setLocation(500, 0);

        audioPlayer = new AudioPlayer(audioStream);

        //display.seek(100);
        display.setTimeKeeper(audioPlayer);
        new Thread(audioPlayer).start();

        try {
            audioData = FileUser.getXML();
            //
            noDetectCounts = new int[audioData.size()];
            detectionCounts = new int[audioData.size()];

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(MainProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(MainProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Set Audio Player Event Listner For Detect Words
        // Apply Listner Design pattern (Observer Design pattern)
        audioPlayer.addAudioEventListener(new AudioEventAdapter() {

            public void beforePlay(SampleChunk sc) {

                MFCC mfcc = new MFCC();// Create MFCC object
                double[][] calculateMFCC = mfcc.calculateMFCC(sc.getSampleBuffer());
                //     synchronized (calculateMFCC) {
                // check Featureas
                boolean check = check(calculateMFCC);
                if (check) {
                    sc.setSamples(new byte[sc.getNumberOfSamples()]);// Censoring Audio 
                }

//<editor-fold defaultstate="collapsed" desc="Train system">
//                    try {
//                        FileUser.saveXML(Arrays.toString(calculateMFCC[0]), false);
//                        
//                    } catch (TransformerException ex) {
//                        Logger.getLogger(MainProcessor.class.getName()).log(Level.SEVERE, null, ex);
//                    } catch (ParserConfigurationException ex) {
//                        Logger.getLogger(MainProcessor.class.getName()).log(Level.SEVERE, null, ex);
//                    } catch (IOException ex) {
//                        Logger.getLogger(MainProcessor.class.getName()).log(Level.SEVERE, null, ex);
//                    } catch (SAXException ex) {
//                        Logger.getLogger(MainProcessor.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//</editor-fold>
                //   }
                vis.setData(sc.getSampleBuffer());
                MBFImage bFImage = vis.getVisualisationImage();
                BufferedImage bufferedImage = ImageUtilities.createBufferedImageForDisplay(bFImage, buffImage);
                if (bufferedImage != null) {
                    imgComponent.setImage(bufferedImage);
                }
                imgComponent.setSize(bFImage.getWidth(), bFImage.getHeight());
                imgComponent.setPreferredSize(new Dimension(imgComponent.getWidth(), imgComponent.getHeight()));
                newInstance.setSize(new Dimension(imgComponent.getWidth() + 50, imgComponent.getHeight() + 50));
                newInstance.setResizable(false);

            }

            public void afterPlay(AudioPlayer ap, SampleChunk sc) {

            }
        });

    }

    /**
     *
     * Check Audio Sample Is Detected Or Not
     *
     * @param samples
     * @return
     */
    private static synchronized boolean check(final double[][] samples) {

        boolean audioDetected = false;

        L1:
        for (int j = 0; j < audioData.size(); j++) {

            int detection_count = detectionCounts[j];
            int noDetectedCount = noDetectCounts[j];

            boolean okToNext = false;

            double[] checkValues = null;//Create Array to get Audio Frame Values to be check

            if (audioData.get(j).size() > detection_count) {// get checkValues if audioData double array size grater than detection count
                checkValues = audioData.get(j).get(detection_count);// Get checkValues from audioData double array
            } else {
                detection_count = 0;// Assign 0 if audioData double array size is less than detection count
                checkValues = audioData.get(j).get(detection_count);// Get checkValues from audioData double array
            }

            double values[] = samples[0];//Get The current sample from the original audio track to be detected to an array

            int okCount = 0;//number of compared and detected array elements

            for (int i = 0; i < values.length; i++) {
                if (i < checkValues.length) {

                    double valueWhichFromMFCCArray = values[i];// Get Sample MFCC Feature Array element 
                    double valueWichStoredMFCCArray = checkValues[i]; // Get Stored MFCC featured array element

                    double diff = valueWhichFromMFCCArray - valueWichStoredMFCCArray;// Get Different between stored value and sample value

                    //Convert less than 0 values to grater than 0 values 
                    if (diff < 0) {
                        diff *= -1;
                    }

                    if (diff < 4) {
                        okCount++;// If diff less than 4 detect the samples are matched
                    }
                }
            }

            double range = (okCount * 100) / values.length;// The percentage of the sucessfully compared values 

            System.out.print(range + " =>");

            if (range > 90) {
                okToNext = true;// If the precentage is grater than 90 okToNext is set to true we can process the next frame
            }

            System.out.println((noDetectedCount * 100 / audioData.get(j).size()));
            if (okToNext) {// If okToNext true then increase detection_count by one
                detection_count++;
                System.out.println("Detected");
            } else if ((noDetectedCount * 100 / audioData.get(j).size()) > 40) { // If noDetectedCount count is greater than 40 reset cheking frames
                //If the sample chunk cannot be matched with the input audio track process will start again with another frame
                detection_count = 0;//Set reset values detection_count=0
                noDetectedCount = 0;//Set reset values noDetectedCount=0
                // It is set to 0 to stop the process and go to the next frame
                System.out.println("Not Detected");

            } else {
                noDetectedCount++;// If okToNext false then increase noDetectedCount by one
            }

            detectionCounts[j] = detection_count;
            noDetectCounts[j] = noDetectedCount;

            System.out.println(((detection_count * 100) / audioData.get(j).size()));

            if (((detection_count * 100) / audioData.get(j).size()) > 60) {
                System.out.println("Audio Sample Detected");
                audioDetected = true;
                break L1;
                //return true;
            } else {
                System.out.println("Audio Sample "
                        + "Not Detected");
                audioDetected = false;
                //return false;
            }

        }
        return audioDetected;
    }

    public JPanel getJp() {
        return jp;
    }

    public Video<MBFImage> getVideo() {
        return video;
    }

    public VideoDisplay<MBFImage> getDisplay() {
        return display;
    }

    public XuggleAudio getAudioStream() {
        return audioStream;
    }

    public void onKeyAction(KeyEvent ke) {
        switch (ke.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                saveAudioSample = true;
                break;
            default:

        }
    }

    public void applyVideoProcessor(VideoDisplayListener<MBFImage> imageProcessor) {
        // Apply Observer Design pattern
        display.addVideoListener(imageProcessor);
    }

    /**
     * Set Audio Player Event Listeners
     *
     * @param ap
     */
    public void applyAudioProcessor(AudioEventListener ap) {
        //  audioPlayer.addAudioEventListener(ap);
    }

    public void stop() {
        this.display.setMode(VideoDisplay.Mode.STOP);
        this.audioPlayer.setMode(AudioPlayer.Mode.STOP);
        this.vis.setVisible(false);
        try {
            this.finalize();
        } catch (Throwable ex) {
            Logger.getLogger(MainProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getCurruntVideoPath() {
        return path;
    }

}
