/*-
 * #%L
 * Mathematical morphology library and plugins for ImageJ/Fiji.
 * %%
 * Copyright (C) 2014 - 2017 INRA.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package net.sci.image.binary.distmap;

import net.sci.array.Array;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray3D;


/**
 * Interface for computing distance maps from binary 3D images.
 */
public interface DistanceTransform3D extends DistanceTransform
{
    // ==================================================
    // Static factories

    /**
     * Create a default algorithm for 3D chamfer mask based distance transform
     * on binary images, by specifying whether floating point computation should
     * be used, and if result distance map should be normalized.
     * 
     * @param chamferMask
     *            the 3D chamfer mask to use for propagating distances
     * @param floatingPoint
     *            boolean flag indicating whether result should be provided as
     *            <code>Float32</code> (if true) or as <code>UInt16</code> (if
     *            false).
     * @param normalize
     *            indicates whether the resulting distance map should be
     *            normalized (divide distances by the first chamfer weight)
     * @return an algorithm for computing chamfer distance maps on binary
     *         images.
     */
    public static DistanceTransform3D create(ChamferMask3D chamferMask, boolean floatingPoint, boolean normalize)
    {
        return floatingPoint
                ? new ChamferDistanceTransform3DFloat32(chamferMask, normalize)
                : new ChamferDistanceTransform3DUInt16(chamferMask, normalize);    
    }
    

    // ==================================================
    // New methods

    /**
     * Computes the distance map from a 3D binary image. Distance is computed
     * for each foreground (white) pixel, as the chamfer distance to the nearest
     * background (black) pixel.
     * 
     * @param array
     *            a 3D binary image with white pixels (255) as foreground
     * @return a new 3D image containing:
     *         <ul>
     *         <li>0 for each background pixel</li>
     *         <li>the distance to the nearest background pixel otherwise</li>
     *         </ul>
     */
    public ScalarArray3D<?> process3d(BinaryArray3D array);

    
    // ==================================================
    // Specialization of ArrayOperator interface

    /**
     * Process the input scalar array and return the result in a new array.
     * 
     * The input array must be an instance of BinaryArray.
     * 
     * @param array
     *            the input array
     * @return the operator result as a new instance of ScalarArray
     * @throws IllegalArgumentException
     *             if the input array is not an instance of BinaryArray3D
     */
    @Override
    public default <T> ScalarArray<? extends Scalar<?>> process(Array<T> array)
    {
        if (!(array instanceof BinaryArray3D))
        {
            throw new IllegalArgumentException("Requires a 3D binary array as input");
        }
        
        return process3d((BinaryArray3D) array);
    }

    /**
     * Override default behavior to check if input array is binary.
     * 
     * @return true if input array is binary and 3D
     */
    @Override
    public default boolean canProcess(Array<?> array)
    {
        return array instanceof BinaryArray3D;
    }
}
