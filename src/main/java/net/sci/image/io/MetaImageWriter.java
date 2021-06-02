/**
 * 
 */
package net.sci.image.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

import net.sci.array.Array;
import net.sci.array.scalar.Float32Array;
import net.sci.array.scalar.Float64Array;
import net.sci.array.scalar.Int16Array;
import net.sci.array.scalar.Int32Array;
import net.sci.array.scalar.UInt16Array;
import net.sci.array.scalar.UInt8Array;
import net.sci.image.Image;

/**
 * Writes image data as MetaImage file.
 * 
 * Example:<pre><code>
    UInt8Array2D data = UInt8Array2D.create(50, 20);
    data.populateValues((x,y) -> (double) y * 10.0 + x); 
    Image image = new Image(res2d);
        
    System.out.println("Save image...");
    MetaImageWriter writer = new MetaImageWriter(outputFileName);
    try
    {
        writer.writeImage(image);
    }
    catch(IOException ex)
    {
        System.err.println(ex);
        return;
    }
 * </code></pre>
 * References about MetaImage file format:
 * <ul>
 * <li> MetaIO Documentation (<a href="http://www.itk.org/Wiki/MetaIO/Documentation"> http://www.itk.org/Wiki/MetaIO/Documentation</a>) </li>
 * </ul>
 * 
 * @author dlegland
 */
public class MetaImageWriter implements ImageWriter
{
	File headerFile;
	
	MetaImageInfo info;
	
	/**
	 * Creates a new class for writing image data using MetaImage file format.
	 * 
	 * @param file the file to write the header.
	 */
	public MetaImageWriter(File file)
	{
		this.headerFile = file;
	}

	@Override
	public void writeImage(Image image) throws IOException
	{
		// prepare data
		this.info = computeMetaImageInfo(image);
		info.elementDataFile = computeElementDataFileName(this.headerFile.getName());

		// print header into header file
		FileOutputStream stream = new FileOutputStream(this.headerFile);
		writeHeader(stream);
		
		// if element data file is different, switch to the new file
		if (!info.elementDataFile.equals(this.headerFile.getName()))
		{
			stream.close();
			File dataFile = new File(this.headerFile.getParentFile(), info.elementDataFile);
			stream = new FileOutputStream(dataFile);
		}
		
		// write data
		writeImageData(image.getData(), stream);
		
		// close stream
		stream.close();
	}

    /**
     * Computes the meta-data to write into header file from the meta data
     * stored in the image.
     * 
     * @param image
     *            the image containing meta data.
     * @return meta-data to write into header file.
     */
	public MetaImageInfo computeMetaImageInfo(Image image)
	{
		MetaImageInfo info = new MetaImageInfo();
		
		// image dimension
		int nd = image.getDimension();
		info.nDims = nd;
		info.dimSize = new int[nd];
		System.arraycopy(image.getSize(), 0, info.dimSize, 0, nd);
		
		// determine element data type
		// TODO: include color / multi-channel types
		Array<?> array = image.getData();
		if (array instanceof UInt8Array)
		{
			info.elementType = MetaImageInfo.ElementType.UINT8;
		}
		else if (array instanceof UInt16Array)
		{
			info.elementType = MetaImageInfo.ElementType.UINT16;
		}
		else if (array instanceof Int16Array)
		{
			info.elementType = MetaImageInfo.ElementType.INT16;
		}
		else if (array instanceof Int32Array)
		{
			info.elementType = MetaImageInfo.ElementType.INT32;
		}
		else if (array instanceof Float32Array)
		{
			info.elementType = MetaImageInfo.ElementType.FLOAT32;
		}
		else if (array instanceof Float64Array)
		{
			info.elementType = MetaImageInfo.ElementType.FLOAT64;
		}
		else
		{
			throw new IllegalArgumentException("Unable to determine MetaImage Type for image containing data with class " + array.getClass());
		}
		
		return info;
	}
	
	private String computeElementDataFileName(String fileName)
	{
        int baseLength = fileName.length();
        String lowerFileName = fileName.toLowerCase();
        if (lowerFileName.endsWith(".mhd") || lowerFileName.endsWith(".mda"))
        {
            fileName = fileName.substring(0, baseLength - 4);
        }
        return fileName + ".raw";
	}
	
	/**
     * Writes the header into the specified stream.
     * 
     * @param stream
     *            the stream to write in.
     * @return the metaimage info.
     * @throws IOException
     *             if an exception occurs
     */
	private MetaImageInfo writeHeader(OutputStream stream) throws IOException
	{
		PrintStream ps = new PrintStream(stream);
		
		ps.printf(Locale.US, "%s = %s\n", "ObjectType", "Image");
		ps.printf(Locale.US, "%s = %d\n", "NDims", info.nDims);
		String dimString = Integer.toString(info.dimSize[0]);
		for (int d = 1; d < info.dimSize.length; d++)
		{
			dimString = dimString + " " + info.dimSize[d];
		}
		ps.printf(Locale.US, "%s = %s\n", "DimSize", dimString);
		ps.printf(Locale.US, "%s = %s\n", "ElementType", info.elementType.getMetString());

		// TODO: add optional info fields

		ps.printf(Locale.US, "%s = %s\n", "ElementDataFile", info.elementDataFile);
		
		return info;
	}
	
	private void writeImageData(Array<?> array, OutputStream stream) throws IOException
	{
		BufferedOutputStream bos = new BufferedOutputStream(stream);
		if (array instanceof UInt8Array)
		{
			UInt8Array array8 = (UInt8Array) array;
			for (int[] pos : array8.positions())
			{
				bos.write(array8.getByte(pos));
			}
		}
		else
		{
			throw new RuntimeException("Can not manage arays with class: " + array.getClass()); 
		}
		bos.flush();
	}
	
}
