/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videocensoring.platform.main.core.featureextract.video;

import com.videocensoring.platform.main.core.MainProcessor;
import com.videocensoring.platform.main.core.featureextract.video.pixel.HSVSkinPixelDetector;
import com.videocensoring.platform.main.userinterface.MainWindow;
import com.videocensoring.platform.main.userinterface.ResultPanel;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openimaj.image.DisplayUtilities.ImageComponent;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.analysis.algorithm.histogram.HistogramAnalyser;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processor.ImageProcessor;
import org.openimaj.image.processor.PixelProcessor;
import org.openimaj.math.statistics.distribution.Histogram;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;

/**
 * All the processed frames which are going to be displayed are processed in
 * this class
 *
 * @author Kushan
 */
public class VideoProcessor implements VideoDisplayListener<MBFImage> {

    private float thresholdValue = 0.3f;
    private int dialateBoxValue;
    private MBFImage firstImage;

    private final ResultPanel newInstance = ResultPanel.getNewInstance("HSV Detection");
    private BufferedImage buffImage;
    private final ImageComponent imgComponent;

    public VideoProcessor() {
        MainWindow.addNewInternalFrame(newInstance);
        //frame.setVisible(true);
        imgComponent = new ImageComponent();
        newInstance.getContentPane().add(imgComponent);
        newInstance.setLocation(500, 0);
    }

    @Override
    public void afterUpdate(VideoDisplay<MBFImage> display) {
    }

    /**
     * This method will extract the video into frames and analyze these frames
     * to detect content in it
     *
     * @param extractedFrame is the extracted extractedFrame
     */
    @Override
    public synchronized void beforeUpdate(MBFImage extractedFrame) {
        File file = new File("data/" + MainProcessor.getCurruntVideoPath());
        file.mkdirs();

        try {
            ImageUtilities.write(extractedFrame, new File(file, Calendar.getInstance().getTimeInMillis() + ".png"));
        } catch (IOException ex) {
            Logger.getLogger(VideoProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }

        MBFImage clone = extractedFrame.clone();//A clone of the extractedFrame is formed
        synchronized (extractedFrame) {// the extractedFrame is sysnchronized and locked so that another thread cannot access it
            FImage greyFrame = null;
            //greyFrame = motionDetection(extractedFrame);
            processImage(extractedFrame, greyFrame);
        }
        firstImage = clone;

        MBFImage bFImage = clone.clone();
        bFImage.processInplace(new PixelProcessor<Float[]>() {
            @Override
            public Float[] processPixel(Float[] pixel) {
                return RGBColour.WHITE;
            }
        });
        //Clone Frame Conver to HSV for draw Histogram
        clone = Transforms.RGB_TO_HSV(clone);

        //Create Histogram for HSV
        plotHisto(bFImage, clone.bands.get(0), RGBColour.RED);
        plotHisto(bFImage, clone.bands.get(1), RGBColour.GREEN);
        plotHisto(bFImage, clone.bands.get(2), RGBColour.BLUE);

        //Create Bufferd image for Display Histogram
        BufferedImage bufferedImage = ImageUtilities.createBufferedImageForDisplay(bFImage, buffImage);
        if (bufferedImage != null) {
            imgComponent.setImage(bufferedImage);
        }
        imgComponent.setSize(bFImage.getWidth(), bFImage.getHeight());
        imgComponent.setPreferredSize(new Dimension(imgComponent.getWidth(), imgComponent.getHeight()));
        newInstance.setSize(new Dimension(imgComponent.getWidth() + 50, imgComponent.getHeight() + 50));
        newInstance.setResizable(false);

    }

    public void plotHisto(MBFImage img, FImage img2, Float[] colour) {
        // The number of bins is set to the image width here, but
        // we could specify a specific amount here.
        int nBins = img.getWidth();

        // Calculate the histogram
        Histogram h = HistogramAnalyser.getHistogram(img2, nBins);

        // Find the maximum so we can scale the bins
        double max = h.max();

        // Work out how fat to draw the lines.
        double lineWidth = img.getWidth() / nBins;

        // Now draw all the bins.
        int x = 0;
        for (double d : h.getVector()) {
            img.drawLine(
                    x, img.getHeight(), x,
                    img.getHeight() - (int) (d * 50 / max * img.getHeight()),
                    (int) lineWidth, colour);
            x += lineWidth;
        }
    }

    /**
     * The skin will be detected using this method
     *
     * @param in is a MBFImage. IT is the current frame which is to be processed
     * next
     * @param helper is the previous image which is already processed. That
     * frame is already passed to the video stream.
     */
    public void processImage(final MBFImage in, FImage helper) {
        //The color space is checked whether the color space is RGB or RGBA
        if (in.colourSpace != ColourSpace.RGB && in.colourSpace != ColourSpace.RGBA) {
            throw new IllegalArgumentException("RGB or RGBA colourspace is required");// If the frame is not a RGB image and exception is thrown
        }

        //Processing the pixels of the image
        if (firstImage != null) { //firstImage is the previous image, the previous cannot be null.
            final HSVSkinPixelDetector pixelProcessor = new HSVSkinPixelDetector(firstImage);//The HSVSkinPixels Detector is initialized
            // final YCbCrSkinPixelDetector pixelProcessor = new YCbCrSkinPixelDetector(firstImage);

            //  in.subtractInplace(firstImage);
            in.processInplace((ImageProcessor<MBFImage>) pixelProcessor);// the pixelProcessor object is passed to the processInplace method 

        }

        //in.processInplace((PixelProcessor<Float[]>) new HSVSkinPixelDetector());
        // in.processInplace(new YCbCrSkinPixelDetector());
    }

}
