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

package storybook.ui.panel.attributes;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import storybook.model.BookModel;
import storybook.model.hbn.dao.AttributeDAOImpl;
import storybook.model.hbn.entity.Attribute;
import storybook.toolkit.I18N;
import storybook.toolkit.swing.IconButton;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class AttributesPanel extends AbstractPanel {

	private List<Attribute> attributes;
	private List<AttributePanel> attrPanels;
	private List<String> keys;

	public AttributesPanel(MainFrame mainFrame) {
		super(mainFrame);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}

	@Override
	public void init() {
		attrPanels = new ArrayList<AttributePanel>();
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("wrap 2,fillx", "[grow][]", ""));

		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		AttributeDAOImpl dao = new AttributeDAOImpl(session);
		keys = dao.findKeys();

		for (Attribute attribute : attributes) {
			session.refresh(attribute);
			AttributePanel panel = new AttributePanel(attribute, keys);
			attrPanels.add(panel);
			add(panel);
			add(getRemoveButton(panel));
		}

		AttributePanel newAttrPanel = new AttributePanel(keys);
		attrPanels.add(newAttrPanel);
		add(newAttrPanel);
		add(getRemoveButton(newAttrPanel));

		model.commit();

		IconButton btAdd = new IconButton(getAddAction());
		btAdd.setText(I18N.getMsg("msg.common.add"));
		btAdd.setIcon(I18N.getIcon("icon.small.plus"));
		add(btAdd, "newline,span,gap 0 0 10 0");
	}

	private IconButton getRemoveButton(AttributePanel panel) {
		IconButton bt = new IconButton();
		RemoveAction act = new RemoveAction(bt, panel);
		bt.setAction(act);
		bt.setIcon(I18N.getIcon("icon.small.minus"));
		bt.setSize20x20();
		bt.setFlat();
		return bt;
	}

	public AbstractAction getAddAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AttributePanel panel = new AttributePanel(keys);
				attrPanels.add(panel);
				add(panel, getComponentCount() - 1);
				add(getRemoveButton(panel), getComponentCount() - 1);
				revalidate();
				repaint();
			}
		};
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public List<Attribute> getAttributes() {
		List<Attribute> attributes = new ArrayList<Attribute>();
		for (AttributePanel panel : attrPanels) {
			Attribute attr = panel.getAttribute();
			if (attr == null) {
				continue;
			}
			attributes.add(attr);
		}
		return attributes;
	}

	class RemoveAction extends AbstractAction {
		private JButton bt;
		private AttributePanel panel;

		public RemoveAction(JButton bt, AttributePanel panel) {
			this.bt = bt;
			this.panel = panel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			attrPanels.remove(panel);
			remove(bt);
			remove(panel);
			revalidate();
			repaint();
		}
	}
}
