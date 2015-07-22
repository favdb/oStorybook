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

package storybook.toolkit;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import com.inet.jortho.FileUserDictionary;
import com.inet.jortho.SpellChecker;

import storybook.SbConstants;
import storybook.SbConstants.PreferenceKey;
import storybook.SbConstants.Spelling;
import storybook.model.hbn.entity.Preference;
import storybook.SbApp;

public class SpellCheckerUtil {

	private static File userDictDir = null;

	public static File getDictionaryDir() throws IOException {
		try {
			File dir = new File(".");
			File file = new File(dir.getCanonicalPath() + File.separator + SbConstants.Directory.DICTIONARIES);
			return file;
		} catch (IOException e) {
			SbApp.error("SpellCheckerUtil.getDictionaryDir()", e);
		}
		return null;
	}

	public static URL getDictionaryDirAsURL() throws MalformedURLException, IOException {
		URI uri = getDictionaryDir().toURI();
		return uri.toURL();
	}

	public static void registerDictionaries() {
		SbApp.trace("SpellCheckerUtil.registerDictionaries()");
		try {
			URL url = getDictionaryDirAsURL();
			Preference pref = PrefUtil.get(PreferenceKey.SPELLING, Spelling.none.toString());
			String spelling = pref.getStringValue();
			String lang = spelling.substring(0, 2);

			SpellChecker.registerDictionaries(url, "en,de,es,fr,it,nl,pl", lang);

			// user dictionary directory
			File usrDictDir = initUserDictDir();
			FileUserDictionary fud = new FileUserDictionary(usrDictDir.toString());
			SpellChecker.setUserDictionaryProvider(fud);
		} catch (IOException e) {
			SbApp.error("SpellCheckerUtil.registerDictionaries()",e);
		}
	}

	public static File initUserDictDir() {
		if (userDictDir == null) {
			File dir = new File(System.getProperty("user.home"));
			userDictDir = new File(dir + File.separator + ".storybook" + File.separator + SbConstants.Directory.USER_DICTS);
			userDictDir.mkdir();
		}
		return userDictDir;
	}

	public static boolean isSpellCheckActive() {
//		String spelling = PrefManager.getInstance().getStringValue(Constants.Preference.SPELLING);
//		if (spelling.equals(Constants.Spelling.none.name())) {
//			return false;
//		}
		return true;
	}
}
