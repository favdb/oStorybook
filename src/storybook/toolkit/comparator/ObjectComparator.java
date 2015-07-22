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
 * For testing propose only.
 */
public class ObjectComparator implements Comparator<Object> {

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object o1, Object o2) {
		System.out.println("ObjectComparator.compare(): o1:" + o1);
		System.out
				.println("ObjectComparator.compare(): o1 cl:" + o1.getClass());
		System.out.println("ObjectComparator.compare(): o2:" + o2);
		System.out
				.println("ObjectComparator.compare(): o2 cl:" + o2.getClass());
		return 0;
	}
}
