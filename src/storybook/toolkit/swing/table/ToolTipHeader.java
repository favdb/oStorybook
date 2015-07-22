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

import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

@SuppressWarnings("serial")
public class ToolTipHeader extends JTableHeader {
	String[] toolTips;

	public ToolTipHeader(TableColumnModel model) {
		super(model);
	}

	public String getToolTipText(MouseEvent evt) {
		try {
			int col = columnAtPoint(evt.getPoint());
			int modelCol = getTable().convertColumnIndexToModel(col);
			String retStr = toolTips[modelCol];
			if (retStr == null || retStr.length() < 1) {
				return super.getToolTipText(evt);
			}
			return retStr;
		} catch (Exception e) {
			// ignore
		}
		return "";
	}

	public void setToolTipStrings(String[] toolTips) {
		this.toolTips = toolTips;
	}
}
