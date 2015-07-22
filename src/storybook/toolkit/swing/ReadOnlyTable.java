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

package storybook.toolkit.swing;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Provides a read-only table.
 *
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class ReadOnlyTable extends JTable {

	public ReadOnlyTable() {
		super();
	}

	public ReadOnlyTable(int numRows, int numColumns) {
		super(numRows, numColumns);
	}

	public ReadOnlyTable(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
	}

	public ReadOnlyTable(TableModel dm, TableColumnModel cm,
			ListSelectionModel sm) {
		super(dm, cm, sm);
	}

	public ReadOnlyTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
	}

	public ReadOnlyTable(TableModel dm) {
		super(dm);
	}

	public ReadOnlyTable(Vector<?> rowData, Vector<?> columnNames) {
		super(rowData, columnNames);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		// return super.isCellEditable(row, column);
		return false;
	}
}
