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

package storybook.toolkit.swing;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.dnd.Autoscroll;

import javax.swing.JViewport;
import javax.swing.SwingUtilities;

/**
 * @author martin
 *
 */
public class AutoscrollSupport implements Autoscroll {
	// component for which autoscroll to be enabled
	Component comp;

	// The insets where autoscrolling is active */
	Insets insets;

	// no of units to be scrolled in each direction
	Insets scrollUnits;

	public AutoscrollSupport(Component comp, Insets insets) {
		this(comp, insets, insets);
	}

	public AutoscrollSupport(Component comp, Insets insets, Insets scrollUnits) {
		this.comp = comp;
		this.insets = insets;
		this.scrollUnits = scrollUnits;
	}

	public void autoscroll(Point cursorLoc) {
		JViewport viewport = getViewport();
		if (viewport == null)
			return;
		Point viewPos = viewport.getViewPosition();
		int viewHeight = viewport.getExtentSize().height;
		int viewWidth = viewport.getExtentSize().width;

		// perform scrolling
		if ((cursorLoc.y - viewPos.y) < insets.top) { // scroll up
			viewport.setViewPosition(new Point(viewPos.x, Math.max(viewPos.y
					- scrollUnits.top, 0)));
		} else if ((viewPos.y + viewHeight - cursorLoc.y) < insets.bottom) { // scroll
																				// down
			viewport.setViewPosition(new Point(viewPos.x, Math.min(viewPos.y
					+ scrollUnits.bottom, comp.getHeight() - viewHeight)));
		} else if ((cursorLoc.x - viewPos.x) < insets.left) { // scroll left
			viewport.setViewPosition(new Point(Math.max(viewPos.x
					- scrollUnits.left, 0), viewPos.y));
		} else if ((viewPos.x + viewWidth - cursorLoc.x) < insets.right) { // scroll
																			// right
			viewport.setViewPosition(new Point(Math.min(viewPos.x
					+ scrollUnits.right, comp.getWidth() - viewWidth),
					viewPos.y));
		}
	}

	public Insets getAutoscrollInsets() {
		int height = comp.getHeight();
		int width = comp.getWidth();
		return new Insets(height, width, height, width);
	}

	JViewport getViewport() {
		return (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class,
				comp);
	}
}
