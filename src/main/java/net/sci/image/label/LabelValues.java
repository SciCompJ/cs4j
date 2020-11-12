package net.sci.image.label;

import java.util.Map;

import net.sci.array.Arrays;
import net.sci.array.scalar.*;
import net.sci.image.data.Cursor2D;
import net.sci.image.data.Cursor3D;


/**
 * Utility methods for combining label images and intensity images.
 * 
 * @author David Legland
 *
 */
public class LabelValues
{
	/**
     * For each label, finds the position of the point belonging to label region
     * defined by <code>labelImage</code> and with maximal value in intensity
     * image <code>valueImage</code>.
     * 
     * @param labelImage
     *            the intensity image containing label of each pixel
     * @param labels
     *            the list of labels in the label image
     * @param valueImage
     *            the intensity image containing values to compare
     * @return the position of maximum value in intensity image for each label
     */
    public static final double[] maxValues(IntArray<?> labelImage, int[] labels,
            ScalarArray<?> valueImage)
    {
        // check image dimensionality
        if (!Arrays.isSameDimensionality(valueImage, labelImage))
        {
            throw new IllegalArgumentException("Both images must have same dimensionality");
        }
        // check image dimensions
        if (!Arrays.isSameSize(valueImage, labelImage))
        {
            throw new IllegalArgumentException("Both images must have same dimensions");
        }
        
        // Compute value of greatest label
        int nLabels = labels.length;
        
        // init index of each label
        Map<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);
                
        // Init value of maximum for each label
        double[] maxValues = new double[nLabels];
        for (int i = 0; i < nLabels; i++)
            maxValues[i] = Double.NEGATIVE_INFINITY;
        
        // iterate on image positions
        for (int[] pos : labelImage.positions())
        {
            int label = labelImage.getInt(pos);

            // do not process pixels that do not belong to particle
            if (label == 0)
                continue;

            if (labelIndices.containsKey(label))
            {
                int index = labelIndices.get(label);

                // update values and positions
                double value = valueImage.getValue(pos);
                if (value > maxValues[index])
                    maxValues[index] = value;
            }
        }
                
        return maxValues;
    
    }

	
    /**
	 * For each label, finds the position of the point belonging to label region
	 * defined by <code>labelImage</code> and with maximal value in intensity
	 * image <code>valueImage</code>.
	 * 
	 * @param valueImage
	 *            the intensity image containing values to compare
	 * @param labelImage
	 *            the intensity image containing label of each pixel
	 * @param labels
	 *            the list of labels in the label image
	 * @return the position of maximum value in intensity image for each label
	 */
    public static final PositionValuePair2D[] findMaxValues2d(
            IntArray2D<?> labelImage, int[] labels, ScalarArray2D<?> valueImage)
	{
		// Create associative map between each label and its index
		Map<Integer,Integer> labelIndices = LabelImages.mapLabelIndices(labels);
		
		// Init Position and value of maximum for each label
		int nLabels = labels.length;
		PositionValuePair2D[] pairs = new PositionValuePair2D[nLabels]; 
		for (int i = 0; i < nLabels; i++) 
		{
			pairs[i] = new PositionValuePair2D(new Cursor2D(-1, -1), Double.NEGATIVE_INFINITY);
		}
		
		// iterate on image pixels
		int sizeX = labelImage.size(0);
		int sizeY = labelImage.size(1);
		for (int y = 0; y < sizeY; y++) 
		{
			for (int x = 0; x < sizeX; x++) 
			{
				int label = (int) labelImage.getValue(x, y);
				
				// do not process pixels that do not belong to any particle
				if (label == 0)
					continue;
				if (!labelIndices.containsKey(label))
					continue;

				// get position-value pair corresponding to current label
				int index = labelIndices.get(label);
				PositionValuePair2D pair = pairs[index];
				
				// update values and positions
				double value = valueImage.getValue(x, y);
				if (value > pair.value) 
				{
					pair.position = new Cursor2D(x, y);
					pair.value = value;
				}
			}
		}
				
		return pairs;
	}
	
	/**
	 * For each label, finds the position of the 3D point belonging to label
	 * region defined by the 3D <code>labelImage</code> and with maximal value
	 * in intensity 3D image <code>valueImage</code>.
	 * 
	 * @param valueImage
	 *            the intensity image containing values to compare
	 * @param labelImage
	 *            the intensity image containing label of each pixel
	 * @param labels
	 *            the list of labels in the label image
	 * @return the position of maximum value in intensity image for each label
	 */
    public static final PositionValuePair3D[] findMaxValues3d(
            IntArray3D<?> labelImage, int[] labels, ScalarArray3D<?> valueImage)
	{
		// get image size
		int sizeX = labelImage.size(0);
		int sizeY = labelImage.size(1);
		int sizeZ = labelImage.size(2);

		// check image dimensions
		if (labelImage.size(0) != sizeX || labelImage.size(1) != sizeY || labelImage.size(2) != sizeZ)
		{
			throw new IllegalArgumentException("Both images must have same dimensions");
		}

		// Create associative map between each label and its index
		Map<Integer,Integer> labelIndices = LabelImages.mapLabelIndices(labels);
		
		// Init Position and value of maximum for each label
		int nLabels = labels.length;
		PositionValuePair3D[] pairs = new PositionValuePair3D[nLabels]; 
		for (int i = 0; i < nLabels; i++) 
		{
			pairs[i] = new PositionValuePair3D(new Cursor3D(-1, -1, -1), Double.NEGATIVE_INFINITY);
		}
		
		// iterate on image pixels
		for (int z = 0; z < sizeZ; z++) 
		{
			for (int y = 0; y < sizeY; y++) 
			{
				for (int x = 0; x < sizeX; x++) 
				{
					int label = (int) labelImage.getValue(x, y, z);
					
					// do not process pixels that do not belong to any particle
					if (label == 0)
						continue;
					if (!labelIndices.containsKey(label))
						continue;
	
					// get position-value pair corresponding to current label
					int index = labelIndices.get(label);
					PositionValuePair3D pair = pairs[index];
					
					// update values and positions
					double value = valueImage.getValue(x, y, z);
					if (value > pair.value) 
					{
						pair.position = new Cursor3D(x, y, z);
						pair.value = value;
					}
				}
			}
		}
		
		return pairs;
	}

	/**
	 * For each label, finds the position of the point belonging to label region
	 * defined by <code>labelImage</code> and with minimal value in intensity
	 * image <code>valueImage</code>.
	 * 
	 * @param valueImage
	 *            the intensity image containing values to compare
	 * @param labelImage
	 *            the intensity image containing label of each pixel
	 * @param labels
	 *            the list of labels in the label image
	 * @return the position of minimum value in intensity image for each label
	 */
    public static final PositionValuePair2D[] findMinValues2d(
            IntArray2D<?> labelImage, int[] labels, ScalarArray2D<?> valueImage)
	{
		// Create associative map between each label and its index
		Map<Integer,Integer> labelIndices = LabelImages.mapLabelIndices(labels);
		
		// Init Position and value of maximum for each label
		int nLabels = labels.length;
		PositionValuePair2D[] pairs = new PositionValuePair2D[nLabels]; 
		for (int i = 0; i < nLabels; i++) 
		{
			pairs[i] = new PositionValuePair2D(new Cursor2D(-1, -1), Double.POSITIVE_INFINITY);
		}
		
		// iterate on image pixels
		int sizeX = labelImage.size(0);
		int sizeY = labelImage.size(1);
		for (int y = 0; y < sizeY; y++) 
		{
			for (int x = 0; x < sizeX; x++) 
			{
				int label = (int) labelImage.getValue(x, y);
				
				// do not process pixels that do not belong to any particle
				if (label == 0)
					continue;
				if (!labelIndices.containsKey(label))
					continue;

				// get position-value pair corresponding to current label
				int index = labelIndices.get(label);
				PositionValuePair2D pair = pairs[index];
				
				// update values and positions
				double value = valueImage.getValue(x, y);
				if (value < pair.value) 
				{
					pair.position = new Cursor2D(x, y);
					pair.value = value;
				}
			}
		}
				
		return pairs;
	}

	/**
	 * For each label, finds the position of the 3D point belonging to 3D label
	 * region defined by <code>labelImage</code> and with minimal value in 3D
	 * intensity image <code>valueImage</code>.
	 * 
	 * @param valueImage
	 *            the intensity image containing values to compare
	 * @param labelImage
	 *            the intensity image containing label of each pixel
	 * @param labels
	 *            the list of labels in the label image
	 * @return the position of minimum value in intensity image for each label
	 */
	public static final PositionValuePair3D[] findMinValues3d(
	        IntArray3D<?> labelImage, int[] labels, ScalarArray3D<?> valueImage)
	{
		// get image size
		int sizeX = labelImage.size(0);
		int sizeY = labelImage.size(1);
		int sizeZ = labelImage.size(2);

		// check image dimensions
		if (labelImage.size(0) != sizeX || labelImage.size(1) != sizeY || labelImage.size(2) != sizeZ)
		{
			throw new IllegalArgumentException("Both images must have same dimensions");
		}

		// Create associative map between each label and its index
		Map<Integer,Integer> labelIndices = LabelImages.mapLabelIndices(labels);
		
		// Init Position and value of maximum for each label
		int nLabels = labels.length;
		PositionValuePair3D[] pairs = new PositionValuePair3D[nLabels]; 
		for (int i = 0; i < nLabels; i++) 
		{
			pairs[i] = new PositionValuePair3D(new Cursor3D(-1, -1, -1), Double.POSITIVE_INFINITY);
		}
		
		// iterate on image voxels
		for (int z = 0; z < sizeZ; z++) 
		{
			for (int y = 0; y < sizeY; y++) 
			{
				for (int x = 0; x < sizeX; x++) 
				{
					int label = (int) labelImage.getValue(x, y, z);

					// do not process pixels that do not belong to any particle
					if (label == 0)
						continue;
					if (!labelIndices.containsKey(label))
						continue;

					// get position-value pair corresponding to current label
					int index = labelIndices.get(label);
					PositionValuePair3D pair = pairs[index];

					// update values and positions
					double value = valueImage.getValue(x, y, z);
					if (value < pair.value) 
					{
						pair.position = new Cursor3D(x, y, z);
						pair.value = value;
					}
				}
			}
		}
				
		return pairs;
	}

	/**
	 * For each label, finds the position of the point belonging to label region
	 * defined by <code>labelImage</code> and with maximal value in intensity
	 * image <code>valueImage</code>.
	 * 
	 * @param valueImage
	 *            the intensity image containing values to compare
	 * @param labelImage
	 *            the intensity image containing label of each pixel
	 * @param labels
	 *            the list of labels in the label image
	 * @return the position of maximum value in intensity image for each label
	 */
    public static final Cursor2D[] maxValuePositions2d(IntArray2D<?> labelImage, int[] labels,
            ScalarArray2D<?> valueImage)
	{
        // get image size
        int sizeX = labelImage.size(0);
        int sizeY = labelImage.size(1);

        // check image dimensions
        if (labelImage.size(0) != sizeX || labelImage.size(1) != sizeY)
        {
            throw new IllegalArgumentException("Both images must have same dimensions");
        }

        // Create associative map between each label and its index
		Map<Integer,Integer> labelIndices = LabelImages.mapLabelIndices(labels);
		
		// Init Position and value of maximum for each label
		int nLabels = labels.length;
		Cursor2D[] posMax 	= new Cursor2D[nLabels];
		double[] maxValues = new double[nLabels];
		for (int i = 0; i < nLabels; i++) 
		{
			maxValues[i] = Double.NEGATIVE_INFINITY;
			posMax[i] = new Cursor2D(-1, -1);
		}
		
		// iterate on image pixels
		for (int y = 0; y < sizeY; y++) 
		{
			for (int x = 0; x < sizeX; x++) 
			{
				int label = labelImage.getInt(x, y);
				
				// do not process pixels that do not belong to any particle
				if (label == 0)
					continue;

				int index = labelIndices.get(label);
				
				// update values and positions
				double value = valueImage.getValue(x, y);
				if (value > maxValues[index]) 
				{
					posMax[index] = new Cursor2D(x, y);
					maxValues[index] = value;
				}
			}
		}
				
		return posMax;
	}

	/**
	 * For each label, finds the position of the point belonging to label region
	 * defined by <code>labelImage</code> and with maximal value in intensity
	 * image <code>valueImage</code>.
	 * 
	 * @param valueImage
	 *            the intensity image containing values to compare
	 * @param labelImage
	 *            the intensity image containing label of each pixel
	 * @param labels
	 *            the list of labels in the label image
	 * @return the position of maximum value in intensity image for each label
	 */
    public static final Cursor3D[] maxValuePositions3d(IntArray3D<?> labelImage, int[] labels,
            ScalarArray3D<?> valueImage)
	{
		// get image size
		int sizeX = labelImage.size(0);
		int sizeY = labelImage.size(1);
		int sizeZ = labelImage.size(2);

		// check image dimensions
		if (labelImage.size(0) != sizeX || labelImage.size(1) != sizeY || labelImage.size(2) != sizeZ)
		{
			throw new IllegalArgumentException("Both images must have same dimensions");
		}

		// Create associative map between each label and its index
		Map<Integer,Integer> labelIndices = LabelImages.mapLabelIndices(labels);
		
		// Init Position and value of maximum for each label
		int nLabels = labels.length;
		Cursor3D[] posMax 	= new Cursor3D[nLabels];
		double[] maxValues = new double[nLabels];
		for (int i = 0; i < nLabels; i++) 
		{
			maxValues[i] = Double.NEGATIVE_INFINITY;
			posMax[i] = new Cursor3D(-1, -1, -1);
		}
		
		// iterate on image pixels
		for (int z = 0; z < sizeZ; z++) 
		{
			for (int y = 0; y < sizeY; y++) 
			{
				for (int x = 0; x < sizeX; x++) 
				{
					int label = labelImage.getInt(x, y, z);

					// do not process pixels that do not belong to any particle
					if (label == 0)
						continue;

					int index = labelIndices.get(label);

					// update values and positions
					double value = valueImage.getValue(x, y, z);
					if (value > maxValues[index]) 
					{
						posMax[index] = new Cursor3D(x, y, z);
						maxValues[index] = value;
					}
				}
			}
		}
				
		return posMax;
	}

	/**
	 * For each label, finds the position of the point belonging to label region
	 * defined by <code>labelImage</code> and with minimal value in intensity
	 * image <code>valueImage</code>.
	 * 
	 * @param valueImage
	 *            the intensity image containing values to compare
	 * @param labelImage
	 *            the intensity image containing label of each pixel
	 * @param labels
	 *            the list of labels in the label image
	 * @return the position of minimum value in intensity image for each label
	 */
    public static final Cursor2D[] minValuePositions2d(IntArray2D<?> labelImage, int[] labels,
            ScalarArray2D<?> valueImage)
	{
        // get image size
        int sizeX = labelImage.size(0);
        int sizeY = labelImage.size(1);

        // check image dimensions
        if (labelImage.size(0) != sizeX || labelImage.size(1) != sizeY)
        {
            throw new IllegalArgumentException("Both images must have same dimensions");
        }

		// Create associative map between each label and its index
		Map<Integer,Integer> labelIndices = LabelImages.mapLabelIndices(labels);
		
		// Init Position and value of minimum for each label
		int nLabels = labels.length;
		Cursor2D[] posMax 	= new Cursor2D[nLabels];
		double[] maxValues = new double[nLabels];
		for (int i = 0; i < nLabels; i++) 
		{
			maxValues[i] = Double.POSITIVE_INFINITY;
			posMax[i] = new Cursor2D(-1, -1);
		}
		
        // iterate on image pixels
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++) 
            {
				int label = labelImage.getInt(x, y);
				
				// do not process pixels that do not belong to any particle
				if (label == 0)
					continue;

				int index = labelIndices.get(label);
				
				// update values and positions
				double value = valueImage.getValue(x, y);
				if (value < maxValues[index]) 
				{
					posMax[index] = new Cursor2D(x, y);
					maxValues[index] = value;
				}
			}
		}
				
		return posMax;
	}
	
	/**
	 * For each label, finds the position of the point belonging to label region
	 * defined by <code>labelImage</code> and with minimal value in intensity
	 * image <code>valueImage</code>.
	 * 
	 * @param valueImage
	 *            the intensity image containing values to compare
	 * @param labelImage
	 *            the intensity image containing label of each pixel
	 * @param labels
	 *            the list of labels in the label image
	 * @return the position of minimum value in intensity image for each label
	 */
    public static final Cursor3D[] minValuePositions3d(IntArray3D<?> labelImage, int[] labels,
            ScalarArray3D<?> valueImage)
	{
		// get image size
		int sizeX = labelImage.size(0);
		int sizeY = labelImage.size(1);
		int sizeZ = labelImage.size(2);

		// check image dimensions
		if (labelImage.size(0) != sizeX || labelImage.size(1) != sizeY || labelImage.size(2) != sizeZ)
		{
			throw new IllegalArgumentException("Both images must have same dimensions");
		}

		// Create associative map between each label and its index
		Map<Integer,Integer> labelIndices = LabelImages.mapLabelIndices(labels);
		
		// Init Position and value of maximum for each label
		int nLabels = labels.length;
		Cursor3D[] posMax 	= new Cursor3D[nLabels];
		double[] maxValues = new double[nLabels];
		for (int i = 0; i < nLabels; i++) 
		{
			maxValues[i] = Double.POSITIVE_INFINITY;
			posMax[i] = new Cursor3D(-1, -1, -1);
		}
		
		// iterate on image pixels
		for (int z = 0; z < sizeZ; z++) 
		{
			for (int y = 0; y < sizeY; y++) 
			{
				for (int x = 0; x < sizeX; x++) 
				{
					int label = labelImage.getInt(x, y, z);

					// do not process pixels that do not belong to any particle
					if (label == 0)
						continue;

					int index = labelIndices.get(label);

					// update values and positions
					double value = valueImage.getValue(x, y, z);
					if (value < maxValues[index]) 
					{
						posMax[index] = new Cursor3D(x, y, z);
						maxValues[index] = value;
					}
				}
			}
		}
				
		return posMax;
	}

    /**
     * Private constructor to prevent class instantiation.
     */
    private LabelValues()
    {
    }
    
	public static class PositionValuePair2D
	{
	    Cursor2D position;
		double value;
		
		public PositionValuePair2D(Cursor2D position, double value)
		{
			this.position = position;
			this.value = value;
		}
		
		public Cursor2D getPosition()
		{
			return position;
		}
		
		public double getValue()
		{
			return value;
		}
	}

	public static class PositionValuePair3D
	{
		Cursor3D position;
		double value;
		
		public PositionValuePair3D(Cursor3D position, double value)
		{
			this.position = position;
			this.value = value;
		}
		
		public Cursor3D getPosition()
		{
			return position;
		}
		
		public double getValue()
		{
			return value;
		}
	}
}
