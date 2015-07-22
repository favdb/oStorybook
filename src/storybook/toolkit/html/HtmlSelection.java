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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author martin
 *
 */
public class HtmlSelection implements Transferable, ClipboardOwner {

	public static DataFlavor htmlFlavor;
	private DataFlavor[] supportedFlavors = { htmlFlavor };
	private String htmlText;

	public HtmlSelection(String htmlText) {
		this.htmlText = htmlText;
		htmlFlavor = new DataFlavor("text/html", "HTML");
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// System.out.println("HtmlSelection.lostOwnership(): ");
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(htmlFlavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (flavor.equals(htmlFlavor)) {
			return new ByteArrayInputStream(htmlText.getBytes());
		} else {
			throw new UnsupportedFlavorException(htmlFlavor);
		}
	}
}
