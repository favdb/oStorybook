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

package storybook.toolkit.swing.htmleditor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLEditorKit;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class SbHTMLEditorKit extends HTMLEditorKit {

	private SbLinkController handler = new SbLinkController();

	public void install(JEditorPane c) {
		MouseListener[] oldMouseListeners = c.getMouseListeners();
		MouseMotionListener[] oldMouseMotionListeners = c
				.getMouseMotionListeners();
		super.install(c);
		// the following code removes link handler added by original
		// HTMLEditorKit

		for (MouseListener l : c.getMouseListeners()) {
			c.removeMouseListener(l);
		}
		for (MouseListener l : oldMouseListeners) {
			c.addMouseListener(l);
		}

		for (MouseMotionListener l : c.getMouseMotionListeners()) {
			c.removeMouseMotionListener(l);
		}
		for (MouseMotionListener l : oldMouseMotionListeners) {
			c.addMouseMotionListener(l);
		}

		// add out link handler instead of removed one
		c.addMouseListener(handler);
		c.addMouseMotionListener(handler);
	}

	public class SbLinkController extends LinkController {

		public void mouseClicked(MouseEvent e) {
			JEditorPane editor = (JEditorPane) e.getSource();
			if (editor.isEditable() && SwingUtilities.isLeftMouseButton(e)) {
				if (e.getClickCount() == 2) {
					editor.setEditable(false);
					super.mouseClicked(e);
					editor.setEditable(true);
				}
			}
		}

		@SuppressWarnings("unused")
		public void mouseMoved(MouseEvent e) {
			JEditorPane editor = (JEditorPane) e.getSource();
			if (editor.isEditable()) {
				boolean isNeedCursorChange = false;
				editor.setEditable(false);
				isNeedCursorChange = true;
				super.mouseMoved(e);
				isNeedCursorChange = false;
				editor.setEditable(true);
				isNeedCursorChange = true;
			}
		}
	}
}
