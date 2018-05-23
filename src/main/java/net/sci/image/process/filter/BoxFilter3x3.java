/**
 * 
 */
package net.sci.image.process.filter;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Arrays;
import net.sci.array.process.ScalarArrayOperator;
import net.sci.array.scalar.Float32Array;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.image.ImageArrayOperator;

/**
 * A preliminary implementation of Box Mean Filter. Superseeded by BoxFilter.
 * Kept for testing purpose.
 * 
 * @see BoxFilter
 * 
 * @author dlegland
 *
 */
public final class BoxFilter3x3 extends AlgoStub implements ImageArrayOperator, ScalarArrayOperator
{

	/* (non-Javadoc)
	 * @see net.sci.array.ArrayOperator#process(net.sci.array.Array, net.sci.array.Array)
	 */
	public void processScalar(ScalarArray<?> source, ScalarArray<?> target)
	{
        int nd1 = source.dimensionality();
        int nd2 = target.dimensionality();
        if (nd1 != 2 || nd2 != 2)
        {
            throw new IllegalArgumentException("Both arrays must have the dimensionality 2");
        }
        
        if (!Arrays.isSameSize(source, target))
        {
            throw new IllegalArgumentException("Both arrays must have the same size");
        }

        processScalar2d(ScalarArray2D.wrap(source), ScalarArray2D.wrap(target));
	}

	public void processScalar2d(ScalarArray2D<?> source, ScalarArray2D<?> target)
	{
		int sizeX = source.getSize(0);
		int sizeY = source.getSize(1);
		
		for(int y = 0; y < sizeY; y++)
		{
		    
	        this.fireProgressChanged(this, y, sizeY);
//	        SwingUtilities.invokeLater(new Runnable() {
//	            public void run() {
//	              progressBar.setValue(i);
//	            }
//	          });
	        
			for(int x = 0; x < sizeX; x++)
			{
				double sum = 0;
				
				// iterate over neighbors
				for (int y2 = y - 1; y2 <= y + 1; y2++)
				{
					int y2r = Math.min(Math.max(y2, 0), sizeY - 1);
					for (int x2 = x - 1; x2 <= x + 1; x2++)
					{
						int x2r = Math.min(Math.max(x2, 0), sizeX - 1);
						sum += source.getValue(x2r, y2r);
					}
				}
				
				target.setValue(x, y, sum / 9);
			}
			
//			// Temporisation to test ui update
//			try 
//			{
//			    Thread.sleep(100);
//			} 
//			catch(InterruptedException ex)
//			{
//			    ex.printStackTrace();
//			}
		}
		
        this.fireProgressChanged(this, sizeY, sizeY);
	}
	
	public Array<?> createEmptyOutputArray(Array<?> array)
	{
		int[] dims = array.getSize();
		return Float32Array.create(dims);
	}

    @Override
    public ScalarArray<?> processScalar(ScalarArray<? extends Scalar> array)
    {
        ScalarArray<?> output = array.newInstance(array.getSize());
        processScalar(array, output);
        return output;
    }
}
