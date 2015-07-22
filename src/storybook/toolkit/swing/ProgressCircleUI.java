package storybook.toolkit.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class ProgressCircleUI extends BasicProgressBarUI {
   private static final double BAR_THICKNESS = 0.25;
   
   private double barThickness = BAR_THICKNESS;
   private Color backColor = null;
   
   @Override
   public Dimension getPreferredSize(JComponent c) {
      Dimension d = super.getPreferredSize(c);
      int v = Math.max(d.width, d.height);
      d.setSize(v, v);
      return d;
    }

   /**
    * Get bar thickness.
    * @return bar thickness
    */
   public double getBarThickness()
   {
      return barThickness;
   }

   /**
    * Set bar thickness.
    * @param barThickness bar thickness between 0.0 and 1.0
    */
   public void setBarThickness(double barThickness)
   {
      if ((barThickness >= 0.0) && (barThickness <= 1.0)) {
          this.barThickness = barThickness;
      }
   }
   
    /**
     * Get back color.
    * @return the backColor
    */
   public Color getBackColor()
   {
      return backColor;
   }

   /**
    * Set back color.
    * @param backColor the backColor to set
    */
   public void setBackColor(Color backColor)
   {
      this.backColor = backColor;
   }

   @Override public void paint(Graphics g, JComponent c) {
      Insets b = progressBar.getInsets(); // area for border
      int barRectWidth  = progressBar.getWidth()  - b.right - b.left;
      int barRectHeight = progressBar.getHeight() - b.top - b.bottom;
      if (barRectWidth <= 0 || barRectHeight <= 0) {
        return;
      }

      // draw the cells
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_ON);

      if (backColor != null) {
         g2.setPaint(backColor);
      } else {
         g2.setPaint(progressBar.getForeground().brighter());
      }
      
      drawPart(g2, 360.0, barRectWidth, barRectHeight, b);

      g2.setPaint(progressBar.getForeground());
      double degree = 360 * progressBar.getPercentComplete();
      drawPart(g2, degree, barRectWidth, barRectHeight, b);
      
      g2.dispose();

      // Deal with possible text painting
      if (progressBar.isStringPainted()) {
        Font fontref = progressBar.getFont();
        double sz = Math.min(barRectWidth, barRectHeight);
        Font font = new Font(fontref.getName(), fontref.getStyle(),(int) (sz / 4));
        g.setColor(Color.GREEN);
        progressBar.setFont(font);
        this.
        paintString(g, b.left, b.top, barRectWidth, barRectHeight, 0, b);
      }
    }
    
    private void drawPart(Graphics2D g2, double degree, int width, int height, Insets b)
    {
       double sz = Math.min(width, height);
       double cx = b.left + width  * .5;
       double cy = b.top  + height * .5;
       double or = sz * .5;
       double ir = or * (1.0 - barThickness);
       Shape inner = new Ellipse2D.Double(cx - ir, cy - ir, ir * 2, ir * 2);
       Shape outer = new Arc2D.Double(
           cx - or, cy - or, sz, sz, 90 - degree, degree, Arc2D.PIE);
       Area area = new Area(outer);
       area.subtract(new Area(inner));
       g2.fill(area);
       
    }
  }