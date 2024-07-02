/**
 * 
 */
package net.sci.array.color;

import net.sci.array.numeric.UInt16Array;

/**
 * @author dlegland
 *
 */
public class BufferedPackedShortRGB16ArrayND extends RGB16ArrayND
{
    // =============================================================
    // Class variables

    UInt16Array buffer;
    
    // =============================================================
    // Constructor

    /**
     * @param sizes the size of the array
     */
    public BufferedPackedShortRGB16ArrayND(int... sizes)
    {
        super(sizes);
        
        int nd = sizes.length;
        int[] bufDims = new int[nd+1];
        System.arraycopy(sizes, 0, bufDims, 0, nd);
        bufDims[nd] = 3;
        this.buffer = UInt16Array.create(bufDims);
    }


    // =============================================================
    // Implementation of the IntVectorArray interface

    /* (non-Javadoc)
     * @see net.sci.array.vector.IntVectorArray#getSamples(int[])
     */
    @Override
    public int[] getSamples(int[] pos)
    {
        int[] res = new int[3];
        int nd = this.dimensionality();
        int[] pos2 = new int[nd+1];
        System.arraycopy(pos, 0, pos2, 0, nd);
        for (int c = 0; c < 3; c++)
        {
            pos2[nd] = c;
            res[c] = buffer.getInt(pos2);
        }

        return res;
    }

    /* (non-Javadoc)
     * @see net.sci.array.vector.IntVectorArray#getSamples(int[], int[])
     */
    @Override
    public int[] getSamples(int[] pos, int[] intValues)
    {
        int nd = this.dimensionality();
        int[] pos2 = new int[nd+1];
        System.arraycopy(pos, 0, pos2, 0, nd);
        for (int c = 0; c < 3; c++)
        {
            pos2[nd] = c;
            intValues[c] = buffer.getInt(pos2);
        }

        return intValues;
    }

    /* (non-Javadoc)
     * @see net.sci.array.vector.IntVectorArray#setSamples(int[], int[])
     */
    @Override
    public void setSamples(int[] pos, int[] intValues)
    {
        int nd = this.dimensionality();
        int[] pos2 = new int[nd+1];
        System.arraycopy(pos, 0, pos2, 0, nd);
        for (int c = 0; c < 3; c++)
        {
            pos2[nd] = c;
            buffer.setInt(pos2, intValues[c]);
        }
    }

    /* (non-Javadoc)
     * @see net.sci.array.vector.IntVectorArray#getSample(int[], int)
     */
    @Override
    public int getSample(int[] pos, int channel)
    {
        int nd = this.dimensionality();
        int[] pos2 = new int[nd+1];
        System.arraycopy(pos, 0, pos2, 0, nd);
        pos2[nd] = channel;
        return buffer.getInt(pos2);
    }

    /* (non-Javadoc)
     * @see net.sci.array.vector.IntVectorArray#setSample(int[], int, int)
     */
    @Override
    public void setSample(int[] pos, int channel, int intValue)
    {
        int nd = this.dimensionality();
        int[] pos2 = new int[nd+1];
        System.arraycopy(pos, 0, pos2, 0, nd);
        pos2[nd] = channel;
        buffer.setInt(pos2, intValue);
    }


    // =============================================================
    // Implementation of the Array interface

    /* (non-Javadoc)
     * @see net.sci.array.Array#get(int[])
     */
    @Override
    public RGB16 get(int[] pos)
    {
        int nd = this.dimensionality();
        int[] pos2 = new int[nd+1];
        System.arraycopy(pos, 0, pos2, 0, nd);
        pos2[nd] = 0;
        int r = this.buffer.getInt(pos2);
        pos2[nd] = 1;
        int g = this.buffer.getInt(pos2);
        pos2[nd] = 2;
        int b = this.buffer.getInt(pos2);
        return new RGB16(r, g, b);
    }

    /* (non-Javadoc)
     * @see net.sci.array.Array#set(int[], java.lang.Object)
     */
    @Override
    public void set(int[] pos, RGB16 rgb)
    {
        int nd = this.dimensionality();
        int[] pos2 = new int[nd+1];
        System.arraycopy(pos, 0, pos2, 0, nd);
        pos2[nd] = 0;
        this.buffer.setInt(pos2, rgb.getSample(0));
        pos2[nd] = 1;
        this.buffer.setInt(pos2, rgb.getSample(1));
        pos2[nd] = 2;
        this.buffer.setInt(pos2, rgb.getSample(2));
    }

    /* (non-Javadoc)
     * @see net.sci.array.color.RGB16ArrayND#duplicate()
     */
    @Override
    public RGB16ArrayND duplicate()
    {
        RGB16ArrayND result = new BufferedPackedShortRGB16ArrayND(this.sizes);
        int[] samples = new int[3];
        for (int[] pos : result.positions())
        {
            result.setSamples(pos, this.getSamples(pos, samples));
        }
        return result;
    }

    /* (non-Javadoc)
     * @see net.sci.array.color.RGB16ArrayND#iterator()
     */
    @Override
    public net.sci.array.color.RGB16Array.Iterator iterator()
    {
        return new Iterator();
    }

    private class Iterator implements net.sci.array.color.RGB16Array.Iterator
    {
        PositionIterator iter;

        public Iterator()
        {
            this.iter = positionIterator();
        }
        
        @Override
        public boolean hasNext()
        {
            return iter.hasNext();
        }
        
        @Override
        public RGB16 next()
        {
            forward();
            return get();
        }

        @Override
        public void forward()
        {
            iter.forward();
        }

        @Override
        public double getValue(int c)
        {
            return BufferedPackedShortRGB16ArrayND.this.getValue(iter.get(), c);
        }

        @Override
        public void setValue(int c, double value)
        {
            BufferedPackedShortRGB16ArrayND.this.setValue(iter.get(), c, value);
        }

        @Override
        public RGB16 get()
        {
            return BufferedPackedShortRGB16ArrayND.this.get(iter.get());
        }

        @Override
        public void set(RGB16 rgb16)
        {
            BufferedPackedShortRGB16ArrayND.this.set(iter.get(), rgb16);
        }
    }
}
