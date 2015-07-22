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

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class FixedColumnScrollPane extends JScrollPane implements
		RowSorterListener {

	private JTable scrollableTable;
	private JTable fixedTable;

	public FixedColumnScrollPane(JTable table, int fixedColumns) {
		super(table);
		this.scrollableTable = table;

		// Use the table to create a new table sharing
		// the DataModel and ListSelectionModel
		fixedTable = new JTable(scrollableTable.getModel());
		fixedTable.setFocusable(false);
		fixedTable.setSelectionModel(scrollableTable.getSelectionModel());
		fixedTable.getTableHeader().setReorderingAllowed(false);
		// fixedTable.getTableHeader().setResizingAllowed( false );

		// create row sorter and add listener
		fixedTable.setAutoCreateRowSorter(true);
		scrollableTable.setAutoCreateRowSorter(true);
		fixedTable.getRowSorter().addRowSorterListener(this);
		scrollableTable.getRowSorter().addRowSorterListener(this);

		fixedTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		scrollableTable.putClientProperty("terminateEditOnFocusLost",
				Boolean.TRUE);

		// Remove the fixed columns from the main table
		for (int i = 0; i < fixedColumns; i++) {
			TableColumnModel columnModel = scrollableTable.getColumnModel();
			columnModel.removeColumn(columnModel.getColumn(0));
		}

		// Remove the non-fixed columns from the fixed table
		while (fixedTable.getColumnCount() > fixedColumns) {
			TableColumnModel columnModel = fixedTable.getColumnModel();
			columnModel.removeColumn(columnModel.getColumn(fixedColumns));
		}

		// set header table cell renderer
		for (int c = 0; c < fixedTable.getColumnModel().getColumnCount(); ++c) {
			TableColumn column = fixedTable.getColumnModel().getColumn(c);
			column.setCellRenderer(new HeaderTableCellRenderer());
		}

		// Add the fixed table to the scroll pane
		fixedTable.setPreferredScrollableViewportSize(fixedTable
				.getPreferredSize());
		setRowHeaderView(fixedTable);
		setCorner(JScrollPane.UPPER_LEFT_CORNER, fixedTable.getTableHeader());
	}

	public JTable getFixedTable() {
		return fixedTable;
	}

	public JTable getScrollableTable() {
		return scrollableTable;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sorterChanged(RowSorterEvent e) {
		RowSorter<TableModel> rowSorter = e.getSource();
		if (e.getSource() == scrollableTable.getRowSorter()) {
			fixedTable.setRowSorter(rowSorter);
		} else if (e.getSource() == fixedTable.getRowSorter()) {
			scrollableTable.setRowSorter(rowSorter);
		}
	}
}
