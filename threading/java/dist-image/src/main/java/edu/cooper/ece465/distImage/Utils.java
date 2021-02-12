package edu.cooper.ece465.distImage;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.common.IBufferedImageFactory;

public class Utils {
    public static BufferedImage imageReadExample(final File file)
            throws ImageReadException, IOException {
        final Map<String, Object> params = new HashMap<String, Object>();

        // set optional parameters if you like
        params.put(ImagingConstants.BUFFERED_IMAGE_FACTORY,
                new ManagedImageBufferedImageFactory());

        // params.put(ImagingConstants.PARAM_KEY_VERBOSE, Boolean.TRUE);

        // read image
        final BufferedImage image = Imaging.getBufferedImage(file, params);

        return image;
    }

    public static class ManagedImageBufferedImageFactory implements
            IBufferedImageFactory {

        public BufferedImage getColorBufferedImage(final int width, final int height,
                                                   final boolean hasAlpha) {
            final GraphicsEnvironment ge = GraphicsEnvironment
                    .getLocalGraphicsEnvironment();
            final GraphicsDevice gd = ge.getDefaultScreenDevice();
            final GraphicsConfiguration gc = gd.getDefaultConfiguration();
            return gc.createCompatibleImage(width, height,
                    Transparency.TRANSLUCENT);
        }

        public BufferedImage getGrayscaleBufferedImage(final int width, final int height,
                                                       final boolean hasAlpha) {
            return getColorBufferedImage(width, height, hasAlpha);
        }
    }

}