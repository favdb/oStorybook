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

import java.text.DateFormat;
import java.util.Date;

import javax.swing.table.DefaultTableCellRenderer;

import storybook.toolkit.DateUtil;
import storybook.toolkit.I18N;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class DateTableCellRenderer extends DefaultTableCellRenderer {

	public DateTableCellRenderer() {
		super();
	}

	@Override
	public void setValue(Object value) {
		try {
			if (value instanceof Date) {
				Date date = (Date) value;
				DateFormat formatter;
				if (DateUtil.isZeroTimeDate(date)) {
					formatter = I18N.getMediumDateFormatter();
				} else {
					formatter = I18N.getDateTimeFormatter();
				}
				setText(formatter.format(date));
			} else {
				setText("");
			}
		} catch (Exception e) {
			setText("");
		}
	}
}
