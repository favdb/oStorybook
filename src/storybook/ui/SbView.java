/*
Storybook: Open Source software for novelists and authors.
Copyright (C) 2008 - 2012 Martin Mustun, 2015 FaVdB

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

package storybook.ui;

import java.awt.Component;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.infonode.docking.View;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class SbView extends View {

	private static int counter = -1;

	private boolean loaded;
	private Integer number;

	public SbView(String title) {
		this(title, null, null);
	}

	public SbView(String title, Icon icon) {
		this(title, icon, null);
	}

	public SbView(String title, Component comp) {
		this(title, null, comp);
	}

	public SbView(String title, Icon icon, Component comp) {
		super(title, icon, comp);
		loaded = comp != null;
		number = counter++;
	}

	public void load(JComponent comp) {
		super.setComponent(comp);
		loaded = true;
	}

	public void unload() {
		super.setComponent(null);
		loaded = false;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public boolean isWindowShowing() {
		return getRootWindow() != null;
	}

	public void cleverRestoreFocus() {
		setVisible(true);
		if (!isMinimized()) {
			restore();
		}
		restoreFocus();
	}

	@Override
	public String toString() {
		return "View " + number + ": " + getTitle();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = hash * 31 + number.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		SbView test = (SbView) obj;
		return Objects.equals(number, test.number);
	}
}
