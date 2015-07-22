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

package storybook.toolkit.swing.panel;

import java.awt.Color;
import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import storybook.toolkit.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.panel.AbstractPanel;

import net.miginfocom.swing.MigLayout;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class PlainTextEditor extends AbstractPanel implements CaretListener {

	private JTextArea ta;
	private int maxLength;
	private JLabel lbMessage;

	public PlainTextEditor() {
		super();
		initAll();
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("wrap,fill"));

		ta = new JTextArea(10, 20);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.addCaretListener(this);

		JScrollPane scroller = new JScrollPane(ta);
		SwingUtil.setMaxPreferredSize(scroller);

		lbMessage = new JLabel(" ");

		// layout
		add(scroller);
		add(lbMessage);
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		if (maxLength > 0) {
			int len = maxLength - getText().length() - 1;
			if (len < 0) {
				lbMessage.setForeground(Color.red);
			} else {
				lbMessage.setForeground(Color.black);
			}
			lbMessage.setText(I18N.getMsg("msg.editor.letters.left", len));
		}
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public String getText() {
		return ta.getText();
	}

	public void setText(String txt) {
		ta.setText(txt);
		ta.setCaretPosition(0);
	}
}
