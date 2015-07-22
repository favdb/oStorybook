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

package storybook.toolkit.swing.undo;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;

@SuppressWarnings("serial")
public class UndoableTextField extends JTextField implements
		UndoableComponent, KeyListener, DocumentListener {

	private static final int LIMIT = 100;

	private SbUndoManager undo;
	private AbstractAction undoAction;
	private AbstractAction redoAction;

	public UndoableTextField() {
		super();
		addKeyListener(this);
		undo = new SbUndoManager(this);
		undo.setLimit(LIMIT);
		undoAction = new UndoAction(undo);
		redoAction = new RedoAction(undo);
		// add key strokes
		InputMap inputMap = getInputMap();
		inputMap.put(KeyStroke.getKeyStroke("control Z"), undoAction);
		inputMap.put(KeyStroke.getKeyStroke("control Y"), redoAction);
	}

	public UndoManager getUndoManager() {
		return undo;
	}

	public AbstractAction getUndoAction() {
		return undoAction;
	}

	public AbstractAction getRedoAction() {
		return redoAction;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int ch = e.getKeyCode();
		if (ch == KeyEvent.VK_SPACE || ch == KeyEvent.VK_ENTER) {
			undo.endGroup();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		undo.endGroup();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
	}
}
