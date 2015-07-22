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
package storybook.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.ViewSerializer;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.MixedViewHandler;
import net.infonode.docking.util.StringViewMap;
import net.infonode.util.Direction;
import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import storybook.SbApp;
import storybook.SbConstants;
import storybook.SbConstants.BookKey;
import storybook.SbConstants.PreferenceKey;
import storybook.SbConstants.Storybook;
import storybook.SbConstants.ViewName;
import storybook.action.ActionHandler;
import storybook.action.SbActionManager;
import storybook.controller.BookController;
import storybook.model.BlankModel;
import storybook.model.BookModel;
import storybook.model.DbFile;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Internal;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Preference;
import storybook.toolkit.BookUtil;
import storybook.toolkit.DockingWindowUtil;
import storybook.toolkit.I18N;
import storybook.toolkit.PrefUtil;
import storybook.toolkit.SpellCheckerUtil;
import storybook.toolkit.swing.FontUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.dialog.unicodlg.UnicodeDialog;
import storybook.ui.edit.EntityEditor;
import storybook.ui.interfaces.IPaintable;
//import storybook.view.net.BrowserPanel;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.panel.BlankPanel;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame implements IPaintable {

	private BookModel bookModel;
	private BookController bookController;
	private SbActionManager sbActionManager;
	private ViewFactory viewFactory;
	private JToolBar mainToolBar;
	private RootWindow rootWindow;
	private StatusBarPanel statusBar;
	private HashMap<Integer, JComponent> dynamicViews = new HashMap<Integer, JComponent>();
	private DbFile dbFile;
	private Part currentPart;
	private boolean EditorModless;
	private UnicodeDialog unicodeDialog;

	public MainFrame() {
		FontUtil.setDefaultFont(new Font("Arial", Font.PLAIN, 12));
	}

	@Override
	public void init() {
		SbApp.trace("MainFrame.init()");
		dbFile = null;
		viewFactory = new ViewFactory(this);
		sbActionManager = new SbActionManager(this);
		sbActionManager.init();
		bookController = new BookController(this);
		BlankModel model = new BlankModel(this);
		bookController.attachModel(model);
		setIconImage(I18N.getIconImage("icon.sb"));
		addWindowListener(new MainFrameWindowAdaptor());
	}

	public void init(DbFile dbF) {
		SbApp.trace("MainFrame.init(" + dbF.getDbName() + ")");
		try {
			this.dbFile = dbF;
			viewFactory = new ViewFactory(this);
			sbActionManager = new SbActionManager(this);
			sbActionManager.init();
			// model and controller
			bookController = new BookController(this);
			bookModel = new BookModel(this);
			if (!dbF.getDbName().isEmpty()) {
				bookModel.initSession(dbF.getDbName());
			}
			bookController.attachModel(bookModel);
			// Google maps
			//Preference pref = PrefUtil.get(PreferenceKey.GOOGLE_MAPS_URL, SbConstants.DEFAULT_GOOGLE_MAPS_URL);
			//NetUtil.setGoogleMapUrl(pref.getStringValue());
			// spell checker
			SpellCheckerUtil.registerDictionaries();
			// listener
			addWindowListener(new MainFrameWindowAdaptor());
		} catch (Exception e) {
			SbApp.error("MainFrame.init(" + dbF.getName() + ")", e);
		}
	}

	@Override
	public void initUi() {
		SbApp.trace(">>> MainFrame.initUi()");
		setLayout(new MigLayout("flowy,fill,ins 0,gap 0", "", "[grow]"));
		setIconImage(I18N.getIconImage("icon.sb"));
		setTitle();
		restoreDimension();
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		SbApp.getInstance().resetUiFont();
		sbActionManager.reloadMenuToolbar();
		initRootWindow();
		setDefaultLayout();
		SbApp.trace("add(rootWindow, \"grow\");");
		add(rootWindow, "grow");
		SbApp.trace("statusBar = new StatusBarPanel(this);");
		statusBar = new StatusBarPanel(this);
		SbApp.trace("add(statusBar, \"growx\");");
		add(statusBar, "growx");
		SbApp.trace("bookController.attachView(statusBar);");
		bookController.attachView(statusBar);
		SbApp.trace("pack();");
		pack();
		setVisible(true);
		initAfterPack();
		JMenuBar menubar = getJMenuBar();
		bookController.detachView(menubar);
		bookController.attachView(menubar);
		// load last used layout
		DockingWindowUtil.loadLayout(this, SbConstants.BookKey.LAST_USED_LAYOUT.toString());
		// always hide the editor
		hideEditor();
		// restore last used part
		try {
			Internal internal = BookUtil.get(this, BookKey.LAST_USED_PART.toString(), 1);
			Part part = null;
			if (internal != null && internal.getIntegerValue() != null) {
				Session session = bookModel.beginTransaction();
				PartDAOImpl dao = new PartDAOImpl(session);
				part = dao.find((long) internal.getIntegerValue());
				bookModel.commit();
				if (part == null) {
					part = getCurrentPart();
				}
			} else {
				part = getCurrentPart();
			}
			sbActionManager.getActionHandler().handleChangePart(part);
		} catch (Exception e) {
			SbApp.trace("exiting try in MainFrame.initUi()");
		}
		//		bookController.attachView(this);
		SbApp.trace("<<< MainFrame.initUi()");
	}

//	public void modelPropertyChange(PropertyChangeEvent evt) {
//		Object oldValue = evt.getOldValue();
//		Object newValue = evt.getNewValue();
//		String propName = evt.getPropertyName();
//	}
	public void setTitle() {
		SbApp.trace("MainFrame.setTitle()");
		String prodFullTitle = Storybook.PRODUCT_FULL_NAME.toString();
		if (dbFile != null) {
			Part part = getCurrentPart();
			String partName = "";
			if (part != null) {
				partName = part.getNumberName();
			}
			String title = dbFile.getName();
			Internal internal = BookUtil.get(this, BookKey.TITLE, "");
			if (internal != null && !internal.getStringValue().isEmpty()) {
				title = internal.getStringValue();
			}
			setTitle(title + " [" + I18N.getMsg("msg.common.part") + " " + partName + "]" + " - " + prodFullTitle);
		} else {
			setTitle(prodFullTitle);
		}
	}

	private void initRootWindow() {
		SbApp.trace("MainFrame.initRootWindow()");
		StringViewMap viewMap = viewFactory.getViewMap();
		MixedViewHandler handler = new MixedViewHandler(viewMap, new ViewSerializer() {
			@Override
			public void writeView(View view, ObjectOutputStream out) throws IOException {
				out.writeInt(((DynamicView) view).getId());
			}

			@Override
			public View readView(ObjectInputStream in) throws IOException {
				return getDynamicView(in.readInt());
			}
		});
		rootWindow = DockingUtil.createRootWindow(viewMap, handler, true);
		rootWindow.setName("rootWindow");
		rootWindow.setPreferredSize(new Dimension(4096, 2048));
		// suppression du editorView
		//SbView editorView = viewFactory.getEditorView();
		//bookController.attachView(editorView.getComponent());
		// set theme
		DockingWindowsTheme currentTheme = new ShapedGradientDockingTheme();
		RootWindowProperties properties = new RootWindowProperties();
		properties.addSuperObject(currentTheme.getRootWindowProperties());
		// Our properties object is the super object of the root window
		// properties object, so all property values of the
		// theme and in our property object will be used by the root window
		rootWindow.getRootWindowProperties().addSuperObject(properties);
		rootWindow.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	}

	public void setDefaultLayout() {
		SbApp.trace("MainFrame.setDefaultLayout()");
		SbView scenesView = getView(ViewName.SCENES);
		SbView chaptersView = getView(ViewName.CHAPTERS);
		SbView partsView = getView(ViewName.PARTS);
		SbView locationsView = getView(ViewName.LOCATIONS);
		SbView personsView = getView(ViewName.PERSONS);
		SbView relationshipView = getView(ViewName.RELATIONSHIPS);
		SbView gendersView = getView(ViewName.GENDERS);
		SbView categoriesView = getView(ViewName.CATEGORIES);
		SbView listAttributes = getView(ViewName.ATTRIBUTES);
		SbView strandsView = getView(ViewName.STRANDS);
		SbView ideasView = getView(ViewName.IDEAS);
		SbView tagsView = getView(ViewName.TAGS);
		SbView itemsView = getView(ViewName.ITEMS);
		SbView tagLinksView = getView(ViewName.TAGLINKS);
		SbView itemLinksView = getView(ViewName.ITEMLINKS);
		SbView internalsView = getView(ViewName.INTERNALS);
		SbView chronoView = getView(ViewName.CHRONO);
		SbView bookView = getView(ViewName.BOOK);
		SbView manageView = getView(ViewName.MANAGE);
		SbView readingView = getView(ViewName.READING);
		SbView memoriaView = getView(ViewName.MEMORIA);
		SbView chartPersonsByDate = getView(ViewName.CHART_PERSONS_BY_DATE);
		SbView chartPersonsByScene = getView(ViewName.CHART_PERSONS_BY_SCENE);
		SbView chartWiWW = getView(ViewName.CHART_WiWW);
		SbView chartStrandsByDate = getView(ViewName.CHART_STRANDS_BY_DATE);
		SbView chartOccurrenceOfPersons = getView(ViewName.CHART_OCCURRENCE_OF_PERSONS);
		SbView chartOccurrenceOfLocations = getView(ViewName.CHART_OCCURRENCE_OF_LOCATIONS);
		SbView chartGantt = getView(ViewName.CHART_GANTT);
		SbView editorView = getView(ViewName.EDITOR);
		SbView treeView = getView(ViewName.TREE);
		SbView infoView = getView(ViewName.INFO);
		SbView navigationView = getView(ViewName.NAVIGATION);
		SbView planView = getView(ViewName.PLAN);
		SbView timeEventView = getView(ViewName.TIMEEVENT);
		TabWindow tabInfoNavi = new TabWindow(new SbView[]{infoView, navigationView});
		tabInfoNavi.setName("tabInfoNaviWindow");
		SplitWindow swTreeInfo = new SplitWindow(false, 0.6f, treeView, tabInfoNavi);
		swTreeInfo.setName("swTreeInfo");
		TabWindow tabWindow = new TabWindow(new SbView[]{chronoView,
			bookView, manageView, readingView, memoriaView, scenesView,
			personsView, relationshipView, locationsView, chaptersView, gendersView,
			categoriesView, partsView, strandsView, ideasView, tagsView,
			itemsView, tagLinksView, itemLinksView,
			internalsView, listAttributes,
			chartPersonsByDate, chartPersonsByScene, chartWiWW,
			chartStrandsByDate, chartOccurrenceOfPersons,
			chartOccurrenceOfLocations, chartGantt, planView, timeEventView});
		tabWindow.setName("tabWindow");
		//SplitWindow swTabWinEditor = new SplitWindow(true, 0.60f, tabWindow, editorView);
		//swTabWinEditor.setName("swTabWinEditor");
		//SplitWindow swMain = new SplitWindow(true, 0.20f, swTreeInfo, swTabWinEditor);
		SplitWindow swMain = new SplitWindow(true, 0.20f, swTreeInfo, tabWindow);
		swMain.setName("swMain");
		rootWindow.setWindow(swMain);
		bookView.close();
		manageView.close();
		readingView.close();
		memoriaView.close();
		chaptersView.close();
		partsView.close();
		personsView.close();
		relationshipView.close();
		gendersView.close();
		categoriesView.close();
		listAttributes.close();
		strandsView.close();
		ideasView.close();
		tagsView.close();
		tagLinksView.close();
		itemsView.close();
		itemLinksView.close();
		internalsView.close();
		chartPersonsByDate.close();
		chartPersonsByScene.close();
		chartWiWW.close();
		chartStrandsByDate.close();
		chartOccurrenceOfPersons.close();
		chartOccurrenceOfLocations.close();
		chartGantt.close();
		planView.close();
		timeEventView.close();
		//editorView.minimize(Direction.RIGHT);
		//WindowBar windowBar = rootWindow.getWindowBar(Direction.RIGHT);
		//windowBar.setContentPanelSize(EntityEditor.MINIMUM_SIZE.width + 20);
		infoView.restoreFocus();
		chronoView.restoreFocus();
		rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);
		DockingWindowUtil.setRespectMinimumSize(this);
		SbApp.trace("end of MainFrame.setDefaultLayout()");
	}

	private void initAfterPack() {
		unicodeDialog = new UnicodeDialog(this);
		SbView scenesView = getView(ViewName.SCENES);
		SbView locationsView = getView(ViewName.LOCATIONS);
		SbView personsView = getView(ViewName.PERSONS);
		SbView chronoView = getView(ViewName.CHRONO);
		SbView treeView = getView(ViewName.TREE);
		SbView quickInfoView = getView(ViewName.INFO);
		SbView navigationView = getView(ViewName.NAVIGATION);
		// add docking window adapter to all views (except editor)
		MainDockingWindowAdapter dockingAdapter = new MainDockingWindowAdapter();
		for (int i = 0; i < viewFactory.getViewMap().getViewCount(); ++i) {
			View view = viewFactory.getViewMap().getViewAtIndex(i);
			/*if (view.getName().equals(ViewName.EDITOR.toString())) {
			 continue;
			 }*/
			view.addListener(dockingAdapter);
		}
		// load initially shown views here
		SbView[] views2 = {scenesView, personsView, locationsView, chronoView, treeView, quickInfoView, navigationView};
		for (SbView view : views2) {
			viewFactory.loadView(view);
			bookController.attachView(view.getComponent());
			bookModel.fireAgain(view);
		}
		quickInfoView.restoreFocus();
		chronoView.restoreFocus();
	}

	public SbView getView(String viewName) {
		return viewFactory.getView(viewName);
	}

	public SbView getView(ViewName viewName) {
		return viewFactory.getView(viewName);
	}

	public void showView(ViewName viewName) {
		SbApp.trace("MainFrame.showView(" + viewName.name() + ")");
		if (viewName.equals(SbConstants.ViewName.EDITOR)) {
			return;
		}
		setWaitingCursor();
		SbView view = getView(viewName);
		if (view.getRootWindow() != null) {
			view.restoreFocus();
		} else {
			SbApp.trace(">>> RootWindow=null");
			DockingUtil.addWindow(view, rootWindow);
		}
		view.requestFocusInWindow();
		DockingWindowUtil.setRespectMinimumSize(this);
		setDefaultCursor();
		/*if (viewName.equals(SbConstants.ViewName.EDITOR)) {
		 showEditor();
		 }*/
	}

	public void showAndFocus(ViewName viewName) {
		SbApp.trace("MainFrame.showAndFocus(" + viewName.name() + ")");
		View view = getView(viewName);
		view.restore();
		view.restoreFocus();
	}

	public void closeView(ViewName viewName) {
		SbApp.trace("MainFrame.closeView(" + viewName.name() + ")");
		SbView view = getView(viewName);
		view.close();
	}

	public void refresh() {
		setWaitingCursor();
		for (int i = 0; i < viewFactory.getViewMap().getViewCount(); ++i) {
			SbView view = (SbView) viewFactory.getViewMap().getViewAtIndex(i);
			getBookController().refresh(view);
		}
		setDefaultCursor();
	}

	public void refreshStatusBar() {
		statusBar.refresh();
	}

	public void showEditor() {
		SbApp.trace("MainFrame.showEditor()");
		/*SwingUtilities.invokeLater(new Runnable() {
		 @Override
		 public void run() {
		 SbApp.trace("MainFrame.showEditor()-->run");
		 SbView editorView = getView(ViewName.EDITOR);
		 editorView.cleverRestoreFocus();
		 }
		 });*/
		SbApp.trace("no MainFrame.showEditor()");
	}

	public void hideEditor() {
		/*Timer timer = new Timer(200, new ActionListener() {
		 @Override
		 public void actionPerformed(ActionEvent e) {*/
		View editorView = getView(ViewName.EDITOR);
		 if (!editorView.isShowing()) {
		 return;
		 }/*
		 if (editorView.isMinimized()) {
		 WindowBar bar = rootWindow.getWindowBar(Direction.RIGHT);
		 bar.setSelectedTab(-1);
		 } else {*/
		 editorView.close();
		 /*}*/
		/*}
		 });
		 timer.setRepeats(false);
		 timer.start();*/
	}

	public void initBlankUi() {
		dbFile = null;
		setTitle(Storybook.PRODUCT_FULL_NAME.toString());
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		setLocation(screenSize.width / 2 - 450, screenSize.height / 2 - 320);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		SbApp.getInstance().resetUiFont();
		sbActionManager.reloadMenuToolbar();
		BlankPanel blankPanel = new BlankPanel(this);
		blankPanel.initAll();
		add(blankPanel);
		pack();
		setVisible(true);
	}

	public void setDefaultCursor() {
		SwingUtil.setDefaultCursor(this);
	}

	public void setWaitingCursor() {
		SwingUtil.setWaitingCursor(this);
	}

	public DbFile getDbFile() {
		return dbFile;
	}

	public boolean isBlank() {
		return dbFile == null;
	}

	public BookController getBookController() {
		return bookController;
	}

	public BookModel getBookModel() {
		return bookModel;
	}

	public RootWindow getRootWindow() {
		return rootWindow;
	}

	public SbActionManager getSbActionManager() {
		return sbActionManager;
	}

	public ActionHandler getActionController() {
		return sbActionManager.getActionController();
	}

	public ViewFactory getViewFactory() {
		return viewFactory;
	}

	private MainFrame getThis() {
		return this;
	}

	public boolean isMaximized() {
		return (getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
	}

	public void setMaximized() {
		setExtendedState(Frame.MAXIMIZED_BOTH);
	}

	public void close() {
		if (!isBlank()) {
			Preference pref = PrefUtil.get(PreferenceKey.CONFIRM_EXIT, true);
			if (pref.getBooleanValue()) {
				int n = JOptionPane.showConfirmDialog(getThis(),
						I18N.getMsg("msg.common.want.close"),
						I18N.getMsg("msg.common.close"),
						JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
					return;
				}
			}
			// save
			getSbActionManager().getActionHandler().handleFileSave();
			// save dimension, location, maximized
			Dimension dim = getSize();
			PrefUtil.set(PreferenceKey.SIZE_WIDTH, dim.width);
			PrefUtil.set(PreferenceKey.SIZE_HEIGHT, dim.height);
			PrefUtil.set(PreferenceKey.POS_X, getLocationOnScreen().x);
			PrefUtil.set(PreferenceKey.POS_Y, getLocationOnScreen().y);
			PrefUtil.set(PreferenceKey.MAXIMIZED, isMaximized());
			// save layout
			DockingWindowUtil.saveLayout(this, SbConstants.BookKey.LAST_USED_LAYOUT.toString());
			// save last used part
			BookUtil.store(this, BookKey.LAST_USED_PART.toString(), (Integer) ((int) (long) getCurrentPart().getId()));
		}

		SbApp app = SbApp.getInstance();
		app.removeMainFrame(this);
		dispose();
		if (app.getMainFrames().isEmpty()) {
			app.exit();
		}
	}

	private View getDynamicView(int id) {
		View view = (View) dynamicViews.get(new Integer(id));
		if (view == null) {
			view = new DynamicView("Dynamic View " + id, null, createDummyViewComponent("Dynamic View " + id), id);
		}
		return view;
	}

	private static JComponent createDummyViewComponent(String text) {
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < 100; j++) {
			sb.append(text).append(". This is line ").append(j).append("\n");
		}
		return new JScrollPane(new JTextArea(sb.toString()));
	}

	private void restoreDimension() {
		int w = PrefUtil.get(PreferenceKey.SIZE_WIDTH, SbConstants.DEFAULT_SIZE_WIDTH).getIntegerValue();
		int h = PrefUtil.get(PreferenceKey.SIZE_HEIGHT, SbConstants.DEFAULT_SIZE_HEIGHT).getIntegerValue();
		setPreferredSize(new Dimension(w, h));
		int x = PrefUtil.get(PreferenceKey.POS_X, SbConstants.DEFAULT_POS_X).getIntegerValue();
		int y = PrefUtil.get(PreferenceKey.POS_Y, SbConstants.DEFAULT_POS_Y).getIntegerValue();
		setLocation(x, y);
		boolean maximized = PrefUtil.get(PreferenceKey.MAXIMIZED, false).getBooleanValue();
		if (maximized) {
			setMaximized();
		}
	}

	public void updateStat() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private static class DynamicView extends View {

		private final int id;

		DynamicView(String title, Icon icon, Component component, int id) {
			super(title, icon, component);
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}

	private class MainFrameWindowAdaptor extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent evt) {
			close();
		}
	}

	private class MainDockingWindowAdapter extends DockingWindowAdapter {

		@Override
		public void windowAdded(DockingWindow addedToWindow, DockingWindow addedWindow) {
			SbApp.trace("MainDockingWindowAdapter.windowAdded(" + addedToWindow.getName() + ", " + addedWindow.getName() + ")");
			if (addedWindow != null && addedWindow instanceof SbView) {
				SbView view = (SbView) addedWindow;
				if (!view.isLoaded()) {
					viewFactory.loadView(view);
					bookController.attachView(view.getComponent());
					bookModel.fireAgain(view);
				}
			}
		}

		@Override
		public void windowClosed(DockingWindow window) {
			SbApp.trace("MainDockingWindowAdapter.windowClosed(" + window.getName() + ")");
			if (window != null && window instanceof SbView) {
				SbView view = (SbView) window;
				/* suppression editorView
				 if (ViewName.EDITOR.toString().equals(view.getName())) {
				 // don't detach / unload the editor
				 return;
				 }
				 */
				if (!view.isLoaded()) {
					return;
				}
				bookController.detachView((AbstractPanel) view.getComponent());
				viewFactory.unloadView(view);
				/* suppression du garbage collector
				 SbApp.getInstance().runGC();
				 */
			}
		}
	}

	public Part getCurrentPart() {
		try {
			Session session = bookModel.beginTransaction();
			if (currentPart == null) {
				PartDAOImpl dao = new PartDAOImpl(session);
				currentPart = dao.findFirst();
			} else {
				session.refresh(currentPart);
			}
			bookModel.commit();
			return currentPart;
		} catch (NullPointerException e) {
		}
		return null;
	}

	public void setCurrentPart(Part currentPart) {
		if (currentPart != null) {
			this.currentPart = currentPart;
		}
	}

	public boolean hasCurrentPart() {
		return currentPart != null;
	}

	public void setMainToolBar(JToolBar toolBar) {
		if (mainToolBar != null) {
			SwingUtil.unfloatToolBar(mainToolBar);
			getContentPane().remove(mainToolBar);
		}
		this.mainToolBar = toolBar;
		getContentPane().add(mainToolBar, BorderLayout.NORTH);
	}

	public JToolBar getMainToolBar() {
		return mainToolBar;
	}

	public void showEditorAsDialog(AbstractEntity entity) {
		JDialog dlg = new JDialog((Frame) this, true);
		if (EditorModless) dlg.setModalityType(Dialog.ModalityType.MODELESS);
		EntityEditor editor = new EntityEditor(this, entity, dlg);
		dlg.setTitle(I18N.getMsg("msg.common.editor"));
		dlg.setSize(this.getWidth() / 2, 680);
		dlg.add(editor);
		dlg.setLocationRelativeTo(this);
		dlg.setVisible(true);
	}

	public void showUnicodeDialog()
	{
		unicodeDialog.show();
	}
}
