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

package storybook.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.ViewUtil;
import storybook.ui.panel.AbstractPanel;

/**
 * @author martin
 *
 */
public class ScrollToEntityAction implements ActionListener {

	private boolean found = false;
	private AbstractPanel container;
	private JPanel panel;
	private AbstractEntity entity;

	public ScrollToEntityAction(AbstractPanel container, JPanel panel,
			AbstractEntity entity) {
		this.container = container;
		this.panel = panel;
		this.entity = entity;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (entity instanceof Scene) {
			found = ViewUtil.doScrolling(container, panel, (Scene) entity);
		} else if (entity instanceof Chapter) {
			found = ViewUtil.doScrolling(container, panel, (Chapter) entity);
		}
	}

	public boolean isFound() {
		return found;
	}
}
