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

package storybook.ui.panel.manage;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.infonode.docking.View;
import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import storybook.SbApp;
import storybook.SbConstants;
import storybook.SbConstants.BookKey;
import storybook.SbConstants.ViewName;
import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Internal;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.BookUtil;
import storybook.toolkit.I18N;
import storybook.toolkit.ViewUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.panel.AbstractScrollPanel;
import storybook.ui.MainFrame;
import storybook.ui.options.ManageOptionsDialog;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class ManagePanel extends AbstractScrollPanel {

	private int cols;

	public ManagePanel(MainFrame mainFrame) {
		super(mainFrame);
	}

	@Override
	protected void setZoomValue(int val) {
		BookUtil.store(mainFrame, BookKey.MANAGE_ZOOM, val);
		mainFrame.getBookController().manageSetZoom(val);
	}

	@Override
	protected int getZoomValue() {
		Internal internal = BookUtil.get(mainFrame,
				BookKey.MANAGE_ZOOM, SbConstants.DEFAULT_MANAGE_ZOOM);
		return internal.getIntegerValue();
	}

	@Override
	protected int getMinZoomValue() {
		return SbConstants.MIN_MANAGE_ZOOM;
	}

	@Override
	protected int getMaxZoomValue() {
		return SbConstants.MAX_MANAGE_ZOOM;
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		SbApp.trace("ManagePanel.modelPropertyChange(...)");
		String propName = evt.getPropertyName();
		Object newValue = evt.getNewValue();

		if (BookController.CommonProps.REFRESH.check(propName)) {
			View newView = (View) evt.getNewValue();
			View view = (View) getParent().getParent();
			if (view == newView) {
				init();
				refresh();
			}
			return;
		}

		if (BookController.CommonProps.SHOW_OPTIONS.check(propName)) {
			View view = (View) evt.getNewValue();
			if (!view.getName().equals(ViewName.MANAGE.toString())) {
				return;
			}
			ManageOptionsDialog dlg = new ManageOptionsDialog(mainFrame);
			SwingUtil.showModalDialog(dlg, this);
			return;
		}

		if (BookController.ManageViewProps.SHOW_ENTITY.check(propName)) {
			if (newValue instanceof Scene) {
				Scene scene = (Scene) newValue;
				ViewUtil.scrollToScene(this, panel, scene);
				return;
			}
			if (newValue instanceof Chapter) {
				Chapter chapter = (Chapter) newValue;
				ViewUtil.scrollToChapter(this, panel, chapter);
				return;
			}
		}

		if (BookController.ManageViewProps.COLUMNS.check(propName)) {
			init();
			refresh();
			return;
		}

		if (BookController.PartProps.CHANGE.check(propName)) {
			refresh();
			ViewUtil.scrollToTop(scroller);
			return;
		}

		if (BookController.ChapterProps.DELETE.check(propName)
				|| BookController.ChapterProps.DELETE_MULTI.check(propName)
				|| BookController.ChapterProps.NEW.check(propName)) {
			refresh();
			return;
		}

		if (BookController.StrandProps.DELETE.check(propName)) {
			refresh();
			return;
		}

		dispatchToChapterPanels(this, evt);
	}

	@Override
	public void init() {
		SbApp.trace("ManagePanel.init()");
		try {
			Internal internal = BookUtil.get(mainFrame, BookKey.MANAGE_COLUMNS, SbConstants.DEFAULT_MANAGE_COLUMNS);
			cols = internal.getIntegerValue();
		} catch (Exception e) {
			e.printStackTrace();
			cols = SbConstants.DEFAULT_MANAGE_COLUMNS;
		}
	}

	@Override
	public void initUi() {
		SbApp.trace("ManagePanel.initUI()");
		setLayout(new MigLayout("flowy,fill,ins 0"));
		panel = new JPanel();
		panel.setBackground(SwingUtil.getBackgroundColor());
		scroller = new JScrollPane(panel);
		SwingUtil.setUnitIncrement(scroller);
		SwingUtil.setMaxPreferredSize(scroller);

		refresh();

		registerKeyboardAction();
		panel.addMouseWheelListener(this);
	}

	@Override
	public void refresh() {
		SbApp.trace("ManagePanel.refresh()");
		Part currentPart = mainFrame.getCurrentPart();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		List<Chapter> chapters = dao.findAll(currentPart);
		model.commit();

		removeAll();

		// "chapter" for unassigned scenes
		JScrollPane scrollerUnassigend = new JScrollPane(new ChapterPanel(mainFrame));
		scrollerUnassigend.setMinimumSize(new Dimension(200, 180));
		add(scrollerUnassigend, "growx");
		add(scroller, "grow");

		// chapters
		MigLayout layout = new MigLayout(
				"wrap " + cols,
				"", // columns
				"[top]" // rows
				);
		panel.setLayout(layout);
		panel.removeAll();
		for (Chapter chapter : chapters) {
			panel.add(new ChapterPanel(mainFrame, chapter), "grow");
		}
		if (panel.getComponentCount() == 0) {
			panel.add(new JLabel(I18N.getMsg("msg.warning.no.chapters")));
		}
		revalidate();
		repaint();
	}

	private static void dispatchToChapterPanels(Container cont, PropertyChangeEvent evt) {
		List<Component> ret = new ArrayList<>();
		SwingUtil.findComponentsByClass(cont, ChapterPanel.class, ret);
		for (Component comp : ret) {
			ChapterPanel panel = (ChapterPanel) comp;
			panel.modelPropertyChange(evt);
		}
	}
}
