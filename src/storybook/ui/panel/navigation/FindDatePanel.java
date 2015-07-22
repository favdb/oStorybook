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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import storybook.SbConstants.ViewName;
import storybook.action.ScrollToStrandDateAction;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.handler.StrandEntityHandler;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.Strand;
import storybook.toolkit.I18N;
import storybook.toolkit.swing.IconButton;
import storybook.toolkit.swing.SwingUtil;
import storybook.toolkit.swing.panel.ViewsRadioButtonPanel;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;
import storybook.ui.SbView;
import storybook.ui.panel.book.BookPanel;
import storybook.ui.panel.chrono.ChronoPanel;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class FindDatePanel extends AbstractPanel implements ItemListener {

	private JComboBox strandCombo;
	private JComboBox dateCombo;
	private ViewsRadioButtonPanel viewsRbPanel;
	private JLabel lbWarning;

	public FindDatePanel(MainFrame mainFrame) {
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
		setLayout(new MigLayout("fillx,wrap 2", "[]10[grow]", "[][]10[]"));

		JLabel lbChapter = new JLabel(I18N.getMsgColon("msg.common.strand"));
		strandCombo = new JComboBox();
		StrandEntityHandler handler = new StrandEntityHandler(mainFrame);
		EntityUtil.fillEntityCombo(mainFrame, strandCombo, handler,
				new Strand(), false, false);
		strandCombo.addItemListener(this);

		JLabel lbDate = new JLabel(I18N.getMsgColon("msg.common.date"));
		dateCombo = new JComboBox();
		refreshDateCombo();

		IconButton btPrev = new IconButton("icon.small.previous",
				getPreviousAction());
		btPrev.setSize20x20();

		IconButton btNext = new IconButton("icon.small.next", getNextAction());
		btNext.setSize20x20();

		JLabel lbShow = new JLabel(I18N.getMsgColon("msg.navigation.show.in"));
		viewsRbPanel = new ViewsRadioButtonPanel(mainFrame, false);

		lbWarning = new JLabel(" ");

		JButton btFind = new JButton();
		btFind.setAction(getFindAction());
		btFind.setText(I18N.getMsg("msg.common.find"));
		btFind.setIcon(I18N.getIcon("icon.small.search"));
		SwingUtil.addEnterAction(btFind, getFindAction());

		// layout
		add(lbChapter);
		add(strandCombo, "growx");
		add(lbDate);
		add(dateCombo, "growx,split 3");
		add(btPrev);
		add(btNext);
		add(lbShow, "top");
		add(viewsRbPanel);
		add(lbWarning, "span,split 2,left,growx");
		add(btFind, "right");
	}

	@SuppressWarnings("unchecked")
	private void refreshDateCombo() {
		Strand strand = (Strand) strandCombo.getSelectedItem();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		List<Date> dates = dao.findDistinctDatesByStrand(strand);
		model.commit();
		dateCombo.removeAllItems();
		for (Date date : dates) {
			dateCombo.addItem(date);
		}
	}

	private AbstractAction getPreviousAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				int index = dateCombo.getSelectedIndex();
				--index;
				if (index == -1) {
					index = 0;
				}
				dateCombo.setSelectedIndex(index);
				scrollToStrandDate();
			}
		};
	}

	private AbstractAction getNextAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				int index = dateCombo.getSelectedIndex();
				++index;
				if (index == dateCombo.getItemCount()) {
					index = dateCombo.getItemCount() - 1;
				}
				dateCombo.setSelectedIndex(index);
				scrollToStrandDate();
			}
		};
	}

	private AbstractAction getFindAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				scrollToStrandDate();
			}
		};
	}

	private void scrollToStrandDate() {
		Strand strand = (Strand) strandCombo.getSelectedItem();
		Date date = (Date) dateCombo.getSelectedItem();
		SbView view = null;
		boolean chrono = viewsRbPanel.isChronoSelected();
		boolean book = viewsRbPanel.isBookSelected();
		if (chrono) {
			view = mainFrame.getView(ViewName.CHRONO);
		} else if (book) {
			view = mainFrame.getView(ViewName.BOOK);
		}
		if (chrono) {
			mainFrame.showView(ViewName.CHRONO);
		} else if (book) {
			mainFrame.showView(ViewName.BOOK);
		}
		AbstractPanel container = null;
		JPanel panel = null;
		if (chrono) {
			container = (ChronoPanel) view.getComponent();
			panel = ((ChronoPanel) container).getPanel();
		} else if (book) {
			container = (BookPanel) view.getComponent();
			panel = ((BookPanel) container).getPanel();
		}

		int delay = 20;
		if (!view.isLoaded()) {
			delay += 180;
		}
		if (book) {
			delay += 100;
		}
		ScrollToStrandDateAction action = new ScrollToStrandDateAction(
				container, panel, strand, date, lbWarning);
		Timer timer = new Timer(delay, action);
		timer.setRepeats(false);
		timer.start();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			refreshDateCombo();
		}
	}
}
