/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoListener;
import net.sci.array.binary.BinaryArray;
import net.sci.image.morphology.BinaryMorphologicalFilter;
import net.sci.image.morphology.Strel;

/**
 * Morphological closing of a binary array, that consists in computing a
 * morphological dilation followed by a morphological erosion using the same
 * (reversed) structuring element.
 * </p>
 * 
 * @see BinaryDilation
 * @see BinaryErosion
 * @see BinaryOpening
 * @see Closing
 * 
 * @author dlegland
 *
 */
public class BinaryClosing extends BinaryMorphologicalFilter implements AlgoListener
{
    public BinaryClosing(Strel strel)
    {
        super(strel);
    }
        
    public BinaryClosing(Strel strel, boolean padding)
    {
        super(strel, padding);
    }
        
    @Override
    public BinaryArray processBinary(BinaryArray array)
    {
        this.fireStatusChanged(this, "Compute Dilation");
        BinaryDilation dilation = new BinaryDilation(strel);
        dilation.addAlgoListener(this);
        BinaryArray resDil = dilation.processBinary(array);
        
        this.fireStatusChanged(this, "Compute Erosion");
        BinaryErosion erosion = new BinaryErosion(strel.reverse(), padding);
        erosion.addAlgoListener(this);
        BinaryArray res = erosion.processBinary(resDil);
        
        return res;
    }

    @Override
    public void algoProgressChanged(AlgoEvent evt)
    {
        this.fireProgressChanged(evt);
    }

    @Override
    public void algoStatusChanged(AlgoEvent evt)
    {
        this.fireStatusChanged(evt);
    }
}
