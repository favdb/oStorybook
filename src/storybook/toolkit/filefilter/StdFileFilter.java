/*
 * SbApp: Open Source software for novelists and authors.
 * Original idea 2008 - 2012 Martin Mustun
 * Copyrigth (C) Favdb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package storybook.toolkit.filefilter;

import java.io.File;

/**
 *
 * @author favdb
 */
public class StdFileFilter extends javax.swing.filechooser.FileFilter {
	String ext="";
	String desc="";
	public StdFileFilter(String extension, String description) {
		if (extension.indexOf(".") == -1) {
			extension = "." + extension;
		}
		this.ext  = extension;
		this.desc = description;
	}
	@Override
    public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}
        String filename = file.getName();
        return filename.endsWith(ext);
    }
	@Override
    public String getDescription() {
        return(desc+" ("+ext+")");
    }

}
