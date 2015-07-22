/*
Storybook: Open Source software for novelists and authors.
Copyright (C) 2008 - 2012 Martin Mustun

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package storybook.ui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import storybook.SbConstants;
import storybook.toolkit.I18N;
import storybook.toolkit.swing.SwingUtil;

import net.miginfocom.swing.MigLayout;
import storybook.ui.MainFrame;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class BlankPanel extends AbstractPanel {

	public BlankPanel(MainFrame mainFrame) {
		super(mainFrame);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("fill,wrap", "[center]", "[grow]"));
		setPreferredSize(new Dimension(900, 505));
		setBackground(SwingUtil.getBackgroundColor());
		setBorder(BorderFactory.createLoweredBevelBorder());

		removeAll();

		JLabel lbVersion = new JLabel();
		lbVersion.setText(SbConstants.Storybook.PRODUCT_FULL_NAME.toString());
		lbVersion.setForeground(new Color(0xb9, 0xcf, 0x70));

		JLabel lbLogo = new JLabel(I18N.getIcon("icon.logo.500.blurred"));

		add(lbLogo);
		add(lbVersion);

		revalidate();
		repaint();
	}
}
