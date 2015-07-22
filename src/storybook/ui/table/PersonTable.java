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
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Category;
import storybook.model.hbn.entity.Gender;
import storybook.model.hbn.entity.Person;
import storybook.ui.MainFrame;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class PersonTable extends AbstractTable {

	public PersonTable(MainFrame mainFrame) {
		super(mainFrame);
	}

	@Override
	public void init() {
		columns = SbColumnFactory.getInstance().getPersonColumns();
	}

	@Override
	protected void modelPropertyChangeLocal(PropertyChangeEvent evt) {
		try {
			String propName = evt.getPropertyName();
			if (BookController.PersonProps.INIT.check(propName)) {
				initTableModel(evt);
			} else if (BookController.PersonProps.UPDATE.check(propName)) {
				updateEntity(evt);
			} else if (BookController.PersonProps.NEW.check(propName)) {
				newEntity(evt);
			} else if (BookController.PersonProps.DELETE.check(propName)) {
				deleteEntity(evt);
			} else if (BookController.GenderProps.UPDATE.check(propName)) {
				updateGenders(evt);
			} else if (BookController.CategoryProps.UPDATE.check(propName)) {
				updateCategories(evt);
			}

		} catch (Exception e) {
		}
	}

	private void updateCategories(PropertyChangeEvent evt) {
		Category oldCategory = (Category) evt.getOldValue();
		Category newCategory = (Category) evt.getNewValue();
		for (int row = 0; row < tableModel.getRowCount(); ++row) {
			if (oldCategory.equals(newCategory)) {
				tableModel.setValueAt(newCategory, row, 1);
			}
		}
	}

	private void updateGenders(PropertyChangeEvent evt) {
		Gender oldGender = (Gender) evt.getOldValue();
		Gender newGender = (Gender) evt.getNewValue();
		for (int row = 0; row < tableModel.getRowCount(); ++row) {
			if (oldGender.equals(newGender)) {
				tableModel.setValueAt(newGender, row, 1);
			}
		}
	}

	@Override
	protected void sendSetEntityToEdit(int row) {
		if (row == -1) {
			return;
		}
		Person person = (Person) getEntityFromRow(row);
		ctrl.setPersonToEdit(person);
		mainFrame.showView(ViewName.EDITOR);
	}

	@Override
	protected void sendSetNewEntityToEdit(AbstractEntity entity) {
		ctrl.setPersonToEdit((Person) entity);
		mainFrame.showView(ViewName.EDITOR);
	}

	@Override
	protected synchronized void sendDeleteEntity(int row) {
		Person person = (Person) getEntityFromRow(row);
		ctrl.deletePerson(person);
	}

	@Override
	protected synchronized void sendDeleteEntities(int[] rows) {
		ArrayList<Long> ids = new ArrayList<Long>();
		for (int row : rows) {
			Person person = (Person) getEntityFromRow(row);
			ids.add(person.getId());
		}
		ctrl.deleteMultiPersons(ids);
	}

	@Override
	protected AbstractEntity getEntity(Long id) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PersonDAOImpl dao = new PersonDAOImpl(session);
		Person person = dao.find(id);
		model.commit();
		return person;
	}

	@Override
	protected AbstractEntity getNewEntity() {
		return new Person();
	}
}
