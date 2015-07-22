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
package storybook.ui.chart.legend;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import net.miginfocom.swing.MigLayout;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;

public abstract class AbstractLegendPanel extends AbstractPanel
{
  public AbstractLegendPanel()
  {
  }

  public AbstractLegendPanel(MainFrame paramMainFrame)
  {
    super(paramMainFrame);
  }

	@Override
  public void modelPropertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
  }

	@Override
  public void init()
  {
  }

	@Override
  public void initUi()
  {
    setLayout(new MigLayout("flowx"));
    setBackground(Color.white);
  }
}