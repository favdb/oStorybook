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

package storybook.ui.combo;

import java.util.List;

import javax.swing.DefaultComboBoxModel;

import org.hibernate.Session;
import storybook.model.BookModel;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.Scene;
import storybook.ui.MainFrame;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class RelativeSceneComboModel extends DefaultComboBoxModel implements
		IRefreshableComboModel {

	private MainFrame mainFrame;

	public RelativeSceneComboModel() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public void refresh() {
		if (mainFrame == null) {
			return;
		}
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		List<Scene> scenes = dao.findAll();
		for (Scene scene : scenes) {
			addElement(scene);
		}
		model.commit();
	}

	@Override
	public void setSelectedItem(Object obj) {
		Scene scene;
		if (obj instanceof Long) {
			BookModel model = mainFrame.getBookModel();
			Session session = model.beginTransaction();
			scene = (Scene) session.get(Scene.class, (Long) obj);
			model.commit();
		} else {
			scene = (Scene) obj;
		}
		super.setSelectedItem(scene);
	}

	@Override
	public MainFrame getMainFrame() {
		return mainFrame;
	}

	@Override
	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
}
