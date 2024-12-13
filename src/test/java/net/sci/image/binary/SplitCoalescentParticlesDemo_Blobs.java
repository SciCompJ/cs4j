/**
 * 
 */
package net.sci.image.binary;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.image.Image;
import net.sci.image.filtering.GaussianFilter5x5;

/**
 * Demonstrates the use of the class <code>SplitCoalescentParticles</code>.
 */
public class SplitCoalescentParticlesDemo_Blobs
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        BinaryArray2D markers = BinaryArray2D.create(200, 200);
        markers.fill(true);
        markers.setBoolean(120, 80, false);
        markers.setBoolean(80, 120, false);
        ScalarArray2D<?> distMap = BinaryImages.distanceMap2d(markers);
        
        ScalarArray2D<?> distMap2 = (ScalarArray2D<?>) new GaussianFilter5x5().process(distMap);
        
        Image distMapImage = new Image(distMap2); 
        distMapImage.getDisplaySettings().setDisplayRange(distMap2.finiteValueRange());
        distMapImage.show();
        
        BinaryArray2D blobs = BinaryArray2D.create(200, 200);
        blobs.fillBooleans((x,y) -> distMap.getValue(x, y) < 60);
        new Image(blobs).show(); 
        
        BinaryArray2D blobs2 = new SplitCoalescentParticles().processBinary2d(blobs);
        new Image(blobs2).show(); 
    }
}
