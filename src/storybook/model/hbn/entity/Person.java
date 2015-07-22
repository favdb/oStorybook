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

import java.awt.Color;
import java.util.Date;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import storybook.toolkit.swing.ColorUtil;

/**
 * @hibernate.class
 *   table="PERSON"
 */
public class Person extends AbstractEntity implements Comparable<Person> {

	private Gender gender;
	private String firstname;
	private String lastname;
	private String abbreviation;
	private Date birthday;
	private Date dayofdeath;
	private String occupation;
	private String description;
	private Integer color;
	private String notes;
	private Category category;
	private List<Attribute> attributes;

	public Person() {
		super();
	}

	public Person(Gender gender, String firstname, String lastname,
			String abbreviation, Date birthday, Date dayofdeath,
			String occupation, String description, Integer color, String notes,
			Category category, List<Attribute> attributes) {
		this.gender = gender;
		this.firstname = firstname;
		this.lastname = lastname;
		this.abbreviation = abbreviation;
		this.birthday = birthday;
		this.dayofdeath = dayofdeath;
		this.occupation = occupation;
		this.description = description;
		this.color = color;
		this.notes = notes;
		this.category = category;
		this.attributes = attributes;
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

	// cascade: none,all,save-update,delete
	/**
	 * @hibernate.many-to-one
	 * column="gender_id"
	 * cascade="none"
	 */
	public Gender getGender() {
		return this.gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	/**
	 * @hibernate.property
	 */
	public String getFirstname() {
		return this.firstname == null ? "" : this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @hibernate.property
	 */
	public String getLastname() {
		return this.lastname == null ? "" : this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFullName() {
		return getFirstname() + " " + getLastname();
	}

	public String getFullNameAbbr() {
		return getFirstname() + " " + getLastname() + " [" + getAbbreviation()
				+ "]";
	}

	/**
	 * @hibernate.property
	 */
	public String getAbbreviation() {
		return this.abbreviation == null ? "" : this.abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	/**
	 * @hibernate.property
	 */
	public Date getBirthday() {
		return this.birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	/**
	 * @hibernate.property
	 */
	public Date getDayofdeath() {
		return this.dayofdeath;
	}

	public void setDayofdeath(Date dayofdeath) {
		this.dayofdeath = dayofdeath;
	}

	/**
	 * @hibernate.property
	 */
	public String getOccupation() {
		return this.occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	/**
	 * @hibernate.property
	 */
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @hibernate.property
	 */
	public Integer getColor() {
		return this.color;
	}

	public Color getJColor() {
		if (color == null) {
			return null;
		}
		return new Color(color);
	}

	public String getHTMLColor() {
		return ColorUtil.getHTMLName(getJColor());
	}

	public void setColor(Integer color) {
		this.color = color;
	}

	public void setJColor(Color color) {
		if (color == null) {
			this.color = null;
			return;
		}
		this.color = color.getRGB();
	}

	/**
	 * @hibernate.property
	 */
	public String getNotes() {
		return this.notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @hibernate.many-to-one
	 *   column="category_id"
	 *   cascade="none"
	 *   lazy="false"
	 */
	public Category getCategory() {
		return this.category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		if (isTransient()) {
//			return I18N.getMsg("msg.common.person") + " [" + getTransientId()
//					+ "]";
			return "";
		}
		return getFullNameAbbr();
	}

	@Override
	public String getAbbr(){
		return abbreviation;
	}

	@Override
	public Icon getIcon() {
		if (gender != null) {
			return gender.getIcon();
		}
		return new ImageIcon();
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		Person test = (Person) obj;
		boolean ret = true;
		ret = ret && equalsStringNullValue(abbreviation, test.getAbbreviation());
		ret = ret && equalsStringNullValue(firstname, test.getFirstname());
		ret = ret && equalsStringNullValue(lastname, test.getLastname());
		ret = ret && equalsObjectNullValue(gender, test.getGender());
		ret = ret && equalsDateNullValue(birthday, test.getBirthday());
		ret = ret && equalsDateNullValue(dayofdeath, test.getDayofdeath());
		ret = ret && equalsIntegerNullValue(color, test.getColor());
		ret = ret && equalsObjectNullValue(category, test.getCategory());
		ret = ret && equalsStringNullValue(description, test.getDescription());
		ret = ret && equalsStringNullValue(notes, test.getNotes());
		ret = ret && equalsListNullValue(attributes, test.getAttributes());
		return ret;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + (abbreviation != null ? abbreviation.hashCode() : 0);
		hash = hash * 31 + (firstname != null ? firstname.hashCode() : 0);
		hash = hash * 31 + (lastname != null ? lastname.hashCode() : 0);
		hash = hash * 31 + (gender != null ? gender.hashCode() : 0);
		hash = hash * 31 + (birthday != null ? birthday.hashCode() : 0);
		hash = hash * 31 + (dayofdeath != null ? dayofdeath.hashCode() : 0);
		hash = hash * 31 + (color != null ? color.hashCode() : 0);
		hash = hash * 31 + (category != null ? category.hashCode() : 0);
		hash = hash * 31 + (description != null ? description.hashCode() : 0);
		hash = hash * 31 + (notes != null ? notes.hashCode() : 0);
		hash = hash * 31 + (attributes != null ? getListHashCode(attributes) : 0);
		return hash;
	}

	@Override
	public int compareTo(Person o) {
		if (category == null && o == null) {
			return 0;
		}
		if (category != null && o.getCategory() == null) {
			return -1;
		}
		if (o.getCategory() != null && category == null) {
			return -1;
		}
		int cmp = category.getSort().compareTo(o.getCategory().getSort());
		if (cmp == 0) {
			return getFullName().compareTo(o.getFullName());
		}
		return cmp;
	}
}
