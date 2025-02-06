/**
 * 
 */
package net.sci.image.morphology.strel;

import java.util.Collection;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoListener;
import net.sci.algo.AlgoStub;
import net.sci.array.numeric.ScalarArray3D;

/**
 * Implementation stub for separable Structuring elements.
 * 
 * @author David Legland
 *
 */
public abstract class AbstractSeparableStrel3D extends AlgoStub implements SeparableStrel3D, AlgoListener
{
    public ScalarArray3D<?> dilation(ScalarArray3D<?> array)
    {
        // Allocate memory for result
        ScalarArray3D<?> result = array.duplicate();
        
        // Extract structuring elements
        Collection<InPlaceStrel3D> strels = this.decompose();
        int n = strels.size();
        
        // Dilation
        int i = 1;
        for (InPlaceStrel3D strel : strels)
        {
            fireStatusChanged(this, createStatusMessage("Dilation", i, n));
            runDilation(result, strel);
            i++;
        }
        
        // clear status bar
        fireStatusChanged(this, "");
        
        return result;
    }
    
    public ScalarArray3D<?> erosion(ScalarArray3D<?> array)
    {
        // Allocate memory for result
        ScalarArray3D<?> result = array.duplicate();
        
        // Extract structuring elements
        Collection<InPlaceStrel3D> strels = this.decompose();
        int n = strels.size();
        
        // Erosion
        int i = 1;
        for (InPlaceStrel3D strel : strels)
        {
            fireStatusChanged(this, createStatusMessage("Erosion", i, n));
            runErosion(result, strel);
            i++;
        }
        
        // clear status bar
        fireStatusChanged(this, "");
        
        return result;
    }
    
    public ScalarArray3D<?> closing(ScalarArray3D<?> array)
    {
        // Allocate memory for result
        ScalarArray3D<?> result = array.duplicate();
        
        // Extract structuring elements
        Collection<InPlaceStrel3D> strels = this.decompose();
        int n = strels.size();
        
        // Dilation
        int i = 1;
        for (InPlaceStrel3D strel : strels)
        {
            fireStatusChanged(this, createStatusMessage("Dilation", i, n));
            runDilation(result, strel);
            i++;
        }
        
        // Erosion (with reversed strel)
        i = 1;
        strels = this.reverse().decompose();
        for (InPlaceStrel3D strel : strels)
        {
            fireStatusChanged(this, createStatusMessage("Erosion", i, n));
            runErosion(result, strel);
            i++;
        }
        
        // clear status bar
        fireStatusChanged(this, "");
        
        return result;
    }
    
    public ScalarArray3D<?> opening(ScalarArray3D<?> array)
    {
        // Allocate memory for result
        ScalarArray3D<?> result = array.duplicate();
        
        // Extract structuring elements
        Collection<InPlaceStrel3D> strels = this.decompose();
        int n = strels.size();
        
        // Erosion
        int i = 1;
        for (InPlaceStrel3D strel : strels)
        {
            fireStatusChanged(this, createStatusMessage("Erosion", i, n));
            runErosion(result, strel);
            i++;
        }
        
        // Dilation (with reversed strel)
        i = 1;
        strels = this.reverse().decompose();
        for (InPlaceStrel3D strel : strels)
        {
            fireStatusChanged(this, createStatusMessage("Dilation", i, n));
            runDilation(result, strel);
            i++;
        }
        
        // clear status bar
        fireStatusChanged(this, "");
        
        return result;
    }
    
    private void runDilation(ScalarArray3D<?> array, InPlaceStrel3D strel)
    {
        strel.addAlgoListener(this);
        strel.inPlaceDilation3d(array);
        strel.removeAlgoListener(this);
    }
    
    private void runErosion(ScalarArray3D<?> array, InPlaceStrel3D strel)
    {
        strel.addAlgoListener(this);
        strel.inPlaceErosion3d(array);
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
