/*
CleverColorChooser: Clever Color Chooser with "no color" and
color palette (both optional).

This class needs MigLayout to compile:
http://www.miglayout.com/

Copyright (C) 2008-2009 Martin Mustun

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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import storybook.ui.interfaces.IRefreshable;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class CleverColorChooser extends JPanel implements IRefreshable,
		MouseListener {

	public static final String COMP_NAME_BT_PALETTE = "bt:palette";

	private IconButton btPalette;
	private JButton btColorChooser;
	private IconButton btClearColor;
	private ColorLabel lbShowColor;
	private Color[] colors;
	private boolean allowNoColor;
	private Color startColor;
	private String title;

	public CleverColorChooser() {
		this("", null, null, true);
	}

	public CleverColorChooser(String title, Color startColor, Color[] colors,
			boolean allowNoColor) {
		this.title = title;
		this.startColor = startColor;
		this.colors = colors;
		this.allowNoColor = allowNoColor;
		initGUI();
	}

	private void initGUI() {
		MigLayout layout = new MigLayout("insets 0");
		setLayout(layout);

		// shows the selected color
		lbShowColor = new ColorLabel(startColor);

		// custom palette
		if (colors != null) {
			btPalette = new IconButton("icon.small.palette",
					getShowPalettePopupAction());
			btPalette.setSize20x20();
			btPalette.setName(COMP_NAME_BT_PALETTE);
			lbShowColor.setComponentPopupMenu(createPalettePopupMenu());
		}

		// the color chooser
		btColorChooser = new JButton();
		btColorChooser.setAction(getShowColorChooserAction());

		// button to clear the color
		if (allowNoColor) {
			btClearColor = new IconButton("icon.small.delete",
					getClearColorAction());
			btClearColor.setSize20x20();
		}

		add(lbShowColor);
		if (colors != null) {
			add(btPalette);
		}
		if (allowNoColor) {
			add(btClearColor);
		}
		add(btColorChooser);
	}

	@Override
	public void refresh() {
		removeAll();
		initGUI();
	}

	private AbstractAction getShowColorChooserAction() {
		return new AbstractAction(title) {
			public void actionPerformed(ActionEvent evt) {
				Component parent = getThis().getParent();
				if (parent == null) {
					parent = getThis();
				}
				Color color = JColorChooser.showDialog(parent, title,
						lbShowColor.getBackground());
				if (color != null) {
					lbShowColor.setBackground(color);
				}
			}
		};
	}

	private AbstractAction getClearColorAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				lbShowColor.setBackground(null);
			}
		};
	}

	private AbstractAction getShowPalettePopupAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				JComponent comp = (JComponent) evt.getSource();
				JPopupMenu menu = createPalettePopupMenu();
				menu.show(comp, 10, 10);
			}
		};
	}

	private JPopupMenu createPalettePopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		menu.add(new ColorsPanel());
		return menu;
	}

	/**
	 * Returns its self for use within anonymous objects that require references
	 * to this object without being able to use <code>this</code> keyword.
	 */
	protected CleverColorChooser getThis() {
		return this;
	}

	public Color getColor() {
		if (!lbShowColor.getText().isEmpty()) {
			return null;
		}
		return lbShowColor.getBackground();
	}

	public void setColor(Color color) {
		lbShowColor.setBackground(color);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent evt) {
		Object source = evt.getSource();
		if (source instanceof JLabel) {
			JComponent comp = (JComponent) source;
			JComponent parent1 = (JComponent) comp.getParent();
			JComponent parent2 = (JComponent) parent1.getParent();
			JPopupMenu menu = (JPopupMenu) parent2;
			menu.setVisible(false);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	private class ColorsPanel extends JPanel implements MouseListener {
		private static final long serialVersionUID = -4673345291752905659L;

		public ColorsPanel() {
			MigLayout layout = new MigLayout("insets 1,wrap 4", "[20]", "[20]");
			setLayout(layout);

			for (Color color : colors) {
				JLabel label = new JLabel();
				label.setOpaque(true);
				label.setBackground(color);
				label.setFocusable(true);
				label.addMouseListener(this);
				label.addMouseListener(getThis());
				add(label, "grow");
			}
		}

		@Override
		public void mouseClicked(MouseEvent evt) {
		}

		@Override
		public void mouseEntered(MouseEvent evt) {
			JComponent comp = (JComponent) evt.getSource();
			Color color = comp.getBackground();
			Color borderColor;
			if (ColorUtil.isDark(color)) {
				borderColor = Color.white;
			} else {
				borderColor = Color.black;
			}
			comp.setBorder(BorderFactory.createLineBorder(borderColor, 2));
		}

		@Override
		public void mouseExited(MouseEvent evt) {
			JComponent comp = (JComponent) evt.getSource();
			comp.setBorder(null);
		}

		@Override
		public void mousePressed(MouseEvent evt) {
			JLabel label = (JLabel) evt.getSource();
			Color color = label.getBackground();
			lbShowColor.setBackground(color);
			lbShowColor.setText("");
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}

	private class ColorLabel extends JLabel {
		private static final long serialVersionUID = 7299513480525524900L;

		// public ColorLabel() {
		// this(null);
		// }

		public ColorLabel(Color color) {
			super("", SwingConstants.CENTER);
			Dimension dim = new Dimension(50, 20);
			setPreferredSize(dim);
			setMinimumSize(dim);
			setMaximumSize(dim);
			setOpaque(true);
			setBorder(BorderFactory.createLineBorder(Color.black));
			setBackground(color);
		}

		@Override
		public void setBackground(Color bg) {
			super.setBackground(bg);
			if (bg == null) {
				setText("X");
			} else {
				setText("");
			}
		}
	}
}
