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

package storybook.ui.edit;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import org.apache.commons.lang3.text.WordUtils;

import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Person;
import storybook.toolkit.swing.FontUtil;

/**
 * @author martin
 *
 */
public class PersonCbPanelDecorator extends CbPanelDecorator {
	private String oldCat = "";

	public PersonCbPanelDecorator() {
	}

	@Override
	public void decorateBeforeFirstEntity() {
		oldCat = "";
	}

	@Override
	public void decorateBeforeEntity(AbstractEntity entity) {
		Person p = (Person) entity;
		String cat = WordUtils.capitalize(p.getCategory().getName());
		if (!oldCat.equals(cat)) {
			JLabel lb = new JLabel(cat);
			lb.setFont(FontUtil.getBoldFont());
			panel.add(lb, "span");
			oldCat = cat;
		}
	}

	@Override
	public void decorateEntity(JCheckBox cb, AbstractEntity entity) {
		Person p = (Person) entity;
		JLabel lbIcon = new JLabel(p.getIcon());
		lbIcon.setToolTipText(p.getGender().getName());
		panel.add(lbIcon, "split 2");
		panel.add(cb);
	}

	@Override
	public void decorateAfterEntity(AbstractEntity entity) {
	}
}
