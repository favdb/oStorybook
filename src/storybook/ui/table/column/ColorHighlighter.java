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
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Highlighter;

/**
 * @author martin
 *
 */
@Deprecated
public class ColorHighlighter implements Highlighter {
	@Override
	public void addChangeListener(ChangeListener arg0) {
	}

	@Override
	public ChangeListener[] getChangeListeners() {
		return null;
	}

	@Override
	public Component highlight(Component comp, ComponentAdapter compAdapter) {
		Color bg = comp.getBackground();
		JPanel panel = new JPanel(new MigLayout("ins 3"));
		panel.setOpaque(true);
		panel.setBackground(bg);
		try {
			Object val = compAdapter.getValue();
			Integer intVal = Integer.parseInt(val.toString());
			Color clr = new Color(intVal);
			JLabel lb = new JLabel();
			lb.setPreferredSize(new Dimension(24, 16));
			lb.setOpaque(true);
			lb.setBackground(clr);
			panel.add(lb);
			return panel;
		} catch (NumberFormatException e) {
			return panel;
		}
	}

	@Override
	public void removeChangeListener(ChangeListener arg0) {
	}
}
