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

package storybook.test.langtool;

import java.util.List;

import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.rules.RuleMatch;

/**
 * @author martin
 *
 */
public class TestLangTool {
	public static void main(String[] args) {
		System.out.println("start...");
		try {
			JLanguageTool langTool = new JLanguageTool(Language.ENGLISH);
			langTool.activateDefaultPatternRules();
			List<RuleMatch> matches = langTool.check("A sentence "
					+ "with a error in the Hitchhiker's Guide tot he Galaxy");
			for (RuleMatch match : matches) {
				System.out.println("Potential error at line "
						+ match.getEndLine() + ", column " + match.getColumn()
						+ ": " + match.getMessage());
				System.out.println("Suggested correction: "
						+ match.getSuggestedReplacements());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("finished.");
	}
}
