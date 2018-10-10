package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author Chelsea Chen
 */
class Table {
    /**
     * A new Table whose columns are given by COLUMNTITLES, which may
     * not contain duplicate names.
     */
    Table(String[] columnTitles) {
        if (columnTitles.length == 0) {
            throw error("table must have at least one column");
        }
        _size = 0;
        _rowSize = columnTitles.length;

        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                            columnTitles[i]);
                }
            }
        }

        _titles = columnTitles;

        _columns = new ValueList[_rowSize];

        for (int i = 0; i < _rowSize; i++) {
            _columns[i] = new ValueList();
        }
    }

    /**
     * A new Table whose columns are give by COLUMNTITLES.
     */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /**
     * Return the number of columns in this table.
     */
    public int columns() {
        return _rowSize;
    }

    /**
     * Return the title of the Kth column.  Requires 0 <= K < columns().
     */
    public String getTitle(int k) throws DBException {
        if (0 > k || k >= columns()) {
            throw new DBException("getTitle: column index out of bounds");
        }
        return this._titles[k];
    }

    /**
     * Return the number of the column whose title is TITLE, or -1 if
     * there isn't one.
     */
    public int findColumn(String title) {
        int l = this._titles.length;
        for (int i = 0; i < l; i++) {
            if (this._titles[i].equals(title)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Return the number of rows in this table.
     */
    public int size() {
        return _size;
    }

    /**
     * Return the value of column number COL (0 <= COL < columns())
     * of record number ROW (0 <= ROW < size()).
     */
    public String get(int row, int col) {
        try {
            return _columns[col].get(row);
        } catch (IndexOutOfBoundsException excp) {
            throw error("invalid row or column");
        }
    }

    /**
     * Add a new row whose column values are VALUES to me if no equal
     * row already exists.  Return true if anything was added,
     * false otherwise. Collaborator: Erica
     */
    public boolean add(String[] values) {

        int l = _columns.length;

        if (_size != 0) {
            for (int r = 0; r < _size; r++) {
                int numd = 0;
                for (int c = 0; c < _rowSize; c++) {
                    if (!_columns[c].get(r).equals(values[c])) {
                        numd++;
                    }
                }
                if (numd == 0) {
                    return false;
                }
            }
        } else {
            _index.add(size());
            for (int i = 0; i < l; i++) {
                _columns[i] = new ValueList();
            }
        }
        for (int j = 0; j < l; j++) {
            _columns[j].add(values[j]);
        }
        for (int x = 0; x < size(); x++) {
            if (compareRows(size(), _index.get(x)) < 0) {
                _index.add(x, size());
                break;
            } else if (x == size() - 1) {
                _index.add(size());
                break;
            }
        }
        _size++;
        return true;
    }

    /**
     * Add a new row whose column values are extracted by COLUMNS from
     * the rows indexed by ROWS, if no equal row already exists.
     * Return true if anything was added, false otherwise. See
     * Column.getFrom(Integer...) for a description of how Columns
     * extract values.
     */
    public boolean add(List<Column> columns, Integer... rows) {

        String[] entry = new String[columns.size()];

        for (int col = 0; col < columns.size(); col++) {
            entry[col] = columns.get(col).getFrom(rows);
        }
        return add(entry);

    }

    /**
     * Read the contents of the file NAME.db, and return as a Table.
     * Format errors in the .db file cause a DBException.
     */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");

            header = input.readLine();
            table = new Table(columnNames);
            while (header != null) {
                String[] newrow = header.split(",");
                table.add(newrow);
                header = input.readLine();
            }

        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /**
     * Write the contents of TABLE into the file NAME.db. Any I/O errors
     * cause a DBException.
     */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = "";
            output = new PrintStream(name + ".db");

            for (int i = 0; i < columns(); i++) {
                if (i != columns() - 1) {
                    output.print(getTitle(i) + ",");
                } else {
                    output.print(getTitle(i));
                    output.println();
                }
            }
            int r = 0;
            while (r < _size) {
                for (int j = 0; j < columns(); j++) {
                    if (j != columns() - 1) {
                        output.print(_columns[j].get(r) + ",");
                    } else {
                        output.print(_columns[_columns.length - 1].get(r));
                        output.println();
                        r++;
                    }
                }
            }
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /**
     * Print my contents on the standard output, separated by spaces
     * and indented by two spaces.
     */
    void print() {
        for (int i = 0; i < _index.size(); i++) {
            System.out.print(" ");
            for (int j = 0; j < columns(); j++) {
                System.out.print(_columns[j].get(_index.get(i)) + " ");
            }
            System.out.println();
        }
    }

    /**
     * Return a new Table whose columns are COLUMNNAMES, selected from
     * rows of this table that satisfy CONDITIONS.
     */
    Table select(List<String> columnNames, List<Condition> conditions) {
        Table result = new Table(columnNames);
        ArrayList columns = new ArrayList();

        for (String colN : columnNames) {
            Column newCol = new Column(colN, this);
            columns.add(newCol);
        }

        for (int i = 0; i < size(); i++) {
            if (Condition.test(conditions, i)) {
                result.add(columns, i);
            }
        }
        return result;
    }

    /**
     * Return a new Table whose columns are COLUMNNAMES, selected
     * from pairs of rows from this table and from TABLE2 that match
     * on all columns with identical names and satisfy CONDITIONS.
     */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {

        Table result = new Table(columnNames);
        List<Column> listcols = new ArrayList<Column>();

        for (String colN : columnNames) {
            Column columnOfColName = new Column(colN, this, table2);
            listcols.add(columnOfColName);
        }

        List<String> commonColName = new ArrayList<String>();

        for (int i = 0; i < this._titles.length; i += 1) {
            for (int j = 0; j < table2._titles.length; j += 1) {
                String name1 = this._titles[i];
                String name2 = table2._titles[j];
                if (name1.equals(name2)) {
                    commonColName.add(name1);
                }
            }
        }
        List<Column> comm1 = new ArrayList<Column>();
        List<Column> comm2 = new ArrayList<Column>();

        for (String one : commonColName) {
            Column col1 = new Column(one, this);
            Column col2 = new Column(one, table2);
            comm1.add(col1);
            comm2.add(col2);
        }

        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < table2.size(); j++) {
                if (Condition.test(conditions, i, j)
                        && equijoin(comm1, comm2, i, j)) {
                    result.add(listcols, i, j);
                }
            }
        }
        return result;
    }

    /** Return <0, 0, or >0 depending on whether the row formed from
     *  the elements _columns[0].get(K0), _columns[1].get(K0), ...
     *  is less than, equal to, or greater than that formed from elememts
     *  _columns[0].get(K1), _columns[1].get(K1), ....  This method ignores
     *  the _index. */
    private int compareRows(int k0, int k1) {
        int l = _columns.length;

        for (int i = 0; i < _columns.length; i += 1) {
            int j = _columns[i].get(k0).compareTo(_columns[i].get(k1));
            if (j != 0) {
                return j;
            }
        }
        return 0;
    }

    /** Return true if the columns COMMON1 from ROW1 and COMMON2 from
     *  ROW2 all have identical values.  Assumes that COMMON1 and
     *  COMMON2 have the same number of elements and the same names,
     *  that the columns in COMMON1 apply to this table, those in
     *  COMMON2 to another, and that ROW1 and ROW2 are indices, respectively,
     *  into those tables. */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    int row1, int row2) {

        for (int i = 0; i < common1.size(); i++) {
            Column col1 = common1.get(i);
            String col1S = col1.getFrom(row1);

            Column col2 = common2.get(i);
            String col2S = col2.getFrom(row2);

            if (!col1S.equals(col2S)) {
                return false;
            }
        }
        return true;
    }

    /** A class that is essentially ArrayList<String>.  For technical reasons,
     *  we need to encapsulate ArrayList<String> like this because the
     *  underlying design of Java does not properly distinguish between
     *  different kinds of ArrayList at runtime (e.g., if you have a
     *  variable of type Object that was created from an ArrayList, there is
     *  no way to determine in general whether it is an ArrayList<String>,
     *  ArrayList<Integer>, or ArrayList<Object>).  This leads to annoying
     *  compiler warnings.  The trick of defining a new type avoids this
     *  issue. */
    private static class ValueList extends ArrayList<String> {
    }

    /** My column titles. */
    private final String[] _titles;
    /** My columns. Row i consists of _columns[k].get(i) for all k. */
    private final ValueList[] _columns;

    /** Rows in the database are supposed to be sorted. To do so, we
     *  have a list whose kth element is the index in each column
     *  of the value of that column for the kth row in lexicographic order.
     *  That is, the first row (smallest in lexicographic order)
     *  is at position _index.get(0) in _columns[0], _columns[1], ...
     *  and the kth row in lexicographic order in at position _index.get(k).
     *  When a new row is inserted, insert its index at the appropriate
     *  place in this list.
     *  (Alternatively, we could simply keep each column in the proper order
     *  so that we would not need _index.  But that would mean that inserting
     *  a new row would require rearranging _rowSize lists (each list in
     *  _columns) rather than just one. */
    private final ArrayList<Integer> _index = new ArrayList<>();

    /** My number of rows (redundant, but convenient). */
    private int _size;
    /** My number of columns (redundant, but convenient). */
    private final int _rowSize;

    /** @return
    * Allows you to get the private variable _titles.*/
    public String[] getTitles() {
        return _titles;
    }
}
