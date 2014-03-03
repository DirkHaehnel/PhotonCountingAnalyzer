/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package janalyzer;

import java.text.*;
import javax.swing.text.*;

/**
 *
 * @author cpieper
 */
public class JA_NumberFormatter extends NumberFormatter {

    public JA_NumberFormatter(NumberFormat format) {
        super(format);
    }

    /**
     * Invokes <code>parseObject</code> on <code>f</code>, returning
     * its value.
     */
    public Object stringToValue(String text, Format f) throws ParseException {
        if (f == null) {
            return text;
        }
        if (f instanceof DecimalFormat)
            return ((DecimalFormat)f).parse(text);
        return super.stringToValue(text);

    }

    public Object stringToValue(String text) throws ParseException {
        if (text.toLowerCase().equals("inf") || text.toLowerCase().equals("infinity"))
            return Double.POSITIVE_INFINITY;
        return super.stringToValue(text);
    }
}
