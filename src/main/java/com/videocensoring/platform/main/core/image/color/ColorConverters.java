/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videocensoring.platform.main.core.image.color;

/**
 *
 * @author Kushan
 */
public class ColorConverters {

    /**
     *
     * Convert Given Set of RGB values to HSV Color values and return them *
     *
     *
     * @param rgb
     * @param hsv
     * @return
     */
    public static Float[] RGB_TO_HSV(final Float[] rgb, final Float[] hsv) {
        float H, S, V;

        final float R = rgb[0];
        final float G = rgb[1];
        final float B = rgb[2];

        // Blue Is the dominant color
        if ((B > G) && (B > R)) {
            // Value is set as the dominant color
            V = B;
            if (V != 0) {
                float min;
                if (R > G) {
                    min = G;
                } else {
                    min = R;
                }

                // Delta is the difference between the most dominant
                // color
                // and the least dominant color. This will be used to
                // compute saturation.
                final float delta = V - min;
                if (delta != 0) {
                    S = (delta / V);
                    H = 4 + (R - G) / delta;
                } else {
                    S = 0;
                    H = 4 + (R - G);
                }

                // Hue is just the difference between the two least
                // dominant
                // colors offset by the dominant color. That is, here 4
                // puts
                // hue in the blue range. Then red and green just tug it
                // one
                // way or the other. Notice if red and green are equal,
                // hue
                // will stick squarely on blue
                H *= 60;
                if (H < 0) {
                    H += 360;
                }

                H /= 360;
            } else {
                S = 0;
                H = 0;
            }
        } // Green is the dominant color
        else if (G > R) {
            V = G;
            if (V != 0) {
                float min;
                if (R > B) {
                    min = B;
                } else {
                    min = R;
                }

                final float delta = V - min;

                if (delta != 0) {
                    S = (delta / V);
                    H = 2 + (B - R) / delta;
                } else {
                    S = 0;
                    H = 2 + (B - R);
                }
                H *= 60;
                if (H < 0) {
                    H += 360;
                }

                H /= 360;
            } else {
                S = 0;
                H = 0;
            }
        } // Red is the dominant color
        else {
            V = R;
            if (V != 0) {
                float min;
                if (G > B) {
                    min = B;
                } else {
                    min = G;
                }

                final float delta = V - min;
                if (delta != 0) {
                    S = (delta / V);
                    H = (G - B) / delta;
                } else {
                    S = 0;
                    H = (G - B);
                }
                H *= 60;

                if (H < 0) {
                    H += 360;
                }
                H /= 360;
            } else {
                S = 0;
                H = 0;
            }
        }

        hsv[0] = H;
        hsv[1] = S;
        hsv[2] = V;

        return hsv;
    }

    /**
     *
     * Convert Given Set of RGB values to YCbCr Color values and return them
     *
     * @param rgb
     * @param ycbcr
     * @return
     */
    public static Float[] RGB_TO_YCBCR(Float[] rgb, Float[] ycbcr) {
        float r = rgb[0] * 255;
        float g = rgb[1] * 255;
        float b = rgb[2] * 255;

        float y = (float) (0.299 * r + 0.587 * g + 0.114 * b);

        float cb = (float) (-0.16874 * r - 0.33126 * g + 0.50000 * b);
        cb += 128;

        float cr = (float) (0.50000 * r - 0.41869 * g - 0.08131 * b);
        cr += 128;

        ycbcr[0] = y;

        ycbcr[1] = cb;

        ycbcr[2] = cr;
        // System.out.println(Arrays.toString(ycbcr));

        return ycbcr;

    }
}
