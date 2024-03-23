/**
 * 
 */
package net.sci.image.morphology;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.image.Connectivity2D;
import net.sci.image.Connectivity3D;
import net.sci.image.morphology.extrema.RegionalExtrema2D;
import net.sci.image.morphology.extrema.RegionalExtrema3D;
import net.sci.image.morphology.reconstruct.MorphologicalReconstruction2D;
import net.sci.image.morphology.reconstruct.MorphologicalReconstruction2DHybrid;
import net.sci.image.morphology.reconstruct.MorphologicalReconstruction3D;
import net.sci.image.morphology.reconstruct.MorphologicalReconstruction3DHybrid;

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
 * {@code
 *	// Get current image processor
 *	ScalarArray2D<?> image = IJ.getImage().getProcessor();
 *	// Computes extended minima with a dynamic of 15, using the 4-connectivity
 *	ScalarArray2D<?> minima = MinimaAndMaxima.extendedMinima(image, 15, 4); 
 *	// Display result in a new imagePlus
 *	ImagePlus res = new ImagePlus("Minima", minima);
 *	res.show(); 
 * }</pre>
 * 
 * @see MorphologicalReconstruction
 * 
 * @author David Legland
 *
 */
public class MinimaAndMaxima
{
	// ==============================================================
	// Public enumerations

	/**
	 * One of the two types of extrema.
	 */
	public enum Type 
	{
		MINIMA, 
		MAXIMA;
	}
	
	
	// ==============================================================
	// Private constants

    /**
     * The default connectivity used by reconstruction algorithms in 2D arrays.
     */
    private final static Connectivity2D DEFAULT_CONNECTIVITY_2D = Connectivity2D.C4;
    
    /**
     * The default connectivity used by reconstruction algorithms in 3D arrays.
     */
    private final static Connectivity3D DEFAULT_CONNECTIVITY_3D = Connectivity3D.C6;
    

	// ==============================================================
	// Constructor
	
	/**
	 * Private constructor to prevent class instantiation.
	 */
	private MinimaAndMaxima()
	{
	}


	// ==============================================================
	// Regional Minima and Maxima
	
	/**
	 * Computes the regional maxima in grayscale array <code>array</code>, using
	 * the default connectivity.
	 * 
	 * @param array
	 *            the array to process
	 * @return the regional maxima of input array
	 */
	public final static BinaryArray2D regionalMaxima(ScalarArray2D<?> array)
	{
		return regionalMaxima(array, DEFAULT_CONNECTIVITY_2D);
	}

	/**
	 * Computes the regional maxima in grayscale array <code>array</code>, using
	 * the specified connectivity.
	 * 
	 * @param array
	 *            the array to process
	 * @param conn
	 *            the connectivity for maxima, that should be either 4 or 8
	 * @return the regional maxima of input array
	 */
	public final static BinaryArray2D regionalMaxima(ScalarArray2D<?> array,
			Connectivity2D conn)
	{
		RegionalExtrema2D algo = new RegionalExtrema2D();
		algo.setConnectivity(conn);
		algo.setExtremaType(Type.MAXIMA);
		
		return algo.process(array);
	}
	
    /**
     * Computes the regional maxima in grayscale 3D array <code>array</code>, 
     * using the default connectivity for 3D arrays.
     * 
     * @param array
     *            the array to process
     * @return the regional minima of input array
     */
    public final static BinaryArray3D regionalMaxima(ScalarArray3D<?> array) 
    {
        return regionalMaxima(array, DEFAULT_CONNECTIVITY_3D);
    }
    /**
     * Computes the regional maxima in 3D grayscale array <code>array</code>, 
     * using the specified connectivity.
     * 
     * @param array
     *            the arrayto process
     * @param conn
     *            the connectivity for minima, that should be either 4 or 8
     * @return the regional minima of input array
     */
    public final static BinaryArray3D regionalMaxima(ScalarArray3D<?> array, Connectivity3D conn) 
    {
        RegionalExtrema3D algo = new RegionalExtrema3D();
        algo.setConnectivity(conn);
        algo.setExtremaType(Type.MAXIMA);
        
        return algo.process(array);
    }
    
//	/**
//	 * Computes the regional maxima in grayscale array <code>array</code>, 
//	 * using the specified connectivity, and a slower algorithm (used for testing).
//	 * 
//	 * @param array
//	 *            the array to process
//	 * @param conn
//	 *            the connectivity for maxima, that should be either 4 or 8
//	 * @return the regional maxima of input array
//	 */
//	public final static ScalarArray2D<?> regionalMaximaByReconstruction(
//			ScalarArray2D<?> array,
//			int conn) 
//	{
//		// Compute mask array
//		ScalarArray2D<?> mask = array.duplicate();
//		mask.add(1);
//		
//		// Call geodesic reconstruction algorithm
//		MorphologicalReconstruction2D algo = new MorphologicalReconstruction2DHybrid(
//				GeodesicReconstructionType.BY_DILATION, conn);
//		ScalarArray2D<?> rec = algo.process(array, mask);
//		
//		// allocate memory for result
//		int sizeX = array.getSize(0);
//		int sizeY = array.getSize(1);
//		ScalarArray2D<?> result = new ByteProcessor(sizeX, sizeY);
//		
//		// create binary result array
//		for (int y = 0; y < sizeY; y++) {
//			for (int x = 0; x < sizeX; x++) {
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
	 * Computes the regional minima in grayscale array <code>array</code>, 
	 * using the default connectivity.
	 * 
	 * @param array
	 *            the array to process
	 * @return the regional minima of input array
	 */
	public final static BinaryArray2D regionalMinima(ScalarArray2D<?> array) 
	{
		return regionalMinima(array, DEFAULT_CONNECTIVITY_2D);
	}

    /**
     * Computes the regional minima in grayscale array <code>array</code>, 
     * using the specified connectivity.
     * 
     * @param array
     *            the array to process
     * @param conn
     *            the connectivity for minima, that should be either 4 or 8
     * @return the regional minima of input array
     */
    public final static BinaryArray2D regionalMinima(ScalarArray2D<?> array, Connectivity2D conn) 
    {
        RegionalExtrema2D algo = new RegionalExtrema2D();
        algo.setConnectivity(conn);
        algo.setExtremaType(Type.MINIMA);
        
        return algo.process(array);
    }
    
    /**
     * Computes the regional minima in grayscale array <code>array</code>, 
     * using the default connectivity for 3D arrays.
     * 
     * @param array
     *            the array to process
     * @return the regional minima of input array
     */
    public final static BinaryArray3D regionalMinima(ScalarArray3D<?> array) 
    {
        return regionalMinima(array, DEFAULT_CONNECTIVITY_3D);
    }

    /**
     * Computes the regional minima in 3D grayscale array <code>array</code>, 
     * using the specified connectivity.
     * 
     * @param array
     *            the array to process
     * @param conn
     *            the connectivity for minima, that should be either 4 or 8
     * @return the regional minima of input array
     */
    public final static BinaryArray3D regionalMinima(ScalarArray3D<?> array, Connectivity3D conn) 
    {
        RegionalExtrema3D algo = new RegionalExtrema3D();
        algo.setConnectivity(conn);
        algo.setExtremaType(Type.MINIMA);
        
        return algo.process(array);
    }
    
//	/**
//	 * Computes the regional minima in grayscale array <code>array</code>, 
//	 * using the specified connectivity, and a slower algorithm (used for testing).
//	 * 
//	 * @param array
//	 *            the array to process
//	 * @param conn
//	 *            the connectivity for minima, that should be either 4 or 8
//	 * @return the regional minima of input array
//	 */
//	public final static ScalarArray2D<?> regionalMinimaByReconstruction(ScalarArray2D<?> array,
//			Connectivity2D conn)
//	{
//		ScalarArray2D<?> marker = array.duplicate();
//		addValue(marker, 1);
//		
//		MorphologicalReconstruction2D algo = new MorphologicalReconstruction2DHybrid(
//				MorphologicalReconstruction.Type.BY_EROSION, conn);
//		ScalarArray2D<?> rec = algo.process(marker, array);
//		
//		int sizeX = array.getSize(0);
//		int sizeY = array.getSize(1);
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


    // ==============================================================
    // Extended Minima and Maxima
    
	/**
	 * Computes the extended maxima in grayscale array <code>array</code>, 
	 * keeping maxima with the specified dynamic, and using the default 
	 * connectivity.
	 * 
	 * @param array
	 *            the array to process
	 * @param dynamic
	 *            the minimal difference between a maxima and its boundary 
	 * @return the extended maxima of input array
	 */
	public final static ScalarArray2D<?> extendedMaxima(ScalarArray2D<?> array,
			double dynamic)
	{
		return extendedMaxima(array, dynamic, DEFAULT_CONNECTIVITY_2D);
	}


    /**
     * Computes the extended maxima in grayscale array <code>array</code>, 
     * keeping maxima with the specified dynamic, and using the specified
     * connectivity.
     * 
     * @param array
     *            the array to process
     * @param dynamic
     *            the minimal difference between a maxima and its boundary 
     * @param conn
     *            the connectivity for maxima, that should be either 4 or 8
     * @return the extended maxima of input array
     */
    public final static ScalarArray2D<?> extendedMaxima(ScalarArray2D<?> array,
            double dynamic, Connectivity2D conn)
    {
        ScalarArray2D<?> mask = array.duplicate();
        addValue(mask, dynamic);
        
        MorphologicalReconstruction2D algo = new MorphologicalReconstruction2DHybrid(
                MorphologicalReconstruction.Type.BY_DILATION, conn);
        ScalarArray2D<?> rec = algo.process(array, mask);
        
        return regionalMaxima(rec, conn);
    }

    /**
     * Computes the extended maxima in grayscale array <code>array</code>, 
     * keeping maxima with the specified dynamic, and using the default 
     * connectivity.
     * 
     * @param array
     *            the array to process
     * @param dynamic
     *            the minimal difference between a maxima and its boundary 
     * @return the extended maxima of input array
     */
    public final static ScalarArray3D<?> extendedMaxima(ScalarArray3D<?> array,
            double dynamic)
    {
        return extendedMaxima(array, dynamic, DEFAULT_CONNECTIVITY_3D);
    }

    /**
     * Computes the extended maxima in grayscale array <code>array</code>, 
     * keeping maxima with the specified dynamic, and using the specified
     * connectivity.
     * 
     * @param array
     *            the array to process
     * @param dynamic
     *            the minimal difference between a maxima and its boundary 
     * @param conn
     *            the connectivity for maxima, that should be either 6 or 26
     * @return the extended maxima of input array
     */
    public final static ScalarArray3D<?> extendedMaxima(ScalarArray3D<?> array,
            double dynamic, Connectivity3D conn)
    {
        ScalarArray3D<?> mask = array.duplicate();
        addValue(mask, dynamic);
        
        MorphologicalReconstruction3D algo = new MorphologicalReconstruction3DHybrid(
                MorphologicalReconstruction.Type.BY_DILATION, conn);
        ScalarArray3D<?> rec = algo.process(array, mask);
        
        return regionalMaxima(rec, conn);
    }

    /**
	 * Computes the extended minima in grayscale array <code>array</code>, 
	 * keeping minima with the specified dynamic, and using the default 
	 * connectivity.
	 * 
	 * @param array
	 *            the array to process
	 * @param dynamic
	 *            the minimal difference between a minima and its boundary 
	 * @return the extended minima of input array
	 */
	public final static ScalarArray2D<?> extendedMinima(ScalarArray2D<?> array,
			double dynamic)
	{
		return extendedMinima(array, dynamic, DEFAULT_CONNECTIVITY_2D);
	}

	/**
	 * Computes the extended minima in grayscale array <code>array</code>, 
	 * keeping minima with the specified dynamic, and using the specified 
	 * connectivity.
	 * 
	 * @param array
	 *            the array to process
	 * @param dynamic
	 *            the minimal difference between a minima and its boundary 
	 * @param conn
	 *            the connectivity for minima, that should be either 4 or 8
	 * @return the extended minima of input array
	 */
	public final static ScalarArray2D<?> extendedMinima(ScalarArray2D<?> array,
			double dynamic, Connectivity2D conn)
	{
		ScalarArray2D<?> marker = (ScalarArray2D<?>) array.plus(dynamic);
		
		MorphologicalReconstruction2D algo = new MorphologicalReconstruction2DHybrid(
				MorphologicalReconstruction.Type.BY_EROSION, conn);
		ScalarArray2D<?> rec = algo.process(marker, array);

		return regionalMinima(rec, conn);
	}

    /**
     * Computes the extended minima in grayscale array <code>array</code>, 
     * keeping minima with the specified dynamic, and using the default 
     * connectivity.
     * 
     * @param array
     *            the array to process
     * @param dynamic
     *            the minimal difference between a minima and its boundary 
     * @return the extended minima of input array
     */
    public final static ScalarArray3D<?> extendedMinima(ScalarArray3D<?> array,
            double dynamic)
    {
        return extendedMinima(array, dynamic, DEFAULT_CONNECTIVITY_3D);
    }

    /**
     * Computes the extended minima in grayscale array <code>array</code>, 
     * keeping minima with the specified dynamic, and using the specified 
     * connectivity.
     * 
     * @param array
     *            the array to process
     * @param dynamic
     *            the minimal difference between a minima and its boundary 
     * @param conn
     *            the connectivity for minima, that should be either 6 or 26
     * @return the extended minima of input array
     */
    public final static ScalarArray3D<?> extendedMinima(ScalarArray3D<?> array,
            double dynamic, Connectivity3D conn)
    {
        ScalarArray3D<?> marker = (ScalarArray3D<?>) array.plus(dynamic);
        
        MorphologicalReconstruction3D algo = new MorphologicalReconstruction3DHybrid(
                MorphologicalReconstruction.Type.BY_EROSION, conn);
        ScalarArray3D<?> rec = algo.process(marker, array);

        return regionalMinima(rec, conn);
    }


    // ==============================================================
    // Imposition of Minima and Maxima
    
	/**
	 * Imposes the maxima given by marker array into the input array, using 
	 * the default connectivity.
	 * 
	 * @param array
	 *            the array to process
	 * @param maxima
	 *            a binary array of maxima 
	 * @return the result of maxima imposition
	 */
	public final static ScalarArray2D<?> imposeMaxima(ScalarArray2D<?> array,
			ScalarArray2D<?> maxima)
	{
		return imposeMaxima(array, maxima, DEFAULT_CONNECTIVITY_2D);
	}
	
	/**
	 * Imposes the maxima given by marker array into the input array, using
	 * the specified connectivity.
	 * 
	 * @param array
	 *            the array to process
	 * @param maxima
	 *            a binary array of maxima 
	 * @param conn
	 *            the connectivity for maxima, that should be either 4 or 8
	 * @return the result of maxima imposition
	 */
	public final static ScalarArray2D<?> imposeMaxima(ScalarArray2D<?> array,
			ScalarArray2D<?> maxima, Connectivity2D conn)
	{
		ScalarArray2D<?> marker = array.duplicate();
		ScalarArray2D<?> mask = array.duplicate();
		
		int sizeX = array.size(0);
		int sizeY = array.size(1);
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				if (maxima.getValue(x, y) > 0)
				{
					marker.setValue(x, y, Double.MAX_VALUE);
					mask.setValue(x, y, Double.MAX_VALUE);
				} 
				else
				{
					marker.setValue(x, y, Double.NEGATIVE_INFINITY);
					mask.setValue(x, y, array.getValue(x, y)-1); // TODO: potential problem for floating-point arrays
				}
			}
		}
		
		return MorphologicalReconstruction.reconstructByDilation(marker, mask, conn);
	}

	   /**
     * Imposes the maxima given by marker array into the input array, using 
     * the default connectivity.
     * 
     * @param array
     *            the array to process
     * @param maxima
     *            a binary array of maxima 
     * @return the result of maxima imposition
     */
    public final static ScalarArray3D<?> imposeMaxima(ScalarArray3D<?> array,
            ScalarArray3D<?> maxima)
    {
        return imposeMaxima(array, maxima, DEFAULT_CONNECTIVITY_3D);
    }
    
    /**
     * Imposes the maxima given by marker array into the input array, using
     * the specified connectivity.
     * 
     * @param array
     *            the array to process
     * @param maxima
     *            a binary array of maxima 
     * @param conn
     *            the connectivity for maxima, that should be either 4 or 8
     * @return the result of maxima imposition
     */
    public final static ScalarArray3D<?> imposeMaxima(ScalarArray3D<?> array,
            ScalarArray3D<?> maxima, Connectivity3D conn)
    {
        ScalarArray3D<?> marker = array.duplicate();
        ScalarArray3D<?> mask = array.duplicate();
        
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    if (maxima.getValue(x, y, z) > 0)
                    {
                        marker.setValue(x, y, z, Double.MAX_VALUE);
                        mask.setValue(x, y, z, Double.MAX_VALUE);
                    } 
                    else
                    {
                        marker.setValue(x, y, z, Double.NEGATIVE_INFINITY);
                        mask.setValue(x, y, z, array.getValue(x, y, z) - 1); // TODO: potential problem for floating-point arrays
                    }
                }
            }
        }
        
        return MorphologicalReconstruction.reconstructByDilation(marker, mask, conn);
    }

    /**
     * Imposes the minima given by marker array into the input array, using 
     * the default connectivity.
     * 
     * @param array
     *            the array to process
     * @param minima
     *            a binary array of minima 
     * @return the result of minima imposition
     */
    public final static ScalarArray2D<?> imposeMinima(ScalarArray2D<?> array,
            ScalarArray2D<?> minima)
    {
        return imposeMinima(array, minima, DEFAULT_CONNECTIVITY_2D);
    }
    
    /**
     * Imposes the minima given by marker array into the input array, using 
     * the specified connectivity.
     * 
     * @param array
     *            the array to process
     * @param minima
     *            a binary array of minima 
     * @param conn
     *            the connectivity for minima, that should be either 4 or 8
     * @return the result of minima imposition
     */
    public final static ScalarArray2D<?> imposeMinima(ScalarArray2D<?> array,
            ScalarArray2D<?> minima, Connectivity2D conn)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        ScalarArray2D<?> marker = array.duplicate();
        ScalarArray2D<?> mask = array.duplicate();
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                if (minima.getValue(x, y) > 0)
                {
                    marker.setValue(x, y, Double.NEGATIVE_INFINITY);
                    mask.setValue(x, y, Double.NEGATIVE_INFINITY);
                } 
                else
                {
                    marker.setValue(x, y, Double.MAX_VALUE);
                    mask.setValue(x, y, array.getValue(x, y)+1);  // TODO: potential problem for floating point arrays
                }
            }
        }
        
        return MorphologicalReconstruction.reconstructByErosion(marker, mask, conn);
    }

	/**
	 * Imposes the minima given by marker array into the input array, using 
	 * the default connectivity.
	 * 
	 * @param array
	 *            the array to process
	 * @param minima
	 *            a binary array of minima 
	 * @return the result of minima imposition
	 */
	public final static ScalarArray3D<?> imposeMinima(ScalarArray3D<?> array,
			ScalarArray3D<?> minima)
	{
		return imposeMinima(array, minima, DEFAULT_CONNECTIVITY_3D);
	}
	
	/**
	 * Imposes the minima given by marker array into the input array, using 
	 * the specified connectivity.
	 * 
	 * @param array
	 *            the array to process
	 * @param minima
	 *            a binary array of minima 
	 * @param conn
	 *            the connectivity for minima, that should be either 6 or 26
	 * @return the result of minima imposition
	 */
	public final static ScalarArray3D<?> imposeMinima(ScalarArray3D<?> array,
			ScalarArray3D<?> minima, Connectivity3D conn)
	{
		int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
		
		ScalarArray3D<?> marker = array.duplicate();
		ScalarArray3D<?> mask = array.duplicate();
		for (int z = 0; z < sizeZ; z++)
		{
		    for (int y = 0; y < sizeY; y++)
		    {
		        for (int x = 0; x < sizeX; x++)
		        {
		            if (minima.getValue(x, y, z) > 0)
		            {
		                marker.setValue(x, y, z, Double.NEGATIVE_INFINITY);
		                mask.setValue(x, y, z, Double.NEGATIVE_INFINITY);
		            } 
		            else
		            {
		                marker.setValue(x, y, z, Double.MAX_VALUE);
		                mask.setValue(x, y, z, array.getValue(x, y, z)+1);  // TODO: potential problem for floating point arrays
		            }
		        }
		    }
		}
		return MorphologicalReconstruction.reconstructByErosion(marker, mask, conn);
	}
	
	
	// ==============================================================
	// Private utilities

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
