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

package storybook.toolkit.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.border.Border;

public class DotBorder implements Border {

	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(3, 3, 6, 6);
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.gray);
		int w = 1;
//		float[] dash = { 1, 3 };
		float[] dash = { 1, 2 };
		float dash_phase = 1;
		g2.setStroke(new BasicStroke(w, BasicStroke.CAP_SQUARE,
				BasicStroke.JOIN_MITER, 10, dash, dash_phase));
		g2.drawRect(x, y, width - 2, height - 2);
	}
}
