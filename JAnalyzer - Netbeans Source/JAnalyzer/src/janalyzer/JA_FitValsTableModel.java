/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package janalyzer;

import javax.swing.table.*;
import java.text.*;
import javax.swing.text.*;

/**
 *
 * @author cpieper
 */
public class JA_FitValsTableModel extends AbstractTableModel {

    public static int maxrows = 10;
    private int rows;
    private String[] cols = {"Name", "Start", "Bounds", "Min", "Max"};
    private String[] Names;
    private Double[] Vals, LowBound, HighBound;
    private Boolean[] Bounds;
    private NumberFormatter nf;

    JA_FitValsTableModel(NumberFormatter nf) {
        rows = 2;
        Names = new String[maxrows];
        Names[0] = "D_1";
        Names[1] = "T_1";
        Vals = new Double[maxrows];
        Vals[0] = new Double(1E-6);
        Vals[1] = new Double(10);
        LowBound = new Double[maxrows];
        LowBound[0] = new Double(0);
        LowBound[1] = new Double(0);
        HighBound = new Double[maxrows];
        HighBound[0] = Double.POSITIVE_INFINITY;
        HighBound[1] = Double.POSITIVE_INFINITY;
        Bounds = new Boolean[maxrows];
        Bounds[0] = false;
        Bounds[1] = false;

        this.nf = nf;
    }

    public void deleteRow(int row) {
        if ((row >= 0) && (row < rows)) {
            for (int j = row; j < rows-1; j++) {
               Names[j] = Names[j+1];
               Vals[j] = Vals[j+1];
               LowBound[j] = LowBound[j+1];
               HighBound[j] = HighBound[j+1];
               Bounds[j] = Bounds[j+1];
            }
            rows--;
            fireTableDataChanged();
        }
    }

    public void insertRow(int row, String Name, Double Value) {
        if ((rows < maxrows) && (row >= 0) && (row <= rows)) {
            for (int j = rows; j > row; j--) {
               Names[j] = Names[j-1];
               Vals[j] = Vals[j-1];
               LowBound[j] = LowBound[j-1];
               HighBound[j] = HighBound[j-1];
               Bounds[j] = Bounds[j-1];
            }
            Names[row] = Name;
            Vals[row] = Value;
            LowBound[row] = new Double(0);
            HighBound[row] = Double.POSITIVE_INFINITY;
            Bounds[row] = new Boolean(false);
            rows++;
            fireTableDataChanged();
        }
    }

    public void addRow(String Name, Double Value) {
        if (rows < maxrows) {
            Names[rows] = Name;
            Vals[rows] = Value;
            LowBound[rows] = new Double(0);
            HighBound[rows] = Double.POSITIVE_INFINITY;
            Bounds[rows] = new Boolean(false);
            rows++;
            fireTableDataChanged();
        }
    }

    public int getRowCount() {
        return rows;
    }

    public int getColumnCount() {
        return 5;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if ((columnIndex >= 0) && (columnIndex < 5)) {
            if ((rowIndex >= 0) && (rowIndex < rows)) {
                switch (columnIndex) {
                    case 0:
                        return Names[rowIndex];
                    case 1:
                        return Vals[rowIndex];
                    case 2:
                        return Bounds[rowIndex];
                    case 3:
                        return LowBound[rowIndex];
                    case 4:
                        return HighBound[rowIndex];
                }
            }
        }
        return null;
    }

    public Object[] getValueArray(int idx) {
        switch (idx) {
            case 0:
                return Vals;
            case 1:
                return Bounds;
            case 2:
                return LowBound;
            case 3:
                return HighBound;
        }
        return null;
    }

    /**
     *  Returns a default name for the column using spreadsheet conventions:
     *  A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
     *  returns an empty string.
     *
     * @param column  the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    public String getColumnName(int column) {
        if ((column >= 0) && (column < 5)) {
            return cols[column];
	}
        return null;
    }

    /**
     *  Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     *  @param columnIndex  the column being queried
     *  @return the Object.class
     */
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return Double.class;
            case 2:
                return Boolean.class;
            case 3:
                return Double.class;
            case 4:
                return Double.class;
        }
        return Object.class;
    }

    /**
     *  Returns false.  This is the default implementation for all cells.
     *
     *  @param  rowIndex  the row being queried
     *  @param  columnIndex the column being queried
     *  @return false
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return false;
            case 1:
            case 2:
                return true;
            case 3:
            case 4:
                if ((rowIndex >= 0) && (rowIndex < rows)) {
                    return Bounds[rowIndex].booleanValue();
                }
        }
	return false;
    }

    /**
     *  This empty implementation is provided so users don't have to implement
     *  this method if their data model is not editable.
     *
     *  @param  aValue   value to assign to cell
     *  @param  rowIndex   row of cell
     *  @param  columnIndex  column of cell
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if ((columnIndex >= 0) && (columnIndex < 5)) {
            if ((rowIndex >= 0) && (rowIndex < rows)) {
                try {
                    switch (columnIndex) {
                        case 0:
                            Names[rowIndex] = (String)aValue;
                            break;
                        case 1:
                            Vals[rowIndex] = (Double)nf.stringToValue(aValue.toString());
                            break;
                        case 2:
                            Bounds[rowIndex] = (Boolean)aValue;
                            break;
                        case 3:
                            LowBound[rowIndex] = (Double)nf.stringToValue(aValue.toString());
                            break;
                        case 4:
                            HighBound[rowIndex] = (Double)nf.stringToValue(aValue.toString());
                            break;
                    }
                }
                catch (ParseException e) {
                }
            }
        }
    }

}
