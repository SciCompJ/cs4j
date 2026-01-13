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
import java.util.List;
import java.util.stream.Stream;

import net.sci.table.CategoricalColumn;
import net.sci.table.Column;
import net.sci.table.NumericColumn;
import net.sci.table.NumericTable;
import net.sci.table.Table;
import net.sci.table.impl.ColumnsTable;

/**
 * Reads a table from a delimited file. Many options can be set through the
 * Builder class, such as the type of delimiter, the number of lines to skip, or
 * the presence/absence of row names.
 * 
 * {@snippet lang = "java" :
 * DelimitedTableReader reader = DelimitedTableReader.builder()
 *      .delimiters(",")
 *      .readHeader(true)
 *      .readRowNames(false)
 *      .build();
 * Table table = reader.readTable(fileOrInputStream);
 * }
 * 
 * @author dlegland
 *
 */
public class DelimitedTableReader implements TableReader
{
    // =============================================================
    // Static methods

    /**
     * Returns a new Builder class for creating new instances of
     * DelimitedTableReader.
     * 
     * @return a new Builder class.
     */
    public static final Builder builder()
    {
        return new Builder();
    }
    
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
    
    /**
     * If true, simply avoid special processing for quotes. Default is false.
     */
    private boolean ignoreQuotes = false;

    
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

    @Deprecated
    public String getDelimiters()
    {
        return delimiters;
    }

    /**
     * @deprecated replaced by Builder class
     * @param delimiters
     */
    @Deprecated
    public void setDelimiters(String delimiters)
    {
        this.delimiters = delimiters;
    }

    @Deprecated
    public boolean isReadHeader()
    {
        return readHeader;
    }

    /**
     * @deprecated replaced by Builder class
     * @param readHeader
     */
    @Deprecated
    public void setReadHeader(boolean readHeader)
    {
        this.readHeader = readHeader;
    }

    @Deprecated
    public int getSkipLines()
    {
        return skipLines;
    }

    /**
     * @deprecated replaced by Builder class
     * @param skipLines
     */
    @Deprecated
    public void setSkipLines(int skipLines)
    {
        this.skipLines = skipLines;
    }

    @Deprecated
    public boolean isReadRowNames()
    {
        return readRowNames;
    }

    /**
     * @deprecated replaced by Builder class
     * @param readRowNames
     */
    @Deprecated
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
        // read the table from a stream obtained from the file
        FileInputStream stream = new FileInputStream(file);
        Table table = readTable(stream);
        
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
    public Table readTable(InputStream stream) throws IOException
    {
        // meta data for table
        int nCols;
        ArrayList<ArrayList<String>> columnTokens;
        String[] colNames;
        ArrayList<String> rowNames = new ArrayList<String>();
        
        // Create text reader from the stream
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        LineNumberReader reader = new LineNumberReader(br);
        
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
            lineTokens = parseTokens(line, delimiterRegexp);
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
        
        Table table;
        if (Stream.of(columns).allMatch(col -> col instanceof NumericColumn))
        {
            NumericColumn[] numCols = Stream.of(columns)
                    .map(col -> (NumericColumn) col)
                    .toArray(NumericColumn[]::new);
            table = NumericTable.create(numCols);
        }
        else
        {
            table = new ColumnsTable(columns);
        }
        
        // populates meta-data
        if (readRowNames)
        {
            table.setRowNames(rowNames.toArray(new String[0]));
        }
        
        return table;
    }
    
    private String[] parseTokens(String line, String delimiterRegexp)
    {
        return this.ignoreQuotes ? line.split(delimiterRegexp) : splitQuotedTokens(line, delimiterRegexp);
    }
    
    /**
     * Splits the input string into tokens, using the specified delimiters, and
     * taking into account tokens delimited with double quotes.
     * 
     * @param string
     *            the String to parse
     * @param delimiters
     *            the token delimiters
     * @return the list of tokens within the String
     */
    static final String[] splitQuotedTokens(String string, String delimiters)
    {
        ArrayList<String> tokens = new ArrayList<String>();
        
        String delimiterRegexp = "[" + delimiters + "]+";
        
        // iterate over token starts
        String remainingString = string;
        while (!remainingString.isEmpty())
        {
            if (remainingString.startsWith("\""))
            {
                // Process token enclosed with quotes
                String[] splitted = splitToNextNonEscapedQuote(remainingString);
                tokens.add(splitted[0]);
                
                if (splitted.length == 1 || splitted[1].isEmpty())
                {
                    break;
                }
                // otherwise, use the second part as new remaining string
                String[] remainingTokens = splitted[1].split(delimiterRegexp, 2);
                remainingString = remainingTokens.length > 1 ? remainingTokens[1] : "";
            }
            else
            {
                // process "regular" token
                // iterate over next delimiter, or end of string
                String[] splitted = remainingString.split(delimiterRegexp, 2);
                tokens.add(splitted[0]);
                
                // otherwise, use the second part as new remaining string 
                remainingString = splitted.length > 1 ? splitted[1] : "";
            }
        }
        
        return tokens.toArray(String[]::new);
    }
    
    private static final String[] splitToNextNonEscapedQuote(String string)
    {
        int startIndex = 1;
        int index;
        while (true)
        {
            index = string.indexOf('"', startIndex);
            if (index < 0) throw new RuntimeException("Could not find closing quote");
            
            // check escaped quotes
            if (index < string.length() - 1)
            {
                if (string.charAt(index + 1) == '"')
                {
                    startIndex = index + 2;
                    continue;
                }
            }
            break;
        }
        String sub1 = string.substring(1, index);
        String sub2 = string.substring(index+1);
        return new String[] {sub1, sub2};
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
        List<String> levelTokens = tokens.stream().distinct().toList(); 
        
        // then compute level index for each token
        int nRows = tokens.size();
        int[] indices = new int[nRows];
        for (int r = 0; r < nRows; r++)
        {
            String token = tokens.get(r);
            indices[r] = levelTokens.indexOf(token);
        }
        return CategoricalColumn.create(name, indices, levelTokens.toArray(new String[0]));
    }
    
    /**
     * Builder class for the DelimitedTableReader class.
     */
    public static class Builder
    {
        private String delimiters = " \t";
        private boolean readHeader = true;
        private int skipLines = 0;
        private boolean readRowNames = true;
        private boolean ignoreQuotes = false;
        
        private Builder()
        {
        }
        
        /**
         * Sets up the delimiter to use between tokens within table.
         * 
         * @param delimiters
         *            the String containing the delimiter. May contain several
         *            delimiter characters.
         * @return a reference to the Builder class.
         */
        public Builder delimiters(String delimiters)
        {
            this.delimiters = delimiters;
            return this;
        }
        
        /**
         * Chooses whether the header line must be read. Default is {@code true}.
         * 
         * @param readHeader
         *            the boolean flag for reading the header
         * @return a reference to the Builder class.
         */
        public Builder readHeader(boolean readHeader)
        {
            this.readHeader = readHeader;
            return this;
        }

        /**
         * Sets up the number of lines to skip before starting to read data.
         * Default is zero
         * 
         * @param skipLines
         *            the number of lines to skip
         * @return a reference to the Builder class.
         */
        public Builder skipLines(int skipLines)
        {
            this.skipLines = skipLines;
            return this;
        }

        /**
         * Chooses whether the file contains row names. If yes, row names are
         * used to populate the row axis of the result table. Default is true.
         * 
         * @param readRowNames
         *            the boolean flag for reading row names
         * @return a reference to the Builder class.
         */
        public Builder readRowNames(boolean readRowNames)
        {
            this.readRowNames = readRowNames;
            return this;
        }
        
        /**
         * Chooses whether processing of quotes should be avoided. If true,
         * simply avoid special processing for quotes. Default is false.
         * 
         * @param ignoreQuotes
         *            the boolean flag for ignoring the quotes
         * @return a reference to the Builder class.
         */
        public Builder ignoreQuotes(boolean ignoreQuotes)
        {
            this.ignoreQuotes = ignoreQuotes;
            return this;
        }
        
        /**
         * Creates a new instance of DelimitedTableReader based on current
         * settings.
         * 
         * @return a correctly initialized DelimitedTableReader
         */
        public DelimitedTableReader build()
        {
            DelimitedTableReader reader = new DelimitedTableReader(delimiters);
            reader.readHeader = this.readHeader;
            reader.skipLines = this.skipLines;
            reader.readRowNames = this.readRowNames;
            reader.ignoreQuotes = this.ignoreQuotes;
            return reader;
        }
    }
}
