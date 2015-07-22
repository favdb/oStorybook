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
package storybook.ui.memoria;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JSlider;

public class DateSlider extends JSlider
	implements ComponentListener {

	private List<Date> dates = new ArrayList<>();
	@SuppressWarnings("unchecked")
	private final Hashtable<Integer, JLabel> labelTable = new Hashtable();
	private int startDateIndex = 0;
	private int numberOfTickers = 1;
	private int value = -1;

	public DateSlider(int paramInt) {
		super(paramInt);
		addComponentListener(this);
	}

	public void setDates(ArrayList<Date> paramArrayList) {
		this.dates = paramArrayList;
		refresh();
	}

	public void setDate(Date paramDate) {
		if (paramDate == null) {
			return;
		}
		refresh();
		int i = getIndex(paramDate);
		if ((i > -1) && (i <= getMaximum())) {
			setValue(i);
		} else {
			i = -1;
			int j = 0;
			for (Date localDate : this.dates) {
				if (localDate.compareTo(paramDate) == 0) {
					i = j;
				}
				j++;
			}
			this.startDateIndex = (i - this.numberOfTickers + 1);
			if (this.startDateIndex < 0) {
				this.startDateIndex = 0;
				this.value = i;
			} else {
				this.value = this.numberOfTickers;
			}
			refresh();
			this.value = -1;
		}
	}

	private int getIndex(Date paramDate) {
		Dictionary localDictionary = getLabelTable();
		Enumeration localEnumeration = localDictionary.keys();
		int i = -1;
		while (localEnumeration.hasMoreElements()) {
			int j = ((Integer) localEnumeration.nextElement()).intValue();
			JLabel localJLabel = (JLabel) localDictionary.get(Integer.valueOf(j));
			if (localJLabel.getText().compareTo(paramDate.toString()) == 0) {
				i = j;
				break;
			}
		}
		return i;
	}

	public Date getDate() {
		return (Date) this.dates.get(this.startDateIndex + getValue());
	}

	public boolean isIncrementAvailable() {
		return this.startDateIndex + 1 <= this.dates.size() - this.numberOfTickers;
	}

	public void inc() {
		if (isIncrementAvailable()) {
			this.startDateIndex += 1;
		}
	}

	public boolean isDecrementAvailable() {
		return this.startDateIndex - 1 >= 0;
	}

	public void dec() {
		if (isDecrementAvailable()) {
			this.startDateIndex -= 1;
		}
	}

	public void refresh() {
		refresh(null);
	}

	public void refresh(Boolean paramBoolean) {
		int i = this.startDateIndex + this.numberOfTickers;
		if (i > this.dates.size()) {
			i = this.dates.size();
			this.startDateIndex = (i - this.numberOfTickers);
		}
		if (this.startDateIndex < 0) {
			this.startDateIndex = 0;
		}
		int j = 0;
		for (Date localDate : this.dates.subList(this.startDateIndex, i)) {
			this.labelTable.put(new Integer(j), new JLabel(localDate.toString()));
			j++;
		}
		setMinimum(0);
		setMaximum(j - 1);
		if (paramBoolean != null) {
			if (paramBoolean.booleanValue()) {
				setValue(getValue() - 1);
			} else {
				setValue(getValue() + 1);
			}
		}
		if (this.value != -1) {
			setValue(this.value);
		}
		setLabelTable(this.labelTable);
		setPaintTrack(true);
		setMinorTickSpacing(1);
		setMajorTickSpacing(2);
		setPaintTicks(true);
		setPaintLabels(true);
		setSnapToTicks(true);
		repaint();
	}

	public int getNumberOfTickers() {
		return this.numberOfTickers;
	}

	public void setNumberOfTickers(int paramInt) {
		this.numberOfTickers = paramInt;
	}

	@Override
	public void componentResized(ComponentEvent paramComponentEvent) {
		this.numberOfTickers = (getWidth() / 100);
		setNumberOfTickers(this.numberOfTickers);
		Date localDate = getDate();
		setDate(localDate);
	}

	@Override
	public void componentMoved(ComponentEvent paramComponentEvent) {
	}

	@Override
	public void componentShown(ComponentEvent paramComponentEvent) {
	}

	@Override
	public void componentHidden(ComponentEvent paramComponentEvent) {
	}
}