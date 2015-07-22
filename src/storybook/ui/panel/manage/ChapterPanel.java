/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008 - 2011 Martin Mustun

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
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import storybook.SbApp;
import storybook.SbConstants;
import storybook.SbConstants.BookKey;
import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Internal;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.BookUtil;
import storybook.toolkit.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.toolkit.swing.label.VerticalLabelUI;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;
import storybook.ui.interfaces.IRefreshable;
import storybook.ui.panel.manage.dnd.DTScenePanel;
import storybook.ui.panel.manage.dnd.SceneTransferHandler;

@SuppressWarnings("serial")
public class ChapterPanel extends AbstractPanel implements IRefreshable {

	private Chapter chapter;
	private SceneTransferHandler sceneTransferHandler;
	private int prefWidth;

	public ChapterPanel(MainFrame mainFrame) {
		this(mainFrame, null);
	}

	public ChapterPanel(MainFrame mainFrame, Chapter chapter) {
		super(mainFrame);
		this.chapter = chapter;
		initAll();
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		Object newValue = evt.getNewValue();
		Object oldValue = evt.getOldValue();
		String propName = evt.getPropertyName();

		if (BookController.ManageViewProps.ZOOM.check(propName)) {
			refresh();
			return;
		}

		if (BookController.StrandProps.UPDATE.check(propName)) {
			refresh();
			return;
		}

		if (BookController.ChapterProps.UPDATE.check(propName)) {
			if(chapter == null){
				return;
			}
			Chapter newChapter = (Chapter) newValue;
			if (!newChapter.getId().equals(chapter.getId())) {
				return;
			}
			chapter = newChapter;
			refresh();
			return;
		}

		if (BookController.SceneProps.UPDATE.check(propName)) {
			Chapter newSceneChapter = ((Scene) newValue).getChapter();
			Chapter oldSceneChapter = ((Scene) oldValue).getChapter();
			if (newSceneChapter == null && chapter == null) {
				refresh();
				return;
			}
			if (chapter == null || newSceneChapter == null
					|| oldSceneChapter == null) {
				refresh();
				return;
			}
			if (!newSceneChapter.getId().equals(chapter.getId())
					&& !oldSceneChapter.getId().equals(chapter.getId())) {
				return;
			}
			refresh();
			return;
		}
	}

	private void setZoomedSize(int zoomValue) {
		prefWidth = 50 + zoomValue * 10;
	}

	@Override
	public void init() {
		SbApp.trace("ChapterPanel.init()");
		try {
			Internal internal = BookUtil.get(mainFrame, BookKey.MANAGE_ZOOM, SbConstants.DEFAULT_MANAGE_ZOOM);
			setZoomedSize(internal.getIntegerValue());
		} catch (Exception e) {
			e.printStackTrace();
			setZoomedSize(SbConstants.DEFAULT_MANAGE_ZOOM);
		}
	}

	@Override
	public void initUi() {
		SbApp.trace("ChapterPanel.initUI()");
		MigLayout layout;
		if (isForUnassignedScene()) {
			layout = new MigLayout(
					"flowx",
					"[]", // columns
					"[fill]" // rows
					);
		} else {
			layout = new MigLayout(
					"flowy",
					"[]",
					"[]4[]0[]");
		}
		setLayout(layout);
		setBorder(SwingUtil.getBorderDefault());
		if (!isForUnassignedScene()) {
			setPreferredSize(new Dimension(prefWidth, 80));
		}
		setComponentPopupMenu(EntityUtil.createPopupMenu(mainFrame, chapter));

		JLabel lbChapter = new JLabel();
		StringBuilder buf = new StringBuilder();
		if (chapter == null) {
			buf.append(I18N.getMsg("msg.unassigned.scenes"));
			lbChapter.setUI(new VerticalLabelUI(false));
		} else {
			buf.append(chapter.getChapternoStr());
			buf.append(" ");
			buf.append(chapter.getTitle());
		}
		lbChapter.setVerticalAlignment(SwingConstants.TOP);
		lbChapter.setText(buf.toString());
		add(lbChapter);

		sceneTransferHandler = new SceneTransferHandler(mainFrame);

		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		List<Scene> scenes = dao.findScenes(chapter);
		model.commit();
		if (chapter == null) {
			// show all unassigned scenes
			for (Scene scene : scenes) {
				DTScenePanel dtScene = new DTScenePanel(mainFrame, scene, DTScenePanel.TYPE_UNASSIGNED);
				dtScene.setTransferHandler(sceneTransferHandler);
				SwingUtil.setForcedSize(dtScene, new Dimension(prefWidth - 10, 140));
				add(dtScene, "growy");
			}
			// to make a scene unassigned
			DTScenePanel makeUnassigned = new DTScenePanel(mainFrame, DTScenePanel.TYPE_MAKE_UNASSIGNED);
			makeUnassigned.setTransferHandler(sceneTransferHandler);
			makeUnassigned.setPreferredSize(new Dimension(280, 140));
			add(makeUnassigned, "grow");
		} else {
			DTScenePanel begin = new DTScenePanel(mainFrame, DTScenePanel.TYPE_BEGIN);
			begin.setTransferHandler(sceneTransferHandler);
			if (scenes.isEmpty()) {
				SwingUtil.setMaxPreferredSize(begin);
			} else {
				begin.setPreferredSize(new Dimension(Short.MAX_VALUE, 15));
			}
			add(begin);

			int i = 0;
			for (Scene scene : scenes) {
				// scene
				DTScenePanel dtScene = new DTScenePanel(mainFrame, scene);
				dtScene.setTransferHandler(sceneTransferHandler);
				add(dtScene, "growx");

				// move next
				DTScenePanel next = new DTScenePanel(mainFrame, DTScenePanel.TYPE_NEXT);
				if (scene.getSceneno() != null) {
					next.setPreviousNumber(scene.getSceneno());
				}
				next.setTransferHandler(sceneTransferHandler);
				if (i < scenes.size() - 1) {
					next.setPreferredSize(new Dimension(Short.MAX_VALUE, 15));
				} else {
					SwingUtil.setMaxPreferredSize(next);
				}
				add(next);
				++i;
			}
		}
	}

	public boolean isForUnassignedScene() {
		return chapter == null;
	}

	protected ChapterPanel getThis() {
		return this;
	}

	public Chapter getChapter() {
		return chapter;
	}

	/**
	 * Gets all {@link DTScenePanel} that have a scene assigned.
	 *
	 * @return a list of all {@link DTScenePanel}
	 * @see DTScenePanel
	 */
	public List<DTScenePanel> getDTScenePanels() {
		List<DTScenePanel> list = new ArrayList<DTScenePanel>();
		for (Component comp : getComponents()) {
			if (comp instanceof DTScenePanel && ((DTScenePanel) comp).getScene() != null) {
				list.add((DTScenePanel) comp);
			}
		}
		return list;
	}
}
