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

import storybook.ui.MainFrame;

/**
 *
 * @author martin
 */
public class InfoExporter extends AbstractExporter {

	private String text = "";

	public InfoExporter(MainFrame m) {
		super(m, true);
	}

	@Override
	public StringBuffer getContent() {
		return new StringBuffer(this.text);
	}

	public void setContent(String s) {
		this.text = s;
	}
}