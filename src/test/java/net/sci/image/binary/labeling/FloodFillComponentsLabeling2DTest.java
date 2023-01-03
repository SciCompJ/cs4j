package net.sci.image.binary.labeling;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.image.data.Connectivity2D;

public class FloodFillComponentsLabeling2DTest
{
	@Test
	public void testProcess2d_separatedSquares()
	{
		// create the reference 2D image, that contains four squares with size 2x2
		BinaryArray2D image = BinaryArray2D.create(8, 8);
		for (int y = 0; y < 2; y++)
		{
			for (int x = 0; x < 2; x++)
			{
				image.setBoolean(x + 1, y + 1, true);
				image.setBoolean(x + 5, y + 1, true);
				image.setBoolean(x + 1, y + 5, true);
				image.setBoolean(x + 5, y + 5, true);
			}
		}
		
		// compute labels of the binary image
		FloodFillComponentsLabeling2D algo = new FloodFillComponentsLabeling2D(Connectivity2D.C4, 8);
		ScalarArray2D<?> labels = (ScalarArray2D<?>) algo.process(image);
		
		// check labels and empty regions
		assertEquals(0, (int) labels.getValue(0, 0));
		assertEquals(1, (int) labels.getValue(2, 2));
		assertEquals(0, (int) labels.getValue(4, 4));
		assertEquals(4, (int) labels.getValue(6, 6));
		assertEquals(0, (int) labels.getValue(7, 7));
	}
	
    /**
     * Computes connected components on an image with five squares that touch by
     * corners. Using connectivity C4, the number of components must be five.
     */
    @Test
    public void testProcess2d_cornerTouchingSquares_C4()
    {
        // create the reference 2D image, that contains four squares with size 2x2
        BinaryArray2D image = createImage_fiveSquares();
        
        // compute labels of the binary image
        FloodFillComponentsLabeling2D algo = new FloodFillComponentsLabeling2D(Connectivity2D.C4, 8);
        ScalarArray2D<?> labels = (ScalarArray2D<?>) algo.process(image);
        
        // check labels and empty regions
        assertEquals(0, (int) labels.getValue(0, 0));
        assertEquals(1, (int) labels.getValue(2, 2));
        assertEquals(3, (int) labels.getValue(4, 4));
        assertEquals(5, (int) labels.getValue(6, 6));
        assertEquals(0, (int) labels.getValue(7, 7));
    }
    
    /**
     * Computes connected components on an image with five squares that touch by
     * corners. Using connectivity C8, the number of components must be one.
     */
    @Test
    public void testProcess2d_cornerTouchingSquares_C8()
    {
        // create the reference 2D image, that contains four squares with size 2x2
        BinaryArray2D image = createImage_fiveSquares();
        
        // compute labels of the binary image
        FloodFillComponentsLabeling2D algo = new FloodFillComponentsLabeling2D(Connectivity2D.C8, 8);
        ScalarArray2D<?> labels = (ScalarArray2D<?>) algo.process(image);
        
        // check labels and empty regions
        assertEquals(0, (int) labels.getValue(0, 0));
        assertEquals(1, (int) labels.getValue(2, 2));
        assertEquals(1, (int) labels.getValue(4, 4));
        assertEquals(1, (int) labels.getValue(6, 6));
        assertEquals(0, (int) labels.getValue(7, 7));
    }
    
	private BinaryArray2D createImage_fiveSquares()
	{
	    // create the reference 2D image, that contains five squares with size 2x2
        BinaryArray2D image = BinaryArray2D.create(8, 8);
        for (int y = 0; y < 2; y++)
        {
            for (int x = 0; x < 2; x++)
            {
                image.setBoolean(x + 1, y + 1, true);
                image.setBoolean(x + 5, y + 1, true);
                image.setBoolean(x + 3, y + 3, true);
                image.setBoolean(x + 1, y + 5, true);
                image.setBoolean(x + 5, y + 5, true);
            }
        }
        return image;
	}
}
