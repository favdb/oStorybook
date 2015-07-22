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
public class OdtFileFilter extends javax.swing.filechooser.FileFilter {
	@Override
    public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}
        String filename = file.getName();
        return filename.endsWith(".odt");
    }
	@Override
    public String getDescription() {
        return("Open Document File (*.odt)");
    }

}
