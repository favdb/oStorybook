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
package storybook.ui.panel.manage.dnd;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import storybook.SbApp;

import storybook.model.hbn.entity.Scene;
import storybook.ui.MainFrame;

@SuppressWarnings("serial")
public class DTScenePanel extends ScenePanel implements MouseMotionListener {

	private boolean trace = false;
	private MouseEvent firstMouseEvent = null;
	private int previousNumber = 0;

	public DTScenePanel(MainFrame mainFrame, Scene scene) {
		this(mainFrame, scene, TYPE_NONE);
		SbApp.trace("DTScenePanel_scene(" + mainFrame.getName() + "," + scene.getFullTitle() + ")");
	}

	public DTScenePanel(MainFrame mainFrame, int type) {
		this(mainFrame, null, type);
		SbApp.trace("DTScenePanel_type(" + mainFrame.getName() + "," + type + ")");
	}

	public DTScenePanel(MainFrame mainFrame, Scene scene, int type) {
		super(mainFrame, scene, type);
		SbApp.trace("DTScenePanel_full(" + mainFrame.getName() + ","
				+ ((scene!=null)?scene.getFullTitle():"null") + "," + type + ")");
		addMouseMotionListener(this);
		setAutoscrolls(true);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		firstMouseEvent = null;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (getScene() == null) {
			return;
		}

		if (firstMouseEvent != null) {
			e.consume();

			// if they are holding down the control key, COPY rather than MOVE
			// int ctrlMask = InputEvent.CTRL_DOWN_MASK;
			// int action = ((e.getModifiersEx() & ctrlMask) == ctrlMask) ?
			// TransferHandler.COPY
			// : TransferHandler.MOVE;
			int action = TransferHandler.MOVE;

			int dx = Math.abs(e.getX() - firstMouseEvent.getX());
			int dy = Math.abs(e.getY() - firstMouseEvent.getY());
			// arbitrarily define a 5-pixel shift as the
			// official beginning of a drag
			if (dx > 5 || dy > 5) {
				// this is a drag, not a click
				JComponent comp = (JComponent) e.getSource();
				TransferHandler handler = comp.getTransferHandler();
				// tell the transfer handler to initiate the drag
				handler.exportAsDrag(comp, firstMouseEvent, action);
				firstMouseEvent = null;
			}
			return;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (getScene() == null) {
			return;
		}
		firstMouseEvent = e;
		e.consume();
	}

	public int getPreviousNumber() {
		return previousNumber;
	}

	public void setPreviousNumber(int previousNumber) {
		this.previousNumber = previousNumber;
	}
}
