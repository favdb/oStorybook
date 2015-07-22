package storybook.toolkit.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;

public class H2FileFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String name = f.getName().toLowerCase();
		String ext = FilenameUtils.getExtension(name);
		if (ext != null) {
			if (!ext.equals("db")) {
				return false;
			}
			if (name.contains(".h2") || name.contains(".data")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "H2 Database File";
	}

}
