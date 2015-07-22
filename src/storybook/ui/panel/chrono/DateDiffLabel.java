/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008 - 2011 Martin Mustun

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

package storybook.ui.panel.chrono;

import java.util.Date;

import javax.swing.JLabel;

import org.apache.commons.lang3.time.FastDateFormat;
import storybook.toolkit.DateUtil;
import storybook.toolkit.I18N;

@SuppressWarnings("serial")
public class DateDiffLabel extends JLabel {

	private Date date1;
	private Date date2;

	public DateDiffLabel(Date date1, Date date2) {
		this(date1, date2, false);
	}

	public DateDiffLabel(Date date1, Date date2, boolean isVertical) {
		super("", JLabel.CENTER);
		this.date1 = date1;
		this.date2 = date2;
		String text = I18N.getMsgColon("msg.pref.datediff") + " " + getDays();
		FastDateFormat fdf = FastDateFormat
				.getDateInstance(FastDateFormat.SHORT);
		String dateStr1 = fdf.format(date1);
		String dateStr2 = fdf.format(date2);
		String text2 = "(" + dateStr1 + " - " + dateStr2 + ")";
		setText(getDays() + " " + text2);
		setToolTipText("<html>" + text + "<br>" + text2);
		setIcon(I18N.getIcon("icon.small.datediff"));
	}

	public final int getDays() {
		return DateUtil.calculateDaysBetween(date1, date2);
	}
}
