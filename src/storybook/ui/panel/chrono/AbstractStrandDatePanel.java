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

package storybook.ui.panel.chrono;

import java.beans.PropertyChangeEvent;
import java.util.Date;

import storybook.controller.BookController;
import storybook.model.hbn.entity.Strand;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;

@SuppressWarnings("serial")
public abstract class AbstractStrandDatePanel extends AbstractPanel {

	protected Strand strand;
	protected Date date;

	public AbstractStrandDatePanel(MainFrame mainFrame, Strand strand, Date date) {
		super(mainFrame);
		this.strand = strand;
		this.date = date;
		init();
		initUi();
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		if (BookController.SceneProps.NEW.check(propName)
				|| BookController.SceneProps.DELETE.check(propName)) {
			refresh();
			if (getParent() != null) {
				getParent().validate();
			} else {
				validate();
			}
		}
	}

	public Strand getStrand() {
		return strand;
	}

	public Date getDate() {
		return date;
	}
}
