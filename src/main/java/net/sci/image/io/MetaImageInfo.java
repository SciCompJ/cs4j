/**
 * 
 */
package net.sci.image.io;

/**
 * MetaImage file format manager. Contains declaration of constants, and handles file info.
 */
public final class MetaImageInfo
{
	/**
	 * The set of element types managed by this class.
	 */
	public enum ElementType 
	{
		UINT8,
		UINT16;
		
		/**
		 * Parses element type from its name. Should be case insensitive.
		 */
		public static final ElementType fromLabel(String label) 
		{
			if (label != null)
			{
				label = label.toLowerCase();
			}
			for (ElementType elementType : ElementType.values())
			{
				String cmp = elementType.toString().toLowerCase();
				if (cmp.equals(label))
				{
					return elementType;
				}
			}
			throw new IllegalArgumentException("Unable to parse ElementType with label: " + label);
		}
	}

	public String ObjectTypeName = "";

	/** Number of dimensions, should be >0 when initialized */
	public int nDims = 0;

	/** The size in each dimension */
	public int[] dimSize = null;

	/** the type of element stored in this file */
	public ElementType elementType = ElementType.UINT8;
	public String elementTypeName = "";

	/** the name of the file containing the data */
	public String elementDataFile = null;

	public int headerSize = 0;

	// values for spatial calibration
	public double[] elementSpacing = null;
	public double[] elementSize = null;

	public boolean elementByteOrderMSB = false;
	public int elementNumberOfChannels = 1;

	public boolean binaryData = true;
	public boolean binaryDataByteOrderMSB = false;
	public boolean compressedData = false;
	public int compressedDataSize = 0;

	public String anatomicalOrientation = "";
	public double[] centerOfRotation = null;
	public double[] offset = null;
}
