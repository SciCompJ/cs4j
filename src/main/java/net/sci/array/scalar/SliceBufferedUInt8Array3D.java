/**
 * 
 */
package net.sci.array.scalar;

/**
 * Virtual array that buffers a selected number of slices from another 3D array
 * of UInt8.
 * 
 * @author dlegland
 *
 */
public class SliceBufferedUInt8Array3D extends UInt8Array3D
{
    UInt8Array3D refArray;
    
    UInt8Array2D[] slices;
    int[] sliceIndices;
    
    /**
     * Creates a new SliceBufferedUInt8Array3D.
     * 
     * @param refArray
     *            the reference array
     * @param nSlices
     *            the number of slices to buffer
     */
    public SliceBufferedUInt8Array3D(UInt8Array3D refArray, int nSlices)
    {
        super(refArray.size(0), refArray.size(1), refArray.size(2));
        
        this.refArray = refArray;
        this.slices = new UInt8Array2D[nSlices];
        this.sliceIndices = new int[nSlices];
        
        // allocate memory for slices, and init slice indices with -1 values
        for (int i = 0; i < nSlices; i++)
        {
            this.slices[i] = UInt8Array2D.create(this.size0, this.size1);
            this.sliceIndices[i] = -1;
        }
    }

    /**
     * Returns the array corresponding to the slice data.
     */
    public UInt8Array2D slice(int sliceIndex)
    {
        int index = getSliceBufferIndex(sliceIndex);
        return this.slices[index].duplicate();
    }
    
    @Override
    public byte getByte(int... pos)
    {
        int sliceIndex = pos[2];
        int index = getSliceBufferIndex(sliceIndex);
        
        return this.slices[index].getByte(pos[0], pos[1]);
    }
    
    /**
     * Ensures the slice with the specified index is loaded, and return the
     * slice buffer index.
     * 
     * @param zIndex
     *            the z index of the slice
     * @return the index of the slice in the buffer array
     */
    private int getSliceBufferIndex(int zIndex)
    {
        int bufferSize = this.sliceIndices.length;
        for (int i = 0; i < bufferSize; i++)
        {
            if (this.sliceIndices[i] == -1) break;
            if (this.sliceIndices[i] == zIndex) return i;
        }
        
        // keep reference to last slice
        UInt8Array2D slice0 = this.slices[bufferSize-1];
        
        // shift slices and indices, starting from the end
        for (int i = bufferSize - 1; i > 0; i--)
        {
            this.slices[i] = this.slices[i-1];
            this.sliceIndices[i] = this.sliceIndices[i-1];
        }
        
        // populate the new slice
        for (int y = 0; y < this.size1; y++)
        {
            for (int x = 0; x < this.size0; x++)
            {
                slice0.setByte(x, y, this.refArray.getByte(x, y, zIndex));
            }
        }
        
        // update info for first slice
        this.slices[0] = slice0;
        this.sliceIndices[0] = zIndex;
        
        // if slice is loaded, it is assigned to buffer index 0.
        return 0;
    }

    /**
     * @throws a RuntimeException, as this operation is not authorized.
     */
    @Override
    public void setByte(int x, int y, int z, byte b)
    {
        throw new RuntimeException("Unauthorized operation");
    }
    
    public static final void main(String... args)
    {
        UInt8Array3D refArray = UInt8Array3D.create(10, 10, 10);
        refArray.populateValues((x,y,z) -> (double) (x + y * 10.0 + Math.floor(z / 5.0) * 2.0));

        SliceBufferedUInt8Array3D array = new SliceBufferedUInt8Array3D(refArray, 2);

        System.out.println("read slice 0"); // need to load slice 0
        array.getValue(0, 0, 0);
        array.getValue(0, 1, 0);
        array.getValue(4, 1, 0);
        array.getValue(5, 4, 0);
        System.out.println("read slice 1");  // need to load slice 1
        array.getValue(0, 0, 1);
        array.getValue(0, 1, 1);
        System.out.println("read slice 0 again");  // slice 0 is already buffered
        array.getValue(0, 0, 0);
        array.getValue(0, 1, 0);
        array.getValue(4, 1, 0);
        System.out.println("read slice 2");  // need to load slice 2, this will remove slice 0 from buffer
        array.getValue(0, 0, 2);
        array.getValue(0, 1, 2);
        System.out.println("read slice 1 again");  // slice 1 is already buffered
        array.getValue(0, 0, 1);
        array.getValue(0, 1, 1);
        System.out.println("read slice 0 again"); // need to load slice 0 again
        array.getValue(0, 0, 0);
        array.getValue(0, 1, 0);
        array.getValue(4, 1, 0);
    }
}
