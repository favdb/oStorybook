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

package storybook.ui.label;

import javax.swing.JLabel;

import storybook.model.state.SceneState;
import storybook.toolkit.I18N;
import storybook.ui.interfaces.IRefreshable;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class SceneStateLabel extends JLabel implements IRefreshable {

	private SceneState state;
	private boolean iconOnly;

	public SceneStateLabel(SceneState state) {
		this(state, false);
	}

	public SceneStateLabel(SceneState state, boolean iconOnly) {
		this.state = state;
		this.iconOnly = iconOnly;
		refresh();
	}

	@Override
	public void refresh() {
		if (!iconOnly) {
			setText(state.toString());
		}
		setIcon(state.getIcon());
		setToolTipText(I18N.getMsgColon("msg.status") + " " + state);
	}

	public SceneState getState() {
		return state;
	}

	public void setState(SceneState state) {
		this.state = state;
	}
}
