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

import java.awt.Color;
import java.awt.Component;

import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Highlighter;
import storybook.toolkit.swing.label.DateLabel;
import storybook.ui.combo.SceneStateComboModel;

/**
 * @author martin
 *
 */
@Deprecated
public class SceneStatusHighlighter implements Highlighter {

	@Override
	public void addChangeListener(ChangeListener arg0) {
	}

	@Override
	public ChangeListener[] getChangeListeners() {
		return null;
	}

	@Override
	public Component highlight(Component comp, ComponentAdapter compAdapter) {
		DateLabel lb = new DateLabel();
		Color bg = comp.getBackground();
		lb.setBackground(bg);
		Integer val = Integer.parseInt(compAdapter.getValue().toString());
		SceneStateComboModel model = new SceneStateComboModel();
//		SceneStatus st = model.findByNumber(val);
//		lb.setText(st.getName());
		return lb;
	}

	@Override
	public void removeChangeListener(ChangeListener arg0) {
	}

}
