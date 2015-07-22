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

package storybook.model.state;

import javax.swing.Icon;

/**
 * @author martin
 *
 */
public class AbstractState {

	protected Integer number;
	protected String name;
	protected Icon icon;

	public AbstractState() {
		super();
	}

	public Integer getNumber() {
		return number;
	}

	public String getName() {
		return name;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getToolTip() {
		return toString();
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		// if (!super.equals(obj)) {
		// return false;
		// }
		if (!(obj instanceof AbstractState)) {
			return false;
		}
		AbstractState test = (AbstractState) obj;
		boolean ret = true;
		ret = ret && number.equals(test.number);
		return ret;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + number.hashCode();
		return hash;
	}
}
