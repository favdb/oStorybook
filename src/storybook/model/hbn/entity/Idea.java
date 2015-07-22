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

package storybook.model.hbn.entity;

import storybook.model.state.IdeaState;
import storybook.model.state.IdeaStateModel;
import storybook.toolkit.TextUtil;
import storybook.toolkit.html.HtmlUtil;

/**
 * Ideas generated by hbm2java
 * @hibernate.class
 *   table="IDEAS"
 */
public class Idea extends AbstractEntity {

	private Integer status;
	private String note;
	private String category;

	public Idea() {
	}

	public Idea(Integer status, String note, String category) {
		this.status = status;
		this.note = note;
		this.category = category;
	}

	/**
	 * @hibernate.id
	 *   column="ID"
	 *   generator-class="increment"
	 *   unsaved-value="null"
	 */
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @hibernate.property
	 */
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setIdeaState(IdeaState state) {
		this.status = state.getNumber();
	}

	public IdeaState getIdeaState() {
		IdeaStateModel model = new IdeaStateModel();
		return (IdeaState) model.findByNumber(this.status);
	}

	/**
	 * @hibernate.property
	 */
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getNotes() {
		return getNote();
	}

	public void setNotes(String notes) {
		setNote(notes);
	}

	/**
	 * @hibernate.property
	 */
	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		if (note == null) {
			return category;
		}
		String text = HtmlUtil.htmlToText(note);
		if (category == null || category.isEmpty()) {
			return TextUtil.truncateString(text, 50);
		}
		return category + ": " + TextUtil.truncateString(text, 50);
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		Idea test = (Idea) obj;
		boolean ret = true;
		ret = ret && equalsIntegerNullValue(status, test.getStatus());
		ret = ret && equalsStringNullValue(category, test.getCategory());
		ret = ret && equalsStringNullValue(note, test.getNote());
		return ret;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + (status != null ? status.hashCode() : 0);
		hash = hash * 31 + (category != null ? category.hashCode() : 0);
		hash = hash * 31 + (note != null ? note.hashCode() : 0);
		return hash;
	}
}
