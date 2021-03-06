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
public class JA_BunchDialog extends javax.swing.JDialog {

    public static final int ID_OK = 0;
    public static final int ID_CANCEL = 99;
    
    private int retval;
    private JA_Main parent = null;

    /** Creates new form JA_FitDialog */
    public JA_BunchDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        if (parent instanceof JA_Main)
            this.parent = (JA_Main)parent;
        retval = -1;
        initComponents();
        setLocationRelativeTo(null);

        DecimalFormat df;
        NumberFormatter nf2;

        df = new DecimalFormat();
        df.setMaximumIntegerDigits(5);
        df.setMaximumFractionDigits(3);
        df.setGroupingUsed(false);
        nf2 = new JA_NumberFormatter(df);
        nf2.setValueClass(Integer.class);
        nf2.setMinimum(new Integer(0));
        nf2.setMaximum(new Integer(99999));

        jFormattedTextField_Bunch1.setFormatterFactory(new DefaultFormatterFactory(nf2));
        jFormattedTextField_Bunch2.setFormatterFactory(new DefaultFormatterFactory(nf2));

        jFormattedTextField_Bunch1.setValue(new Integer(1));
        jFormattedTextField_Bunch2.setValue(new Integer(10));

        jRadioButton_Del.setSelected(true);
    }

    public void setRange(int curr, int max) {
        DecimalFormat df;
        NumberFormatter nf2;

        df = new DecimalFormat();
        df.setMaximumIntegerDigits(5);
        df.setMaximumFractionDigits(3);
        df.setGroupingUsed(false);
        nf2 = new JA_NumberFormatter(df);
        nf2.setValueClass(Integer.class);
        nf2.setMinimum(new Integer(1));
        nf2.setMaximum(new Integer(max));

        jFormattedTextField_Bunch1.setFormatterFactory(new DefaultFormatterFactory(nf2));
        jFormattedTextField_Bunch2.setFormatterFactory(new DefaultFormatterFactory(nf2));

        jFormattedTextField_Bunch1.setValue(new Integer(curr));
        jFormattedTextField_Bunch2.setValue(new Integer(curr));
    }

    public int getBunch1() {
        return ((Integer)jFormattedTextField_Bunch1.getValue()).intValue();
    }

    public int getBunch2() {
        return ((Integer)jFormattedTextField_Bunch2.getValue()).intValue();
    }

    public boolean isDelAction() {
        return jRadioButton_Del.isSelected();
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
        jLabel9 = new javax.swing.JLabel();
        jFormattedTextField_Bunch1 = new javax.swing.JFormattedTextField();
        jFormattedTextField_Bunch2 = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        jButton_CANCEL = new javax.swing.JButton();
        jButton_OK = new javax.swing.JButton();
        jRadioButton_Del = new javax.swing.JRadioButton();
        jRadioButton_UnDel = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Time Trace");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setText("  to");

        jFormattedTextField_Bunch1.setText("0");

        jFormattedTextField_Bunch2.setText("inf");
        jFormattedTextField_Bunch2.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                jFormattedTextField_Bunch2VetoableChange(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setText("Action:");

        jButton_CANCEL.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton_CANCEL.setText("Cancel");
        jButton_CANCEL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_CANCELActionPerformed(evt);
            }
        });

        jButton_OK.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton_OK.setText("OK");
        jButton_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_OKActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton_Del);
        jRadioButton_Del.setText("Delete Bunches");
        jRadioButton_Del.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_DelActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton_UnDel);
        jRadioButton_UnDel.setText("Undelete Bunches");
        jRadioButton_UnDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_UnDelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton_Del)
                            .addComponent(jLabel13)
                            .addComponent(jButton_CANCEL))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jRadioButton_UnDel)
                            .addComponent(jButton_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(jFormattedTextField_Bunch1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jFormattedTextField_Bunch2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton_Del)
                    .addComponent(jRadioButton_UnDel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField_Bunch1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jFormattedTextField_Bunch2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_CANCEL)
                    .addComponent(jButton_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

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

        /*jFormattedTextField_TimeWnd1.setEnabled(false);
        jFormattedTextField_TimeWnd2.setEnabled(false);
        jCheckBox_TimeWnd.setSelected(false);

        jFormattedTextField_TimeWnd1.setValue(new Double(0));
        jFormattedTextField_TimeWnd2.setValue(Double.POSITIVE_INFINITY);*/
    }//GEN-LAST:event_formWindowActivated

    private void jFormattedTextField_Bunch2VetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_jFormattedTextField_Bunch2VetoableChange
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(this, evt);
    }//GEN-LAST:event_jFormattedTextField_Bunch2VetoableChange

    private void jRadioButton_DelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_DelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton_DelActionPerformed

    private void jRadioButton_UnDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_UnDelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton_UnDelActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JA_BunchDialog dialog = new JA_BunchDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jButton_CANCEL;
    private javax.swing.JButton jButton_OK;
    private javax.swing.JFormattedTextField jFormattedTextField_Bunch1;
    private javax.swing.JFormattedTextField jFormattedTextField_Bunch2;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JRadioButton jRadioButton_Del;
    private javax.swing.JRadioButton jRadioButton_UnDel;
    // End of variables declaration//GEN-END:variables

}
