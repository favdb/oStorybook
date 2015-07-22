/*
 * SbApp: Open Source software for novelists and authors.
 * Original idea 2008 - 2012 Martin Mustun
 * Copyrigth (C) Favdb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package storybook.ui.chart.jfreechart;

import java.awt.Color;
import org.jfree.data.xy.XYDataItem;

public class ColorXYDataItem extends XYDataItem {
  private Color color;

  public ColorXYDataItem(Number p1, Number p2, Color color)
  {
    super(p1, p2);
    this.color = color;
  }

  public ColorXYDataItem(double paramDouble1, double paramDouble2, Color paramColor)
  {
    super(paramDouble1, paramDouble2);
    this.color = paramColor;
  }

  public Color getColor()
  {
    return this.color;
  }
}