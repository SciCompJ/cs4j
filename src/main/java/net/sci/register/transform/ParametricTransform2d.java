/**
 * 
 */
package net.sci.register.transform;

import net.sci.geom.geom2d.Transform2d;
import net.sci.register.ParametricObject;

/**
 * @author dlegland
 *
 */
public abstract class ParametricTransform2d extends ParametricObject implements
		Transform2d
{
	protected ParametricTransform2d(double[] params)
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
