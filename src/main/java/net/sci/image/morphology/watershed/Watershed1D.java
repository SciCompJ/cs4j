/**
 * 
 */
package net.sci.image.morphology.watershed;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray1D;
import net.sci.array.scalar.IntArray1D;
import net.sci.array.scalar.ScalarArray1D;
import net.sci.image.binary.FloodFillComponentsLabeling1D;
import net.sci.image.morphology.MinimaAndMaxima;
import net.sci.image.morphology.extrema.RegionalExtrema1D;

/**
 * A tentative implementation of watershed algorithm for 1D arrays.
 *
 * @see Watershed2D
 * @see Watershed3D
 * 
 * @author dlegland
 *
 */
public class Watershed1D extends AlgoStub
{
    /**
     * Computes the watershed on the given (scalar) array.
     * 
     * @param array
     *            the array containing 2D intensity map
     * @return the label map corresponding to the resulting basins, using 0 as
     *         label for the background
     */
    public IntArray1D<?> process(ScalarArray1D<?> array)
    {
        // compute regional minima within image
        this.fireStatusChanged(this, "Compute regional minima");
        RegionalExtrema1D minimaAlgo = new RegionalExtrema1D(MinimaAndMaxima.Type.MINIMA);
        BinaryArray1D minima = minimaAlgo.processScalar(array);
        
        // compute labels of the minima
        this.fireStatusChanged(this, "Connected component labeling of minima");
        FloodFillComponentsLabeling1D algo = new FloodFillComponentsLabeling1D(32);
        IntArray1D<?> markers = algo.processBinary1d(minima);
        
        // compute marker-based watershed
        this.fireStatusChanged(this, "Process watershed");
        MarkerBasedWatershed1D watershedAlgo = new MarkerBasedWatershed1D();
        IntArray1D<?> watershed = watershedAlgo.process(array, markers);
        
        return watershed;
    }

}
