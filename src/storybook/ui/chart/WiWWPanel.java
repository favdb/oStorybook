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
package storybook.ui.chart;

import storybook.model.hbn.entity.Person;
import storybook.toolkit.swing.ColorUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.toolkit.swing.label.CleverLabel;
import storybook.ui.interfaces.IPaintable;
import storybook.ui.interfaces.IRefreshable;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class WiWWPanel extends JPanel
  implements IRefreshable, IPaintable
{
  private WiWWContainer container;
  private boolean isSelected;

  public WiWWPanel(WiWWContainer paramWiWWContainer)
  {
    this(paramWiWWContainer, false);
  }

  public WiWWPanel(WiWWContainer paramWiWWContainer, boolean paramBoolean)
  {
    this.container = paramWiWWContainer;
    this.isSelected = paramBoolean;
    init();
    initUi();
  }

  public void init()
  {
  }

  public void initUi()
  {
    setLayout(new MigLayout("ins 0 1 0 1"));
    Color localColor1 = SwingUtil.getTableSelectionBackgroundColor();
    if (this.isSelected)
      setBackground(ColorUtil.blend(Color.white, localColor1, 0.75D));
    else
      setBackground(Color.white);
    Iterator localIterator = this.container.getCharacterList().iterator();
    while (localIterator.hasNext())
    {
      Person localPerson = (Person)localIterator.next();
      CleverLabel localCleverLabel = new CleverLabel("", 0);
      Color localColor2 = localPerson.getJColor() == null ? ColorUtil.getNiceDarkGray() : localPerson.getJColor();
      Color localColor3;
      if (this.isSelected)
        localColor3 = ColorUtil.blend(localColor2, localColor1, 0.85D);
      else
        localColor3 = localColor2;
      localCleverLabel.setText(localPerson.getAbbreviation());
      localCleverLabel.setBackground(localColor3);
      localCleverLabel.setPreferredSize(new Dimension(30, 20));
      add(localCleverLabel);
    }
  }

  public void refresh()
  {
    removeAll();
    init();
    initUi();
  }
}