package net.sci.image.vectorize;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array2D;
import net.sci.geom.geom2d.Point2D;

public class FindTest
{
    
    @Test
    public void testFindPixels()
    {
        UInt8Array2D img = UInt8Array2D.create(6, 5);
        img.setValue(1, 1, 10);
        img.setValue(4, 2, 10);
        img.setValue(2, 3, 10);
        img.setValue(4, 4, 10);
        
        Collection<Point2D> positions = Find.findPixels(img);
        
        assertEquals(4, positions.size());
    }
}
