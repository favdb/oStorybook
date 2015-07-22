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

package storybook.toolkit;

import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;

public class Period {
	private Date startDate;
	private Date endDate;

	public Period(Date start, Date end) {
		this.startDate = start;
		this.endDate = end;
	}

	public boolean isOverlapping(Period p) {
		return this.getStartDate().compareTo(p.getEndDate()) < 0
				&& this.getEndDate().compareTo(p.getStartDate()) > 0;
	}

	public boolean isInside(Date date) {
		if (date.compareTo(startDate) == 0) {
			return true;
		}
		if (date.compareTo(endDate) == 0) {
			return true;
		}
		return date.after(startDate) && date.before(endDate);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Period)) {
			return false;
		}
		Period p = (Period) o;
		return this.getStartDate().compareTo(p.getStartDate()) == 0
				&& this.getEndDate().compareTo(p.getEndDate()) == 0;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + getStartDate().hashCode();
		hash = hash * 31 + getEndDate().hashCode();
		return hash;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public boolean isValid() {
		if (startDate == null || endDate == null) {
			return false;
		}
		return true;
	}

	public String getShortString() {
		return getString(FastDateFormat.SHORT);
	}

	public String getString(int dateFormat) {
		if (!isValid()) {
			return I18N.getMsg("msg.common.invalid.period");
		}
		String startStr = FastDateFormat.getDateInstance(dateFormat).format(
				startDate);
		if (startDate.equals(endDate)) {
			return startStr;
		}
		String endStr = FastDateFormat.getDateInstance(dateFormat).format(
				endDate);
		return startStr + " - " + endStr;
	}

	@Override
	public String toString() {
		return getString(FastDateFormat.LONG);
	}
}
