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
import java.io.StringWriter;

import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import storybook.toolkit.I18N;
import storybook.toolkit.html.HtmlUtil;
import storybook.toolkit.net.NetUtil;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class OpenUrlAction extends AbstractAction {

	private JEditorPane pane;

	public OpenUrlAction(JEditorPane pane) {
		super(I18N.getMsg("msg.common.open.link"), I18N
				.getIcon("icon.small.web"));
		this.pane = pane;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			HTMLEditorKit kit = (HTMLEditorKit) pane.getEditorKit();
			Document doc = pane.getDocument();
			StringWriter writer = new StringWriter();
			int pos = pane.getCaretPosition();
			kit.write(writer, doc, pos - 1, 1);
			String url = HtmlUtil.findHref(writer.toString());
			if (url == null) {
				return;
			}
			NetUtil.openBrowser(url);
		} catch (Exception e1) {
			// ignore
		}
	}
}
