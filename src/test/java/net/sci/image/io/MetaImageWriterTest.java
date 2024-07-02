/**
 * 
 */
package net.sci.image.io;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array2D;
import net.sci.image.Image;

/**
 * @author dlegland
 *
 */
public class MetaImageWriterTest
{

	/**
	 * Test method for {@link net.sci.image.io.MetaImageWriter#writeImage(net.sci.image.Image)}.
	 * @throws IOException 
	 */
	@Test
	public void testWriteImage() throws IOException
	{
		UInt8Array2D array = UInt8Array2D.create(10, 8);
		Image image = new Image(array);
		
		File outputFile = new File("testWriteMHD.mhd");
		
		MetaImageWriter writer = new MetaImageWriter(outputFile);
		writer.writeImage(image);
			
		outputFile.delete();
		File rawFile = new File("testWriteMHD.raw");
		rawFile.delete();
		
	}

}
