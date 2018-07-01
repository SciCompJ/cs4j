/**
 * 
 */
package net.sci.image.io;

import java.io.IOException;

import net.sci.image.Image;

/**
 * Interface for writing an image to a file.
 * 
 * Typical expected usage:
 * <code><pre>
 * Image image = ...
 * File file = ...
 * ImageWriter writer = new XXXImageWriter(file);
 * writer.writeImage(image);
 * writer.close();
 * </pre></code>
 * 
 * @author dlegland
 *
 */
public interface ImageWriter
{
	/**
	 * Writes the content of an image into the specified writer.
	 * 
	 * @param image
	 *            the image to write
	 * @throws IOException
	 *             if a problem occurred during image writing
	 */
	public void writeImage(Image image) throws IOException;
}
