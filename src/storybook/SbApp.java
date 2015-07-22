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

package storybook;

import java.awt.Component;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.FontUIResource;

import org.apache.commons.io.FileUtils;

import storybook.SbConstants.BookKey;
import storybook.SbConstants.PreferenceKey;
import storybook.action.OpenFileAction;
import storybook.controller.PreferenceController;
import storybook.model.DbFile;
import storybook.model.PreferenceModel;
import storybook.model.hbn.entity.Preference;
import storybook.model.oldModel.ModelMigration;
import storybook.toolkit.BookUtil;
import storybook.toolkit.I18N;
import storybook.toolkit.PrefUtil;
import storybook.toolkit.net.Updater;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;
import storybook.ui.dialog.ExceptionDialog;
import storybook.ui.dialog.FirstStartDialog;
import storybook.ui.dialog.PostModelUpdateDialog;
import storybook.ui.dialog.SplashDialog;
import storybook.ui.dialog.file.NewFileDialog;

public class SbApp extends Component {
	private static boolean bTrace=false;
	private static boolean bTraceHibernate=false;

	private static SbApp instance;

	private PreferenceModel preferenceModel;
	private PreferenceController preferenceController;
	private final List<MainFrame> mainFrames;
	private Font defaultFont;

	private SbApp() {
		mainFrames = new ArrayList<>();
	}

	private void init() {
		trace("SbApp.init()");
		SplashDialog dlgStart=new SplashDialog("oStorybook init");
		try {
			MainFrame mainFrame = new MainFrame();
			// preference model and controller
			trace("-->PreferenceController()");
			preferenceController = new PreferenceController();
			trace("-->PreferenceModel()");
			preferenceModel = new PreferenceModel(mainFrame);
			trace("-->PreferenceModel.attachModel()");
			preferenceController.attachModel(preferenceModel);
			trace("-->PreferenceController.attachView()");
			preferenceController.attachView(this);
			trace("-->initI18N()");
			initI18N();
			trace("-->SwingUtil.setLookAndFeel()");
			SwingUtil.setLookAndFeel();
			restoreDefaultFont();
			// first start dialog
			Preference prefFirstStart = PrefUtil.get(PreferenceKey.FIRST_START_4, true);
			if (prefFirstStart.getBooleanValue()) {
				FirstStartDialog dlg = new FirstStartDialog();
				SwingUtil.showModalDialog(dlg, null);
				PrefUtil.set(PreferenceKey.FIRST_START_4, false);
			}

			Preference pref = PrefUtil.get(PreferenceKey.OPEN_LAST_FILE, false);
			boolean fileHasBeenOpened = false;
			if (pref.getBooleanValue()) {
				Preference pref2 = PrefUtil.get(PreferenceKey.LAST_OPEN_FILE, "");
				DbFile dbFile = new DbFile(pref2.getStringValue());
				trace("SbApp.init(): loading... " + dbFile);
				fileHasBeenOpened = openFile(dbFile);
			}
			if (fileHasBeenOpened) {
				// check for updates
				Updater.checkForUpdate();
				dlgStart.dispose();
				return;
			}
			mainFrame.init();
			mainFrame.initBlankUi();
			addMainFrame(mainFrame);

			// check for updates
			Updater.checkForUpdate();
/* abandon de l'appel au garbarge collector, utilisation non recommandée
			Timer t1 = new Timer(10000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					runGC();
				}
			});
			t1.start();
			*/

		} catch (Exception e) {
			error("SbApp.init()",e);
			dlgStart.dispose();
			ExceptionDialog dlg = new ExceptionDialog(e);
			SwingUtil.showModalDialog(dlg, null);
		}
		dlgStart.dispose();
	}

	public void initI18N() {
		trace("SbApp.initI18N()");
		String localeStr = PrefUtil.get(PreferenceKey.LANG, SbConstants.DEFAULT_LANG).getStringValue();
		SbConstants.Language lang = SbConstants.Language.valueOf(localeStr);
		Locale locale = lang.getLocale();
		setLocale(locale);
		I18N.initResourceBundles(getLocale());
	}

	public PreferenceModel getPreferenceModel() {
		return preferenceModel;
	}

	public PreferenceController getPreferenceController() {
		return preferenceController;
	}

	public List<MainFrame> getMainFrames() {
		return mainFrames;
	}

	public void addMainFrame(MainFrame mainFrame) {
		trace("SbApp.addMainFrame("+mainFrame.getName()+")");
		mainFrames.add(mainFrame);
	}

	public void removeMainFrame(MainFrame mainFrame) {
		trace("SbApp.removeMainFrame("+mainFrame.getName()+")");
		mainFrames.remove(mainFrame);
	}

	public void closeBlank() {
		trace("SbApp.closeBlank()");
		for (MainFrame mainFrame : mainFrames) {
			if (mainFrame.isBlank()) {
				mainFrames.remove(mainFrame);
				mainFrame.dispose();
			}
		}
	}
/* suppression de l'appel du garbage collector
	public void runGC(){
		System.gc();
	}
*/
	public static SbApp getInstance() {
		if (instance == null) {
			instance = new SbApp();
		}
		return instance;
	}

	public void createNewFile() {
		trace("SbApp.createNewFile()");
		try {
			NewFileDialog dlg = new NewFileDialog();
			SwingUtil.showModalDialog(dlg, null);
			if (dlg.isCanceled()) {
				return;
			}
			DbFile dbFile = new DbFile(dlg.getFile());
			String dbName = dbFile.getDbName();
			if (dbName == null) {
				return;
			}
			final MainFrame newMainFrame = new MainFrame();
			newMainFrame.init(dbFile);
			newMainFrame.getBookModel().initEntites();
			BookUtil.store(newMainFrame, BookKey.USE_HTML_SCENES, dlg.getUseHtmlScenes());
			BookUtil.store(newMainFrame, BookKey.USE_HTML_DESCR, dlg.getUseHtmlDescr());
			BookUtil.store(newMainFrame, BookKey.BOOK_CREATION_DATE,
					new SimpleDateFormat("dd/MM/yy").format(new Date()));
			newMainFrame.initUi();
			newMainFrame.getBookController().fireAgain();
			addMainFrame(newMainFrame);
			closeBlank();
			updateFilePref(dbFile);
			setDefaultCursor();
		} catch (Exception e) {
			error("SbApp.createNewFile()",e);
		}
	}

	public void renameFile(final MainFrame mainFrame, File file) {
		trace("SbApp.renameFile("+mainFrame.getName()+","+file.getAbsolutePath()+")");
		try {
			FileUtils.copyFile(mainFrame.getDbFile().getFile(), file);
			DbFile dbFile = new DbFile(file);
			OpenFileAction act = new OpenFileAction("", dbFile);
			act.actionPerformed(null);
			Timer t1 = new Timer(1000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					mainFrame.close();
				}
			});
			t1.setRepeats(false);
			t1.start();
			Timer t2 = new Timer(4000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					mainFrame.getDbFile().getFile().delete();
				}
			});
			t2.setRepeats(false);
			t2.start();
		} catch (IOException e) {
			error("SbApp.renameFile("+mainFrame.getName()+","+file.getName()+")", e);
		}
	}

	public boolean openFile() {
		trace("SbApp.openFile()");
		final DbFile dbFile = BookUtil.openDocumentDialog();
		if (dbFile == null) {
			return false;
		}
		return openFile(dbFile);
	}

	public boolean openFile(final DbFile dbFile) {
		trace("SbApp.openFile("+dbFile.getDbName()+")");
		try {
			// file doesn't exist
			if (!dbFile.getFile().exists()) {
				String txt = I18N.getMsg("msg.dlg.project.not.exits.text", dbFile.getFile().getPath());
				JOptionPane.showMessageDialog(null, txt, I18N.getMsg("msg.dlg.project.not.exits.title"),
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			// file is read-only
			if (!dbFile.getFile().canWrite()) {
				String txt = I18N.getMsg("msg.error.db.read.only", dbFile.getFile().getPath());
				JOptionPane.showMessageDialog(null, txt, I18N.getMsg("msg.common.warning"),
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			// file already opened
			String dbName = dbFile.getDbName();
			if (checkIfAlreadyOpened(dbName)) {
				return true;
			}
			// model update from Storybook 3.x to 4.0
			final ModelMigration oldPersMngr = ModelMigration.getInstance();
			oldPersMngr.open(dbFile);
			try {
				if (!oldPersMngr.checkAndAlterModel()) {
					oldPersMngr.closeConnection();
					return false;
				}
			} catch (Exception e) {
				oldPersMngr.closeConnection();
				SbApp.error("SbApp.openFile("+dbFile.getDbName()+")",e);
				ExceptionDialog dlg = new ExceptionDialog(e);
				SwingUtil.showModalDialog(dlg, null);
				return false;
			}
			oldPersMngr.closeConnection();
			setWaitCursor();
			String text = I18N.getMsg("msg.common.loading", dbFile.getName());
			//final HourglassSplash dlg = new HourglassSplash(text);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						MainFrame newMainFrame = new MainFrame();
						newMainFrame.init(dbFile);
						newMainFrame.initUi();
						addMainFrame(newMainFrame);
						closeBlank();
						updateFilePref(dbFile);
						reloadMenuBars();
						setDefaultCursor();
						//dlg.dispose();

						if (oldPersMngr.hasAlteredDbModel()) {
							PostModelUpdateDialog dlg2 = new PostModelUpdateDialog(newMainFrame);
							SwingUtil.showModalDialog(dlg2, newMainFrame);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			});
		} catch (HeadlessException e) {
		}
		return true;
	}

	private boolean checkIfAlreadyOpened(String dbName) {
		trace("SbApp.checkIfAlreadyOpened("+dbName+")");
		for (MainFrame mainFrame : mainFrames) {
			if (mainFrame.isBlank()) {
				continue;
			}
			if (mainFrame.getDbFile().getDbName().equals(dbName)) {
				mainFrame.setVisible(true);
				return true;
			}
		}
		return false;
	}

	private void updateFilePref(DbFile dbFile) {
		trace("SbApp.updateFilePref("+dbFile.getDbName()+")");
		// save last open directory and file
		File file = dbFile.getFile();
		PrefUtil.set(PreferenceKey.LAST_OPEN_DIR, file.getParent());
		PrefUtil.set(PreferenceKey.LAST_OPEN_FILE, file.getPath());
		// save recent files
		List<DbFile> list = PrefUtil.getDbFileList();
		if (!list.contains(dbFile)) {
			list.add(dbFile);
		}
		// check recent files and remove non-existing entries
		Iterator<DbFile> it = list.iterator();
		while (it.hasNext()) {
			DbFile dbFile2 = it.next();
			if (!dbFile2.getFile().exists()) {
				it.remove();
			}
		}
		PrefUtil.setDbFileList(list);
		reloadMenuBars();
	}

	public void clearRecentFiles() {
		trace("SbApp.clearRecentFiles()");
		PrefUtil.setDbFileList(new ArrayList<DbFile>());
		reloadMenuBars();
	}

	public void exit() {
		trace("SbApp.exit()");
		if (mainFrames.size() > 0) {
			Preference pref = PrefUtil.get(PreferenceKey.CONFIRM_EXIT, true);
			if (pref.getBooleanValue()) {
				int n = JOptionPane.showConfirmDialog(null,
						I18N.getMsg("msg.mainframe.want.exit"),
						I18N.getMsg("msg.common.exit"),
						JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
					return;
				}
			}
			saveAll();
		}
		System.exit(0);
	}

	public void resetUiFont() {
		if (defaultFont == null) {
			return;
		}
		SwingUtil.setUIFont(new FontUIResource(defaultFont.getName(), defaultFont.getStyle(), defaultFont.getSize()));
	}

	public void setDefaultFont(Font font) {
		if (font == null) {
			return;
		}
		defaultFont = font;
		resetUiFont();
		PrefUtil.set(PreferenceKey.DEFAULT_FONT_NAME, font.getName());
		PrefUtil.set(PreferenceKey.DEFAULT_FONT_SIZE, font.getSize());
		PrefUtil.set(PreferenceKey.DEFAULT_FONT_STYLE, font.getStyle());
	}

	public Font getDefaultFont() {
		return this.defaultFont;
	}

	public void restoreDefaultFont() {
		Preference pref = PrefUtil.get(PreferenceKey.DEFAULT_FONT_NAME, SbConstants.DEFAULT_FONT_NAME);
		String name = SbConstants.DEFAULT_FONT_NAME;
		if (pref != null && !pref.getStringValue().isEmpty()) {
			name = pref.getStringValue();
		}
		pref = PrefUtil.get(PreferenceKey.DEFAULT_FONT_STYLE, SbConstants.DEFAULT_FONT_STYLE);
		int style = 0;
		if (pref != null) {
			style = pref.getIntegerValue();
		}
		pref = PrefUtil.get(PreferenceKey.DEFAULT_FONT_SIZE, SbConstants.DEFAULT_FONT_SIZE);
		int size = 0;
		if (pref != null) {
			size = pref.getIntegerValue();
		}
		// set default font
		setDefaultFont(new Font(name, style, size));
	}

	public void refresh() {
		trace("SbApp.refresh()");
		for (MainFrame mainFrame : mainFrames) {
			int width = mainFrame.getWidth();
			int height = mainFrame.getHeight();
			boolean maximized = mainFrame.isMaximized();
			mainFrame.getSbActionManager().reloadMenuToolbar();
			mainFrame.setSize(width, height);
			if (maximized) {
				mainFrame.setMaximized();
			}
			mainFrame.refresh();
		}
	}

	public void reloadMenuBars() {
		for (MainFrame mainFrame : mainFrames) {
			mainFrame.getSbActionManager().reloadMenuToolbar();
		}
	}

	public void reloadStatusBars() {
		for (MainFrame mainFrame : mainFrames) {
			mainFrame.refreshStatusBar();
		}
	}

	public void setWaitCursor() {
		for (MainFrame mainFrame : mainFrames) {
			SwingUtil.setWaitingCursor(mainFrame);
		}
	}

	public void setDefaultCursor() {
		for (MainFrame mainFrame : mainFrames) {
			SwingUtil.setDefaultCursor(mainFrame);
		}
	}

	public void saveAll() {
		trace("SbApp.saveAll()");
		for (MainFrame mainFrame : mainFrames) {
			mainFrame.getSbActionManager().getActionHandler().handleFileSave();
		}
	}

	public void modelPropertyChange(PropertyChangeEvent evt) {
		// works, but currently not used
		// may be used for entity copying between files
		// String propName = evt.getPropertyName();
		// Object newValue = evt.getNewValue();
		// Object oldValue = evt.getOldValue();
	}

	public static void main(String[] args) {
		String tempDir = System.getProperty("java.io.tmpdir");
		String fn = tempDir + File.separator + "storybook.lck";
		if (!lockInstance(fn)) {
			Object[] options = { I18N.getMsg("msg.running.remove"),
					I18N.getMsg("msg.common.cancel") };
			int n = JOptionPane.showOptionDialog(null,
					I18N.getMsg("msg.running.msg"),
					I18N.getMsg("msg.running.title"),
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
			if (n == 0) {
				File file = new File(fn);
				if (file.exists() && file.canWrite()) {
					if (!file.delete()) {
						JOptionPane.showMessageDialog(null, "Delete failed",
								"File\n" + file.getAbsolutePath() + "\ncould not be deleted.",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			return;
		}
		
		if (args.length>0) {
			for (String arg : args) {
				if (arg.equalsIgnoreCase("--trace")) {
					SbApp.bTrace=true;
					SbApp.trace("Storybook execution in trace mode");
				}
				if (arg.equalsIgnoreCase("--hibernate")) {
					SbApp.bTraceHibernate=true;
					SbApp.trace("Hibernate in trace mode");
				}
			}
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SbApp app=SbApp.getInstance();
				app.init();
			}
		});
	}

	private static boolean lockInstance(final String lockFile) {
		try {
			final File file = new File(lockFile);
			final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
			final FileLock fileLock = randomAccessFile.getChannel().tryLock();
			if (fileLock != null) {
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						try {
							fileLock.release();
							randomAccessFile.close();
							file.delete();
						} catch (IOException e) {
							System.err.println("Unable to remove lock file: "+ lockFile+"->"+e.getMessage());
						}
					}
				});
				return true;
			}
		} catch (IOException e) {
			System.err.println("Unable to create and/or lock file: " + lockFile+"->"+e.getMessage());
		}
		return false;
	}

	public static void error(String txt, Exception e) {
		System.err.println(txt+" Exception:"+e.getMessage());
	}

	public static void trace(String msg) {
		if (bTrace) {
			System.out.println(msg);
		}
	}

	public static boolean getTrace() {
		return(bTrace);
	}

	public static boolean getTraceHibernate() {
		return(bTraceHibernate);
	}

	public static void setTrace(boolean b) {
		bTrace=b;
		System.out.println((b?"Enter":"Exit")+" trace mode");
	}

}
