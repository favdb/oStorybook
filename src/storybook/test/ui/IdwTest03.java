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

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import storybook.ui.SbView;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings({ "serial" })
public class IdwTest03 extends JFrame {

	private static IdwTest03 instance;

	private void init() {
		initUi();
	}

	private void initUi() {
		setLayout(new MigLayout("wrap,fill"));
		setTitle("IdwTest03");
		setPreferredSize(new Dimension(600, 400));
		setLocation(400, 200);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		TestComponentFactory factory = TestComponentFactory.getInstance();
		MainDockingWindowAdapter adapter = new MainDockingWindowAdapter();

		SbView[] views = new SbView[2];
		ViewMap viewMap = new ViewMap();

		views[0] = new SbView("View 1");
		factory.setView0(views[0]);
		views[0].addListener(adapter);
		viewMap.addView(0, views[0]);

		views[1] = new SbView("View 2");
		factory.setView1(views[1]);
		views[1].addListener(adapter);
		viewMap.addView(1, views[1]);

		RootWindow rootWindow = DockingUtil.createRootWindow(viewMap, true);
		rootWindow.setName("rootWindow");
		rootWindow.setWindow(new SplitWindow(true, views[0], views[1]));
		rootWindow.getWindowBar(Direction.LEFT).setEnabled(true);

		add(rootWindow, "grow");

		pack();
		setVisible(true);
	}

	private class MainDockingWindowAdapter extends DockingWindowAdapter {
		@Override
		public void windowAdded(DockingWindow addedToWindow,
				DockingWindow addedWindow) {
			System.out
					.println("IdwTest03.MainDockingWindowAdapter.windowAdded(): addedWindow:"
							+ addedWindow);
			System.out
					.println("IdwTest03.MainDockingWindowAdapter.windowAdded(): addedWindow is view:"
							+ (addedWindow instanceof View));
			if (addedWindow != null && addedWindow instanceof SbView) {
				SbView view = (SbView) addedWindow;
				if (!view.isLoaded()) {
					System.out
							.println("IdwTest03.MainDockingWindowAdapter.windowRemoved(): set component");
					TestComponentFactory factory = TestComponentFactory
							.getInstance();
					JComponent comp = factory.getComponent(view);
					view.load(comp);
				}
			}
		}

		@Override
		public void windowClosed(DockingWindow window) {
			System.out
					.println("IdwTest03.MainDockingWindowAdapter.windowClosed(): window:"
							+ window);
			if (window != null && window instanceof SbView) {
				System.out
						.println("IdwTest03.MainDockingWindowAdapter.windowClosed(): remove component");
				SbView view = (SbView) window;
				view.unload();
			}
		}

		@Override
		public void windowClosing(DockingWindow window)
				throws OperationAbortedException {
		}

		@Override
		public void windowRemoved(DockingWindow removedFromWindow,
				DockingWindow removedWindow) {
		}

		@Override
		public void windowShown(DockingWindow window) {
		}
	}

	public static IdwTest03 getInstance() {
		if (instance == null) {
			instance = new IdwTest03();
		}
		return instance;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				IdwTest03.getInstance().init();
			}
		});
	}
}
