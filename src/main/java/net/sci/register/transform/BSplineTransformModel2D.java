/**
 * 
 */
package net.sci.register.transform;

import net.sci.array.Array2D;
import net.sci.array.impl.GenericArray2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Vector2D;
import net.sci.util.MathUtils;

/**
 * 
 */
public class BSplineTransformModel2D extends ParametricTransform2D
{
    // =============================================================
    // Class fields

    /**
     * The size of the grid defining the transform.
     */
    int[] gridSize;
    
    /**
     * The spacing in each direction between grid vertices.
     */
    double[] gridSpacing;
    
    /**
     * The coordinates of the first vertex of the grid.
     */
    double[] gridOrigin;
    
    
    // =============================================================
    // Constructor

    public BSplineTransformModel2D(int[] gridSize)
    {
        super((int) MathUtils.prod(gridSize));
        
        this.gridSize = gridSize;
        this.gridSpacing = new double[] {1.0, 1.0};
        this.gridOrigin = new double[] {0.0, 0.0};
    }

    public BSplineTransformModel2D(int[] gridSize, double[] spacing, Point2D origin)
    {
        super((int) MathUtils.prod(gridSize));
        
        this.gridSize = gridSize;
        this.gridSpacing = spacing;
        this.gridOrigin = new double[] {origin.x(), origin.y()};
    }

    
    // =============================================================
    // Methods for grid management
    
    public Array2D<Point2D> gridVertices()
    {
        Array2D<Point2D> vertices = GenericArray2D.create(gridSize[0], gridSize[1], new Point2D());
        for (int iy = 0; iy < gridSize[1]; iy++)
        {
            double vy = iy * gridSpacing[1] + gridOrigin[1];
            for (int ix = 0; ix < gridSize[0]; ix++)
            {
                double vx = ix * gridSpacing[0] + gridOrigin[0];
                vertices.set(ix, iy, new Point2D(vx, vy));
            }
        }
        return vertices;
    }
    
    public Array2D<Vector2D> vertexShifts()
    {
        Array2D<Vector2D> shifts = GenericArray2D.create(gridSize[0], gridSize[1], new Vector2D());
        for (int iy = 0; iy < gridSize[1]; iy++)
        {
            int index = iy * gridSize[0];
            for (int ix = 0; ix < gridSize[0]; ix++)
            {
                double vx = this.parameters[index + ix];
                double vy = this.parameters[index + ix + 1];
                shifts.set(ix, iy, new Vector2D(vx, vy));
            }
        }
        return shifts;
    }
    
    /**
     * Computes the determinant of the jacobian at the specified point.
     * A value equal to 1 corresponds to a conservation of the local area. A value lower than 1 indicates a shrinking, whereas a value greater than 1 indicates an expansion.
     *  
     * @param point
     *            the position to compute the Jacobian
     * @return the determinant of the Jacobian matrix
     */
    public double detJacobian(Point2D point)
    {
        double[][] jac = jacobian(point);
        double det = jac[0][0] * jac[1][1] - jac[1][0] * jac[0][1];
        return det;
    }
    
    /**
     * Computes the Jacobian matrix of the transform at the specified point. The
     * Jacobian matrix is obtained from the partial derivatives along each
     * dimension of the function of each coordinate. The result is stored in a
     * 2-by-2 array of double.
     * 
     * @param point
     *            the position to compute the Jacobian
     * @return the coefficients of the Jacobian matrix
     */
    public double[][] jacobian(Point2D point)
    {
        // initialize jacobian matrix to identity
        double[][] jac = new double[][] {{1.0, 0.0}, {0.0, 1.0}};
        
        // coordinates of point with respect to grid vertices
        double xg = (point.x() - gridOrigin[0]) / gridSpacing[0];
        double yg = (point.y() - gridOrigin[1]) / gridSpacing[1];
        
        // coordinates of containing grid tile
        int xt = (int) Math.floor(xg);
        int yt = (int) Math.floor(yg);
        
        // retrieve fractional part of the coordinates (between 0 and 1)
        double xu = xg - xt;
        double yu = yg - yt;
        
        // pre-compute the weights for adjacent tiles in x and y directions
        double[] xWeights = new double[4];
        xWeights[0] = BSplines.beta3_0(xu);
        xWeights[1] = BSplines.beta3_1(xu);
        xWeights[2] = BSplines.beta3_2(xu);
        xWeights[3] = BSplines.beta3_3(xu);
        double[] yWeights = new double[4];
        yWeights[0] = BSplines.beta3_0(yu);
        yWeights[1] = BSplines.beta3_1(yu);
        yWeights[2] = BSplines.beta3_2(yu);
        yWeights[3] = BSplines.beta3_3(yu);
        
        // pre-compute the derivative weights for adjacent tiles in x and y directions
        double[] xdWeights = new double[4];
        xdWeights[0] = BSplines.beta3_0d(xu);
        xdWeights[1] = BSplines.beta3_1d(xu);
        xdWeights[2] = BSplines.beta3_2d(xu);
        xdWeights[3] = BSplines.beta3_3d(xu);
        double[] ydWeights = new double[4];
        ydWeights[0] = BSplines.beta3_0d(yu);
        ydWeights[1] = BSplines.beta3_1d(yu);
        ydWeights[2] = BSplines.beta3_2d(yu);
        ydWeights[3] = BSplines.beta3_3d(yu);
        
        
        // Iterate over neighbor tile vertices
        for (int iy = -1; iy <= 2; iy++)
        {
            int yt2 = yt + iy;
            if (yt2 < 0 || yt2 >= gridSize[1]) continue;
            int yIndex = yt2 * gridSize[0];
            
            // compute y-coefficients of bezier function and derivative
            double wy = yWeights[iy + 1];
            double wyd = ydWeights[iy + 1];
            
            for (int ix = -1; ix <= 2; ix++)
            {
                int xt2 = xt + ix;
                if (xt2 < 0 || xt2 >= gridSize[0]) continue;
                
                // compute x-coefficients of bezier function and derivative
                double wx = xWeights[ix + 1];
                double wxd = xdWeights[ix + 1];
                
                // identify shift associated to current grid vertex
                double dvx = this.parameters[(yIndex + xt2) * 2];
                double dvy = this.parameters[(yIndex + xt2) * 2 + 1];
                
                // update parameters of jacobian
                jac[0][0] += (wxd * wy * dvx / gridSpacing[0]);
                jac[0][1] += (wx * wyd * dvx / gridSpacing[1]);
                jac[1][0] += (wxd * wy * dvy / gridSpacing[0]);
                jac[1][1] += (wx * wyd * dvy / gridSpacing[1]);
            }
        }
        
        return jac;
    }
    
    // =============================================================
    // Implementation of the Transform2D interface

    @Override
    public Point2D transform(Point2D point)
    {
        // compute position wrt the grid vertices
        double xg = (point.x() - gridOrigin[0]) / gridSpacing[0];
        double yg = (point.y() - gridOrigin[1]) / gridSpacing[1];

        // coordinates of containing grid tile
        int xt = (int) Math.floor(xg);
        int yt = (int) Math.floor(yg);
                
        // coordinates within the unit tile
        double xu = xg - xt;
        double yu = yg - yt;
        
        // compute weights for adjacent tiles in x direction
        double[] xWeights = new double[4];
        xWeights[0] = BSplines.beta3_0(xu);
        xWeights[1] = BSplines.beta3_1(xu);
        xWeights[2] = BSplines.beta3_2(xu);
        xWeights[3] = BSplines.beta3_3(xu);
        
        // compute weights for adjacent tiles in y direction
        double[] yWeights = new double[4];
        yWeights[0] = BSplines.beta3_0(yu);
        yWeights[1] = BSplines.beta3_1(yu);
        yWeights[2] = BSplines.beta3_2(yu);
        yWeights[3] = BSplines.beta3_3(yu);
        
        
        // initialize shift values
        double vx = 0.0;
        double vy = 0.0;
        
        // iterate over neighbor tiles
        for (int iy = -1; iy <= 2; iy++)
        {
            int yt2 = yt + iy;
            if (yt2 < 0 || yt2 >= gridSize[1]) continue;
            int yIndex = yt2 * gridSize[0];

            // weight associated to y coord
            double wy = yWeights[iy + 1];
                    
            for (int ix = -1; ix <= 2; ix++)
            {
                int xt2 = xt + ix;
                if (xt2 < 0 || xt2 >= gridSize[0]) continue;
                
                // weight associated to x coord
                double wx = xWeights[ix + 1];
                
                // update shift
                vx += this.parameters[(yIndex + xt2) * 2] * wx * wy;
                vy += this.parameters[(yIndex + xt2) * 2 + 1] * wx * wy;
            }
        }
        
        // add interpolated shift to current point
        return point.translate(vx, vy);
    }
}
