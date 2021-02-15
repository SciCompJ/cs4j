package net.sci.array.color;

import net.sci.array.Arrays;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArrayUInt8View;
import net.sci.array.scalar.UInt8Array;

/**
 * A "virtual" RGB8 array that represents the intensity differences between two
 * arrays containing scalar values. Intensities in the first array are
 * represented in magenta, while intensities in the second array are represented
 * in green. Identical intensities are therefore represented as white.
 * 
 * This class is a view, and computes the RGB8 on the fly values depending on
 * the requested element positions. To generate a modifiable array, the
 * "duplicate" method can be employed.
 * 
 * 
 * @author dlegland
 *
 */public class ScalarArraysDifferenceView implements RGB8Array
{
     // =============================================================
     // Class variables

     /**
      * The first array, represented in magenta.
      */
     ScalarArray<?> array1;
     
     /**
      * The value associated to black for first array.
      */
     double vmin1;

     /**
      * The value associated to magenta for first array.
      */
     double vmax1;

     
     /**
      * The second array, represented in green.
      */
     ScalarArray<?> array2;
     
     /**
      * The value associated to black for second array.
      */
     double vmin2;

     /**
      * The value associated to green for second array.
      */
     double vmax2;
     
     
     // =============================================================
     // Constructors
     
     public ScalarArraysDifferenceView(UInt8Array array1, UInt8Array array2)
     {
         if (!Arrays.isSameDimensionality(array1, array2))
         {
             int d1 = array1.dimensionality();
             int d2 = array2.dimensionality();
             throw new IllegalArgumentException(String.format("Both arrays must have same dimensionality (here, %d and %d)", d1, d2));
         }
         if (!Arrays.isSameSize(array1, array2))
         {
             throw new IllegalArgumentException("Both arrays must have same size");
         }
         
         this.array1 = array1;
         this.vmin1 = 0.0;
         this.vmax1 = 255.0;
         this.array2 = array2;
         this.vmin2 = 0.0;
         this.vmax2 = 255.0;
     }
     
     
     public ScalarArraysDifferenceView(ScalarArray<?> array1, double vmin1, double vmax1, ScalarArray<?> array2, double vmin2, double vmax2)
     {
         if (!Arrays.isSameDimensionality(array1, array2))
         {
             int d1 = array1.dimensionality();
             int d2 = array2.dimensionality();
             throw new IllegalArgumentException(String.format("Both arrays must have same dimensionality (here, %d and %d)", d1, d2));
         }
         if (!Arrays.isSameSize(array1, array2))
         {
             throw new IllegalArgumentException("Both arrays must have same size");
         }
         
         this.array1 = array1;
         this.vmin1 = vmin1;
         this.vmax1 = vmax1;
         this.array2 = array2;
         this.vmin2 = vmin2;
         this.vmax2 = vmax2;
     }
     
         
     // =============================================================
     // Implementation of VectorArray interface
     
     /* (non-Javadoc)
      * @see net.sci.array.vector.VectorArray#getValue(int[], int)
      */
     @Override
     public double getValue(int[] pos, int channel)
     {
         switch(channel)
         {
             case 0:
             case 2:
                 return convertUInt8(this.array1.getValue(pos), vmin1, vmax1); 
             case 1:
                 return convertUInt8(this.array2.getValue(pos), vmin2, vmax2); 
             default:
                 throw new IllegalArgumentException("Channel index must be comprised between 0 and 2");
         }
     }

     /* (non-Javadoc)
      * @see net.sci.array.vector.VectorArray#setValue(int[], int, double)
      */
     @Override
     public void setValue(int[] pos, int channel, double value)
     {
         throw new RuntimeException("Can not modify values");
     }


     /* (non-Javadoc)
      * @see net.sci.array.color.RGB8Array#channel(int)
      */
     @Override
     public UInt8Array channel(int channel)
     {
         switch(channel)
         {
             case 0:
             case 2:
                 new ScalarArrayUInt8View(this.array1, vmin1, vmax1);
             case 1:
                 new ScalarArrayUInt8View(this.array2, vmin2, vmax2);
             default:
                 throw new IllegalArgumentException("Channel index must be comprised between 0 and 2");
         }
     }

     /* (non-Javadoc)
      * @see net.sci.array.color.RGB8Array#channels()
      */
     @Override
     public Iterable< UInt8Array> channels()
     {
         return new Iterable<UInt8Array>()
         { 
             @Override
             public java.util.Iterator<UInt8Array> iterator()
             {
                 return channelIterator();
             }
         };
     }

     /* (non-Javadoc)
      * @see net.sci.array.color.RGB8Array#channelIterator()
      */
     @Override
     public java.util.Iterator<UInt8Array> channelIterator()
     {
         return new ChannelIterator();
     }
     

     // =============================================================
     // Implementation of Array interface
     
     /* (non-Javadoc)
      * @see net.sci.array.Array#get(int[])
      */
     @Override
     public RGB8 get(int... pos)
     {
         int v1 = convertUInt8(array1.getValue(pos), vmin1, vmax1);
         int v2 = convertUInt8(array2.getValue(pos), vmin2, vmax2);
         return new RGB8(v1, v2, v1);
     }
     
     private static final int convertUInt8(double value, double vmin, double vmax)
     {
         return (int) (255 * (value - vmin) / (vmax - vmin));
     }

     /* (non-Javadoc)
      * @see net.sci.array.Array#set(int[], java.lang.Object)
      */
     @Override
     public void set(int[] pos, RGB8 value)
     {
         throw new RuntimeException("Can not modify values");
     }

     /* (non-Javadoc)
      * @see net.sci.array.Array#positionIterator()
      */
     @Override
     public net.sci.array.Array.PositionIterator positionIterator()
     {
         return this.array1.positionIterator();
     }

     /* (non-Javadoc)
      * @see net.sci.array.Array#dimensionality()
      */
     @Override
     public int dimensionality()
     {
         return this.array1.dimensionality();
     }

     /* (non-Javadoc)
      * @see net.sci.array.Array#size()
      */
     @Override
     public int[] size()
     {
         return this.array1.size();
     }


     /* (non-Javadoc)
      * @see net.sci.array.Array#size(int)
      */
     @Override
     public int size(int dim)
     {
         return this.array1.size(dim);
     }


     /* (non-Javadoc)
      * @see net.sci.array.color.RGB8Array#iterator()
      */
     @Override
     public Iterator iterator()
     {
         return new Iterator()
         {
             PositionIterator iter = positionIterator();

             @Override
             public boolean hasNext()
             {
                 return iter.hasNext();
             }

             @Override
             public void forward()
             {
                 iter.forward();
             }

             @Override
             public RGB8 get()
             {
                 return ScalarArraysDifferenceView.this.get(iter.get());
             }

             @Override
             public void set(RGB8 value)
             {
                 throw new RuntimeException("Can not modify values of a ScalarArraysDifferenceView");
             }

             @Override
             public double getValue(int c)
             {
                 switch(c)
                 {
                     case 0:
                     case 2:
                         return convertUInt8(ScalarArraysDifferenceView.this.array1.getValue(iter.get()), vmin1, vmax1); 
                     case 1:
                         return convertUInt8(ScalarArraysDifferenceView.this.array2.getValue(iter.get()), vmin2, vmax2); 
                     default:
                         throw new IllegalArgumentException("Channel index must be comprised between 0 and 2");
                 }
             }

             @Override
             public void setValue(int c, double value)
             {
                 throw new RuntimeException("Can not modify values of a ScalarArraysDifferenceView");
             }
         };
     }

     

     private class ChannelIterator implements java.util.Iterator<UInt8Array> 
     {
         int channel = -1;

         @Override
         public boolean hasNext()
         {
             return channel < 2;
         }

         @Override
         public UInt8Array next()
         {
             channel++;
             return ScalarArraysDifferenceView.this.channel(channel);
         }
     } 
     

}
