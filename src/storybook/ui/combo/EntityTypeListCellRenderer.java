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

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jdesktop.swingx.icon.EmptyIcon;
import storybook.ui.memoria.EntityTypeCbItem;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class EntityTypeListCellRenderer extends JLabel implements
		ListCellRenderer {

	public EntityTypeListCellRenderer() {
		setOpaque(true);
		setVerticalAlignment(CENTER);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		String text = "";
		Icon icon = new EmptyIcon();
		if (value instanceof EntityTypeCbItem) {
			EntityTypeCbItem etItem = (EntityTypeCbItem) value;
			text = etItem.getText();
			icon = etItem.getIcon();
		}
		setText(text);
		setIcon(icon);
		return this;
	}
}
