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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import storybook.toolkit.swing.SwingUtil;
import storybook.ui.RadioButtonGroup;

import net.miginfocom.swing.MigLayout;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class RadioButtonGroupPanel extends JPanel implements ItemListener {

	private RadioButtonGroup rbg;
	private ButtonGroup buttonGroup;
	private HashMap<Integer, JPanel> panelMap;

	public RadioButtonGroupPanel(RadioButtonGroup rbg) {
		this.rbg = rbg;
		buttonGroup = new ButtonGroup();
		panelMap = new HashMap<Integer, JPanel>();

		setLayout(new MigLayout("wrap"));
		setBorder(SwingUtil.getBorderEtched());

		for (Integer key : this.rbg.getGroupMap().keySet()) {
			JRadioButton rb = new JRadioButton();
			rb.setText(this.rbg.getGroupMap().get(key).getRight());
			rb.addItemListener(this);
			rb.setName(key.toString());
			add(rb);
			buttonGroup.add(rb);
			JPanel panel = new JPanel(new MigLayout("ins 0,wrap 2"));
			panelMap.put(key, panel);
			add(panel);
		}
	}

	public ArrayList<AbstractButton> getNotSelectedButtons() {
		ArrayList<AbstractButton> btList = new ArrayList<AbstractButton>();
		Enumeration<AbstractButton> buttons = buttonGroup.getElements();
		while (buttons.hasMoreElements()) {
			AbstractButton bt = buttons.nextElement();
			if (!bt.isSelected()) {
				btList.add(bt);
			}
		}
		return btList;
	}

	public AbstractButton getSelectedButton() {
		Enumeration<AbstractButton> buttons = buttonGroup.getElements();
		while (buttons.hasMoreElements()) {
			AbstractButton bt = buttons.nextElement();
			if (bt.isSelected()) {
				return bt;
			}
		}
		return null;
	}

	public AbstractButton getButton(Integer key) {
		Enumeration<AbstractButton> buttons = buttonGroup.getElements();
		while (buttons.hasMoreElements()) {
			AbstractButton bt = buttons.nextElement();
			if (bt.getName().equals(key.toString())) {
				return bt;
			}
		}
		return null;
	}

	public JPanel getSubPanel(Integer key) {
		return panelMap.get(key);
	}

	public void enableSubPanel(Integer key) {
		if (key == null) {
			return;
		}
		JPanel panel = getSubPanel(key);
		SwingUtil.enableContainerChildren(panel, true);
	}

	public void disableSubPanel(Integer key) {
		if (key == null) {
			return;
		}
		JPanel panel = getSubPanel(key);
		SwingUtil.enableContainerChildren(panel, false);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getItem() instanceof JRadioButton) {
			JRadioButton rb = (JRadioButton) e.getItem();
			int key = Integer.parseInt(rb.getName());
			if (rb.isSelected()) {
				enableSubPanel(key);
			} else {
				disableSubPanel(key);
			}
		}
	}
}
