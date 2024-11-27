/**
 * 
 */
package net.sci.image.binary;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.Binary;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.process.AdditiveInverse;
import net.sci.image.Connectivity;
import net.sci.image.Connectivity2D;
import net.sci.image.Connectivity3D;
import net.sci.image.morphology.MinimaAndMaxima;
import net.sci.image.morphology.watershed.Watershed2D;
import net.sci.image.morphology.watershed.Watershed3D;

/**
 * 
 */
public class SplitCoalescentParticles extends AlgoStub implements ArrayOperator
{
    double dynamic = 1.0;

    Connectivity conn = Connectivity2D.C4;
    
    
    public void setDynamic(double dyn)
    {
        this.dynamic = dyn;
    }
    
    public void setConnectivity(Connectivity conn)
    {
        this.conn = conn;
    }
    
    @Override
    public <T> BinaryArray process(Array<T> array)
    {
        // wrap array into an instance of BinaryArray
        if (array.elementClass() != Binary.class)
        {
            throw new RuntimeException("Requires an array containing binary data");
        }
        BinaryArray binaryArray = BinaryArray.wrap(array);
        
        // switch dimensionality
        return switch (array.dimensionality())
        {
            case 2 -> processBinary2d(BinaryArray2D.wrap(binaryArray));
            case 3 -> processBinary3d(BinaryArray3D.wrap(binaryArray));
            default -> throw new RuntimeException("Currently implemented only for 2D and 3D arrays");
        };
    }
    
    public BinaryArray2D processBinary2d(BinaryArray2D array)
    {
        this.fireStatusChanged(this, "distance map");
        ScalarArray2D<?> distMap = BinaryImages.distanceMap(array);
        ScalarArray2D<?> inverted =  ScalarArray2D.wrap((ScalarArray<?>) new AdditiveInverse().view(distMap));
        
        this.fireStatusChanged(this, "extended minima");
        Connectivity2D conn2d = Connectivity2D.convert(this.conn);
        BinaryArray2D minima = MinimaAndMaxima.extendedMinima(inverted, this.dynamic, conn2d);
        this.fireStatusChanged(this, "impose minima");
        ScalarArray2D<?> imposed = MinimaAndMaxima.imposeMinima(inverted, minima, conn2d);
        this.fireStatusChanged(this, "watershed");
        ScalarArray2D<?> wat = new Watershed2D(conn2d).process(imposed);
        
        this.fireStatusChanged(this, "combine binary masks");
        BinaryArray2D res = BinaryArray2D.create(array.size(0), array.size(1));
        res.fillBooleans((x,y) -> array.getBoolean(x, y) && wat.getValue(x, y) > 0);
        
        return res;
    }
    
    public BinaryArray3D processBinary3d(BinaryArray3D array)
    {
        this.fireStatusChanged(this, "distance map");
        ScalarArray3D<?> distMap = BinaryImages.distanceMap(array);
        ScalarArray3D<?> inverted =  ScalarArray3D.wrap((ScalarArray<?>) new AdditiveInverse().view(distMap));
        
        this.fireStatusChanged(this, "extended minima");
        Connectivity3D conn3d = Connectivity3D.convert(this.conn);
        BinaryArray3D minima = MinimaAndMaxima.extendedMinima(inverted, this.dynamic, conn3d);
        this.fireStatusChanged(this, "impose minima");
        ScalarArray3D<?> imposed = MinimaAndMaxima.imposeMinima(inverted, minima, conn3d);
        this.fireStatusChanged(this, "watershed");
        ScalarArray3D<?> wat = new Watershed3D(conn3d).process(imposed);
        
        this.fireStatusChanged(this, "combine binary masks");
        BinaryArray res = BinaryArray.create(array.size());
        res.fillBooleans(pos -> array.getBoolean(pos) && wat.getValue(pos) > 0);
        
        return BinaryArray3D.wrap(res);
    }
}
