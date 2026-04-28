package net.sci.image.vectorize;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom3d.Point3D;

public class FindNonZeroElementsTest
{
    @Test
    public void test_findPixels()
    {
        UInt8Array2D img = UInt8Array2D.create(6, 5);
        img.setValue(1, 1, 10);
        img.setValue(4, 2, 10);
        img.setValue(2, 3, 10);
        img.setValue(4, 4, 10);
        
        Collection<Point2D> positions = FindNonZeroElements.findPixels(img);
        
        assertEquals(4, positions.size());
    }
    
    @Test
    public void test_findVoxels()
    {
        UInt8Array3D img = UInt8Array3D.create(6, 5, 4);
        img.setValue(1, 1, 1, 10);
        img.setValue(4, 2, 1, 20);
        img.setValue(2, 3, 1, 30);
        img.setValue(4, 4, 1, 40);
        img.setValue(4, 2, 3, 20);
        img.setValue(2, 3, 0, 30);
        
        Collection<Point3D> positions = FindNonZeroElements.findVoxels(img);
        
        assertEquals(6, positions.size());
    }
}
