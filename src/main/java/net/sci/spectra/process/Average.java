/**
 * 
 */
package net.sci.spectra.process;

import net.sci.axis.CategoricalAxis;
import net.sci.spectra.Spectra;

/**
 * Computes the average spectrum from a collection of spectra. Returns the
 * result in a Spectra instance with one row.
 */
public class Average
{
    public Spectra process(Spectra spectra)
    {
        int nr = spectra.rowCount();
        int nc = spectra.columnCount();
        
        // compute average of each column
        double[] avg = new double[nc];
        for (int c = 0; c < nc; c++)
        {
            double sum = 0;
            for (int r = 0; r < nr; r++)
            {
                sum += spectra.getValue(r, c);
            }
            
            avg[c] = sum / nr;
        }
        
        
        String name = spectra.getName();
        if (name != null)
        {
            name += "-mean";
        }
        else
        {
            name = "mean";
        }
        
        Spectra res = new Spectra(new double[][] {avg});
        res.setColumnAxis(spectra.getColumnAxis().duplicate());
        res.setRowAxis(new CategoricalAxis("name", new String[] {name}));
        
        return res;
    }
}
