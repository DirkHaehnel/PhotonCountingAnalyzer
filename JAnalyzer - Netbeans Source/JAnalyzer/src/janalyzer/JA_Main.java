/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JA_Main.java
 *
 * Created on 24.02.2010, 10:17:07
 */

package janalyzer;

import com.mathworks.jmi.*;
import com.mathworks.mde.cmdhist.*;

import java.text.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

import org.jfree.ui.RectangleEdge;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.*;
import org.jfree.data.Range;

/**
 *
 * @author cpieper
 */
public class JA_Main extends javax.swing.JFrame implements CompletionObserver, MouseListener, MouseMotionListener {

    private Matlab mat;
    private CmdHistory cmdHist;
    private File cd;
    private int lastAction;
    private ChartPanel chartPanel;
    private JFreeChart chart;
    private JA_Plot plot;
    private NumberAxis naxx,naxy;
    private DecimalFormat df;

    // loads
    private boolean iscalc_TCSPC = false;
    private boolean isload_Trace = false;
    private boolean iscalc_FCS = false;
    private boolean exists_FCS_Mat_File = false;
    private boolean multiple_FCS_Selected = false;

    // Dialogs
    JA_FitDialog fitDlg;
    JA_BaselineDlg blDlg;
    JA_FCSFitDialog fcsfitDlg;
    JA_FCSFitDialog_SingleFocus fcsfitDlgSF;
    JA_TraceDialog traceDlg;
    JA_FCSDialog fcsDlg;
    JA_AutoTimegateDialog timegateDlg;
    JA_BunchDialog bunchDlg;
    JA_BurstDialog burstDlg;

    private File ht3file;


    public double[]   fcstime;
    public double[][] tcspc;
    public double[][] fcstrace, fcstimetmp;
    public Integer[] tcspcsize, fcssize, dsize, fcstimesize;
    public double tcspc_delx;
    public Hashtable<String, Object> ht3header;
    public int snapedToCursor = -1;

    public int traceanzbunch = -1;
    public double tracedelta = 1;
    public int tracecurrbunch = -1;
    public int fcsanzbunch = -1;
    public int fcscurrbunch = -1;
    public int fcsanzcorrs = -1;
    public boolean[] fcsbunchdelete;

    public String nextCommand = "";
    public String nextMatfile = "";
    public String var2GetName;
    private String lastfcsfile = "";
    public int[] var2GetSize;

    public static final int JAA_STARTUP = -5;
    public static final int JAA_IDLE = 0;
    public static final int JAA_OTHER = 1;
    public static final int JAA_BATCHCALC = 3;
    public static final int JAA_GETPREFDIR = 5;
    public static final int JAA_READHEAD = 11;
    public static final int JAA_TRACE_READ = 41;
    public static final int JAA_TRACE_GETSIZE = 42;
    public static final int JAA_TRACE_SHOW = 43;
    public static final int JAA_TCSPC_RRHEAD = 50;
    public static final int JAA_TCSPC = 51;
    public static final int JAA_TCSPC_SHOW = 52;
    public static final int JAA_AUTOTIMEGATE = 54;
    public static final int JAA_AUTOTIMEGATE_SHOW = 55;
    public static final int JAA_BASELINE = 61;
    public static final int JAA_FIT = 71;
    public static final int JAA_FIT_RESULT = 72;
    public static final int JAA_2FOCUS2FCS_READHEAD = 91;
    public static final int JAA_2FOCUS2FCS_CALC = 92;
    public static final int JAA_2FOCUS2FCS_CROSSREAD = 93;
    public static final int JAA_2FOCUS2FCS_CROSSREADANZBUNCH = 94;
    public static final int JAA_2FOCUS2FCS_ISMETADATA = 97;
    public static final int JAA_2FOCUS2FCS_GETMETADATA = 98;
    public static final int JAA_2FOCUS2FCS_ISBUNCHED = 99;
    public static final int JAA_2FOCUS2FCS_GETBUNCHED = 100;

    public static final int JAA_2FOCUS2FCS_SHOWCROSS = 95;
    public static final int JAA_2FOCUS2FCS_GETCORRTIMES = 96;
    public static final int JAA_2FOCUS2FCS_FCSFIT = 111;
    public static final int JAA_GETVAL_TCSPC = 901;

    public static final int JAA_MASK_GET_SIZE = 0x10000;


    /** Creates new form JA_Main */
    public JA_Main() {
        this(null);
    }

    public JA_Main(String[] args) {
        // Set the default locale to custom locale
        Locale.setDefault(new Locale("en", "DE"));
        lastAction = JAA_STARTUP;

        mat = new Matlab();
        cmdHist = CmdHistory.getInstance();

        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        //Runtime rt = Runtime.getRuntime();
        //rt.totalMemory();
        //rt.freeMemory();

        initComponents();
        setLocationRelativeTo(null);
        blockFrame(true);

        if ((args != null) && (args.length >= 1)) {
            File tmpdir = new File(args[0]);
            if (tmpdir.isDirectory()) {
                cd = tmpdir;
                ((DefaultComboBoxModel)jComboBox_WorkDir.getModel()).addElement(cd);
                ((DefaultComboBoxModel)jComboBox_WorkDir.getModel()).setSelectedItem(cd);
                updateFileList();
            }
        }

        var2GetSize = new int[2];
        lastAction = JAA_GETPREFDIR;
        mat.eval("prefdir", this);

        // init Dialog
        fitDlg = new JA_FitDialog(this, true);
        blDlg = new JA_BaselineDlg(this, true);
        fcsfitDlg = new JA_FCSFitDialog(this, true);
        fcsfitDlgSF = new JA_FCSFitDialog_SingleFocus(this, true);
        traceDlg = new JA_TraceDialog(this, true);
        fcsDlg = new JA_FCSDialog(this, true);
        timegateDlg  = new JA_AutoTimegateDialog(this, true);
        bunchDlg = new JA_BunchDialog(this, true);
        burstDlg = new JA_BurstDialog(this, true);

        df = new JA_NumberFormat();
        df.setMaximumFractionDigits(3);
        df.setMaximumIntegerDigits(1);
        df.setGroupingUsed(false);

        DefaultXYItemRenderer render = new DefaultXYItemRenderer();
        Shape sh = new Polygon();
        render.setSeriesShape(0, sh);
        render.setSeriesShape(1, sh);
        render.setSeriesShape(2, sh);
        render.setSeriesShape(3, sh);
        render.setSeriesPaint(0, new java.awt.Color(255, 0, 0));
        render.setSeriesPaint(1, new java.awt.Color(0, 0, 255));
        render.setSeriesPaint(2, new java.awt.Color(0, 205, 0));
        render.setSeriesPaint(3, new java.awt.Color(125, 125, 0));
        //SamplingXYLineRenderer render = new SamplingXYLineRenderer();

        naxx = new NumberAxis("Channel");
        naxy = new NumberAxis("Counts");
        XYSeriesCollection ds = new XYSeriesCollection();
        
        //XYSeries set1 = new XYSeries("Cursor 1", false, true);
        //set1.add(20, 1);
        //set1.add(40, 100);
        //ds.addSeries(set1);
        plot = new JA_Plot(ds, naxx, naxy, render);
        //plot.addCursor(new JA_Cursor(30));
        chart = new JFreeChart(plot);
        chartPanel = new ChartPanel(chart);
        chartPanel.setMouseZoomable(false);
        chartPanel.addMouseListener(this);
        chartPanel.addMouseMotionListener(this);

        jPanel2.add(chartPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 105, 600, 400));

        this.setVisible(true);
    }

    private void blockFrame(boolean b) {
        // TODO Add New Buttons here:

        jButton_TCSPC.setEnabled(!b);
        jButton_PlotTCSPC.setEnabled(!b && iscalc_TCSPC);
        jButton_Fit.setEnabled(!b && iscalc_TCSPC);
        jButton_Baseline.setEnabled(!b && iscalc_TCSPC);
        jButton_AutoTimegate.setEnabled(!b && iscalc_TCSPC);

        jButton_Trace.setEnabled(!b);

        jScrollBar_TraceBunch.setEnabled(!b && isload_Trace && (traceanzbunch > 1));

        if (multiple_FCS_Selected) {
            jButton_TwoFocusFCS.setText("Batch Calc");
        }
        else {
            if (exists_FCS_Mat_File) {
                jButton_TwoFocusFCS.setText("Load FCS");
            }
            else
                jButton_TwoFocusFCS.setText("Calc FCS");
        }
        jButton_TwoFocusFCS.setEnabled(!b);
        jButton_TwoFocusFit.setEnabled(!b && iscalc_FCS);
        jButton_DelBunch.setEnabled(!b && iscalc_FCS);
        jButton_SaveBunch.setEnabled(!b && iscalc_FCS);
        jScrollBar_Bunch.setEnabled(!b && iscalc_FCS);
        jComboBox_Curves.setEnabled(!b && iscalc_FCS);
        jButton_PlotCorrs.setEnabled(!b && iscalc_FCS);

        jList_Files.setEnabled(!b);
    }

    private void updateFileList() {
        if (cd.isDirectory()) {
            // find Files
            ht3header = null;
            jTree_FileHeader.setModel(null);
            jList_Files.setSelectedIndex(-1);
            File[] files = cd.listFiles(new ht3FileFilter());
            JA_SortedList dlm = new JA_SortedList();
            for (int i = 0; i < files.length; i++) {
                dlm.add(files[i].getName());
            }
            jList_Files.setModel(dlm);
        }
    }

    public long checkRange(long x, long min, long max) {
        if (x < min)
            return min;
        if (x > max)
            return max;
        return x;
    }

    public void mouseClicked(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        int x = e.getX();
        int y = e.getY();
        double[] cpos = plot.getCursorPositions();
        Rectangle2D dataArea = plot.getLastDataArea();
        if (dataArea != null) {
            for (int i = 0; i < cpos.length; i++) {
                double cursx = naxx.valueToJava2D(cpos[i], dataArea,
                                RectangleEdge.TOP);
                double dist = Math.abs(cursx - x);
                if (dist < 5) {
                    // Snap on Cursor
                    snapedToCursor = i;
                }
            }
            plot.setSnapedToCursor(snapedToCursor);
            if (snapedToCursor != -1)
                plot.changed();
        }
    }

    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        if (snapedToCursor != -1)
            plot.changed();
        snapedToCursor = -1;
        plot.setSnapedToCursor(-1);
    }

    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseDragged(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        int x = e.getX();
        int y = e.getY();
        Rectangle2D dataArea = plot.getLastDataArea();
        if (dataArea != null) {
            double xx = naxx.java2DToValue(x, dataArea,
                            RectangleEdge.TOP);
            jTextPane1.setText("X: " + x + "\nY: " + y+ "\nX-Val: " + xx);
            if ((snapedToCursor != -1) && (naxx.getRange().contains(xx))) {
                plot.setCursorPos(snapedToCursor, xx);
                plot.changed();
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public class ht3FileFilter implements FileFilter {

        public ht3FileFilter() {
        }

        public boolean accept(File pathname) {
            return pathname.getName().toLowerCase().endsWith(".ht3") || pathname.getName().toLowerCase().endsWith(".pt3");
        }
    }

    public void readCWDHist(String path) {
        File configFile = new File(path + File.separator + "cwdhistory.m");
        DefaultComboBoxModel dcbm = ((DefaultComboBoxModel)jComboBox_WorkDir.getModel());

        if (configFile.exists()) {
            try {
                //use buffering, reading one line at a time
                //FileReader always assumes default encoding is OK!
                BufferedReader input =  new BufferedReader(new FileReader(configFile));
                String groupStr = null;
                try {
                    String line = null; //not declared within while loop
                    while (( line = input.readLine()) != null){
                        // Process Config File
                        if (line.substring(0, 3).matches("[A-Za-z]:[\\\\/]")) {
                            File tmp = new File(line);
                            if (tmp.isDirectory())
                                dcbm.addElement(tmp);
                        }
                    }
                }
                finally {
                    input.close();
                }
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
        else {
            //JOptionPane.showMessageDialog(null, "Error finding cwdhistory.m", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void completed(int v, Object o) {
        try {
        switch (lastAction) {
            case JAA_GETPREFDIR:
                lastAction = JAA_IDLE;
                if (o instanceof String) {
                    String s = (String)o;
                    s = s.replaceFirst("[a-zA-Z0-9]+ =", "");
                    s = s.replaceAll("[\n\r]", "");
                    readCWDHist(s);
                }
                blockFrame(false);
                break;
            case JAA_BATCHCALC:
                lastAction = JAA_IDLE;
                blockFrame(false);
                break;
            case JAA_READHEAD:
                ht3header = Struct2HashTable(o);
                jTree_FileHeader.setModel(HashTable2TreeModel(ht3header));
                lastAction = JAA_IDLE;
                blockFrame(false);
                break;
            case JAA_GETVAL_TCSPC:
            case JAA_TCSPC_SHOW:
                //JOptionPane.showMessageDialog(this, o);
                
                tcspcsize = new Integer[2];
                tcspc = Obj2DArray(o, tcspcsize);
                String blub = "TCPSC:\nSize " + tcspcsize[0].toString() + " " + tcspcsize[1].toString() + "\n";
                blub += tcspc[0][0] + " " + tcspc[0][1];
                jTextPane1.setText(blub);
                tcspc_delx = 1;

                if (ht3header != null) {
                    if (ht3header.containsKey("Resolution")) {
                        double res_ns = parse(ht3header.get("Resolution"));
                        if ((res_ns == 0) || (res_ns == Double.NaN)) {
                            updatePlot(tcspc, tcspcsize, new String[] {"Detector 1", "Detector 2", "Detector 3", "Detector 4"}, "Channel", "Counts", true);
                        }
                        else {
                            tcspc_delx = res_ns;
                            updatePlot(tcspc, res_ns, tcspcsize, new String[] {"Detector 1", "Detector 2", "Detector 3", "Detector 4"}, "Time (ns)", "Counts", true);
                        }
                    }
                    else {
                        updatePlot(tcspc, tcspcsize, new String[] {"Detector 1", "Detector 2", "Detector 3", "Detector 4"}, "Channel", "Counts", true);
                    }
                }
                else {
                    updatePlot(tcspc, tcspcsize, new String[] {"Detector 1", "Detector 2", "Detector 3", "Detector 4"}, "Channel", "Counts", true);
                }

                lastAction = JAA_IDLE;
                blockFrame(false);
                break;
            case JAA_TCSPC_RRHEAD:
                if ((ht3file != null ) && (ht3file.exists())) {
                    ht3header = Struct2HashTable(o);
                    jTree_FileHeader.setModel(HashTable2TreeModel(ht3header));
                    lastAction = JAA_TCSPC;
                    mat.evalConsoleOutput("[bin, tcspcdata, head] = ReadTCSPC('" + ht3file.getAbsolutePath() +"');", this);
                }
                break;
            case JAA_TCSPC:
                // continue showing TCSPC
                lastAction = JAA_GETVAL_TCSPC;
                getMatlabVariable("tcspcdata");
                break;
            case JAA_AUTOTIMEGATE:
                lastAction = JAA_AUTOTIMEGATE_SHOW;
                getMatlabVariable("t1");
                break;
            case JAA_AUTOTIMEGATE_SHOW:
                Integer[] tgsize = new Integer[2];
                double[][] timegates = Obj2DArray(o, tgsize);
                double cutoff = timegateDlg.getCutoff();
                int tgates = tgsize[1].intValue();

                String blub2 = tgsize[1].toString() + " Timegates found:\n";
                int cc = plot.getCursorCount();
                for (int ii = cc; ii > tgates; ii--) {
                    plot.removeCursor(ii-1);
                }
                double res_ns = 0.004;
                if (ht3header.containsKey("Resolution")) {
                    res_ns = parse(ht3header.get("Resolution"));
                }
                for (int ii = 0; ii < tgates; ii++) {
                    double thistg = (timegates[0][ii] * res_ns) + cutoff;
                    blub2 +=  thistg + " ";
                    plot.setCursorPos(ii, thistg);
                }
                jTextPane1.setText(blub2);           
                plot.changed();

                lastAction = JAA_IDLE;
                blockFrame(false);
                break;
            case JAA_TRACE_READ:
                // get Trace Size
                lastAction = JAA_TRACE_GETSIZE;
                mat.eval("size(trace, 1)", this);
                break;
            case JAA_TRACE_GETSIZE:
                // continue showing TRACE
                Integer[] bunchsizet = new Integer[2];
                double[][] anzbuncht = Obj2DArray(o, bunchsizet);
                if (bunchsizet[0] == 1) {
                    traceanzbunch = (int)Math.ceil(anzbuncht[0][0] / 1000);
                    tracecurrbunch = 1;
                    jLabel_TraceBunch.setText("1 of " + traceanzbunch);
                    jScrollBar_TraceBunch.setModel(new DefaultBoundedRangeModel(1,0,1,traceanzbunch));
                    lastAction = JAA_TRACE_SHOW;
                    if (traceanzbunch > 1) {
                        getMatlabVariable("trace(1:1000,:)");
                    }
                    else {
                        getMatlabVariable("trace");
                    }
                    isload_Trace = true;
                }
                else {
                    isload_Trace = false;
                    tracecurrbunch = -1;
                    lastAction = JAA_IDLE;
                    blockFrame(false);
                }
                break;
            case JAA_TRACE_SHOW:
                Integer[] tracesize = new Integer[2];
                try {
                    double[][] trace = Obj2DArray(o, tracesize);
                    //if (tracesize[0] == 4)
                    //   tracesize[0] = 2;
                    //updatePlot(double[][] data, double offsetX, double deltaX, Integer[] dim, String[] Names, String xaxs, String yaxs, boolean logy)
                    double offset = (tracecurrbunch - 1) * tracedelta;
                    updatePlot(trace, offset, tracedelta / 1000, tracesize, new String[] {"Detector 1", "Detector 2", "Detector 3", "Detector 4"}, "Time (s)", "Counts", false);

                    lastAction = JAA_IDLE;
                    blockFrame(false);
                }
                catch (Exception e){
                    JOptionPane.showMessageDialog(this, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                    isload_Trace = false;
                    tracecurrbunch = -1;
                    lastAction = JAA_IDLE;
                    blockFrame(false);
                }
                break;
            case JAA_FIT:
                lastAction = JAA_FIT_RESULT;
                mat.eval("x", this);
                break;
            case JAA_FIT_RESULT:
                dsize = new Integer[2];
                double[][] dtmp = Obj2DArray(o, dsize);
                String ret = "";
                for (int k = 0; k < dsize[1]; k++) {
                    ret += "Lifetime " + (k+1) + ": " + dtmp[0][k] + " ns\n";
                }
                //jTextPane1.setText("d len: " + dtmp.length + "\nsize len: " + dsize.length);
                jTextPane1.setText(ret);
                lastAction = JAA_IDLE;
                blockFrame(false);
                break;
            case JAA_BASELINE:
                // reload TCSPC
                lastAction = JAA_GETVAL_TCSPC;
                getMatlabVariable("tcspcdata");
                break;
            case JAA_2FOCUS2FCS_READHEAD:
                if ((ht3file != null ) && (ht3file.exists())) {
                    ht3header = Struct2HashTable(o);
                    jTree_FileHeader.setModel(HashTable2TreeModel(ht3header));
                    lastAction = JAA_2FOCUS2FCS_ISMETADATA;
                    mat.evalConsoleOutput(nextCommand, this);
                    nextCommand = "";
                }
                break;
            case JAA_2FOCUS2FCS_CALC:
                lastAction = JAA_2FOCUS2FCS_ISMETADATA;
                mat.evalConsoleOutput("[y, t] = FCSCrossRead(res);", this);
                break;
            case JAA_2FOCUS2FCS_ISMETADATA:
                lastAction = JAA_2FOCUS2FCS_GETMETADATA;
                iscalc_FCS = true;
                mat.eval("isfield(res, 'metadata')", this);
                break;
            case JAA_2FOCUS2FCS_GETMETADATA:
                String sm = o.toString();
                sm = sm.replaceFirst("[a-zA-Z0-9]+ =", "");
                sm = sm.replaceAll("[\n ]+", "");

                if (Integer.parseInt(sm) == 1) {
                    lastAction = JAA_2FOCUS2FCS_CROSSREAD;
                    mat.feval("eval", new Object[] {"res.metadata"}, 1, this);
                    //mat.eval("res.metadata", this);
                }
                else {
                    lastAction = JAA_2FOCUS2FCS_CROSSREADANZBUNCH;
                    fcsfitDlg.metadata = null;
                    mat.eval("size(y)", this);
                }
                break;
            case JAA_2FOCUS2FCS_CROSSREAD:
                fcsfitDlg.metadata = Struct2HashTable(o);
                fcsfitDlgSF.metadata = Struct2HashTable(o);
                //JOptionPane.showMessageDialog(this, metadata.keys().nextElement());

                lastAction = JAA_2FOCUS2FCS_CROSSREADANZBUNCH;
                mat.eval("size(y)", this);
                break;
            case JAA_2FOCUS2FCS_CROSSREADANZBUNCH:
                Integer[] bunchsize = new Integer[2];
                double[][] anzbunch = Obj2DArray(o, bunchsize);
                if ((bunchsize[0] == 3) || (bunchsize[0] == 2)) {
                    fcsanzbunch = 1;
                    if (bunchsize[0] == 3)
                        fcsanzbunch = (int)anzbunch[2][0];
                    fcsanzcorrs = (int)anzbunch[1][0];
                    fcscurrbunch = 1;
                    fcsbunchdelete = new boolean[fcsanzbunch];
                    jLabel_Bunch.setText("1 of " + fcsanzbunch);
                    jScrollBar_Bunch.setModel(new DefaultBoundedRangeModel(1,0,1,fcsanzbunch));

                    int j = 0;
                    while (((fcsanzcorrs - j * 4) % 6 != 0) && (j < 2)) {
                        j++;
                    }
                    if (j == 3)
                        j = 0;

                    DefaultComboBoxModel amod = new DefaultComboBoxModel();
                    if (fcsanzcorrs <= 2) {
                        for (int i = 1; i <= fcsanzcorrs; i++)
                            amod.addElement(new JA_CurveSet(i, i));
                    } else {
                        int sets = (int)Math.floor((fcsanzcorrs - j * 4) / 6);
                        int bla = 1;
                        for (int i = 0; i < sets; i++) {
                            amod.addElement(new JA_CurveSet(bla, bla+5));
                            bla += 6;
                        }
                        for (int i = 0; i < j; i++) {
                            amod.addElement(new JA_CurveSet(bla, bla+3));
                            bla += 4;
                        }
                        if(bla < fcsanzcorrs) {
                            amod.addElement(new JA_CurveSet(bla, fcsanzcorrs));
                        }
                    }
                    
                    jComboBox_Curves.setModel(amod);
                    jComboBox_Curves.setSelectedIndex(0);
                    fcsfitDlg.setCurveModel(amod);
                    fcsfitDlgSF.setCurveModel(amod);
                    fcsfitDlg.setBunchs(fcsanzbunch);
                    
                    lastAction = JAA_2FOCUS2FCS_GETCORRTIMES;
                    getMatlabVariable("t(:)");
                }
                else {
                    iscalc_FCS = false;
                    fcscurrbunch = -1;
                    lastAction = JAA_IDLE;
                    blockFrame(false);
                }
                break;
            case JAA_2FOCUS2FCS_GETCORRTIMES:
                //setBinTimes(double[] bintimes, int anzbins) {
                fcstimesize = new Integer[3];
                try {
                    fcstimetmp = Obj2DArray(o, fcstimesize);
                    int s = (fcstimesize[1]).intValue();
                    fcstime = new double[s];
                    for (int i = 0; i < s; i++) {
                        //int exp = Math.getExponent(fcstimetmp[0][i]);
                        //double mult = Math.pow(10, exp-2);
                        //fcstime[i] = Math.round(fcstimetmp[0][i]/mult)*mult;
                        fcstime[i] = fcstimetmp[0][i];
                    }
                    fcsfitDlg.setBinTimes(fcstime, s);
                    fcsfitDlgSF.setBinTimes(fcstime, s);

                    lastAction = JAA_2FOCUS2FCS_ISBUNCHED;
                    mat.eval("Stemp = load('" + nextMatfile + "');isfield(Stemp, 'UsedBunchs')", this);
                }
                catch (Exception e){
                    StackTraceElement[] st = e.getStackTrace();
                    String estr = e.toString();
                    for (int i = 0; i < st.length; i++) {
                        estr += "\r\n" + st[i].toString();
                    }
                    JOptionPane.showMessageDialog(this, estr, "Error in JAA_2FOCUS2FCS_GETCORRTIMES", JOptionPane.ERROR_MESSAGE);
                    iscalc_FCS = false;
                    fcscurrbunch = -1;
                    lastAction = JAA_IDLE;
                    blockFrame(false);
                }
                break;
            case JAA_2FOCUS2FCS_ISBUNCHED:
                String sub = o.toString();
                sub = sub.replaceFirst("[a-zA-Z0-9]+ =", "");
                sub = sub.replaceAll("[\n ]+", "");

                if (Integer.parseInt(sub) == 1) {
                    lastAction = JAA_2FOCUS2FCS_GETBUNCHED;
                    getMatlabVariable("Stemp.UsedBunchs");
                }
                else {
                    lastAction = JAA_2FOCUS2FCS_SHOWCROSS;
                    getMatlabVariable("y(:,:,1)");
                }


                break;
            case JAA_2FOCUS2FCS_GETBUNCHED:
                Integer[] bunches = new Integer[3];
                try {
                    double[][] activbunches = Obj2DArray(o, bunches);
                    for (int i = 0; i < fcsanzbunch; i++)
                        fcsbunchdelete[i] = true;
                    for (int i = 0; i < bunches[1]; i++) {
                        int val = (int)(activbunches[0][i] - 1);
                        if (val < fcsanzbunch)
                            fcsbunchdelete[val] = false;
                    }

                    UpdateDelButton();
                    //JOptionPane.showMessageDialog(this, "Anz: " + bunches[0] + ", " + bunches[1] + ", " + bunches[2]);

                    lastAction = JAA_2FOCUS2FCS_SHOWCROSS;
                    getMatlabVariable("y(:,:,1)");
                }
                catch (Exception e){
                    JOptionPane.showMessageDialog(this, e.toString(), "Error in JAA_2FOCUS2FCS_GETBUNCHED", JOptionPane.ERROR_MESSAGE);
                    iscalc_FCS = false;
                    fcscurrbunch = -1;
                    lastAction = JAA_IDLE;
                    blockFrame(false);
                }
                break;
            case JAA_2FOCUS2FCS_SHOWCROSS:
                fcssize = new Integer[3];
                try {
                    fcstrace = Obj2DArray(o, fcssize);

                    UpdateDelButton();
                    fcssize[2] = fcsfitDlg.getFirstCurve() - 1;
                    if (fcssize[0] <= 2)    // 1 Laser FCS
                        fcssize[0] = 1;

                    updatePlot(fcstrace, fcstime, fcssize, new String[] {"Corr " + fcssize[2], "Corr " + (fcssize[2] + 1), "Corr " + (fcssize[2] + 2), "Corr " + (fcssize[2] + 3)}, "Time", "Correlation", false, true);

                    lastAction = JAA_IDLE;
                    blockFrame(false);
                }
                catch (Exception e){
                    JOptionPane.showMessageDialog(this, e.toString(), "Error in JAA_2FOCUS2FCS_SHOWCROSS", JOptionPane.ERROR_MESSAGE);
                    iscalc_FCS = false;
                    fcscurrbunch = -1;
                    lastAction = JAA_IDLE;
                    blockFrame(false);
                }
                break;
            default:
                if ((lastAction & JAA_MASK_GET_SIZE) > 0) {
                    for (int i = 0; i < 2; i++)
                        var2GetSize[i] = 1;
                    if (o instanceof double[]) {
                        double[] d = (double[])o;
                        int j = 0;
                        for (int i = 0; (i < d.length) && (j < 2); i++) {
                            if (d[i] > 1) {
                                var2GetSize[j] = (int)Math.round(d[i]);
                                j++;
                            }
                        }
                    }
                    else {
                        Integer[] anz = new Integer[2];
                        double[][] sizes = Obj2DArray(o, anz);
                        int j = 0;
                        for (int i = 0; (i < anz[0]) && (j < 2); i++) {
                            if (sizes[i][0] > 1) {
                                var2GetSize[j] = (int)Math.round(sizes[i][0]);
                                j++;
                            }
                        }

                    }
                    lastAction -= JAA_MASK_GET_SIZE;
                    mat.feval("eval", new Object[] {var2GetName}, 1, this);
                }
                else {
                    // Matlab Command Completed
                    String ble = "Int: " + v + "\n" + "Obj: " + o.getClass().toString() + "\n";
                    if (o instanceof double[]) {
                        double[] d = (double[])o;
                        ble += "length: " + d.length + "\n";
                        for (int i = 0; i < d.length; i++) {
                            ble += "d["+i+"] = " + d[i] + "\n";
                        }
                    } else if (o instanceof Object[]){
                        Object[] objs = (Object[])o;
                        for (int i = 0; i < objs.length; i++) {
                            ble += "o["+i+"] = " + objs[i].getClass().toString() + "\n";
                            ble += Struct2Str(objs[i],1);
                        }
                    } else if (o instanceof String) {
                        String s = (String)o;
                        s = s.replaceFirst("[a-zA-Z0-9]+ =", "");
                        s = s.replaceAll("\r\n", "");
                        ble += s + "\n";
                    }
                    jTextPane1.setText(ble);
                    lastAction = JAA_IDLE;
                    blockFrame(false);
                }
                break;
        }}
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "last Action:  " + lastAction + " " + e.toString());
            lastAction = JAA_IDLE;
            blockFrame(false);
            //e.printStackTrace();
        }
    }

    public double[][] Obj2DArray(Object o) {
        return Obj2DArray(o, null);
    }

    public double[][] Obj2DArray(Object o, Integer[] size) {
        double[][] ret = new double[0][0];
        try {
            if (o instanceof double[]) {
                double[] obj = (double[])o;
                ret = new double[var2GetSize[1]][var2GetSize[0]];
                
                int k = 0;
                for (int j = 0; j < var2GetSize[1]; j++)
                    for (int i = 0; i < var2GetSize[0]; i++)
                        ret[j][i] = obj[k++];

                if (size != null)
                {
                    size[0] = var2GetSize[1];
                    size[1] = var2GetSize[0];
                }
                return ret;
            }
            else if (o instanceof String) {
                String s = (String)o;
                s = s.replaceFirst("[a-zA-Z0-9]+ =", "");
                double factor = 1;

                String[] blaas = s.split("\n", 5);
                for (int i = 0; i < blaas.length; i++) {
                    if (blaas[i].matches("[ ]*1.0e[+-][0-9]+ \\*[ ]*")) {
                        String tmp = blaas[i].replaceAll(" \\*[ \r]*", "");
                        tmp = tmp.replaceAll("[ ]+", "");
                        factor = Double.parseDouble(tmp);
                    }
                }

                s = s.replaceAll("[ ]*1.0e[+-][0-9]+ \\*[ ]*", "");
                s = s.replaceAll("[\n\r][\n\r]+", "");
                String[] strs = s.split("\n");
                int y = strs.length;
                if (y > 0) {
                    String[] vals = strs[0].replaceFirst("[ ]+", "").split("[ ]+");
                    int x = vals.length;
                    ret = new double[x][y];
                    size[0] = x;
                    size[1] = y;
                    for (int i = 0; i< y; i++) {
                        vals = strs[i].replaceFirst("[ ]+", "").split("[ ]+");
                        int xx = (x < vals.length) ? x : vals.length;
                        for (int j = 0; j< xx; j++) {
                            ret[j][i] = Double.parseDouble(vals[j]) * factor;
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void updateCursor(double c1, double c2) {
        /*
        int c = cursor1.getItemCount();
        cursor1.delete(0, c-1);
        c = cursor2.getItemCount();
        cursor2.delete(0, c-1);
        double low = plot.getRangeAxis().getLowerBound();
        double high = plot.getRangeAxis().getUpperBound();
        double span = high - low;
        cursor1.add(c1, low);
        cursor1.add(c1, high-(span*0.05));
        cursor2.add(c2, low);
        cursor2.add(c2, high-(span*0.05));*/
        if (plot != null) {
            plot.setCursorPos(0, c1);
            plot.setCursorPos(1, c2);
            plot.changed();
        }
    }

    public void UpdateDelButton() {
        if (fcscurrbunch >= 0) {
            if (fcsbunchdelete[fcscurrbunch-1]) {
                jButton_DelBunch.setForeground(Color.red);
                jButton_DelBunch.setText("use Bunch");
            }
            else {
                jButton_DelBunch.setForeground(jButton_TwoFocusFCS.getForeground());
                jButton_DelBunch.setText("Delete Bunch");
            }
        }
    }

    public void updatePlot(double[][] data, Integer[] dim) {
        updatePlot(data, dim, new String[] {}, "Channel", "Counts", false);
    }

    public void updatePlot(double[][] data, Integer[] dim, String[] Names, String xaxs, String yaxs, boolean logy) {
        updatePlot(data, 1, dim, Names, xaxs, yaxs, logy);
    }

    public void updatePlot(double[][] data, double deltaX, Integer[] dim, String[] Names, String xaxs, String yaxs, boolean logy) {
        updatePlot(data, 0, deltaX, dim, Names, xaxs, yaxs, logy);
    }

    public void updatePlot(double[][] data, double offsetX, double deltaX, Integer[] dim, String[] Names, String xaxs, String yaxs, boolean logy) {
        int dim2 = 1;
        if (dim.length > 1)
            dim2 = dim[1].intValue();
        double[] xvalues = new double[dim2];
        for (int j = 0; j < dim2; j++)
            xvalues[j] = j*deltaX+offsetX;
        updatePlot(data, xvalues, dim, Names, xaxs, yaxs, logy, false);
    }

    public void updatePlot(double[][] data, double[] xvalues, Integer[] dim, String[] Names, String xaxs, String yaxs, boolean logy, boolean logx) {
        //DefaultTableXYDataset ds = new DefaultTableXYDataset();
        XYSeriesCollection ds = new XYSeriesCollection();
        plot.setDataset(ds);
        
        naxx = new NumberAxis(xaxs);
        if (logx) {
            naxx = new JA_LogarithmicAxis(xaxs);
            ((LogarithmicAxis)naxx).setExpTickLabelsFlag(true);
            ((LogarithmicAxis)naxx).setAllowNegativesFlag(false);
        }
        naxx.setAutoRangeIncludesZero(false);
        
        naxy = new NumberAxis(yaxs);
        naxy.setStandardTickUnits(JA_TickUnits.createUnits());
        if (logy) {
            naxy = new LogarithmicAxis(yaxs);
            ((LogarithmicAxis)naxy).setExpTickLabelsFlag(true);
        }
        naxy.setAutoRangeIncludesZero(false);
        plot.setDomainAxis(naxx);
        plot.setRangeAxis(naxy);
        plot.removeAllCursor();


        int dim1 = 1;
        int dim2 = 1;
        int skip = 10;
        int start = 0;
        boolean b6curves = false;
        switch (dim.length) {
            case 3:
                start = dim[2].intValue();
            case 2:
                dim2 = dim[1].intValue();
                skip = (int)Math.ceil(dim2/1000);
                if (skip < 1)
                    skip = 1;
            case 1:
                dim1 = dim[0].intValue();
            default:
        }

        if ((dim1 == 6) || (dim1 == 16) || (dim1 == 22)) {
            b6curves = (dim1-start) >= 6;
        }
        if (dim1 > 4)
            dim1 = 4;

        for (int i = 0; i < dim1 ; i++) {
            String str = "Signal " + i;
            if (Names.length > i)
                str = Names[i];
            XYSeries set1 = new XYSeries(str, false, false);
            for (int j = 0; j < dim2; j+=skip) {
                if ((logy) && (data[i+start][j] <= 0)) {
                    set1.add(xvalues[j], 1);
                }
                else {
                    if ((b6curves) && (i > 1)) {
                        set1.add(xvalues[j], data[i+start][j]+data[i+start+2][j]);
                    }
                    else
                        set1.add(xvalues[j], data[i+start][j]);
                }
            }
            ds.addSeries(set1);
        }
        plot.setDataset(ds);
        chartPanel.repaint();
    }

    public double parse(Object o) {
        if (o instanceof double[]) {
            double[] obj = (double[])o;
            if (obj.length > 0)
                return obj[0];
        }
        else if (o instanceof Object[]) {
            Object[] obj = (Object[])o;
            if (obj.length > 0)
                return parse(obj[0]);
        }
        return Double.NaN;
    }

    public TreeModel HashTable2TreeModel(Hashtable<String, Object> ht) {
        Iterator<String> enm = ht.keySet().iterator();
        DefaultMutableTreeNode papanode = new DefaultMutableTreeNode("File Header");

        String comment = ht.get("CommentField").toString();
        papanode.add(new DefaultMutableTreeNode("CommentFiled: " + comment, false));
        String creationtime = ht.get("FileTime").toString();
        papanode.add(new DefaultMutableTreeNode("Created on: " + creationtime, false));
        double sa = parse(ht.get("StopAfter")) / 1000;
        papanode.add(new DefaultMutableTreeNode("StopAfter: " + sa + "s", false));

        DefaultMutableTreeNode rootnode = new DefaultMutableTreeNode("Raw Data");
        HashTable2TreeNode(rootnode, ht);
        papanode.add(rootnode);
        return new DefaultTreeModel(papanode);
    }

    public void HashTable2TreeNode(DefaultMutableTreeNode rootnode, Hashtable<String, Object> ht) {
        Iterator<String> enm = ht.keySet().iterator();
        while (enm.hasNext()) {
            String key = enm.next();
            Object val = ht.get(key);
            DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(key);

            if (val instanceof Hashtable) {
                Hashtable<String, Object> hh = (Hashtable<String, Object>) val;
                HashTable2TreeNode(childnode, hh);
            } else {
                String str = Struct2Str(val);
                String[] strs = str.split("\n");
                for (int i=0; i<strs.length;i++)
                    childnode.add(new DefaultMutableTreeNode(strs[i],false));
            }
            rootnode.add(childnode);
        }
    }

    public Hashtable<String, Object> Struct2HashTable(Object o) {
        Hashtable<String, Object> ht = null;
        if (o instanceof Object[]){
            Object[] o1 = (Object[])o;
            if ((o1.length == 2) && (o1[0] instanceof String[]) && (o1[1] instanceof Object[])) {
                String[] str = (String[])o1[0];
                Object[] obj = (Object[])o1[1];
                int dim = obj.length;
                Object[] obj2 = (Object[])obj[0];
                if (str.length == obj2.length) {
                    ht = new Hashtable<String, Object>();
                    for (int i = 0; i < str.length; i++) {
                        Object[] newobj = new Object[dim];

                        for (int j = 0; j < dim; j++) {
                            obj2 = (Object[])obj[j];
                            if (obj2[i] instanceof Object[]) {
                                Hashtable<String, Object> ht2 = Struct2HashTable(obj2[i]);
                                if (ht2 != null) {
                                   newobj[j] = ht2;
                                }
                                else
                                   newobj[j] = obj2[i];
                            }
                            else
                                newobj[j] = obj2[i];

                        }
                        if (dim > 1) {
                            ht.put(str[i], newobj);
                        }
                        else {
                            ht.put(str[i], newobj[0]);
                        }
                    }
                }
            }
        }
        return ht;
    }

    public String Struct2Str(Object o) {
        return Struct2Str(o, 0);
    }

    public String Struct2Str(Object o, int level) {
        String str = "";
        String tab = "";
        for (int i = 0; i < level; i++)
            tab += " ";
        if (o instanceof double[]) {
            double[] d = (double[])o;
            for (int i = 0; i < d.length; i++) {
                str += tab + "d["+i+"] = " + d[i] + "\n";
            }
        } else if (o instanceof String[]){
            String[] objs = (String[])o;
            for (int i = 0; i < objs.length; i++) {
                str += tab + "s["+i+"] = " + objs[i] + "\n";
            }
        } else if (o instanceof Object[]){
            Object[] objs = (Object[])o;
            for (int i = 0; i < objs.length; i++) {
                str += tab + "o["+i+"] = " + objs[i].getClass().toString() + "\n";
                str += Struct2Str(objs[i], level+1);
            }
        } else {
            str += tab + o.toString() + "\n";
        }
        return str;
    }

    public String DArray2String(double[] d) {
        String str = "[";
        if (d != null) {
            if (d.length >= 1) {
                if (d[0] == Double.POSITIVE_INFINITY) {
                    str += "inf";
                }
                else
                    str = str + d[0];
            }
            for (int i = 1; i < d.length; i++) {
                if (d[i] == Double.POSITIVE_INFINITY) {
                    str += ", inf";
                }
                else
                    str = str + ", " + d[i];
            }
        }
        str += "]";
        return str;
    }

    public String IntArray2String(int[] d) {
        String str = "[";
        if (d != null) {
            if (d.length >= 1) {
                str = str + d[0];
            }
            for (int i = 1; i < d.length; i++) {
                str = str + ", " + d[i];
            }
        }
        str += "]";
        return str;
    }

    public void getMatlabVariable(String varName) {
        var2GetName = varName;
        lastAction |= JAA_MASK_GET_SIZE;
        mat.eval("size(" + varName + ")", this);
    }

    public void ExecMatlab(String command) {
        ExecMatlab(command, JAA_OTHER);
    }

    public void ExecMatlab(String command, int newState) {
        ExecMatlab(command, newState, true);
    }

    public void ExecMatlab(String command, int newState, boolean consoleOutput) {
        ExecMatlab(command, newState, consoleOutput, true);
    }

    public void ExecMatlab(String command, int newState, boolean consoleOutput, boolean addHist) {
        if (lastAction == JAA_IDLE) {
            try {
                blockFrame(true);
                lastAction = newState;
                AddOuputAndCmdHist(command, addHist);
                if (consoleOutput) {
                    mat.evalConsoleOutput(command, this);
                }
                else
                    mat.eval(command, this);
            } catch(Exception e) {
                System.err.println("error in evalConsoleOutput");
                e.printStackTrace();
            }
        }
        else
            System.err.println("idle...");
    }
    
    public void AddOuputAndCmdHist(String command) {
        AddOuputAndCmdHist(command, true);
    }

    public void AddOuputAndCmdHist(String command, boolean addHist) {
        System.out.println(command);
        if (addHist) {
            String[] comms = command.split("\n");
            for (int i = 0; i < comms.length; i++)
                cmdHist.add(comms[i]);
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox_WorkDir = new javax.swing.JComboBox();
        jButton_ChangeDir = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jButton_TCSPC = new javax.swing.JButton();
        jButton_Fit = new javax.swing.JButton();
        jButton_PlotTCSPC = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jButton_RemCursors = new javax.swing.JButton();
        jButton_AddCursors = new javax.swing.JButton();
        jButton_Baseline = new javax.swing.JButton();
        jButton_AutoTimegate = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jButton_Trace = new javax.swing.JButton();
        jLabel_TimeStr = new javax.swing.JLabel();
        jScrollBar_TraceBunch = new javax.swing.JScrollBar();
        jLabel_TraceBunch = new javax.swing.JLabel();
        jButton_Burst = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jButton_SaveBunch = new javax.swing.JButton();
        jScrollBar_Bunch = new javax.swing.JScrollBar();
        jLabel_Bunch = new javax.swing.JLabel();
        jLabel_BunchStr = new javax.swing.JLabel();
        jButton_TwoFocusFCS = new javax.swing.JButton();
        jButton_TwoFocusFit = new javax.swing.JButton();
        jButton_PlotCorrs = new javax.swing.JButton();
        jComboBox_Curves = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jButton_DelBunch = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList_Files = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTree_FileHeader = new javax.swing.JTree();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("JAnalyzer");
        setMinimumSize(new java.awt.Dimension(800, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setPreferredSize(new java.awt.Dimension(781, 40));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Current Directory:");
        jLabel1.setPreferredSize(new java.awt.Dimension(126, 20));
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jComboBox_WorkDir.setMinimumSize(new java.awt.Dimension(51, 20));
        jComboBox_WorkDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_WorkDirActionPerformed(evt);
            }
        });
        jPanel1.add(jComboBox_WorkDir, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, 260, -1));

        jButton_ChangeDir.setText("...");
        jButton_ChangeDir.setPreferredSize(new java.awt.Dimension(73, 20));
        jButton_ChangeDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_ChangeDirActionPerformed(evt);
            }
        });
        jPanel1.add(jButton_ChangeDir, new org.netbeans.lib.awtextra.AbsoluteConstraints(425, 10, 20, -1));

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setMinimumSize(new java.awt.Dimension(600, 400));
        jPanel2.setPreferredSize(new java.awt.Dimension(600, 400));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneStateChanged(evt);
            }
        });

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton_TCSPC.setText("TCSPC");
        jButton_TCSPC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_TCSPCActionPerformed(evt);
            }
        });
        jPanel4.add(jButton_TCSPC, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, -1));

        jButton_Fit.setText("Fit Lifetime");
        jButton_Fit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_FitActionPerformed(evt);
            }
        });
        jPanel4.add(jButton_Fit, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, 110, -1));

        jButton_PlotTCSPC.setText("plot(TCSPC)");
        jButton_PlotTCSPC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_PlotTCSPCActionPerformed(evt);
            }
        });
        jPanel4.add(jButton_PlotTCSPC, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        jScrollPane1.setViewportView(jTextPane1);

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 10, 120, 50));

        jButton_RemCursors.setText("Del. Cursor");
        jButton_RemCursors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_RemCursorsActionPerformed(evt);
            }
        });
        jPanel4.add(jButton_RemCursors, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 40, 100, -1));

        jButton_AddCursors.setText("Add Cursor(s)");
        jButton_AddCursors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_AddCursorsActionPerformed(evt);
            }
        });
        jPanel4.add(jButton_AddCursors, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 100, -1));

        jButton_Baseline.setText("Baseline/Rebin");
        jButton_Baseline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_BaselineActionPerformed(evt);
            }
        });
        jPanel4.add(jButton_Baseline, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 110, -1));

        jButton_AutoTimegate.setText("Autodetect");
        jButton_AutoTimegate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_AutoTimegateActionPerformed(evt);
            }
        });
        jPanel4.add(jButton_AutoTimegate, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 100, -1));

        jTabbedPane.addTab("TCSPC", jPanel4);

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton_Trace.setText("Trace");
        jButton_Trace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_TraceActionPerformed(evt);
            }
        });
        jPanel5.add(jButton_Trace, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 100, -1));

        jLabel_TimeStr.setFont(new java.awt.Font("Tahoma", 1, 14));
        jLabel_TimeStr.setText("Bunch:");
        jPanel5.add(jLabel_TimeStr, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 45, -1, -1));

        jScrollBar_TraceBunch.setBlockIncrement(1);
        jScrollBar_TraceBunch.setOrientation(javax.swing.JScrollBar.HORIZONTAL);
        jScrollBar_TraceBunch.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                jScrollBar_TraceBunchAdjustmentValueChanged(evt);
            }
        });
        jPanel5.add(jScrollBar_TraceBunch, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 45, 320, -1));

        jLabel_TraceBunch.setFont(new java.awt.Font("Tahoma", 1, 14));
        jLabel_TraceBunch.setText("1 of 1");
        jPanel5.add(jLabel_TraceBunch, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 45, 120, -1));

        jButton_Burst.setText("Burst Analysis");
        jButton_Burst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_BurstActionPerformed(evt);
            }
        });
        jPanel5.add(jButton_Burst, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, 100, -1));

        jTabbedPane.addTab("Time Trace", jPanel5);

        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton_SaveBunch.setText("Save");
        jButton_SaveBunch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_SaveBunchActionPerformed(evt);
            }
        });
        jPanel6.add(jButton_SaveBunch, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 43, -1, -1));

        jScrollBar_Bunch.setBlockIncrement(1);
        jScrollBar_Bunch.setOrientation(javax.swing.JScrollBar.HORIZONTAL);
        jScrollBar_Bunch.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                jScrollBar_BunchAdjustmentValueChanged(evt);
            }
        });
        jPanel6.add(jScrollBar_Bunch, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 45, 260, -1));

        jLabel_Bunch.setFont(new java.awt.Font("Tahoma", 1, 14));
        jLabel_Bunch.setText("1 of 1");
        jPanel6.add(jLabel_Bunch, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 45, 65, -1));

        jLabel_BunchStr.setFont(new java.awt.Font("Tahoma", 1, 14));
        jLabel_BunchStr.setText("Bunch:");
        jPanel6.add(jLabel_BunchStr, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 45, -1, -1));

        jButton_TwoFocusFCS.setText("Calc FCS");
        jButton_TwoFocusFCS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_TwoFocusFCSActionPerformed(evt);
            }
        });
        jPanel6.add(jButton_TwoFocusFCS, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 100, -1));

        jButton_TwoFocusFit.setText("Fit FCS");
        jButton_TwoFocusFit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_TwoFocusFitActionPerformed(evt);
            }
        });
        jPanel6.add(jButton_TwoFocusFit, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 10, 100, -1));

        jButton_PlotCorrs.setText("plot( t, y )");
        jButton_PlotCorrs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_PlotCorrsActionPerformed(evt);
            }
        });
        jPanel6.add(jButton_PlotCorrs, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, 110, -1));

        jComboBox_Curves.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Curve 1-4", "Curve 5-8", "Curve 9-12", "Curve 13-16" }));
        jComboBox_Curves.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox_CurvesItemStateChanged(evt);
            }
        });
        jPanel6.add(jComboBox_Curves, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 10, 100, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel2.setText("Show:");
        jPanel6.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 13, -1, -1));

        jButton_DelBunch.setText("Delete Bunch");
        jButton_DelBunch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_DelBunchMouseClicked(evt);
            }
        });
        jButton_DelBunch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_DelBunchActionPerformed(evt);
            }
        });
        jPanel6.add(jButton_DelBunch, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 43, 100, -1));

        jTabbedPane.addTab("Two Focus FCS", jPanel6);

        jPanel2.add(jTabbedPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 5, 600, 100));

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setPreferredSize(new java.awt.Dimension(170, 450));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jList_Files.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList_Files.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList_FilesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jList_Files);

        jPanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 150, 220));

        jTree_FileHeader.setModel(null);
        jScrollPane3.setViewportView(jTree_FileHeader);

        jPanel3.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 150, 220));

        getContentPane().add(jPanel3, java.awt.BorderLayout.WEST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_TCSPCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_TCSPCActionPerformed
        // Start Comand:
        if (jList_Files.getSelectedIndex() != -1) {
            String file = (String)jList_Files.getSelectedValue();
            ht3file = new File(cd.getAbsolutePath() + "\\" + file);
            if (ht3file.exists()) {
                if (lastAction == JAA_IDLE) {
                    blockFrame(true);
                    iscalc_TCSPC = true;
                    lastAction = JAA_TCSPC_RRHEAD;
                    System.out.println("[bin, tcspcdata, head] = ReadTCSPC('" + ht3file.getAbsolutePath() +"');");
                    cmdHist.add("[bin, tcspcdata, head] = ReadTCSPC('" + ht3file.getAbsolutePath() +"');");
                    mat.feval("ht3v2read_head", new Object[] {new String(cd.getAbsolutePath() + "\\" + file)}, this);
                }
            }
            else {
                JOptionPane.showMessageDialog(this, "Select File");
            }
        }
        else {
                JOptionPane.showMessageDialog(this, "Select File");
        }
    }//GEN-LAST:event_jButton_TCSPCActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // hide Window
        this.setVisible(false);
    }//GEN-LAST:event_formWindowClosing

    private void jButton_TraceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_TraceActionPerformed
        // TODO add your handling code here:
        if (jList_Files.getSelectedIndex() != -1) {
            String file = (String)jList_Files.getSelectedValue();
            ht3file = new File(cd.getAbsolutePath() + "\\" + file);
            if (ht3file.exists()) {
                traceDlg.setVisible(true);
                if (traceDlg.getReturnVal() == JA_TraceDialog.ID_OK) {
                    jTextPane1.setText("OK");
                    String tw = DArray2String(traceDlg.getTimeWnd());
                    String bw = Double.toString(traceDlg.getBinWidth());
                    tracedelta = traceDlg.getBinWidth();
                    String exstr;
                    exstr = "[trace, tcspcdata, head] = ReadAndBin('" + ht3file.getAbsolutePath() + "', " + bw + ", " + tw + ");";
                    ExecMatlab(exstr, JAA_TRACE_READ, false);
                }
            }
        }
    }//GEN-LAST:event_jButton_TraceActionPerformed

    private void jButton_ChangeDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_ChangeDirActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if ((cd != null) && (cd.isDirectory()))
            fc.setCurrentDirectory(cd);
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        fc.setDialogTitle("Open Folder");
        int ret = fc.showDialog(this, null);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File sel = fc.getSelectedFile();
            if ((sel != null) && (sel.isDirectory()) && (!sel.equals(cd))) {
                cd = sel;
                ((DefaultComboBoxModel)jComboBox_WorkDir.getModel()).addElement(cd);
                ((DefaultComboBoxModel)jComboBox_WorkDir.getModel()).setSelectedItem(cd);
                updateFileList();
            }
        }
    }//GEN-LAST:event_jButton_ChangeDirActionPerformed

    private void jComboBox_WorkDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_WorkDirActionPerformed
        if (File.class.isInstance(jComboBox_WorkDir.getSelectedItem())) {
            File sel = (File)jComboBox_WorkDir.getSelectedItem();
            if (!sel.equals(cd)) {
                cd = sel;
                updateFileList();
            }
        }
    }//GEN-LAST:event_jComboBox_WorkDirActionPerformed

    private void jList_FilesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList_FilesValueChanged
        if (jList_Files.getSelectedIndex() != -1) {
            String file = (String)jList_Files.getSelectedValue();
            ht3header = null;
            int[] idxes = jList_Files.getSelectedIndices();
            multiple_FCS_Selected = ((idxes != null) && (idxes.length > 1));
            if (lastAction == JAA_IDLE) {
                File matfile = new File(cd.getAbsolutePath() + "\\" + file.replaceAll(".[ph]t3", ".mat"));
                blockFrame(true);
                iscalc_TCSPC = false;
                iscalc_FCS = false;
                isload_Trace = false;
                exists_FCS_Mat_File = matfile.exists();
                lastAction = JAA_READHEAD;
                mat.feval("ht3v2read_head", new Object[] {new String(cd.getAbsolutePath() + "\\" + file)}, this);
            }
        }
    }//GEN-LAST:event_jList_FilesValueChanged

    private void jButton_PlotTCSPCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_PlotTCSPCActionPerformed
        ExecMatlab("plot(tcspcdata);", JAA_OTHER);
        /*try {
            if (lastAction == JAA_IDLE) {
                //mat.feval("ReadAndBin", new Object[] {ht3file.getAbsolutePath(), new Double(1000), new Double[]{new Double(0), new Double(100)}}, this);
                //mat.fevalConsoleOutput("ReadAndBin", new Object[] {ht3file.getAbsolutePath(), new Double(1000)},0,this);
                blockFrame(true);
                lastAction = JAA_OTHER;
                mat.eval("plot(tcspc);", this);
                //mat.fevalConsoleOutput("size", new Object[] {"tcspc"}, 0, this); // returns size of string
            }
        } catch(Exception e) {
            System.err.println("error in eval");
            e.printStackTrace();
        }*/
    }//GEN-LAST:event_jButton_PlotTCSPCActionPerformed

    private void jButton_FitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_FitActionPerformed
        //ExecMatlab("[x, dx, steps] = Simplex('ExpFun',[0.8],[0],[],[],[],2000:10000,tcspc(2000:10000,1));", JAA_FIT);
        if ((tcspcsize != null) && (tcspcsize.length == 2)) {
            fitDlg.setNumCurves(tcspcsize[0].intValue());
            double[] cpos = plot.getCursorPositions();
            if (cpos.length >= 2) {
                fitDlg.setRange(cpos[0], cpos[1]);
            }
            fitDlg.setVisible(true);
            if (fitDlg.getReturnVal() == JA_FitDialog.ID_OK) {
                jTextPane1.setText("OK");
                String st = DArray2String(fitDlg.getStartLT());
                String min = DArray2String(fitDlg.getMinLT());
                double[] rg = fitDlg.getRange();
                long ch1 = Math.round(rg[0] / tcspc_delx)+1;
                long ch2 = Math.round(rg[1] / tcspc_delx)+1;
                if (ch1 > ch2) {
                    long t = ch1;
                    ch1 = ch2;
                    ch2 = t;
                }
                int[] detectorSet = fitDlg.getSelectedDetectorSet();
                long cnt = (ch2 - ch1) + 1;
                String exstr;
                if ((detectorSet == null) || (detectorSet.length == 0)) {
                    exstr = "[x, dx, steps] = Simplex('ExpFun'," + st + "," + min + ",[],[],[],";
                    exstr += "(1:" + cnt + ")*" + tcspc_delx + ",sum(tcspcdata(" + ch1 + ":" + ch2 + ",:),2),1);";
                }
                else {
                    if (detectorSet.length == 1) {
                        exstr = "[x, dx, steps] = Simplex('ExpFun'," + st + "," + min + ",[],[],[],";
                        exstr += "(1:" + cnt + ")*" + tcspc_delx + ",tcspcdata(" + ch1 + ":" + ch2 + "," + detectorSet[0] + "),1);";
                    }
                    else {
                        String chans = IntArray2String(detectorSet);
                        exstr = "[x, dx, steps] = Simplex('ExpFun'," + st + "," + min + ",[],[],[],";
                        exstr += "(1:" + cnt + ")*" + tcspc_delx + ",sum(tcspcdata(" + ch1 + ":" + ch2 + "," + chans + "),2),1);";
                    }
                }
                ExecMatlab(exstr, JAA_FIT, false);
            }
        }
        else
            JOptionPane.showMessageDialog(this, "No TCSPC loaded");
    }//GEN-LAST:event_jButton_FitActionPerformed

    private void jButton_RemCursorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_RemCursorsActionPerformed
        // TODO add your handling code here:
        int cc = plot.getCursorCount();
        if (cc > 0) {
            plot.removeCursor(cc-1);
            plot.changed();
        }
    }//GEN-LAST:event_jButton_RemCursorsActionPerformed

    private void jButton_AddCursorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_AddCursorsActionPerformed
        // TODO add your handling code here:
        int cc = plot.getCursorCount();
        Range rg = naxx.getRange();
        double low = rg.getLowerBound();
        double high = rg.getUpperBound();
        double range = high - low;

        if (cc < 2) {
            updateCursor(low + range/4, low + (range*3/4));
        }
        else if (cc < 4) {
            double[] pos = plot.getCursorPositions();
            double cx = pos[pos.length-1];
            plot.setCursorPos(cc, cx + (high-cx)/2);
            plot.changed();
        }

    }//GEN-LAST:event_jButton_AddCursorsActionPerformed

    private void jButton_BaselineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_BaselineActionPerformed
        if ((tcspcsize != null) && (tcspcsize.length == 2)) {
            if (ht3header != null) {
                if (ht3header.containsKey("Resolution")) {
                    double res_ps = Math.round(parse(ht3header.get("Resolution")) * 1000);
                    blDlg.setBaseBinning(res_ps);
                    double[] cpos = plot.getCursorPositions();
                    if (cpos.length >= 2) {
                        blDlg.setRange(cpos[0], cpos[1]);
                    }
                    blDlg.setVisible(true);
                    if (blDlg.getReturnVal() == JA_BaselineDlg.ID_OK) {
                        jTextPane1.setText("OK");
                        int fac = blDlg.getRebinFactor();
                        double[] rg = blDlg.getRange();
                        long ch1 = Math.round(rg[0] / (tcspc_delx * fac))+1;
                        long ch2 = Math.round(rg[1] / (tcspc_delx * fac))+1;
                        int newchans = (int)Math.floor(tcspcsize[1] / fac);
                        if (ch1 > ch2) {
                            long t = ch1;
                            ch1 = ch2;
                            ch2 = t;
                            ch1 = checkRange(ch1, 1, newchans);
                            ch2 = checkRange(ch2, 1, newchans);
                        }
                        long cnt = (ch2 - ch1) + 1;

                        String exstr = "";
                        boolean doBL = blDlg.doBaseline();
                        boolean doRebin = (fac > 1);
                        if (doRebin) {
                            // update vec
                            double newres = parse(ht3header.get("Resolution"))*fac;
                            ht3header.remove("Resolution");
                            ht3header.put("Resolution", new double[] {newres});

                            // get rest
                            int rest = tcspcsize[1] % fac;
                            if (rest == 0)
                                rest = fac;
                            String zs = "zeros(1,"+tcspcsize[0]+")";

                            exstr += "tcspcdata = ";
                            for (int i = 0; i < fac; i++) {
                                if (i > 0) {
                                    if (i >= rest) {
                                        exstr += " + vertcat(tcspcdata(" + (i+1) + ":" + fac + ":end, :), " + zs + ")";
                                    }
                                    else
                                        exstr += " + tcspcdata(" + (i+1) + ":" + fac + ":end, :)";
                                }
                                else
                                    exstr += "tcspcdata(" + (i+1) + ":" + fac + ":end, :)";
                            }
                            exstr += ";\n";
                            // reset head:
                            exstr += "head.Resolution = head.Resolution * " + fac + ";\n";
                        }
                        if (doBL) {
                            for (int i = 1; i <= tcspcsize[0]; i++) {
                                if (i > 1)
                                    exstr += "\n";
                                exstr += "tcspcdata(:," + i + ") = abs(tcspcdata(:," + i + ") - mean(tcspcdata(" + ch1 + ":" + ch2 + "," + i + ")))+1;";
                            }
                        }
                        ExecMatlab(exstr, JAA_BASELINE);
                    }
                }
            }
        }
    }//GEN-LAST:event_jButton_BaselineActionPerformed

    private void jButton_SaveBunchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_SaveBunchActionPerformed
        if (fcsanzbunch > 0) {
            if (lastAction == JAA_IDLE) {
                String range = "";
                int included = 0;
                int totbunchs = 0;
                for (int i = 0; i < fcsanzbunch; i++) {
                    if (!fcsbunchdelete[i]) {
                        if (included == 0)
                            range += ((range.length() > 0) ? " " : "") + (i+1);
                        included++;
                        totbunchs++;
                    } else {
                        switch (included) {
                            case 1:
                                break;
                            case 0:
                                break;
                            default:
                                range += ":" + i;
                        }
                        included = 0;
                    }
                }
                if (included > 1)
                    range += ":" + fcsanzbunch;

                String file = (String)jList_Files.getSelectedValue();
                File matfile = new File(cd.getAbsolutePath() + "\\" + file.replaceAll(".[ph]t3", ".mat"));

                String exstr = "SaveBunchs('" + matfile.getAbsolutePath() + "', [" + range + "]);";
                ExecMatlab(exstr, JAA_OTHER, false, false);
            }
        }
    }//GEN-LAST:event_jButton_SaveBunchActionPerformed

    private void jButton_TwoFocusFCSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_TwoFocusFCSActionPerformed
        // Start Comand:
        if (jList_Files.getSelectedIndex() != -1) {
            if (multiple_FCS_Selected) {
                Object[] files = jList_Files.getSelectedValues();

                if (lastAction == JAA_IDLE) {
                    String exstr = "";
                    fcsDlg.setVisible(true);
                    if (fcsDlg.getReturnVal() == JA_BaselineDlg.ID_OK) {
                        blockFrame(true);
                        String maxInt = fcsDlg.getMaxCorr();
                        double photons = fcsDlg.getPhotons();
                        double cutoff = fcsDlg.getCutoff() * 1e-9;
                        String met = fcsDlg.getMetadata();
                        boolean clustercalc = fcsDlg.getClusterCalc();
                        int laser = fcsDlg.getLasers();
                        String photonStr = ", [], " + df.format(cutoff);
                        if (photons != 1e6) {
                            photonStr = ", [], " + df.format(cutoff) + ", " + df.format(photons);
                        }

                        // Build Command String
                        for (int i = 0; i < files.length; i++) {
                            String file = (String)files[i];
                            File thisht3file = new File(cd.getAbsolutePath() + "\\" + file);
                            File matfile = new File(cd.getAbsolutePath() + "\\" + file.replaceAll(".[ph]t3", ".mat"));
                            if (thisht3file.exists() && !matfile.exists()) {
                                if (exstr.length() > 1)
                                    exstr +=  "\n";
                                exstr += (clustercalc) ? "[res, head] = MultiFocus2FCS_Cluster('" : "[res, head] = MultiFocus2FCS('" ;
                                exstr += thisht3file.getAbsolutePath() + "', " + laser +", " + maxInt + photonStr + ");\n";
                                exstr += "res.metadata = GenMetadata(" + met + ");\n";
                                exstr += "save('" + matfile.getAbsolutePath() + "', 'res', 'head');";
                            }
                        }
                        // execute
                        if (exstr.equals("")) {
                            blockFrame(false);
                        }
                        else
                            ExecMatlab(exstr, JAA_BATCHCALC, true, false);  // now comm history
                    }
                }
            }
            else {
                String file = (String)jList_Files.getSelectedValue();
                ht3file = new File(cd.getAbsolutePath() + "\\" + file);
                File matfile = new File(cd.getAbsolutePath() + "\\" + file.replaceAll(".[ph]t3", ".mat"));
                if (ht3file.exists()) {
                    lastfcsfile = ht3file.getAbsolutePath();
                    if (matfile.exists()) {
                        // Load Matfile
                        nextMatfile = matfile.getAbsolutePath();
                        
                        String exstr = "load('" + matfile.getAbsolutePath() + "');\n";
                        exstr += "[y, t] = FCSCrossRead(res);";
                        ExecMatlab(exstr, JAA_2FOCUS2FCS_CALC);
                    }
                    else {
                        if (lastAction == JAA_IDLE) {
                            fcsDlg.setVisible(true);
                            if (fcsDlg.getReturnVal() == JA_BaselineDlg.ID_OK) {
                                blockFrame(true);
                                // calc FCS
                                String maxInt = fcsDlg.getMaxCorr();
                                double photons = fcsDlg.getPhotons();
                                double cutoff = fcsDlg.getCutoff() * 1e-9;
                                String met = fcsDlg.getMetadata();
                                boolean clustercalc = fcsDlg.getClusterCalc();
                                int laser = fcsDlg.getLasers();

                                String photonStr = ", [], " + df.format(cutoff);
                                if (photons != 1e6) {
                                    photonStr = ", [], " + df.format(cutoff) + ", " + df.format(photons);
                                }

                                lastAction = JAA_2FOCUS2FCS_READHEAD;
                                nextCommand = (clustercalc) ? "[res, head] = MultiFocus2FCS_Cluster('" : "[res, head] = MultiFocus2FCS('" ;
                                nextCommand += ht3file.getAbsolutePath() + "', " + laser +", " + maxInt + photonStr + ");\n";
                                nextCommand += "res.metadata = GenMetadata(" + met + ");\n";
                                nextCommand += "save('" + matfile.getAbsolutePath() + "', 'res', 'head');\n";
                                nextCommand += "[y, t] = FCSCrossRead(res);";

                                nextMatfile = matfile.getAbsolutePath();

                                AddOuputAndCmdHist(nextCommand);
                                mat.feval("ht3v2read_head", new Object[] {new String(cd.getAbsolutePath() + "\\" + file)}, this);
                            }
                        }
                    }
                }
                else {
                    JOptionPane.showMessageDialog(this, "Select File");
                }
            }
        }
        else {
                JOptionPane.showMessageDialog(this, "Select File");
        }
    }//GEN-LAST:event_jButton_TwoFocusFCSActionPerformed

    private void jScrollBar_BunchAdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_jScrollBar_BunchAdjustmentValueChanged
        if (lastAction == JAA_IDLE) {
            try {
                if (fcsanzbunch > 0) {
                    blockFrame(true);
                    fcscurrbunch = jScrollBar_Bunch.getValue();
                    jLabel_Bunch.setText(fcscurrbunch + " of " + fcsanzbunch);
                    lastAction = JAA_2FOCUS2FCS_SHOWCROSS;
                    getMatlabVariable("y(:,:," + fcscurrbunch + ")");
                }
            } catch(Exception e) {
                System.err.println("error in eval");
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jScrollBar_BunchAdjustmentValueChanged

    private void jButton_TwoFocusFitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_TwoFocusFitActionPerformed
        if (fcsanzbunch > 0) {
            String range = "";
            int included = 0;
            int totbunchs = 0;
            for (int i = 0; i < fcsanzbunch; i++) {
                if (!fcsbunchdelete[i]) {
                    if (included == 0)
                        range += ((range.length() > 0) ? " " : "") + (i+1);
                    included++;
                    totbunchs++;
                } else {
                    switch (included) {
                        case 1:
                            break;
                        case 0:
                            break;
                        default:
                            range += ":" + i;
                    }
                    included = 0;
                }
            }
            if (included > 1)
                range += ":" + fcsanzbunch;

            String file = (String)jList_Files.getSelectedValue();
            File matfile = new File(cd.getAbsolutePath() + "\\" + file.replaceAll(".[ph]t3", ".mat"));

            if ((fcsfitDlgSF.metadata != null) && (fcsfitDlgSF.metadata.containsKey("NumLasers")) && (fcsfitDlgSF.getMetaTag("NumLasers") == 1.0)) {
                // Single Focus FCS Fit
                fcsfitDlgSF.setVisible(true);
                int ret = fcsfitDlgSF.getReturnVal();
                if (ret == JA_FCSFitDialog_SingleFocus.ID_OK) {
                    int[] fitvals = fcsfitDlgSF.getFitValues();
                    String curves = fcsfitDlgSF.getCurves();
                    String fitrange = fcsfitDlgSF.getRange();
                    String exstr = "";
                    exstr += "data.t = t(" + fitrange + ");\n";

                    exstr += "data.y = sum(y(" + fitrange + "," + curves + ",:),3);\n";

                    exstr += "[dc a F triplet c err z] = RiglerFit(data," + fcsfitDlgSF.getInitialVals() + "," + fcsfitDlgSF.getModulation() + "," + fcsfitDlgSF.getBoundsStr() + ");\n";
                    if (fcsfitDlg.getExportFigure() == 1)
                        exstr += "\nExportFigure('" + lastfcsfile.replaceAll(".[ph]t3", "") + "', 'png');";
                    ExecMatlab(exstr, JAA_2FOCUS2FCS_FCSFIT);
                }
            }
            else {
                // Two Focus FCS Fit
                fcsfitDlg.setBunchs(totbunchs);
                fcsfitDlg.setVisible(true);
                int ret = fcsfitDlg.getReturnVal();
                if (ret == JA_FCSFitDialog.ID_OK) {
                    double[] retvals = fcsfitDlg.getValues();
                    int[] fitvals = fcsfitDlg.getFitValues();
                    String curves = fcsfitDlg.getCurves();
                    String fitrange = fcsfitDlg.getRange();
                    String w0a0 = fcsfitDlg.getInitialValsW0A0();
                    String w0a0max = fcsfitDlg.getMaxValsW0A0();
                    String w0a0min = fcsfitDlg.getMinValsW0A0();
                    String exstr = "";
                    exstr += "data.t = t(" + fitrange + ");\n";

                    exstr += "data.y = y(" + fitrange + "," + curves + ",[" + range + "]);\n";
                    exstr += "global pd;\n";
                    exstr += "pd = " + fcsfitDlg.getInitialVals() + ";\n";
                    int pooling = fcsfitDlg.getPooling();
                    int poolN = fcsfitDlg.getPoolN();
                    String bootstrap = "0";
                    switch (pooling) {
                        case 1:
                            int nn = (int)Math.floor(totbunchs / poolN);
                            bootstrap = "[" + nn + ", " + poolN + "i]";
                            break;
                        case 2:
                            bootstrap = "[" + Integer.toString(poolN) + ", " + Integer.toString((int)Math.round(totbunchs/2 + 0.3)) + "]";
                            break;
                    }
                    boolean diff3D = fcsfitDlg.is3DDiffusion();
                    if (!diff3D) {
                        if (w0a0.equals("[]")) {
                            w0a0 = "[400, 350, 1i]";
                        }
                        else {
                            w0a0.replaceAll("]", ", 1i]");
                        }
                    }

                    exstr += "[dc w0 a0 triplet c velo err z] = FCSFit(data," + w0a0 + "," + fitvals[0] + "," + bootstrap+ "," + DArray2String(retvals) + "," + fcsfitDlg.getBoundsStr() + "," + w0a0min + "," + w0a0max + ");\n";
                    exstr += "SaveFitResults('" + matfile.getAbsolutePath() + "',dc,w0,a0,triplet,c,velo,err," + fitrange + "," + curves + ",[" + range + "]," + fcsfitDlg.getInitialVals() + "," + w0a0 + "," + fitvals[0] + "," + bootstrap+ "," + DArray2String(retvals) + "," + fcsfitDlg.getBoundsStr() + "," + w0a0min + "," + w0a0max + ");";
                    if (fcsfitDlg.getExportFigure() == 1)
                        exstr += "\nExportFigure('" + lastfcsfile.replaceAll(".[ph]t3", "") + "', 'png');";
                    ExecMatlab(exstr, JAA_2FOCUS2FCS_FCSFIT);
                }
            }
        }
    }//GEN-LAST:event_jButton_TwoFocusFitActionPerformed

    private void jScrollBar_TraceBunchAdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_jScrollBar_TraceBunchAdjustmentValueChanged
        if (lastAction == JAA_IDLE) {
            try {
                if (traceanzbunch > 0) {
                    blockFrame(true);
                    tracecurrbunch = jScrollBar_TraceBunch.getValue();
                    jLabel_TraceBunch.setText(tracecurrbunch + " of " + traceanzbunch);
                    lastAction = JAA_TRACE_SHOW;
                    if (tracecurrbunch < traceanzbunch) {
                        int j = tracecurrbunch * 1000;
                        int i = j - 999;
                        getMatlabVariable("trace("+ i + ":" + j + ",:)");
                    }
                    else {
                        int i = tracecurrbunch * 1000 - 999;
                        getMatlabVariable("trace("+ i + ":end,:)");
                    }
                }
            } catch(Exception e) {
                System.err.println("error in eval");
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jScrollBar_TraceBunchAdjustmentValueChanged

    private void jTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneStateChanged
        // Update Graph if necc
        int idx = jTabbedPane.getSelectedIndex();
        if (lastAction == JAA_IDLE) {
            jList_Files.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            switch (idx) {
                case 0:
                    if (iscalc_TCSPC) {
                        // continue showing TCSPC
                        blockFrame(true);
                        lastAction = JAA_GETVAL_TCSPC;
                        getMatlabVariable("tcspcdata");
                    }
                    else {
                        XYSeriesCollection ds = new XYSeriesCollection();
                        plot.setDataset(ds);

                        naxx = new NumberAxis("Time (ns)");
                        naxx.setAutoRangeIncludesZero(false);
                        naxy = new NumberAxis("CountsJA");
                        plot.setDomainAxis(naxx);
                        plot.setRangeAxis(naxy);
                        plot.removeAllCursor();
                    }
                    break;
                case 1:
                    if (isload_Trace) {
                        if (traceanzbunch > 0) {
                            blockFrame(true);
                            tracecurrbunch = jScrollBar_TraceBunch.getValue();
                            if (traceanzbunch == 1)
                                tracecurrbunch = 1;
                            jLabel_TraceBunch.setText(tracecurrbunch + " of " + traceanzbunch);
                            lastAction = JAA_TRACE_SHOW;
                            if (tracecurrbunch < traceanzbunch) {
                                int j = tracecurrbunch * 1000;
                                int i = j - 999;
                                getMatlabVariable("trace("+ i + ":" + j + ",:)");
                            }
                            else {
                                int i = tracecurrbunch * 1000 - 999;
                                getMatlabVariable("trace("+ i + ":end,:)");
                            }
                        }
                    }
                    else {
                        XYSeriesCollection ds = new XYSeriesCollection();
                        plot.setDataset(ds);

                        naxx = new NumberAxis("Time (s)");
                        naxx.setAutoRangeIncludesZero(false);
                        naxy = new NumberAxis("Counts");
                        plot.setDomainAxis(naxx);
                        plot.setRangeAxis(naxy);
                        plot.removeAllCursor();
                    }
                    break;
                case 2:
                    jList_Files.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                    if (iscalc_FCS) {
                        if (fcsanzbunch > 0) {
                            blockFrame(true);
                            fcscurrbunch = jScrollBar_Bunch.getValue();
                            jLabel_Bunch.setText(fcscurrbunch + " of " + fcsanzbunch);
                            lastAction = JAA_2FOCUS2FCS_SHOWCROSS;
                            getMatlabVariable("y(:,:," + fcscurrbunch + ")");
                        }
                    }
                    else {
                        XYSeriesCollection ds = new XYSeriesCollection();
                        plot.setDataset(ds);

                        naxx = new NumberAxis("Time");
                        naxx.setAutoRangeIncludesZero(false);
                        naxy = new NumberAxis("Correlation");
                        plot.setDomainAxis(naxx);
                        plot.setRangeAxis(naxy);
                        plot.removeAllCursor();
                    }
                    break;
                default:
                    break;                    
            }
        }
    }//GEN-LAST:event_jTabbedPaneStateChanged

    private void jButton_PlotCorrsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_PlotCorrsActionPerformed
        ExecMatlab("MultiFocusFCSPlot(y, t);", JAA_OTHER);
    }//GEN-LAST:event_jButton_PlotCorrsActionPerformed

    private void jComboBox_CurvesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox_CurvesItemStateChanged
        if (lastAction == JAA_IDLE) {
            try {
                if (fcsanzbunch > 0) {
                    blockFrame(true);
                    fcscurrbunch = jScrollBar_Bunch.getValue();
                    jLabel_Bunch.setText(fcscurrbunch + " of " + fcsanzbunch);
                    lastAction = JAA_2FOCUS2FCS_SHOWCROSS;
                    getMatlabVariable("y(:,:," + fcscurrbunch + ")");
                }
            } catch(Exception e) {
                System.err.println("error in eval");
                e.printStackTrace();
            }
        }
}//GEN-LAST:event_jComboBox_CurvesItemStateChanged

    private void jButton_AutoTimegateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_AutoTimegateActionPerformed
        if ((tcspcsize != null) && (tcspcsize.length == 2)) {
            timegateDlg.setVisible(true);
            if (timegateDlg.getReturnVal() == JA_AutoTimegateDialog.ID_OK) {
                jTextPane1.setText("OK");
                String exstr = "[t1, len] = AutodetectTimeGates(tcspcdata, " + timegateDlg.getLasers() + ")";
                ExecMatlab(exstr, JAA_AUTOTIMEGATE, false);
            }
        }
        else
            JOptionPane.showMessageDialog(this, "No TCSPC loaded");
    }//GEN-LAST:event_jButton_AutoTimegateActionPerformed

    private void jButton_DelBunchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_DelBunchActionPerformed
        if (fcsanzbunch > 0) {
            fcsbunchdelete[fcscurrbunch-1] = !fcsbunchdelete[fcscurrbunch-1];
            UpdateDelButton();
        }
    }//GEN-LAST:event_jButton_DelBunchActionPerformed

    private void jButton_DelBunchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_DelBunchMouseClicked
        if ((fcsanzbunch > 0) && (evt.getButton() == evt.BUTTON3)) {
            bunchDlg.setRange(fcscurrbunch, fcsanzbunch);
            bunchDlg.setVisible(true);
            if (bunchDlg.getReturnVal() == JA_BunchDialog.ID_OK) {
                int b1 = bunchDlg.getBunch1();
                int b2 = bunchDlg.getBunch2();
                boolean del = bunchDlg.isDelAction();
                if (b2 < b1) {
                    int tpb = b2;
                    b2 = b1;
                    b1 = tpb;
                }
                for (int i = b1; i <= b2; i++) {
                    fcsbunchdelete[i-1] = del;
                }
                UpdateDelButton();
            }
        }
    }//GEN-LAST:event_jButton_DelBunchMouseClicked

    private void jButton_BurstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_BurstActionPerformed
        // TODO add your handling code here:
        if (jList_Files.getSelectedIndex() != -1) {
            String file = (String)jList_Files.getSelectedValue();
            ht3file = new File(cd.getAbsolutePath() + "\\" + file);
            if (ht3file.exists()) {

                burstDlg.setVisible(true);
                if (burstDlg.getReturnVal() == JA_BurstDialog.ID_OK) {
                    jTextPane1.setText("OK");
                    String tw = DArray2String(burstDlg.getTimeWnd());
                    String bw = Double.toString(burstDlg.getBinWidth());
                    int laser = burstDlg.getLasers();
                    String exstr;
                    exstr = "[t, tcspc, head] = BurstDetect('" + ht3file.getAbsolutePath() + "', " + laser + ", " + bw + ", [], " + tw + ");";
                    ExecMatlab(exstr, JAA_OTHER, false);
                }
            }
        }

    }//GEN-LAST:event_jButton_BurstActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JA_Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_AddCursors;
    private javax.swing.JButton jButton_AutoTimegate;
    private javax.swing.JButton jButton_Baseline;
    private javax.swing.JButton jButton_Burst;
    private javax.swing.JButton jButton_ChangeDir;
    private javax.swing.JButton jButton_DelBunch;
    private javax.swing.JButton jButton_Fit;
    private javax.swing.JButton jButton_PlotCorrs;
    private javax.swing.JButton jButton_PlotTCSPC;
    private javax.swing.JButton jButton_RemCursors;
    private javax.swing.JButton jButton_SaveBunch;
    private javax.swing.JButton jButton_TCSPC;
    private javax.swing.JButton jButton_Trace;
    private javax.swing.JButton jButton_TwoFocusFCS;
    private javax.swing.JButton jButton_TwoFocusFit;
    private javax.swing.JComboBox jComboBox_Curves;
    private javax.swing.JComboBox jComboBox_WorkDir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel_Bunch;
    private javax.swing.JLabel jLabel_BunchStr;
    private javax.swing.JLabel jLabel_TimeStr;
    private javax.swing.JLabel jLabel_TraceBunch;
    private javax.swing.JList jList_Files;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollBar jScrollBar_Bunch;
    private javax.swing.JScrollBar jScrollBar_TraceBunch;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTree jTree_FileHeader;
    // End of variables declaration//GEN-END:variables

}
