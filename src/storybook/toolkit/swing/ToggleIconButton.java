/*
Storybook: Scene-based software for novelists and authors.
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

package storybook.toolkit.swing;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JToggleButton;

import storybook.toolkit.I18N;

@SuppressWarnings("serial")
public class ToggleIconButton extends JToggleButton {

	public ToggleIconButton() {
		super();
	}

	public ToggleIconButton(String resourceIcon) {
		this(resourceIcon, null);
	}

	public ToggleIconButton(Action action) {
		this(null, null, action);
	}

	public ToggleIconButton(String resourceIcon, Action action) {
		this(resourceIcon, null, action);
	}

	public ToggleIconButton(String resourceIcon, String resourceToolTip,
			Action action) {
		if (action != null) {
			setAction(action);
		}
		if (resourceIcon != null) {
			setIcon(I18N.getIcon(resourceIcon));
		}
		if (resourceToolTip != null) {
			setToolTipText(I18N.getMsg((resourceToolTip)));
		}
	}

	public void setIcon(String resourceIcon) {
		if (resourceIcon != null) {
			setIcon(I18N.getIcon(resourceIcon));
		}
	}

	public void setSize16x16() {
		SwingUtil.setForcedSize(this, new Dimension(16, 16));
	}

	public void setSize20x20() {
		SwingUtil.setForcedSize(this, new Dimension(20, 20));
	}

	public void setSize32x20() {
		SwingUtil.setForcedSize(this, new Dimension(32, 20));
	}

	public void setSize22x22() {
		SwingUtil.setForcedSize(this, new Dimension(22, 22));
	}

	public void setSize72x72() {
		SwingUtil.setForcedSize(this, new Dimension(72, 72));
	}

	public void setNoBorder() {
		this.setBorder(null);
	}

	public void setControlButton() {
		this.setSize16x16();
		this.setNoBorder();
	}
}
