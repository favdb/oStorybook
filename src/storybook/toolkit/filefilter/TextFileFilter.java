package storybook.toolkit.filefilter;

import java.io.File;

public class TextFileFilter extends javax.swing.filechooser.FileFilter {
	@Override
    public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}
        String filename = file.getName();
        return filename.endsWith(".txt");
    }
	@Override
    public String getDescription() {
        return "Text File (*.txt)";
    }
}
