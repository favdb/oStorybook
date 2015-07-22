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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import storybook.controller.BookController;
import storybook.ui.MainFrame;
import storybook.ui.interfaces.IPaintable;

import net.miginfocom.swing.MigLayout;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class DebugInfoPanel extends JPanel implements IPaintable,
		ActionListener {

	private MainFrame mainFrame;
	private Timer timer;
	private JLabel lbAttachedViews;

	public DebugInfoPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		init();
		initUi();
	}

	@Override
	public void init() {
		timer = new Timer(2000, this);
		timer.start();
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("flowx,ins 0"));
		setPreferredSize(new Dimension(200, 30));
		lbAttachedViews = new JLabel();
		add(lbAttachedViews);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		BookController ctrl = mainFrame.getBookController();
		int n = ctrl.getNumberOfAttachedViews();
		lbAttachedViews.setText("Attached Views: " + Integer.toString(n));
		lbAttachedViews.setToolTipText("<html>"+ctrl.getInfoAttachedViews(true));
	}
}
