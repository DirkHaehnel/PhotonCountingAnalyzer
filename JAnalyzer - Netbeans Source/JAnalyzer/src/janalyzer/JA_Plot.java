/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package janalyzer;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import org.jfree.chart.plot.*;

import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectList;


/**
 *
 * @author cpieper
 */
public class JA_Plot extends XYPlot {
    
    private java.util.Vector<JA_Cursor> cursors;
    private Rectangle2D lastDataArea = null;
    private int snapedToCursor = -1;

    public JA_Plot( XYDataset dataset,
                    ValueAxis domainAxis,
                    ValueAxis rangeAxis,
                    XYItemRenderer renderer) {
        super(dataset, domainAxis, rangeAxis, renderer);

        cursors = new java.util.Vector<JA_Cursor>();
    }

    public void addCursor(JA_Cursor cur) {
        cursors.add(cur);
    }

    public void removeCursor(int idx) {
        cursors.removeElementAt(idx);
    }

    public void changed() {
        fireChangeEvent();
    }

    public void setCursorPos(int cursor, double position) {
        int count = cursors.size();
        if (position == Double.NaN) {
            if (cursor < count) {
                cursors.remove(cursor);
            }
        }
        else if ((cursor >= 0) && (cursor < 8)) {
            
            for(int i = count; i <= cursor; i++)
                cursors.add(new JA_Cursor(Double.NaN));
            cursors.get(cursor).setCursorPos(position);
        }
    }

    public void removeAllCursor() {
        cursors.clear();
    }

    public int getCursorCount() {
        return cursors.size();
    }

    public double[] getCursorPositions() {
        double[] cpos = new double[cursors.size()];
        for (int i = 0; i < cursors.size(); i++) {
            cpos[i] = cursors.get(i).getCursorPos();
        }
        return cpos;
    }

    public Rectangle2D getLastDataArea() {
        return lastDataArea;
    }

    @Override
    protected void drawRangeCrosshair(Graphics2D g2, Rectangle2D dataArea,
            PlotOrientation orientation, double value, ValueAxis axis,
            Stroke stroke, Paint paint) {

        if (axis.getRange().contains(value)) {
            Line2D line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                double xx = axis.valueToJava2D(value, dataArea,
                        RectangleEdge.BOTTOM);
                line = new Line2D.Double(xx, dataArea.getMinY(), xx,
                        dataArea.getMaxY());
            }
            else {
                double yy = axis.valueToJava2D(value, dataArea,
                        RectangleEdge.LEFT);
                line = new Line2D.Double(dataArea.getMinX(), yy,
                        dataArea.getMaxX(), yy);
            }
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
        }

    }

    /**
     * Draws the plot within the specified area on a graphics device.
     *
     * @param g2  the graphics device.
     * @param area  the plot area (in Java2D space).
     * @param anchor  an anchor point in Java2D space (<code>null</code>
     *                permitted).
     * @param parentState  the state from the parent plot, if there is one
     *                     (<code>null</code> permitted).
     * @param info  collects chart drawing information (<code>null</code>
     *              permitted).
     */
    @Override
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor,
            PlotState parentState, PlotRenderingInfo info) {

        RectangleInsets axisOffset = getAxisOffset();
        int rendercount = getRendererCount();
        ObjectList renderers = new ObjectList();
        for (int i = 0; i<rendercount; i++)
            renderers.set(i, getRenderer(i));

        // if the plot area is too small, just return...
        boolean b1 = (area.getWidth() <= MINIMUM_WIDTH_TO_DRAW);
        boolean b2 = (area.getHeight() <= MINIMUM_HEIGHT_TO_DRAW);
        if (b1 || b2) {
            return;
        }

        // record the plot area...
        if (info != null) {
            info.setPlotArea(area);
        }

        // adjust the drawing area for the plot insets (if any)...
        RectangleInsets insets = getInsets();
        insets.trim(area);

        AxisSpace space = calculateAxisSpace(g2, area);
        Rectangle2D dataArea = space.shrink(area, null);

        axisOffset.trim(dataArea);
        createAndAddEntity((Rectangle2D) dataArea.clone(), info, null, null);
        if (info != null) {
            info.setDataArea(dataArea);
        }

        // draw the plot background and axes...
        drawBackground(g2, dataArea);
        Map axisStateMap = drawAxes(g2, area, dataArea, info);

        PlotOrientation orient = getOrientation();

        // the anchor point is typically the point where the mouse last
        // clicked - the crosshairs will be driven off this point...
        if (anchor != null && !dataArea.contains(anchor)) {
            anchor = null;
        }
        CrosshairState crosshairState = new CrosshairState();
        crosshairState.setCrosshairDistance(Double.POSITIVE_INFINITY);
        crosshairState.setAnchor(anchor);

        crosshairState.setAnchorX(Double.NaN);
        crosshairState.setAnchorY(Double.NaN);
        if (anchor != null) {
            ValueAxis domainAxis = getDomainAxis();
            if (domainAxis != null) {
                double x;
                if (orient == PlotOrientation.VERTICAL) {
                    x = domainAxis.java2DToValue(anchor.getX(), dataArea,
                            getDomainAxisEdge());
                }
                else {
                    x = domainAxis.java2DToValue(anchor.getY(), dataArea,
                            getDomainAxisEdge());
                }
                crosshairState.setAnchorX(x);
            }
            ValueAxis rangeAxis = getRangeAxis();
            if (rangeAxis != null) {
                double y;
                if (orient == PlotOrientation.VERTICAL) {
                    y = rangeAxis.java2DToValue(anchor.getY(), dataArea,
                            getRangeAxisEdge());
                }
                else {
                    y = rangeAxis.java2DToValue(anchor.getX(), dataArea,
                            getRangeAxisEdge());
                }
                crosshairState.setAnchorY(y);
            }
        }
        crosshairState.setCrosshairX(getDomainCrosshairValue());
        crosshairState.setCrosshairY(getRangeCrosshairValue());
        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();

        g2.clip(dataArea);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                getForegroundAlpha()));

        AxisState domainAxisState = (AxisState) axisStateMap.get(
                getDomainAxis());
        if (domainAxisState == null) {
            if (parentState != null) {
                domainAxisState = (AxisState) parentState.getSharedAxisStates()
                        .get(getDomainAxis());
            }
        }

        AxisState rangeAxisState = (AxisState) axisStateMap.get(getRangeAxis());
        if (rangeAxisState == null) {
            if (parentState != null) {
                rangeAxisState = (AxisState) parentState.getSharedAxisStates()
                        .get(getRangeAxis());
            }
        }
        if (domainAxisState != null) {
            drawDomainTickBands(g2, dataArea, domainAxisState.getTicks());
        }
        if (rangeAxisState != null) {
            drawRangeTickBands(g2, dataArea, rangeAxisState.getTicks());
        }
        if (domainAxisState != null) {
            drawDomainGridlines(g2, dataArea, domainAxisState.getTicks());
            drawZeroDomainBaseline(g2, dataArea);
        }
        if (rangeAxisState != null) {
            drawRangeGridlines(g2, dataArea, rangeAxisState.getTicks());
            drawZeroRangeBaseline(g2, dataArea);
        }

        // draw the markers that are associated with a specific renderer...
        for (int i = 0; i < renderers.size(); i++) {
            drawDomainMarkers(g2, dataArea, i, Layer.BACKGROUND);
        }
        for (int i = 0; i < renderers.size(); i++) {
            drawRangeMarkers(g2, dataArea, i, Layer.BACKGROUND);
        }

        // now draw annotations and render data items...
        boolean foundData = false;
        DatasetRenderingOrder order = getDatasetRenderingOrder();
        if (order == DatasetRenderingOrder.FORWARD) {

            // draw background annotations
            int rendererCount = renderers.size();
            for (int i = 0; i < rendererCount; i++) {
                XYItemRenderer r = getRenderer(i);
                if (r != null) {
                    ValueAxis domainAxis = getDomainAxisForDataset(i);
                    ValueAxis rangeAxis = getRangeAxisForDataset(i);
                    r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis,
                            Layer.BACKGROUND, info);
                }
            }

            // render data items...
            for (int i = 0; i < getDatasetCount(); i++) {
                foundData = render(g2, dataArea, i, info, crosshairState)
                    || foundData;
            }

            // draw foreground annotations
            for (int i = 0; i < rendererCount; i++) {
                XYItemRenderer r = getRenderer(i);
                if (r != null) {
                    ValueAxis domainAxis = getDomainAxisForDataset(i);
                    ValueAxis rangeAxis = getRangeAxisForDataset(i);
                    r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis,
                            Layer.FOREGROUND, info);
                }
            }

        }
        else if (order == DatasetRenderingOrder.REVERSE) {

            // draw background annotations
            int rendererCount = renderers.size();
            for (int i = rendererCount - 1; i >= 0; i--) {
                XYItemRenderer r = getRenderer(i);
                if (i >= getDatasetCount()) { // we need the dataset to make
                    continue;                 // a link to the axes
                }
                if (r != null) {
                    ValueAxis domainAxis = getDomainAxisForDataset(i);
                    ValueAxis rangeAxis = getRangeAxisForDataset(i);
                    r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis,
                            Layer.BACKGROUND, info);
                }
            }

            for (int i = getDatasetCount() - 1; i >= 0; i--) {
                foundData = render(g2, dataArea, i, info, crosshairState)
                    || foundData;
            }

            // draw foreground annotations
            for (int i = rendererCount - 1; i >= 0; i--) {
                XYItemRenderer r = getRenderer(i);
                if (i >= getDatasetCount()) { // we need the dataset to make
                    continue;                 // a link to the axes
                }
                if (r != null) {
                    ValueAxis domainAxis = getDomainAxisForDataset(i);
                    ValueAxis rangeAxis = getRangeAxisForDataset(i);
                    r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis,
                            Layer.FOREGROUND, info);
                }
            }

        }

        // draw Cursors...
        int cursorcount = cursors.size();

        ValueAxis xAxis = getDomainAxis(0);
        RectangleEdge xAxisEdge = getDomainAxisEdge(0);

        for (int i = 0; i<cursorcount; i++) {
            JA_Cursor cur = cursors.get(i);
            Color paint;
            double x = cur.getCursorPos();
            int n = (i % 4);
            int brt = 150;
            int brt2 = 0;
            if (i == snapedToCursor) {
                brt = 255;
                brt2 = 50;
            }
            switch (n) {
                case 0:
                  paint = new Color(brt,0,0);
                  break;
                case 1:
                  paint = new Color(brt2,brt2,brt);
                  break;
                case 2:
                  paint = new Color(brt2,brt,brt2);
                  break;
                case 3:
                  paint = new Color(brt,brt,0);
                  break;
                default:
                  paint = Color.BLACK;
            }

            BasicStroke stroke = new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] {8, 6}, 0.0f);
            if (x != Double.NaN) {
                drawDomainCrosshair(g2, dataArea, orient, x, xAxis, stroke, paint);
                // Draw Label
                if (xAxis.getRange().contains(x)) {
                    double minY = dataArea.getMinY();
                    double xx = xAxis.valueToJava2D(x, dataArea,
                                RectangleEdge.BOTTOM);

                    //g2.setStroke(stroke);
                    //g2.setPaint(paint);
                    //g2.draw(line);
                    g2.drawString(Integer.toString(i+1), (float)(xx+3), (float)(minY+13));
                }
            }
        }

        if (!foundData) {
            drawNoDataMessage(g2, dataArea);
        }

        for (int i = 0; i < renderers.size(); i++) {
            drawDomainMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }
        for (int i = 0; i < renderers.size(); i++) {
            drawRangeMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }

        drawAnnotations(g2, dataArea, info);
        g2.setClip(originalClip);
        g2.setComposite(originalComposite);

        drawOutline(g2, dataArea);
        lastDataArea = dataArea;
    }

    /**
     * @param snapedToCursor the snapedToCursor to set
     */
    public void setSnapedToCursor(int snapedToCursor) {
        this.snapedToCursor = snapedToCursor;
    }

}
