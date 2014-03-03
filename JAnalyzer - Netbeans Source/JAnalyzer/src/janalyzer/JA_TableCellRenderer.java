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
public class JA_TableCellRenderer extends DefaultTableCellRenderer {

    private NumberFormatter nf;

    public JA_TableCellRenderer(NumberFormatter nf) {
	super();
        this.nf = nf;
    }

    /**
     * Sets the <code>String</code> object for the cell being rendered to
     * <code>value</code>.
     *
     * @param value  the string value for this cell; if value is
     *		<code>null</code> it sets the text value to an empty string
     * @see JLabel#setText
     *
     */
    protected void setValue(Object value) {
        if (value instanceof Double) {
            try {
                Double val = (Double) value;
                if (Math.abs(val.doubleValue()) < 1000) {
                    setText(val.toString());
                }
                else
                    setText(nf.valueToString(value));
            }
            catch (ParseException e) {
            }
        }
        else
            setText((value == null) ? "" : value.toString());
    }

}
