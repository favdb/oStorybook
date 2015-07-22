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

import java.awt.Dimension;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;
import storybook.model.hbn.entity.Person;
import storybook.toolkit.swing.ColorUtil;
import storybook.toolkit.swing.label.CleverLabel;
import storybook.ui.MainFrame;

public class PersonsLegendPanel extends AbstractLegendPanel
{
  private Collection<Person> collection;

  public PersonsLegendPanel(MainFrame paramMainFrame, Set<Person> paramSet)
  {
    super(paramMainFrame);
    this.collection = paramSet;
    initAll();
  }

	@Override
  public void initUi()
  {
    setLayout(new MigLayout("wrap 10"));
    setOpaque(false);
    Iterator localIterator = this.collection.iterator();
    while (localIterator.hasNext())
    {
      Person localPerson = (Person)localIterator.next();
      CleverLabel localCleverLabel = new CleverLabel(localPerson.getAbbreviation(), 0);
      localCleverLabel.setPreferredSize(new Dimension(50, 18));
      if (localPerson.getColor() != null) {
			localCleverLabel.setBackground(ColorUtil.darker(localPerson.getJColor(), 0.05D));
		}
      else {
			localCleverLabel.setBackground(ColorUtil.getNiceDarkGray());
		}
      JLabel localJLabel = new JLabel();
      localJLabel.setText(localPerson.getFullName());
      add(localCleverLabel, "sg");
      add(localJLabel, "gap after 10");
    }
  }
}