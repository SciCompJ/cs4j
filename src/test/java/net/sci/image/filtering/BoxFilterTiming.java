package net.sci.image.filtering;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import net.sci.array.numeric.Float32Array3D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.image.Image;
import net.sci.image.io.TiffImageReader;

public class BoxFilterTiming
{
	
	public static final void main(String... args) throws IOException
	{
		String fileName = BoxFilterTiming.class.getResource("/images/mri.tif").getFile();
		
		TiffImageReader reader = new TiffImageReader(fileName);
		Image image = reader.readImage();
		
		assertEquals(3, image.getDimension());

		ScalarArray3D<?> array = (ScalarArray3D<?>) image.getData();
		assertEquals(128, array.size(0));
		assertEquals(128, array.size(1));
		assertEquals( 27, array.size(2));

		BoxFilter filter = new BoxFilter(new int[]{5, 5, 3});
		long t0, t1;
		double dt1, dt2;
		
		System.out.println("Start comparing timing");

        Float32Array3D target1 = Float32Array3D.create(128, 128, 27);
        t0 = System.nanoTime();
        filter.processScalar3d(array, target1);
        t1 = System.nanoTime();
        dt1 = (t1 - t0) / 1000000;
        System.out.println("Scalar 3D, elapsed time: " + dt1);
        
        Float32Array3D target2 = Float32Array3D.create(128, 128, 27);
        t0 = System.nanoTime();
        filter.processScalar3d_exact(array, target2);
        t1 = System.nanoTime();
        dt1 = (t1 - t0) / 1000000;
        System.out.println("Scalar 3D exact, elapsed time: " + dt1);
        
        t0 = System.nanoTime();
        filter.processScalar(array, target1);
        t1 = System.nanoTime();
        dt2 = (t1 - t0) / 1000000;
        System.out.println("Scalar,    elapsed time: " + dt2);
        
		System.out.println("Timing ratio: " + String.format("%7.3f", dt2 / dt1));
	}
	
}
