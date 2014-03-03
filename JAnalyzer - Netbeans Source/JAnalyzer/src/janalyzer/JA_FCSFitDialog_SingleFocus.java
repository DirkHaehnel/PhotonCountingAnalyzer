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
public class JA_FCSFitDialog_SingleFocus extends javax.swing.JDialog {

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
    public JA_FCSFitDialog_SingleFocus(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        // Set the default locale to custom locale
        Locale.setDefault(new Locale("en", "DE"));

        if (parent instanceof JA_Main)
            this.parent = (JA_Main)parent;
        retval = -1;
        numDiffs = 1;

        initComponents();
        setLocationRelativeTo(null);

        DecimalFormat df;
        NumberFormatter nf, nf2;

        df = new DecimalFormat();
        df.setMaximumIntegerDigits(4);
        df.setMaximumFractionDigits(3);
        df.setGroupingUsed(false);

        nf = new NumberFormatter(df);
        nf.setValueClass(Integer.class);
        nf.setMinimum(new Integer(0));
        nf.setMaximum(new Integer(5));

        jFormattedTextField_NumExp.setFormatterFactory(new DefaultFormatterFactory(nf));

        nf = new NumberFormatter(df);
        nf.setValueClass(Integer.class);
        nf.setMinimum(new Integer(1));
        nf.setMaximum(new Integer(3));

        jFormattedTextField_NumExp.setValue(new Integer(0));
        numExps = 0;
        numDiffs = 1;
        numExted = 0;

        nf = new NumberFormatter(df);
        nf.setValueClass(Integer.class);
        nf.setMinimum(new Integer(1));
        nf.setMaximum(new Integer(1000));
        jFormattedTextField_RangeBin1.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_RangeBin2.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_RangeBin1.setValue(new Integer(1));
        jFormattedTextField_RangeBin2.setValue(new Integer(1000));

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

        tm_fitvals = new JA_FitValsTableModel(nf2);
        tm_fitvals.deleteRow(1);
        tm_fitvals.deleteRow(0);
        tm_fitvals.insertRow(0, "D_" + 1, new Double(10));
        tm_fitvals.insertRow(1, "a", new Double(3.3));

        jTable_FitCoeff.setDefaultEditor(Double.class, de);
        jTable_FitCoeff.setDefaultRenderer(Double.class, new JA_TableCellRenderer(nf2));
        jTable_FitCoeff.setModel(tm_fitvals);
        jTable_FitCoeff.setDragEnabled(false);

        boolean vis = false;
        jPanel_Range.setVisible(vis);
        getContentPane().remove(jButton_CANCEL);
        getContentPane().remove(jButton_OK);
        getContentPane().add(jButton_CANCEL, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, (vis) ? 370 : 290, -1, -1));
        getContentPane().add(jButton_OK, new org.netbeans.lib.awtextra.AbsoluteConstraints(475, (vis) ? 370 : 290, 73, -1));
        Rectangle br = this.getBounds();
        br.height = (vis) ? 440 : 360;
        this.setBounds(br);
    }

    public void updateTable() {
        if (tm_fitvals != null) {
            int oldExps = numExps;
            int oldExted = numExted;
            int allExted = numExted;
            numExps = ((Integer)jFormattedTextField_NumExp.getValue()).intValue();
            numExted = (jCheckBox_Modulation.isSelected()) ? 1 : 0;

            if (oldExps > numExps) {
                // delete exps
                for (int i = oldExps; i > numExps; i--) {
                    tm_fitvals.deleteRow(allExted+i+numDiffs);
                }
            }
            else {
                // add rows
                for (int i = oldExps; i < numExps; i++) {
                    tm_fitvals.insertRow(1+allExted+i+numDiffs, "T_" + (i+1), new Double(10));
                }
            }
            if (numExted != oldExted) {
                if (numExted == 1) {
                    tm_fitvals.insertRow(2, "F_" + 0, new Double(10));
                }
                else {
                    tm_fitvals.deleteRow(2);
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

    public String getModulation() {
        return jCheckBox_Modulation.isSelected() ? "1" : "[]";
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

    public int[] getFitValues() {
        int[] vals;
        vals = new int[2];
        vals[0] = ((Integer)jFormattedTextField_NumExp.getValue()).intValue();
        vals[1] = 1;
        return vals;
    }

    public String getInitialVals() {
        String ret = "[";
        int rows = tm_fitvals.getRowCount();
        int diffs = 2;

        Double[] Vals = (Double[])tm_fitvals.getValueArray(0);
        for (int i = 0; i < rows; i++) {
            if (i > 0)
                ret += " ";
            Double value = Vals[i];
            if (i == 0)
                value = value * 1e-3;
            if (i > (jCheckBox_Modulation.isSelected() ? 2 : 1))
                value = value * 1e-6;
            
            ret += value.toString().replaceAll("[Ii]nfinity", "inf");
        }
        ret += "]";
        return ret;
    }

    public String getBoundsStr() {
        String ret = "[";
        int rows = tm_fitvals.getRowCount();
        int diffs = 2;

        Boolean[] Bounds = (Boolean[])tm_fitvals.getValueArray(1);
        Double[] LowBound = (Double[])tm_fitvals.getValueArray(2);
        Double[] HighBound = (Double[])tm_fitvals.getValueArray(3);
        boolean bounded = false;
        for (int i = 0; i < rows; i++) {
            if (ret.length() > 1)
                ret += " ";
            if (Bounds[i].booleanValue()) {
                Double[] value = new Double[2];
                value[0] = LowBound[i];
                value[1] = HighBound[i];

                for (int k = 0; k < 2; k++) {
                    if (i == 0)
                        value[k] = value[k] * 1e-3;
                    if (i > (jCheckBox_Modulation.isSelected() ? 2 : 1))
                        value[k] = value[k] * 1e-6;
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

    public int getExportFigure() {
        if (jCheckBox_Export.isSelected())
            return 1;
        return 0;
    }

    public int getReturnVal() {
        return retval;
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
        jButton_CANCEL = new javax.swing.JButton();
        jButton_OK = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox_Curves = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        jCheckBox_Export = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jFormattedTextField_NumExp = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_FitCoeff = new javax.swing.JTable();
        jCheckBox_Range = new javax.swing.JCheckBox();
        jCheckBox_Modulation = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
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

        jButton_CANCEL.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton_CANCEL.setText("Cancel");
        jButton_CANCEL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_CANCELActionPerformed(evt);
            }
        });
        getContentPane().add(jButton_CANCEL, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, -1, -1));

        jButton_OK.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton_OK.setText("OK");
        jButton_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_OKActionPerformed(evt);
            }
        });
        getContentPane().add(jButton_OK, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 370, 73, -1));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel1.setText("Curves to Fit:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 13, -1, -1));

        jComboBox_Curves.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Curve 1-4", "Curve 5-8", "Curve 9-12", "Curve 13-16" }));
        jComboBox_Curves.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox_CurvesItemStateChanged(evt);
            }
        });
        jPanel1.add(jComboBox_Curves, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 10, 100, -1));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setText("Export Fit results:");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, 20));

        jCheckBox_Export.setText(" export .png");
        jCheckBox_Export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox_ExportActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox_Export, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 255, 80));

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Exponents:");
        jPanel4.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, 20));

        jFormattedTextField_NumExp.setText("1");
        jFormattedTextField_NumExp.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextField_NumExpPropertyChange(evt);
            }
        });
        jPanel4.add(jFormattedTextField_NumExp, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 20, 38, -1));

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

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 260, 110));

        jCheckBox_Range.setText("specify Fit Range");
        jCheckBox_Range.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox_RangeActionPerformed(evt);
            }
        });
        jPanel4.add(jCheckBox_Range, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, -1, -1));

        jCheckBox_Modulation.setText("include modulation term");
        jCheckBox_Modulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox_ModulationActionPerformed(evt);
            }
        });
        jPanel4.add(jCheckBox_Modulation, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, -1, -1));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 50, 280, 220));
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 320, 10, 10));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel18.setText("Settings:");
        getContentPane().add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

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

        getContentPane().add(jPanel_Range, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 540, 80));

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel27.setText("Fit Parameters:");
        getContentPane().add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 20, -1, -1));

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
        //Object val1 = jFormattedTextField_Range1.getValue();
        //Object val2 = jFormattedTextField_Range2.getValue();
    }//GEN-LAST:event_formWindowActivated

    private void jFormattedTextField_NumExpPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFormattedTextField_NumExpPropertyChange
        if (evt.getPropertyName().equals("value")) {
            updateTable();
        }
    }//GEN-LAST:event_jFormattedTextField_NumExpPropertyChange

    private void jComboBox_CurvesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox_CurvesItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
        }
    }//GEN-LAST:event_jComboBox_CurvesItemStateChanged

    private void jCheckBox_RangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox_RangeActionPerformed
        boolean vis = jCheckBox_Range.isSelected();
        jPanel_Range.setVisible(vis);
        getContentPane().remove(jButton_CANCEL);
        getContentPane().remove(jButton_OK);
        getContentPane().add(jButton_CANCEL, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, (vis) ? 370 : 290, -1, -1));
        getContentPane().add(jButton_OK, new org.netbeans.lib.awtextra.AbsoluteConstraints(475, (vis) ? 370 : 290, 73, -1));
        Rectangle br = this.getBounds();
        br.height = (vis) ? 440 : 360;
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

    private void jCheckBox_ExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox_ExportActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox_ExportActionPerformed

    private void jCheckBox_ModulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox_ModulationActionPerformed
        updateTable();
}//GEN-LAST:event_jCheckBox_ModulationActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JA_FCSFitDialog_SingleFocus dialog = new JA_FCSFitDialog_SingleFocus(new javax.swing.JFrame(), true);
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
    private javax.swing.JCheckBox jCheckBox_Modulation;
    private javax.swing.JCheckBox jCheckBox_Range;
    private javax.swing.JComboBox jComboBox_Curves;
    private javax.swing.JFormattedTextField jFormattedTextField_NumExp;
    private javax.swing.JFormattedTextField jFormattedTextField_RangeBin1;
    private javax.swing.JFormattedTextField jFormattedTextField_RangeBin2;
    private javax.swing.JFormattedTextField jFormattedTextField_RangeT1;
    private javax.swing.JFormattedTextField jFormattedTextField_RangeT2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel_Range;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_FitCoeff;
    // End of variables declaration//GEN-END:variables

}
