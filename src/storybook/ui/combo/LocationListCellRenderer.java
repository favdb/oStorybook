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

package storybook.ui.combo;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import storybook.model.hbn.entity.Location;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class LocationListCellRenderer extends DefaultListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		try {
			JLabel label = (JLabel) super.getListCellRendererComponent(list,
					value, index, isSelected, cellHasFocus);
			if (!(value instanceof Location)) {
				String str = value.toString();
				if (str.isEmpty()) {
					label.setText(" ");
					return label;
				}
				label.setText(value.toString());
				return label;
			}
			Location location = (Location) value;
			label.setIcon(location.getIcon());
			label.setText(location.getFullName(true));
			return label;
		} catch (Exception e) {
			return new JLabel("");
		}
	}
}
