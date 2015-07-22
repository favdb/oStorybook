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

import java.awt.Component;
import java.awt.Event;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;

import org.hibernate.Session;
import storybook.SbConstants;
import storybook.SbApp;
import storybook.SbConstants.PreferenceKey;
import storybook.controller.BookController;
import storybook.model.DbFile;
import storybook.model.BookModel;
import storybook.model.PreferenceModel;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.dao.PreferenceDAOImpl;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Preference;
import storybook.toolkit.I18N;
import storybook.toolkit.IOUtil;
import storybook.toolkit.PrefUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;

import com.sun.jaf.ui.ActionManager;
import com.sun.jaf.ui.UIFactory;
import java.io.IOException;
import storybook.ui.MainMenu;

/**
 * @author martin
 *
 */
public class SbActionManager implements PropertyChangeListener {

//	private final static int MENUBAR_INDEX_FILE = 0;
//	private final static int MENUBAR_INDEX_PARTS = 6;
//	private final static int MENUBAR_INDEX_WINDOW = 9;
	private ActionHandler actionHandler;
	private ActionManager actionManager;
	private final MainFrame mainFrame;
	private MainMenu mainMenu;

	public SbActionManager(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public SbActionManager(MainFrame mainFrame,boolean withInit) {
		this.mainFrame = mainFrame;
		init();
	}

	public void init() {
		SbApp.trace("SbActionManager.init()");
		initActions();
		initUiFactory();
		if (mainFrame.isBlank()) {
			//disableActionsForBlank(mainFrame.getJMenuBar());
			mainMenu.setMenuForBlank();
		}
	}

	private void initActions() {
		SbApp.trace("SbActionManager.initActions()");
		/*
		try {*/
			actionManager = new ActionManager();
			ActionManager.setInstance(actionManager);
			/*
			File file = new File("actions.xml");
			String str = IOUtil.readFileAsString(file.getAbsolutePath());
			// i18n replacements
			Pattern patt = Pattern.compile("<i18n>([^<]*)</i18n>");
			Matcher m = patt.matcher(str);
			StringBuffer sb = new StringBuffer(str.length());
			while (m.find()) {
				String text = I18N.getMsg(m.group(1));
				m.appendReplacement(sb, Matcher.quoteReplacement(text));
			}
			m.appendTail(sb);
			str = sb.toString();
			patt = Pattern.compile("<i18ndot>([^<]*)</i18ndot>");
			m = patt.matcher(str);
			sb = new StringBuffer(str.length());
			while (m.find()) {
				String text = I18N.getMsgDot(m.group(1));
				m.appendReplacement(sb, Matcher.quoteReplacement(text));
			}
			m.appendTail(sb);
			str = sb.toString();
			InputStream is = IOUtil.stringToInputStream(str);
			actionManager.loadActions(is);
			// actionManager.loadActions(xmlFile);
		} catch (IOException e) {
			SbApp.error("SbActionManager.initActions()", e);
		}*/
		registerActions();
	}

	private void registerActions() {
		SbApp.trace("SbActionManager.registerActions()");
		actionHandler = new ActionHandler(mainFrame);

		// file
		/*
		actionManager.registerCallback("new-command", actionHandler, "handleNewFile");
		actionManager.registerCallback("open-command", actionHandler, "handleOpenFile");
		actionManager.registerCallback("recent-clear-command", actionHandler, "handleRecentClear");
		*/
		actionManager.registerCallback("save-command", actionHandler, "handleSave");
		/*
		actionManager.registerCallback("save-as-command", actionHandler, "handleSaveAs");
		actionManager.registerCallback("rename-command", actionHandler, "handleRenameFile");
		actionManager.registerCallback("close-command", actionHandler, "handleClose");
		actionManager.registerCallback("export-book-text-command", actionHandler, "handleExportBookText");
		actionManager.registerCallback("export-print-command", actionHandler, "handleExportPrint");
		actionManager.registerCallback("open-export-folder-command", actionHandler, "handleOpenExportFolder");
		actionManager.registerCallback("document-preferences-command", actionHandler, "handleDocumentPreferences");
		actionManager.registerCallback("exit-command", actionHandler, "handleExit");

		// edit
		actionManager.registerCallback("copy-book-text-command", actionHandler, "handleCopyBookText");
		actionManager.registerCallback("copy-blurb-command", actionHandler, "handleCopyBlurb");

		// views
		actionManager.registerCallback("view-chrono-command", actionHandler, "handleShowChronoView");
		actionManager.registerCallback("view-book-command", actionHandler, "handleShowBookView");
		actionManager.registerCallback("view-manage-command", actionHandler, "handleShowManageView");
		actionManager.registerCallback("view-reading-command", actionHandler, "handleShowReadingView");
		actionManager.registerCallback("view-memoria-command", actionHandler, "handleShowMemoria");
		actionManager.registerCallback("view-editor-command", actionHandler, "handleShowEditor");
		actionManager.registerCallback("view-tree-command", actionHandler, "handleShowTree");
		actionManager.registerCallback("view-info-command", actionHandler, "handleShowInfo");
		actionManager.registerCallback("view-navigation-command", actionHandler, "handleShowNavigation");

		// main entities
		actionManager.registerCallback("entity-scenes-command", actionHandler, "handleShowScenes");
		actionManager.registerCallback("entity-chapters-command", actionHandler, "handleShowChapters");
		actionManager.registerCallback("entity-parts-command", actionHandler, "handleShowParts");
		actionManager.registerCallback("entity-strands-command", actionHandler, "handleShowStrands");
		actionManager.registerCallback("entity-persons-command", actionHandler, "handleShowPersons");
		actionManager.registerCallback("entity-genders-command", actionHandler, "handleShowGenders");
		actionManager.registerCallback("entity-categories-command", actionHandler, "handleShowCategories");
		actionManager.registerCallback("entity-locations-command", actionHandler, "handleShowLocations");
		actionManager.registerCallback("rename-city-command", actionHandler, "handleRenameCity");
		actionManager.registerCallback("rename-country-command", actionHandler, "handleRenameCountry");

		// secondary entities
		actionManager.registerCallback("entity-tags-command", actionHandler, "handleShowTags");
		actionManager.registerCallback("entity-taglinks-command", actionHandler, "handleShowTagLinks");
		actionManager.registerCallback("rename-tag-category-command", actionHandler, "handleRenameTagCategory");
		actionManager.registerCallback("entity-items-command", actionHandler, "handleShowItems");
		actionManager.registerCallback("entity-itemlinks-command", actionHandler, "handleShowItemLinks");
		actionManager.registerCallback("rename-item-category-command", actionHandler, "handleRenameItemCategory");
		actionManager.registerCallback("entity-ideas-command", actionHandler, "handleShowIdeas");
		actionManager.registerCallback("entity-internals-command", actionHandler, "handleShowInternals");

		// create new entity
		actionManager.registerCallback("new-scene-command", actionHandler, "handleNewScene");
		actionManager.registerCallback("new-chapter-command", actionHandler, "handleNewChapter");
		actionManager.registerCallback("create-chapters-command", actionHandler, "handleCreateChapters");
		actionManager.registerCallback("new-part-command", actionHandler, "handleNewPart");
		actionManager.registerCallback("new-part-command", actionHandler, "handleNewPart");
		actionManager.registerCallback("new-strand-command", actionHandler, "handleNewStrand");
		actionManager.registerCallback("new-person-command", actionHandler, "handleNewPerson");
		actionManager.registerCallback("new-category-command", actionHandler, "handleNewCategory");
		actionManager.registerCallback("new-gender-command", actionHandler, "handleNewGender");
		actionManager.registerCallback("new-location-command", actionHandler, "handleNewLocation");
		actionManager.registerCallback("new-tag-command", actionHandler, "handleNewTag");
		actionManager.registerCallback("new-taglink-command", actionHandler, "handleNewTagLink");
		actionManager.registerCallback("new-item-command", actionHandler, "handleNewItem");
		actionManager.registerCallback("new-itemlink-command", actionHandler, "handleNewItemLink");
		actionManager.registerCallback("new-idea-command", actionHandler, "handleNewIdea");

		// parts
		actionManager.registerCallback("part-previous-command", actionHandler, "handlePreviousPart");
		actionManager.registerCallback("part-next-command", actionHandler, "handleNextPart");

		// charts
		actionManager.registerCallback("chart-persons-by-date-command", actionHandler, "handleChartPersonsByDate");
		actionManager.registerCallback("chart-persons-by-scene-command", actionHandler, "handleChartPersonsByScene");
		actionManager.registerCallback("chart-wiww-command", actionHandler, "handleChartWiWW");
		actionManager.registerCallback("chart-strands-by-date-command", actionHandler, "handleChartStrandsByDate");
		actionManager.registerCallback("chart-occurrence-of-persons-command", actionHandler, "handleChartOccurrenceOfPersons");
		actionManager.registerCallback("chart-occurrence-of-locations-command", actionHandler, "handleChartOccurrenceOfLocations");
		actionManager.registerCallback("chart-gantt-command", actionHandler, "handleChartGantt");

		// tools
		actionManager.registerCallback("tasklist-command", actionHandler, "handleTaskList");
		actionManager.registerCallback("flashofinspiration-command", actionHandler, "handleFlashOfInspiration");
		actionManager.registerCallback("langtool-command", actionHandler, "handleLangTool");
		actionManager.registerCallback("text2html-command", actionHandler, "handleText2Html");
		actionManager.registerCallback("html2text-command", actionHandler, "handleHtml2Text");

		// window
		actionManager.registerCallback("save-layout-command", actionHandler, "handleSaveLayout");
		actionManager.registerCallback("manage-layouts-command", actionHandler, "handleManageLayouts");
		actionManager.registerCallback("reset-layout-command", actionHandler, "handleResetLayout");
		actionManager.registerCallback("default-layout-command", actionHandler, "handleDefaultLayout");
		actionManager.registerCallback("persons-locations-layout-command", actionHandler, "handlePersonsLocationsLayout");
		actionManager.registerCallback("tags-items-layout-command", actionHandler, "handleTagsItemsLayout");
		actionManager.registerCallback("chrono-only-layout-command", actionHandler, "handleChronoOnlyLayout");
		actionManager.registerCallback("book-only-layout-command", actionHandler, "handleBookOnlyLayout");
		actionManager.registerCallback("manage-only-layout-command", actionHandler, "handleManageOnlyLayout");
		actionManager.registerCallback("reading-only-layout-command", actionHandler, "handleReadingOnlyLayout");
		actionManager.registerCallback("refresh-command", actionHandler, "handleRefresh");
		actionManager.registerCallback("preferences-command", actionHandler, "handlePreferences");

		// help
		//actionManager.registerCallback("go-pro-command", actionHandler, "handleGoPro");
		actionManager.registerCallback("report-bug-command", actionHandler, "handleReportBug");
		actionManager.registerCallback("doc-command", actionHandler, "handleDoc");
		actionManager.registerCallback("faq-command", actionHandler, "handleFAQ");
		actionManager.registerCallback("homepage-command", actionHandler, "handleHomepage");
		actionManager.registerCallback("facebook-command", actionHandler, "handleFacebook");
		actionManager.registerCallback("check-update-command", actionHandler, "handleCheckUpdate");
		actionManager.registerCallback("about-command", actionHandler, "handleAbout");
		actionManager.registerCallback("trace-command", actionHandler, "handleTrace");

		// development
		actionManager.registerCallback("run-gc-command", actionHandler, "handleRunGC");
		actionManager.registerCallback("dump-attached-views-command", actionHandler, "handleDumpAttachedViews");
		actionManager.registerCallback("dummy-command", actionHandler, "handleDummy");
		*/
	}

	private void initUiFactory() {
		SbApp.trace("SbActionManager.initUiFactory()");
		//UIFactory.setActionManager(ActionManager.getInstance());
		//UIFactory factory = UIFactory.getInstance();
		mainMenu=new MainMenu(mainFrame);
		JMenuBar menubar = mainMenu.menuBar;
		if (menubar != null) {
			//menubar.addPropertyChangeListener(this);
			reloadRecentMenu(menubar);
			reloadPartMenu(menubar);
			reloadWindowMenu(menubar);
			Preference pref = PrefUtil.get(PreferenceKey.TRANSLATOR_MODE, false);
			if (pref != null && pref.getBooleanValue()) {
				JMenuItem item = new JMenuItem(new RunAttesoroAction());
				menubar.add(item);
			}
			mainFrame.setJMenuBar(menubar);
		} else {
			System.err.println("General error : unable to load main menu");
		}
		//JToolBar toolBar = factory.createToolBar("main-toolbar");
		JToolBar toolBar=mainMenu.toolBar;
		if (toolBar != null) {
			toolBar.setName(SbConstants.ComponentName.TB_MAIN.toString());
			mainFrame.setMainToolBar(toolBar);
		}

		mainFrame.invalidate();
		mainFrame.validate();
		mainFrame.pack();
		mainFrame.repaint();
	}

	private void reloadWindowMenu(JMenuBar menubar) {
		SbApp.trace("SbActionManager.reloadWindowMenu(" + menubar.getName() + ")");
		//JMenu menu = menubar.getMenu(MENUBAR_INDEX_WINDOW);
		//JMenu miLoad = (JMenu) menu.getItem(0);
		JMenu miLoad=mainMenu.windowLoadLayout;
		miLoad.removeAll();
		PreferenceModel model = SbApp.getInstance().getPreferenceModel();
		Session session = model.beginTransaction();
		PreferenceDAOImpl dao = new PreferenceDAOImpl(session);
		List<Preference> pref = dao.findAll();
		for (Preference preference : pref) {
			if (preference.getKey().startsWith(PreferenceKey.DOCKING_LAYOUT.toString())) {
				String name = preference.getStringValue();
				if (SbConstants.BookKey.LAST_USED_LAYOUT.toString().equals(name))
					continue;
				LoadDockingLayoutAction act = new LoadDockingLayoutAction(mainFrame, name);
				JMenuItem item = new JMenuItem(act);
				miLoad.add(item);
			}
		}
		model.commit();
	}

	/*private int findRecent(JMenu menu) {
		int rc = 2;
		for (int i = 0; i < menu.getItemCount(); i++) {
			if (menu.getItem(i).getLabel().equals(I18N.getMsg("msg.file.open.recent")))
				return (i);
		}
		return (rc);
	}*/

	private void reloadRecentMenu(JMenuBar menubar) {
		SbApp.trace("SbActionManager.reloadRecentMenu(" + menubar.getName() + ")");
		//JMenu menu = menubar.getMenu(MENUBAR_INDEX_FILE);
		//JMenu miRecent = (JMenu) menu.getItem(findRecent(menu));
		JMenu miRecent=mainMenu.fileOpenRecent;
		miRecent.removeAll();
		List<DbFile> list = PrefUtil.getDbFileList();
		for (DbFile dbFile : list) {
			OpenFileAction act = new OpenFileAction(dbFile.getName() + " ["
					+ dbFile.toString() + "]", dbFile);
			JMenuItem item = new JMenuItem(act);
			miRecent.add(item);
		}
		miRecent.addSeparator();
		JMenuItem item = new JMenuItem(new ClearRecentFilesAction(actionHandler));
		miRecent.add(item);
	}

	private void reloadPartMenu(JMenuBar menubar) {
		SbApp.trace("SbActionManager.reloadPartMenu(" + menubar.getName() + ")");
		BookModel model = mainFrame.getBookModel();
		if (model == null)
			return;
		//JMenu menu = menubar.getMenu(MENUBAR_INDEX_PARTS);
		//JMenuItem miPreviousPart = menu.getItem(0);
		//JMenuItem miNextPart = menu.getItem(1);
		//JMenuItem miParts = menu.getItem(2);
		JMenu menu=mainMenu.menuParts;
		JMenuItem miPreviousPart=mainMenu.partPrevious;
		JMenuItem miNextPart=mainMenu.partNext;
		Part currentPart = mainFrame.getCurrentPart();
		Session session = model.beginTransaction();
		PartDAOImpl dao = new PartDAOImpl(session);
		List<Part> parts = dao.findAll();
		model.commit();
		menu.removeAll();
		int pos = 0;
		ButtonGroup group = new ButtonGroup();
		for (Part part : parts) {
			Action action = new ChangePartAction(I18N.getMsg("msg.common.part") + " " + part.getNumberName(), actionHandler, part);
			JRadioButtonMenuItem rbmi = new JRadioButtonMenuItem(action);
			SwingUtil.setAccelerator(rbmi, KeyEvent.VK_0 + part.getNumber(), Event.ALT_MASK);
			if (currentPart.getId().equals(part.getId()))
				rbmi.setSelected(true);
			group.add(rbmi);
			menu.insert(rbmi, pos);
			++pos;
		}
		menu.insertSeparator(pos++);
		menu.insert(miPreviousPart, pos++);
		menu.insert(miNextPart, pos++);
		menu.insertSeparator(pos++);
		//menu.insert(miParts, pos++);
	}

	private void selectPartMenu(JMenuBar menubar) {
		SbApp.trace("SbActionManager.selectPartMenu(" + menubar.getName() + ")");
		//JMenu menu = menubar.getMenu(MENUBAR_INDEX_PARTS);
		JMenu menu=mainMenu.menuParts;
		Component[] comps = menu.getMenuComponents();
		for (Component comp : comps) {
			if (comp instanceof JRadioButtonMenuItem) {
				JRadioButtonMenuItem rbmi = (JRadioButtonMenuItem) comp;
				ChangePartAction action = (ChangePartAction) rbmi.getAction();
				if (action.getPart().getId().equals(mainFrame.getCurrentPart().getId())) {
					rbmi.setSelected(true);
					return;
				}
			}
		}
	}

	public void reloadMenuToolbar() {
//		boolean maximized = mainFrame.isMaximized();
//		int width = mainFrame.getWidth();
//		int height = mainFrame.getHeight();
		init();
//		if (maximized) {
//			mainFrame.setMaximized();
//		} else {
//			mainFrame.setSize(width, height);
//		}
	}

	/*
	public void disableActionsForBlank(JMenuBar menubar) {
		SbApp.trace("SbActionManager.disableActionsForBlank(" + menubar.getName() + ")");
		String[] actionNames = {
			"file-menu-command",
			"new-command",
			"open-command",
			"recent-menu-command",
			"recent-clear-command",
			"exit-command",
			"edit-menu-command",
			"view-menu-command",
			"main-entities-menu-command",
			"secondary-entities-menu-command",
			"new-entity-menu-command",
			"parts-menu-command",
			"charts-menu-command",
			"tools-menu-command",
			"langtool-command",
			"window-menu-command",
			"preferences-command",
			"help-menu",
			"report-bug-command",
			"doc-command",
			"faq-command",
			"homepage-command",
			"facebook-command",
			"check-update-command",
			"about-command",
			"trace-command"
		};
		List<String> list = Arrays.asList(actionNames);
		Set<String> ids = actionManager.getActionIDs();
		for (String id : ids) {
			if (!list.contains(id))
				actionManager.getAction(id).setEnabled(false);
		}
	}*/

	public ActionHandler getActionController() {
		SbApp.trace("SbActionManager.getActionController()");
		return actionHandler;
	}

	public ActionManager getActionManager() {
		SbApp.trace("SbActionManager.getActionManager()");
		return actionManager;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		SbApp.trace("SbActionManager.propertyChange(" + evt.getPropertyName() + "::" + evt.getNewValue() + ")");
		String propName = evt.getPropertyName();
		if (BookController.PartProps.NEW.check(propName)
				|| BookController.PartProps.UPDATE.check(propName)
				|| BookController.PartProps.DELETE.check(propName)) {
			reloadMenuToolbar();
			return;
		}
		if (BookController.PartProps.CHANGE.check(propName))
			selectPartMenu(mainFrame.getJMenuBar()); //return;
	}

	public ActionHandler getActionHandler() {
		SbApp.trace("SbActionManager.getActionHandler()");
		return actionHandler;
	}
}
