/**
 * 
 */
package net.sci.optim;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Simplex optimizer, adapted from Numerical Recipes 3.
 * 
 * Stores a (ND+1)-by-ND simplex, evaluates function at each vertex of the
 * simplex, and update position of simplex vertices to converge towards 
 * minimum.   
 * 
 * @author dlegland
 *
 */
public class NelderMeadSimplexOptimizer extends Optimizer 
{
	
	public final static double TINY = 1e-10;
	
    /** maximum number of iterations */
    int maximumIterationNumber = 200;
    
    /** tolerance on function value */ 
    double functionValueTolerance = 1e-5;
    
    /** delta in each direction */
    double[] deltas; 
    
    /** the inner simplex, as a (ND+1)-by-ND array */
    double[][] simplex;
    
    /** the vector of function evaluations, one for each vertex of the simplex */
    double[] evals;
    
    /** the sum of the vertex coordinates */
    double[] psum;
    
    /** number of function evaluations */
    int numFunEvals;
	
    
	// =============================================================
	// Constructors

    public NelderMeadSimplexOptimizer(ScalarFunction fun, double[] initialParameters)
    {
    	this.function = fun;

    	// recopy values of initial state
    	int n = initialParameters.length;
    	this.params = new double[n];
    	for (int i = 0; i < n; i++)
    	{
        	this.params[i] = initialParameters[i];
        }
    	initializeDeltas();
    }

    public NelderMeadSimplexOptimizer(ScalarFunction fun, double[] initialParameters, double[] initialDeltas)
    {
    	this.function = fun;

    	// recopy values of initial state
    	int n = initialParameters.length;
    	this.params = new double[n];
    	this.deltas = new double[n];
    	for (int i = 0; i < n; i++)
    	{
        	this.params[i] = initialParameters[i];
        	this.deltas[i] = initialDeltas[i];
    	}
    }
    
	
    // =============================================================
	// construction helper methods

    private void initializeDeltas()
    {
    	int n = this.params.length;
    	this.deltas = new double[n];
    	for (int i = 0;i < n; i++)
    	{
    		this.deltas[i] = Math.max(this.params[i] / 2, .01);
    	}
    }

    
    // =============================================================
	// methods specific to Nelder-Mead simplex

    
    // =============================================================
	// Implementation of Optimizer interface

    /* (non-Javadoc)
	 * @see net.sci.optim.Optimizer#startOptimization()
	 */
	@Override
	public ScalarFunction.EvaluationResult startOptimization() 
	{
		// Notify beginning of optimization
		// this.notify('OptimizationStarted');
		
		// initialize the simplex.
		initializeSimplex();
		
		// state of the algorithm
		String exitMessage = "Algorithm started";
		System.out.println(exitMessage);
		
		boolean converged = false;
		
		int n = this.params.length;
		
		int indLow = 0;
		int indHigh = n-1;
		int indNext = Math.max(0, n - 2);
		
		double fLow;
		double fHigh;
		double fTry;
		double rtol;
		
		// Main loop

		// infinite loop
		int iter = 1;
		while (true)
		{
			// first, determines the indices of points with the highest (i.e.
			// worst), next highest, and lowest (i.e. best) values.
			Integer[] indices = new Integer[n + 1];
			for (int i = 0; i < n+1; i++)
			{
				indices[i] = i;
			}

			// need to recopy array ref, do not know why ?
			final double[] data = this.evals;
			Arrays.sort(indices, new Comparator<Integer>() {
			    @Override public int compare(final Integer ind1, final Integer ind2) {
			        return Double.compare(data[ind1], data[ind2]);
			    }
			});
			
			// find indices of vertices with extreme values
		    indLow  = indices[0];
		    indHigh = indices[n];
		    indNext = indices[n - 1];
		    
		    // update optimized value and position
		    this.params = this.simplex[indLow];
		    this.value  = this.evals[indLow];
		    
		    // compute relative difference between highest and lowest
		    fLow    = this.evals[indLow];
		    fHigh   = this.evals[indHigh];
		    rtol = 2 * Math.abs(fHigh - fLow) / (Math.abs(fHigh) + Math.abs(fLow) + TINY);

		    // termination with function evaluation
		    if (rtol < this.functionValueTolerance)
		    {
		        exitMessage = String.format("Function converged with relative tolerance %g", 
		        		this.functionValueTolerance);
		        System.out.println(exitMessage);
		        converged = true;
		        break;
		    }
		    
		    // begin a new iteration
		    
		    // first extrapolate by a factor -1 through the face of the simplex
		    // opposite to the highest point.
		    ScalarFunction.EvaluationResult eval = evaluateReflection(indHigh, -1);
		    fTry = eval.getValue();
		    
		    // if the value at the evaluated position is better than current
		    // highest value, then replace the highest value
		    if (fTry < this.evals[indHigh])
		    {
		        this.updateSimplex(indHigh, eval);
//		        if strcmp(this.displayMode, 'iter')
		            System.out.println("  reflection");
//		        end
//		        this.notify('OptimizationIterated');
		    }

		     // if new evaluation is better than current minimum, try to expand
		    if (fTry <= this.evals[indLow])
		    {
		        eval = this.evaluateReflection(indHigh, 2);
		        fTry = eval.getValue();
			    
		        if (fTry < this.evals[indHigh])
		        {
		        	// expansion was successful
		            this.updateSimplex(indHigh, eval);
//		            if strcmp(this.displayMode, 'iter')
		                System.out.println("  expansion");
//		            end
//		            this.notify('OptimizationIterated');
		        }
		    }
		    else if (fTry >= this.evals[indNext])
		    {
		    	// if new evaluation is worse than the second-highest point, look
		    	// for an intermediate point (i.e. do a one-dimensional contraction)
		    	eval = this.evaluateReflection(indHigh, .5);
		    	fTry = eval.getValue();

		    	if (fTry < this.evals[indHigh])
		    	{
		    		//if contraction was successful simplex is updated
		    		this.updateSimplex(indHigh, eval);
		    		//			            if strcmp(this.displayMode, 'iter')
		    		System.out.println("  contraction");
		    		//			            end
		    		//			            this.notify('OptimizationIterated');
		    	}   
		    	else
		    	{
		    		// 1D contraction was not successful, so perform a shrink
		    		// (multidimensional contraction) around lowest point
		    		this.contractSimplex(indLow);
		    		//		            if strcmp(this.displayMode, "iter")
		    		System.out.println("  shrink");
		    		//		            end
		    		//		            this.notify('OptimizationIterated');
		    	}
		    }

		    // termination with number of iterations
		    if (iter > this.maximumIterationNumber)
		    {
		    	exitMessage = String.format("Iteration number reached maximum allowed value: %d", 
		    			this.maximumIterationNumber);
		    	System.out.println(exitMessage);
		    	break;
		    }

		    iter = iter + 1;
		} // main iteration loop

		if (converged)
		{
//	    if (strcmp(this.displayMode, {'iter', 'final'}))
//	    {
	        System.out.println(exitMessage);
//	    }
		}
		else
		{	
//	    if strcmp(this.displayMode, {'iter', 'final', 'notify'})
	        System.out.println(exitMessage);
//	    end
		}

//		// send termination event
//		this.notify("OptimizationTerminated");
		return new ScalarFunction.EvaluationResult(this.params, this.value);
	}

    /**
     * Initializes simplex from initial parameters array and delta array.
     * 
     * The resulting simplex is a (ND+1)-by-ND array containing the coordinates
     * of a vertex on each row.
     */
    private void initializeSimplex()
    {
    	if (this.params == null)
    	{
    		throw new RuntimeException("Requires initialisation of optimizer state 'theta')");
    	}
    	int n = this.params.length;
    	
        // initialize vertex coordinates
        this.simplex = new double[n+1][];
        for (int i = 0; i < n; i++)
        {
        	// initialize with current state
        	double[] vi = new double[n];
        	for (int j = 0; j < n; j++)
        	{
        		vi[j] = this.params[j];
        	}
        	
        	// add increment in i-th direction
        	vi[i] = vi[i] + this.deltas[i];
        	
        	this.simplex[i] = vi;
        }
        
        // last vertex is the current position
    	double[] vi = new double[n];
    	for (int j = 0; j < n; j++)
    	{
    		vi[j] = this.params[j];
    	}	
    	this.simplex[n] = vi;
        
    	// computes the sum of vertex coordinates
    	this.psum = new double[n];
    	for (int j = 0; j < n; j++)
    	{
    		for (int i = 0; i < n+1; i++)
    		{
    			psum[j] += this.simplex[i][j];
    		}
    	}
    	
    	 // evaluate function for each vertex of the simplex
         this.evals = new double[n + 1];
         for (int i = 0; i < n+1; i++)
         {
             this.evals[i] = this.function.evaluate(this.simplex[i]);
         }
         
         // reset number of function evaluations
         this.numFunEvals = 0;
    }
    
    /**
	 * helper function that evaluates the value of the function at the
	 * reflection of point with index <code>index</code>.
	 *
	 * @param index
	 *            the index of the vertex to refect, between 0 and nd
	 * @param factor
	 *            the amount of reflection; expansion (>1), reflection (<0) or
	 *            contraction (0<F<1)
	 * @return an instance of UnivariateFunction.EvaluationResult
	 */
    private ScalarFunction.EvaluationResult evaluateReflection(int index, double factor)
    {        
        // compute weighting factors
        int nd = this.params.length;
        double fac1 = (1 - factor) / nd;
        double fac2 = fac1 - factor;
         
        // compute position of the new candidate point
        double[] ptry = new double[nd];
        for (int j = 0; j < nd; j++)
        {
        	ptry[j] = this.psum[j] * fac1 - this.simplex[index][j] * fac2;
        }
        
        // evaluate function value
        double ytry = this.function.evaluate(ptry);
        this.numFunEvals = this.numFunEvals + 1;
        
    	return new ScalarFunction.EvaluationResult(ptry, ytry);
    }
    
    /**
     * Updates the vertex by changing the position of a vertex. 
     * The set of evaluated values is updated as well.
     */
    private void updateSimplex(int index, ScalarFunction.EvaluationResult eval)
    {
    	this.evals[index] = eval.getValue();
    	
    	int n = this.params.length;
    	double[] pos = eval.getPosition();
    	
    	for (int j = 0; j < n; j++)
    	{
    		// update sum of simplex coordinates
        	this.psum[j] = this.psum[j] - this.simplex[index][j] + pos[j];
        	
        	// update coords of specified vertex
        	this.simplex[index][j] = pos[j];
    	}
    }

    private void contractSimplex(int indLow)
    {
    	int n = this.params.length;
    	
    	double[] pLow = this.simplex[indLow];
    	
    	// update vertices before indlow
    	for (int i = 0; i < n+1; i++)
    	{
    		// do not update vertex with specified index
    		if (i == indLow)
    		{
    			continue;
    		}
    		
    		for (int j = 0; j < n; j++)
    		{
        		// update coords of current vertex to be half of previous 
    			// position with position of specified vertex
    			this.simplex[i][j] = (this.simplex[i][j] + pLow[j]) * .5;
    			
    			// also update function evaluation for current vertex
    			this.evals[i] = this.function.evaluate(this.simplex[i]);
    		}
    	}

    	// update number of function evaluations
    	this.numFunEvals = this.numFunEvals + n;
    }

	/* (non-Javadoc)
	 * @see net.sci.optim.Optimizer#getFinalResult()
	 */
	@Override
	public double[] getFinalResult() 
	{
		return this.params;
	}

	public double getFinalValue() 
	{
		return this.value;
	}

	public final static void main(String[] args)
	{
		// Create function to evaluate
		ScalarFunction fun = new RosenbrockFunction();
		
		// initial starting value
		double[] params0 = new double[]{0, 0};
		double[] deltas = new double[]{.1, .1};
		
		Optimizer optim = new NelderMeadSimplexOptimizer(fun, params0, deltas);
		
		optim.startOptimization();
		
		double[] pos = optim.getFinalResult();
		double value = fun.evaluate(pos);
		System.out.println(String.format("Final value at position (%g ; %g) is %g", 
				pos[0], pos[1], value));
	}
}
