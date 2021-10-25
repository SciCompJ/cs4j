/**
 * 
 */
package net.sci.image.morphology.watershed;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.IntArray2D;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.image.binary.BinaryImages;
import net.sci.image.data.Connectivity2D;
import net.sci.image.morphology.extrema.RegionalExtrema2D;

/**
 * Watershed algorithm for a 2D scalar array.
 * 
 * Creates a new basin for each regional minima of the input image.
 * 
 * @author dlegland
 *
 */
public class Watershed2D
{
    /** 
     * Connectivity of regions (expected 4 or 8). 
     * The watershed usually has complementary connectivity. 
     */
    Connectivity2D connectivity = Connectivity2D.C4;

    public Watershed2D()
    {
    }
    
    public Watershed2D(Connectivity2D conn)
    {
        this.connectivity = conn;
    }
    
    public IntArray2D<?> process(ScalarArray2D<?> array)
    {
        BinaryArray2D minima = BinaryArray2D.create(array.size(0), array.size(1));

        RegionalExtrema2D minimaAlgo = new RegionalExtrema2D();
        minimaAlgo.process(array, minima);
        IntArray2D<?> markers = BinaryImages.componentsLabeling(minima, 4, 32);
        
        MarkerBasedWatershed2D watershedAlgo = new MarkerBasedWatershed2D(connectivity);
        IntArray2D<?> watershed = watershedAlgo.process(array, markers);
        
        return watershed;
    }

}
