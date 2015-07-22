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

package storybook.model.handler;

import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.Session;

import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Chapter;
import storybook.toolkit.I18N;
import storybook.ui.MainFrame;
import storybook.ui.table.SbColumnFactory;

/**
 * @author martin
 *
 */
public class ChapterEntityHandler extends AbstractEntityHandler {

	public ChapterEntityHandler(MainFrame mainFrame) {
		super(mainFrame, SbColumnFactory.getInstance().getChapterColumns());
	}

	@Override
	public AbstractEntity createNewEntity() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		Integer nextNumber = dao.getNextChapterNumber();
		model.commit();

		Chapter chapter = new Chapter();
		chapter.setChapterno(nextNumber);
		chapter.setDescription("");
		chapter.setNotes("");
		chapter.setCreationTime(new Timestamp(new Date().getTime()));
		chapter.setObjectiveTime(null);
		chapter.setDoneTime(null);
		chapter.setTitle(I18N.getMsg("msg.common.chapter") + " " + nextNumber);
		return chapter;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Class<T> getDAOClass() {
		return (Class<T>) ChapterDAOImpl.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Class<T> getEntityClass() {
		return (Class<T>) Chapter.class;
	}
}
