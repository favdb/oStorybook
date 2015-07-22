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

package storybook.ui.dialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import storybook.controller.BookController;
import storybook.model.EntityUtil;
import storybook.model.handler.ChapterEntityHandler;
import storybook.model.handler.PartEntityHandler;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Part;
import storybook.toolkit.I18N;
import storybook.ui.MainFrame;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class CreateChaptersDialog extends AbstractDialog {

	private JTextField tfQuantity;
	private JComboBox partCombo;

	public CreateChaptersDialog(MainFrame mainFrame) {
		super(mainFrame);
		initAll();
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		super.initUi();
		setLayout(new MigLayout("wrap 2,fill", "", "[][][][grow,fill][]"));
		setTitle(I18N.getMsg("msg.generate.chapters"));
		setPreferredSize(new Dimension(320, 180));

		JLabel lbText = new JLabel(I18N.getMsg("msg.generate.chapters.text"));

		JLabel lbPart = new JLabel(I18N.getMsgColon("msg.common.part"));
		partCombo = new JComboBox();
		PartEntityHandler handler = new PartEntityHandler(mainFrame);
		Part part = new Part();
		EntityUtil.fillEntityCombo(mainFrame, partCombo, handler, part, false,
				false);

		JLabel lbQuantity = new JLabel(I18N.getMsgColon("msg.common.quantity"));
		tfQuantity = new JTextField();
		tfQuantity.setColumns(10);

		// layout
		add(lbText, "span,gapbottom 10");
		add(lbQuantity);
		add(tfQuantity);
		add(lbPart);
		add(partCombo);
		add(new JLabel(), "span,grow");
		add(getOkButton(), "sg,span,split 2,right");
		add(getCancelButton(), "sg");
	}

	@Override
	protected AbstractAction getOkAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				createChapters();
				getThis().dispose();
			}
		};
	}

	private void createChapters() {
		int quant = 0;
		try {
			quant = Integer.parseInt(tfQuantity.getText());
		} catch (NumberFormatException e) {
			// ignore
		}
		if (quant < 1 || quant > 20) {
			return;
		}

		ChapterEntityHandler handler = new ChapterEntityHandler(mainFrame);
		Part part = (Part) partCombo.getSelectedItem();
		for (int i = 0; i < quant; ++i) {
			Chapter ch = (Chapter) handler.createNewEntity();
			ch.setPart(part);
			BookController ctrl = mainFrame.getBookController();
			ctrl.newChapter(ch);
		}
	}

	private CreateChaptersDialog getThis() {
		return this;
	}
}
