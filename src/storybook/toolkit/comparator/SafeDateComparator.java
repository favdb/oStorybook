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
import java.util.Date;

/**
 * @author martin
 *
 */
public class SafeDateComparator implements Comparator<Object> {

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object o1, Object o2) {
		if (o1 == null && o2 == null) {
			return 0;
		}
		if (o1 instanceof Date && !(o2 instanceof Date)) {
			return 1;
		}
		if (o2 instanceof Date && !(o1 instanceof Date)) {
			return -1;
		}
		if (o1 instanceof Date && o2 instanceof Date) {
			DateComparator comp = new DateComparator();
			return comp.compare((Date) o1, (Date) o2);
		}
		if (o1 instanceof String && o2 instanceof String) {
			return ((String) o1).compareTo((String) o2);
		}
		return 0;
	}
}
