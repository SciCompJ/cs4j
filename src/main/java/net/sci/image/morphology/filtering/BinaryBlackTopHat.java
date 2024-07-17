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
 * Applies Black Top-hat on the binary array, by applying binary closing on the
 * array and retaining elements that do not belong to original array.
 * </p>
 * 
 * Example of use:
 * 
 * <pre>
 * {@code
 * BinaryArray inputArray = ...
 * Strel strel = SquareStrel.fromRadius(2);
 * MorphologicalFilterAlgo filter = new BinaryBlackTopHat(strel);
 * BinaryArray result = filter.process(inputArray);
 * }
 * </pre>
 * 
 * @see BinaryOpening
 * @see BlackTopHat
 * @see BinaryWhiteTopHat
 * @see BinaryDilation
 * @see BinaryErosion
 * 
 * @author dlegland
 *
 */
public class BinaryBlackTopHat extends BinaryMorphologicalFilter implements AlgoListener
{
    public BinaryBlackTopHat(Strel strel)
    {
        super(strel);
    }
    
    public BinaryBlackTopHat(Strel strel, boolean padding)
    {
        super(strel);
    }
        
    @Override
    public BinaryArray processBinary(BinaryArray array)
    {
        this.fireStatusChanged(this, "Compute Closing");
        BinaryClosing closing = new BinaryClosing(strel, padding);
        closing.addAlgoListener(this);
        BinaryArray resCl = closing.processBinary(array);
        
        this.fireStatusChanged(this, "Compute closing minus array");
        LogicalBinaryOperator op = LogicalBinaryOperator.AND_NOT;
        op.addAlgoListener(this);
        BinaryArray res = op.process(resCl, array);
        
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
