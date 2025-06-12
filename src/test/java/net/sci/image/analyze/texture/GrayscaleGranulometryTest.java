/**
 * 
 */
package net.sci.image.analyze.texture;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.sci.array.numeric.ScalarArray2D;
import net.sci.image.Image;
import net.sci.image.morphology.strel.Strel2D;
import net.sci.table.Table;

/**
 * 
 */
public class GrayscaleGranulometryTest
{
    /**
     * Test method for {@link net.sci.image.analyze.texture.GrayscaleGranulometry#granulometry(net.sci.array.numeric.ScalarArray2D)}.
     * @throws IOException 
     */
    @Test
    public final void testGranulometry_riceGrain() throws IOException
    {
        String fileName = getClass().getResource("/images/grains.tif").getFile();
        Image image = Image.readImage(new File(fileName));
        
        GrayscaleGranulometry algo = new GrayscaleGranulometry()
                .type(GrayscaleGranulometry.Type.CLOSING)
                .strelShape(Strel2D.Shape.SQUARE)
                .radiusMax(50)
                .radiusStep(1);
        
        Table table = algo.granulometry((ScalarArray2D<?>) image.getData());
        
        assertEquals(51, table.rowCount());
        assertEquals(3, table.columnCount());
    }
    
    /**
     * Test method for {@link net.sci.image.analyze.texture.GrayscaleGranulometry#granulometry(net.sci.array.numeric.ScalarArray2D)}.
     * @throws IOException 
     */
    @Test
    public final void testGranulometry_riceGrains_step5() throws IOException
    {
        String fileName = getClass().getResource("/images/grains.tif").getFile();
        Image image = Image.readImage(new File(fileName));
        
        GrayscaleGranulometry algo = new GrayscaleGranulometry()
                .type(GrayscaleGranulometry.Type.CLOSING)
                .strelShape(Strel2D.Shape.SQUARE)
                .radiusMax(50)
                .radiusStep(5);
        
        Table table = algo.granulometry((ScalarArray2D<?>) image.getData());
        
        assertEquals(11, table.rowCount());
        assertEquals(3, table.columnCount());
    }
    
    /**
     * Test method for {@link net.sci.image.analyze.texture.GrayscaleGranulometry#granulometryCurve(net.sci.array.numeric.ScalarArray2D)}.
     * @throws IOException 
     */
    @Test
    public final void testGranulometryCurve_riceGrain() throws IOException
    {
        String fileName = getClass().getResource("/images/grains.tif").getFile();
        Image image = Image.readImage(new File(fileName));
        
        GrayscaleGranulometry algo = new GrayscaleGranulometry()
                .type(GrayscaleGranulometry.Type.CLOSING)
                .strelShape(Strel2D.Shape.SQUARE)
                .radiusMax(50)
                .radiusStep(1);
        
        Table table = algo.granulometryCurve((ScalarArray2D<?>) image.getData());
        
        assertEquals(50, table.rowCount());
        assertEquals(2, table.columnCount());
    }
}
