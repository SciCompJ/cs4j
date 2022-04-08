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
 * Morphological opening of a binary array, that consists in computing a
 * morphological erosion followed by a morphological dilation using the same
 * (reversed) structuring element.
 * </p>
 * 
 * @see BinaryDilation
 * @see BinaryErosion
 * @see BinaryClosing
 * @see Opening
 * 
 * @author dlegland
 *
 */
public class BinaryOpening extends BinaryMorphologicalFilter implements AlgoListener
{
    public BinaryOpening(Strel strel)
    {
        super(strel);
    }
    
    public BinaryOpening(Strel strel, boolean padding)
    {
        super(strel, padding);
    }
    
    @Override
    public BinaryArray processBinary(BinaryArray array)
    {
        this.fireStatusChanged(this, "Compute Erosion");
        BinaryErosion erosion = new BinaryErosion(strel, padding);
        erosion.addAlgoListener(this);
        BinaryArray resEro = erosion.processBinary(array);
        
        this.fireStatusChanged(this, "Compute Dilation");
        BinaryDilation dilation = new BinaryDilation(strel.reverse());
        dilation.addAlgoListener(this);
        BinaryArray res = dilation.processBinary(resEro);
        
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
