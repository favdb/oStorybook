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

package storybook.ui.options;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import storybook.toolkit.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.toolkit.swing.panel.BackgroundPanel;
import storybook.ui.MainFrame;
import storybook.ui.dialog.AbstractDialog;

import net.miginfocom.swing.MigLayout;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractOptionsDialog extends AbstractDialog implements
		ChangeListener {

	private boolean zoom;

	protected int zoomValue;
	protected int zoomMinValue;
	protected int zoomMaxValue;

	protected BackgroundPanel panel;

	public AbstractOptionsDialog(MainFrame mainFrame) {
		this(mainFrame, true);
	}

	public AbstractOptionsDialog(MainFrame mainFrame, boolean hasZoom) {
		super(mainFrame);
		this.zoom = hasZoom;
		internalInit();
		internalInitUi();
	}

	protected void zoom(int val) {
	}

	private void internalInit() {
		zoomValue = 50;
		zoomMinValue = 0;
		zoomMaxValue = 100;
		init();
	}

	private void internalInitUi() {
		setLayout(new MigLayout("flowy,fill,ins 0"));
		setPreferredSize(new Dimension(500, 300));
		setUndecorated(true);

		ImageIcon imgIcon = I18N.getImageIcon("icon.options");
		panel = new BackgroundPanel(imgIcon.getImage(), BackgroundPanel.ACTUAL);
		panel.setLayout(new MigLayout("fill,wrap 1,ins 20", "[]20[grow]"));
		panel.setBorder(SwingUtil.getBorderDefault());

		if (zoom) {
			// zoom
			JLabel lbZoom = new JLabel(I18N.getMsgColon("msg.common.zoom"));
			JSlider zoomSlider = new JSlider(JSlider.HORIZONTAL, zoomMinValue,zoomMaxValue, zoomValue);
			zoomSlider.setOpaque(false);
			zoomSlider.setMajorTickSpacing(10);
			zoomSlider.setMinorTickSpacing(5);
			zoomSlider.setPaintTicks(true);
			zoomSlider.addChangeListener(this);

			panel.add(lbZoom);
			panel.add(zoomSlider, "growx");
		}

		initUi();

		panel.add(getCloseButton(), "span 2,pushy 200,al left bottom");

		add(panel, "grow");
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider slider = (JSlider) e.getSource();
		if (!slider.getValueIsAdjusting()) {
			int val = slider.getValue();
			zoom(val);
		}
	}

	public int getZoomMinValue() {
		return zoomMinValue;
	}

	public void setZoomMinValue(int zoomMinValue) {
		this.zoomMinValue = zoomMinValue;
	}

	public int getZoomMaxValue() {
		return zoomMaxValue;
	}

	public void setZoomMaxValue(int zoomMaxValue) {
		this.zoomMaxValue = zoomMaxValue;
	}

	public boolean isZoom() {
		return zoom;
	}

	public void setZoom(boolean zoom) {
		this.zoom = zoom;
	}
}
