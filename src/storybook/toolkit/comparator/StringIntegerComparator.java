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

package storybook.toolkit.comparator;

import java.util.Comparator;

/**
 * @author martin
 *
 */
public class StringIntegerComparator implements Comparator<String> {

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return 0;
		}
		if (s1 == null && s2 != null) {
			return -1;
		}
		if (s1 != null && s2 == null) {
			return 1;
		}
		if (s1.isEmpty() || s2.isEmpty()) {
			return s1.compareTo(s2);
		}
		try {
			Integer i1 = Integer.parseInt(s1);
			Integer i2 = Integer.parseInt(s2);
			return i1.compareTo(i2);
		} catch (NumberFormatException e) {
			return s1.compareTo(s2);
		}
	}
}
