/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package janalyzer;

import java.text.*;
import javax.swing.*;

/**
 *
 * @author cpieper
 */
public class JA_NumberFormat extends DecimalFormat {

    //private DecimalFormat df;

    public JA_NumberFormat() {
        super("0.###E0");
        //df = new DecimalFormat();
    }

    public Number parse(String text, ParsePosition pos) {
        int idx = pos.getIndex();
        if (text.toLowerCase().regionMatches(idx, "inf", 0, 3) || text.toLowerCase().regionMatches(idx, "infinity", 0, 8))
            return Double.POSITIVE_INFINITY;
        return super.parse(text.replace('e', 'E'), pos);
    }

    public Number parse(String source) throws ParseException {
        ParsePosition parsePosition = new ParsePosition(0);
        Number result = parse(source, parsePosition);
        if (parsePosition.getErrorIndex() == 0) {
            throw new ParseException("Unparseable number: \"" + source + "\"",
                                     parsePosition.getErrorIndex());
        }
        return result;
    }
}
