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

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

public abstract class AbstractInputVerifier extends InputVerifier {

	public enum ErrorState {
		OK, WARNING, ERROR
	}

	private String errorText;
	private ErrorState errorState = ErrorState.ERROR;
	private final boolean acceptEmpty;
	private boolean checkOnlyOnNewEntities;

	public AbstractInputVerifier() {
		this(false);
	}

	public AbstractInputVerifier(boolean acceptEmpty) {
		this.acceptEmpty = acceptEmpty;
	}

	public boolean isMandatory() {
		return !acceptEmpty;
	}

	public boolean isNumber() {
		return false;
	}

	@Override
	public boolean verify(JComponent comp) {
		if (comp instanceof JTextComponent) {
			if (((JTextComponent) comp).getText().trim().isEmpty() && acceptEmpty) {
				setErrorState(ErrorState.OK);
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean shouldYieldFocus(JComponent input) {
		return true;
	}

	public String getErrorText() {
		return errorText;
	}

	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}

	public boolean isCheckOnlyOnNewEntities() {
		return checkOnlyOnNewEntities;
	}

	public void setCheckOnlyOnNewEntities(boolean checkOnlyOnNewEntities) {
		this.checkOnlyOnNewEntities = checkOnlyOnNewEntities;
	}

	public ErrorState getErrorState() {
		return errorState;
	}

	public void setErrorState(ErrorState errorState) {
		this.errorState = errorState;
	}
}
