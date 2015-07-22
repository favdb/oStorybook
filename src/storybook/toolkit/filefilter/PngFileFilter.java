package storybook.toolkit.filefilter;

import java.io.File;

public class PngFileFilter extends javax.swing.filechooser.FileFilter {
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}
		String filename = file.getName();
		return filename.endsWith(".png");
	}

	public String getDescription() {
		return "*.png";
	}
}
