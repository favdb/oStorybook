/*
Storybook: Scene-based software for novelists and authors.
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

package storybook.toolkit.swing.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import storybook.toolkit.swing.ColorUtil;
import storybook.toolkit.swing.SwingUtil;

public class StandardTableCellRenderer implements TableCellRenderer {

	private boolean hasColoredRows;

	public StandardTableCellRenderer() {
		this(true);
	}

	public StandardTableCellRenderer(boolean coloredRows) {
		this.hasColoredRows = coloredRows;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel(value == null ? "" : value.toString());
		Color newColor;
		Color color = SwingUtil.getTableBackgroundColor();
		if (hasColoredRows && row % 2 == 1) {
			color = SwingUtil.getTableBackgroundColor(true);
		}
		if (isSelected) {
			Color selected = UIManager.getColor("Table.selectionBackground");
			newColor = ColorUtil.blend(color, selected, 0.30);
		} else {
			newColor = color;
		}
		label.setOpaque(true);
		label.setBackground(newColor);
		return label;
	}
}
