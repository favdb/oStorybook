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
import storybook.model.hbn.dao.TagLinkDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.TagLink;
import storybook.ui.MainFrame;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class TagLinkTable extends AbstractTable {

	public TagLinkTable(MainFrame mainFrame) {
		super(mainFrame);
	}

	@Override
	public void init() {
		columns = SbColumnFactory.getInstance().getTagLinkColumns();
	}

	@Override
	protected void modelPropertyChangeLocal(PropertyChangeEvent evt) {
		try {
			String propName = evt.getPropertyName();
			if (BookController.TagLinkProps.INIT.check(propName)) {
				initTableModel(evt);
			} else if (BookController.TagLinkProps.UPDATE.check(propName)) {
				updateEntity(evt);
			} else if (BookController.TagLinkProps.NEW.check(propName)) {
				newEntity(evt);
			} else if (BookController.TagLinkProps.DELETE.check(propName)) {
				deleteEntity(evt);
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void sendSetEntityToEdit(int row) {
		if (row == -1) {
			return;
		}
		TagLink tagLink = (TagLink) getEntityFromRow(row);
		ctrl.setTagLinkToEdit(tagLink);
		mainFrame.showView(ViewName.EDITOR);
	}

	@Override
	protected void sendSetNewEntityToEdit(AbstractEntity entity) {
		ctrl.setTagLinkToEdit((TagLink) entity);
		mainFrame.showView(ViewName.EDITOR);
	}

	@Override
	protected synchronized void sendDeleteEntity(int row) {
		TagLink tagLink = (TagLink) getEntityFromRow(row);
		ctrl.deleteTagLink(tagLink);
	}

	@Override
	protected synchronized void sendDeleteEntities(int[] rows) {
		ArrayList<Long> ids = new ArrayList<Long>();
		for (int row : rows) {
			TagLink tagLink = (TagLink) getEntityFromRow(row);
			ids.add(tagLink.getId());
		}
		ctrl.deleteMultiTagLinks(ids);
	}

	@Override
	protected AbstractEntity getEntity(Long id) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		TagLinkDAOImpl dao = new TagLinkDAOImpl(session);
		TagLink tagLink = dao.find(id);
		model.commit();
		return tagLink;
	}

	@Override
	protected AbstractEntity getNewEntity() {
		return new TagLink();
	}
}
