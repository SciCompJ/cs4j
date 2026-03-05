/**
 * 
 */
package net.sci.register.transform;

import net.sci.geom.geom3d.Transform3D;
import net.sci.register.ParametricObject;

/**
 * Base class for parametric transforms in three dimensions.
 * 
 * @author dlegland
 *
 */
public abstract class ParametricTransform3D extends ParametricObject implements Transform3D
{
    protected ParametricTransform3D(int nParams)
    {
        super(nParams);
    }
    
    protected ParametricTransform3D(double[] params)
    {
        super(params);
    }

}
