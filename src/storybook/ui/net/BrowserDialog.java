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

package storybook.ui.net;

import java.awt.Color;

import javax.swing.JScrollPane;

import storybook.toolkit.I18N;
import storybook.ui.dialog.AbstractDialog;

import net.miginfocom.swing.MigLayout;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class BrowserDialog extends AbstractDialog {

	private String url;
	private String title;
	private int width;
	private int height;

	public BrowserDialog(String url) {
		this(url, I18N.getMsg("BrowserDialog"), 500, 300);
	}

	public BrowserDialog(String url, String title, int width, int height) {
		super();
		this.url = url;
		this.title = title;
		this.width = width;
		this.height = height;
		initAll();
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("wrap,fill,ins 10"));
		setTitle(title);
		setBackground(Color.white);

		BrowserPanel panel = new BrowserPanel(url, width, height);

		// layout
		add(new JScrollPane(panel), "grow");
		add(getCloseButton(), "right,sg");
	}
}
