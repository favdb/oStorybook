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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.ViewSerializer;
import net.infonode.docking.WindowBar;
import net.infonode.docking.drag.DockingWindowDragSource;
import net.infonode.docking.drag.DockingWindowDragger;
import net.infonode.docking.drag.DockingWindowDraggerProvider;
import net.infonode.docking.mouse.DockingWindowActionMouseButtonListener;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.BlueHighlightDockingTheme;
import net.infonode.docking.theme.ClassicDockingTheme;
import net.infonode.docking.theme.DefaultDockingTheme;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.GradientDockingTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.theme.SlimFlatDockingTheme;
import net.infonode.docking.theme.SoftBlueIceDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.MixedViewHandler;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;

/**
 * A small example on how to use InfoNode Docking Windows. This example shows
 * how to handle both static and dynamic views in the same root window.
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class IdwTest02 {
	private static final int ICON_SIZE = 8;

	/**
	 * Custom view icon.
	 */
	private static final Icon VIEW_ICON = new Icon() {
		public int getIconHeight() {
			return ICON_SIZE;
		}

		public int getIconWidth() {
			return ICON_SIZE;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			Color oldColor = g.getColor();

			g.setColor(new Color(70, 70, 70));
			g.fillRect(x, y, ICON_SIZE, ICON_SIZE);

			g.setColor(new Color(100, 230, 100));
			g.fillRect(x + 1, y + 1, ICON_SIZE - 2, ICON_SIZE - 2);

			g.setColor(oldColor);
		}
	};

	/**
	 * Custom view button icon.
	 */
	private static final Icon BUTTON_ICON = new Icon() {
		public int getIconHeight() {
			return ICON_SIZE;
		}

		public int getIconWidth() {
			return ICON_SIZE;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			Color oldColor = g.getColor();

			g.setColor(Color.BLACK);
			g.fillOval(x, y, ICON_SIZE, ICON_SIZE);

			g.setColor(oldColor);
		}
	};

	/**
	 * The one and only root window
	 */
	private RootWindow rootWindow;

	/**
	 * An array of the static views
	 */
	private View[] views = new View[10];

	/**
	 * Contains all the static views
	 */
	private ViewMap viewMap = new ViewMap();

	/**
	 * The view menu items
	 */
	private JMenuItem[] viewItems = new JMenuItem[views.length];

	/**
	 * Contains the dynamic views that has been added to the root window
	 */
	private HashMap dynamicViews = new HashMap();

	/**
	 * The currently applied docking windows theme
	 */
	private DockingWindowsTheme currentTheme = new ShapedGradientDockingTheme();

	/**
	 * A dynamically created view containing an id.
	 */
	private static class DynamicView extends View {
		private int id;

		/**
		 * Constructor.
		 *
		 * @param title
		 *            the view title
		 * @param icon
		 *            the view icon
		 * @param component
		 *            the view component
		 * @param id
		 *            the view id
		 */
		DynamicView(String title, Icon icon, Component component, int id) {
			super(title, icon, component);
			this.id = id;
		}

		/**
		 * Returns the view id.
		 *
		 * @return the view id
		 */
		public int getId() {
			return id;
		}
	}

	/**
	 * In this properties object the modified property values for close buttons
	 * etc. are stored. This object is cleared when the theme is changed.
	 */
	private RootWindowProperties properties = new RootWindowProperties();

	/**
	 * Where the layouts are stored.
	 */
	private byte[][] layouts = new byte[3][];

	/**
	 * The application frame
	 */
	private JFrame frame = new JFrame("InfoNode Docking Windows Example");

	public IdwTest02() {
		createRootWindow();
		setDefaultLayout();
		showFrame();
	}

	/**
	 * Creates a view component containing the specified text.
	 *
	 * @param text
	 *            the text
	 * @return the view component
	 */
	private static JComponent createViewComponent(String text) {
		StringBuffer sb = new StringBuffer();

		for (int j = 0; j < 100; j++)
			sb.append(text + ". This is line " + j + "\n");

		return new JScrollPane(new JTextArea(sb.toString()));
	}

	/**
	 * Returns a dynamic view with specified id, reusing an existing view if
	 * possible.
	 *
	 * @param id
	 *            the dynamic view id
	 * @return the dynamic view
	 */
	private View getDynamicView(int id) {
		View view = (View) dynamicViews.get(new Integer(id));

		if (view == null)
			view = new DynamicView("Dynamic View " + id, VIEW_ICON,
					createViewComponent("Dynamic View " + id), id);

		return view;
	}

	/**
	 * Returns the next available dynamic view id.
	 *
	 * @return the next available dynamic view id
	 */
	private int getDynamicViewId() {
		int id = 0;

		while (dynamicViews.containsKey(new Integer(id)))
			id++;

		return id;
	}

	/**
	 * Creates the root window and the views.
	 */
	@SuppressWarnings("unchecked")
	private void createRootWindow() {
		// Create the views
		for (int i = 0; i < views.length; i++) {
			views[i] = new View("View " + i, VIEW_ICON,
					createViewComponent("View " + i));
			viewMap.addView(i, views[i]);
		}

		// Create a custom view button and add it to view 2
		JButton button = new JButton(BUTTON_ICON);
		button.setOpaque(false);
		button.setBorder(null);
		button.setFocusable(false);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame,
						"You clicked the custom view button.",
						"Custom View Button", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		views[2].getCustomTabComponents().add(button);

		// The mixed view map makes it easy to mix static and dynamic views
		// inside the same root window
		MixedViewHandler handler = new MixedViewHandler(viewMap,
				new ViewSerializer() {
					public void writeView(View view, ObjectOutputStream out)
							throws IOException {
						out.writeInt(((DynamicView) view).getId());
					}

					public View readView(ObjectInputStream in)
							throws IOException {
						return getDynamicView(in.readInt());
					}
				});

		rootWindow = DockingUtil.createRootWindow(viewMap, handler, true);

		// Set gradient theme. The theme properties object is the super object
		// of our properties object, which
		// means our property value settings will override the theme values
		properties.addSuperObject(currentTheme.getRootWindowProperties());

		// Our properties object is the super object of the root window
		// properties object, so all property values of the
		// theme and in our property object will be used by the root window
		rootWindow.getRootWindowProperties().addSuperObject(properties);

		// Enable the bottom window bar
		rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);

		// Add a listener which shows dialogs when a window is closing or
		// closed.
		rootWindow.addListener(new DockingWindowAdapter() {
			public void windowAdded(DockingWindow addedToWindow,
					DockingWindow addedWindow) {
				updateViews(addedWindow, true);
			}

			public void windowRemoved(DockingWindow removedFromWindow,
					DockingWindow removedWindow) {
				updateViews(removedWindow, false);
			}

			public void windowClosing(DockingWindow window)
					throws OperationAbortedException {
				if (JOptionPane.showConfirmDialog(frame,
						"Really close window '" + window + "'?") != JOptionPane.YES_OPTION)
					throw new OperationAbortedException(
							"Window close was aborted!");
			}

		});

		// Add a mouse button listener that closes a window when it's clicked
		// with the middle mouse button.
		rootWindow
				.addTabMouseButtonListener(DockingWindowActionMouseButtonListener.MIDDLE_BUTTON_CLOSE_LISTENER);
	}

	/**
	 * Update view menu items and dynamic view map.
	 *
	 * @param window
	 *            the window in which to search for views
	 * @param added
	 *            if true the window was added
	 */
	@SuppressWarnings("unchecked")
	private void updateViews(DockingWindow window, boolean added) {
		if (window instanceof View) {
			if (window instanceof DynamicView) {
				if (added)
					dynamicViews
							.put(new Integer(((DynamicView) window).getId()),
									window);
				else
					dynamicViews.remove(new Integer(((DynamicView) window)
							.getId()));
			} else {
				for (int i = 0; i < views.length; i++)
					if (views[i] == window && viewItems[i] != null)
						viewItems[i].setEnabled(!added);
			}
		} else {
			for (int i = 0; i < window.getChildWindowCount(); i++)
				updateViews(window.getChildWindow(i), added);
		}
	}

	/**
	 * Sets the default window layout.
	 */
	private void setDefaultLayout() {
		TabWindow tabWindow = new TabWindow(views);

		rootWindow.setWindow(new SplitWindow(true, 0.3f, new SplitWindow(false,
				0.7f, new TabWindow(new View[] { views[0], views[1] }),
				views[2]), tabWindow));

		WindowBar windowBar = rootWindow.getWindowBar(Direction.DOWN);

		while (windowBar.getChildWindowCount() > 0)
			windowBar.getChildWindow(0).close();

		windowBar.addTab(views[3]);
	}

	/**
	 * Initializes the frame and shows it.
	 */
	private void showFrame() {
		frame.getContentPane().add(createToolBar(), BorderLayout.NORTH);
		frame.getContentPane().add(rootWindow, BorderLayout.CENTER);
		frame.setJMenuBar(createMenuBar());
		frame.setSize(900, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * Creates the frame tool bar.
	 *
	 * @return the frame tool bar
	 */
	private JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();
		JLabel label = new JLabel("Drag New View");
		toolBar.add(label);
		new DockingWindowDragSource(label, new DockingWindowDraggerProvider() {
			public DockingWindowDragger getDragger(MouseEvent mouseEvent) {
				return getDynamicView(getDynamicViewId()).startDrag(rootWindow);
			}
		});
		return toolBar;
	}

	/**
	 * Creates the frame menu bar.
	 *
	 * @return the menu bar
	 */
	private JMenuBar createMenuBar() {
		JMenuBar menu = new JMenuBar();
		menu.add(createLayoutMenu());
		menu.add(createFocusViewMenu());
		menu.add(createThemesMenu());
		menu.add(createPropertiesMenu());
		menu.add(createWindowBarsMenu());
		menu.add(createViewMenu());
		return menu;
	}

	/**
	 * Creates the menu where layout can be saved and loaded.
	 *
	 * @return the layout menu
	 */
	private JMenu createLayoutMenu() {
		JMenu layoutMenu = new JMenu("Layout");

		layoutMenu.add("Default Layout").addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setDefaultLayout();
					}
				});

		layoutMenu.addSeparator();

		for (int i = 0; i < layouts.length; i++) {
			final int j = i;

			layoutMenu.add("Save Layout " + i).addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								// Save the layout in a byte array
								ByteArrayOutputStream bos = new ByteArrayOutputStream();
								ObjectOutputStream out = new ObjectOutputStream(
										bos);
								rootWindow.write(out, false);
								out.close();
								layouts[j] = bos.toByteArray();
							} catch (IOException e1) {
								throw new RuntimeException(e1);
							}
						}
					});
		}

		layoutMenu.addSeparator();

		for (int i = 0; i < layouts.length; i++) {
			final int j = i;

			layoutMenu.add("Load Layout " + j).addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									if (layouts[j] != null) {
										try {
											// Load the layout from a byte array
											ObjectInputStream in = new ObjectInputStream(
													new ByteArrayInputStream(
															layouts[j]));
											rootWindow.read(in, true);
											in.close();
										} catch (IOException e1) {
											throw new RuntimeException(e1);
										}
									}
								}
							});
						}
					});
		}

		return layoutMenu;
	}

	/**
	 * Creates the menu where views can be shown and focused.
	 *
	 * @return the focus view menu
	 */
	private JMenu createFocusViewMenu() {
		JMenu viewsMenu = new JMenu("Focus View");

		for (int i = 0; i < views.length; i++) {
			final View view = views[i];
			viewsMenu.add("Focus " + view.getTitle()).addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									// Ensure the view is shown in the root
									// window
									DockingUtil.addWindow(view, rootWindow);

									// Transfer focus to the view
									view.restoreFocus();
								}
							});
						}
					});
		}

		return viewsMenu;
	}

	/**
	 * Creates the menu where the theme can be changed.
	 *
	 * @return the theme menu
	 */
	private JMenu createThemesMenu() {
		JMenu themesMenu = new JMenu("Themes");

		DockingWindowsTheme[] themes = { new DefaultDockingTheme(),
				new BlueHighlightDockingTheme(), new SlimFlatDockingTheme(),
				new GradientDockingTheme(), new ShapedGradientDockingTheme(),
				new SoftBlueIceDockingTheme(), new ClassicDockingTheme() };

		ButtonGroup group = new ButtonGroup();

		for (int i = 0; i < themes.length; i++) {
			final DockingWindowsTheme theme = themes[i];

			JRadioButtonMenuItem item = new JRadioButtonMenuItem(
					theme.getName());
			item.setSelected(i == 4);
			group.add(item);

			themesMenu.add(item).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Clear the modified properties values
					properties.getMap().clear(true);

					setTheme(theme);
				}
			});
		}

		return themesMenu;
	}

	/**
	 * Creates the menu where different property values can be modified.
	 *
	 * @return the properties menu
	 */
	private JMenu createPropertiesMenu() {
		JMenu buttonsMenu = new JMenu("Properties");

		buttonsMenu.add("Enable Close").addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				properties.getDockingWindowProperties().setCloseEnabled(true);
			}
		});

		buttonsMenu.add("Hide Close Buttons").addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						properties.getDockingWindowProperties()
								.setCloseEnabled(false);
					}
				});

		buttonsMenu.add("Freeze Layout").addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Disable window operations
						properties.getDockingWindowProperties().setDragEnabled(
								false);
						properties.getDockingWindowProperties()
								.setCloseEnabled(false);
						properties.getDockingWindowProperties()
								.setMinimizeEnabled(false);
						properties.getDockingWindowProperties()
								.setRestoreEnabled(false);
						properties.getDockingWindowProperties()
								.setMaximizeEnabled(false);

						// Enable tab reordering inside tabbed panel
						properties.getTabWindowProperties()
								.getTabbedPanelProperties()
								.setTabReorderEnabled(true);
					}
				});

		buttonsMenu.add("Unfreeze Layout").addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Enable window operations
						properties.getDockingWindowProperties().setDragEnabled(
								true);
						properties.getDockingWindowProperties()
								.setCloseEnabled(true);
						properties.getDockingWindowProperties()
								.setMinimizeEnabled(true);
						properties.getDockingWindowProperties()
								.setRestoreEnabled(true);
						properties.getDockingWindowProperties()
								.setMaximizeEnabled(true);

						// Disable tab reordering inside tabbed panel
						properties.getTabWindowProperties()
								.getTabbedPanelProperties()
								.setTabReorderEnabled(false);
					}
				});

		return buttonsMenu;
	}

	/**
	 * Creates the menu where individual window bars can be enabled and
	 * disabled.
	 *
	 * @return the window bar menu
	 */
	private JMenu createWindowBarsMenu() {
		JMenu barsMenu = new JMenu("Window Bars");

		for (int i = 0; i < 4; i++) {
			final Direction d = Direction.getDirections()[i];
			JCheckBoxMenuItem item = new JCheckBoxMenuItem("Toggle " + d);
			item.setSelected(d == Direction.DOWN);
			barsMenu.add(item).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Enable/disable the window bar
					rootWindow.getWindowBar(d).setEnabled(
							!rootWindow.getWindowBar(d).isEnabled());
				}
			});
		}

		return barsMenu;
	}

	/**
	 * Creates the menu where not shown views can be shown.
	 *
	 * @return the view menu
	 */
	private JMenu createViewMenu() {
		JMenu menu = new JMenu("Views");

		for (int i = 0; i < views.length; i++) {
			final View view = views[i];
			viewItems[i] = new JMenuItem(view.getTitle());
			viewItems[i].setEnabled(views[i].getRootWindow() == null);
			menu.add(viewItems[i]).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (view.getRootWindow() != null)
						view.restoreFocus();
					else {
						DockingUtil.addWindow(view, rootWindow);
					}
				}
			});
		}

		return menu;
	}

	/**
	 * Sets the docking windows theme.
	 *
	 * @param theme
	 *            the docking windows theme
	 */
	private void setTheme(DockingWindowsTheme theme) {
		properties.replaceSuperObject(currentTheme.getRootWindowProperties(),
				theme.getRootWindowProperties());
		currentTheme = theme;
	}

	public static void main(String[] args) throws Exception {
		// Set InfoNode Look and Feel
		// UIManager.setLookAndFeel(new InfoNodeLookAndFeel());

		// Docking windows should be run in the Swing thread
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new IdwTest02();
			}
		});
	}
}
