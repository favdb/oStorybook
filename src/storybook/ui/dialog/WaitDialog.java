/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008 - 2011 Martin Mustun

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

import java.awt.Frame;

import javax.swing.JLabel;
import javax.swing.JTextField;

import storybook.toolkit.I18N;

public class WaitDialog extends SplashDialog {

	private String text;

	public WaitDialog() {
		this("");
	}

	public WaitDialog(String text) {
		super();
		this.text = text;
		initGUI();
	}

	public WaitDialog(Frame owner) {
		this(owner, "");
	}

	public WaitDialog(Frame owner, String text) {
		super(owner);
		this.text = text;
		initGUI();
	}

	private void initGUI() {
		JLabel lbIcon = new JLabel(I18N.getIcon("icon.large.clock"));
		JTextField tfText = new JTextField(text);
		tfText.setBorder(null);
		tfText.setColumns(10);
		tfText.setEditable(false);
		add(lbIcon, "");
		add(tfText, "gapx 10,grow");
	}
}
