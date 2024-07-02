package net.sci.array.numeric.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array2D;

public class SlicedUInt8Array3DTest
{

	@Test
	public final void testConstructor_Sizes()
	{
		// create array with 2*3*4 = 24 elements 
		SlicedUInt8Array3D array = new SlicedUInt8Array3D(4, 3, 2);
		array.fillInt(10);

		assertEquals(4, array.size(0));
		assertEquals(3, array.size(1));
		assertEquals(2, array.size(2));
	}
	
	@Test
	public final void testConstructor_Slices()
	{
		UInt8Array2D slice0 = UInt8Array2D.create(4, 3);
		UInt8Array2D slice1 = UInt8Array2D.create(4, 3);
        slice0.fillInt(10);
        slice1.fillInt(10);
		
		// create array with 2*3*4 = 24 elements 
		ArrayList<UInt8Array2D> slices = new ArrayList<UInt8Array2D>(2);
		slices.add(slice0);
		slices.add(slice1);
		SlicedUInt8Array3D array = new SlicedUInt8Array3D(slices);
		
		assertEquals(4, array.size(0));
		assertEquals(3, array.size(1));
		assertEquals(2, array.size(2));
	}

	@Test
	public final void testIterator()
	{
		UInt8Array2D slice0 = UInt8Array2D.create(4, 3);
		UInt8Array2D slice1 = UInt8Array2D.create(4, 3);
        slice0.fillInt(10);
        slice1.fillInt(10);
		
		// create array with 2*3*4 = 24 elements 
		ArrayList<UInt8Array2D> slices = new ArrayList<UInt8Array2D>(2);
		slices.add(slice0);
		slices.add(slice1);
		SlicedUInt8Array3D array = new SlicedUInt8Array3D(slices);

		// iterate over elements, count and sum them
		int count = 0;
		double sum = 0;
		for (UInt8 val : array) 
		{
			sum += val.getValue();
			count++;
		}
		
		assertEquals(240, sum, .01);
		assertEquals(24, count);
	}
}
