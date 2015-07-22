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

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

import storybook.SbConstants;
import storybook.SbConstants.BookKey;
import storybook.model.hbn.entity.Internal;
import storybook.toolkit.BookUtil;
import storybook.toolkit.I18N;
import storybook.ui.MainFrame;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class ReadingOptionsDialog extends AbstractOptionsDialog {

	private final String CN_FONT_SIZE = "FontSizeSlider";

	private int fontSize;

	public ReadingOptionsDialog(MainFrame mainFrame) {
		super(mainFrame);
	}

	public ReadingOptionsDialog(MainFrame mainFrame, boolean hasZoom) {
		super(mainFrame, hasZoom);
	}

	@Override
	public void init() {
		setZoomMinValue(10);
		setZoomMaxValue(200);

		try {
			Internal internal = BookUtil.get(mainFrame,
					BookKey.READING_ZOOM, SbConstants.DEFAULT_READING_ZOOM);
			zoomValue = internal.getIntegerValue();
			internal = BookUtil.get(mainFrame,
					BookKey.READING_FONT_SIZE,
					SbConstants.DEFAULT_READING_FONT_SIZE);
			fontSize = internal.getIntegerValue();
		} catch (Exception e) {
			e.printStackTrace();
			zoomValue = SbConstants.DEFAULT_READING_ZOOM;
			fontSize = SbConstants.DEFAULT_READING_FONT_SIZE;
		}
	}

	@Override
	public void initUi() {
		// font size
		JLabel lbFontSize = new JLabel(I18N.getMsgColon("msg.common.font.size"));
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 6, 40, fontSize);
		slider.setName(CN_FONT_SIZE);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(5);
		slider.setOpaque(false);
		slider.setPaintTicks(true);
		slider.addChangeListener(this);

		// layout
		panel.add(lbFontSize);
		panel.add(slider, "growx");
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Component comp = (Component) e.getSource();
		if (CN_FONT_SIZE.equals(comp.getName())) {
			JSlider slider = (JSlider) e.getSource();
			if (!slider.getValueIsAdjusting()) {
				int val = slider.getValue();
				mainFrame.getBookController().readingSetFontSize(val);
				BookUtil.store(mainFrame,
						BookKey.READING_FONT_SIZE, val);
				return;
			}
		}
		super.stateChanged(e);
	}

	@Override
	protected void zoom(int val) {
		BookUtil.store(mainFrame, BookKey.READING_ZOOM, val);
		mainFrame.getBookController().readingSetZoom(val);
	}
}
