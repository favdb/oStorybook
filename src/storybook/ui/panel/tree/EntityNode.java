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

package storybook.ui.panel.tree;

import javax.swing.tree.DefaultMutableTreeNode;

import storybook.model.hbn.entity.AbstractEntity;
import storybook.toolkit.I18N;

@SuppressWarnings("serial")
public class EntityNode extends DefaultMutableTreeNode {
	private String text;
	private AbstractEntity entity;

	public EntityNode(String textKey, AbstractEntity entity) {
		super();
		this.text = I18N.getMsg(textKey);
		this.entity = entity;
	}

	public AbstractEntity getDbTable() {
		return entity;
	}

	@Override
	public String toString() {
		return text;
	}
}
