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
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.pixel.Pixel;
import org.openimaj.image.processing.algorithm.FilterSupport;
import org.openimaj.image.processing.algorithm.MedianFilter;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.image.processing.morphology.Dilate;
import org.openimaj.image.processing.morphology.Erode;
import org.openimaj.image.processing.morphology.StructuringElement;
import org.openimaj.image.processor.ImageProcessor;
import org.openimaj.image.processor.PixelProcessor;
import org.openimaj.math.geometry.shape.Polygon;
import org.openimaj.math.geometry.shape.Rectangle;

/**
 *
 *
 * HSV color space based Skin detector All the pixels in the frame are read from
 * pixel to pixel in this class, THis class is implemented from two interfaces,
 * PixelProcessor and ImageProcessor
 *
 * @author Kushan
 */
public class HSVSkinPixelDetector implements PixelProcessor<Float[]>, ImageProcessor<MBFImage> {

    private MBFImage previouseImage = null;
    private double skinPresentage = 0;
    private int skincount = 0;
    private boolean isSkin = false;
    private List<Pixel> pixels = new ArrayList<Pixel>();

    // private static final CreateInternalWindow<FImage> CIW_GREYIMAGE = new CreateInternalWindow<FImage>("Grey Image", 250, 0);
    private static int frameCount = 0;

    public HSVSkinPixelDetector(MBFImage previouseImage) {
        this.previouseImage = previouseImage;
        //System.out.println("Applying");
    }

    public HSVSkinPixelDetector() {
    }
    /**
     * The passed pixel is converted into HSV color space to check whether it is a skin color pixel or not
     * if it is a skin color pixels it is set to true if not it is passed anyway. Even it it is a skin color pixel 
     * or not it is passed. This method only checks whether the pixel is skin colored
     * @param pixel is an input pixel parameter. Process the input parameter pixel
     * @return the original pixels is returned
     */
    @Override
    public Float[] processPixel(Float[] pixel) {
        
        //Instantiate a "out" array of the FLOat type, to return an output
        Float[] out = new Float[3];
        //Convert RGB Pixel To HSV
        ColorConverters.RGB_TO_HSV(pixel, out);
        float Hv = out[0];// The converted HV values is set to the first element of the array
        float Sv = out[1];
        float Vv = out[2];

        //Check HSV values are under skin color range or not, if it on range then set isSkin true
        if (Hv < 0.25 && 0.15 < Sv && Sv < 0.9 && 0.2 < Vv && Vv < 0.95) {
            //pixel = RGBColour.WHITE;
            isSkin = true;// If the processed pixel is detected as a skin then isSkin = true
        }

        
        //the original pixel is returned.
        return pixel;
    }

    /**
     *
     * Process each Frame and detect whether it has nude Images
     * This method is overridden because because of the ImageProcessing Interface. The processImage method
     * is a method available in the ImageProcessing interface.
     *
     *
     * @param image
     */
    @Override
    public void processImage(MBFImage image) {
        
        //a new arrayList is created to store the pixels detected in the face region. The pixels detected as face pixels
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
        //Calculate Diffent for find active region
        //Front Face Detection
        FKEFaceDetector det2 = new FKEFaceDetector();
        // The face region coordinates in the R layer of the RGB frame are detected and stored to the "faces" list 
        List<KEDetectedFace> faces = det2.detectFaces(redBand);

        // Extracted Faces add to Faces List"facesList"
        for (KEDetectedFace detectedFace : faces) { // The detected faces list"faces"  which are detected from the FKE is added to the "detectedFace" list
            image.drawShape(detectedFace.getShape(), RGBColour.WHITE);// show the selected face region in WHITE
            facesList.add(detectedFace.getBounds().asPolygon());// the Polygons(face region) are added to the faceList
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

        // The system reads pixel by pixel of the input frame
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                //Detect whether Pixels Are skin or not
                processPixel(image.getPixel(x, y));// the co-ordinates of the input image are passed to the processPixel method to check whetther the pixels are skin or not

                //If pixel is skin remember them for 
                if (isSkin) {
                    Pixel pixel = new Pixel(x, y); //a pixel object is instantiated to detect the x,y coordinates of the located position of the pixel  
                    pixels.add(pixel);// the pixel object made to get the coordinates of the pixel is added to the "pixels" array

                    //Increase Skin count
                    skincount += 1; // if it is a skin pixel that pixel is added to the skincount 
                }
                //Set isSkin as false
                isSkin = false;// then to be ready for the next pixel the isSkin is falsed. since the current pixel is already processed.
            }
        }

        //Calculate Skin Presentage
        skinPresentage = (skincount * 100) / (image.getCols() * image.getRows());
        //Display Skin count
        MainWindow.addSkinCountForFrame(frameCount + "", skinPresentage + "%");
        frameCount++;// to identify the number of frames To be shown in the frame count window. 1,2,3,......

        // Create Clone image for Create Blur image
        MBFImage blurImage = image.clone();
        
        /**
         * The obtained clone will be blurred and kept. Then the coordinates of the 
         * blurred pixels will be taken 
         * and set to the original image. 
         */

        //Create Kernal for blur        
        MedianFilter filter = new MedianFilter(FilterSupport.createBlockSupport(25, 25));
        // Blur image using above kernal
        blurImage.processInplace(filter);

        //System.out.println(skinPresentage);
        //Detect and display skin region
        if (skinPresentage > 10) {
            for (final Pixel pixle : pixels) { // The pixle array is read
                boolean inside = false;// inside is set false becaouse we still 
                                       //don't know whether the taken pixel is inside the face polygon

                // Check pixel is on Face List Or not
                for (Polygon polygon : facesList) { //the pixels are taken from the faceList
                    inside = polygon.isInside(pixle); //checks if the skin pixels are inside the polygon
                    if (inside) { 
                        break; //the for loop will be exited
                    }
                }

                // Apply Blur pixel if pixle not in Face region
                if (!inside) { //if the face pixels are not inside 
                    Float[] pixel = blurImage.getPixel(pixle.x, pixle.y);// the co-ordinates of the pixels are taken from the Blur cloned imaged
                    image.setPixel(pixle.x, pixle.y, pixel);//the blurred skin pixels are set to the original image accordingly.
                }

            }
        }

    }

    /**
     * Get Frame Skin percentage
     *
     * @return
     */
    public double getSkinPresentage() {
        return skinPresentage;
    }

    /**
     *
     * Detect Motions using comparing 2 frames
     *This method will detect the movement of an object using a Gray Image by comparing the previous frame and the 
     *current frame. The object will be detected separately from the background
     *
     * @param frame
     * @return
     */
    private FImage motionDetection(MBFImage frame) {
        MBFImage clone = frame.clone();// A clone of the MBFImage will be taken 
        FImage greyFrame = null;// a sinlge layer FImage instance is initialized for the current image

        //Convert Frame To Gray Scale Image
        greyFrame = Transforms.calculateIntensityNTSC(frame);

        //Working if previouse image is not null
        if (previouseImage != null) {

            FImage greyFrameLast = null;//a single layer FImage instance is initialized for the previous frame
            greyFrameLast = Transforms.calculateIntensityNTSC(previouseImage);// Converts the RGB frame of the previousImage to a gray frame(FImage)

            //Substract 2 Frames
            greyFrame = greyFrame
                    .threshold(0.7f).subtractInplace(greyFrameLast.threshold(0.7f));// The highlighted objects will be detected using the threshold values and the two frames will be subtracted
            
            // Morphing process apply to Subtracted Frames
            greyFrame.processInplace(new Dilate(StructuringElement.BOX));
            greyFrame.processInplace(new Erode(StructuringElement.BOX));

        }

        // This is the image which is saved for use as last frame in next frame
        previouseImage = clone;

        return greyFrame;
    }

}
