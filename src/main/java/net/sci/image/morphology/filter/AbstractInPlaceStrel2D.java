/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.array.scalar.ScalarArray2D;

/**
 * Implementation stub for in place Structuring elements. Implements operations
 * methods by calling in-place versions.
 * 
 * @author David Legland
 *
 */
public abstract class AbstractInPlaceStrel2D extends AbstractStrel2D implements	InPlaceStrel2D
		{
//	public ImageStack dilation(ImageStack stack) {
//		ImageStack result = stack.duplicate();
//		this.inPlaceDilation(result);
//		return result;
//	}
//	
//	public ImageStack erosion(ImageStack stack) {
//		ImageStack result = stack.duplicate();
//		this.inPlaceErosion(result);
//		return result;
//	}
//	
//	public ImageStack closing(ImageStack stack) {
//		ImageStack result = stack.duplicate();
//		this.inPlaceDilation(result);
//		this.reverse().inPlaceErosion(result);
//		return result;
//	}
//	
//	public ImageStack opening(ImageStack stack) {
//		ImageStack result = stack.duplicate();
//		this.inPlaceErosion(result);
//		this.reverse().inPlaceDilation(result);
//		return result;
//	}
//	
//	public void inPlaceDilation(ImageStack stack) {
//		boolean flag = this.showProgress();
//		this.showProgress(false);
//		
//		int nSlices = stack.getSize();
//		for (int i = 1; i <= nSlices; i++) {
//			if (flag) {
//				IJ.showProgress(i-1, nSlices);
//			}
//			
//			Array2D<?> img = stack.getProcessor(i);
//			this.inPlaceDilation(img);
//			stack.setProcessor(img, i);
//		}
//		
//		if (flag) {
//			IJ.showProgress(1);
//		}
//		this.showProgress(flag);
//	}
//
//	public void inPlaceErosion(ImageStack stack) {
//		boolean flag = this.showProgress();
//		this.showProgress(false);
//		
//		int nSlices = stack.getSize();
//		for (int i = 1; i <= nSlices; i++) {
//			if (flag) {
//				IJ.showProgress(i-1, nSlices);
//			}
//			
//			Array2D<?> img = stack.getProcessor(i);
//			this.inPlaceErosion(img);
//			stack.setProcessor(img, i);
//		}
//
//		if (flag) {
//			IJ.showProgress(1);
//		}
//		this.showProgress(flag);
//	}

	public ScalarArray2D<?> dilation(ScalarArray2D<?> image)
	{
	    ScalarArray2D<?> result = image.duplicate();
		this.inPlaceDilation(result);
		return result;
	}

	public ScalarArray2D<?> erosion(ScalarArray2D<?> image)
	{
	    ScalarArray2D<?> result = image.duplicate();
		this.inPlaceErosion(result);
		return result;
	}

	public ScalarArray2D<?> closing(ScalarArray2D<?> image)
	{
	    ScalarArray2D<?> result = image.duplicate();
		this.inPlaceDilation(result);
		this.reverse().inPlaceErosion(result);
		return result;
	}

	public ScalarArray2D<?> opening(ScalarArray2D<?> image)
	{
	    ScalarArray2D<?> result = image.duplicate();
		this.inPlaceErosion(result);
		this.reverse().inPlaceDilation(result);
		return result;
	}
}
