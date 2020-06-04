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
 * References about MetaImage file format:
 * <ul>
 * <li> MetaIO Documentation (http://www.itk.org/Wiki/MetaIO/Documentation) </li>
 * </ul>
 * 
 * @author dlegland
 */
public class MetaImageWriter implements ImageWriter
{
	File headerFile;
	
	MetaImageInfo info;
	
	
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
			stream = new FileOutputStream(info.elementDataFile);
//			try (OutputStream out = new BufferedOutputStream(
//					Files.newOutputStream(p, CREATE, APPEND)))
//			{
//				out.write(data, 0, data.length);
//			} catch (IOException x)
//			{
//				System.err.println(x);
//			}
		}
		
		// write data
		writeImageData(image.getData(), stream);
		
		// close stream
		stream.close();
	}

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
	
	
	public MetaImageInfo writeHeader(OutputStream stream) throws IOException
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
	
	public void writeImageData(Array<?> array, OutputStream stream) throws IOException
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
