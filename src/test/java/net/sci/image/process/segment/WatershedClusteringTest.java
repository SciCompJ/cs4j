/**
 * 
 */
package net.sci.image.process.segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import net.sci.array.scalar.IntArray2D;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.image.Image;
import net.sci.image.io.TiffImageReader;

/**
 * @author dlegland
 *
 */
public class WatershedClusteringTest
{

    /**
     * Test method for {@link net.sci.image.process.segment.WatershedClustering#process(net.sci.array.Array)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_wheatGrainTomoSlice() throws IOException
    {
        String fileName = getClass().getResource("/images/wheatGrain/wheatGrain_tomo_180a_z630.tif").getFile();
        
        TiffImageReader reader = new TiffImageReader(fileName);
        Image image = reader.readImage();
        image.show();
        
        assertEquals(2, image.getDimension());

        UInt8Array2D data = (UInt8Array2D) image.getData();
        
        WatershedClustering algo = new WatershedClustering();
        IntArray2D<?> res = IntArray2D.wrap(algo.process(data));
        
        Image resImage = new Image(res, Image.Type.LABEL);
        resImage.show();
        
        
        fail("Not yet implemented"); // TODO
    }

}
