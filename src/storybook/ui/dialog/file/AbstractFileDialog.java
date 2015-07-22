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

package storybook.ui.dialog.file;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import storybook.SbConstants;
import storybook.toolkit.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;
import storybook.ui.dialog.AbstractDialog;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public abstract class AbstractFileDialog
		extends AbstractDialog
		implements CaretListener {

	protected JLabel lbWarning;
	protected JButton btOk;
	protected JTextField tfDir;
	protected JTextField tfName;
	protected JButton btChooseDir;
	protected JPanel optionsPanel;
	protected File file;
	private boolean hideDir = false;

	public AbstractFileDialog(MainFrame mainFrame) {
		super(mainFrame);
		this.mainFrame = mainFrame;
		init();
		initUi();
	}

	protected void initOptionsPanel() {
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		MigLayout layout = new MigLayout("wrap 2", "[]", "[]");
		setLayout(layout);

		JLabel lbName = new JLabel(I18N.getMsgColon("msg.dlg.mng.prjs.project.name"));
		tfName = new JTextField(30);
		tfName.setName("name");
		tfName.addCaretListener(this);

		JLabel lbDir = new JLabel(I18N.getMsgColon("msg.common.folder"));
		tfDir = new JTextField(30);
		tfDir.setName("folder");
		tfDir.addCaretListener(this);

		btChooseDir = new JButton();
		btChooseDir.setAction(getChooseFolderAction());
		btChooseDir.setText(I18N.getMsg("msg.common.choose.folder"));

		optionsPanel = new JPanel();
		initOptionsPanel();

		lbWarning = new JLabel(" ");

		// OK button
		btOk = new JButton();
		btOk.setAction(getOkAction());
		SwingUtil.addEnterAction(btOk, getOkAction());
		btOk.setText(I18N.getMsg("msg.common.ok"));
		btOk.setIcon(I18N.getIcon("icon.small.ok"));
		btOk.setEnabled(false);

		// cancel button
		JButton btCancel = new JButton();
		btCancel.setAction(getCancelAction());
		SwingUtil.addEscAction(btCancel, getCancelAction());
		btCancel.setText(I18N.getMsg("msg.common.cancel"));
		btCancel.setIcon(I18N.getIcon("icon.small.close"));

		// layout
		add(lbName);
		add(tfName);
		if (!hideDir) {
			add(lbDir);
			add(tfDir, "split 2");
			add(btChooseDir);
		}
		add(optionsPanel, "span");
		add(lbWarning, "span,gapy 10");
		add(btOk, "sg,span,split 2,right,gapy 10");
		add(btCancel, "sg");
	}

	protected void setDir(String dir) {
		tfDir.setText(dir);
	}

	protected void setFilename(String filename) {
		tfName.setText(filename);
		tfName.selectAll();
	}

	protected AbstractFileDialog getThis() {
		return this;
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		if (e.getSource() instanceof JTextField) {
			if (tfName.getText().isEmpty() || tfDir.getText().isEmpty()) {
				btOk.setEnabled(false);
				return;
			}
			btOk.setEnabled(true);
			lbWarning.setText(" ");
		}
	}

	public JTextField getTfDir() {
		return tfDir;
	}

	public JTextField getTfName() {
		return tfName;
	}

	public void setTfName(String name) {
		tfName.setText(name);
	}

	public File getFile() {
		return file;
	}

	public void setHideDir(boolean dirOnly) {
		hideDir = dirOnly;
	}

	@Override
	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (tfName.getText().isEmpty() || tfDir.getText().isEmpty()) {
					btOk.setEnabled(false);
					return;
				}
				File dir = new File(tfDir.getText());
				if (!dir.isDirectory() || !dir.canWrite() || !dir.canExecute()) {
					lbWarning.setText(I18N.getMsg("msg.new_file.not.writable"));
					return;
				}
				String name = tfName.getText();
				String fileExt = SbConstants.Storybook.DB_FILE_EXT.toString();
				if (!name.endsWith(fileExt)) {
					name += fileExt;
				}
				file = new File(tfDir.getText() + File.separator + name);
				if (file.exists()) {
					lbWarning.setText(I18N.getMsg("msg.new_file.file.exists"));
					return;
				}
				getThis().canceled = false;
				getThis().dispose();
			}
		};
	}

	private AbstractAction getChooseFolderAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				final JFileChooser fc = new JFileChooser(tfDir.getText());
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int ret = fc.showOpenDialog(mainFrame);
				if (ret != JFileChooser.APPROVE_OPTION) {
					return;
				}
				File dir = fc.getSelectedFile();
				tfDir.setText(dir.getAbsolutePath());
				lbWarning.setText(" ");
			}
		};
	}

	public MainFrame getMainFrame() {
		return mainFrame;
	}
}