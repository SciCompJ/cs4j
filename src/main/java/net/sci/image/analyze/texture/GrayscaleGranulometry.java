/**
 * 
 */
package net.sci.image.analyze.texture;

import java.util.EnumSet;

import net.sci.algo.AlgoStub;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.image.morphology.MorphologicalFilter;
import net.sci.image.morphology.filtering.Closing;
import net.sci.image.morphology.filtering.Dilation;
import net.sci.image.morphology.filtering.Erosion;
import net.sci.image.morphology.filtering.Opening;
import net.sci.image.morphology.strel.Strel2D;
import net.sci.table.NumericColumn;
import net.sci.table.NumericTable;

/**
 * Computation of gray level granulometry curves using mathematical morphology
 * operations.
 * 
 * @author dlegland
 *
 */
public class GrayscaleGranulometry extends AlgoStub
{
	// =======================================================================
	// Enumeration for operations

	/**
	 * An enumeration of granulometry types that can be used for computation of gray
	 * level granulometry curves.
	 */
	public enum Type 
	{
		/** Morphological Erosion*/
		EROSION("Erosion"),
		/** Morphological Dilation*/
		DILATION("Dilation"),
		/** Morphological Closing*/
		CLOSING("Closing"),
		/** Morphological Opening*/
		OPENING("Opening");
		
        private final String label;
        
        private Type(String label) 
        {
            this.label = label;
        }
        
        /**
         * Applies the current operator to the input 3D image.
         * 
         * @param array
         *            the array to process
         * @param strel
         *            the structuring element to use
         * @return the result of morphological operation applied to image
         */
        public ScalarArray2D<?> apply(ScalarArray2D<?> array, Strel2D strel)
        {
            // create filter
            MorphologicalFilter filter = switch(this)
            {
                case DILATION -> new Dilation(strel);
                case EROSION -> new Erosion(strel);
                case CLOSING -> new Closing(strel);
                case OPENING -> new Opening(strel);
                default -> throw new RuntimeException("Unable to create operator from the " + this + " operation");
            };
            
            // apply morphological filter, and wraps into a 2D array
            return ScalarArray2D.wrap(filter.processScalar(array));
        }
        
		public String toString() 
		{
			return this.label;
		}
		
		public static String[] getAllLabels()
		{
			int n = Type.values().length;
			String[] result = new String[n];
			
			int i = 0;
			for (Type op : Type.values())
				result[i++] = op.toString();
			
			return result;
		}
		
		/**
         * Determines the granulometry type from its label.
         * 
         * @throws IllegalArgumentException
         *             if label is not recognized.
         */
		public static Type fromLabel(String opLabel) 
		{
			if (opLabel != null)
				opLabel = opLabel.toLowerCase();
			for (Type op : Type.values()) 
			{
				String cmp = op.toString().toLowerCase();
				if (cmp.equals(opLabel))
					return op;
			}
			throw new IllegalArgumentException("Unable to parse Operation with label: " + opLabel);
        }
        
        public static EnumSet<Type> all()
        {
            return EnumSet.allOf(Type.class);
        }
    };
    

    // =======================================================================
    // class variables
	
	Type op = Type.CLOSING;
	Strel2D.Shape shape = Strel2D.Shape.SQUARE;
	int radiusMax = 50;
	int radiusStep = 1;
	
	
    // =======================================================================
    // Constructors add setters
	
	public GrayscaleGranulometry()
	{
	}
	
    public GrayscaleGranulometry type(Type op)
    {
        this.op = op;
        return this;
    }
    
    public GrayscaleGranulometry strelShape(Strel2D.Shape shape)
    {
        this.shape = shape;
        return this;
    }
    
    public GrayscaleGranulometry radiusMax(int max)
    {
        this.radiusMax = max;
        return this;
    }
    
    public GrayscaleGranulometry radiusStep(int step)
    {
        this.radiusStep = step;
        return this;
    }
    
    
	// =======================================================================
	// methods for computing granulometries
	
    public NumericTable granulometry(ScalarArray2D<?> image) 
    {
        // create result table
        int nSteps = radiusMax / radiusStep;
        
        NumericTable table = NumericTable.create(nSteps + 1, 3);
        table.setColumnNames(new String[] {"Radius", "Diameter", "Volume"});
        
        // initialize first value
        int radius = 0;
        table.setValue(0, "Radius", radius);
        table.setValue(0, "Diameter", 2 * radius + 1);
        double vol = imageVolume(image);
        table.setValue(0, "Volume", vol);
        
        // iterate over strel sizes
        for (int i = 0; i < nSteps; i++) 
        {
            radius += radiusStep;
            
            this.fireStatusChanged(this, "Radius " + radius + "(" + i + "/" + nSteps + ")");
            this.fireProgressChanged(this, i, nSteps);
            
            Strel2D strel = shape.fromRadius(radius);
            ScalarArray2D<?> image2 = op.apply(image, strel);
            
            vol = imageVolume(image2);
            
            table.setValue(i + 1, "Radius", radius);
            table.setValue(i + 1, "Diameter", 2 * radius + 1);
            table.setValue(i + 1, "Volume", vol);
        }
        
        return table;
    }
    

    public NumericTable granulometryCurve(ScalarArray2D<?> array) 
    {
        return derivate(granulometry(array), 1, 2);
    }
    
    
	// =======================================================================
	// Utility methods

	/**
	 * Computes the gray scale volume of the input image, by computing the sum
	 * of intensity value for each pixel.
	 * 
	 * @param image
	 *            a gray scale image
	 * @return the sum of pixel intensities
	 */
    private final static <S extends Scalar<S>> double imageVolume(ScalarArray<S> image) 
	{
	    double sum = 0.0;
	    ScalarArray.Iterator<S> iter = image.iterator();
	    while(iter.hasNext())
	    {
	        sum += iter.nextValue();
	    }
		return sum;
	}

    /**
     * Computes the derivative of a specific column within a table
     * 
     * @param table
     *            the input data table
     * @param indX
     *            index of the column containing abscissa (starting from 0)
     * @param indY
     *            index of the column containing the values to derivate
     *            (starting from 0)
     */
    private final static NumericTable derivate(NumericTable table, int indX, int indY) 
    {
        // number of table entries
        int n = table.rowCount();
        
        // retrieve columns as instances of NumericColumn
        NumericColumn colX = table.column(indX);
        NumericColumn colY = table.column(indY);
        
        // Name of the column containing the "size" information
        String sizeColumnName = colX.getName();
        
        // extract initial and final values
        double v0 = colY.getValue(0);
        double vf = colY.getValue(n-1);

        NumericTable result = NumericTable.create(n-1, 2);
        result.setColumnNames(new String[] {sizeColumnName, "Variation (%)"});
        
        // compute normalized derivative
        double vPrev = v0;
        for (int i = 1; i < n; i++) 
        {
            double vCurr = colY.getValue(i);
            double yres = 100 * (vCurr - vPrev) / (vf - v0);
            
            result.setValue(i - 1, 0, colX.getValue(i));
            result.setValue(i - 1, 1, yres);
            
            vPrev = vCurr;
        }
        
        return result;
    }
}
