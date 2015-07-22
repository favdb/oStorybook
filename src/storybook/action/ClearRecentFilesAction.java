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
/* vérification OK */

package storybook.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import storybook.toolkit.I18N;

/**
 * @author martin
 *
 */
public class ClearRecentFilesAction extends AbstractAction {

	private ActionHandler actionHandler;

	public ClearRecentFilesAction(ActionHandler actionHandler) {
		super(I18N.getMsg("msg.file.clear.recent"));
		this.actionHandler = actionHandler;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		actionHandler.handleRecentClear();
	}

	public ActionHandler getActionHandler() {
		return actionHandler;
	}
}
