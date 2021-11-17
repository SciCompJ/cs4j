/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.array.binary.BinaryArray;
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
public class BinaryClosing extends BinaryMorphologicalFilterAlgo
{
    public BinaryClosing(Strel strel)
    {
        super(strel);
    }
        
    @Override
    public BinaryArray processBinary(BinaryArray array)
    {
        this.fireStatusChanged(this, "Compute Dilation");
        BinaryDilation dilation = new BinaryDilation(strel);
        BinaryArray resDil = dilation.processBinary(array);
        
        this.fireStatusChanged(this, "Compute Erosion");
        BinaryErosion erosion = new BinaryErosion(strel.reverse());
        BinaryArray res = erosion.processBinary(resDil);
        
        return res;
    }
}
