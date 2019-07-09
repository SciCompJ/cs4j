/**
 * 
 */
package net.sci.image.morphology.watershed;

import net.sci.array.scalar.BinaryArray3D;
import net.sci.array.scalar.IntArray3D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.image.binary.BinaryImages;
import net.sci.image.data.Connectivity3D;
import net.sci.image.morphology.extrema.RegionalExtrema3D;

/**
 * Watershed algorithm for a 3D scalar array.
 * 
 * Creates a new basin for each regional minima of the input image.
 * 
 * @author dlegland
 *
 */
public class Watershed3D
{
    /** 
     * Connectivity of regions (expected 6 or 26). 
     * The watershed usually has complementary connectivity. 
     */
    Connectivity3D connectivity = Connectivity3D.C6;

    public Watershed3D()
    {
    }
    
    public Watershed3D(Connectivity3D conn)
    {
        this.connectivity = conn;
    }

    public IntArray3D<?> process(ScalarArray3D<?> array)
    {
        BinaryArray3D minima = BinaryArray3D.create(array.size(0), array.size(1), array.size(2));

        RegionalExtrema3D minimaAlgo = new RegionalExtrema3D();
        minimaAlgo.process(array, minima);
        IntArray3D<?> markers = BinaryImages.componentsLabeling(minima, Connectivity3D.C6, 32);
        
        MarkerBasedWatershed3D watershedAlgo = new MarkerBasedWatershed3D(connectivity);
        IntArray3D<?> watershed = watershedAlgo.process(array, markers);
        
        return watershed;
    }

}
