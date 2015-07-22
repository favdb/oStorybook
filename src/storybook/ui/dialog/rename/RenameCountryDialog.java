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

package storybook.ui.dialog.rename;

import java.util.List;

import org.hibernate.Session;
import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.entity.Location;
import storybook.toolkit.I18N;
import storybook.ui.MainFrame;

@SuppressWarnings("serial")
public class RenameCountryDialog extends AbstractRenameDialog {

	public RenameCountryDialog(MainFrame mainFrame) {
		super(mainFrame);
	}

	@Override
	protected List<String> getList() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		LocationDAOImpl dao = new LocationDAOImpl(session);
		List<String> ret = dao.findCountries();
		model.commit();
		return ret;
	}

	@Override
	protected void rename(String oldValue, String newValue) {
		BookModel model = mainFrame.getBookModel();
		BookController ctrl = mainFrame.getBookController();
		Session session = model.beginTransaction();
		LocationDAOImpl dao = new LocationDAOImpl(session);
		List<Location> locations = dao.findByCountry(oldValue);
		model.commit();
		for (Location location : locations) {
			location.setCountry(newValue);
			ctrl.updateLocation(location);
		}
	}

	@Override
	protected String getDlgTitle() {
		return I18N.getMsg("msg.location.rename.country");
	}
}
