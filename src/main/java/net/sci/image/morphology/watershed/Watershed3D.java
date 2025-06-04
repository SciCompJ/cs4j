/**
 * 
 */
package net.sci.image.morphology.watershed;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.IntArray3D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.image.binary.BinaryImages;
import net.sci.image.connectivity.Connectivity3D;
import net.sci.image.morphology.MinimaAndMaxima;
import net.sci.image.morphology.extrema.RegionalExtrema3D;

/**
 * Watershed algorithm for a 3D scalar array.
 * 
 * Creates a new basin for each regional minima of the input image. This current
 * implementation encapsulates the following steps:
 * <ol>
 * <li>Computation of regional 3D minima</li>
 * <li>Connected-components labeling of the minima</li>
 * <li>call of the marker-based watershed</li>
 * </ol>
 *
 * @see net.sci.image.morphology.extrema.RegionalExtrema3D
 * @see net.sci.image.binary.BinaryImages.#componentsLabeling(BinaryArray3D, Connectivity3D, int)
 * @see MarkerBasedWatershed3D
 * @see Watershed2D
 * 
 * @author dlegland
 */
public class Watershed3D extends AlgoStub
{
    /** 
     * Connectivity of regions (expected 6 or 26). 
     * The watershed usually has complementary connectivity. 
     */
    Connectivity3D connectivity = Connectivity3D.C6;

    /**
     * Creates a watershed algorithm using the default C6 connectivity.
     */
    public Watershed3D()
    {
    }
    
    /**
     * Creates a watershed algorithm using the specified connectivity.
     * 
     * @param conn
     *            the connectivity of the resulting basins
     */
    public Watershed3D(Connectivity3D conn)
    {
        this.connectivity = conn;
    }

    /**
     * Computes the watershed on the given (scalar) array.
     * 
     * @param array
     *            the array containing 3D intensity map
     * @return the label map corresponding to the resulting basins, using 0 as
     *         label for the background
     */
    public IntArray3D<?> process(ScalarArray3D<?> array)
    {
        // compute regional minima within image
        this.fireStatusChanged(this, "Compute regional minima");
        RegionalExtrema3D minimaAlgo = new RegionalExtrema3D(MinimaAndMaxima.Type.MINIMA, connectivity);
        BinaryArray3D minima = minimaAlgo.processScalar(array);
        
        // compute labels of the minima
        this.fireStatusChanged(this, "Connected component labeling of minima");
        IntArray3D<?> markers = BinaryImages.componentsLabeling(minima, connectivity, 32);
        
        // compute marker-based watershed
        this.fireStatusChanged(this, "Process watershed");
        MarkerBasedWatershed3D watershedAlgo = new MarkerBasedWatershed3D(connectivity);
        IntArray3D<?> watershed = watershedAlgo.process(array, markers);
        
        return watershed;
    }

}
