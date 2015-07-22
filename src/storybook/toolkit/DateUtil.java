/*
 Storybook: Scene-based software for novelists and authors.
 Copyright (C) 2008-2009 Martin Mustun

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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import storybook.SbConstants.PreferenceKey;
import storybook.model.hbn.entity.Preference;

public class DateUtil {

    public static String getNiceDates(List<Date> dates) {
	DateFormat formatter = I18N.getLongDateFormatter();
	List<String> dateList = new ArrayList<String>();
	for (Date date : dates) {
	    dateList.add(formatter.format(date));
	}
	return StringUtils.join(dateList, ", ");
    }

    public static String calendarToString(Calendar cal) {
	SimpleDateFormat formatter = new SimpleDateFormat(
		"E yyyy.MM.dd 'at' hh:mm:ss a zzz");
	return formatter.format(cal.getTime());
    }

    public static Timestamp addTimeFromDate(Date date, Date time) {
	Calendar calTime = Calendar.getInstance();
	calTime.setTime(time);
	int h = calTime.get(Calendar.HOUR_OF_DAY);
	int m = calTime.get(Calendar.MINUTE);
	int s = calTime.get(Calendar.SECOND);
	date = DateUtils.setHours(date, h);
	date = DateUtils.setMinutes(date, m);
	date = DateUtils.setSeconds(date, s);
	return new Timestamp(date.getTime());
    }

    public static Date getZeroTimeDate() {
	Calendar cal = Calendar.getInstance();
	cal.set(Calendar.HOUR_OF_DAY, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);
	cal.set(Calendar.MILLISECOND, 0);
	return cal.getTime();
    }

    public static boolean isZeroTimeDate(Date date) {
	Calendar cal = Calendar.getInstance();
	cal.setTime(date);
	if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0
		&& cal.get(Calendar.SECOND) == 0) {
	    return true;
	}
	return false;
    }

    public static Date getZeroTimeDate(Date date) {
	if (date == null) {
	    return null;
	}
	Calendar cal = Calendar.getInstance();
	cal.setTime(date);
	cal.set(Calendar.HOUR_OF_DAY, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);
	cal.set(Calendar.MILLISECOND, 0);
	return cal.getTime();
    }

    public static int calculateDaysBetween(Date d1, Date d2) {
	return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static void expandDates(List<Date> dates) {
	expandDates(dates, 1, 1);
    }

    public static void expandDates(List<Date> dates, int count) {
	expandDates(dates, count, count);
    }

    public static void expandDates(List<Date> dates, int countPast,
	    int countFuture) {
	expandDatesToPast(dates, countPast);
	expandDatesToFuture(dates, countFuture);
    }

    public static void expandDatesToFuture(List<Date> dates) {
	expandDatesToFuture(dates, 1);
    }

    public static void expandDatesToFuture(List<Date> dates, int count) {
	dates.removeAll(Collections.singletonList(null));
	if (dates.isEmpty()) {
	    return;
	}
	for (int i = 0; i < count; ++i) {
	    Date lastDate = Collections.max(dates);
	    if (lastDate == null) {
		return;
	    }
	    lastDate = new Date(DateUtils.addDays(lastDate, 1).getTime());
	    dates.add(lastDate);
	}
    }

    public static void expandDatesToPast(List<Date> dates) {
	expandDatesToPast(dates, 1);
    }

    public static void expandDatesToPast(List<Date> dates, int count) {
	if (dates.isEmpty()) {
	    return;
	}
	dates.removeAll(Collections.singletonList(null));
	for (int i = 0; i < count; ++i) {
	    Date firstDate = Collections.min(dates);
	    firstDate = new Date(DateUtils.addDays(firstDate, -1).getTime());
	    dates.add(firstDate);
	}
    }

    public static String convertDifferenceToString(long difference) {
	String retour = "+";
	// convert as seconds
	difference = difference / 1000;
	long seconds = difference % 60;
	long minutes = (difference % 3600) / 60;
	long hours = difference / 3600;
	long days = hours / 24;
	hours = hours - days * 24;

	if (days != 0) {
	    retour += days + " " + I18N.getMsg("msg.days") + " ";
	}
	if (hours != 0) {
	    retour += hours + " " + I18N.getMsg("msg.hours") + " ";
	}
	if (minutes != 0) {
	    retour += minutes + " " + I18N.getMsg("msg.minutes") + " ";
	}
	if (seconds != 0) {
	    retour += seconds + " " + I18N.getMsg("msg.seconds") + " ";
	}
	return retour;
    }

    public static Date stringToDate(String str) {
	Date d = null;
	try {
	    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	    ParsePosition pos = new ParsePosition(0);
	    d = formatter.parse(str, pos);
	} catch (RuntimeException e) {
	}
	return d;
    }
    
    public static String dateToString(Date date) {
	SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	return(formatter.format(date));
    }
    public static String simpleDateToString(Date date) {
		Preference prefDateFormat = PrefUtil.get(PreferenceKey.DATEFORMAT, "MM-dd-yyyy");
	SimpleDateFormat formatter = new SimpleDateFormat(prefDateFormat.getStringValue());
	return(formatter.format(date));
    }
}
