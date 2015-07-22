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

package storybook.ui.table;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import org.hibernate.Session;
import storybook.SbConstants.ViewName;
import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Part;
import storybook.ui.MainFrame;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class ChapterTable extends AbstractTable {

	public ChapterTable(MainFrame mainFrame) {
		super(mainFrame);
	}

	@Override
	public void init() {
		columns = SbColumnFactory.getInstance().getChapterColumns();
		allowMultiDelete = false;
	}

	@Override
	protected void modelPropertyChangeLocal(PropertyChangeEvent evt) {
		try {
			String propName = evt.getPropertyName();
			if (BookController.ChapterProps.INIT.check(propName)) {
				initTableModel(evt);
			} else if (BookController.ChapterProps.UPDATE.check(propName)) {
				updateEntity(evt);
			} else if (BookController.ChapterProps.NEW.check(propName)) {
				newEntity(evt);
			} else if (BookController.ChapterProps.DELETE.check(propName)) {
				deleteEntity(evt);
			} else if (BookController.PartProps.UPDATE.check(propName)) {
				updateParts(evt);
			}
		} catch (Exception e) {
		}
	}

	private void updateParts(PropertyChangeEvent evt) {
		Part oldPart = (Part) evt.getOldValue();
		Part newPart = (Part) evt.getNewValue();
		for (int row = 0; row < tableModel.getRowCount(); ++row) {
			if (oldPart.equals(newPart)) {
				tableModel.setValueAt(newPart, row, 1);
			}
		}
	}

	@Override
	protected void sendSetEntityToEdit(int row) {
		if (row == -1) {
			return;
		}
		Chapter chapter = (Chapter) getEntityFromRow(row);
		ctrl.setChapterToEdit(chapter);
		mainFrame.showView(ViewName.EDITOR);
	}

	@Override
	protected void sendSetNewEntityToEdit(AbstractEntity entity) {
		ctrl.setChapterToEdit((Chapter) entity);
		mainFrame.showView(ViewName.EDITOR);
	}

	@Override
	protected synchronized void sendDeleteEntity(int row) {
		Chapter chapter = (Chapter) getEntityFromRow(row);
		ctrl.deleteChapter(chapter);
	}

	@Override
	protected synchronized void sendDeleteEntities(int[] rows) {
		ArrayList<Long> ids = new ArrayList<>();
		for (int row : rows) {
			Chapter chapter = (Chapter) getEntityFromRow(row);
			ids.add(chapter.getId());
		}
		ctrl.deleteMultiChapters(ids);
	}

	@Override
	protected AbstractEntity getEntity(Long id) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		Chapter chapter = dao.find(id);
		model.commit();
		return chapter;
	}

	@Override
	protected AbstractEntity getNewEntity() {
		return new Chapter();
	}
}
