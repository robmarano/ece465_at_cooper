package edu.cooper.ece465.imaging;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

public class Util {
    public static BufferedImage imageReadExample(final File file)
            throws ImageReadException, IOException {
        // Simple read without custom factory, compatible with modern commons-imaging
        return Imaging.getBufferedImage(file);
    }
}