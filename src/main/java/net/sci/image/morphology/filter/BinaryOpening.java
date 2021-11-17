/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.array.binary.BinaryArray;
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
public class BinaryOpening extends BinaryMorphologicalFilterAlgo
{
    public BinaryOpening(Strel strel)
    {
        super(strel);
    }
    
    @Override
    public BinaryArray processBinary(BinaryArray array)
    {
        this.fireStatusChanged(this, "Compute Erosion");
        BinaryErosion erosion = new BinaryErosion(strel);
        BinaryArray resEro = erosion.processBinary(array);
        
        this.fireStatusChanged(this, "Compute Dilation");
        BinaryDilation dilation = new BinaryDilation(strel.reverse());
        BinaryArray res = dilation.processBinary(resEro);
        
        return res;
    }
}
