/*
Storybook: Open Source software for novelists and authors.
Original idea 2008 - 2012 Martin Mustun
Copyrigth (C) Favdb

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
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import storybook.SbConstants;
import storybook.SbApp;

/**
 * Common tools
 * @author favdb
 */
public class CommonTools {

	/**
	 * Get the home directory
	 * @return CommonTools object for System user home directory
	 */
	public static File getHomeDir() {
		return new File(System.getProperty("user.home"));
	}

	/**
	 * Get the user directory
	 * @return File object for System user home directory
	 */
	public static File getUserDir() {
		return new File(System.getProperty("user.dir"));
	}

	/**
	 * Get the default export directory
	 * @return File object for the directory
	 */
	public static File getDefaultExportDir() {
		return new File(getHomeDir() + File.separator + "SBexport");
	}

	/**
	 * Get the complete name for the preference config file
	 * @return File object for the file
	 */
	public static File getPrefConfig() {
		return new File(getHomeDir()
			+ File.separator
			+ SbConstants.Storybook.USER_HOME_DIR.toString()
			+ File.separator + "preference.cfg.xml"
			);
	}

	/**
	 * Get the complete name for the preference DB file
	 * @return File object for the file
	 */
	public static File getPrefDBName() {
		return new File(getHomeDir() + File.separator + "preference.h2.db");
	}
}
