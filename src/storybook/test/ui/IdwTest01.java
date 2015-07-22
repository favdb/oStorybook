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

package storybook.test.ui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class IdwTest01 extends JFrame {

	private static IdwTest01 instance;

	private void init() {
		initUi();
	}

	private void initUi() {
		setTitle("IdwTest01");
		setPreferredSize(new Dimension(600, 400));
		setLocation(400, 200);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		View[] views = new View[5];
		ViewMap viewMap = new ViewMap();
		for (int i = 0; i < views.length; i++) {
			views[i] = new View("View " + i, null, new JLabel("This is view "
					+ i + "!"));
			viewMap.addView(i, views[i]);
		}
		RootWindow rootWindow = DockingUtil.createRootWindow(viewMap, true);
		rootWindow.setWindow(new SplitWindow(true, 0.4f, new SplitWindow(false,
				views[0], new SplitWindow(false, views[1], views[2])),
				new TabWindow(new DockingWindow[] { views[3], views[4] })));
		rootWindow.getWindowBar(Direction.LEFT).setEnabled(true);
//		rootWindow.getWindowBar(Direction.DOWN).addTab(views[3]);

		add(rootWindow);

		pack();
		setVisible(true);
	}

	public static IdwTest01 getInstance() {
		if (instance == null) {
			instance = new IdwTest01();
		}
		return instance;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				IdwTest01.getInstance().init();
			}
		});
	}
}
