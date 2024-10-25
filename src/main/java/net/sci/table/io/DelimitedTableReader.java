/**
 * 
 */
package net.sci.table.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

import net.sci.table.CategoricalColumn;
import net.sci.table.Column;
import net.sci.table.NumericColumn;
import net.sci.table.Table;
import net.sci.table.impl.ColumnsTable;

/**
 * Read a table from a delimited file. Many options can be set, such as the type
 * of delimiter, the number of lines to skip, or the presence/absence of row
 * names.
 * 
 * @author dlegland
 *
 */
public class DelimitedTableReader implements TableReader
{
    // =============================================================
    // Class variables

    /**
     * The delimiters between the tokens within the file. Default is " \t",
     * corresponding to either space or tabulation delimiter.
     */
    String delimiters = " \t";

    /**
     * Specifies if column header are present in the file. Default is true.
     */
    boolean readHeader = true;

    /**
     * The number of lines to skip before starting reading data. Default is 0.
     */
    int skipLines = 0;

    /**
     * Specifies if row names are present in the file. Default is true.
     */
    boolean readRowNames = true;

    
    // =============================================================
    // Constructors

    /**
     * Creates a new instance of DelimitedTableReader.
     */
    public DelimitedTableReader()
    {
    }

    /**
     * Creates a new instance of DelimitedTableReader, specifying the
     * delimiters.
     * 
     * @param delimiters
     *            the delimiters
     */
    public DelimitedTableReader(String delimiters)
    {
        this.delimiters = delimiters;
    }

    
    // =============================================================
    // Accessors and mutators

    public String getDelimiters()
    {
        return delimiters;
    }

    public void setDelimiters(String delimiters)
    {
        this.delimiters = delimiters;
    }

    public boolean isReadHeader()
    {
        return readHeader;
    }

    public void setReadHeader(boolean readHeader)
    {
        this.readHeader = readHeader;
    }

    public int getSkipLines()
    {
        return skipLines;
    }

    public void setSkipLines(int skipLines)
    {
        this.skipLines = skipLines;
    }

    public boolean isReadRowNames()
    {
        return readRowNames;
    }

    public void setReadRowNames(boolean readRowNames)
    {
        this.readRowNames = readRowNames;
    }

    
    // =============================================================
    // implementation of the TableReader interface

    /*
     * (non-Javadoc)
     * 
     * @see net.sci.table.io.TableReader#readTable()
     */
    @Override
    public Table readTable(File file) throws IOException
    {
        // meta data for table
        int nCols;
        ArrayList<ArrayList<String>> columnTokens;
        String[] colNames;
        ArrayList<String> rowNames = new ArrayList<String>();

        // Create text reader from file
        LineNumberReader reader;
        try
        {
            reader = new LineNumberReader(new FileReader(file));
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Could not open file: " + file, ex);
        }

        // optionally skip some lines
        for (int r = 0; r < skipLines; r++)
        {
            reader.readLine();
        }

        String delimiterRegexp = "[" + delimiters + "]+";

        // parse header line
        String firstLine = reader.readLine();
        String[] lineTokens = firstLine.split(delimiterRegexp);

        // parse first line to identify number of columns
        if (readHeader)
        {
            // first line is the header
            nCols = lineTokens.length - (readRowNames ? 1 : 0);
            colNames = new String[nCols];
            for (int i = 0; i < nCols; i++)
            {
                colNames[i] = lineTokens[i + (readRowNames ? 1 : 0)];
            }
        }
        else
        {
            // first line is a data line
            nCols = lineTokens.length;
            colNames = new String[nCols];
        }

        // Allocate array lists for columns
        columnTokens = new ArrayList<ArrayList<String>>(nCols);
        for (int c = 0; c < nCols; c++)
        {
            columnTokens.add(new ArrayList<String>());
        }

        if (!readHeader)
        {
            // read column values as strings
            int offset = readRowNames ? 1 : 0;
            for (int c = 0; c < nCols; c++)
            {
                columnTokens.get(c).add(lineTokens[c + offset]);
            }
        }

        // read regular lines
        while (true)
        {
            String line = reader.readLine();
            if (line == null)
            {
                break;
            }
            if (line.isEmpty())
            {
                break;
            }

            // read tokens of current row
            lineTokens = line.split(delimiterRegexp);
            if (readRowNames)
            {
                rowNames.add(lineTokens[0]);
                // read column values as strings
                for (int c = 0; c < nCols; c++)
                {
                    columnTokens.get(c).add(lineTokens[c + 1]);
                }
            }
            else
            {
                // read column values as strings
                for (int c = 0; c < nCols; c++)
                {
                    columnTokens.get(c).add(lineTokens[c]);
                }
            }
        }

        reader.close();

        // convert columns
        Column[] columns = new Column[nCols];
        for (int c = 0; c < nCols; c++)
        {
            columns[c] = createColumn(colNames[c], columnTokens.get(c));
        }
        Table table = new ColumnsTable(columns);

        // populates meta-data
        if (readRowNames)
        {
            table.setRowNames(rowNames.toArray(new String[0]));
        }

        // also set the name of the table to the name of the file
        table.setName(file.getName());

        return table;
    }
    
    private static final Column createColumn(String name, ArrayList<String> tokens)
    {
        if (areAllNumeric(tokens))
        {
            return createNumericColumn(name, tokens);
        }
        else
        {
            return createCategoricalColumn(name, tokens);
        }
    }
    
    /**
     * Checks if the tokens of a given are all numeric or not.
     */
    private static final boolean areAllNumeric(ArrayList<String> tokens)
    {
        for (String token : tokens)
        {
            // test if contains a numeric value or not
            if (!token.matches(".*\\d.*")) return false;
        }
        return true;
    }
    
    /**
     * Creates a new numeric columns from a list of token containing numeric
     * values.
     * 
     * @param name
     *            the name of the column
     * @param tokens
     *            the tokens to convert
     * @return a new numeric column
     */
    private static final NumericColumn createNumericColumn(String name, ArrayList<String> tokens)
    {
        int nRows = tokens.size();
        double[] colValues = new double[nRows];
        for (int r = 0; r < nRows; r++)
        {
            String token = tokens.get(r);
            colValues[r] = Double.parseDouble(token);
        }
        return NumericColumn.create(name, colValues);
    }
    
    /**
     * Creates a new categorical columns from a list of token containing different levels.
     * 
     * @param name
     *            the name of the column
     * @param tokens
     *            the tokens to convert
     * @return a new numeric column
     */
    private static final CategoricalColumn createCategoricalColumn(String name, ArrayList<String> tokens)
    {
        // first compute levels
        ArrayList<String> levelTokens = new ArrayList<String>();
        for (String token : tokens)
        {
            if (!(levelTokens.contains(token)))
            {
                levelTokens.add(token);
            }
        }
        
        // then compute level index for each tokn
        int nRows = tokens.size();
        int[] indices = new int[nRows];
        for (int r = 0; r < nRows; r++)
        {
            String token = tokens.get(r);
            indices[r] = levelTokens.indexOf(token);
        }
        return CategoricalColumn.create(name, indices, levelTokens.toArray(new String[] {}));
    }
}
