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

package storybook.toolkit;

import javax.swing.Action;
import javax.swing.JPopupMenu;

import storybook.SbConstants;
import storybook.model.stringcategory.AbstractStringCategory;
import storybook.model.stringcategory.CityCategory;
import storybook.model.stringcategory.CountryCategory;
import storybook.model.stringcategory.ItemCategory;
import storybook.model.stringcategory.TagCategory;
import storybook.ui.MainFrame;

import com.sun.jaf.ui.ActionManager;

/**
 * @author martin
 *
 */
public class StringCategoryUtil {

	public static JPopupMenu createPopupMenu(MainFrame mainFrame,
			AbstractStringCategory cat) {
		JPopupMenu menu = new JPopupMenu();
		if (cat == null) {
			return null;
		}
		ActionManager actMngr = mainFrame.getSbActionManager()
				.getActionManager();
		Action act = null;
		if (cat instanceof TagCategory) {
			act = actMngr.getAction("rename-tag-category-command");
		} else if (cat instanceof ItemCategory) {
			act = actMngr.getAction("rename-item-category-command");
		} else if (cat instanceof CityCategory) {
			act = actMngr.getAction("rename-city-command");
		} else if (cat instanceof CountryCategory) {
			act = actMngr.getAction("rename-country-command");
		}
		if (act == null) {
			return null;
		}
		act.putValue(SbConstants.ActionKey.CATEGORY.toString(), cat.getName());
		menu.add(act);
		return menu;
	}
}
