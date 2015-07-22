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
import storybook.model.hbn.dao.CategoryDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Category;
import storybook.ui.MainFrame;
import storybook.ui.SbView;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class CategoryTable extends AbstractTable {

	public CategoryTable(MainFrame mainFrame) {
		super(mainFrame);
		hasOrder = true;
	}

	@Override
	public void init() {
		columns = SbColumnFactory.getInstance().getCategoryColumns();
	}

	@Override
	protected void modelPropertyChangeLocal(PropertyChangeEvent evt) {
		try {
			String propName = evt.getPropertyName();
			if (BookController.CategoryProps.INIT.check(propName)) {
				initTableModel(evt);
			} else if (BookController.CategoryProps.UPDATE.check(propName)) {
				updateEntity(evt);
			} else if (BookController.CategoryProps.NEW.check(propName)) {
				newEntity(evt);
			} else if (BookController.CategoryProps.DELETE.check(propName)) {
				deleteEntity(evt);
			} else if (BookController.CategoryProps.ORDER_UP
					.check(propName)) {
				orderUpEntity(evt);
			} else if (BookController.CategoryProps.ORDER_DOWN
					.check(propName)) {
				orderDownEntity(evt);
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void sendOrderUpEntity(int row) {
		if (row == -1) {
			return;
		}
		Category category = (Category) getEntityFromRow(row);
		ctrl.orderUpCategory(category);
	}

	@Override
	protected void sendOrderDownEntity(int row) {
		if (row == -1) {
			return;
		}
		Category category = (Category) getEntityFromRow(row);
		ctrl.orderDownCategory(category);
	}

	@Override
	protected void orderUpEntity(PropertyChangeEvent evt) {
		AbstractEntity entity = (AbstractEntity) evt.getNewValue();
		Category category = (Category)entity;

		BookModel model = mainFrame.getBookModel();

		Session session = model.beginTransaction();
		CategoryDAOImpl dao = new CategoryDAOImpl(session);
		dao.orderCategories();
		model.commit();

		session = model.beginTransaction();
		dao = new CategoryDAOImpl(session);
		dao.orderUpCatgory(category);
		model.commit();

		SbView view = mainFrame.getView(ViewName.CATEGORIES);
		mainFrame.getBookController().refresh(view);

		sortByColumn(2);
	}

	@Override
	protected void orderDownEntity(PropertyChangeEvent evt) {
		AbstractEntity entity = (AbstractEntity) evt.getNewValue();
		Category category = (Category)entity;

		BookModel model = mainFrame.getBookModel();

		Session session = model.beginTransaction();
		CategoryDAOImpl dao = new CategoryDAOImpl(session);
		dao.orderCategories();
		model.commit();

		session = model.beginTransaction();
		dao = new CategoryDAOImpl(session);
		dao.orderDownCategory(category);
		model.commit();

		SbView view = mainFrame.getView(ViewName.CATEGORIES);
		mainFrame.getBookController().refresh(view);

		sortByColumn(2);
	}

	@Override
	protected void sendSetEntityToEdit(int row) {
		if (row == -1) {
			return;
		}
		Category category = (Category) getEntityFromRow(row);
		ctrl.setCategoryToEdit(category);
		mainFrame.showView(ViewName.EDITOR);
	}

	@Override
	protected void sendSetNewEntityToEdit(AbstractEntity entity) {
		ctrl.setCategoryToEdit((Category) entity);
		mainFrame.showView(ViewName.EDITOR);
	}

	@Override
	protected synchronized void sendDeleteEntity(int row) {
		Category category = (Category) getEntityFromRow(row);
		ctrl.deleteCategory(category);
	}

	@Override
	protected synchronized void sendDeleteEntities(int[] rows) {
		ArrayList<Long> ids = new ArrayList<Long>();
		for (int row : rows) {
			Category category = (Category) getEntityFromRow(row);
			ids.add(category.getId());
		}
		ctrl.deleteMultiCategories(ids);
	}

	@Override
	protected AbstractEntity getEntity(Long id) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		CategoryDAOImpl dao = new CategoryDAOImpl(session);
		Category category = dao.find(id);
		model.commit();
		return category;
	}

	@Override
	protected AbstractEntity getNewEntity() {
		return new Category();
	}
}
