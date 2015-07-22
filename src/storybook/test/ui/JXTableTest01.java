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

package storybook.test.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class JXTableTest01 extends JFrame implements ActionListener {

	private static JXTableTest01 instance;

	private JXTable table;
	private DefaultTableModel model;
	private JTextField tfFilter;
	private JButton btAdd;

	private SbRowFilter rowFilter;

	private void init() {
		initUi();
	}

	private void initUi() {
		try {
			setLayout(new MigLayout("fill,flowy"));
			setTitle("JXTableTest01");
			setPreferredSize(new Dimension(600, 400));
			setLocation(400, 200);
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			tfFilter = new JTextField(20);
			tfFilter.addActionListener(this);

			btAdd = new JButton("Add");
			btAdd.addActionListener(this);

			Vector<Vector<String>> data = new Vector<Vector<String>>();
			for (int i = 0; i < 2; ++i) {
				Vector<String> row = new Vector<String>();
				row.add("name " + i + " 01");
				row.add("city " + i + " 02");
				data.add(row);
			}
			Vector<String> columnNames = new Vector<String>();
			columnNames.add("col 1");
			columnNames.add("col 2");
			model = new DefaultTableModel(data, columnNames);
			table = new JXTable(model);

			table.setColumnControlVisible(true);
			table.setShowGrid(false, false);
			table.addHighlighter(HighlighterFactory.createSimpleStriping());
			table.setVisibleRowCount(10);

			rowFilter = new SbRowFilter(table);

			add(tfFilter);
			add(btAdd);
			JScrollPane scroller = new JScrollPane(table);
			add(scroller, "grow");

			pack();
			setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static JXTableTest01 getInstance() {
		if (instance == null) {
			instance = new JXTableTest01();
		}
		return instance;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JXTableTest01.getInstance().init();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JTextField) {
			String text = tfFilter.getText();
			System.out.println("JXTableTest01.actionPerformed(): text:" + text);
			rowFilter.setText(text);
			table.setRowFilter(rowFilter);
			return;
		}
		if (e.getSource() instanceof JButton) {
			System.out.println("JXTableTest01.actionPerformed(): add");
			Vector<String> v = new Vector<String>();
			v.add("smith");
			v.add("new york city");
			model.addRow(v);
		}
	}
}

class SbRowFilter extends RowFilter<TableModel, Integer> {
	private String text;
	private JXTable table;

	public SbRowFilter(JXTable table) {
		text = "";
		this.table = table;
	}

	public void setText(String text) {
		this.text = (text == null ? "" : text);
	}

	@Override
	public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
		try {
			DefaultTableModel model = (DefaultTableModel) entry.getModel();
			// Person person = personModel.getPerson(entry.getIdentifier());
			int identifier = entry.getIdentifier();
			for (int c = 0; c < table.getColumnCount(); ++c) {
				TableColumn col = table.getColumn(c);
				int colMi = col.getModelIndex();
				String val = (String) model.getValueAt(identifier, colMi);
				System.out.println("SbRowFilter.include(): val:" + val);
				if (val.contains(text)) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
