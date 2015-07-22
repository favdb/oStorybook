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

package storybook.toolkit.swing.splash;

import java.awt.Color;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import storybook.toolkit.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.interfaces.IPaintable;

import net.miginfocom.swing.MigLayout;
import storybook.SbApp;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class HourglassSplash extends JDialog implements IPaintable {
	private String text;
	private JLabel lbText;
	private JLabel lbProgress;

	public HourglassSplash(String text) {
		this.text = text;
		init();
		initUi();
	}

	public HourglassSplash(Frame owner, String text) {
		this.text = text;
		init();
		initUi();
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("ins 0,fill,center,flowy"));
		setUndecorated(true);
		setAlwaysOnTop(true);

		JPanel panel = new JPanel(new MigLayout("ins 20,fill,center,flowy"));
		panel.setBackground(Color.white);
		panel.setBorder(SwingUtil.getBorderDefault());

		JLabel lbHg = new JLabel(I18N.getIcon("icon.large.hourglass"));
		lbText = new JLabel(text);

		panel.add(lbHg, "al center,gap bottom 10");
		panel.add(lbText);
		lbProgress=new JLabel("...");
		lbProgress.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		add(panel);

		SwingUtil.showDialog(this, null);
		toFront();
		repaint(0);
	}
	public void setText(String s) {
		lbText.setText(s);
		SbApp.trace("progress : "+s);
		repaint(0);
	}
	public void setProgress(String s) {
		lbProgress.setText(s);
		repaint(0);
	}
}
