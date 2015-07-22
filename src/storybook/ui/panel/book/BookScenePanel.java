/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008 - 2011 Martin Mustun

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

package storybook.ui.panel.book;

import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;

import storybook.model.EntityUtil;
import storybook.model.hbn.entity.Scene;
import storybook.ui.panel.AbstractScenePanel;
import storybook.ui.MainFrame;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class BookScenePanel extends AbstractScenePanel {

	private JPanel cmdPanel;
	private BookTextPanel textPanel;
	private BookInfoPanel infoPanel;

	public BookScenePanel(MainFrame mainFrame, Scene scene) {
		super(mainFrame, scene);
		init();
		initUi();
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		refresh();
	}

	@Override
	public void refresh() {
		MigLayout layout = new MigLayout(
				"wrap 3,fill",
				"[]",
				"[top]"
		);
		setLayout(layout);
		setOpaque(false);
		setComponentPopupMenu(EntityUtil.createPopupMenu(mainFrame, scene));

		removeAll();

		// info panel
		infoPanel = new BookInfoPanel(mainFrame, scene);

		// text panel
		textPanel = new BookTextPanel(mainFrame, scene);

		// command panel
		cmdPanel = createCommandPanel();

		// layout
		add(infoPanel, "w 250,h 250");
		add(textPanel, "grow,gap 10");
		add(cmdPanel);

		revalidate();
		repaint();
	}

	private JPanel createCommandPanel() {
		JPanel panel = new JPanel(new MigLayout("flowy,insets 0"));
		panel.setOpaque(false);

		// layout
		panel.add(getEditButton());
		panel.add(getDeleteButton());
		panel.add(getNewButton());

		return panel;
	}

	protected BookScenePanel getThis() {
		return this;
	}
}
