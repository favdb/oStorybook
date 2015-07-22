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

package storybook.toolkit.swing.panel;

import java.beans.PropertyChangeEvent;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import storybook.toolkit.I18N;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;

import net.miginfocom.swing.MigLayout;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class ViewsRadioButtonPanel extends AbstractPanel {

	private JRadioButton rbChrono;
	private JRadioButton rbBook;
	private JRadioButton rbManage;
	private boolean showManage;

	public ViewsRadioButtonPanel(MainFrame mainFrame) {
		this(mainFrame, true);
	}

	public ViewsRadioButtonPanel(MainFrame mainFrame, boolean showManage) {
		super(mainFrame);
		this.showManage = showManage;
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
		setLayout(new MigLayout("ins 0,wrap 2", "[]", "[]"));

		JLabel lbChronoIcon = new JLabel(I18N.getIcon("icon.small.chrono.view"));
		rbChrono = new JRadioButton();
		rbChrono.setText(I18N.getMsg("msg.menu.view.chrono"));
		rbChrono.setSelected(true);
		JLabel lbBookIcon = new JLabel(I18N.getIcon("icon.small.book.view"));
		rbBook = new JRadioButton();
		rbBook.setText(I18N.getMsg("msg.menu.view.book"));
		JLabel lbManageIcon = new JLabel(I18N.getIcon("icon.small.manage.view"));
		if (showManage) {
			rbManage = new JRadioButton();
			rbManage.setText(I18N.getMsg("msg.menu.view.manage"));
		}
		ButtonGroup btGroup = new ButtonGroup();
		btGroup.add(rbChrono);
		btGroup.add(rbBook);
		if (showManage) {
			btGroup.add(rbManage);
		}

		// layout
		add(lbChronoIcon);
		add(rbChrono);
		add(lbBookIcon, "");
		add(rbBook, "");
		if (showManage) {
			add(lbManageIcon, "");
			add(rbManage, "");
		}
	}

	public boolean isChronoSelected() {
		return rbChrono.isSelected();
	}

	public boolean isBookSelected() {
		return rbBook.isSelected();
	}

	public boolean isManageSelected() {
		return rbManage.isSelected();
	}
}
