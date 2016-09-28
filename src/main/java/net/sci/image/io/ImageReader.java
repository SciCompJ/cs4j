/**
 * 
 */
package net.sci.image.io;

import java.io.IOException;

import net.sci.image.Image;

/**
 * @author dlegland
 *
 */
public interface ImageReader
{
	public Image readImage() throws IOException;
}
