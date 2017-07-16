/**
 * 
 */
package net.sci.image.io;

import java.io.File;
import java.io.IOException;

import net.sci.image.Image;

/**
 * Interface for writing an image to a file.
 * 
 * @author dlegland
 *
 */
public interface ImageWriter
{
	/**
	 * Writes the content of an image into the specified file.
	 * 
	 * @param image
	 *            the image to write
	 * @param file
	 *            the file to write the image in.
	 * @throws IOException
	 *             if a problem occurred during image writing
	 */
	public void writeImage(Image image, File file) throws IOException;
}
