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

import java.util.Objects;


/**
 * @hibernate.class
 *   table="TAG"
 *   discriminator-value="-1"
 * @hibernate.discriminator
 *   type="integer"
 *   column="type"
 */
public abstract class AbstractTag extends AbstractEntity {

	public static final int TYPE_TAG = 0;
	public static final int TYPE_ITEM = 1;
	//TODO utiliser les objets Tag pour identifier des liens d'objet à objet de même nature
	// exemples:
	// objet lié à un autre objet
	// lieu lié à un autre lieu
	// etiquette liée à une autre étiquette
	public static final int TYPE_LINK = 10;
	// dans ce cas :
	// - category recevra le nom du type d'objet à lier (Person, Item, Location, Tag)
	// - name recevra l'Id du premier objet suivi de l'Id du second objet, séparateur |
	//TODO faire l'écran de saisie correspondant
	//TODO faire le pannel Associations
	//TODO faire le print correspondant

	protected Integer type;
	private String category;
	private String name;
	private String description;
	private String notes;

	public AbstractTag() {
	}

	public AbstractTag(Integer type, String category, String name, String description, String notes) {
		this.type = type;
		this.category = category;
		this.name = name;
		this.description = description;
		this.notes = notes;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Tag getTag() {
		if (type != TYPE_TAG) {
			return null;
		}
		return (Tag) this;
	}

	public Item getItem() {
		if (type != TYPE_ITEM) {
			return null;
		}
		return (Item) this;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = (category == null ? "" : category);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		AbstractTag test = (AbstractTag) obj;
		if (!Objects.equals(type, test.type)) {
			return false;
		}
		boolean ret = true;
		ret = ret && equalsStringNullValue(name, test.getName());
		ret = ret && equalsStringNullValue(category, test.getCategory());
		ret = ret && equalsStringNullValue(description, test.getDescription());
		ret = ret && equalsStringNullValue(notes, test.getNotes());
		return ret;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + (type != null ? type.hashCode() : 0);
		hash = hash * 31 + (name != null ? name.hashCode() : 0);
		hash = hash * 31 + (category != null ? category.hashCode() : 0);
		hash = hash * 31 + (description != null ? description.hashCode() : 0);
		hash = hash * 31 + (notes != null ? notes.hashCode() : 0);
		return hash;
	}
}
