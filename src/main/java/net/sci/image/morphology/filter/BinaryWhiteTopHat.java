/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoListener;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.process.LogicalBinaryOperator;
import net.sci.image.morphology.BinaryMorphologicalFilter;
import net.sci.image.morphology.Strel;

/**
 * Applies Top-hat on the binary array, by retaining only elements from the
 * original array that disappear after an opening.
 * </p>
 * 
 * Example of use:
 * 
 * <pre>
 * {@code
 * BinaryArray inputArray = ...
 * Strel strel = SquareStrel.fromRadius(2);
 * MorphologicalFilterAlgo filter = new BinaryWhiteTopHat(strel);
 * BinaryArray result = filter.process(inputArray);
 * }
 * </pre>
 * 
 * @see BinaryOpening
 * @see WhiteTopHat
 * @see BinaryBlackTopHat
 * @see BinaryDilation
 * @see BinaryErosion
 * 
 * @author dlegland
 *
 */
public class BinaryWhiteTopHat extends BinaryMorphologicalFilter implements AlgoListener
{
    public BinaryWhiteTopHat(Strel strel)
    {
        super(strel);
    }
    
    public BinaryWhiteTopHat(Strel strel, boolean padding)
    {
        super(strel);
    }
        
    @Override
    public BinaryArray processBinary(BinaryArray array)
    {
        this.fireStatusChanged(this, "Compute Opening");
        BinaryOpening opening = new BinaryOpening(strel, padding);
        opening.addAlgoListener(this);
        BinaryArray resOp = opening.processBinary(array);
        
        this.fireStatusChanged(this, "Compute array minus opening");
        LogicalBinaryOperator op = LogicalBinaryOperator.AND_NOT;
        op.addAlgoListener(this);
        BinaryArray res = op.process(array, resOp);
        
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
