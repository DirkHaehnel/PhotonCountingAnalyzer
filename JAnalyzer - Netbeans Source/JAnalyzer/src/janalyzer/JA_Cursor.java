/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package janalyzer;

/**
 *
 * @author cpieper
 */
public class JA_Cursor extends Object {

    private double CursorPos;

    public JA_Cursor(double CursorPos) {
        this.CursorPos = CursorPos;
    }

    /**
     * @return the CursorPos
     */
    public double getCursorPos() {
        return CursorPos;
    }

    /**
     * @param CursorPos the CursorPos to set
     */
    public void setCursorPos(double CursorPos) {
        this.CursorPos = CursorPos;
    }

}
