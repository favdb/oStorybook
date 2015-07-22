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

package storybook.ui.edit;

import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;

import storybook.model.EntityUtil;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.ui.panel.AbstractPanel;

import net.miginfocom.swing.MigLayout;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class TitlePanel extends AbstractPanel {

	private JLabel lbIcon;
	private JLabel lbTitle;

	public TitlePanel() {
		initAll();
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("flowx,ins 2"));
		setOpaque(false);
		lbIcon = new JLabel();
		add(lbIcon);
		lbTitle = new JLabel();
		add(lbTitle);
	}

	public void refresh(AbstractEntity entity) {
		lbIcon.setIcon(EntityUtil.getEntityIcon(entity));
		StringBuilder buf = new StringBuilder();
		buf.append("<html>\n");
		buf.append(EntityUtil.getEntityFullTitle(entity));
		buf.append("\n");
		lbTitle.setText(buf.toString());
	}
}
