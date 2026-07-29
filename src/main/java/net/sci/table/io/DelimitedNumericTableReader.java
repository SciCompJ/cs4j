/**
 * 
 */
package net.sci.table.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;

import net.sci.algo.AlgoStub;
import net.sci.table.NumericTable;

/**
 * Reads a numeric table from a delimited file. Many options can be set, such as
 * the type of delimiter, the number of lines to skip, or the presence/absence
 * of row names.
 * 
 * {@snippet :
 * DelimitedNumericTableReader reader = new DelimitedNumericTableReader()
 *         .setDelimiters(",")
 *         .setReadHeader(true)
 *         .setReadRowNames(false);
 * NumericTable table = reader.readTable(fileOrInputStream);
 * }
 * 
 * @see DelimitedTableReader
 * 
 * @author dlegland
 */
public class DelimitedNumericTableReader extends AlgoStub implements TableReader
{
    // =============================================================
    // Class variables

    /**
     * The delimiters between the tokens within the file. Default is " \t",
     * corresponding to either space or tabulation delimiter.
     */
    private String delimiters = " \t";

    /**
     * Specifies if column header are present in the file. Default is true.
     */
    private boolean readHeader = true;

    /**
     * The number of lines to skip before starting reading data. Default is 0.
     */
    private int skipLines = 0;

    /**
     * Specifies if row names are present in the file. Default is true.
     */
    private boolean readRowNames = true;

    
    // =============================================================
    // Constructors

    /**
     * Creates a new instance of DelimitedTableReader.
     */
    public DelimitedNumericTableReader()
    {
    }

    /**
     * Creates a new instance of DelimitedTableReader, specifying the
     * delimiters.
     * 
     * @param delimiters
     *            the delimiters
     */
    public DelimitedNumericTableReader(String delimiters)
    {
        this.delimiters = delimiters;
    }

    
    // =============================================================
    // Accessors and mutators

    public String getDelimiters()
    {
        return delimiters;
    }

    /**
     * Sets up the delimiter to use between tokens within table.
     * 
     * @param delimiters
     *            the String containing the delimiter. May contain several
     *            delimiter characters.
     */
    public DelimitedNumericTableReader setDelimiters(String delimiters)
    {
        this.delimiters = delimiters;
        return this;
    }

    public boolean isReadHeader()
    {
        return readHeader;
    }

    /**
     * Chooses whether the header line must be read. Default is {@code true}.
     * 
     * @param readHeader
     *            the boolean flag for reading the header
     */
    public DelimitedNumericTableReader setReadHeader(boolean readHeader)
    {
        this.readHeader = readHeader;
        return this;
    }

    public int getSkipLines()
    {
        return skipLines;
    }

    /**
     * Sets up the number of lines to skip before starting to read data.
     * Default is zero
     * 
     * @param skipLines
     *            the number of lines to skip
     */
    public DelimitedNumericTableReader setSkipLines(int skipLines)
    {
        this.skipLines = skipLines;
        return this;
    }

    public boolean isReadRowNames()
    {
        return readRowNames;
    }

    /**
     * Chooses whether the file contains row names. If yes, row names are
     * used to populate the row axis of the result table. Default is true.
     * 
     * @param readRowNames
     *            the boolean flag for reading row names
     */
    public DelimitedNumericTableReader setReadRowNames(boolean readRowNames)
    {
        this.readRowNames = readRowNames;
        return this;
    }
    
    
    // =============================================================
    // implementation of the TableReader interface

    /*
     * (non-Javadoc)
     * 
     * @see net.sci.table.io.TableReader#readTable()
     */
    @Override
    public NumericTable readTable(File file) throws IOException
    {
        // read the table from a stream obtained from the file
        FileInputStream stream = new FileInputStream(file);
        NumericTable table = readTable(stream);
        
        // also set the name of the table to the name of the file
        table.setName(file.getName());
        return table;
    }
    
    /**
     * Reads a data table from the specified input stream.
     * 
     * @param stream
     *            the stream to read data from
     * @return a new Table
     * @throws IOException
     *             if a problem occurred during reading
     */
    public NumericTable readTable(InputStream stream) throws IOException
    {
        // meta data for table
        int nCols;
        ArrayList<ArrayList<String>> columnTokens;
        String[] colNames;
        ArrayList<String> rowNames = new ArrayList<String>();
        
        // Create text reader from the stream
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        LineNumberReader reader = new LineNumberReader(br);
        
        this.fireStatusChanged(this, "read header");
        // optionally skip some lines
        for (int r = 0; r < skipLines; r++)
        {
            reader.readLine();
        }
        
        // convert list of delimiters into a regexp string
        String delimiterRegexp = "[" + delimiters + "]+";
        
        // parse header line
        String[] lineTokens = parseTokens(reader.readLine(), delimiterRegexp);
        
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
        ArrayList<ArrayList<Double>>columnValues = new ArrayList<ArrayList<Double>>(nCols);
        for (int c = 0; c < nCols; c++)
        {
            columnTokens.add(new ArrayList<String>());
            columnValues.add(new ArrayList<Double>());
        }
        
        int offset = readRowNames ? 1 : 0;
        if (!readHeader)
        {
            // use first line tokens to initialize first values
            for (int c = 0; c < nCols; c++)
            {
                columnTokens.get(c).add(lineTokens[c + offset]);
                double val = parseDouble(lineTokens[c + offset], reader.getLineNumber(), c);
                columnValues.get(c).add(val);
            }
        }
        
        this.fireStatusChanged(this, "read data");
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
            lineTokens = parseTokens(line, delimiterRegexp);
            if (readRowNames)
            {
                rowNames.add(lineTokens[0]);
            }
            
            // read column values as strings
            for (int c = 0; c < nCols; c++)
            {
                double val = parseDouble(lineTokens[c + offset], reader.getLineNumber(), c);
                columnValues.get(c).add(val);
            }
        }
        
        reader.close();
        
        this.fireStatusChanged(this, "convert columns");
        
        // create new empty numeric table
        int nRows = columnValues.get(0).size();
        NumericTable table = NumericTable.create(nRows, nCols);
        
        // fill table from parsed values
        for (int c = 0; c < nCols; c++)
        {
            this.fireProgressChanged(this, c, nCols);
            
            ArrayList<Double> values = columnValues.get(c);
            for (int r = 0; r < nRows; r++)
            {
                table.setValue(r, c, values.get(r));
            }
        }
        this.fireProgressChanged(this, 0, 1);
        
        // populates meta-data
        table.setColumnNames(colNames);
        if (readRowNames)
        {
            table.setRowNames(rowNames.toArray(new String[0]));
        }
        
        return table;
    }
    
    private String[] parseTokens(String line, String delimiterRegexp)
    {
        return line.strip().split(delimiterRegexp);
    }
    
    private static final Double parseDouble(String token, int lineNumber, int colNumber)
    {
        try
        {
            return Double.parseDouble(token);
        }
        catch(NumberFormatException ex)
        {
            throw new RuntimeException(String.format(
                    "Could not convert numeric value from token \"%s\" (line #%d, token #%d)", token,
                    lineNumber, colNumber + 1));
        }
    }
}
