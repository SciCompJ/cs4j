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
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;


/**
 * Interface for computing distance maps from binary 2D images.
 */
public interface DistanceTransform2D extends DistanceTransform
{
    // ==================================================
    // Static factories

    /**
     * Create a default algorithm for 2D chamfer mask based distance transform
     * on binary images, by specifying whether floating point computation should
     * be used, and if result distance map should be normalized.
     * 
     * @param chamferMask
     *            the 2D chamfer mask to use for propagating distances
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
    public static DistanceTransform2D create(ChamferMask2D chamferMask, boolean floatingPoint, boolean normalize)
    {
        return floatingPoint
                ? new ChamferDistanceTransform2DFloat32(chamferMask, normalize)
                : new ChamferDistanceTransform2DUInt16(chamferMask, normalize);    
    }
    

    // ==================================================
    // New methods

    /**
     * Computes the distance map from a 2D binary image. Distance is computed
     * for each foreground (white) pixel, as the chamfer distance to the nearest
     * background (black) pixel.
     * 
     * @param array
     *            a 2D boolean array with white pixels as foreground
     * @return a new 2D array containing:
     *         <ul>
     *         <li>0 for each background pixel</li>
     *         <li>the distance to the nearest background pixel otherwise</li>
     *         </ul>
     */
    public ScalarArray2D<?> process2d(BinaryArray2D array);
    
    
    // ==================================================
    // Specialization of ArrayOperator interface

    /**
     * Process the input scalar array and return the result in a new array.
     * 
     * The input array must be an instance of BinaryArray.
     * 
     * @param array
     *            the input array, must be 2D and be instance of BinaryArray.
     * @return the operator result as a new instance of ScalarArray.
     * @throws IllegalArgumentException
     *             if the input array is not an instance of BinaryArray
     */
    @Override
    public default <T> ScalarArray<? extends Scalar<?>> process(Array<T> array)
    {
        if (!(array instanceof BinaryArray2D))
        {
            throw new IllegalArgumentException("Requires a binary array as input");
        }
        
        return process2d((BinaryArray2D) array);
    }

    /**
     * Override default behavior to check if input array is binary.
     * 
     * @return true if input array is binary
     */
    @Override
    public default boolean canProcess(Array<?> array)
    {
        return array instanceof BinaryArray2D;
    }
}
