/**
 * 
 */
package net.sci.image.process.shape;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import net.sci.array.Array2D;
import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array2D;

/**
 * @author dlegland
 *
 */
public class MontageTest
{

    /**
     * Test method for {@link net.sci.image.process.shape.Montage#create(int, int, java.util.Collection)}.
     */
    @Test
    public final void testCreateCollection_2x2()
    {
        // create a collection of four arrays
        ArrayList<UInt8Array2D> arrays = new ArrayList<>(4);
        UInt8Array2D tmp1 = UInt8Array2D.create(3, 3);
        tmp1.fillInt(10);
        UInt8Array2D tmp2 = UInt8Array2D.create(3, 3);
        tmp2.fillInt(20);
        UInt8Array2D tmp3 = UInt8Array2D.create(3, 3);
        tmp3.fillInt(30);
        UInt8Array2D tmp4 = UInt8Array2D.create(3, 3);
        tmp4.fillInt(40);
        arrays.add(tmp1);
        arrays.add(tmp2);
        arrays.add(tmp3);
        arrays.add(tmp4);
        
        Array2D<UInt8> res = Montage.create(2, 2, arrays);
        
//        System.out.println(res);
        
        assertEquals(6, res.size(0));
        assertEquals(6, res.size(1));
    }

    /**
     * Test method for {@link net.sci.image.process.shape.Montage#create(int, int, java.util.Collection)}.
     */
    @Test
    public final void testCreateCollection_2x2_differentSizes()
    {
        // create a collection of four arrays
        ArrayList<UInt8Array2D> arrays = new ArrayList<>(4);
        UInt8Array2D tmp1 = UInt8Array2D.create(4, 2);
        tmp1.fillInt(10);
        UInt8Array2D tmp2 = UInt8Array2D.create(3, 3);
        tmp2.fillInt(20);
        UInt8Array2D tmp3 = UInt8Array2D.create(3, 2);
        tmp3.fillInt(30);
        UInt8Array2D tmp4 = UInt8Array2D.create(2, 4);
        tmp4.fillInt(40);
        arrays.add(tmp1);
        arrays.add(tmp2);
        arrays.add(tmp3);
        arrays.add(tmp4);
        
        Array2D<UInt8> res = Montage.create(2, 2, arrays);
        
//        System.out.println(res);
        
        assertEquals(7, res.size(0));
        assertEquals(7, res.size(1));
    }

    /**
     * Test method for {@link net.sci.image.process.shape.Montage#create(int, int, net.sci.array.Array2D<T>[])}.
     */
    @Test
    public final void testCreateIntIntArray2DOfTArray()
    {
        // create a collection of four arrays
        UInt8Array2D tmp1 = UInt8Array2D.create(3, 3);
        tmp1.fillInt(10);
        UInt8Array2D tmp2 = UInt8Array2D.create(3, 3);
        tmp2.fillInt(20);
        UInt8Array2D tmp3 = UInt8Array2D.create(3, 3);
        tmp3.fillInt(30);
        UInt8Array2D tmp4 = UInt8Array2D.create(3, 3);
        tmp4.fillInt(40);
        
        @SuppressWarnings("unchecked")
        Array2D<UInt8> res = Montage.create(2, 2, tmp1, tmp2, tmp3, tmp4);
        
        assertEquals(6, res.size(0));
        assertEquals(6, res.size(1));
//        System.out.println(res);
    }

}
