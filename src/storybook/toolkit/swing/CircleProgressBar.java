package storybook.toolkit.swing;

import java.awt.Color;

import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;

@SuppressWarnings("serial")
public class CircleProgressBar extends JProgressBar
{
   private ProgressCircleUI ui;

   public CircleProgressBar()
   {
      super();
      initUI();
   }

   public CircleProgressBar(int arg0)
   {
      super(arg0);
      initUI();
   }

   public CircleProgressBar(BoundedRangeModel arg0)
   {
      super(arg0);
      initUI();
   }

   public CircleProgressBar(int arg0, int arg1)
   {
      super(arg0, arg1);
      initUI();
   }

   public CircleProgressBar(int arg0, int arg1, int arg2)
   {
      super(arg0, arg1, arg2);
      initUI();
   }
   /**
    * Get bar thickness.
    * @return bar thickness
    */
   public double getBarThickness()
   {
      return ui.getBarThickness();
   }

   /**
    * Set bar thickness.
    * @param barThickness bar thickness between 0.0 and 1.0
    */
   public void setBarThickness(double barThickness)
   {
      ui.setBarThickness(barThickness);
   }
   
    /**
     * Get back color.
    * @return the backColor
    */
   public Color getBackColor()
   {
      return ui.getBackColor();
   }

   /**
    * Set back color.
    * @param backColor the backColor to set
    */
   public void setBackColor(Color backColor)
   {
      ui.setBackColor(backColor);
   }
   
   private void initUI()
   {
      setStringPainted(true);
      ui = new ProgressCircleUI();
      setUI(ui);
   }

}
