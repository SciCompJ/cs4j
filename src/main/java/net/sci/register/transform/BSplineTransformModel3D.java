/**
 * 
 */
package net.sci.register.transform;

import net.sci.array.Array3D;
import net.sci.array.impl.GenericArray3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Vector3D;
import net.sci.util.MathUtils;

/**
 * 
 */
public class BSplineTransformModel3D extends ParametricTransform3D
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

    public BSplineTransformModel3D(int[] gridSize)
    {
        super((int) MathUtils.prod(gridSize));
        
        this.gridSize = gridSize;
        this.gridSpacing = new double[] {1.0, 1.0, 1.0};
        this.gridOrigin = new double[] {0.0, 0.0, 0.0};
    }

    public BSplineTransformModel3D(int[] gridSize, double[] spacing, Point3D origin)
    {
        super((int) MathUtils.prod(gridSize));
        
        this.gridSize = gridSize;
        this.gridSpacing = spacing;
        this.gridOrigin = new double[] {origin.x(), origin.y(), origin.z()};
    }

    
    // =============================================================
    // Methods for grid management
    
    public Array3D<Point3D> gridVertices()
    {
        Array3D<Point3D> vertices = GenericArray3D.create(gridSize[0], gridSize[1], gridSize[2], new Point3D());
        for (int iz = 0; iz < gridSize[2]; iz++)
        {
            double vz = iz * gridSpacing[2] + gridOrigin[2];
            for (int iy = 0; iy < gridSize[1]; iy++)
            {
                double vy = iy * gridSpacing[1] + gridOrigin[1];
                for (int ix = 0; ix < gridSize[0]; ix++)
                {
                    double vx = ix * gridSpacing[0] + gridOrigin[0];
                    vertices.set(ix, iy, iz, new Point3D(vx, vy, vz));
                }
            }
        }
        return vertices;
    }
    
    public Array3D<Vector3D> vertexShifts()
    {
        Array3D<Vector3D> shifts = GenericArray3D.create(gridSize[0], gridSize[1], gridSize[2], new Vector3D());
        for (int iz = 0; iz < gridSize[2]; iz++)
        {
            int indexY = iz * gridSize[1] * gridSize[0] * 3;
            for (int iy = 0; iy < gridSize[1]; iy++)
            {
                int index = indexY + iy * gridSize[0] * 3;
                for (int ix = 0; ix < gridSize[0]; ix++)
                {
                    int indX = index + 3 * ix;
                    double vx = this.parameters[indX];
                    double vy = this.parameters[indX + 1];
                    double vz = this.parameters[indX + 2];
                    shifts.set(ix, iy, iz, new Vector3D(vx, vy, vz));
                }
            }
        }
        return shifts;
    }
    
    /**
     * Computes the determinant of the jacobian at the specified point. A value
     * equal to 1 corresponds to a conservation of the local area. A value lower
     * than 1 indicates a shrinking, whereas a value greater than 1 indicates an
     * expansion.
     * 
     * @param point
     *            the position to compute the Jacobian
     * @return the determinant of the Jacobian matrix
     */
    public double detJacobian(Point3D point)
    {
        double[][] jac = jacobian(point);
        double det = 0;
        det += jac[0][0] * (jac[1][1] * jac[2][2] - jac[1][2] * jac[2][1]);
        det += jac[1][0] * (jac[0][2] * jac[2][1] - jac[0][1] * jac[2][2]);
        det += jac[2][0] * (jac[0][1] * jac[1][2] - jac[0][2] * jac[1][1]);
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
    public double[][] jacobian(Point3D point)
    {
        // initialize jacobian matrix to identity
        double[][] jac = new double[][] {{1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 1.0}};
        
        // coordinates of point with respect to grid vertices
        double xg = (point.x() - gridOrigin[0]) / gridSpacing[0];
        double yg = (point.y() - gridOrigin[1]) / gridSpacing[1];
        double zg = (point.z() - gridOrigin[2]) / gridSpacing[2];
        
        // coordinates of containing grid tile
        int xt = (int) Math.floor(xg);
        int yt = (int) Math.floor(yg);
        int zt = (int) Math.floor(zg);
        
        // retrieve fractional part of the coordinates (between 0 and 1)
        double xu = xg - xt;
        double yu = yg - yt;
        double zu = zg - zt;
        
        // pre-compute the weights for adjacent tiles in x, y and z directions
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
        double[] zWeights = new double[4];
        zWeights[0] = BSplines.beta3_0(zu);
        zWeights[1] = BSplines.beta3_1(zu);
        zWeights[2] = BSplines.beta3_2(zu);
        zWeights[3] = BSplines.beta3_3(zu);
        
        // pre-compute the derivative weights for adjacent tiles in x, y and z directions
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
        double[] zdWeights = new double[4];
        zdWeights[0] = BSplines.beta3_0d(zu);
        zdWeights[1] = BSplines.beta3_1d(zu);
        zdWeights[2] = BSplines.beta3_2d(zu);
        zdWeights[3] = BSplines.beta3_3d(zu);
        
        
        // Iterate over neighbor tile vertices
        for (int iz = -1; iz <= 2; iz++)
        {
            int zt2 = yt + iz;
            if (zt2 < 0 || zt2 >= gridSize[1]) continue;
            int zIndex = zt2 * gridSize[0] * gridSize[1];
            
            // compute z-coefficients of bezier function and derivative
            double wz = zWeights[iz + 1];
            double wzd = zdWeights[iz + 1];
            
            for (int iy = -1; iy <= 2; iy++)
            {
                int yt2 = yt + iy;
                if (yt2 < 0 || yt2 >= gridSize[1]) continue;
                int yIndex = zIndex + yt2 * gridSize[0];
                
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
                    int xIndex = (yIndex + xt2) * 3;
                    double dvx = this.parameters[xIndex];
                    double dvy = this.parameters[xIndex + 1];
                    double dvz = this.parameters[xIndex + 2];
                    
                    // update parameters of jacobian
                    jac[0][0] += (wxd * wy * wz * dvx / gridSpacing[0]);
                    jac[0][1] += (wx * wyd * wz * dvx / gridSpacing[1]);
                    jac[0][2] += (wx * wy * wzd * dvx / gridSpacing[2]);
                    jac[1][0] += (wxd * wy * wz * dvy / gridSpacing[0]);
                    jac[1][1] += (wx * wyd * wz * dvy / gridSpacing[1]);
                    jac[1][2] += (wx * wy * wzd * dvy / gridSpacing[2]);
                    jac[2][0] += (wxd * wy * wz * dvz / gridSpacing[0]);
                    jac[2][1] += (wx * wyd * wz * dvz / gridSpacing[1]);
                    jac[2][2] += (wx * wy * wzd * dvz / gridSpacing[2]);
                }
            }
        }
        return jac;
    }
    
    // =============================================================
    // Implementation of the Transform2D interface

    @Override
    public Point3D transform(Point3D point)
    {
        // compute position wrt the grid vertices
        double xg = (point.x() - gridOrigin[0]) / gridSpacing[0];
        double yg = (point.y() - gridOrigin[1]) / gridSpacing[1];
        double zg = (point.z() - gridOrigin[2]) / gridSpacing[2];

        // coordinates of containing grid tile
        int xt = (int) Math.floor(xg);
        int yt = (int) Math.floor(yg);
        int zt = (int) Math.floor(zg);
                
        // coordinates within the unit tile
        double xu = xg - xt;
        double yu = yg - yt;
        double zu = zg - zt;
        
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
        
        // compute weights for adjacent tiles in z direction
        double[] zWeights = new double[4];
        zWeights[0] = BSplines.beta3_0(zu);
        zWeights[1] = BSplines.beta3_1(zu);
        zWeights[2] = BSplines.beta3_2(zu);
        zWeights[3] = BSplines.beta3_3(zu);
        
        
        // initialize shift values
        double vx = 0.0;
        double vy = 0.0;
        double vz = 0.0;
        
        // iterate over neighbor tiles
        for (int iz = -1; iz <= 2; iz++)
        {
            int zt2 = zt + iz;
            if (zt2 < 0 || zt2 >= gridSize[2]) continue;
            int zIndex = zt2 * gridSize[0] * gridSize[1];

            // weight associated to y coord
            double wz = zWeights[iz + 1];
            
            for (int iy = -1; iy <= 2; iy++)
            {
                int yt2 = yt + iy;
                if (yt2 < 0 || yt2 >= gridSize[1]) continue;
                int yIndex = zIndex + yt2 * gridSize[0];
                
                // weight associated to y coord
                double wy = yWeights[iy + 1];
                
                for (int ix = -1; ix <= 2; ix++)
                {
                    int xt2 = xt + ix;
                    if (xt2 < 0 || xt2 >= gridSize[0]) continue;
                    
                    // weight associated to x coord
                    double wx = xWeights[ix + 1];
                    
                    // global weight associated to current vertex
                    double wxyz = wx * wy * wz;
                   
                    // update shift
                    int index = (yIndex + xt2) * 3;
                    vx += this.parameters[index] * wxyz;
                    vy += this.parameters[index + 1] * wxyz;
                    vz += this.parameters[index + 2] * wxyz;
                }
            }
        }
        // add interpolated shift to current point
        return point.translate(vx, vy, vz);
    }
}
