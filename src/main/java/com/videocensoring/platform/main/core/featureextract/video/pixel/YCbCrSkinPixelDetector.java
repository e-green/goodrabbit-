/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videocensoring.platform.main.core.featureextract.video.pixel;

import com.videocensoring.platform.main.core.image.color.ColorConverters;
import com.videocensoring.platform.main.userinterface.MainWindow;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.pixel.Pixel;
import org.openimaj.image.processing.algorithm.FilterSupport;
import org.openimaj.image.processing.algorithm.MedianFilter;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.image.processor.ImageProcessor;
import org.openimaj.image.processor.PixelProcessor;
import org.openimaj.math.geometry.shape.Polygon;
import org.openimaj.math.geometry.shape.Rectangle;

/**
 *
 *
 * YCbCr color space based Skin detector
 *
 * @author Kushan
 */
public class YCbCrSkinPixelDetector implements PixelProcessor<Float[]>, ImageProcessor<MBFImage> {

    private MBFImage previouseImage = null;
    private double skinPresentage = 0;
    private int skincount = 0;
    private boolean isSkin = false;
    private List<Pixel> pixels = new ArrayList<Pixel>();

    // private static final CreateInternalWindow<FImage> CIW_GREYIMAGE = new CreateInternalWindow<FImage>("Grey Image", 250, 0);
    private static int frameCount = 0;

    public YCbCrSkinPixelDetector(MBFImage firstImage) {
        this.previouseImage = firstImage;
    }

    @Override
    public Float[] processPixel(Float[] pixel) {

        Float[] pOut = new Float[3];

        ColorConverters.RGB_TO_YCBCR(pixel, pOut);

        float Y = pOut[0];
        float Cb = pOut[1];
        float Cr = pOut[2];

        if (Y < 80 && 80 < Cb && Cb < 120 && 133 < Cr && Cr < 173) {
            isSkin = true;
        }

        return pixel;
    }

    /**
     *
     * Process Frame and detect it has nude Images
     *
     *
     * @param image
     */
    @Override
    public void processImage(MBFImage image) {

        List<Polygon> facesList = new CopyOnWriteArrayList<>();

        //Create Grey Image
        FImage redBand = image.getBand(0).clone();
        //Create Grey Image
        FImage greyImage = Transforms.calculateIntensityNTSC_LUT(image);
        FImage greyPreImage = Transforms.calculateIntensityNTSC_LUT(previouseImage);

        //<editor-fold defaultstate="collapsed" desc="Get Thresh values from Previsoue And Currunt frame">
        greyImage = greyImage.threshold(0.5f);
        greyPreImage = greyPreImage.threshold(0.5f);
//</editor-fold>
        //Calculate Diffent for find active reagion
        // Front Face Detection
        FKEFaceDetector det2 = new FKEFaceDetector();
        List<KEDetectedFace> faces = det2.detectFaces(redBand);

        // Extracted Faces add to Faces List
        for (KEDetectedFace detectedFace : faces) {
            redBand.drawShape(detectedFace.getShape(), 1f);
            facesList.add(detectedFace.getBounds().asPolygon());
        }

        //Cascade Detector for Detection
        HaarCascadeDetector det1 = null;

        //Detect Face Alternative 1
        det1 = HaarCascadeDetector.BuiltInCascade.frontalface_alt.load();
        List<Rectangle> detect = det1.getDetector().detect(redBand);

        // Extracted Faces add to Faces List
        for (Rectangle rectangle : detect) {
            redBand.drawShape(rectangle, 1f);
            facesList.add(rectangle.asPolygon());
        }

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                //Detect Pixels Are skin or not
                image.setPixel(x, y, processPixel(image.getPixel(x, y)));

                //If pixel is skin remember them for 
                if (isSkin) {
                    Pixel pixel = new Pixel(x, y);
                    pixels.add(pixel);

                    //Increase Skin count
                    skincount += 1;
                }
                //Set isSkin as false
                isSkin = false;
            }
        }

        //Calculate Skin Presentage
        skinPresentage = (skincount * 100) / (image.getCols() * image.getRows());
        //Display Skin count
        MainWindow.addSkinCountForFrame(frameCount + "", skinPresentage + "%");
        frameCount++;

        // Create Clone image for Create Blur image
        MBFImage blurImage = image.clone();

        //Create Kernal for blur
        MedianFilter filter = new MedianFilter(FilterSupport.createBlockSupport(25, 25));
        // Blur image using above kernal
        blurImage.processInplace(filter);

        //System.out.println(skinPresentage);
        //Detect and display skin region
        if (skinPresentage > 10) {
            for (final Pixel pixle : pixels) {
                boolean inside = false;

                // Check pixel is on Face List Or not
                for (Polygon polygon : facesList) {
                    inside = polygon.isInside(pixle);
                    if (inside) {
                        break;
                    }
                }

                // Apply Blur pixel if pixle not in Face region
                if (!inside) {
                    Float[] pixel = blurImage.getPixel(pixle.x, pixle.y);
                    image.setPixel(pixle.x, pixle.y, pixel);
                }

            }
        }

    }

}
