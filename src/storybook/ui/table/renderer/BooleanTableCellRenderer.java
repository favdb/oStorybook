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

package storybook.ui.table.renderer;

import javax.swing.table.DefaultTableCellRenderer;

import storybook.toolkit.I18N;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class BooleanTableCellRenderer extends DefaultTableCellRenderer {

	public BooleanTableCellRenderer() {
		super();
	}

	@Override
	public void setValue(Object value) {
		try {
			if (value != null && value instanceof String) {
				setText(value.equals("true") ? I18N.getMsg("msg.common.yes")
						: I18N.getMsg("msg.common.no"));
			} else {
				setText("");
			}
		} catch (Exception e) {
			setText("");
		}
	}
}
