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

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

@SuppressWarnings("serial")
public class SbUndoManager extends UndoManager implements
		UndoableEditListener {

	public SbCompoundEdit compoundEdit = null;
	private JTextComponent textComponent;
	private boolean groupEnd = false;

	public SbUndoManager(JTextComponent editor) {
		this.textComponent = editor;
		editor.getDocument().addUndoableEditListener(this);
	}

	@Override
	public void undoableEditHappened(UndoableEditEvent evt) {
		if (compoundEdit == null) {
			compoundEdit = createCompoundEdit(evt.getEdit());
			return;
		}

		// group ended?
		if (!groupEnd) {
			compoundEdit.addEdit(evt.getEdit());
			return;
		}

		// new group
		groupEnd = false;
		compoundEdit.end();
		compoundEdit = createCompoundEdit(evt.getEdit());
	}

	private SbCompoundEdit createCompoundEdit(UndoableEdit edit) {
		SbCompoundEdit ce = new SbCompoundEdit();
		ce.addEdit(edit);
		addEdit(ce);
		return ce;
	}

	@Override
	public synchronized void discardAllEdits() {
		super.discardAllEdits();
		groupEnd = false;
		compoundEdit = null;
	}

	@Override
	public synchronized boolean canRedo() {
		if (compoundEdit != null) {
			return true;
		}
		return super.canRedo();
	}

	public void endGroup() {
		groupEnd = true;
	}

	public JTextComponent getEditor() {
		return textComponent;
	}

	private class SbCompoundEdit extends CompoundEdit {
		private static final long serialVersionUID = -7132641862624605455L;

		public boolean isInProgress() {
			return false;
		}

		public void undo() throws CannotUndoException {
			if (compoundEdit != null) {
				compoundEdit.end();
			}
			super.undo();
			compoundEdit = null;
		}
	}
}
