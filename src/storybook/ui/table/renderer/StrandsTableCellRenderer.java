/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008 - 2011 Martin Mustun

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

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import storybook.SbConstants.ClientPropertyName;
import storybook.model.BookModel;
import storybook.model.hbn.entity.Strand;
import storybook.ui.MainFrame;

@SuppressWarnings("serial")
public class StrandsTableCellRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		MainFrame mainFrame = (MainFrame) table
				.getClientProperty(ClientPropertyName.MAIN_FRAME.toString());
		JLabel lbText = (JLabel) super.getTableCellRendererComponent(table,
				null, isSelected, hasFocus, row, column);
		if (value instanceof String) {
			return lbText;
		}
		List<String> abbrList = new ArrayList<String>();
		try {
			@SuppressWarnings("unchecked")
			List<Strand> list = (List<Strand>) value;
			for (Strand strand : list) {
				abbrList.add(strand.getAbbr());
			}
		} catch (LazyInitializationException lie) {
			BookModel model = mainFrame.getBookModel();
			Session session = model.beginTransaction();
			@SuppressWarnings("unchecked")
			List<Strand> list = (List<Strand>) value;
			for (Strand strand : list) {
				session.refresh(strand);
				abbrList.add(strand.getAbbr());
			}
			model.commit();
		} catch (Exception e) {
			e.printStackTrace();
			return lbText;
		}
		lbText.setText(" " + StringUtils.join(abbrList, ", "));
		return lbText;
	}
}
