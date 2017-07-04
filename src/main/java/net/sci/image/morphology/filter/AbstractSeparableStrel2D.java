/**
 * 
 */
package net.sci.image.morphology.filter;

import java.util.Collection;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoListener;
import net.sci.array.data.Array2D;

/**
 * Implementation stub for separable Structuring elements.
 * @author David Legland
 *
 */
public abstract class AbstractSeparableStrel2D extends AbstractStrel2D 
implements SeparableStrel2D, AlgoListener 
{
	public Array2D<?> dilation(Array2D<?> image)
	{
		// Allocate memory for result
		Array2D<?> result = image.duplicate();
		
		// Extract structuring elements
		Collection<InPlaceStrel2D> strels = this.decompose();
		int n = strels.size();
		
		// Dilation
		int i = 1;
		for (InPlaceStrel2D strel : strels)
		{
			fireStatusChanged(this, createStatusMessage("Dilation", i, n));
			runDilation(result, strel);
			i++;
		}
		
		// clear status bar
		fireStatusChanged(this, "");
		
		return result;
	}

	public Array2D<?> erosion(Array2D<?> image) 
	{
		// Allocate memory for result
		Array2D<?> result = image.duplicate();
		
		// Extract structuring elements
		Collection<InPlaceStrel2D> strels = this.decompose();
		int n = strels.size();
		
		// Erosion
		int i = 1;
		for (InPlaceStrel2D strel : strels)
		{
			fireStatusChanged(this, createStatusMessage("Erosion", i, n));
			runErosion(result, strel);
			i++;
		}
		
		// clear status bar
		fireStatusChanged(this, "");
		
		return result;
	}

	public Array2D<?> closing(Array2D<?> image)
	{
		// Allocate memory for result
		Array2D<?> result = image.duplicate();
		
		// Extract structuring elements
		Collection<InPlaceStrel2D> strels = this.decompose();
		int n = strels.size();
		
		// Dilation
		int i = 1;
		for (InPlaceStrel2D strel : strels)
		{
			fireStatusChanged(this, createStatusMessage("Dilation", i, n));
			runDilation(result, strel);
			i++;
		}
		
		// Erosion (with reversed strel)
		i = 1;
		strels = this.reverse().decompose();
		for (InPlaceStrel2D strel : strels)
		{
			fireStatusChanged(this, createStatusMessage("Erosion", i, n));
			runErosion(result, strel);
			i++;
		}
		
		// clear status bar
		fireStatusChanged(this, "");
		
		return result;
	}

	public Array2D<?> opening(Array2D<?> image) 
	{
		// Allocate memory for result
		Array2D<?> result = image.duplicate();
		
		// Extract structuring elements
		Collection<InPlaceStrel2D> strels = this.decompose();
		int n = strels.size();
		
		// Erosion
		int i = 1;
		for (InPlaceStrel2D strel : strels)
		{
			fireStatusChanged(this, createStatusMessage("Erosion", i, n));
			runErosion(result, strel);
			i++;
		}
		
		// Dilation (with reversed strel)
		i = 1;
		strels = this.reverse().decompose();
		for (InPlaceStrel2D strel : strels) 
		{
			fireStatusChanged(this, createStatusMessage("Dilation", i, n));
			runDilation(result, strel);
			i++;
		}
		
		// clear status bar
		fireStatusChanged(this, "");

		return result;
	}
	
	private void runDilation(Array2D<?> image, InPlaceStrel2D strel)
	{
		strel.addAlgoListener(this);
		strel.inPlaceDilation(image);
		strel.removeAlgoListener(this);
	}
	
	private void runErosion(Array2D<?> image, InPlaceStrel2D strel) 
	{
		strel.addAlgoListener(this);
		strel.inPlaceErosion(image);
		strel.removeAlgoListener(this);
	}
	
	private String createStatusMessage(String opName, int i, int n)
	{
		return opName + " " + i + "/" + n;
	}
	
	/**
	 * Propagates the event by changing the source.
	 */
	public void algoProgressChanged(AlgoEvent evt)
	{
		this.fireProgressChanged(this, evt.getCurrentProgress(), evt.getTotalProgress());
	}
	
	/**
	 * Propagates the event by changing the source.
	 */
	public void algoStatusChanged(AlgoEvent evt)
	{
		this.fireStatusChanged(this, evt.getStatus());
	}
}
