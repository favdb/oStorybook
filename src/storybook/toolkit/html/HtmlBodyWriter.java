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

package storybook.toolkit.html;

import java.io.IOException;
import java.io.Writer;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLWriter;

/**
 * @author martin
 *
 */
public class HtmlBodyWriter extends HTMLWriter {
	private boolean inBody = false;

	public HtmlBodyWriter(Writer writer, HTMLDocument doc) {
		super(writer, doc);
	}

	private boolean isBody(Element elem) {
		// copied from HTMLWriter.startTag()
		AttributeSet attr = elem.getAttributes();
		Object nameAttribute = attr.getAttribute(StyleConstants.NameAttribute);
		HTML.Tag name;
		if (nameAttribute instanceof HTML.Tag) {
			name = (HTML.Tag) nameAttribute;
		} else {
			name = null;
		}
		return name == HTML.Tag.BODY;
	}

	@Override
	protected void startTag(Element elem) throws IOException,
			BadLocationException {
		if (inBody) {
			super.startTag(elem);
		}
		if (isBody(elem)) {
			inBody = true;
		}
	}

	@Override
	protected void endTag(Element elem) throws IOException {
		if (isBody(elem)) {
			inBody = false;
		}
		if (inBody) {
			super.endTag(elem);
		}
	}
}
