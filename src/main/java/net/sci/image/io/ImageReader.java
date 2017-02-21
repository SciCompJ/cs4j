/**
 * 
 */
package net.sci.image.io;

import java.io.IOException;

import net.sci.image.Image;

/**
 * Interface for reading an image from a file.
 * 
 * @author dlegland
 *
 */
public interface ImageReader
{
    /**
     * Reads an image.
     * 
     * @return a new Image instance
     * @throws IOException
     *             if there was a problem during image reading.
     */
	public Image readImage() throws IOException;
}
