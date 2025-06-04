/**
 * 
 */
package net.sci.image.morphology.watershed;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.image.binary.BinaryImages;
import net.sci.image.connectivity.Connectivity2D;
import net.sci.image.morphology.MinimaAndMaxima;
import net.sci.image.morphology.extrema.RegionalExtrema2D;

/**
 * Watershed algorithm for a 2D scalar array.
 * 
 * Creates a new basin for each regional minima of the input image. The current
 * implementation encapsulates the following steps:
 * <ol>
 * <li>Computation of regional 2D minima</li>
 * <li>Connected-components labeling of the minima</li>
 * <li>call of the marker-based watershed</li>
 * </ol>
 *
 * @see net.sci.image.morphology.extrema.RegionalExtrema2D
 * @see net.sci.image.binary.BinaryImages.#componentsLabeling(BinaryArray2D, Connectivity2D, int)
 * @see MarkerBasedWatershed2D
 * @see Watershed3D
 * 
 * @author dlegland
 *
 */
public class Watershed2D extends AlgoStub
{
    /** 
     * Connectivity of regions (expected 4 or 8). 
     * The watershed usually has complementary connectivity. 
     */
    Connectivity2D connectivity = Connectivity2D.C4;

    /**
     * Creates a watershed algorithm using the default C4 connectivity.
     */
    public Watershed2D()
    {
    }
    
    /**
     * Creates a watershed algorithm using the specified connectivity.
     * 
     * @param conn
     *            the connectivity of the resulting basins
     */
    public Watershed2D(Connectivity2D conn)
    {
        this.connectivity = conn;
    }
    
    /**
     * Computes the watershed on the given (scalar) array.
     * 
     * @param array
     *            the array containing 2D intensity map
     * @return the label map corresponding to the resulting basins, using 0 as
     *         label for the background
     */
    public IntArray2D<?> process(ScalarArray2D<?> array)
    {
        // compute regional minima within image
        this.fireStatusChanged(this, "Compute regional minima");
        RegionalExtrema2D minimaAlgo = new RegionalExtrema2D(MinimaAndMaxima.Type.MINIMA, connectivity);
        BinaryArray2D minima = minimaAlgo.processScalar(array);
        
        // compute labels of the minima
        this.fireStatusChanged(this, "Connected component labeling of minima");
        IntArray2D<?> markers = BinaryImages.componentsLabeling(minima, connectivity, 32);
        
        // compute marker-based watershed
        this.fireStatusChanged(this, "Process watershed");
        MarkerBasedWatershed2D watershedAlgo = new MarkerBasedWatershed2D(connectivity);
        IntArray2D<?> watershed = watershedAlgo.process(array, markers);
        
        return watershed;
    }

}
