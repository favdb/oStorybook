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

import java.awt.event.ActionEvent;
import java.util.List;

import org.hibernate.Session;

import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.I18N;
import storybook.ui.MainFrame;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class ChapterOrderByTimestampAction extends AbstractEntityAction {

	private final Chapter chapter;

	public ChapterOrderByTimestampAction(MainFrame mainFrame, Chapter chapter) {
		super(mainFrame, chapter, I18N.getMsg("msg.common.order.by.time"), I18N
				.getIcon("icon.small.sort"));
		this.chapter = chapter;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		BookModel model = mainFrame.getBookModel();
		BookController ctrl = mainFrame.getBookController();
		Session session = model.beginTransaction();
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		List<Scene> scenes = dao.findScenesOrderByTimestamp(chapter);
		session.close();
		int counter = 1;
		for (Scene scene : scenes) {
			scene.setSceneno(counter);
			ctrl.updateScene(scene);
			++counter;
		}
	}
}
