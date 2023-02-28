/**
 * 
 */
package net.sci.image.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.sci.array.Array;
import net.sci.image.Image;

/**
 * Writes a planar image into a common image file format managed by the ImageIO
 * package.
 * 
 * @author dlegland
 *
 */
public class ImageIOImageWriter implements ImageWriter
{
    File file = null;
    
	/**
	 * public constructor.
	 */
	public ImageIOImageWriter( File file)
	{
	    this.file = file;
	}

	/* (non-Javadoc)
	 * @see net.sci.image.io.ImageWriter#writeImage(net.sci.image.Image)
	 */
	@Override
	public void writeImage(Image image) throws IOException
	{
		Array<?> array = image.getData();
		if (array.dimensionality() > 2)
		{
			throw new IllegalArgumentException("Requires an array of dimensionality 2");
		}
		
		// convert to buffered image
		BufferedImage bufImg = image.getType().createAwtImage(image);
		
		String format = formatFromFile(file);
		ImageIO.write(bufImg, format, file);
	}
	
	private String formatFromFile(File file)
	{
		String fileName = file.getName();
		if (fileName.endsWith(".png")) 
			return "png";
		if (fileName.endsWith(".gif")) 
			return "gif";
		if (fileName.endsWith(".bmp")) 
			return "bmp";
		if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) 
			return "jpg";
		throw new RuntimeException("Unable to determine format from file name: " + fileName);
	}

}
