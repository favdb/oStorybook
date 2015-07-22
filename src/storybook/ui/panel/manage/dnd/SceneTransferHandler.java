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

package storybook.ui.panel.manage.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.hibernate.Session;
import storybook.SbApp;
import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Scene;
import storybook.ui.MainFrame;
import storybook.ui.panel.manage.ChapterPanel;

@SuppressWarnings("serial")
public class SceneTransferHandler extends TransferHandler {

	private MainFrame mainFrame;
	private DataFlavor sceneFlavor = DataFlavor.stringFlavor;
	private DTScenePanel sourceScene;

	public SceneTransferHandler(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	@Override
	public boolean importData(JComponent comp, Transferable t) {
		if (canImport(comp, t.getTransferDataFlavors())) {
			DTScenePanel destDtScene = (DTScenePanel) comp;
			// don't drop on myself
			if (sourceScene == destDtScene) {
				return true;
			}
			try {
				String sourceSceneIdStr = (String) t
						.getTransferData(sceneFlavor);
				long sourceSceneId = Long.parseLong(sourceSceneIdStr);
				switch (destDtScene.getType()) {
				case DTScenePanel.TYPE_NONE:
					return swapScenes(sourceSceneId, destDtScene);
				case DTScenePanel.TYPE_BEGIN:
					return moveSceneToBegin(sourceSceneId, destDtScene);
				case DTScenePanel.TYPE_NEXT:
					return moveScene(sourceSceneId, destDtScene);
				case DTScenePanel.TYPE_MAKE_UNASSIGNED:
					return unassignScene(sourceSceneId);
				default:
					break;
				}
			} catch (UnsupportedFlavorException | IOException | NumberFormatException e) {
			}
		}
		return false;
	}

	private boolean unassignScene(long sourceSceneId) {
		try {
			BookModel model = mainFrame.getBookModel();
			Session session = model.beginTransaction();
			BookController ctrl = mainFrame.getBookController();
			Scene scene = (Scene) session.get(Scene.class, sourceSceneId);
			// detach scene from session
			session.close();
			scene.setChapter(null);
			ctrl.updateScene(scene);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private boolean moveScene(long sourceSceneId, DTScenePanel destDtScene) {
		try {
			ChapterPanel destChapterPanel = (ChapterPanel) destDtScene
					.getParent();
			Chapter destChapter = destChapterPanel.getChapter();
			int sceneNo = destDtScene.getPreviousNumber() + 1;
			BookModel model = mainFrame.getBookModel();
			Session session = model.beginTransaction();
			BookController ctrl = mainFrame.getBookController();
			Scene scene = (Scene) session.get(Scene.class, sourceSceneId);
			// detach scene from session
			session.close();

			// set destination chapter
			scene.setSceneno(-99);
			scene.setChapter(destChapter);
			ctrl.updateScene(scene);

			int counter = 1;
			session = model.beginTransaction();
			ChapterDAOImpl dao = new ChapterDAOImpl(session);
			List<Scene> scenes = dao.findScenes(destChapter);
			SbApp.trace("SceneTransferHandler.moveScene(): scenes:"+scenes);
			session.close();
			for (Scene s : scenes) {
				if (s.getSceneno() != null && s.getSceneno().intValue() == -99) {
					// insert scene
					s.setSceneno(sceneNo);
					ctrl.updateScene(s);
					++counter;
					continue;
				}
				if (s.getSceneno() != null && s.getSceneno() < sceneNo) {
					++counter;
					continue;
				}
				s.setSceneno(counter + 1);
				ctrl.updateScene(s);
				++counter;
			}
			EntityUtil.renumberScenes(mainFrame, destChapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private boolean moveSceneToBegin(long sourceSceneId, DTScenePanel destDtScene){
		ChapterPanel destChapterPanel = (ChapterPanel) destDtScene.getParent();
		Chapter destChapter = destChapterPanel.getChapter();

		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		BookController ctrl = mainFrame.getBookController();
		Scene scene = (Scene) session.get(Scene.class, sourceSceneId);
		session.close();
		if (destChapter != null) {
			scene.setChapter(destChapter);
		}
		scene.setSceneno(-1);
		ctrl.updateScene(scene);
		EntityUtil.renumberScenes(mainFrame, destChapter);
		return true;
	}

	private boolean swapScenes(long sourceSceneId, DTScenePanel destDtScene) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		BookController ctrl = mainFrame.getBookController();
		Scene sourceScene = (Scene) session.get(Scene.class, sourceSceneId);
		Scene destScene = destDtScene.getScene();
		session.close();
		Chapter sourceChapter = sourceScene.getChapter();
		int sourceSceneno = sourceScene.getSceneno();
		sourceScene.setChapter(destScene.getChapter());
		sourceScene.setSceneno(destScene.getSceneno());
		destScene.setChapter(sourceChapter);
		destScene.setSceneno(sourceSceneno);
		ctrl.updateScene(sourceScene);
		ctrl.updateScene(destScene);
		return true;
	}

	protected Transferable createTransferable(JComponent comp) {
		sourceScene = (DTScenePanel) comp;
		return new SceneTransferable(sourceScene);
	}

	public int getSourceActions(JComponent comp) {
		return COPY_OR_MOVE;
	}

	protected void exportDone(JComponent comp, Transferable data, int action) {

	}

	public boolean canImport(JComponent comp, DataFlavor[] flavors) {
		for (int i = 0; i < flavors.length; i++) {
			if (sceneFlavor.equals(flavors[i])) {
				return true;
			}
		}
		return false;
	}

	class SceneTransferable implements Transferable {
		private String sceneId;

		SceneTransferable(DTScenePanel pic) {
			sceneId = Long.toString(pic.getScene().getId());
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return sceneId;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { sceneFlavor };
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return sceneFlavor.equals(flavor);
		}
	}
}
