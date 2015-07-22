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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

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
public class MemoriaOptionsDialog extends AbstractOptionsDialog implements
		ItemListener {

	private boolean balloon;
	private JRadioButton rbBalloon;

	public MemoriaOptionsDialog(MainFrame mainFrame) {
		super(mainFrame, false);
	}

	@Override
	public void init() {
		try {
			Internal internal = BookUtil.get(mainFrame,
					BookKey.MEMORIA_BALLOON,
					SbConstants.DEFAULT_MEMORIA_BALLOON);
			balloon = internal.getBooleanValue();
		} catch (Exception e) {
			e.printStackTrace();
			balloon = SbConstants.DEFAULT_MEMORIA_BALLOON;
		}
	}

	@Override
	public void initUi() {
		// balloon or tree layout
		JLabel lbPres = new JLabel(I18N.getMsgColon("msg.graph.presentation"));

		ButtonGroup bgPresentation = new ButtonGroup();
		rbBalloon = new JRadioButton(I18N.getMsg("msg.graph.pres.balloon"));
		if (balloon) {
			rbBalloon.setSelected(true);
		}
		bgPresentation.add(rbBalloon);
		JRadioButton rbTree = new JRadioButton(
				I18N.getMsg("msg.graph.pres.tree"));
		bgPresentation.add(rbTree);
		if (!balloon) {
			rbTree.setSelected(true);
		}

		// layout
		panel.add(lbPres);
		panel.add(rbBalloon, "split 2");
		panel.add(rbTree);

		rbTree.addItemListener(this);
		rbBalloon.addItemListener(this);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		boolean val = rbBalloon.isSelected();
		mainFrame.getBookController().memoriaSetBalloonLayout(val);
		BookUtil.store(mainFrame, BookKey.MEMORIA_BALLOON, val);
	}
}
