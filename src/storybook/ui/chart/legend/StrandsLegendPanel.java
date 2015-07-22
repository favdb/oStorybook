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
import java.util.Iterator;
import javax.swing.JLabel;
import org.hibernate.Session;
import storybook.model.BookModel;
import storybook.model.hbn.dao.StrandDAOImpl;
import storybook.model.hbn.entity.Strand;
import storybook.toolkit.I18N;
import storybook.toolkit.swing.ColorUtil;
import storybook.toolkit.swing.label.CleverLabel;
import storybook.ui.MainFrame;

public class StrandsLegendPanel extends AbstractLegendPanel
{
  public StrandsLegendPanel(MainFrame paramMainFrame)
  {
    super(paramMainFrame);
    initAll();
  }

	@Override
  public void initUi()
  {
    setOpaque(false);
    BookModel localDocumentModel = this.mainFrame.getBookModel();
    Session localSession = localDocumentModel.beginTransaction();
    StrandDAOImpl localStrandDAOImpl = new StrandDAOImpl(localSession);
    add(new JLabel(I18N.getMsg("msg.report.caption.strands")), "gapright 5");
    Iterator localIterator = localStrandDAOImpl.findAllOrderBySort().iterator();
    while (localIterator.hasNext())
    {
      Strand localStrand = (Strand)localIterator.next();
      CleverLabel localCleverLabel = new CleverLabel(localStrand.getName(), 0);
      localCleverLabel.setPreferredSize(new Dimension(100, 20));
      localCleverLabel.setBackground(ColorUtil.darker(localStrand.getJColor(), 0.05D));
      add(localCleverLabel, "sg");
    }
  }
}