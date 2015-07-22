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

package storybook.toolkit.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import storybook.ui.panel.AbstractPanel;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class AutoCompleteComboBox extends AbstractPanel {

	private JComboBox combo;
	private IconButton btClear;
	private boolean setPreferredSize = true;
	private boolean addClearButton = true;

	public AutoCompleteComboBox() {
		super();
		initAll();
	}

	public AutoCompleteComboBox(boolean setPreferredSize, boolean addClearButton) {
		super();
		this.setPreferredSize = setPreferredSize;
		this.addClearButton = addClearButton;
		initAll();
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		// not used
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("ins 0,flowx"));

		combo = new JComboBox();
		combo.setEditable(true);
		if (setPreferredSize) {
			combo.setPreferredSize(new Dimension(250, 26));
		}
		AutoCompleteDecorator.decorate(combo);

		if (addClearButton) {
			btClear = new IconButton("icon.small.clear", getClearAction());
			btClear.setFlat();
			btClear.setSize20x20();
		}

		// layout
		add(combo);
		if (addClearButton) {
			add(btClear);
		}
	}

	public AbstractAction getClearAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				combo.setSelectedItem("");
			}
		};
	}

	public JComboBox getJComboBox() {
		return combo;
	}
}
