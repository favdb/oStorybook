/*
 * SbApp: Open Source software for novelists and authors.
 * Original idea 2008 - 2012 Martin Mustun
 * Copyrigth (C) Favdb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package storybook.ui.chart.jfreechart;

import storybook.toolkit.I18N;
import storybook.toolkit.swing.ColorUtil;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;
import org.apache.commons.lang.time.DateUtils;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

/**
 *
 * @author favdb
 */
public class ChartUtil {

	@SuppressWarnings("unchecked")
	public static TreeSet<Date> correctDates(TreeSet<Date> paramTreeSet) {
		TreeSet localTreeSet = new TreeSet();
		Date localDate1 = (Date) paramTreeSet.first();
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.setTime(localDate1);
		int i = localCalendar1.get(1);
		if (i > 1900) {
			return paramTreeSet;
		}
		for (Date localDate2 : paramTreeSet) {
			Calendar localCalendar2 = Calendar.getInstance();
			localCalendar2.setTime(localDate2);
			int j = localCalendar2.get(1);
			localDate2 = DateUtils.addYears(localDate2, 1900 - j);
			localTreeSet.add(localDate2);
		}
		return localTreeSet;
	}

	public static void hideLegend(CategoryPlot paramCategoryPlot) {
		paramCategoryPlot.setFixedLegendItems(new LegendItemCollection());
	}

	public static void hideDomainAxis(CategoryPlot paramCategoryPlot) {
		CategoryAxis localCategoryAxis = paramCategoryPlot.getDomainAxis();
		localCategoryAxis.setTickMarksVisible(false);
		localCategoryAxis.setTickLabelsVisible(false);
	}

	public static void hideRangeAxis(CategoryPlot paramCategoryPlot) {
		ValueAxis localValueAxis = paramCategoryPlot.getRangeAxis();
		localValueAxis.setTickMarksVisible(false);
		localValueAxis.setTickLabelsVisible(false);
	}

	public static ItemLabelPosition getNiceItemLabelPosition() {
		ItemLabelAnchor localItemLabelAnchor = ItemLabelAnchor.OUTSIDE6;
		TextAnchor localTextAnchor1 = TextAnchor.BOTTOM_LEFT;
		TextAnchor localTextAnchor2 = TextAnchor.TOP_LEFT;
		double d = Math.toRadians(270.0D);
		return new ItemLabelPosition(localItemLabelAnchor, localTextAnchor1, localTextAnchor2, d);
	}

	public static void setNiceSeriesColors(CategoryDataset paramCategoryDataset, AbstractRenderer paramAbstractRenderer) {
		Color[] arrayOfColor = ColorUtil.getDarkColors(ColorUtil.getPastelColors(), 0.35D);
		for (int i = 0; i < paramCategoryDataset.getRowCount(); i++) {
			Color localColor = arrayOfColor[(i % arrayOfColor.length)];
			paramAbstractRenderer.setSeriesPaint(i, localColor);
		}
	}

	public static Marker getAverageMarker(double paramDouble) {
		ValueMarker localValueMarker = new ValueMarker(paramDouble, Color.red, new BasicStroke(0.3F));
		localValueMarker.setLabel(I18N.getMsg("msg.common.average"));
		localValueMarker.setLabelFont(new Font("SansSerif", 2, 11));
		localValueMarker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
		localValueMarker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
		return localValueMarker;
	}

	public static Marker getDateMarker(Date paramDate) {
		return getDateMarker(paramDate, paramDate.toString());
	}

	public static Marker getDateMarker(Date paramDate, String paramString) {
		return getDateMarker(paramDate, paramString, false);
	}

	public static Marker getDateMarker(Date paramDate, String paramString, boolean paramBoolean) {
		double d = paramDate.getTime();
		ValueMarker localValueMarker = new ValueMarker(d, Color.red, new BasicStroke(0.3F));
		localValueMarker.setLabel(paramString);
		localValueMarker.setLabelFont(new Font("SansSerif", 2, 11));
		localValueMarker.setLabelAnchor(RectangleAnchor.BOTTOM);
		if (paramBoolean) {
			localValueMarker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
		} else {
			localValueMarker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
		}
		return localValueMarker;
	}

	public static Marker getDateIntervalMarker(Date paramDate1, Date paramDate2) {
		String str = paramDate1.toString() + " - " + paramDate2.toString();
		return getDateIntervalMarker(paramDate1, paramDate2, str);
	}

	public static Marker getDateIntervalMarker(Date paramDate1, Date paramDate2, String paramString) {
		double d1 = paramDate1.getTime();
		double d2 = paramDate2.getTime();
		BasicStroke localBasicStroke = new BasicStroke(0.3F);
		IntervalMarker localIntervalMarker = new IntervalMarker(d1, d2, Color.pink, localBasicStroke, Color.black, localBasicStroke, 0.5F);
		localIntervalMarker.setLabel(paramString);
		localIntervalMarker.setLabelAnchor(RectangleAnchor.BOTTOM);
		localIntervalMarker.setLabelTextAnchor(TextAnchor.BOTTOM_CENTER);
		return localIntervalMarker;
	}
}