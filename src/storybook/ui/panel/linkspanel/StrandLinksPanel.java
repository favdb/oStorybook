/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008-2012 Martin Mustun

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

package storybook.ui.panel.linkspanel;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import org.hibernate.UnresolvableObjectException;
import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;
import storybook.toolkit.swing.label.CleverLabel;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;

@SuppressWarnings("serial")
public class StrandLinksPanel extends AbstractPanel {

	private Scene scene;
	private boolean opaque;

	public StrandLinksPanel(MainFrame mainFrame, Scene scene, boolean opaque) {
		this.mainFrame = mainFrame;
		this.opaque = opaque;
		this.scene = scene;
		refresh();
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		Object newValue = evt.getNewValue();
		String propName = evt.getPropertyName();

		if (BookController.SceneProps.UPDATE.check(propName)) {
			if (!((Scene) newValue).getId().equals(scene.getId())) {
				// not this scene
				return;
			}
			refresh();
			return;
		}

		if (BookController.StrandProps.UPDATE.check(propName)) {
			EntityUtil.refresh(mainFrame, scene.getStrand());
			refresh();
		}
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("insets 2"));
		if (opaque) {
			setOpaque(true);
			setBackground(scene.getStrand().getJColor());
		} else {
			setOpaque(false);
		}
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		session.refresh(scene);
		List<Strand> list = scene.getStrands();
		if (list != null) {
			Collections.sort(list);
		}
		for (Strand strand : list) {
			try {
				session.refresh(strand);
			} catch (UnresolvableObjectException e) {
				e.printStackTrace();
				continue;
			}
			CleverLabel lb = new CleverLabel(strand.getAbbreviation(),
					JLabel.CENTER);
			lb.setToolTipText(EntityUtil.getToolTip(strand));
			lb.setBackground(strand.getJColor());
			add(lb, "w 30");
		}
		model.commit();
	}
}
