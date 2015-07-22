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
import javax.swing.JTextField;

import storybook.toolkit.I18N;

public class IntegerVerifier extends AbstractInputVerifier {

	private boolean onlyPositive;

	public IntegerVerifier() {
		this(false);
	}

	public IntegerVerifier(boolean onlyPositive) {
		super(false);
		this.onlyPositive = onlyPositive;
	}

	public IntegerVerifier(boolean onlyPositiveNumbers, boolean acceptEmty) {
		super(acceptEmty);
		this.onlyPositive = onlyPositiveNumbers;
	}

	@Override
	public boolean isNumber() {
		return true;
	}

	@Override
	public boolean verify(JComponent comp) {
		if (super.verify(comp)) {
			return true;
		}
		if (comp instanceof JTextField) {
			JTextField tf = (JTextField) comp;
			try {
				int i = Integer.parseInt(tf.getText());
				if (onlyPositive) {
					if (i < 0) {
						throw new NumberFormatException(
								I18N.getMsg("msg.verifier.integer.positive"));
					}
				}
				return true;
			} catch (NumberFormatException e) {
				setErrorText(I18N.getMsg("msg.verifier.wrong.format") + " "
						+ e.getLocalizedMessage());
			}
		}
		return false;
	}
}
