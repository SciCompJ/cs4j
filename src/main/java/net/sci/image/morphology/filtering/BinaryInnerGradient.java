/**
 * 
 */
package net.sci.image.morphology.filtering;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoListener;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.process.LogicalBinaryOperator;
import net.sci.image.morphology.BinaryMorphologicalFilter;
import net.sci.image.morphology.Strel;

/**
 * Morphological gradient (also known as Beucher gradient) of a binary array.
 * The morphological gradient consists in computing a morphological dilation,
 * and "subtracting" the morphological erosion computed with the same (reversed)
 * structuring element.
 * </p>
 * 
 * @see BinaryGradient
 * @see BinaryDilation
 * @see BinaryErosion
 * @see BinaryClosing
 * 
 * @author dlegland
 *
 */
public class BinaryInnerGradient extends BinaryMorphologicalFilter implements AlgoListener
{
    public BinaryInnerGradient(Strel strel)
    {
        super(strel);
    }
    
    public BinaryInnerGradient(Strel strel, boolean padding)
    {
        super(strel);
    }
        
    @Override
    public BinaryArray processBinary(BinaryArray array)
    {
        this.fireStatusChanged(this, "Compute Erosion");
        BinaryErosion erosion = new BinaryErosion(strel, padding);
        erosion.addAlgoListener(this);
        BinaryArray resEro = erosion.processBinary(array);
        
        this.fireStatusChanged(this, "Compute array minus erosion");
        LogicalBinaryOperator op = LogicalBinaryOperator.AND_NOT;
        op.addAlgoListener(this);
        BinaryArray res = op.process(array, resEro);
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
