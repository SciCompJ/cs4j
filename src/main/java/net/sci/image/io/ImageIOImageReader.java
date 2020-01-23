/**
 * 
 */
package net.sci.image.io;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.sci.array.Array;
import net.sci.array.color.RGB8;
import net.sci.array.color.RGB8Array2D;
import net.sci.array.scalar.IntArray2D;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.image.Image;

/**
 * Encapsulate Java ImageIO into ImageReader interface.
 * 
 * @author dlegland
 *
 */
public class ImageIOImageReader implements ImageReader
{
	File file;

	/**
	 * 
	 */
	public ImageIOImageReader(File file) throws IOException
	{
		this.file = file;
	}

	/**
	 * 
	 */
	public ImageIOImageReader(String fileName) throws IOException
	{
		this.file = new File(fileName);;
	}

	/* (non-Javadoc)
	 * @see net.sci.image.io.ImageReader#readImage()
	 */
	@Override
	public Image readImage() throws IOException
	{
		BufferedImage bufImg = ImageIO.read(file);

		// Convert to Image class
		Image image = convertBufferedImage(bufImg);
		image.setName(file.getName());
		image.setFilePath(file.getPath());

		return image;
	}

	/**
	 * Converts an instance of BufferedImage to an Image.
	 * 
	 * @param bufImg
	 *            an instance of BufferedImage
	 * @return the converted image
	 */
	public static final Image convertBufferedImage(BufferedImage bufImg)
	{
		// Image dimension
		int width = bufImg.getWidth();
		int height = bufImg.getHeight();

		// get the raster, that contains data
		WritableRaster raster = bufImg.getRaster();

		int nc = raster.getNumBands();

		Array<?> array = null;
		if (nc == 1)
		{
			// Create new intensity image
			IntArray2D<?> intArray = UInt8Array2D.create(width, height);

			// Initialize image data with raster content
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					int value = raster.getSample(x, y, 0);
					intArray.setInt(value, x, y);
				}
			}
			array = intArray;

		} 
		else if (nc == 3 || nc == 4)
		{
			// Create new color image
			RGB8Array2D rgbArray = RGB8Array2D.create(width, height);

			// Initialize image data with raster content
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					int r = raster.getSample(x, y, 0);
					int g = raster.getSample(x, y, 1);
					int b = raster.getSample(x, y, 2);
					rgbArray.set(new RGB8(r, g, b), x, y);
				}
			}
			array = rgbArray;
		} 
		else
		{
			throw new RuntimeException(
					"Can not manage images with number of bands equal to " + nc);
		}

		// create the meta-image
		Image image = new Image(array);
		return image;
	}

}
