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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang3.exception.ExceptionUtils;
import storybook.toolkit.I18N;
import storybook.toolkit.ViewUtil;
import storybook.toolkit.html.HtmlSelection;
import storybook.toolkit.swing.SwingUtil;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class ExceptionDialog extends AbstractDialog {

	private JTextArea ta;
	private Exception e;

	public ExceptionDialog(Exception e) {
		super();
		this.e = e;
		initAll();
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("wrap,fill", "", ""));
		setTitle("Exception");
		setIconImage(I18N.getIconImage("icon.small.error"));
		setPreferredSize(new Dimension(700, 500));

		ta = new JTextArea();
		ta.setEditable(false);
		StringBuilder buf = new StringBuilder();
		buf.append("Exception Message:\n");
		buf.append(e.getLocalizedMessage());
		buf.append("\n\nStack Trace:\n");
		buf.append(ExceptionUtils.getStackTrace(e));
		ta.setText(buf.toString());
		JScrollPane scroller = new JScrollPane(ta);
		SwingUtil.setMaxPreferredSize(scroller);

		// copy text
		JButton btCopyText = new JButton();
		btCopyText.setAction(getCopyTextAction());
		btCopyText.setText(I18N.getMsg("msg.file.info.copy.text"));
		btCopyText.setIcon(I18N.getIcon("icon.small.copy"));

		// layout
		add(scroller, "grow");
		add(btCopyText, "split 2,sg");
		add(getCloseButton(), "sg");

		ViewUtil.scrollToTop(scroller);
	}

	private AbstractAction getCopyTextAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				HtmlSelection selection = new HtmlSelection(ta.getText());
				Clipboard clbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				clbrd.setContents(selection, selection);
			}
		};
	}
}
