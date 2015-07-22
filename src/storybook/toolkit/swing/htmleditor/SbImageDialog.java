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

package storybook.toolkit.swing.htmleditor;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import storybook.toolkit.I18N;
import storybook.toolkit.swing.IconButton;
import storybook.ui.dialog.AbstractDialog;

import net.miginfocom.swing.MigLayout;

/**
 * @author martin
 */

@SuppressWarnings("serial")
public class SbImageDialog extends AbstractDialog {

	private JTextField tfFilename;
	private JTextField tfWidth;
	private JTextField tfHeight;
	private double ratio = 1.0;
	private int imageWidth = 0;
	private int imageHeight = 0;

	public SbImageDialog(JComponent parent) {
		super(parent);
		initAll();
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("wrap 2,fill", "[][]", ""));
		setTitle(I18N.getMsg("msg.editor.insert.image"));

		JLabel lbFilename = new JLabel(
				I18N.getMsgColon("msg.file.info.filename"));
		tfFilename = new JTextField();
		tfFilename.setColumns(20);
		IconButton btChooseFile = new IconButton("icon.small.open",
				new ChooseImageFileAction());
		btChooseFile.setSize32x20();

		JLabel lbWidth = new JLabel(I18N.getMsgColon("msg.common.width"));

		tfWidth = new JTextField();
		tfWidth.setColumns(10);

		IconButton btCalcWidth = new IconButton("icon.small.calc",
				new CalcWidthAction());
		btCalcWidth.setSize20x20();

		JLabel lbHeight = new JLabel(I18N.getMsgColon("msg.common.height"));
		tfHeight = new JTextField();
		tfHeight.setColumns(10);

		IconButton btCalcHeight = new IconButton("icon.small.calc",
				new CalcHeightAction());
		btCalcHeight.setSize20x20();

		// layout
		add(lbFilename);
		add(tfFilename, "grow,split 2");
		add(btChooseFile);
		add(lbWidth);
		add(tfWidth, "split 2");
		add(btCalcWidth);
		add(lbHeight);
		add(tfHeight, "split 2");
		add(btCalcHeight);
		add(getOkButton(), "gaptop 20,span,right,split 2,sg");
		add(getCancelButton(), "sg");
	}

	public String getHTML() {
		String fn = tfFilename.getText();
		String w = tfWidth.getText();
		String h = tfHeight.getText();
		return "<img src='" + fn + "' width='" + w + "' height='" + h + "'>";
	}

	protected SbImageDialog getThis() {
		return this;
	}

	private class CalcWidthAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			int n = Integer.parseInt(tfHeight.getText());
			String val = Integer.toString((int) ((double) n * ratio));
			tfWidth.setText(val);
		}
	}

	private class CalcHeightAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			int n = Integer.parseInt(tfWidth.getText());
			String val = Integer.toString((int) ((double) n / ratio));
			tfHeight.setText(val);
		}
	}

	private class ChooseImageFileAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showDialog(getThis(), "OK");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					File file = fc.getSelectedFile();
					tfFilename.setText(file.toURI().toURL().toString());
					BufferedImage img = ImageIO.read(file);
					imageWidth = img.getWidth();
					imageHeight = img.getHeight();
					tfWidth.setText(Integer.toString(imageWidth));
					tfHeight.setText(Integer.toString(imageHeight));
					ratio = (double) imageWidth / (double) imageHeight;
				} catch (Exception e1) {
				}
			}
		}
	}
}
