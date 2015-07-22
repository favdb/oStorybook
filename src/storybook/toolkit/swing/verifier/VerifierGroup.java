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

import java.util.Vector;

import javax.swing.JComponent;

public class VerifierGroup extends AbstractInputVerifier {

	private Vector<AbstractInputVerifier> verifiers = new Vector<AbstractInputVerifier>();

	public VerifierGroup() {
	}

	public void addVerifier(AbstractInputVerifier verifier) {
		verifiers.add(verifier);
	}

	public Vector<AbstractInputVerifier> getVerifiers() {
		return verifiers;
	}

	public boolean isInteger() {
		for (AbstractInputVerifier verifier : verifiers) {
			if (verifier instanceof IntegerVerifier) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isNumber() {
		for (AbstractInputVerifier verifier : verifiers) {
			if (verifier.isNumber()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean verify(JComponent comp) {
		// check errors first
		for (AbstractInputVerifier verifier : verifiers) {
			if (verifier.verify(comp)) {
				continue;
			}
			if (verifier.getErrorState() != ErrorState.ERROR) {
				continue;
			}
			setErrorState(verifier.getErrorState());
			setErrorText(verifier.getErrorText());
			return false;
		}
		// check warnings
		for (AbstractInputVerifier verifier : verifiers) {
			if (verifier.verify(comp)) {
				continue;
			}
			if (verifier.getErrorState() != ErrorState.WARNING) {
				continue;
			}
			setErrorState(verifier.getErrorState());
			setErrorText(verifier.getErrorText());
			return false;
		}
		return true;
	}

	@Override
	public boolean isMandatory() {
		for (AbstractInputVerifier verifier : verifiers) {
			if (verifier.isMandatory()) {
				return true;
			}
		}
		return false;
	}
}
