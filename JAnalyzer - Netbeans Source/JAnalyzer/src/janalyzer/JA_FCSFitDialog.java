/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JA_FitDialog.java
 *
 * Created on 25.02.2010, 14:37:00
 */

package janalyzer;

import java.text.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import java.awt.*;

/**
 *
 * @author cpieper
 */
public class JA_FCSFitDialog extends javax.swing.JDialog {

    public static final int ID_OK = 0;
    public static final int ID_CANCEL = 99;
    
    private int retval;
    private JA_Main parent = null;
    private JA_FitValsTableModel tm_fitvals;
    private int numExps, numDiffs, numExted, numExted2;
    public Hashtable<String, Object> metadata = null;
    private int anzbins = -1;
    private double[] bintimes = null;


    /** Creates new form JA_FitDialog */
    public JA_FCSFitDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        // Set the default locale to custom locale
        Locale.setDefault(new Locale("en", "DE"));

        if (parent instanceof JA_Main)
            this.parent = (JA_Main)parent;
        retval = -1;
        initComponents();
        setLocationRelativeTo(null);

        DecimalFormat df;
        NumberFormatter nf, nf2;

        df = new DecimalFormat();
        df.setMaximumIntegerDigits(4);
        df.setMaximumFractionDigits(3);
        df.setGroupingUsed(false);
        nf = new NumberFormatter(df);
        nf.setValueClass(Double.class);
        nf.setMinimum(new Double(0));
        nf.setMaximum(new Double(999));

        jFormattedTextField_Pinhole.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_FociDist.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_lam1.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_lam2.setFormatterFactory(new DefaultFormatterFactory(nf));

        jFormattedTextField_Pinhole.setValue(new Double(150));
        jFormattedTextField_FociDist.setValue(new Double(452));
        jFormattedTextField_lam1.setValue(new Double(470));
        jFormattedTextField_lam2.setValue(new Double(520));

        nf = new NumberFormatter(df);
        nf.setValueClass(Integer.class);
        nf.setMinimum(new Integer(0));
        nf.setMaximum(new Integer(5));

        jFormattedTextField_NumExp.setFormatterFactory(new DefaultFormatterFactory(nf));

        nf = new NumberFormatter(df);
        nf.setValueClass(Integer.class);
        nf.setMinimum(new Integer(1));
        nf.setMaximum(new Integer(5));

        jFormattedTextField_NumDiff.setFormatterFactory(new DefaultFormatterFactory(nf));

        jFormattedTextField_NumExp.setValue(new Integer(1));
        jFormattedTextField_NumDiff.setValue(new Integer(1));
        numExps = 1;
        numDiffs = 1;
        numExted = 0;

        nf = new NumberFormatter(df);
        nf.setValueClass(Integer.class);
        nf.setMinimum(new Integer(1));
        nf.setMaximum(new Integer(100));

        jFormattedTextField_BootStrap.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_BootStrap.setValue(new Integer(5));

        jFormattedTextField_SumEvery.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_SumEvery.setValue(new Integer(5));

        nf = new NumberFormatter(df);
        nf.setValueClass(Integer.class);
        nf.setMinimum(new Integer(1));
        nf.setMaximum(new Integer(1000));
        jFormattedTextField_RangeBin1.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_RangeBin2.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_RangeBin1.setValue(new Integer(1));
        jFormattedTextField_RangeBin2.setValue(new Integer(1000));

        jRadioButton_Diff3D.setSelected(true);

        jRadioButton_SumAll.setSelected(true);
        jFormattedTextField_BootStrap.setEnabled(false);
        jFormattedTextField_BootStrap.setVisible(false);

        jFormattedTextField_SumEvery.setEnabled(false);
        jFormattedTextField_SumEvery.setVisible(false);
        jLabel_Pool.setText("");

        df = new JA_NumberFormat();
        df.setMaximumFractionDigits(2);
        df.setMaximumIntegerDigits(1);
        df.setGroupingUsed(false);
        nf2 = new JA_NumberFormatter(df);
        nf2.setValueClass(Double.class);
        nf2.setMinimum(new Double(0));
        nf2.setMaximum(Double.POSITIVE_INFINITY);

        jFormattedTextField_RangeT1.setFormatterFactory(new DefaultFormatterFactory(nf2));
        jFormattedTextField_RangeT2.setFormatterFactory(new DefaultFormatterFactory(nf2));
        jFormattedTextField_RangeT1.setValue(new Double(0));
        jFormattedTextField_RangeT2.setValue(Double.POSITIVE_INFINITY);

        // Set up Table
        df = new JA_NumberFormat();
        df.setMaximumFractionDigits(15);
        df.setMaximumIntegerDigits(1);
        df.setGroupingUsed(false);
        nf2 = new JA_NumberFormatter(df);
        nf2.setValueClass(Double.class);
        nf2.setMinimum(Double.NEGATIVE_INFINITY);
        nf2.setMaximum(Double.POSITIVE_INFINITY);

        JFormattedTextField tedf = new JFormattedTextField();
        tedf.setFormatterFactory(new DefaultFormatterFactory(nf2));
        DefaultCellEditor de = new DefaultCellEditor(tedf);

        Object[] cols = {"Name", "Value", "Bounds", "Min", "Max"};
        Object[][] data = {{"T1", new Double(10), true, new Double(10), Double.POSITIVE_INFINITY}};
        tm_fitvals = new JA_FitValsTableModel(nf2);

        jTable_FitCoeff.setDefaultEditor(Double.class, de);
        jTable_FitCoeff.setDefaultRenderer(Double.class, new JA_TableCellRenderer(nf2));
        jTable_FitCoeff.setModel(tm_fitvals);
        jTable_FitCoeff.setDragEnabled(false);

        boolean vis = false;
        jPanel_Range.setVisible(vis);
        getContentPane().remove(jButton_CANCEL);
        getContentPane().remove(jButton_OK);
        getContentPane().add(jButton_CANCEL, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, (vis) ? 450 : 370, -1, -1));
        getContentPane().add(jButton_OK, new org.netbeans.lib.awtextra.AbsoluteConstraints(475, (vis) ? 450 : 370, 73, -1));
        Rectangle br = this.getBounds();
        br.height = (vis) ? 520 : 440;
        this.setBounds(br);
    }

    public void updateTable() {
        if (tm_fitvals != null) {
            int oldExps = numExps;
            int oldDiffs = numDiffs;
            int oldExted = numExted;
            int oldExted2 = numExted2;
            int allExted = numExted + numExted2;
            numExps = ((Integer)jFormattedTextField_NumExp.getValue()).intValue();
            numDiffs = ((Integer)jFormattedTextField_NumDiff.getValue()).intValue();
            numExted = (jCheckBox_W0A0.isSelected() && jRadioButton_Diff3D.isSelected()) ? 2 : 0;
            numExted2 = (jCheckBox_Flow.isSelected() && jRadioButton_Diff3D.isSelected()) ? 1 : 0;
            jCheckBox_W0A0.setVisible(jRadioButton_Diff3D.isSelected());
            jCheckBox_Flow.setVisible(jRadioButton_Diff3D.isSelected());


            if (oldDiffs > numDiffs) {
                // delete exps
                for (int i = oldDiffs; i > numDiffs; i--) {
                    tm_fitvals.deleteRow(allExted+i-1);
                }
            }
            else {
                // add rows
                for (int i = oldDiffs; i < numDiffs; i++) {
                    tm_fitvals.insertRow(allExted+i, "D_" + (i+1), new Double(1E-6));
                }
            }

            if (oldExps > numExps) {
                // delete exps
                for (int i = oldExps; i > numExps; i--) {
                    tm_fitvals.deleteRow(allExted+i+numDiffs-1);
                }
            }
            else {
                // add rows
                for (int i = oldExps; i < numExps; i++) {
                    tm_fitvals.insertRow(allExted+i+numDiffs, "T_" + (i+1), new Double(10));
                }
            }
            if (numExted != oldExted) {
                if (numExted == 2) {
                    tm_fitvals.insertRow(0, "w_" + 0, new Double(450));
                    tm_fitvals.insertRow(1, "a_" + 0, new Double(220));
                }
                else {
                    tm_fitvals.deleteRow(1);
                    tm_fitvals.deleteRow(0);
                }
            }
            if (numExted2 != oldExted2) {
                if (numExted2 > 0) {
                    tm_fitvals.insertRow(numExted, "V_x", new Double(100));
                    if (numExted2 == 2)
                        tm_fitvals.insertRow(numExted + 1, "V_y", new Double(0));
                }
                else {
                    if (oldExted2 == 2)
                        tm_fitvals.deleteRow(numExted + 1);
                    tm_fitvals.deleteRow(numExted);
                }
            }            
        }
    }

    public void setBinTimes(double[] bintimes, int anzbins) {
        this.anzbins = anzbins;
        this.bintimes = bintimes;

        int b1 = ((Integer)jFormattedTextField_RangeBin1.getValue()).intValue();
        int b2 = ((Integer)jFormattedTextField_RangeBin2.getValue()).intValue();
        if (b1 > anzbins)
            b1 = 1;
        if (b2 > anzbins)
            b2 = anzbins;
        if (b1 > b2) {
            int t = b2;
            b2 = b1;
            b1 = t;
        }
        jFormattedTextField_RangeBin1.setValue(new Integer(b1));
        jFormattedTextField_RangeBin2.setValue(new Integer(b2));
        jFormattedTextField_RangeT1.setValue(new Double(bintimes[b1-1]));
        jFormattedTextField_RangeT2.setValue(new Double(bintimes[b2-1]));
    }

    public void setCurveModel(DefaultComboBoxModel amod) {
        jComboBox_Curves.setModel(amod);
        jComboBox_Curves.setSelectedIndex(0);
    }

    public String getCurves() {
        Object o =  jComboBox_Curves.getSelectedItem();
        if ((o != null) && (o instanceof JA_CurveSet)) {
            return ((JA_CurveSet)o).getSetStr();
        }
        return "1:6";
    }

    public String getRange() {
        if ((anzbins > 0) && jCheckBox_Range.isSelected()) {
            int b1 = ((Integer)jFormattedTextField_RangeBin1.getValue()).intValue();
            int b2 = ((Integer)jFormattedTextField_RangeBin2.getValue()).intValue();
            if (b1 > anzbins)
                b1 = 1;
            if (b2 > anzbins)
                b2 = anzbins;
            if (b1 > b2) {
                int t = b2;
                b2 = b1;
                b1 = t;
            }
            return b1 + ":" + b2;
        }
        return ":";
    }

    public int getFirstCurve() {
        Object o =  jComboBox_Curves.getSelectedItem();
        if ((o != null) && (o instanceof JA_CurveSet)) {
            int[] s = ((JA_CurveSet)o).getSet();
            return s[0];
        }
        return 0;
    }

    public Double getMetaTag(String key) {
        Double ret = new Double(0);
        if (metadata.containsKey(key)) {
            Object o = metadata.get(key);
            if (o instanceof double[]) {
                double[] d = (double[])o;
                ret = new Double(d[0]);
            }
        }
        return ret;
    }

    public void setPreset(int num) {
        if (metadata != null) {
            switch(num) {
                case 1:
                default:
                    // Funktion 1-6 (blue or red if single color red if two color)
                    if (metadata.containsKey("LambdaEx2")) {
                        jFormattedTextField_FociDist.setValue(getMetaTag("Distance2"));
                        jFormattedTextField_lam1.setValue(getMetaTag("LambdaEx2"));
                        jFormattedTextField_lam2.setValue(getMetaTag("LambdaEm2"));
                    }
                    else {
                        jFormattedTextField_FociDist.setValue(getMetaTag("Distance1"));
                        jFormattedTextField_lam1.setValue(getMetaTag("LambdaEx1"));
                        jFormattedTextField_lam2.setValue(getMetaTag("LambdaEm1"));
                    }
                    break;
                 case 2:
                    // Funktion 7-12 (blue if two color)
                    jFormattedTextField_FociDist.setValue(getMetaTag("Distance1"));
                    jFormattedTextField_lam1.setValue(getMetaTag("LambdaEx1"));
                    jFormattedTextField_lam2.setValue(getMetaTag("LambdaEm1"));
                    break;
                case 3:
                    // Funktion 13-18 (FRET if two color)
                    if (metadata.containsKey("LambdaEx2")) {
                        jFormattedTextField_FociDist.setValue(getMetaTag("Distance1"));
                        jFormattedTextField_lam1.setValue(getMetaTag("LambdaEx1"));
                        jFormattedTextField_lam2.setValue(getMetaTag("LambdaEm2"));
                    }
                    else {
                        jFormattedTextField_FociDist.setValue(getMetaTag("Distance1"));
                        jFormattedTextField_lam1.setValue(getMetaTag("LambdaEx1"));
                        jFormattedTextField_lam2.setValue(getMetaTag("LambdaEm1"));
                    }
                    break;

            }
        }
        else {
            switch(num) {
                case 1:
                    // blue
                    jFormattedTextField_FociDist.setValue(new Double(452));
                    jFormattedTextField_lam1.setValue(new Double(470));
                    jFormattedTextField_lam2.setValue(new Double(520));
                    break;
                case 3:
                    // FRET
                    jFormattedTextField_FociDist.setValue(new Double(452));
                    jFormattedTextField_lam1.setValue(new Double(470));
                    jFormattedTextField_lam2.setValue(new Double(670));
                    break;
                case 2:
                default:
                    // red
                    jFormattedTextField_FociDist.setValue(new Double(445));
                    jFormattedTextField_lam1.setValue(new Double(640));
                    jFormattedTextField_lam2.setValue(new Double(670));
                    break;
            }
        }
    }

    public double[] getValues() {
        double[] vals;
        vals = new double[4];
        vals[0] = (Double)jFormattedTextField_Pinhole.getValue() / 2 * 1000 / 60;
        vals[1] = (Double)jFormattedTextField_lam1.getValue();
        vals[2] = (Double)jFormattedTextField_lam2.getValue();
        vals[3] = (Double)jFormattedTextField_FociDist.getValue();
        return vals;
    }

    public int[] getFitValues() {
        int[] vals;
        vals = new int[2];
        vals[0] = ((Integer)jFormattedTextField_NumExp.getValue()).intValue();
        vals[1] = ((Integer)jFormattedTextField_NumDiff.getValue()).intValue();
        return vals;
    }

    public String getInitialVals() {
        String ret = "[";
        int rows = tm_fitvals.getRowCount();
        int diffs = ((Integer)jFormattedTextField_NumDiff.getValue()).intValue();

        Double[] Vals = (Double[])tm_fitvals.getValueArray(0);
        for (int i = numExted+numExted2; i < rows; i++) {
            if (i > numExted+numExted2)
                ret += " ";
            Double value = Vals[i];
            if (i < diffs+numExted+numExted2) {
                value = 1e-8 / value;
            }
            ret += value.toString().replaceAll("[Ii]nfinity", "inf");
        }
        ret += "]";
        return ret;
    }

    public String getInitialValsW0A0() {
        String ret = "[";

        Double[] Vals = (Double[])tm_fitvals.getValueArray(0);
        if (numExted > 0) {
            for (int i = 0; i < numExted; i++) {
                if (i > 0)
                    ret += " ";
                Double value = Vals[i];
                ret += value.toString().replaceAll("[Ii]nfinity", "inf");
            }
            for (int i = 0; i < numExted2; i++) {
                ret += " ";
                Double value = Vals[i + numExted];
                ret += value.toString().replaceAll("[Ii]nfinity", "inf");
            }
        }
        else if (numExted2 > 0) {
            ret += "450 150";
            for (int i = 0; i < numExted2; i++) {
                ret += " ";
                Double value = Vals[i + numExted];
                ret += value.toString().replaceAll("[Ii]nfinity", "inf");
            }
        }
        ret += "]";
        return ret;
    }

    public String getMaxValsW0A0() {
        String ret = "[";

        Boolean[] Bounds = (Boolean[])tm_fitvals.getValueArray(1);
        Double[] Vals = (Double[])tm_fitvals.getValueArray(3);
        if (numExted > 0) {
            for (int i = 0; i < numExted; i++) {
                if (i > 0)
                    ret += " ";
                Double value = Vals[i];
                if (!Bounds[i].booleanValue()) {
                    value = Double.POSITIVE_INFINITY;
                }
                ret += value.toString().replaceAll("[Ii]nfinity", "inf");
            }
            for (int i = 0; i < numExted2; i++) {
                ret += " ";
                Double value = Vals[i + numExted];
                if (!Bounds[i].booleanValue()) {
                    value = Double.POSITIVE_INFINITY;
                }
                ret += value.toString().replaceAll("[Ii]nfinity", "inf");
            }
        }
        else if (numExted2 > 0) {
            ret += "inf inf";
            for (int i = 0; i < numExted2; i++) {
                ret += " ";
                Double value = Vals[i + numExted];
                if (!Bounds[i].booleanValue()) {
                    value = Double.POSITIVE_INFINITY;
                }
                ret += value.toString().replaceAll("[Ii]nfinity", "inf");
            }
        }

        ret += "]";
        return ret;
    }

    public String getMinValsW0A0() {
        String ret = "[";

        Boolean[] Bounds = (Boolean[])tm_fitvals.getValueArray(1);
        Double[] Vals = (Double[])tm_fitvals.getValueArray(2);
        if (numExted > 0) {
            for (int i = 0; i < numExted; i++) {
                if (i > 0)
                    ret += " ";
                Double value = Vals[i];
                if (!Bounds[i].booleanValue()) {
                    value = 0.;
                }
                ret += value.toString().replaceAll("[Ii]nfinity", "inf");
            }
            for (int i = 0; i < numExted2; i++) {
                ret += " ";
                Double value = Vals[i + numExted];
                if (!Bounds[i].booleanValue()) {
                    value = Double.NEGATIVE_INFINITY;
                }
                ret += value.toString().replaceAll("[Ii]nfinity", "inf");
            }
        }
        else if (numExted2 > 0) {
            ret += "0 0";
            for (int i = 0; i < numExted2; i++) {
                ret += " ";
                Double value = Vals[i + numExted];
                if (!Bounds[i].booleanValue()) {
                    value = Double.NEGATIVE_INFINITY;
                }
                ret += value.toString().replaceAll("[Ii]nfinity", "inf");
            }
        }
        
        ret += "]";
        return ret;
    }

    public String getBoundsStr() {
        String ret = "[";
        int rows = tm_fitvals.getRowCount();
        int diffs = ((Integer)jFormattedTextField_NumDiff.getValue()).intValue();

        Boolean[] Bounds = (Boolean[])tm_fitvals.getValueArray(1);
        Double[] LowBound = (Double[])tm_fitvals.getValueArray(2);
        Double[] HighBound = (Double[])tm_fitvals.getValueArray(3);
        boolean bounded = false;
        for (int i = numExted+numExted2; i < rows; i++) {
            if (ret.length() > 1)
                ret += " ";
            if (Bounds[i].booleanValue()) {
                Double[] value = new Double[2];
                value[0] = LowBound[i];
                value[1] = HighBound[i];
                if (i < diffs+numExted+numExted2) {
                    for (int k = 0; k < 2; k++) {
                        if (value[k] == 0) {
                            value[k] = Double.POSITIVE_INFINITY;
                        }
                        else if (value[k] == Double.POSITIVE_INFINITY) {
                            value[k] = 0.;
                        }
                        else {
                            value[k] = 1e-8 / value[k];
                        }
                    }
                }
                if (value[0] > value[1]) {
                    Double tv = value[0];
                    value[0] = value[1];
                    value[1] = tv;
                }
                bounded = true;
                ret += "[" + value[0].toString().replaceAll("[Ii]nfinity", "inf") + " " + value[1].toString().replaceAll("[Ii]nfinity", "inf") + "]'";
            }
            else {
                ret += "[0 inf]'";
            }
        }
        if (!bounded)
            return "[]";
        ret += "]";
        return ret;
    }

    public int getPooling() {
        int pool = 0;
        if (jRadioButton_PoolN.isSelected())
            pool = 1;
        if (jRadioButton_Boot.isSelected())
            pool = 2;
        return pool;
    }

    public void setBunchs(int fcsanzbunch) {
        jFormattedTextField_BootStrap.setValue(new Integer(fcsanzbunch));
    }

    public int getPoolN() {
        int ret = 0;
        if (jRadioButton_PoolN.isSelected())
            ret = ((Integer)jFormattedTextField_SumEvery.getValue()).intValue();
        if (jRadioButton_Boot.isSelected())
            ret = ((Integer)jFormattedTextField_BootStrap.getValue()).intValue();
        return ret;
    }

    public int getExportFigure() {
        if (jCheckBox_Export.isSelected())
            return 1;
        return 0;
    }

    public boolean is3DDiffusion() {
        return jRadioButton_Diff3D.isSelected();
    }

    public int getReturnVal() {
        return retval;
    }

    public void updatePoolType(int i) {
        jFormattedTextField_BootStrap.setVisible(i==2);
        jFormattedTextField_BootStrap.setEnabled(i==2);

        jFormattedTextField_SumEvery.setEnabled(i==1);
        jFormattedTextField_SumEvery.setVisible(i==1);

        String tt = "";
        switch (i) {
            case 1:
                tt = "Sum every N Bunchs. N = ";
                break;
            case 2:
                tt = "Bootstrap Iterations:";
                break;
        }
        jLabel_Pool.setText(tt);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jComboBox_Preset = new javax.swing.JComboBox();
        jButton_CANCEL = new javax.swing.JButton();
        jButton_OK = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jFormattedTextField_lam2 = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jFormattedTextField_lam1 = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jFormattedTextField_Pinhole = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        jFormattedTextField_FociDist = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jComboBox_Curves = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jFormattedTextField_NumExp = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        jFormattedTextField_NumDiff = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_FitCoeff = new javax.swing.JTable();
        jCheckBox_Range = new javax.swing.JCheckBox();
        jCheckBox_W0A0 = new javax.swing.JCheckBox();
        jCheckBox_Flow = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jFormattedTextField_BootStrap = new javax.swing.JFormattedTextField();
        jLabel_Pool = new javax.swing.JLabel();
        jRadioButton_Boot = new javax.swing.JRadioButton();
        jRadioButton_SumAll = new javax.swing.JRadioButton();
        jRadioButton_PoolN = new javax.swing.JRadioButton();
        jFormattedTextField_SumEvery = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jRadioButton_Diff2D = new javax.swing.JRadioButton();
        jRadioButton_Diff3D = new javax.swing.JRadioButton();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel_Range = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jFormattedTextField_RangeBin1 = new javax.swing.JFormattedTextField();
        jFormattedTextField_RangeT1 = new javax.swing.JFormattedTextField();
        jFormattedTextField_RangeBin2 = new javax.swing.JFormattedTextField();
        jFormattedTextField_RangeT2 = new javax.swing.JFormattedTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jCheckBox_Export = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("FCSFit Settings");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox_Preset.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Blue", "Red", "FRET" }));
        jComboBox_Preset.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox_PresetItemStateChanged(evt);
            }
        });
        getContentPane().add(jComboBox_Preset, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 11, 92, -1));

        jButton_CANCEL.setFont(new java.awt.Font("Tahoma", 1, 12));
        jButton_CANCEL.setText("Cancel");
        jButton_CANCEL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_CANCELActionPerformed(evt);
            }
        });
        getContentPane().add(jButton_CANCEL, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 450, -1, -1));

        jButton_OK.setFont(new java.awt.Font("Tahoma", 1, 12));
        jButton_OK.setText("OK");
        jButton_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_OKActionPerformed(evt);
            }
        });
        getContentPane().add(jButton_OK, new org.netbeans.lib.awtextra.AbsoluteConstraints(475, 450, 73, -1));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel2.setText("Pinhole size:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel3.setText("Distance between Foci:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, 20));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel6.setText("Wave Len 1 ");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, 20));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel8.setText("Wave Len 2 ");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, -1, 20));

        jFormattedTextField_lam2.setText("0");
        jPanel1.add(jFormattedTextField_lam2, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 150, 38, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel9.setText("nm");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 150, -1, 20));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel7.setText("nm");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 120, -1, 20));

        jFormattedTextField_lam1.setText("0");
        jPanel1.add(jFormattedTextField_lam1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 120, 38, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel4.setText("um");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 50, -1, 20));

        jFormattedTextField_Pinhole.setText("150");
        jPanel1.add(jFormattedTextField_Pinhole, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, 38, -1));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel5.setText("nm");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 80, -1, 20));

        jFormattedTextField_FociDist.setText("0");
        jPanel1.add(jFormattedTextField_FociDist, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 80, 38, -1));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel1.setText("Curves to Fit:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 13, -1, -1));

        jComboBox_Curves.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Curve 1-4", "Curve 5-8", "Curve 9-12", "Curve 13-16" }));
        jComboBox_Curves.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox_CurvesItemStateChanged(evt);
            }
        });
        jPanel1.add(jComboBox_Curves, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, 100, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 255, 180));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel14.setText("Export Fit results:");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 10, -1, 20));

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel10.setText("Exponents:");
        jPanel4.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, 20));

        jFormattedTextField_NumExp.setText("1");
        jFormattedTextField_NumExp.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextField_NumExpPropertyChange(evt);
            }
        });
        jPanel4.add(jFormattedTextField_NumExp, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, 38, -1));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel11.setText("Diff. Coefficients:");
        jPanel4.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, 20));

        jFormattedTextField_NumDiff.setText("1");
        jFormattedTextField_NumDiff.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextField_NumDiffPropertyChange(evt);
            }
        });
        jPanel4.add(jFormattedTextField_NumDiff, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 20, 38, -1));

        jTable_FitCoeff.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable_FitCoeff);

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 82, 260, 110));

        jCheckBox_Range.setText("specify Fit Range");
        jCheckBox_Range.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox_RangeActionPerformed(evt);
            }
        });
        jPanel4.add(jCheckBox_Range, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, -1, -1));

        jCheckBox_W0A0.setText("specify w_0 and a_0");
        jCheckBox_W0A0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox_W0A0ActionPerformed(evt);
            }
        });
        jPanel4.add(jCheckBox_W0A0, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, -1, -1));

        jCheckBox_Flow.setText("Flow measurement");
        jCheckBox_Flow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox_FlowActionPerformed(evt);
            }
        });
        jPanel4.add(jCheckBox_Flow, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 200, -1, -1));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 105, 280, 250));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel15.setText("Bunch pooling Method:");
        jPanel2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 20));

        jFormattedTextField_BootStrap.setText("0");
        jPanel2.add(jFormattedTextField_BootStrap, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 70, 38, -1));

        jLabel_Pool.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel_Pool.setText("Bootstrap Iterations:");
        jPanel2.add(jLabel_Pool, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, 20));

        buttonGroup2.add(jRadioButton_Boot);
        jRadioButton_Boot.setText("Bootstrap");
        jRadioButton_Boot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_BootActionPerformed(evt);
            }
        });
        jPanel2.add(jRadioButton_Boot, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 35, -1, -1));

        buttonGroup2.add(jRadioButton_SumAll);
        jRadioButton_SumAll.setText("Sum All");
        jRadioButton_SumAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_SumAllActionPerformed(evt);
            }
        });
        jPanel2.add(jRadioButton_SumAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, -1, -1));

        buttonGroup2.add(jRadioButton_PoolN);
        jRadioButton_PoolN.setText("Sum every N");
        jRadioButton_PoolN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_PoolNActionPerformed(evt);
            }
        });
        jPanel2.add(jRadioButton_PoolN, new org.netbeans.lib.awtextra.AbsoluteConstraints(75, 35, -1, -1));

        jFormattedTextField_SumEvery.setText("0");
        jPanel2.add(jFormattedTextField_SumEvery, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 70, 38, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 245, 255, 110));
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 340, 10, 10));

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel16.setText("Select Preset");
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 13, -1, -1));

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonGroup1.add(jRadioButton_Diff2D);
        jRadioButton_Diff2D.setText("2D (membrane)");
        jRadioButton_Diff2D.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_Diff2DActionPerformed(evt);
            }
        });
        jPanel3.add(jRadioButton_Diff2D, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, -1, -1));

        buttonGroup1.add(jRadioButton_Diff3D);
        jRadioButton_Diff3D.setText("3D (free)");
        jRadioButton_Diff3D.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_Diff3DActionPerformed(evt);
            }
        });
        jPanel3.add(jRadioButton_Diff3D, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, -1, -1));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel17.setText("Diffusion:");
        jPanel3.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 20));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, 280, 40));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel18.setText("Settings:");
        getContentPane().add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        jPanel_Range.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel_Range.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel13.setText("Fit Range:");
        jPanel_Range.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jFormattedTextField_RangeBin1.setText("1");
        jFormattedTextField_RangeBin1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField_RangeBin1ActionPerformed(evt);
            }
        });
        jFormattedTextField_RangeBin1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextField_RangeBin1PropertyChange(evt);
            }
        });
        jPanel_Range.add(jFormattedTextField_RangeBin1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, 38, -1));

        jFormattedTextField_RangeT1.setText("1e-6");
        jFormattedTextField_RangeT1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextField_RangeT1PropertyChange(evt);
            }
        });
        jPanel_Range.add(jFormattedTextField_RangeT1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 40, 50, -1));

        jFormattedTextField_RangeBin2.setText("150");
        jFormattedTextField_RangeBin2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextField_RangeBin2PropertyChange(evt);
            }
        });
        jPanel_Range.add(jFormattedTextField_RangeBin2, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 40, 38, -1));

        jFormattedTextField_RangeT2.setText("1e3");
        jFormattedTextField_RangeT2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextField_RangeT2PropertyChange(evt);
            }
        });
        jPanel_Range.add(jFormattedTextField_RangeT2, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 40, 50, -1));

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel19.setText("time ");
        jPanel_Range.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 40, 40, 20));

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel21.setText(" s");
        jPanel_Range.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 40, 20, 20));

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel22.setText("bin ");
        jPanel_Range.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, 30, 20));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel23.setText("From:");
        jPanel_Range.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 50, 20));

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel24.setText("To:");
        jPanel_Range.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 40, 30, 20));

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel25.setText("bin ");
        jPanel_Range.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 40, 30, 20));

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel20.setText("time ");
        jPanel_Range.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 40, 40, 20));

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel26.setText(" s");
        jPanel_Range.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 40, 20, 20));

        getContentPane().add(jPanel_Range, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 360, 540, 80));

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel27.setText("Fit Parameters:");
        getContentPane().add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 40, -1, -1));

        jCheckBox_Export.setText(" export .png");
        jCheckBox_Export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox_ExportActionPerformed(evt);
            }
        });
        getContentPane().add(jCheckBox_Export, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 10, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_CANCELActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_CANCELActionPerformed
        retval = ID_CANCEL;
        this.setVisible(false);
    }//GEN-LAST:event_jButton_CANCELActionPerformed

    private void jButton_OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_OKActionPerformed
        retval = ID_OK;
        this.setVisible(false);
    }//GEN-LAST:event_jButton_OKActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        /*setLocationRelativeTo(null);
        Object val1 = jFormattedTextField_Range1.getValue();
        Object val2 = jFormattedTextField_Range2.getValue();
        parent.updateCursor(((Double)val1).doubleValue(), ((Double)val2).doubleValue());*/
    }//GEN-LAST:event_formWindowOpened

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        setLocationRelativeTo(parent);
        retval = ID_CANCEL;
        jLabel16.setVisible(metadata == null);
        jComboBox_Preset.setVisible(metadata == null);
        if (metadata != null) {
            setPreset(jComboBox_Curves.getSelectedIndex() + 1);
            jFormattedTextField_Pinhole.setValue(getMetaTag("Pinhole"));
        }
        //Object val1 = jFormattedTextField_Range1.getValue();
        //Object val2 = jFormattedTextField_Range2.getValue();
    }//GEN-LAST:event_formWindowActivated

    private void jComboBox_PresetItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox_PresetItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            int i = jComboBox_Preset.getSelectedIndex();
            if (i >= 0)
                setPreset(i+1);
        }
    }//GEN-LAST:event_jComboBox_PresetItemStateChanged

    private void jFormattedTextField_NumExpPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFormattedTextField_NumExpPropertyChange
        if (evt.getPropertyName().equals("value")) {
            updateTable();
        }
    }//GEN-LAST:event_jFormattedTextField_NumExpPropertyChange

    private void jFormattedTextField_NumDiffPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFormattedTextField_NumDiffPropertyChange
        if (evt.getPropertyName().equals("value")) {
            updateTable();
        }
    }//GEN-LAST:event_jFormattedTextField_NumDiffPropertyChange

    private void jComboBox_CurvesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox_CurvesItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            if (metadata != null) {
                int i = jComboBox_Curves.getSelectedIndex();
                if (i >= 0)
                    setPreset(i+1);
            }
        }
    }//GEN-LAST:event_jComboBox_CurvesItemStateChanged

    private void jRadioButton_PoolNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_PoolNActionPerformed
        updatePoolType(1);
    }//GEN-LAST:event_jRadioButton_PoolNActionPerformed

    private void jRadioButton_SumAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_SumAllActionPerformed
        updatePoolType(0);
    }//GEN-LAST:event_jRadioButton_SumAllActionPerformed

    private void jRadioButton_BootActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_BootActionPerformed
        updatePoolType(2);
    }//GEN-LAST:event_jRadioButton_BootActionPerformed

    private void jCheckBox_RangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox_RangeActionPerformed
        boolean vis = jCheckBox_Range.isSelected();
        jPanel_Range.setVisible(vis);
        getContentPane().remove(jButton_CANCEL);
        getContentPane().remove(jButton_OK);
        getContentPane().add(jButton_CANCEL, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, (vis) ? 450 : 370, -1, -1));
        getContentPane().add(jButton_OK, new org.netbeans.lib.awtextra.AbsoluteConstraints(475, (vis) ? 450 : 370, 73, -1));
        Rectangle br = this.getBounds();
        br.height = (vis) ? 520 : 440;
        this.setBounds(br);
    }//GEN-LAST:event_jCheckBox_RangeActionPerformed

    private void jFormattedTextField_RangeBin1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField_RangeBin1ActionPerformed

    }//GEN-LAST:event_jFormattedTextField_RangeBin1ActionPerformed

    private void jFormattedTextField_RangeBin1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFormattedTextField_RangeBin1PropertyChange
        if (evt.getPropertyName().equals("value") && bintimes != null) {
            int b1 = ((Integer)jFormattedTextField_RangeBin1.getValue()).intValue();
            int b2 = ((Integer)jFormattedTextField_RangeBin2.getValue()).intValue();
            if (b1 > anzbins)
                b1 = 1;
            if (b2 > anzbins)
                b2 = anzbins;
            if (b1 > b2) {
                int t = b2;
                b2 = b1;
                b1 = t;
            }
            jFormattedTextField_RangeBin1.setValue(new Integer(b1));
            jFormattedTextField_RangeBin2.setValue(new Integer(b2));
            jFormattedTextField_RangeT1.setValue(new Double(bintimes[b1-1]));
            jFormattedTextField_RangeT2.setValue(new Double(bintimes[b2-1]));
        }
    }//GEN-LAST:event_jFormattedTextField_RangeBin1PropertyChange

    private void jFormattedTextField_RangeBin2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFormattedTextField_RangeBin2PropertyChange
        if (evt.getPropertyName().equals("value") && bintimes != null) {
            int b1 = ((Integer)jFormattedTextField_RangeBin1.getValue()).intValue();
            int b2 = ((Integer)jFormattedTextField_RangeBin2.getValue()).intValue();
            if (b1 > anzbins)
                b1 = 1;
            if (b2 > anzbins)
                b2 = anzbins;
            if (b1 > b2) {
                int t = b2;
                b2 = b1;
                b1 = t;
            }
            jFormattedTextField_RangeBin1.setValue(new Integer(b1));
            jFormattedTextField_RangeBin2.setValue(new Integer(b2));
            jFormattedTextField_RangeT1.setValue(new Double(bintimes[b1-1]));
            jFormattedTextField_RangeT2.setValue(new Double(bintimes[b2-1]));
        }
    }//GEN-LAST:event_jFormattedTextField_RangeBin2PropertyChange

    private void jFormattedTextField_RangeT1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFormattedTextField_RangeT1PropertyChange
        if (evt.getPropertyName().equals("value") && bintimes != null) {
            double nval = ((Double)evt.getNewValue()).doubleValue();
            int i = 0;
            while ((i < anzbins) && (nval > bintimes[i])) {
                i++;
            }
            jFormattedTextField_RangeBin1.setValue(new Integer(i+1));
        }
    }//GEN-LAST:event_jFormattedTextField_RangeT1PropertyChange

    private void jFormattedTextField_RangeT2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFormattedTextField_RangeT2PropertyChange
        if (evt.getPropertyName().equals("value") && bintimes != null) {
            double nval = ((Double)evt.getNewValue()).doubleValue();
            int i = anzbins-1;
            while ((i > 0) && (nval < bintimes[i])) {
                i--;
            }
            jFormattedTextField_RangeBin2.setValue(new Integer(i+1));
        }
    }//GEN-LAST:event_jFormattedTextField_RangeT2PropertyChange

    private void jCheckBox_W0A0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox_W0A0ActionPerformed
        updateTable();
    }//GEN-LAST:event_jCheckBox_W0A0ActionPerformed

    private void jRadioButton_Diff3DActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_Diff3DActionPerformed
        updateTable();
    }//GEN-LAST:event_jRadioButton_Diff3DActionPerformed

    private void jRadioButton_Diff2DActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_Diff2DActionPerformed
        updateTable();
    }//GEN-LAST:event_jRadioButton_Diff2DActionPerformed

    private void jCheckBox_FlowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox_FlowActionPerformed
        updateTable();
    }//GEN-LAST:event_jCheckBox_FlowActionPerformed

    private void jCheckBox_ExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox_ExportActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox_ExportActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JA_FCSFitDialog dialog = new JA_FCSFitDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton_CANCEL;
    private javax.swing.JButton jButton_OK;
    private javax.swing.JCheckBox jCheckBox_Export;
    private javax.swing.JCheckBox jCheckBox_Flow;
    private javax.swing.JCheckBox jCheckBox_Range;
    private javax.swing.JCheckBox jCheckBox_W0A0;
    private javax.swing.JComboBox jComboBox_Curves;
    private javax.swing.JComboBox jComboBox_Preset;
    private javax.swing.JFormattedTextField jFormattedTextField_BootStrap;
    private javax.swing.JFormattedTextField jFormattedTextField_FociDist;
    private javax.swing.JFormattedTextField jFormattedTextField_NumDiff;
    private javax.swing.JFormattedTextField jFormattedTextField_NumExp;
    private javax.swing.JFormattedTextField jFormattedTextField_Pinhole;
    private javax.swing.JFormattedTextField jFormattedTextField_RangeBin1;
    private javax.swing.JFormattedTextField jFormattedTextField_RangeBin2;
    private javax.swing.JFormattedTextField jFormattedTextField_RangeT1;
    private javax.swing.JFormattedTextField jFormattedTextField_RangeT2;
    private javax.swing.JFormattedTextField jFormattedTextField_SumEvery;
    private javax.swing.JFormattedTextField jFormattedTextField_lam1;
    private javax.swing.JFormattedTextField jFormattedTextField_lam2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_Pool;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel_Range;
    private javax.swing.JRadioButton jRadioButton_Boot;
    private javax.swing.JRadioButton jRadioButton_Diff2D;
    private javax.swing.JRadioButton jRadioButton_Diff3D;
    private javax.swing.JRadioButton jRadioButton_PoolN;
    private javax.swing.JRadioButton jRadioButton_SumAll;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_FitCoeff;
    // End of variables declaration//GEN-END:variables

}
