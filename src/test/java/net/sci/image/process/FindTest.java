package net.sci.image.process;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array2D;
import net.sci.geom.geom2d.Point2D;

public class FindTest
{
    
    @Test
    public void testFindPixels()
    {
        UInt8Array2D img = UInt8Array2D.create(6, 5);
        img.setValue(10, 1, 1);
        img.setValue(10, 4, 2);
        img.setValue(10, 2, 3);
        img.setValue(10, 4, 4);
        
        Collection<Point2D> positions = Find.findPixels(img);
        
        assertEquals(4, positions.size());
    }
}
