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
public class JA_FCSDialog extends javax.swing.JDialog {

    public static final int ID_OK = 0;
    public static final int ID_CANCEL = 99;
    
    private static final int DIST_640 = 450;
    private static final int DIST_532 = 450;
    private static final int DIST_470 = 432;

    private int retval;
    private JA_Main parent = null;

    /** Creates new form JA_FitDialog */
    public JA_FCSDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        if (parent instanceof JA_Main)
            this.parent = (JA_Main)parent;

        retval = -1;
        initComponents();
        setLocationRelativeTo(null);

        DecimalFormat df;
        NumberFormatter nf, nf2;

        df = new DecimalFormat();
        df.setMaximumIntegerDigits(3);
        df.setMaximumFractionDigits(3);
        df.setGroupingUsed(false);
        nf = new NumberFormatter(df);
        nf.setValueClass(Double.class);
        nf.setMinimum(new Double(1));
        nf.setMaximum(new Double(100));

        jFormattedTextField_MaxCorr.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_MaxCorr.setValue(new Double(3));

        nf = new NumberFormatter(df);
        nf.setValueClass(Integer.class);
        nf.setMinimum(new Integer(1));
        nf.setMaximum(new Integer(100));
        jFormattedTextField_NSub.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_NSub.setValue(new Integer(10));

        df = new JA_NumberFormat();
        df.setMaximumFractionDigits(15);
        df.setMaximumIntegerDigits(1);
        df.setGroupingUsed(false);
        nf2 = new JA_NumberFormatter(df);
        nf2.setValueClass(Double.class);
        nf2.setMinimum(new Double(1e5));
        nf2.setMaximum(new Double(1e10));

        jFormattedTextField_Photons.setFormatterFactory(new DefaultFormatterFactory(nf2));
        jFormattedTextField_Photons.setValue(new Double(1e6));

        df = new DecimalFormat();
        df.setMaximumIntegerDigits(3);
        df.setMaximumFractionDigits(3);
        df.setGroupingUsed(false);
        nf = new NumberFormatter(df);
        nf.setValueClass(Double.class);
        nf.setMinimum(new Double(0));
        nf.setMaximum(new Double(100));

        jFormattedTextField_Cutoff.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_Cutoff.setValue(new Double(0));

        jFormattedTextField_Temperature.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_Temperature.setValue(new Double(22));

        df = new DecimalFormat();
        df.setMaximumIntegerDigits(3);
        df.setMaximumFractionDigits(0);
        df.setGroupingUsed(false);
        nf = new NumberFormatter(df);
        nf.setValueClass(Double.class);
        nf.setMinimum(new Double(300));
        nf.setMaximum(new Double(900));

        jFormattedTextField_Em1.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_Em1.setValue(new Double(520));
        jFormattedTextField_Dist1.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_Dist1.setValue(new Double(DIST_470));
        jFormattedTextField_Em2.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_Em2.setValue(new Double(670));
        jFormattedTextField_Dist2.setFormatterFactory(new DefaultFormatterFactory(nf));
        jFormattedTextField_Dist2.setValue(new Double(DIST_640));

        jRadioButton_2Laser.setSelected(true);
        updateLaserType(2);
        jCheckBox_Cluster.setSelected(false);
    }

    public String getMaxCorr() {
        return "[" + jFormattedTextField_MaxCorr.getValue().toString() + ", " + jFormattedTextField_NSub.getValue().toString()  + "]";
    }

    public double getPhotons() {
        return (Double)jFormattedTextField_Photons.getValue();
    }

    public int getLasers() {
        if (jRadioButton_1Laser.isSelected())
                return 1;
        return (jRadioButton_2Laser.isSelected()) ? 2 : 4;
    }

    public double getCutoff() {
        return (Double)jFormattedTextField_Cutoff.getValue();
    }

    public boolean getClusterCalc() {
        return jCheckBox_Cluster.isSelected();
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

    public String getMetadata() {
        String met = "";

        met += Integer.toString(getLasers()) + ", ";

        met += jComboBox_Ex1.getSelectedItem().toString();
        if (jRadioButton_4Laser.isSelected())
            met += ", " + jComboBox_Ex2.getSelectedItem().toString();

        met += ", " + jFormattedTextField_Em1.getValue().toString();
        if (jRadioButton_4Laser.isSelected())
            met += ", " + jFormattedTextField_Em2.getValue().toString();

        if (jRadioButton_1Laser.isSelected()) {
            met += ", 0";
        }
        else
            met += ", " + jFormattedTextField_Dist1.getValue().toString();
        if (jRadioButton_4Laser.isSelected())
            met += ", " + jFormattedTextField_Dist2.getValue().toString();

        met += ", " + jComboBox_Pinhole.getSelectedItem().toString();
        met += ", " + jFormattedTextField_Temperature.getValue().toString();

        return met;
    }

    public void setPreset(int laser, int num) {
        if (laser == 1) {
            switch(num) {
                case 1:
                    // blue
                    jFormattedTextField_Em1.setValue(new Double(520));
                    jFormattedTextField_Dist1.setValue(new Double(DIST_470));
                    break;
                case 2:
                    // green
                    jFormattedTextField_Em1.setValue(new Double(560));
                    jFormattedTextField_Dist1.setValue(new Double(DIST_532));
                    break;
                case 3:
                default:
                    // red
                    jFormattedTextField_Em1.setValue(new Double(670));
                    jFormattedTextField_Dist1.setValue(new Double(DIST_640));
                    break;
            }
        }
        if (laser == 2) {
            switch(num) {
                case 1:
                    // blue
                    jFormattedTextField_Em2.setValue(new Double(520));
                    jFormattedTextField_Dist2.setValue(new Double(DIST_470));
                    break;
                case 2:
                    // green
                    jFormattedTextField_Em2.setValue(new Double(560));
                    jFormattedTextField_Dist2.setValue(new Double(DIST_532));
                    break;
                case 3:
                default:
                    // red
                    jFormattedTextField_Em2.setValue(new Double(670));
                    jFormattedTextField_Dist2.setValue(new Double(DIST_640));
                    break;
            }
        }
    }

    public void updateLaserType(int i) {
        jLabel20.setVisible(i>2);
        jLabel16.setVisible(i>2);
        jLabel17.setVisible(i>2);
        jLabel18.setVisible(i>2);
        jComboBox_Ex2.setVisible(i>2);
        jFormattedTextField_Em2.setVisible(i>2);
        jFormattedTextField_Dist2.setVisible(i>2);

        jLabel23.setVisible(i>1);
        jLabel15.setVisible(i>1);
        jFormattedTextField_Dist1.setVisible(i>1);
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
        jLabel1 = new javax.swing.JLabel();
        jButton_CANCEL = new javax.swing.JButton();
        jButton_OK = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jCheckBox_Cluster = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jFormattedTextField_Cutoff = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jFormattedTextField_Photons = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        jRadioButton_4Laser = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jFormattedTextField_MaxCorr = new javax.swing.JFormattedTextField();
        jRadioButton_2Laser = new javax.swing.JRadioButton();
        jLabel11 = new javax.swing.JLabel();
        jRadioButton_1Laser = new javax.swing.JRadioButton();
        jLabel24 = new javax.swing.JLabel();
        jFormattedTextField_NSub = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jFormattedTextField_Temperature = new javax.swing.JFormattedTextField();
        jComboBox_Pinhole = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jFormattedTextField_Dist1 = new javax.swing.JFormattedTextField();
        jFormattedTextField_Em1 = new javax.swing.JFormattedTextField();
        jComboBox_Ex1 = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        jComboBox_Ex2 = new javax.swing.JComboBox();
        jFormattedTextField_Em2 = new javax.swing.JFormattedTextField();
        jFormattedTextField_Dist2 = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Two Focus FCS");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel1.setText("Two Focus FCS Parameters");

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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel6.setText("ns");

        jCheckBox_Cluster.setText("use Cluster");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel5.setText("Cutoff:");

        jFormattedTextField_Cutoff.setText("0");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel3.setText("Photons:");

        jFormattedTextField_Photons.setText("1e6");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel2.setText("Max Correlation Time:");

        buttonGroup1.add(jRadioButton_4Laser);
        jRadioButton_4Laser.setText("4 Lasers");
        jRadioButton_4Laser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_4LaserActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel4.setText("s");

        jFormattedTextField_MaxCorr.setText("3");

        buttonGroup1.add(jRadioButton_2Laser);
        jRadioButton_2Laser.setText("2 Lasers");
        jRadioButton_2Laser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_2LaserActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel11.setText("Laser Pulses:");

        buttonGroup1.add(jRadioButton_1Laser);
        jRadioButton_1Laser.setText("1 Laser");
        jRadioButton_1Laser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_1LaserActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel24.setText("NSub:");

        jFormattedTextField_NSub.setText("0");
        jFormattedTextField_NSub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField_NSubActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel11))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton_1Laser)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jFormattedTextField_Photons, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jFormattedTextField_MaxCorr, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel4)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24)
                            .addComponent(jLabel5))
                        .addGap(107, 107, 107)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jFormattedTextField_Cutoff, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6))
                            .addComponent(jFormattedTextField_NSub, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox_Cluster)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRadioButton_2Laser)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButton_4Laser)))
                .addGap(68, 68, 68))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jRadioButton_1Laser)
                    .addComponent(jRadioButton_2Laser)
                    .addComponent(jRadioButton_4Laser))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFormattedTextField_MaxCorr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2)
                    .addComponent(jCheckBox_Cluster))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jFormattedTextField_Photons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jFormattedTextField_Cutoff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField_NSub))
                .addGap(13, 13, 13))
        );

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel7.setText("Measurement Metadata");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel8.setText("Pinhole:");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 20, 184, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel9.setText("Temperature:");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 79, 118, -1));

        jFormattedTextField_Temperature.setText("22");
        jFormattedTextField_Temperature.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField_TemperatureActionPerformed(evt);
            }
        });
        jPanel2.add(jFormattedTextField_Temperature, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 103, 50, -1));

        jComboBox_Pinhole.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "200", "150", "100", "75", "50" }));
        jComboBox_Pinhole.setSelectedIndex(1);
        jComboBox_Pinhole.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox_PinholeItemStateChanged(evt);
            }
        });
        jPanel2.add(jComboBox_Pinhole, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 46, 51, -1));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel10.setText("°C");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(66, 105, 64, -1));

        jFormattedTextField_Dist1.setText("432");
        jFormattedTextField_Dist1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField_Dist1ActionPerformed(evt);
            }
        });
        jPanel2.add(jFormattedTextField_Dist1, new org.netbeans.lib.awtextra.AbsoluteConstraints(202, 103, 50, -1));

        jFormattedTextField_Em1.setText("510");
        jFormattedTextField_Em1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField_Em1ActionPerformed(evt);
            }
        });
        jPanel2.add(jFormattedTextField_Em1, new org.netbeans.lib.awtextra.AbsoluteConstraints(202, 77, 50, -1));

        jComboBox_Ex1.setEditable(true);
        jComboBox_Ex1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "470", "532", "640" }));
        jComboBox_Ex1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox_Ex1ItemStateChanged(evt);
            }
        });
        jPanel2.add(jComboBox_Ex1, new org.netbeans.lib.awtextra.AbsoluteConstraints(202, 46, 50, -1));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel12.setText("µm");
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(67, 48, 63, -1));

        jComboBox_Ex2.setEditable(true);
        jComboBox_Ex2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "470", "532", "640" }));
        jComboBox_Ex2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox_Ex2ItemStateChanged(evt);
            }
        });
        jPanel2.add(jComboBox_Ex2, new org.netbeans.lib.awtextra.AbsoluteConstraints(316, 46, 50, -1));

        jFormattedTextField_Em2.setText("675");
        jFormattedTextField_Em2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField_Em2ActionPerformed(evt);
            }
        });
        jPanel2.add(jFormattedTextField_Em2, new org.netbeans.lib.awtextra.AbsoluteConstraints(316, 77, 50, -1));

        jFormattedTextField_Dist2.setText("470");
        jFormattedTextField_Dist2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField_Dist2ActionPerformed(evt);
            }
        });
        jPanel2.add(jFormattedTextField_Dist2, new org.netbeans.lib.awtextra.AbsoluteConstraints(316, 103, 50, -1));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel13.setText("nm");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(256, 48, -1, -1));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel14.setText("nm");
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(256, 79, -1, -1));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel15.setText("nm");
        jPanel2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(256, 105, -1, -1));

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel16.setText("nm");
        jPanel2.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 48, -1, -1));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel17.setText("nm");
        jPanel2.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 79, -1, -1));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel18.setText("nm");
        jPanel2.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 105, -1, -1));

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel19.setText("Pulse 1+2");
        jPanel2.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(202, 20, -1, -1));

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel20.setText("Pulse 3+4");
        jPanel2.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(316, 20, -1, -1));

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel21.setText("Ex: ");
        jPanel2.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(136, 48, 60, -1));

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel22.setText("Em: ");
        jPanel2.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(136, 79, 60, -1));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel23.setText("Distance: ");
        jPanel2.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(136, 105, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jButton_CANCEL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 325, Short.MAX_VALUE)
                        .addComponent(jButton_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_CANCEL)
                    .addComponent(jButton_OK))
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
    }//GEN-LAST:event_formWindowActivated

    private void jComboBox_PinholeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox_PinholeItemStateChanged
        // TODO add your handling code here:
}//GEN-LAST:event_jComboBox_PinholeItemStateChanged

    private void jFormattedTextField_TemperatureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField_TemperatureActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextField_TemperatureActionPerformed

    private void jFormattedTextField_Dist1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField_Dist1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextField_Dist1ActionPerformed

    private void jFormattedTextField_Em1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField_Em1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextField_Em1ActionPerformed

    private void jComboBox_Ex1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox_Ex1ItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            int i = jComboBox_Ex1.getSelectedIndex();
            if (i >= 0)
                setPreset(1, i+1);
        }
    }//GEN-LAST:event_jComboBox_Ex1ItemStateChanged

    private void jComboBox_Ex2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox_Ex2ItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            int i = jComboBox_Ex2.getSelectedIndex();
            if (i >= 0)
                setPreset(2, i+1);
        }
    }//GEN-LAST:event_jComboBox_Ex2ItemStateChanged

    private void jFormattedTextField_Em2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField_Em2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextField_Em2ActionPerformed

    private void jFormattedTextField_Dist2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField_Dist2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextField_Dist2ActionPerformed

    private void jRadioButton_2LaserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_2LaserActionPerformed
        updateLaserType(2);
    }//GEN-LAST:event_jRadioButton_2LaserActionPerformed

    private void jRadioButton_4LaserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_4LaserActionPerformed
        updateLaserType(4);
    }//GEN-LAST:event_jRadioButton_4LaserActionPerformed

    private void jRadioButton_1LaserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_1LaserActionPerformed
        updateLaserType(1);
    }//GEN-LAST:event_jRadioButton_1LaserActionPerformed

    private void jFormattedTextField_NSubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField_NSubActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextField_NSubActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JA_FCSDialog dialog = new JA_FCSDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JCheckBox jCheckBox_Cluster;
    private javax.swing.JComboBox jComboBox_Ex1;
    private javax.swing.JComboBox jComboBox_Ex2;
    private javax.swing.JComboBox jComboBox_Pinhole;
    private javax.swing.JFormattedTextField jFormattedTextField_Cutoff;
    private javax.swing.JFormattedTextField jFormattedTextField_Dist1;
    private javax.swing.JFormattedTextField jFormattedTextField_Dist2;
    private javax.swing.JFormattedTextField jFormattedTextField_Em1;
    private javax.swing.JFormattedTextField jFormattedTextField_Em2;
    private javax.swing.JFormattedTextField jFormattedTextField_MaxCorr;
    private javax.swing.JFormattedTextField jFormattedTextField_NSub;
    private javax.swing.JFormattedTextField jFormattedTextField_Photons;
    private javax.swing.JFormattedTextField jFormattedTextField_Temperature;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton_1Laser;
    private javax.swing.JRadioButton jRadioButton_2Laser;
    private javax.swing.JRadioButton jRadioButton_4Laser;
    // End of variables declaration//GEN-END:variables

}
