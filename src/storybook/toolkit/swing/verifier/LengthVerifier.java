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

package storybook.toolkit.swing.verifier;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import storybook.toolkit.I18N;
import storybook.toolkit.swing.htmleditor.HtmlEditor;
import storybook.toolkit.swing.panel.PlainTextEditor;

public class LengthVerifier extends AbstractInputVerifier {

	private int length;

	public LengthVerifier(int length) {
		super(true);
		this.length = length;
	}

	public LengthVerifier(int length, boolean acceptEmpty) {
		super(acceptEmpty);
		this.length = length;
	}

	@Override
	public boolean verify(JComponent comp) {
		String errorMsg = I18N.getMsg("msg.verifier.too.long", length);
		if (comp instanceof JTextComponent) {
			JTextComponent tc = (JTextComponent) comp;
			if (tc.getText().length() < length) {
				return true;
			}
			setErrorText(errorMsg);
			return false;
		}
		if (comp instanceof JComboBox) {
			JComboBox combo = (JComboBox) comp;
			Object item = combo.getSelectedItem();
			if (item == null
					|| (item != null && item.toString().length() < length)) {
				return true;
			}
			setErrorText(errorMsg);
			return false;
		}
		if (comp instanceof HtmlEditor) {
			HtmlEditor editor = (HtmlEditor) comp;
			if (editor.getText().length() < length) {
				return true;
			}
			setErrorText(errorMsg);
			return false;
		}
		if (comp instanceof PlainTextEditor) {
			PlainTextEditor editor = (PlainTextEditor) comp;
			if (editor.getText().length() < length) {
				return true;
			}
			setErrorText(errorMsg);
			return false;
		}
		return true;
	}
}
