/**
 * 
 */
package net.sci.geom.geom3d;

import java.util.Locale;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

/**
 * Implementation of 3D rotation based on quaternions.
 *
 * The Rotation3D class represents a 3D (linear) rotation. It is based on
 * quaternion computation, and does not include the translation part of affine
 * transforms.
 *
 * Rotation3D can be useful for converting from and to Euler angles, and
 * concatenating rotations.
 *
 *
 * References
 * https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/index.html
 * 
 * @author dlegland
 *
 */
public class Rotation3D
{
    // ===================================================================
    // Static methods
    
    /**
     * Creates a Rotation3D from a 3-by-3 or 4-by-4 rotation matrix.
     * 
     * @param matrix
     *            a 3-by-3 or 4-by-4 array corresponding to the linear part of a
     *            3D rotation matrix. First array index corresponds to row index.
     * @return the Rotation3D instance corresponding to the rotation matrix
     */
    public static final Rotation3D fromMatrix(double[][] matrix)
    {
        // check size of input matrix
        int nRows = matrix.length;
        int nCols = matrix[0].length;
        if (nRows != nCols || (nRows != 3 && nRows != 4) || (nCols != 3 && nCols != 4))
        {
            throw new IllegalArgumentException("Requires a 4-by-4 or a 3-by-3 rotation matrix.");
        }
        
        // check determinant, allowing also determinant equal to -1 
        Matrix mat = new Matrix(matrix);
        if (Math.abs(mat.det()) - 1 > 1e-8)
        {
            throw new IllegalArgumentException("Requires a rotation matrix with determinant equal to 1.0, here equal to " + mat.det());
        }
        
        // Use formula from Wikipedia, english version
        Matrix K = new Matrix(new double[][] {
            {matrix[0][0]-matrix[1][1]-matrix[2][2], matrix[1][0]+matrix[0][1], matrix[2][0]+matrix[0][2], matrix[2][1]-matrix[1][2]}, 
            {matrix[1][0]+matrix[0][1], matrix[1][1]-matrix[0][0]-matrix[2][2], matrix[2][1]+matrix[1][2], matrix[0][2]-matrix[2][0]}, 
            {matrix[2][0]+matrix[0][2], matrix[2][1]+matrix[1][2], matrix[2][2]-matrix[0][0]-matrix[1][1], matrix[1][0]-matrix[0][1]}, 
            {matrix[2][1]-matrix[1][2], matrix[0][2]-matrix[2][0], matrix[1][0]-matrix[0][1], matrix[0][0]+matrix[1][1]+matrix[2][2]}});
        
        // perform singular value decomposition
        // The first eigen vector corresponds to largest eigen value by construction of SVD
        SingularValueDecomposition svd = new SingularValueDecomposition(K);
        Matrix U = svd.getU();
        
        if (U.get(3, 0) >= 0.0)
        {
            // positive case
            return new Rotation3D(U.get(3, 0), U.get(0, 0), U.get(1, 0), U.get(2, 0));
        }
        else
        {
            return new Rotation3D(-U.get(3, 0), -U.get(0, 0), -U.get(1, 0), -U.get(2, 0));
        }
    }
    
    public static final Rotation3D fromEulerAngles(double angle1, double angle2, double angle3)
    {
        // elementary rotations
        Rotation3D rot1 = fromAxisAngle(Vector3D.E_1, angle1);
        Rotation3D rot2 = fromAxisAngle(Vector3D.E_2, angle2);
        Rotation3D rot3 = fromAxisAngle(Vector3D.E_3, angle3);
        
        // return composition of rotations
        return rot3.compose(rot2).compose(rot1);
    }
    
    /**
     * Creates a new Rotation3D from an axis the rotation angle around that axis.
     * 
     * @return a new Rotation3D corresponding to the rotation by angle
     *         "angleInRadians" around the axis specified by input vector.
     */
    public static final Rotation3D fromAxisAngle(Vector3D axis, double angleInRadians)
    {
        // pre-computations
        axis = axis.normalize();
        double halfAngle = angleInRadians * 0.5;
        double sinHalf = Math.sin(halfAngle);
        
        // compute coefficients
        double q0 = Math.cos(halfAngle);
        double q1 = axis.x() * sinHalf;
        double q2 = axis.y() * sinHalf;
        double q3 = axis.z() * sinHalf;
        return new Rotation3D(q0, q1, q2, q3);
    }
    

    // ===================================================================
    // Class variables

    /** The scalar coordinate of the quaternion. */
    private double q0;
    
    /** The first coordinate of the vectorial part of the quaternion. */
    private double q1;
    
    /** The second coordinate of the vectorial part of the quaternion. */
    private double q2;
    
    /** The third coordinate of the vectorial part of the quaternion. */
    private double q3;
    
    
    // ===================================================================
    // Constructors
    
    /**
     * Empty constructor, corresponding to unit quaternion (1,0,0,0).
     */
    public Rotation3D()
    {
        this.q0 = 1.0;
        this.q1 = 0.0;
        this.q2 = 0.0;
        this.q3 = 0.0;
    }

    /**
     * Initialization constructor from the four quaternion components.
     * Components are expected to be normalized.
     * 
     * @param q0
     *            the scalar coordinate of the quaternion
     * @param q1
     *            the first coordinate of the vectorial part of the quaternion
     * @param q2
     *            the second coordinate of the vectorial part of the quaternion
     * @param q3
     *            the third coordinate of the vectorial part of the quaternion
     */
    private Rotation3D(double q0, double q1, double q2, double q3)
    {
        this.q0 = q0;
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
    }
    
    
    // ===================================================================
    // Methods
    
    /**
     * Applies this 3D rotation to a vector.
     * 
     * @param v
     *            the vector to rotate
     * @return the rotated vector
     */
    public Vector3D rotate(Vector3D v)
    {
        double[][] mat = affineMatrix();
        
        double x2 = mat[0][0] * v.x() + mat[0][1] * v.y() + mat[0][2] * v.z();
        double y2 = mat[1][0] * v.x() + mat[1][1] * v.y() + mat[1][2] * v.z();
        double z2 = mat[2][0] * v.x() + mat[2][1] * v.y() + mat[2][2] * v.z();
        
        return new Vector3D(x2, y2, z2);
    }
    
    public double[] eulerAngles()
    {
        double phi, theta, psi;

        // extract |cos(theta)|
        double[][] mat = affineMatrix();
        double cy = Math.hypot(mat[0][0], mat[1][0]);
        // avoid dividing by 0
        if (cy > 16*1e-16)
        {
            // normal case: theta <> 0
            phi   = Math.atan2( mat[1][0], mat[0][0]);
            theta = Math.atan2(-mat[2][0], cy);
            psi   = Math.atan2( mat[2][1], mat[2][2]);
        }
        else
        {
            phi   = 0;
            theta = Math.atan2(-mat[2][0], cy);
            psi   = Math.atan2(-mat[1][2], mat[1][1]);
        }
        
        // concatenate into an array
        return new double[] {psi, theta, phi};
    }
    
    /**
     * Retrieves the rotation axis of this 3D rotation.
     * 
     * @return the rotation axis of this 3D rotation.
     */
    public Vector3D axis()
    {
        double norm = Math.sqrt(this.q1 * this.q1 + this.q2 * this.q2 + this.q3 * this.q3);
        if (norm > 1e-12)
            return new Vector3D(q1 / norm, q2 / norm, q3 / norm);
        else
            return Vector3D.E_1;
    }
    
    /**
     * Retrieves the rotation angle of this 3D rotation.
     * 
     * @return the rotation angle of this 3D rotation.
     */
    public double angle()
    {
        double norm = Math.sqrt(this.q1*this.q1 + this.q2*this.q2 + this.q3*this.q3);
        return 2 * Math.atan2(norm, this.q0);
    }
    
    
    // ===================================================================
    // Utility methods
    
    /**
     * Converts this Rotation3D into a 4-by-4 rotation matrix.
     * 
     * @return the 4-by-4 rotation matrix corresponding to this rotation.
     */
    public double[][] affineMatrix()
    {
        // pre-compute pair products
        double q00 = this.q0 * this.q0;
        double q01 = this.q0 * this.q1;
        double q02 = this.q0 * this.q2;
        double q03 = this.q0 * this.q3;
        double q11 = this.q1 * this.q1;
        double q12 = this.q1 * this.q2;
        double q13 = this.q1 * this.q3;
        double q22 = this.q2 * this.q2;
        double q23 = this.q2 * this.q3;
        double q33 = this.q3 * this.q3;

        // create the matrix
        double[][] res = new double[][] {
            {q00 + q11 - q22 - q33, 2 * (q12 - q03), 2 * (q02 + q13), 0},
            {2 * (q03 + q12), q00 - q11 + q22 - q33, 2 * (q23 - q01), 0}, 
            {2 * (q13 - q02), 2 * (q01 + q23), q00 - q11 - q22 + q33, 0}, 
            {0, 0, 0, 1} };
            return res;
    }
    
    public Rotation3D inverse()
    {
        return new Rotation3D(q0, -q1, -q2, -q3);
    }
    
    /**
     * Composition of two rotations, corresponding to the product of the
     * corresponding quaternions.
     * 
     * @param that
     *            the rotation to compose with
     * @return the result of the product this * that.
     */
    public Rotation3D compose(Rotation3D that)
    {
        // computes component values of new rotation
        double nq0 = this.q0 * that.q0 - this.q1 * that.q1 - this.q2 * that.q2 - this.q3 * that.q3;
        double nq1 = this.q0 * that.q1 + this.q1 * that.q0 + this.q2 * that.q3 - this.q3 * that.q2;
        double nq2 = this.q0 * that.q2 - this.q1 * that.q3 + this.q2 * that.q0 + this.q3 * that.q1;
        double nq3 = this.q0 * that.q3 + this.q1 * that.q2 - this.q2 * that.q1 + this.q3 * that.q0;

        // encapsulates into rotation object
        return new Rotation3D(nq0, nq1, nq2, nq3);
    }
    
    public boolean isIdentity(double tol)
    {
        if (Math.abs(this.q0 - 1.0) > tol) return false;
        if (Math.abs(this.q1) > tol) return false;
        if (Math.abs(this.q2) > tol) return false;
        if (Math.abs(this.q3) > tol) return false;
        return true;
    }
    
    public boolean almostEquals(Rotation3D that, double tol)
    {
        if (Math.abs(this.q0 - that.q0) > tol) return false;
        if (Math.abs(this.q1 - that.q1) > tol) return false;
        if (Math.abs(this.q2 - that.q2) > tol) return false;
        if (Math.abs(this.q3 - that.q3) > tol) return false;
        return true;
    }
    
    @Override
    public String toString()
    {
        return String.format(Locale.ENGLISH, "Rotation3D(%f, %f, %f, %f)", q0, q1, q2, q3);
    }
}
