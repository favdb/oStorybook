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

package storybook.toolkit.swing.splash;

import java.awt.Frame;

import javax.swing.JDialog;

import storybook.ui.interfaces.IPaintable;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class SplashDialog extends JDialog implements IPaintable {

	public SplashDialog() {
		super();
		init();
		initUi();
	}

	public SplashDialog(Frame owner) {
		super(owner);
		init();
		initUi();
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("ins 0,fill,center"));
		setUndecorated(true);
	}
}
