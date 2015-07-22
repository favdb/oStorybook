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

import javax.swing.AbstractAction;
import javax.swing.Icon;

import storybook.SbApp;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.ui.MainFrame;

/**
 * @author martin
 *
 */
public abstract class AbstractEntityAction extends AbstractAction {

	protected MainFrame mainFrame;
	protected AbstractEntity entity;

	public AbstractEntityAction(MainFrame mainframe, AbstractEntity entity, String name, Icon icon) {
		super(name, icon);
		this.mainFrame = mainframe;
		this.entity = entity;
		SbApp.trace("AbstractEntityAction("+mainFrame.getName()+","+entity.getAbbr()+","+name+",icon)");
	}
}
