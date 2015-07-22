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
/* vérification OK */

package storybook.action;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import net.infonode.docking.View;

import org.apache.commons.io.FileUtils;
import org.hibernate.Session;

import storybook.SbApp;
import storybook.SbConstants;
import storybook.SbConstants.BookKey;
import storybook.SbConstants.ViewName;
import storybook.model.DbFile;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.entity.Internal;
import storybook.model.hbn.entity.Part;
import storybook.toolkit.DockingWindowUtil;
import storybook.toolkit.BookUtil;
import storybook.toolkit.EnvUtil;
import storybook.toolkit.I18N;
import storybook.toolkit.net.NetUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;
import storybook.ui.dialog.AboutDialog;
import storybook.ui.dialog.CreateChaptersDialog;
import storybook.ui.dialog.BookPropertiesDialog;
import storybook.ui.dialog.ManageLayoutsDialog;
import storybook.ui.dialog.PreferencesDialog;
import storybook.ui.dialog.WaitDialog;
import storybook.ui.dialog.file.RenameFileDialog;
import storybook.ui.dialog.file.SaveAsFileDialog;
import storybook.ui.dialog.rename.RenameCityDialog;
import storybook.ui.dialog.rename.RenameCountryDialog;
import storybook.ui.dialog.rename.RenameItemCategoryDialog;
import storybook.ui.dialog.rename.RenameTagCategoryDialog;

import com.sun.jaf.ui.ActionManager;
import storybook.export.BookExporter;
import storybook.ui.SbView;

/**
 * @author martin
 *
 */
public class ActionHandler {

	private final MainFrame mainFrame;

	public ActionHandler(MainFrame mainframe) {
		mainFrame = mainframe;
	}

	/*public void handleCheckUpdate() {//new OK
		if (Updater.checkForUpdate()) {
			JOptionPane.showMessageDialog(mainFrame,
				I18N.getMsg("msg.update.no.text"),
				I18N.getMsg("msg.update.no.title"),
				JOptionPane.INFORMATION_MESSAGE);
		}
	}*/

	public void handleOpenExportFolder() {
		try {
			Internal internal = BookUtil.get(mainFrame,
				BookKey.EXPORT_DIRECTORY,
				EnvUtil.getDefaultExportDir(mainFrame));
			Desktop.getDesktop().open(new File(internal.getStringValue()));
		} catch (IOException | Error ex) {
			SbApp.error("ActionHandler.handleExportDir()", (Exception) ex);
		}
	}

	public void handleText2Html() {
		int n = SwingUtil.showBetaDialog(mainFrame);
		if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
			return;
		}
		mainFrame.setWaitingCursor();
		EntityUtil.convertPlainTextToHtml(mainFrame);
		mainFrame.refresh();
		mainFrame.setDefaultCursor();
	}

	public void handleHtml2Text() {
		int n = SwingUtil.showBetaDialog(mainFrame);
		if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
			return;
		}
		mainFrame.setWaitingCursor();
		EntityUtil.convertHtmlToPlainText(mainFrame);
		mainFrame.refresh();
		mainFrame.setDefaultCursor();
	}

	public void handleLangTool() {
		LangToolAction act = new LangToolAction(mainFrame);
		act.actionPerformed(null);
	}

	public void handleCreateChapters() {
		CreateChaptersDialog dlg = new CreateChaptersDialog(mainFrame);
		SwingUtil.showModalDialog(dlg, mainFrame);
	}

	public void handleRenameCity() {
		RenameCityDialog dlg = new RenameCityDialog(mainFrame);
		ActionManager actMngr = mainFrame.getSbActionManager()
			.getActionManager();
		Action act = actMngr.getAction("rename-city-command");
		Object obj = act.getValue(SbConstants.ActionKey.CATEGORY.toString());
		if (obj != null) {
			dlg.setSelectedItem(obj);
		}
		SwingUtil.showModalDialog(dlg, mainFrame);
		act.putValue(SbConstants.ActionKey.CATEGORY.toString(), null);
	}

	public void handleRenameCountry() {
		RenameCountryDialog dlg = new RenameCountryDialog(mainFrame);
		ActionManager actMngr = mainFrame.getSbActionManager()
			.getActionManager();
		Action act = actMngr.getAction("rename-country-command");
		Object obj = act.getValue(SbConstants.ActionKey.CATEGORY.toString());
		if (obj != null) {
			dlg.setSelectedItem(obj);
		}
		SwingUtil.showModalDialog(dlg, mainFrame);
		act.putValue(SbConstants.ActionKey.CATEGORY.toString(), null);
	}

	public void handleRenameTagCategory() {
		RenameTagCategoryDialog dlg = new RenameTagCategoryDialog(mainFrame);
		ActionManager actMngr = mainFrame.getSbActionManager().getActionManager();
		Action act = actMngr.getAction("rename-tag-category-command");
		Object obj = act.getValue(SbConstants.ActionKey.CATEGORY.toString());
		if (obj != null) {
			dlg.setSelectedItem(obj);
		}
		SwingUtil.showModalDialog(dlg, mainFrame);
		act.putValue(SbConstants.ActionKey.CATEGORY.toString(), null);
	}

	public void handleRenameItemCategory() {
		RenameItemCategoryDialog dlg = new RenameItemCategoryDialog(mainFrame);
		ActionManager actMngr = mainFrame.getSbActionManager().getActionManager();
		Action act = actMngr.getAction("rename-item-category-command");
		Object obj = act.getValue(SbConstants.ActionKey.CATEGORY.toString());
		if (obj != null) {
			dlg.setSelectedItem(obj);
		}
		SwingUtil.showModalDialog(dlg, mainFrame);
		act.putValue(SbConstants.ActionKey.CATEGORY.toString(), null);
	}
/*
	public void handleNewScene()		{handleNewEntity(new Scene());}//new OK
	public void handleNewChapter()	{handleNewEntity(new Chapter());}//new OK
	public void handleNewPart()		{handleNewEntity(new Part());}//new OK
	public void handleNewStrand()		{handleNewEntity(new Strand());}//new OK
	public void handleNewPerson()		{handleNewEntity(new Person());}//new OK
	public void handleNewCategory()	{handleNewEntity(new Category());}//new OK
	public void handleNewGender()		{handleNewEntity(new Gender());}//new OK
	public void handleNewLocation()	{handleNewEntity(new Location());}//new OK
	public void handleNewTag()			{handleNewEntity(new Tag());}//new OK
	public void handleNewTagLink()	{handleNewEntity(new TagLink());}//new OK
	public void handleNewItem()		{handleNewEntity(new Item());}//new OK
	public void handleNewItemLink()	{handleNewEntity(new ItemLink());}//new OK
	public void handleNewIdea()		{handleNewEntity(new Idea());}//new OK

	private void handleNewEntity(AbstractEntity entity) {//new OK
		BookController ctrl = mainFrame.getBookController();
		ctrl.setEntityToEdit(entity);
		mainFrame.showView(ViewName.EDITOR);
		//SbApp.trace("ActionHandler.handleEntity(...)");
		//EditorDlg dlg=new EditorDlg(mainFrame,entity);
		//dlg.setVisible(true);
	}
*/
	/*
	public void handleFlashOfInspiration() {
		FoiDialog dlg = new FoiDialog(mainFrame);
		SwingUtil.showModalDialog(dlg, mainFrame);
	}

	public void handleTaskList() {//new OK
		showAndFocus(ViewName.SCENES);
		mainFrame.getBookController().showTaskList();
	}

	public void handleExportPrint() {
		//ExportPrintDialog dlg = new ExportPrintDialog(mainFrame);
		//SwingUtil.showDialog(dlg, mainFrame);
	}

	public void handleChartGantt() {//new OK
		showAndFocus(ViewName.CHART_GANTT);
	}

	public void handleChartOccurrenceOfLocations() {//new OK
		showAndFocus(ViewName.CHART_OCCURRENCE_OF_LOCATIONS);
	}

	public void handleChartOccurrenceOfPersons() {//new OK
		showAndFocus(ViewName.CHART_OCCURRENCE_OF_PERSONS);
	}

	public void handleChartStrandsByDate() {//new OK
		showAndFocus(ViewName.CHART_STRANDS_BY_DATE);
	}

	public void handleChartWiWW() {//new OK
		showAndFocus(ViewName.CHART_WiWW);
	}

	public void handleChartPersonsByDate() {
		showAndFocus(ViewName.CHART_PERSONS_BY_DATE);
	}

	public void handleChartPersonsByScene() {
		showAndFocus(ViewName.CHART_PERSONS_BY_SCENE);
	}
*/
	public void handlePreviousPart() {
		Part currentPart = mainFrame.getCurrentPart();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl dao = new PartDAOImpl(session);
		List<Part> parts = dao.findAll();
		int index = parts.indexOf(currentPart);
		if (index == 0) {
			// already first part
			return;
		}
		--index;
		handleChangePart(parts.get(index));
	}

	public void handleNextPart() {
		Part currentPart = mainFrame.getCurrentPart();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl dao = new PartDAOImpl(session);
		List<Part> parts = dao.findAll();
		int index = parts.indexOf(currentPart);
		if (index == parts.size() - 1) {
			// already last part
			return;
		}
		++index;
		handleChangePart(parts.get(index));
	}

	public void handleChangePart(Part part) {
		mainFrame.setWaitingCursor();
		Part currentPart = mainFrame.getCurrentPart();
		if (currentPart.getId().equals(part.getId())) {
			// same part
			return;
		}
		mainFrame.setCurrentPart(part);
		mainFrame.setTitle();
		mainFrame.getBookController().changePart(part);
		mainFrame.setDefaultCursor();
	}

	public void handleShowChronoView() {//new OK
		showAndFocus(ViewName.CHRONO);
	}

	public void handleShowAttributesView() {
		showAndFocus(ViewName.ATTRIBUTES);
	}

	public void handleShowBookView() {//new OK
		showAndFocus(ViewName.BOOK);
	}

	public void handleShowManageView() {//new OK
		showAndFocus(ViewName.MANAGE);
	}

	public void handleShowReadingView() {//new OK
		showAndFocus(ViewName.READING);
	}

	public void handleShowMemoria() {
		showAndFocus(ViewName.MEMORIA);
	}

	public void handleShowEditor() {
		mainFrame.showEditor();
	}

	public void handleShowTree() {
		showAndFocus(ViewName.TREE);
	}

	public void handleShowInfo() {
		showAndFocus(ViewName.INFO);
	}

	public void handleShowNavigation() {
		showAndFocus(ViewName.NAVIGATION);
	}

	public void handleDumpAttachedViews() {
		mainFrame.getBookController().printAttachedViews();
	}
	/* suppression du garbage collector
	 public void handleRunGC() {
	 SwingUtil.printMemoryUsage();
	 System.out.println("ActionHandler.handleRunGC(): running GC...");
	 SbApp.getInstance().runGC();
	 SwingUtil.printMemoryUsage();
	 }
	 */

	public void handleDummy() {
		SbApp.trace("ActionHandler.handleDummy(): ");
		try {
			SbView view = mainFrame.getView(SbConstants.ViewName.EDITOR);
			mainFrame.getViewFactory().unloadView(view);
		} catch (Exception ex) {
			SbApp.error("ActionHandler.handleDummy()",ex);
		}
	}


	public void handleShowInternals() {
		showAndFocus(ViewName.INTERNALS);
	}

	public void handleShowScenes() {
		showAndFocus(ViewName.SCENES);
	}

	public void handleShowTags() {
		showAndFocus(ViewName.TAGS);
	}

	public void handleShowTagLinks() {
		showAndFocus(ViewName.TAGLINKS);
	}

	public void handleShowItems() {
		showAndFocus(ViewName.ITEMS);
	}

	public void handleShowItemLinks() {
		showAndFocus(ViewName.ITEMLINKS);
	}

	public void handleShowIdeas() {
		showAndFocus(ViewName.IDEAS);
	}

	public void handleShowStrands() {
		showAndFocus(ViewName.STRANDS);
	}

	public void handleShowCategories() {
		showAndFocus(ViewName.CATEGORIES);
	}

	public void handleShowGenders() {
		showAndFocus(ViewName.GENDERS);
	}

	public void handleShowPersons() {
		showAndFocus(ViewName.PERSONS);
	}

	public void handleShowLocations() {
		showAndFocus(ViewName.LOCATIONS);
	}

	public void handleShowChapters() {
		showAndFocus(ViewName.CHAPTERS);
	}

	public void handleShowParts() {
		showAndFocus(ViewName.PARTS);
	}

	private void showAndFocus(ViewName viewName) {
		View view = mainFrame.getView(viewName);
		view.restore();
		view.restoreFocus();
	}

	/*public void handleNewFile() {//new OK
		SbApp.getInstance().createNewFile();
	}*/

	/*public void handleOpenFile() {//new OK
		mainFrame.setWaitingCursor();
		SbApp.getInstance().openFile();
		mainFrame.setDefaultCursor();
	}*/

	public void handleRecentClear() {
		SbApp.getInstance().clearRecentFiles();
	}

	public void handleFileSave() {//new OK
		WaitDialog dlg = new WaitDialog(mainFrame,I18N.getMsg("msg.file.saving"));
		Timer timer = new Timer(500, new DisposeDialogAction(dlg));
		timer.setRepeats(false);
		timer.start();
		SwingUtil.showModalDialog(dlg, mainFrame);
	}

	public void handleFileSaveAs() {
		SaveAsFileDialog dlg = new SaveAsFileDialog(mainFrame);
		SwingUtil.showModalDialog(dlg, mainFrame);
		if (dlg.isCanceled()) {
			return;
		}
		File file = dlg.getFile();
		try {
			FileUtils.copyFile(mainFrame.getDbFile().getFile(), file);
		} catch (IOException ioe) {
			System.err.println("ActionHandler.handleSaveAs() IOex : "+ioe.getMessage());
		}
		DbFile dbFile = new DbFile(file);
		OpenFileAction act = new OpenFileAction("", dbFile);
		act.actionPerformed(null);
	}

	public void handleFileRename() {
		RenameFileDialog dlg = new RenameFileDialog(mainFrame);
		SwingUtil.showModalDialog(dlg, mainFrame);
		if (dlg.isCanceled()) {
			return;
		}
		File file = dlg.getFile();
		SbApp.getInstance().renameFile(mainFrame, file);
	}

	public void handleClose() {
		mainFrame.close();
	}

	public void handleSaveLayout() {
		String name = JOptionPane.showInputDialog(mainFrame,
			I18N.getMsgColon("msg.common.enter.name"),
			I18N.getMsg("msg.docking.save.layout"),
			JOptionPane.PLAIN_MESSAGE);
		if (name != null) {
			DockingWindowUtil.saveLayout(mainFrame, name);
		}
	}

	public void handleManageLayouts() {
		ManageLayoutsDialog dlg = new ManageLayoutsDialog(mainFrame);
		SwingUtil.showModalDialog(dlg, mainFrame);
	}

	public void handleDefaultLayout() {
		DockingWindowUtil.setLayout(mainFrame,DockingWindowUtil.DEFAULT_LAYOUT);
	}

	public void handlePersonsLocationsLayout() {
		DockingWindowUtil.setLayout(mainFrame,DockingWindowUtil.PERSONS_LOCATIONS_LAYOUT);
	}

	public void handleTagsItemsLayout() {
		DockingWindowUtil.setLayout(mainFrame,DockingWindowUtil.TAGS_ITEMS_LAYOUT);
	}

	public void handleChronoOnlyLayout() {
		DockingWindowUtil.setLayout(mainFrame,DockingWindowUtil.CHRONO_ONLY_LAYOUT);
	}

	public void handleBookOnlyLayout() {
		DockingWindowUtil.setLayout(mainFrame,DockingWindowUtil.BOOK_ONLY_LAYOUT);
	}

	public void handleManageOnlyLayout() {
		DockingWindowUtil.setLayout(mainFrame,DockingWindowUtil.MANAGE_ONLY_LAYOUT);
	}

	public void handleReadingOnlyLayout() {
		DockingWindowUtil.setLayout(mainFrame,DockingWindowUtil.READING_ONLY_LAYOUT);
	}

	public void handleResetLayout() {
		SwingUtil.setWaitingCursor(mainFrame);
		mainFrame.setDefaultLayout();
		// suppression du garbage collector
		//SbApp.getInstance().runGC();
		SwingUtil.setDefaultCursor(mainFrame);
	}

	public void handleRefresh() {
		mainFrame.refresh();
	}

	public void handleBookProperties() {//new OK
		BookPropertiesDialog dlg = new BookPropertiesDialog(mainFrame);
		SwingUtil.showModalDialog(dlg, mainFrame);
	}

	public void handlePreferences() {//new OK
		PreferencesDialog dlg = new PreferencesDialog();
		SwingUtil.showModalDialog(dlg, mainFrame);
	}

	public void handleViewStatus(boolean selected) {
	}

	/*public void handleExportBookText() {//new OK
		DlgExport export=new DlgExport(mainFrame);
		export.setVisible(true);
		// old
		//BookExporter exp = new BookExporter(mainFrame);
		//exp.setExportForOpenOffice(true);
		//exp.exportToHtmlFile();
	}*/

	/*public void handleExportBookHtml() {//new OK
		BookExporter exp = new BookExporter(mainFrame);
		exp.setExportForOpenOffice(true);
		exp.exportToHtmlFile();
	}*/

	public void handleCopyBookText() {//new OK
		BookExporter exp = new BookExporter(mainFrame);
		exp.setExportForOpenOffice(false);
		exp.exportToClipboard();
	}

	public void handleCopyBlurb() {//new OK
		Internal internal = BookUtil.get(mainFrame,BookKey.BLURB, "");
		StringSelection selection = new StringSelection(internal.getStringValue() + "\n");
		Clipboard clbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clbrd.setContents(selection, selection);
	}

	public void handleReportBug() {//new OK
		NetUtil.openBrowser(SbConstants.URL.REPORTBUG.toString());
	}

	public void handleDoc() {//new OK
		NetUtil.openBrowser(SbConstants.URL.DOC.toString());
	}

	public void handleFAQ() {//new OK
		NetUtil.openBrowser(SbConstants.URL.FAQ.toString());
	}

	public void handleHomepage() {
		NetUtil.openBrowser(SbConstants.URL.HOMEPAGE.toString());
	}

	public void handleAbout() {
		AboutDialog dlg = new AboutDialog(mainFrame);
		SwingUtil.showModalDialog(dlg, mainFrame);
	}

	public void handleTrace() {//new OK
		if (SbApp.getInstance().getTrace()) SbApp.getInstance().setTrace(false);
		else SbApp.getInstance().setTrace(true);
	}

	public void handleExit() {
		SbApp.getInstance().exit();
	}
}
