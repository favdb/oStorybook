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

package storybook.test.ui;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.infonode.docking.View;

/**
 * @author martin
 *
 */
public class TestComponentFactory {

	private static TestComponentFactory instance;

	private static int counter = 0;

	private View view0;
	private View view1;

	private TestComponentFactory() {
	}

	public JComponent getComponent(View view) {
		if (view == view0) {
			return new JLabel("comp 1: " + (counter++));
		}
		if (view == view1) {
			return new JLabel("comp 2: " + (counter++));
		}
		return new JLabel("error");
	}

	public static TestComponentFactory getInstance() {
		if (instance == null) {
			instance = new TestComponentFactory();
		}
		return instance;
	}

	public View getView0() {
		return view0;
	}

	public void setView0(View view0) {
		this.view0 = view0;
	}

	public View getView1() {
		return view1;
	}

	public void setView1(View view1) {
		this.view1 = view1;
	}
}
