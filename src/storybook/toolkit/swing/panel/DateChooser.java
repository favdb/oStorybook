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
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang3.time.DateUtils;
import storybook.model.EntityUtil;
import storybook.toolkit.DateUtil;
import storybook.toolkit.I18N;
import storybook.toolkit.swing.IconButton;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class DateChooser extends AbstractPanel {

	private JDateChooser dateChooser;
	private JSpinner timeSpinner;
	private boolean showDateTime;

	public DateChooser(MainFrame mainFrame) {
		super(mainFrame);
		init();
		initUi();
	}

	public DateChooser(MainFrame mainFrame, boolean showDateTime) {
		super(mainFrame);
		this.showDateTime = showDateTime;
		init();
		initUi();
	}

	public boolean hasError() {
		JTextFieldDateEditor tf = (JTextFieldDateEditor) dateChooser
				.getComponent(1);
		if (tf.getForeground() == Color.red) {
			return true;
		}
		return false;
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {

	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("flowx, ins 0"));

		dateChooser = new JDateChooser();
		dateChooser.setMinimumSize(new Dimension(120, 20));

		JLabel lbTime = new JLabel(I18N.getMsgColon("msg.common.time"));

		IconButton btClearTime = new IconButton("icon.small.clear",
				getClearTimeAction());
		btClearTime.setSize20x20();

		timeSpinner = new JSpinner(new SpinnerDateModel());
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner,
				I18N.TIME_FORMAT);
		timeSpinner.setEditor(timeEditor);
		timeSpinner.setValue(DateUtil.getZeroTimeDate());
		timeSpinner.setPreferredSize(new Dimension(80, 30));

		IconButton btFirstDate = new IconButton("icon.small.first",
				getFirstDateAction());
		btFirstDate.setSize20x20();

		IconButton btPrevDay = new IconButton("icon.small.previous",
				getPrevDayAction());
		btPrevDay.setSize20x20();

		IconButton btNextDay = new IconButton("icon.small.next",
				getNextDayAction());
		btNextDay.setSize20x20();

		IconButton btLastDate = new IconButton("icon.small.last",
				getLastDateAction());
		btLastDate.setSize20x20();

		// layout
		add(dateChooser, "gapafter 10");
		add(btFirstDate);
		add(btPrevDay);
		add(btNextDay);
		add(btLastDate);
		if (showDateTime) {
			add(lbTime, "aligny center,newline,span,split 3");
			add(timeSpinner);
			add(btClearTime);
		}
	}

	public void setDate(Date date) {
		dateChooser.setDate(date);
		if (date != null) {
			timeSpinner.setValue(date);
		}
	}

	public Timestamp getTimestamp() {
		if (dateChooser.getDate() == null) {
			return null;
		}
		Date date = dateChooser.getDate();
		Date time = (Date) timeSpinner.getValue();
		return DateUtil.addTimeFromDate(date, time);
	}

	private AbstractAction getFirstDateAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Date date = EntityUtil.findFirstDate(mainFrame);
				dateChooser.setDate(date);
			}
		};
	}

	private AbstractAction getLastDateAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Date date = EntityUtil.findLastDate(mainFrame);
				dateChooser.setDate(date);
			}
		};
	}

	private AbstractAction getNextDayAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Date date;
				if (dateChooser.getDate() == null) {
					date = EntityUtil.findLastDate(mainFrame);
				} else {
					date = DateUtils.addDays(dateChooser.getDate(), 1);
				}
				dateChooser.setDate(date);
			}
		};
	}

	private AbstractAction getPrevDayAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Date date;
				if (dateChooser.getDate() == null) {
					date = EntityUtil.findFirstDate(mainFrame);
				} else {
					date = DateUtils.addDays(dateChooser.getDate(), -1);
				}
				dateChooser.setDate(date);
			}
		};
	}

	private AbstractAction getClearTimeAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				timeSpinner.setValue(DateUtil.getZeroTimeDate());
			}
		};
	}
}
