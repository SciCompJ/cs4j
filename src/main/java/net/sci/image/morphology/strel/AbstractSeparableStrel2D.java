/**
 * 
 */
package net.sci.image.morphology.strel;

import java.util.Collection;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoListener;
import net.sci.algo.AlgoStub;
import net.sci.array.numeric.ScalarArray2D;

/**
 * Implementation stub for separable Structuring elements.
 * 
 * @author David Legland
 *
 */
public abstract class AbstractSeparableStrel2D extends AlgoStub implements SeparableStrel2D, AlgoListener
{
    public ScalarArray2D<?> dilation(ScalarArray2D<?> array)
    {
        // Allocate memory for result
        ScalarArray2D<?> result = array.duplicate();
        
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
    
    public ScalarArray2D<?> erosion(ScalarArray2D<?> array)
    {
        // Allocate memory for result
        ScalarArray2D<?> result = array.duplicate();
        
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
    
    public ScalarArray2D<?> closing(ScalarArray2D<?> array)
    {
        // Allocate memory for result
        ScalarArray2D<?> result = array.duplicate();
        
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
    
    public ScalarArray2D<?> opening(ScalarArray2D<?> array)
    {
        // Allocate memory for result
        ScalarArray2D<?> result = array.duplicate();
        
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
    
    private void runDilation(ScalarArray2D<?> array, InPlaceStrel2D strel)
    {
        strel.addAlgoListener(this);
        strel.inPlaceDilation2d(array);
        strel.removeAlgoListener(this);
    }
    
    private void runErosion(ScalarArray2D<?> array, InPlaceStrel2D strel)
    {
        strel.addAlgoListener(this);
        strel.inPlaceErosion2d(array);
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
