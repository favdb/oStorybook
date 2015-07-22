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

package storybook.model;

import java.io.File;
import storybook.SbApp;

import storybook.SbConstants;

/**
 * @author martin
 *
 */
public class DbFile {

	private File file;
	private String dbName;
	private String name;

	public DbFile(String dbName) {
		this(new File(dbName));
		SbApp.trace("DbFile(dbName="+dbName);
	}

	public DbFile(File file) {
		SbApp.trace("DbFile(file="+file.getAbsolutePath());
		this.file = file;
		String absPath = file.getAbsolutePath();
		String ext = SbConstants.Storybook.DB_FILE_EXT.toString();
		int idx = absPath.lastIndexOf(ext);
		dbName = absPath.substring(0, idx);
		String fileName = file.getName();
		idx = fileName.lastIndexOf(ext);
		name = fileName.substring(0, idx);
	}

	public File getFile() {
		return file;
	}

	public String getPath() {
		return dbName.substring(0, dbName.lastIndexOf(File.separator));
	}

	public String getDbName() {
		return dbName;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		DbFile test = (DbFile) obj;
		boolean ret = true;
		ret = ret && file.equals(test.file);
		return ret;

	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + (file != null ? file.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return file.getPath();
	}
}
