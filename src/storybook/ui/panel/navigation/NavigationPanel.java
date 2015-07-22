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

package storybook.ui.panel.navigation;

import java.beans.PropertyChangeEvent;

import javax.swing.JTabbedPane;

import storybook.controller.BookController;
import storybook.toolkit.I18N;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;

import net.infonode.docking.View;
import net.miginfocom.swing.MigLayout;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class NavigationPanel extends AbstractPanel {

	private JTabbedPane tabbedPane;

	public NavigationPanel(MainFrame mainFrame) {
		super(mainFrame);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		// Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();
		String propName = evt.getPropertyName();

		if (BookController.CommonProps.REFRESH.check(propName)) {
			View newView = (View) newValue;
			View view = (View) getParent().getParent();
			if (view == newView) {
				refresh();
			}
			return;
		}

		if (propName.startsWith("Edit") || propName.startsWith("Init")){
			return;
		}

		if (propName.contains("Scene") || propName.contains("Chapter")
				|| propName.contains("Strand")) {
			refresh();
			return;
		}
	}

	@Override
	public void refresh() {
		int index = tabbedPane.getSelectedIndex();
		super.refresh();
		tabbedPane.setSelectedIndex(index);
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("wrap,fill,ins 0"));

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(I18N.getMsg("msg.menu.navigate.goto.chapter"),
				new FindChapterPanel(mainFrame));
		tabbedPane.addTab(I18N.getMsg("msg.menu.navigate.goto.date"),
				new FindDatePanel(mainFrame));
		add(tabbedPane, "grow");
	}
}
