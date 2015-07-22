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

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import storybook.toolkit.I18N;

public class NotEmptyVerifier extends AbstractInputVerifier {

	public NotEmptyVerifier() {
		super();
	}

	public NotEmptyVerifier(boolean acceptEmpty) {
		super(false);
	}

	@Override
	public boolean verify(JComponent comp) {
		if (comp instanceof JTextComponent) {
			JTextComponent tc = (JTextComponent) comp;
			if (!tc.getText().trim().isEmpty()) {
				return true;
			}
			setErrorText(I18N.getMsg("msg.verifier.nonempty"));
			return false;
		}
		return false;
	}
}
