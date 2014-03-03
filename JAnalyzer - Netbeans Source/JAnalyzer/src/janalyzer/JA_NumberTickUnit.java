/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package janalyzer;

import java.text.*;
import org.jfree.chart.axis.*;
/**
 *
 * @author cpieper
 */
public class JA_NumberTickUnit extends NumberTickUnit {

    /** A formatter for the tick unit. */
    private NumberFormat formatter;

    public JA_NumberTickUnit(double size, NumberFormat formatter, int minorTickCount) {
        super(size, formatter, minorTickCount);
        this.formatter = formatter;
    }

    public String valueToString(double value) {
        return formatter.format(value).toLowerCase();
    }

}
