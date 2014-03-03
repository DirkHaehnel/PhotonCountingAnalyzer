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

/**
 *
 * @author cpieper
 */
public class JA_FitDialog extends javax.swing.JDialog {

    public static final int ID_OK = 0;
    public static final int ID_CANCEL = 99;
    
    private Range[] ranges;
    private int currentRange = -1;
    private int retval;
    private int numCurvs = -1;
    private JA_Main parent = null;

    /** Creates new form JA_FitDialog */
    public JA_FitDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        if (parent instanceof JA_Main)
            this.parent = (JA_Main)parent;
        retval = -1;
        initComponents();
        setLocationRelativeTo(null);

        DecimalFormat df;
        NumberFormatter nf;
        MaskFormatter mf;

        df = new DecimalFormat();
        df.setMaximumIntegerDigits(3);
        df.setMaximumFractionDigits(3);
        df.setGroupingUsed(false);
        nf = new NumberFormatter(df);
        nf.setValueClass(Double.class);
        nf.setMinimum(new Double(0));
        nf.setMaximum(new Double(100));

        jFormattedTextField_LT1.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_LTMin1.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_LT2.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_LTMin2.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_LT2.setEnabled(false);
        jFormattedTextField_LTMin2.setEnabled(false);
        jCheckBox_Exp2.setSelected(false);

        jFormattedTextField_Range1.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_Range2.setFormatterFactory(new DefaultFormatterFactory(nf));

        jFormattedTextField_Range1.setValue(new Double(4));
        jFormattedTextField_Range2.setValue(new Double(20));
        jFormattedTextField_LT1.setValue(new Double(0.8));
        jFormattedTextField_LTMin1.setValue(new Double(0));
        jFormattedTextField_LT2.setValue(new Double(2.5));
        jFormattedTextField_LTMin2.setValue(new Double(0));

    }

    public void setNumCurves(int num) {
        if (num != numCurvs) {
            numCurvs = num;
            DefaultComboBoxModel cm = new DefaultComboBoxModel();
            for (int i = 0; i < num; i++) {
                int[] ti = new int[1];
                ti[0] = (i+1);
                cm.addElement(new DetectorSet("Detector " + (i+1), ti));
            }
            int pairs = (int)Math.floor(num / 2);
            if (pairs > 1) {
                for (int i = 0; i < pairs; i++) {
                    int[] ti = new int[2];
                    ti[0] = i*2 + 1;
                    ti[1] = i*2 + 2;
                    cm.addElement(new DetectorSet("Detectors " + ti[0] + " + " + ti[1], ti));
                }
            }
            DetectorSet allset = new DetectorSet("All Detectors", null);
            cm.addElement(allset);
            cm.setSelectedItem(allset);
            jComboBox_Curve.setModel(cm);
        }
    }

    public int[] getSelectedDetectorSet() {
        Object sel = jComboBox_Curve.getSelectedItem();
        if (sel instanceof DetectorSet) {
            return ((DetectorSet)sel).getSet();
        }
        return null;
    }

    public double[] getStartLT() {
        double[] lt;
        if (jCheckBox_Exp2.isSelected()) {
            lt = new double[2];
            lt[0] = (Double)jFormattedTextField_LT1.getValue();
            lt[1] = (Double)jFormattedTextField_LT2.getValue();
        }
        else {
            lt = new double[1];
            lt[0] = (Double)jFormattedTextField_LT1.getValue();
        }
        return lt;
    }

    public double[] getMinLT() {
        double[] lt;
        if (jCheckBox_Exp2.isSelected()) {
            lt = new double[2];
            lt[0] = (Double)jFormattedTextField_LTMin1.getValue();
            lt[1] = (Double)jFormattedTextField_LTMin2.getValue();
        }
        else {
            lt = new double[1];
            lt[0] = (Double)jFormattedTextField_LTMin1.getValue();
        }
        return lt;
    }

    public double[] getRange() {
        double[] lt;
        lt = new double[2];
        lt[0] = (Double)jFormattedTextField_Range1.getValue();
        lt[1] = (Double)jFormattedTextField_Range2.getValue();
        return lt;
    }

    public void setRange(double r1, double r2) {
        jFormattedTextField_Range1.setValue(new Double(r1));
        jFormattedTextField_Range2.setValue(new Double(r2));
    }

    public void setRanges(Range[] ranges) {
        this.ranges = ranges;
        currentRange = -1;
    }

    public class Range {
        public double start, stop;

        Range(double start, double stop) {
            this.start = start;
            this.stop = stop;
        }
    }

    public int getReturnVal() {
        return retval;
    }

    public class DetectorSet extends Object {
        private String name;
        private int[] set;

        public DetectorSet(String name, int[] set) {
            this.name = name;
            this.set = set;
        }

        public String toString() {
            return name;
        }

        public int[] getSet() {
            return set;
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBox_Exp2 = new javax.swing.JCheckBox();
        jComboBox_Curve = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jFormattedTextField_LT1 = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jFormattedTextField_LTMin1 = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jFormattedTextField_Range1 = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        jFormattedTextField_Range2 = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jFormattedTextField_LT2 = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        jFormattedTextField_LTMin2 = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jButton_CANCEL = new javax.swing.JButton();
        jButton_OK = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Fit Lifetime");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jCheckBox_Exp2.setFont(new java.awt.Font("Tahoma", 1, 12));
        jCheckBox_Exp2.setText("2nd");
        jCheckBox_Exp2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox_Exp2ActionPerformed(evt);
            }
        });

        jComboBox_Curve.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Curve 1", "Curve 2" }));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel1.setText("Fit Curve:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel2.setText("Start Lifetime:");

        jFormattedTextField_LT1.setText("2");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel3.setText("Minimum Lifetime:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel4.setText("ns");

        jFormattedTextField_LTMin1.setText("0");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel5.setText("ns");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel6.setText("Range:");

        jFormattedTextField_Range1.setText("1");
        jFormattedTextField_Range1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextField_Range1PropertyChange(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel7.setText("ns  to");

        jFormattedTextField_Range2.setText("12");
        jFormattedTextField_Range2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFormattedTextField_Range2PropertyChange(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel8.setText("ns");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel9.setText("ns");

        jFormattedTextField_LT2.setText("1");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel10.setText("ns");

        jFormattedTextField_LTMin2.setText("0");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel11.setText("  1st");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel13.setText("Exponents:");

        jButton_CANCEL.setFont(new java.awt.Font("Tahoma", 1, 12));
        jButton_CANCEL.setText("Cancel");
        jButton_CANCEL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_CANCELActionPerformed(evt);
            }
        });

        jButton_OK.setFont(new java.awt.Font("Tahoma", 1, 12));
        jButton_OK.setText("OK");
        jButton_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_OKActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox_Curve, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jFormattedTextField_Range1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFormattedTextField_Range2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel13))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jFormattedTextField_LT1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel4))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jFormattedTextField_LTMin1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel5))
                                    .addComponent(jLabel11)))
                            .addComponent(jButton_CANCEL))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox_Exp2)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jFormattedTextField_LT2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jFormattedTextField_LTMin2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox_Curve, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel13))
                        .addGap(5, 5, 5))
                    .addComponent(jCheckBox_Exp2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(34, 34, 34))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jFormattedTextField_LT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jFormattedTextField_LTMin1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jFormattedTextField_LT2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jFormattedTextField_LTMin2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10)))
                            .addComponent(jLabel3))
                        .addGap(5, 5, 5)))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jFormattedTextField_Range1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jFormattedTextField_Range2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_CANCEL)
                    .addComponent(jButton_OK))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox_Exp2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox_Exp2ActionPerformed
        boolean b = jCheckBox_Exp2.isSelected();
        jFormattedTextField_LT2.setEnabled(b);
        jFormattedTextField_LTMin2.setEnabled(b);
    }//GEN-LAST:event_jCheckBox_Exp2ActionPerformed

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

    private void jFormattedTextField_Range1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFormattedTextField_Range1PropertyChange
        Object val1 = jFormattedTextField_Range1.getValue();
        Object val2 = jFormattedTextField_Range2.getValue();
        if ((parent != null) && (val1 instanceof Double) && (val2 instanceof Double))
            parent.updateCursor(((Double)val1).doubleValue(), ((Double)val2).doubleValue());
    }//GEN-LAST:event_jFormattedTextField_Range1PropertyChange

    private void jFormattedTextField_Range2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFormattedTextField_Range2PropertyChange
        Object val1 = jFormattedTextField_Range1.getValue();
        Object val2 = jFormattedTextField_Range2.getValue();
        if ((parent != null) && (val1 instanceof Double) && (val2 instanceof Double))
            parent.updateCursor(((Double)val1).doubleValue(), ((Double)val2).doubleValue());
    }//GEN-LAST:event_jFormattedTextField_Range2PropertyChange

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        setLocationRelativeTo(parent);
        retval = ID_CANCEL;
        Object val1 = jFormattedTextField_Range1.getValue();
        Object val2 = jFormattedTextField_Range2.getValue();
        parent.updateCursor(((Double)val1).doubleValue(), ((Double)val2).doubleValue());
    }//GEN-LAST:event_formWindowActivated

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JA_FitDialog dialog = new JA_FitDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jButton_CANCEL;
    private javax.swing.JButton jButton_OK;
    private javax.swing.JCheckBox jCheckBox_Exp2;
    private javax.swing.JComboBox jComboBox_Curve;
    private javax.swing.JFormattedTextField jFormattedTextField_LT1;
    private javax.swing.JFormattedTextField jFormattedTextField_LT2;
    private javax.swing.JFormattedTextField jFormattedTextField_LTMin1;
    private javax.swing.JFormattedTextField jFormattedTextField_LTMin2;
    private javax.swing.JFormattedTextField jFormattedTextField_Range1;
    private javax.swing.JFormattedTextField jFormattedTextField_Range2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    // End of variables declaration//GEN-END:variables

}
