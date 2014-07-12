/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videocensoring.platform.main.userinterface;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;

/**
 *
 * @author Kushan
 */
public class CreateInternalWindow<T extends FImage> {

    private final ResultPanel newInstance;
    private BufferedImage bufferedImage;
    private final DisplayUtilities.ImageComponent imgComponent;

    
    /**
     * 
     * Builder Design Pattern
     * 
     * @param name
     * @param x
     * @param y 
     */
    public CreateInternalWindow(String name, int x, int y) {
        newInstance = ResultPanel.getNewInstance(name);
        MainWindow.addNewInternalFrame(newInstance);

        //frame.setVisible(true);
        imgComponent = new DisplayUtilities.ImageComponent();
        newInstance.getContentPane().add(imgComponent);
        newInstance.setLocation(x, y);
    }
    
    

    public final void updateView(T bFImage) {
        bufferedImage = ImageUtilities.createBufferedImageForDisplay(bFImage, bufferedImage);
        if (bufferedImage != null) {
            imgComponent.setImage(bufferedImage);
        }
        imgComponent.setSize(bFImage.getWidth(), bFImage.getHeight());
        imgComponent.setPreferredSize(new Dimension(imgComponent.getWidth(), imgComponent.getHeight()));
        newInstance.setSize(new Dimension(imgComponent.getWidth() + 50, imgComponent.getHeight() + 50));
        newInstance.setResizable(false);       
    }

}
