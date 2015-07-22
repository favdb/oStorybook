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

import java.awt.Color;

import javax.swing.ListCellRenderer;

import org.hibernate.Session;
import storybook.model.BookModel;
import storybook.model.hbn.dao.StrandDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Strand;
import storybook.ui.MainFrame;
import storybook.ui.table.SbColumnFactory;
import storybook.ui.combo.StrandListCellRenderer;

/**
 * @author martin
 *
 */
public class StrandEntityHandler extends AbstractEntityHandler {

	public StrandEntityHandler(MainFrame mainFrame) {
		super(mainFrame, SbColumnFactory.getInstance().getStrandColumns());
	}

	@Override
	public AbstractEntity createNewEntity() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		StrandDAOImpl dao = new StrandDAOImpl(session);
		Integer nextSort = dao.getNextSort();
		model.commit();

		Strand strand = new Strand();
		strand.setSort(nextSort);
		strand.setJColor(Color.lightGray);
		return strand;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Class<T> getDAOClass() {
		return (Class<T>) StrandDAOImpl.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Class<T> getEntityClass() {
		return (Class<T>) Strand.class;
	}

	@Override
	public ListCellRenderer getListCellRenderer() {
		return new StrandListCellRenderer();
	}
}
