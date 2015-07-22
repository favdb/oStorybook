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

package storybook.toolkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author martin
 *
 */
public class LangUtil {
	private static final String[] RCODE = { "M", "CM", "D", "CD", "C", "XC",
			"L", "XL", "X", "IX", "V", "IV", "I" };
	private static final int[] BVAL = { 1000, 900, 500, 400, 100, 90, 50, 40,
			10, 9, 5, 4, 1 };

	public static String intToRoman(int number) {
		if (number <= 0 || number >= 4000) {
			throw new NumberFormatException(
					"Value outside roman numeral range.");
		}
		String roman = "";
		for (int i = 0; i < RCODE.length; i++) {
			while (number >= BVAL[i]) {
				number -= BVAL[i];
				roman += RCODE[i];
			}
		}
		return roman;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List removeNullAndDuplicates(List list) {
		list.removeAll(Collections.singletonList(null));
		Set set = new LinkedHashSet(list);
		return new ArrayList(set);
	}
}
