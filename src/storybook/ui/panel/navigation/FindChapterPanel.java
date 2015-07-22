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

package storybook.ui.panel.navigation;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import storybook.SbConstants.ViewName;
import storybook.controller.BookController;
import storybook.model.EntityUtil;
import storybook.model.handler.ChapterEntityHandler;
import storybook.model.hbn.entity.Chapter;
import storybook.toolkit.I18N;
import storybook.toolkit.swing.IconButton;
import storybook.toolkit.swing.SwingUtil;
import storybook.toolkit.swing.panel.ViewsRadioButtonPanel;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;

import net.miginfocom.swing.MigLayout;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class FindChapterPanel extends AbstractPanel {

	private JComboBox chapterCombo;
	private ViewsRadioButtonPanel viewsRbPanel;

	public FindChapterPanel(MainFrame mainFrame) {
		super(mainFrame);
		initAll();
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("fillx,wrap 2", "[]10[grow]", "[]10[]10[]"));

		JLabel lbChapter = new JLabel(I18N.getMsgColon("msg.common.chapter"));

		chapterCombo = new JComboBox();
		ChapterEntityHandler handler = new ChapterEntityHandler(mainFrame);
		Chapter chapter = new Chapter();
		EntityUtil.fillEntityCombo(mainFrame, chapterCombo, handler, chapter,
				false, false);
		SwingUtil.setMaxWidth(chapterCombo, 200);

		IconButton btPrev = new IconButton("icon.small.previous",
				getPreviousAction());
		btPrev.setSize20x20();

		IconButton btNext = new IconButton("icon.small.next", getNextAction());
		btNext.setSize20x20();

		JLabel lbShow = new JLabel(I18N.getMsgColon("msg.navigation.show.in"));
		viewsRbPanel = new ViewsRadioButtonPanel(mainFrame);

		JButton btFind = new JButton();
		btFind.setAction(getFindAction());
		btFind.setText(I18N.getMsg("msg.common.find"));
		btFind.setIcon(I18N.getIcon("icon.small.search"));
		SwingUtil.addEnterAction(btFind, getFindAction());

		// layout
		add(lbChapter);
		add(chapterCombo, "growx,span 2,split 3");
		add(btPrev);
		add(btNext);
		add(lbShow, "top");
		add(viewsRbPanel);
		add(btFind, "span,right");
	}

	private AbstractAction getPreviousAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				int index = chapterCombo.getSelectedIndex();
				--index;
				if (index == -1) {
					index = 0;
				}
				chapterCombo.setSelectedIndex(index);
				scrollToChapter();
			}
		};
	}

	private AbstractAction getNextAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				int index = chapterCombo.getSelectedIndex();
				++index;
				if (index == chapterCombo.getItemCount()) {
					index = chapterCombo.getItemCount() - 1;
				}
				chapterCombo.setSelectedIndex(index);
				scrollToChapter();
			}
		};
	}

	private AbstractAction getFindAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				scrollToChapter();
			}
		};
	}

	private void scrollToChapter() {
		Chapter chapter = (Chapter) chapterCombo.getSelectedItem();
		BookController ctrl = mainFrame.getBookController();
		if (viewsRbPanel.isChronoSelected()) {
			mainFrame.showView(ViewName.CHRONO);
			ctrl.chronoShowEntity(chapter);
			return;
		}
		if (viewsRbPanel.isBookSelected()) {
			mainFrame.showView(ViewName.BOOK);
			ctrl.bookShowEntity(chapter);
			return;
		}
		if (viewsRbPanel.isManageSelected()) {
			mainFrame.showView(ViewName.MANAGE);
			ctrl.manageShowEntity(chapter);
			return;
		}
	}
}
