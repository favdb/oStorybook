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

package storybook.ui.table.column;

import javax.swing.table.TableModel;

import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import storybook.SbApp;
import storybook.toolkit.I18N;

/**
 * @author martin
 *
 */
@Deprecated
public class CustomColumnFactory extends ColumnFactory {

	// @Override
	public void configureTableColumn(TableModel model, TableColumnExt columnExt) {

		super.configureTableColumn(model, columnExt);

		// String title = columnExt.getTitle();
		// title = title.substring(0, 1).toUpperCase()
		// + title.substring(1).toLowerCase();
		// columnExt.setTitle(title);

//		model.getColumnClass(1);
		Object id=columnExt.getClientProperty("ID");
		SbApp.trace("CustomColumnFactory.configureTableColumn(): id:"+id);
		Highlighter highlighter = null;
		Object extid = columnExt.getIdentifier();
		String idStr = extid.toString();

		if (idStr.equals(I18N.getMsg("msg.dlg.mng.strands.color"))) {
			highlighter = new ColorHighlighter();
		} else if (idStr.equals(I18N.getMsg("msg.dlg.mng.persons.birthday"))
				|| idStr.equals(I18N.getMsg("msg.dlg.person.death"))
				|| idStr.equals(I18N.getMsg("msg.dlg.scene.date"))) {
			highlighter = new DateHighlighter();
		} else if (idStr.equals(I18N.getMsg("msg.idea.table.status"))) {
			highlighter = new IdeaStatusHighlighter();
		} else if (idStr.equals(I18N.getMsg("msg.status"))) {
			highlighter = new SceneStatusHighlighter();
		}

		if (highlighter != null) {
			columnExt.setHighlighters(highlighter);
		}
	}
}
