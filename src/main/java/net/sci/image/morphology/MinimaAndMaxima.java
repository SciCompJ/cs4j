/**
 * 
 */
package net.sci.image.morphology;

import net.sci.array.data.ScalarArray;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.image.data.Connectivity2D;
import net.sci.image.morphology.extrema.ExtremaType;
import net.sci.image.morphology.extrema.RegionalExtrema2D;
import net.sci.image.morphology.reconstruct.MorphologicalReconstruction2D;
import net.sci.image.morphology.reconstruct.MorphologicalReconstruction2DHybrid;

/**
 * A collection of static methods for computing regional and extended minima and
 * maxima.
 * 
 * Regional extrema algorithms are based on flood-filling-like algorithms,
 * whereas extended extrema and extrema imposition algorithms use morphological
 * reconstruction algorithm.
 * 
 * See the books of Serra and Soille for further details.
 * 
 * <p>
 * Example of use:
 * 
 * <pre>
 * <code>
 *	// Get current image processor
 *	ScalarArray2D<?> image = IJ.getImage().getProcessor();
 *	// Computes extended minima with a dynamic of 15, using the 4-connectivity
 *	ScalarArray2D<?> minima = MinimaAndMaxima.extendedMinima(image, 15, 4); 
 *	// Display result in a new imagePlus
 *	ImagePlus res = new ImagePlus("Minima", minima);
 *	res.show(); 
 * </code>
 * </pre>
 * 
 * @see GeodesicReconstruction
 * 
 * @author David Legland
 *
 */
public class MinimaAndMaxima
{
	/**
	 * The default connectivity used by reconstruction algorithms in 2D images.
	 */
	private final static Connectivity2D DEFAULT_CONNECTIVITY_2D = Connectivity2D.C4;
	
	/**
	 * Private constructor to prevent class instantiation.
	 */
	private MinimaAndMaxima()
	{
	}

	/**
	 * Computes the regional maxima in grayscale image <code>image</code>, using
	 * the default connectivity.
	 * 
	 * @param image
	 *            the image to process
	 * @return the regional maxima of input image
	 */
	public final static ScalarArray2D<?> regionalMaxima(ScalarArray2D<?> image)
	{
		return regionalMaxima(image, DEFAULT_CONNECTIVITY_2D);
	}

	/**
	 * Computes the regional maxima in grayscale image <code>image</code>, using
	 * the specified connectivity.
	 * 
	 * @param image
	 *            the image to process
	 * @param conn
	 *            the connectivity for maxima, that should be either 4 or 8
	 * @return the regional maxima of input image
	 */
	public final static ScalarArray2D<?> regionalMaxima(ScalarArray2D<?> image,
			Connectivity2D conn)
	{
		RegionalExtrema2D algo = new RegionalExtrema2D();
		algo.setConnectivity(conn);
		algo.setExtremaType(ExtremaType.MAXIMA);
		
		return (ScalarArray2D<?>) algo.process(image);
	}
	
//	/**
//	 * Computes the regional maxima in grayscale image <code>image</code>, 
//	 * using the specified connectivity, and a slower algorithm (used for testing).
//	 * 
//	 * @param image
//	 *            the image to process
//	 * @param conn
//	 *            the connectivity for maxima, that should be either 4 or 8
//	 * @return the regional maxima of input image
//	 */
//	public final static ScalarArray2D<?> regionalMaximaByReconstruction(
//			ScalarArray2D<?> image,
//			int conn) 
//	{
//		// Compute mask image
//		ScalarArray2D<?> mask = image.duplicate();
//		mask.add(1);
//		
//		// Call geodesic reconstruction algorithm
//		MorphologicalReconstruction2D algo = new MorphologicalReconstruction2DHybrid(
//				GeodesicReconstructionType.BY_DILATION, conn);
//		ScalarArray2D<?> rec = algo.process(image, mask);
//		
//		// allocate memory for result
//		int width = image.getSize(0);
//		int height = image.getSize(1);
//		ScalarArray2D<?> result = new ByteProcessor(width, height);
//		
//		// create binary result image
//		for (int y = 0; y < height; y++) {
//			for (int x = 0; x < width; x++) {
//				if (mask.get(x, y) > rec.get(x, y)) 
//					result.set(x,  y, 255);
//				else
//					result.set(x,  y, 0);
//			}
//		}
//		
//		return result;
//	}

	/**
	 * Computes the regional minima in grayscale image <code>image</code>, 
	 * using the default connectivity.
	 * 
	 * @param image
	 *            the image to process
	 * @return the regional minima of input image
	 */
	public final static ScalarArray2D<?> regionalMinima(ScalarArray2D<?> image) 
	{
		return regionalMinima(image, DEFAULT_CONNECTIVITY_2D);
	}

	/**
	 * Computes the regional minima in grayscale image <code>image</code>, 
	 * using the specified connectivity.
	 * 
	 * @param image
	 *            the image to process
	 * @param conn
	 *            the connectivity for minima, that should be either 4 or 8
	 * @return the regional minima of input image
	 */
	public final static ScalarArray2D<?> regionalMinima(ScalarArray2D<?> image, Connectivity2D conn) 
	{
		RegionalExtrema2D algo = new RegionalExtrema2D();
		algo.setConnectivity(conn);
		algo.setExtremaType(ExtremaType.MINIMA);
		
		return (ScalarArray2D<?>) algo.process(image);
	}
	
//	/**
//	 * Computes the regional minima in grayscale image <code>image</code>, 
//	 * using the specified connectivity, and a slower algorithm (used for testing).
//	 * 
//	 * @param image
//	 *            the image to process
//	 * @param conn
//	 *            the connectivity for minima, that should be either 4 or 8
//	 * @return the regional minima of input image
//	 */
//	public final static ScalarArray2D<?> regionalMinimaByReconstruction(ScalarArray2D<?> image,
//			Connectivity2D conn)
//	{
//		ScalarArray2D<?> marker = image.duplicate();
//		addValue(marker, 1);
//		
//		MorphologicalReconstruction2D algo = new MorphologicalReconstruction2DHybrid(
//				MorphologicalReconstruction.Type.BY_EROSION, conn);
//		ScalarArray2D<?> rec = algo.process(marker, image);
//		
//		int sizeX = image.getSize(0);
//		int sizeY = image.getSize(1);
//		ScalarArray2D<?> result = new ByteProcessor(sizeX, sizeY);
//		
//		for (int y = 0; y < sizeY; y++)
//		{
//			for (int x = 0; x < sizeX; x++)
//			{
//				if (marker.getValue(x, y) > rec.getValue(x, y)) 
//					result.setValue(x,  y, 0);
//				else
//					result.setValue(x,  y, 255);
//			}
//		}
//		
//		return result;
//	}

	/**
	 * Computes the extended maxima in grayscale image <code>image</code>, 
	 * keeping maxima with the specified dynamic, and using the default 
	 * connectivity.
	 * 
	 * @param image
	 *            the image to process
	 * @param dynamic
	 *            the minimal difference between a maxima and its boundary 
	 * @return the extended maxima of input image
	 */
	public final static ScalarArray2D<?> extendedMaxima(ScalarArray2D<?> image,
			double dynamic)
	{
		return extendedMaxima(image, dynamic, DEFAULT_CONNECTIVITY_2D);
	}

	/**
	 * Computes the extended maxima in grayscale image <code>image</code>, 
	 * keeping maxima with the specified dynamic, and using the specified
	 * connectivity.
	 * 
	 * @param image
	 *            the image to process
	 * @param dynamic
	 *            the minimal difference between a maxima and its boundary 
	 * @param conn
	 *            the connectivity for maxima, that should be either 4 or 8
	 * @return the extended maxima of input image
	 */
	public final static ScalarArray2D<?> extendedMaxima(ScalarArray2D<?> image,
			double dynamic, Connectivity2D conn)
	{
		ScalarArray2D<?> mask = image.duplicate();
		addValue(mask, dynamic);
		
		MorphologicalReconstruction2D algo = new MorphologicalReconstruction2DHybrid(
				MorphologicalReconstruction.Type.BY_DILATION, conn);
		ScalarArray2D<?> rec = algo.process(image, mask);
		
		return regionalMaxima(rec, conn);
	}

	/**
	 * Computes the extended minima in grayscale image <code>image</code>, 
	 * keeping minima with the specified dynamic, and using the default 
	 * connectivity.
	 * 
	 * @param image
	 *            the image to process
	 * @param dynamic
	 *            the minimal difference between a minima and its boundary 
	 * @return the extended minima of input image
	 */
	public final static ScalarArray2D<?> extendedMinima(ScalarArray2D<?> image,
			double dynamic)
	{
		return extendedMinima(image, dynamic, DEFAULT_CONNECTIVITY_2D);
	}

	/**
	 * Computes the extended minima in grayscale image <code>image</code>, 
	 * keeping minima with the specified dynamic, and using the specified 
	 * connectivity.
	 * 
	 * @param image
	 *            the image to process
	 * @param dynamic
	 *            the minimal difference between a minima and its boundary 
	 * @param conn
	 *            the connectivity for minima, that should be either 4 or 8
	 * @return the extended minima of input image
	 */
	public final static ScalarArray2D<?> extendedMinima(ScalarArray2D<?> image,
			double dynamic, Connectivity2D conn)
	{
		ScalarArray2D<?> marker = image.duplicate();
		addValue(marker, dynamic);
		
		MorphologicalReconstruction2D algo = new MorphologicalReconstruction2DHybrid(
				MorphologicalReconstruction.Type.BY_EROSION, conn);
		ScalarArray2D<?> rec = algo.process(marker, image);

		return regionalMinima(rec, conn);
	}

	/**
	 * Imposes the maxima given by marker image into the input image, using 
	 * the default connectivity.
	 * 
	 * @param image
	 *            the image to process
	 * @param maxima
	 *            a binary image of maxima 
	 * @return the result of maxima imposition
	 */
	public final static ScalarArray2D<?> imposeMaxima(ScalarArray2D<?> image,
			ScalarArray2D<?> maxima)
	{
		return imposeMaxima(image, maxima, DEFAULT_CONNECTIVITY_2D);
	}
	
	/**
	 * Imposes the maxima given by marker image into the input image, using
	 * the specified connectivity.
	 * 
	 * @param image
	 *            the image to process
	 * @param maxima
	 *            a binary image of maxima 
	 * @param conn
	 *            the connectivity for maxima, that should be either 4 or 8
	 * @return the result of maxima imposition
	 */
	public final static ScalarArray2D<?> imposeMaxima(ScalarArray2D<?> image,
			ScalarArray2D<?> maxima, Connectivity2D conn)
	{
		ScalarArray2D<?> marker = image.duplicate();
		ScalarArray2D<?> mask = image.duplicate();
		
		int sizeX = image.getSize(0);
		int sizeY = image.getSize(1);
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				if (maxima.getValue(x, y) > 0)
				{
					marker.setValue(x, y, 255);// TODO: use another value ?
					mask.setValue(x, y, 255);
				} 
				else
				{
					marker.setValue(x, y, 0);
					mask.setValue(x, y, image.getValue(x, y)-1);
				}
			}
		}
		
		return MorphologicalReconstruction.reconstructByDilation(marker, mask, conn);
	}

	/**
	 * Imposes the minima given by marker image into the input image, using 
	 * the default connectivity.
	 * 
	 * @param image
	 *            the image to process
	 * @param minima
	 *            a binary image of minima 
	 * @return the result of minima imposition
	 */
	public final static ScalarArray2D<?> imposeMinima(ScalarArray2D<?> image,
			ScalarArray2D<?> minima)
	{
		return imposeMinima(image, minima, DEFAULT_CONNECTIVITY_2D);
	}
	
	/**
	 * Imposes the minima given by marker image into the input image, using 
	 * the specified connectivity.
	 * 
	 * @param image
	 *            the image to process
	 * @param minima
	 *            a binary image of minima 
	 * @param conn
	 *            the connectivity for minima, that should be either 4 or 8
	 * @return the result of minima imposition
	 */
	public final static ScalarArray2D<?> imposeMinima(ScalarArray2D<?> image,
			ScalarArray2D<?> minima, Connectivity2D conn)
	{
		int sizeX = image.getSize(0);
		int sizeY = image.getSize(1);
		
		ScalarArray2D<?> marker = image.duplicate();
		ScalarArray2D<?> mask = image.duplicate();
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				if (minima.getValue(x, y) > 0)
				{
					marker.setValue(x, y, 0);
					mask.setValue(x, y, 0);
				} 
				else
				{
					marker.setValue(x, y, 255); // TODO: use another value ?
					mask.setValue(x, y, image.getValue(x, y)+1);
				}
			}
		}
		
		return MorphologicalReconstruction.reconstructByErosion(marker, mask, conn);
	}
	
	private static void addValue(ScalarArray<?> array, double value)
	{
		ScalarArray.Iterator<?> iter = array.iterator();
		while(iter.hasNext())
		{
			iter.forward();
			iter.setValue(iter.getValue() + value);
		}
	}
}
