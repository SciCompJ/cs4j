/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.algo.AlgoStub;
import net.sci.image.morphology.Strel2D;

/**
 * Implementation basis for planar structuring elements. 
 * 
 * @author David Legland
 *
 */
//* Morphological operations for stacks are implemented such that the planar
//* strel is applied to each slice, and the result is added to the result
//* array.
public abstract class AbstractStrel2D extends AlgoStub implements Strel2D 
{	
	private String channelName = null;
	
//	public int[][][] getMask3D() {
//		int[][][] mask3d = new int[1][][];
//		mask3d[0] = getMask();
//		return mask3d;
//	}
//	
//	public int[][] getShifts3D() {
//		int [][] shifts = getShifts();
//		int ns = shifts.length;
//		
//		int [][] shifts3d = new int[ns][3];
//		for (int i = 0; i < ns; i++)
//			shifts3d[i] = new int[]{shifts3d[i][0], shifts3d[i][1], 0};
//		
//		return shifts3d;
//	}
	
	/**
	 * Sets the name of the currently processed channel.
	 */
	public void setChannelName(String channelName) 
	{
		this.channelName = channelName;
	}

	/**
	 * Returns the name of the channel currently processed, or null by default.
	 */
	public String getChannelName()
	{
		return this.channelName;
	}
	
//	public Array3D<?> dilation(Array3D<?> stack) {
//		boolean flag = this.showProgress();
//		this.showProgress(false);
//		
//		Array3D<?> result = (Array3D<?>) stack.duplicate();
//		
//		int nSlices = stack.getSize(2);
//		for (int i = 1; i <= nSlices; i++) 
//		{
//			this.showProgress(flag);
//			fireProgressChanged(this, i-1, nSlices);
//			this.showProgress(false);
//			
//			ImageProcessor img = stack.getProcessor(i);
//			img = dilation(img);
//			result.setProcessor(img, i);
//		}
//		
//		// notify end of slices progression
//		this.showProgress(flag);
//		fireProgressChanged(this, nSlices, nSlices);
//		
//		return result;
//	}
//	
//	public Array3D<?> erosion(Array3D<?> stack) {
//		boolean flag = this.showProgress();
//		
//		int nSlices = stack.getSize();
//		Array3D<?> result = stack.duplicate();
//		for (int i = 1; i <= nSlices; i++) {
//			this.showProgress(flag);
//			fireProgressChanged(this, i-1, nSlices);
//			this.showProgress(false);
//			
//			ImageProcessor img = stack.getProcessor(i);
//			img = erosion(img);
//			result.setProcessor(img, i);
//		}
//		
//		// notify end of slices progression
//		this.showProgress(flag);
//		fireProgressChanged(this, nSlices, nSlices);
//		
//		return result;
//	}
//	
//	public Array3D<?> closing(Array3D<?> stack) {
//		boolean flag = this.showProgress();
//		this.showProgress(false);
//		
//		int nSlices = stack.getSize();
//		Array3D<?> result = stack.duplicate();
//		for (int i = 1; i <= nSlices; i++) {
//			this.showProgress(flag);
//			fireProgressChanged(this, i-1, nSlices);
//			this.showProgress(false);
//			
//			ImageProcessor img = stack.getProcessor(i);
//			img = closing(img);
//			result.setProcessor(img, i);
//		}
//		
//		// notify end of slices progression
//		this.showProgress(flag);
//		fireProgressChanged(this, nSlices, nSlices);
//		
//		return result;
//	}
//	
//	public Array3D<?> opening(Array3D<?> stack) {
//		boolean flag = this.showProgress();
//		this.showProgress(false);
//		
//		int nSlices = stack.getSize();
//		Array3D<?> result = stack.duplicate();
//		for (int i = 1; i <= nSlices; i++) {
//			this.showProgress(flag);
//			fireProgressChanged(this, i-1, nSlices);
//			this.showProgress(false);
//			
//			ImageProcessor img = stack.getProcessor(i);
//			img = opening(img);
//			result.setProcessor(img, i);
//		}
//		
//		// notify end of slices progression
//		this.showProgress(flag);
//		fireProgressChanged(this, nSlices, nSlices);
//		
//		return result;
//	}

}
