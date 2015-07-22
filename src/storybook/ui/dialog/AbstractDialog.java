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

package storybook.ui.dialog;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;

import storybook.toolkit.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.MainFrame;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractDialog extends JDialog {

	protected MainFrame mainFrame;
	protected JComponent parent;

	protected boolean canceled = false;

	public AbstractDialog() {
		this.mainFrame = null;
		this.parent = null;
	}

	public AbstractDialog(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.parent = null;
	}

	public AbstractDialog(JComponent parent) {
		this.parent = parent;
		this.mainFrame = null;
	}

	abstract public void init();

	public void initUi() {
		if (mainFrame != null) {
			setIconImage(mainFrame.getIconImage());
		}
	}

	public void initAll() {
		init();
		initUi();
	}

	protected JButton getOkButton() {
		AbstractAction act = getOkAction();
		JButton bt = new JButton(act);
		bt.setText(I18N.getMsg("msg.common.ok"));
		bt.setIcon(I18N.getIcon("icon.small.ok"));
		SwingUtil.addEnterAction(bt, act);
		return bt;
	}

	protected JButton getCancelButton() {
		AbstractAction act = getCancelAction();
		JButton bt = new JButton(act);
		bt.setText(I18N.getMsg("msg.common.cancel"));
		bt.setIcon(I18N.getIcon("icon.small.cancel"));
		SwingUtil.addEscAction(bt, act);
		return bt;
	}

	protected JButton getCloseButton() {
		JButton bt = new JButton(getOkAction());
		bt.setIcon(I18N.getIcon("icon.small.close"));
		bt.setText(I18N.getMsg("msg.common.close"));
		return bt;
	}

	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canceled = false;
				dispose();
			}
		};
	}

	protected AbstractAction getCancelAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canceled = true;
				dispose();
			}
		};
	}

	public boolean isCanceled() {
		return canceled;
	}
}
