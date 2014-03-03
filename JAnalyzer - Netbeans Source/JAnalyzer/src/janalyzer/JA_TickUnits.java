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
public class JA_TickUnits {

    JA_TickUnits() {

    }

    /**
     * Creates the standard tick units.
     * <P>
     * If you don't like these defaults, create your own instance of TickUnits
     * and then pass it to the setStandardTickUnits() method in the
     * NumberAxis class.
     *
     * @return The standard tick units.
     *
     * @see #setStandardTickUnits(TickUnitSource)
     * @see #createIntegerTickUnits()
     */
    public static TickUnitSource createUnits() {

        TickUnits units = new TickUnits();

        DecimalFormat df;
        df = new DecimalFormat("0.0#E0");
        df.setMaximumIntegerDigits(4);
        df.setMaximumFractionDigits(3);
        df.setGroupingUsed(false);

        // we can add the units in any order, the TickUnits collection will
        // sort them...
        units.add(new JA_NumberTickUnit(0.0000001, df, 2));
        units.add(new JA_NumberTickUnit(0.000001, df, 2));
        units.add(new JA_NumberTickUnit(0.00001, df, 2));
        units.add(new JA_NumberTickUnit(0.0001, df, 2));
        units.add(new JA_NumberTickUnit(0.001, df, 2));
        units.add(new JA_NumberTickUnit(0.01, df, 2));
        units.add(new JA_NumberTickUnit(0.1, df, 2));
        units.add(new JA_NumberTickUnit(1, df, 2));
        units.add(new JA_NumberTickUnit(10, df, 2));
        units.add(new JA_NumberTickUnit(100, df, 2));
        units.add(new JA_NumberTickUnit(1000, df, 2));
        units.add(new JA_NumberTickUnit(10000, df, 2));
        units.add(new JA_NumberTickUnit(100000, df, 2));
        units.add(new JA_NumberTickUnit(1000000, df, 2));
        units.add(new JA_NumberTickUnit(10000000, df, 2));
        units.add(new JA_NumberTickUnit(100000000, df, 2));
        units.add(new JA_NumberTickUnit(1000000000, df, 2));
        units.add(new JA_NumberTickUnit(10000000000.0, df, 2));
        units.add(new JA_NumberTickUnit(100000000000.0, df, 2));

        units.add(new JA_NumberTickUnit(0.00000025, df, 5));
        units.add(new JA_NumberTickUnit(0.0000025, df, 5));
        units.add(new JA_NumberTickUnit(0.000025, df, 5));
        units.add(new JA_NumberTickUnit(0.00025, df, 5));
        units.add(new JA_NumberTickUnit(0.0025, df, 5));
        units.add(new JA_NumberTickUnit(0.025, df, 5));
        units.add(new JA_NumberTickUnit(0.25, df, 5));
        units.add(new JA_NumberTickUnit(2.5, df, 5));
        units.add(new JA_NumberTickUnit(25, df, 5));
        units.add(new JA_NumberTickUnit(250, df, 5));
        units.add(new JA_NumberTickUnit(2500, df, 5));
        units.add(new JA_NumberTickUnit(25000, df, 5));
        units.add(new JA_NumberTickUnit(250000, df, 5));
        units.add(new JA_NumberTickUnit(2500000, df, 5));
        units.add(new JA_NumberTickUnit(25000000, df, 5));
        units.add(new JA_NumberTickUnit(250000000, df, 5));
        units.add(new JA_NumberTickUnit(2500000000.0, df, 5));
        units.add(new JA_NumberTickUnit(25000000000.0, df, 5));
        units.add(new JA_NumberTickUnit(250000000000.0, df, 5));

        units.add(new JA_NumberTickUnit(0.0000005, df, 5));
        units.add(new JA_NumberTickUnit(0.000005, df, 5));
        units.add(new JA_NumberTickUnit(0.00005, df, 5));
        units.add(new JA_NumberTickUnit(0.0005, df, 5));
        units.add(new JA_NumberTickUnit(0.005, df, 5));
        units.add(new JA_NumberTickUnit(0.05, df, 5));
        units.add(new JA_NumberTickUnit(0.5, df, 5));
        units.add(new JA_NumberTickUnit(5L, df, 5));
        units.add(new JA_NumberTickUnit(50L, df, 5));
        units.add(new JA_NumberTickUnit(500L, df, 5));
        units.add(new JA_NumberTickUnit(5000L, df, 5));
        units.add(new JA_NumberTickUnit(50000L, df, 5));
        units.add(new JA_NumberTickUnit(500000L, df, 5));
        units.add(new JA_NumberTickUnit(5000000L, df, 5));
        units.add(new JA_NumberTickUnit(50000000L, df, 5));
        units.add(new JA_NumberTickUnit(500000000L, df, 5));
        units.add(new JA_NumberTickUnit(5000000000L, df, 5));
        units.add(new JA_NumberTickUnit(50000000000L, df, 5));
        units.add(new JA_NumberTickUnit(500000000000L, df, 5));

        return units;

    }

}
