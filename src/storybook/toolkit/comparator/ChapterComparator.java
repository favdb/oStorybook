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

import storybook.model.hbn.entity.Chapter;

/**
 * @author martin
 *
 */
public class ChapterComparator implements Comparator<Chapter> {

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Chapter ch1, Chapter ch2) {
		if (ch1 == null && ch2 == null) {
			return 0;
		}
		if (ch1 == null || ch2 == null) {
			return -1;
		}
		return ch1.compareTo(ch2);
	}
}
