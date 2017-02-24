/**
 * 
 */
package net.sci.register.transform;

import net.sci.geom.geom2d.transform.Transform2D;
import net.sci.register.ParametricObject;

/**
 * @author dlegland
 *
 */
public abstract class ParametricTransform2D extends ParametricObject implements
		Transform2D
{
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
//	getJacobian;
}
