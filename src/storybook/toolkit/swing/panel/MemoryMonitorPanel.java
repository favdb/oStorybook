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

package storybook.toolkit.swing.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import storybook.toolkit.swing.SwingUtil;
import storybook.ui.interfaces.IPaintable;

import net.miginfocom.swing.MigLayout;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class MemoryMonitorPanel extends JPanel implements IPaintable,
		ActionListener {

	private Timer timer;
	private JLabel lbText;

	public MemoryMonitorPanel() {
		init();
		initUi();
	}

	@Override
	public void init() {
		timer = new Timer(2000, this);
		timer.start();
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("flowx,ins 1"));
		lbText = new JLabel("X", JLabel.CENTER);
		lbText.setPreferredSize(new Dimension(200, 16));
		add(lbText);
	}

	@Override
	protected void paintComponent(Graphics g) {
		long free = Runtime.getRuntime().freeMemory();
		long total = Runtime.getRuntime().totalMemory();
		long used = total - free;
		long max = Runtime.getRuntime().maxMemory();

		lbText.setText(SwingUtil.getMemoryUsageSimpleHr());
		lbText.setToolTipText(SwingUtil.getMemoryUsageHr());

		int w = getWidth();
		int h = getHeight();

		int xUsed = (int) (w * used / max);
		int xFree = (int) (w * free / max);
		int xMax = (int) (w * max / max);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(new Color(0x8090ce));
		g2d.fillRect(0, 0, xUsed, h);

		g2d.setColor(new Color(0x7dce7e));
		g2d.fillRect(xUsed, 0, xUsed + xFree, h);

		g2d.setColor(new Color(0xb2e8b3));
		g2d.fillRect(xUsed + xFree, 0, xUsed + xFree + xMax, h);

		g2d.setColor(Color.gray);
		g2d.drawRect(0,0,w-1,h-1);

		// g2d.setPaint(new GradientPaint(0, 0, Color.white, 0, height, new
		// Color(
		// 0x0072B6)));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
}
