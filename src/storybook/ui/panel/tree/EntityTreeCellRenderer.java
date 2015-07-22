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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import storybook.model.EntityUtil;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Scene;

@SuppressWarnings("serial")
class EntityTreeCellRenderer extends DefaultTreeCellRenderer {
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object userObject = node.getUserObject();
		if (leaf) {
			if (userObject instanceof Person) {
				setLeafIcon(((Person) userObject).getGender().getIcon());
			} else if (userObject instanceof Scene) {
				Scene scene = (Scene) userObject;
				setLeafIcon(scene.getSceneState().getIcon());
			} else if (userObject instanceof AbstractEntity) {
				Icon icon = EntityUtil
						.getEntityIcon((AbstractEntity) userObject);
				setLeafIcon(icon);
			} else {
				setLeafIcon(null);
			}
		}
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		if (!leaf && userObject instanceof AbstractEntity) {
			Icon icon = EntityUtil.getEntityIcon((AbstractEntity) userObject);
			setIcon(icon);
		}
		return this;
	};
}
