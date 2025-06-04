/**
 * 
 */
package net.sci.image.binary;

import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.image.Image;
import net.sci.image.connectivity.Connectivity3D;
import net.sci.image.filtering.BoxFilter;

/**
 * Demonstrates the use of the class <code>SplitCoalescentParticles</code>.
 */
public class SplitCoalescentParticlesDemo_Blobs3D
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        BinaryArray3D markers = BinaryArray3D.create(100, 100, 100);
        markers.fill(true);
        markers.setBoolean(61, 42, 52, false);
        markers.setBoolean(43, 64, 48, false);
        ScalarArray3D<?> distMap = BinaryImages.distanceMap3d(markers);
        
        ScalarArray3D<?> distMap2 = (ScalarArray3D<?>) new BoxFilter(new int[]{3, 3, 3}).process(distMap);
        
        Image distMapImage = new Image(distMap2.slice(50));
        distMapImage.getDisplaySettings().setDisplayRange(distMap2.finiteValueRange());
        distMapImage.show();
        
        BinaryArray3D blobs = BinaryArray3D.create(markers.size(0), markers.size(1), markers.size(2));
        blobs.fillBooleans(pos -> distMap.getValue(pos) < 18);
        new Image(blobs.slice(50)).show(); 
        
        SplitCoalescentParticles algo = new SplitCoalescentParticles();
        algo.setConnectivity(Connectivity3D.C26);
        BinaryArray3D blobs2 = algo.processBinary3d(blobs);
        new Image(blobs2.slice(50)).show(); 
    }
}
