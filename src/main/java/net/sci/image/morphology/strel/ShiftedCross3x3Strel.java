/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.ScalarArray2D;

/**
 * Structuring element representing a 3x3 cross, that considers the reference
 * pixel together with four neighbors located either on the left or on the right
 * of the reference pixel.
 * 
 * @see DiamondStrel
 * @author David Legland
 */
public class ShiftedCross3x3Strel
{

    /**
     * A cross-shaped structuring element located on the left of the reference
     * pixel.
     * <p>
     * 
     * The structuring element has the following shape (x: neighbor, o:
     * reference pixel, .: irrelevant):
     * 
     * {@snippet :
     *  . . . . . 
     *  . . x . . 
     *  . x x o . 
     *  . . x . . 
     *  . . . . .
     * } 
     */
    public final static InPlaceStrel2D LEFT = new ShiftedCross3x3Strel.Left();

    /**
     * A cross-shaped structuring element located on the right of the reference
     * pixel.
     * <p>
     * 
     * The structuring element has the following shape (x: neighbor, o:
     * reference pixel, .: irrelevant):
     * 
     * {@snippet :
     *  . . . . . 
     *  . . x . . 
     *  . o x x . 
     *  . . x . . 
     *  . . . . . 
     * }
     */
    public final static InPlaceStrel2D RIGHT = new ShiftedCross3x3Strel.Right();

    /**
     * A cross-shaped structuring element located on the left of the reference
     * pixel.
     * </p>
     * 
     * The structuring has the following shape (x: neighbor, o: reference pixel,
     * .: irrelevant): <code><pre>
     *  . . . . . 
     *  . . x . . 
     *  . x x o . 
     *  . . x . . 
     *  . . . . . 
     * </pre></code>
     */
    private final static class Left extends AbstractStrel2D implements InPlaceStrel2D
    {

        /**
         * Default constructor.
         */
        public Left()
        {
        }

        /*
         * (non-Javadoc)
         * 
         * @see Strel#getSize()
         */
        @Override
        public int[] size()
        {
            return new int[] { 3, 3 };
        }

        /*
         * (non-Javadoc)
         * 
         * @see Strel#getMask()
         */
        @Override
        public BinaryArray2D binaryMask()
        {
            BinaryArray2D mask = BinaryArray2D.create(3, 3);
            mask.setBoolean(0, 1, true);
            mask.setBoolean(1, 0, true);
            mask.setBoolean(1, 1, true);
            mask.setBoolean(1, 2, true);
            mask.setBoolean(2, 1, true);
            return mask;
        }

        /*
         * (non-Javadoc)
         * 
         * @see Strel#getOffset()
         */
        @Override
        public int[] maskOffset()
        {
            return new int[] { 2, 1 };
        }

        /*
         * (non-Javadoc)
         * 
         * @see Strel#getShifts()
         */
        @Override
        public int[][] shifts()
        {
            int[][] shifts = new int[][] { { -1, -1 }, { -2, 0 }, { -1, 0 }, { 0, 0 }, { -1, +1 } };
            return shifts;
        }

        /**
         * Returns this structuring element, as is is self-reverse.
         * 
         * @see InPlaceStrel2D#reverse()
         */
        @Override
        public InPlaceStrel2D reverse()
        {
            return RIGHT;
        }

        /*
         * (non-Javadoc)
         * 
         * @see InPlaceStrel#inPlaceDilation(ij.process.ScalarArray2D<?>)
         */
        @Override
        public void inPlaceDilation(ScalarArray2D<?> array)
        {
            if (array instanceof IntArray2D<?>)
                inPlaceDilationInt((IntArray2D<?>) array);
            else
                inPlaceDilationFloat(array);
        }

        private void inPlaceDilationInt(IntArray2D<?> array)
        {
            // size of image
            int sizeX = array.size(0);
            int sizeY = array.size(1);

            // retrieve minimum value allowed within array
            final int defaultValue = array.typeMin().getInt();
            int[][] buffer = new int[3][sizeX];

            // init buffer with background and first two lines
            for (int x = 0; x < sizeX; x++)
            {
                buffer[0][x] = defaultValue;
                buffer[1][x] = defaultValue;
                buffer[2][x] = array.getInt(x, 0);
            }

            // Iterate over image lines
            int valMax;
            for (int y = 0; y < sizeY; y++)
            {
                fireProgressChanged(this, y, sizeY);

                // permute lines in buffer
                int[] tmp = buffer[0];
                buffer[0] = buffer[1];
                buffer[1] = buffer[2];

                // initialize values of the last line in buffer
                if (y < sizeY - 1)
                {
                    for (int x = 0; x < sizeX; x++)
                        tmp[x] = array.getInt(x, y + 1);
                }
                else
                {
                    for (int x = 0; x < sizeX; x++)
                        tmp[x] = defaultValue;
                }
                buffer[2] = tmp;

                // process first pixel independently
                valMax = Math.max(buffer[1][0], defaultValue);
                array.setInt(valMax, 0, y);
                valMax = max5(buffer[0][0], buffer[1][0], buffer[1][1], buffer[2][0], defaultValue);
                array.setInt(valMax, 1, y);

                // Iterate over pixel of the line, starting from the third one
                for (int x = 2; x < sizeX; x++)
                {
                    valMax = max5(buffer[0][x - 1], buffer[1][x - 2], buffer[1][x - 1],
                            buffer[1][x], buffer[2][x - 1]);
                    array.setInt(valMax, x, y);
                }
            }

            // clear the progress bar
            fireProgressChanged(this, sizeY, sizeY);
        }

        private void inPlaceDilationFloat(ScalarArray2D<?> array)
        {
            // size of image
            int sizeX = array.size(0);
            int sizeY = array.size(1);

            double[][] buffer = new double[3][sizeX];

            // init buffer with background and first two lines
            for (int x = 0; x < sizeX; x++)
            {
                buffer[0][x] = Double.NEGATIVE_INFINITY;
                buffer[1][x] = Double.NEGATIVE_INFINITY;
                buffer[2][x] = array.getValue(x, 0);
            }

            // Iterate over image lines
            double valMax;
            for (int y = 0; y < sizeY; y++)
            {
                fireProgressChanged(this, y, sizeY);

                // permute lines in buffer
                double[] tmp = buffer[0];
                buffer[0] = buffer[1];
                buffer[1] = buffer[2];

                // initialize values of the last line in buffer
                if (y < sizeY - 1)
                {
                    for (int x = 0; x < sizeX; x++)
                        tmp[x] = array.getValue(x, y + 1);
                }
                else
                {
                    for (int x = 0; x < sizeX; x++)
                        tmp[x] = Double.NEGATIVE_INFINITY;
                }
                buffer[2] = tmp;

                // process first two pixels independently
                valMax = Math.max(buffer[1][0], Double.NEGATIVE_INFINITY);
                array.setValue(0, y, valMax);
                valMax = max5(buffer[0][0], buffer[1][0], buffer[1][1], buffer[2][0],
                        Double.NEGATIVE_INFINITY);
                array.setValue(1, y, valMax);

                // Iterate over pixel of the line, starting from the third one
                for (int x = 2; x < sizeX; x++)
                {
                    valMax = max5(buffer[0][x - 1], buffer[1][x - 2], buffer[1][x - 1],
                            buffer[1][x], buffer[2][x - 1]);
                    array.setValue(x, y, valMax);
                }
            }

            // clear the progress bar
            fireProgressChanged(this, sizeY, sizeY);
        }

        /*
         * (non-Javadoc)
         * 
         * @see InPlaceStrel#inPlaceErosion(ij.process.ScalarArray2D<?>)
         */
        @Override
        public void inPlaceErosion(ScalarArray2D<?> array)
        {
            if (array instanceof IntArray2D<?>)
                inPlaceErosionInt((IntArray2D<?>) array);
            else
                inPlaceErosionFloat(array);
        }

        private void inPlaceErosionInt(IntArray2D<?> array)
        {
            // size of image
            int sizeX = array.size(0);
            int sizeY = array.size(1);

            // retrieve maximum value allowed within array
            final int defaultValue = array.typeMax().getInt(); 

            int[][] buffer = new int[3][sizeX];

            // init buffer with background and first two lines
            for (int x = 0; x < sizeX; x++)
            {
                buffer[0][x] = defaultValue;
                buffer[1][x] = defaultValue;
                buffer[2][x] = array.getInt(x, 0);
            }

            // Iterate over image lines
            int valMin;
            for (int y = 0; y < sizeY; y++)
            {
                fireProgressChanged(this, y, sizeY);

                // permute lines in buffer
                int[] tmp = buffer[0];
                buffer[0] = buffer[1];
                buffer[1] = buffer[2];

                // initialize values of the last line in buffer
                if (y < sizeY - 1)
                {
                    for (int x = 0; x < sizeX; x++)
                        tmp[x] = array.getInt(x, y + 1);
                }
                else
                {
                    for (int x = 0; x < sizeX; x++)
                        tmp[x] = defaultValue;
                }
                buffer[2] = tmp;

                // process first pixel independently
                valMin = Math.min(buffer[1][0], defaultValue);
                array.setInt(valMin, 0, y);
                valMin = min5(buffer[0][0], buffer[1][0], buffer[1][1], buffer[2][0], defaultValue);
                array.setInt(valMin, 1, y);

                // Iterate over pixel of the line
                for (int x = 2; x < sizeX; x++)
                {
                    valMin = min5(buffer[0][x - 1], buffer[1][x - 2], buffer[1][x - 1],
                            buffer[1][x], buffer[2][x - 1]);
                    array.setInt(valMin, x, y);
                }
            }

            // clear the progress bar
            fireProgressChanged(this, sizeY, sizeY);
        }

        private void inPlaceErosionFloat(ScalarArray2D<?> array)
        {
            // size of image
            int sizeX = array.size(0);
            int sizeY = array.size(1);

            double[][] buffer = new double[3][sizeX];

            // init buffer with background and first two lines
            for (int x = 0; x < sizeX; x++)
            {
                buffer[0][x] = Double.POSITIVE_INFINITY;
                buffer[1][x] = Double.POSITIVE_INFINITY;
                buffer[2][x] = array.getValue(x, 0);
            }

            // Iterate over image lines
            double valMin;
            for (int y = 0; y < sizeY; y++)
            {
                fireProgressChanged(this, y, sizeY);

                // permute lines in buffer
                double[] tmp = buffer[0];
                buffer[0] = buffer[1];
                buffer[1] = buffer[2];

                // initialize values of the last line in buffer
                if (y < sizeY - 1)
                {
                    for (int x = 0; x < sizeX; x++)
                        tmp[x] = array.getValue(x, y + 1);
                }
                else
                {
                    for (int x = 0; x < sizeX; x++)
                        tmp[x] = Double.POSITIVE_INFINITY;
                }
                buffer[2] = tmp;

                // process first pixel independently
                valMin = Math.min(buffer[1][0], Double.POSITIVE_INFINITY);
                array.setValue(0, y, valMin);
                valMin = min5(buffer[0][0], buffer[1][0], buffer[1][1], buffer[2][0],
                        Double.POSITIVE_INFINITY);
                array.setValue(1, y, valMin);

                // Iterate over pixel of the line
                for (int x = 2; x < sizeX; x++)
                {
                    valMin = min5(buffer[0][x - 1], buffer[1][x - 2], buffer[1][x - 1],
                            buffer[1][x], buffer[2][x - 1]);
                    array.setValue(x, y, valMin);
                }
            }

            // clear the progress bar
            fireProgressChanged(this, sizeY, sizeY);
        }
    }

    /**
     * A cross-shaped structuring element located on the right of the reference
     * pixel.
     * </p>
     * 
     * The structuring has the following shape (x: neighbor, o: reference pixel,
     * .: irrelevant): <code><pre>
     *  . . . . . 
     *  . . x . . 
     *  . o x x . 
     *  . . x . . 
     *  . . . . . 
     * </pre></code>
     */
    private final static class Right extends AbstractStrel2D implements InPlaceStrel2D
    {

        /**
         * Default constructor.
         */
        public Right()
        {
        }

        /*
         * (non-Javadoc)
         * 
         * @see Strel#getSize()
         */
        @Override
        public int[] size()
        {
            return new int[] { 3, 3 };
        }

        /*
         * (non-Javadoc)
         * 
         * @see Strel#getMask()
         */
        @Override
        public BinaryArray2D binaryMask()
        {
            BinaryArray2D mask = BinaryArray2D.create(3, 3);
            mask.setBoolean(0, 1, true);
            mask.setBoolean(1, 0, true);
            mask.setBoolean(1, 1, true);
            mask.setBoolean(1, 2, true);
            mask.setBoolean(2, 1, true);
            return mask;
        }

        /*
         * (non-Javadoc)
         * 
         * @see Strel#getOffset()
         */
        @Override
        public int[] maskOffset()
        {
            return new int[] { 0, 1 };
        }

        /*
         * (non-Javadoc)
         * 
         * @see Strel#getShifts()
         */
        @Override
        public int[][] shifts()
        {
            int[][] shifts = new int[][] { { +1, -1 }, { 0, 0 }, { +1, 0 }, { +2, 0 }, { +1, +1 } };
            return shifts;
        }

        /**
         * Returns this structuring element, as is is self-reverse.
         * 
         * @see InPlaceStrel2D#reverse()
         */
        @Override
        public InPlaceStrel2D reverse()
        {
            return LEFT;
        }

        /*
         * (non-Javadoc)
         * 
         * @see InPlaceStrel#inPlaceDilation(ij.process.ScalarArray2D<?>)
         */
        @Override
        public void inPlaceDilation(ScalarArray2D<?> array)
        {
            if (array instanceof IntArray2D<?>)
                inPlaceDilationInt((IntArray2D<?>) array);
            else
                inPlaceDilationFloat(array);
        }

        private void inPlaceDilationInt(IntArray2D<?> array)
        {
            // size of image
            int sizeX = array.size(0);
            int sizeY = array.size(1);

            int[][] buffer = new int[3][sizeX];

            // retrieve minimum value allowed within array
            final int defaultValue = array.typeMin().getInt();
            
            // init buffer with background and first two lines
            for (int x = 0; x < sizeX; x++)
            {
                buffer[0][x] = defaultValue;
                buffer[1][x] = defaultValue;
                buffer[2][x] = array.getInt(x, 0);
            }

            // Iterate over image lines
            int valMax;
            for (int y = 0; y < sizeY; y++)
            {
                fireProgressChanged(this, y, sizeY);

                // permute lines in buffer
                int[] tmp = buffer[0];
                buffer[0] = buffer[1];
                buffer[1] = buffer[2];

                // initialize values of the last line in buffer
                if (y < sizeY - 1)
                {
                    for (int x = 0; x < sizeX; x++)
                        tmp[x] = array.getInt(x, y + 1);
                }
                else
                {
                    for (int x = 0; x < sizeX; x++)
                        tmp[x] = defaultValue;
                }
                buffer[2] = tmp;

                // Iterate over pixels of the line
                for (int x = 0; x < sizeX - 2; x++)
                {
                    valMax = max5(buffer[0][x + 1], buffer[1][x], buffer[1][x + 1],
                            buffer[1][x + 2], buffer[2][x + 1]);
                    array.setInt(x, y, valMax);
                }

                // process last pixel independently
                valMax = max5(buffer[0][sizeX - 1], buffer[1][sizeX - 2], buffer[1][sizeX - 1],
                        buffer[2][sizeX - 1], defaultValue);
                array.setInt(sizeX - 2, y, valMax);
                valMax = Math.max(buffer[1][sizeX - 1], defaultValue);
                array.setInt(sizeX - 1, y, valMax);
            }

            // clear the progress bar
            fireProgressChanged(this, sizeY, sizeY);
        }

        private void inPlaceDilationFloat(ScalarArray2D<?> array)
        {
            // size of image
            int sizeX = array.size(0);
            int sizeY = array.size(1);

            double[][] buffer = new double[3][sizeX];

            // init buffer with background and first two lines
            for (int x = 0; x < sizeX; x++)
            {
                buffer[0][x] = Double.NEGATIVE_INFINITY;
                buffer[1][x] = Double.NEGATIVE_INFINITY;
                buffer[2][x] = array.getValue(x, 0);
            }

            // Iterate over image lines
            double valMax;
            for (int y = 0; y < sizeY; y++)
            {
                fireProgressChanged(this, y, sizeY);

                // permute lines in buffer
                double[] tmp = buffer[0];
                buffer[0] = buffer[1];
                buffer[1] = buffer[2];

                // initialize values of the last line in buffer
                if (y < sizeY - 1)
                {
                    for (int x = 0; x < sizeX; x++)
                        tmp[x] = array.getValue(x, y + 1);
                }
                else
                {
                    for (int x = 0; x < sizeX; x++)
                        tmp[x] = Double.NEGATIVE_INFINITY;
                }
                buffer[2] = tmp;

                // Iterate over pixels of the line
                for (int x = 0; x < sizeX - 2; x++)
                {
                    valMax = max5(buffer[0][x + 1], buffer[1][x], buffer[1][x + 1],
                            buffer[1][x + 2], buffer[2][x + 1]);
                    array.setValue(x, y, valMax);
                }

                // process last two pixels independently
                valMax = max5(buffer[0][sizeX - 1], buffer[1][sizeX - 2], buffer[1][sizeX - 1],
                        buffer[2][sizeX - 1], Double.NEGATIVE_INFINITY);
                array.setValue(sizeX - 2, y, valMax);
                valMax = Math.max(buffer[1][sizeX - 1], Double.NEGATIVE_INFINITY);
                array.setValue(sizeX - 1, y, valMax);
            }

            // clear the progress bar
            fireProgressChanged(this, sizeY, sizeY);
        }

        /*
         * (non-Javadoc)
         * 
         * @see InPlaceStrel#inPlaceErosion(ij.process.ScalarArray2D<?>)
         */
        @Override
        public void inPlaceErosion(ScalarArray2D<?> array)
        {
            if (array instanceof IntArray2D<?>)
                inPlaceErosionInt((IntArray2D<?>) array);
            else
                inPlaceErosionFloat(array);
        }

        private void inPlaceErosionInt(IntArray2D<?> array)
        {
            // size of image
            int sizeX = array.size(0);
            int sizeY = array.size(1);

            // retrieve maximum value allowed within array
            final int defaultValue = array.typeMax().getInt(); 

            int[][] buffer = new int[3][sizeX];

            // init buffer with background and first two lines
            for (int x = 0; x < sizeX; x++)
            {
                buffer[0][x] = defaultValue;
                buffer[1][x] = defaultValue;
                buffer[2][x] = array.getInt(x, 0);
            }

            // Iterate over image lines
            int valMin;
            for (int y = 0; y < sizeY; y++)
            {
                fireProgressChanged(this, y, sizeY);

                // permute lines in buffer
                int[] tmp = buffer[0];
                buffer[0] = buffer[1];
                buffer[1] = buffer[2];

                // initialize values of the last line in buffer
                if (y < sizeY - 1)
                {
                    for (int x = 0; x < sizeX; x++)
                        tmp[x] = array.getInt(x, y + 1);
                }
                else
                {
                    for (int x = 0; x < sizeX; x++)
                        tmp[x] = defaultValue;
                }
                buffer[2] = tmp;

                // Iterate over pixels of the line
                for (int x = 0; x < sizeX - 2; x++)
                {
                    valMin = min5(buffer[0][x + 1], buffer[1][x], buffer[1][x + 1],
                            buffer[1][x + 2], buffer[2][x + 1]);
                    array.setInt(x, y, valMin);
                }

                // process last pixel independently
                valMin = min5(buffer[0][sizeX - 1], buffer[1][sizeX - 2], buffer[1][sizeX - 1],
                        buffer[2][sizeX - 1], defaultValue);
                array.setInt(sizeX - 2, y, valMin);
                valMin = Math.min(buffer[1][sizeX - 1], defaultValue);
                array.setInt(sizeX - 1, y, valMin);
            }

            // clear the progress bar
            fireProgressChanged(this, sizeY, sizeY);
        }

        private void inPlaceErosionFloat(ScalarArray2D<?> array)
        {
            // size of image
            int sizeX = array.size(0);
            int sizeY = array.size(1);

            double[][] buffer = new double[3][sizeX];

            // init buffer with background and first two lines
            for (int x = 0; x < sizeX; x++)
            {
                buffer[0][x] = Double.POSITIVE_INFINITY;
                buffer[1][x] = Double.POSITIVE_INFINITY;
                buffer[2][x] = array.getValue(x, 0);
            }

            // Iterate over image lines
            double valMin;
            for (int y = 0; y < sizeY; y++)
            {
                fireProgressChanged(this, y, sizeY);

                // permute lines in buffer
                double[] tmp = buffer[0];
                buffer[0] = buffer[1];
                buffer[1] = buffer[2];

                // initialize values of the last line in buffer
                if (y < sizeY - 1)
                {
                    for (int x = 0; x < sizeX; x++)
                        tmp[x] = array.getValue(x, y + 1);
                }
                else
                {
                    for (int x = 0; x < sizeX; x++)
                        tmp[x] = Double.POSITIVE_INFINITY;
                }
                buffer[2] = tmp;

                // Iterate over pixels of the line
                for (int x = 0; x < sizeX - 2; x++)
                {
                    valMin = min5(buffer[0][x + 1], buffer[1][x], buffer[1][x + 1],
                            buffer[1][x + 2], buffer[2][x + 1]);
                    array.setValue(x, y, valMin);
                }

                // process last two pixels independently
                valMin = min5(buffer[0][sizeX - 1], buffer[1][sizeX - 2], buffer[1][sizeX - 1],
                        buffer[2][sizeX - 1], Double.POSITIVE_INFINITY);
                array.setValue(sizeX - 2, y, valMin);
                valMin = Math.min(buffer[1][sizeX - 1], Double.POSITIVE_INFINITY);
                array.setValue(sizeX - 1, y, valMin);
            }

            // clear the progress bar
            fireProgressChanged(this, sizeY, sizeY);
        }

    }

    /**
     * Makes default constructor private to avoid instantiation.
     */
    private ShiftedCross3x3Strel()
    {
    }

    /**
     * Computes the minimum of the 5 values.
     */
    private final static int min5(int v1, int v2, int v3, int v4, int v5)
    {
        int min1 = Math.min(v1, v2);
        int min2 = Math.min(v3, v4);
        min1 = Math.min(min1, v5);
        return Math.min(min1, min2);
    }

    /**
     * Computes the minimum of the 5 double values.
     */
    private final static double min5(double v1, double v2, double v3, double v4, double v5)
    {
        double min1 = Math.min(v1, v2);
        double min2 = Math.min(v3, v4);
        min1 = Math.min(min1, v5);
        return Math.min(min1, min2);
    }

    /**
     * Computes the maximum of the 5 values.
     */
    private final static int max5(int v1, int v2, int v3, int v4, int v5)
    {
        int max1 = Math.max(v1, v2);
        int max2 = Math.max(v3, v4);
        max1 = Math.max(max1, v5);
        return Math.max(max1, max2);
    }

    /**
     * Computes the maximum of the 5 double values.
     */
    private final static double max5(double v1, double v2, double v3, double v4, double v5)
    {
        double max1 = Math.max(v1, v2);
        double max2 = Math.max(v3, v4);
        max1 = Math.max(max1, v5);
        return Math.max(max1, max2);
    }

}
