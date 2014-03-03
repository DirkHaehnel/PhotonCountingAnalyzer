/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package janalyzer;

/**
 *
 * @author cpieper
 */
public class JA_CurveSet extends Object {
    private int[] set;

    public JA_CurveSet(int firstC, int lastC) {
        set = new int[2];
        set[0] = firstC;
        set[1] = lastC;
    }

    public String toString() {
        return "Curve " + set[0] + "-" + set[1];
    }

    public int[] getSet() {
        return set;
    }

    public String getSetStr() {
        return set[0] + ":" + set[1];
    }
}