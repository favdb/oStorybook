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
import java.awt.Dimension;
import javax.swing.JLabel;
import storybook.toolkit.I18N;

public class ChapterLegendPanel extends AbstractLegendPanel {

	@Override
	public void initUi() {
		JLabel localJLabel = new JLabel("", 0);
		localJLabel.setPreferredSize(new Dimension(20, 20));
		localJLabel.setOpaque(true);
		localJLabel.setBackground(Color.lightGray);
		add(localJLabel, "sg");
		add(new JLabel(I18N.getMsg("msg.common.chapter")), "gapright 5");
	}
}