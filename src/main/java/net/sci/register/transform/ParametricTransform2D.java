/**
 * 
 */
package net.sci.register.transform;

import net.sci.geom.geom2d.Transform2D;
import net.sci.register.ParametricObject;

/**
 * Base class for parametric transforms in two dimensions.
 * 
 * @author dlegland
 *
 */
public abstract class ParametricTransform2D extends ParametricObject implements
		Transform2D
{
    protected ParametricTransform2D(int nParams)
    {
        super(nParams);
    }

    protected ParametricTransform2D(double[] params)
    {
        super(params);
    }

//	getDimension;
//
//	transformPoint;
//
//	transformVector;
//
//	jacobian();
}
