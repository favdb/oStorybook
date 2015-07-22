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
package storybook.export;

/**
 *
 * @author favdb
 */
import storybook.model.hbn.entity.Internal;
import storybook.toolkit.BookUtil;
import storybook.toolkit.EnvUtil;
import storybook.toolkit.I18N;
import storybook.toolkit.filefilter.HtmlFileFilter;
import storybook.toolkit.filefilter.TextFileFilter;
import storybook.ui.MainFrame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import storybook.SbConstants;
import storybook.toolkit.html.HtmlUtil;

public abstract class AbstractExporter {

	private String fileName;
	private boolean onlyHtmlExport;
	protected MainFrame mainFrame;

	public abstract StringBuffer getContent();

	public AbstractExporter(MainFrame m) {
		this(m, false);
	}

	public AbstractExporter(MainFrame m, boolean b) {
		this.mainFrame = m;
		this.onlyHtmlExport = b;
		this.fileName = "";
	}

	public boolean exportToTxtFile() {
		Internal internal = BookUtil.get(this.mainFrame,
			SbConstants.BookKey.EXPORT_DIRECTORY,
			EnvUtil.getDefaultExportDir(this.mainFrame));
		File file1 = new File(internal.getStringValue());
		JFileChooser chooser = new JFileChooser(file1);
		chooser.setApproveButtonText(I18N.getMsg("msg.common.export"));
		chooser.setSelectedFile(new File(getFileName()));
		chooser.setFileFilter(new TextFileFilter());
		int i = chooser.showOpenDialog(this.mainFrame);
		if (i == 1) {
			return false;
		}
		File outFile = chooser.getSelectedFile();
		if (!outFile.getName().endsWith(".txt")) {
			outFile = new File(outFile.getPath() + ".txt");
		}
		StringBuffer buffer = getContent();
		try {
			try (BufferedWriter outStream = new BufferedWriter(new FileWriter(outFile))) {
				String str = buffer.toString();
				outStream.write(HtmlUtil.htmlToText(str,true));
			}
		} catch (IOException e) {
			return false;
		}
		JOptionPane.showMessageDialog(this.mainFrame,
			I18N.getMsg("msg.common.export.success")
			+ "\n"
			+ outFile.getAbsolutePath(),
			I18N.getMsg("msg.common.export"), 1);
		return true;
	}
	public boolean exportToHtmlFile() {
		boolean bool = BookUtil.isUseHtmlScenes(this.mainFrame);
		if (this.onlyHtmlExport) {
			bool = true;
		}
		Internal internal = BookUtil.get(this.mainFrame,
			SbConstants.BookKey.EXPORT_DIRECTORY,
			EnvUtil.getDefaultExportDir(this.mainFrame));
		File file1 = new File(internal.getStringValue());
		JFileChooser chooser = new JFileChooser(file1);
		chooser.setApproveButtonText(I18N.getMsg("msg.common.export"));
		chooser.setSelectedFile(new File(getFileName()));
		if (bool) {
			chooser.setFileFilter(new HtmlFileFilter());
		} else {
			chooser.setFileFilter(new TextFileFilter());
		}
		int i = chooser.showOpenDialog(this.mainFrame);
		if (i == 1) {
			return false;
		}
		File outFile = chooser.getSelectedFile();
		if (bool) {
			if ((!outFile.getName().endsWith(".html")) || (outFile.getName().endsWith(".htm"))) {
				outFile = new File(outFile.getPath() + ".html");
			}
		} else if (!outFile.getName().endsWith(".txt")) {
			outFile = new File(outFile.getPath() + ".txt");
		}
		StringBuffer buffer = getContent();
		try {
			try (BufferedWriter outStream = new BufferedWriter(new FileWriter(outFile))) {
				String str = buffer.toString();
				outStream.write(str);
			}
		} catch (IOException e) {
			return false;
		}
		JOptionPane.showMessageDialog(this.mainFrame,
			I18N.getMsg("msg.common.export.success")
			+ "\n"
			+ outFile.getAbsolutePath(),
			I18N.getMsg("msg.common.export"), 1);
		return true;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String s) {
		this.fileName = s;
	}
}