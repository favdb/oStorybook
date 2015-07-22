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

/**
 * @hibernate.subclass
 *   discriminator-value="0"
 */
public class TagLink extends AbstractTagLink {

	private Tag tag;

	public TagLink() {
	}

	public TagLink(Tag tag, Integer type, Scene startScene, Scene endScene, Person person, Location location) {
		super(type, startScene, endScene, person, location);
		this.tag = tag;
	}

	/**
	 * @return 
	 * @hibernate.many-to-one
	 *   column="tag_id"
	 *   cascade="none"
	 */
	public Tag getTag() {
		return this.tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		TagLink test = (TagLink) obj;
		boolean ret = true;
		ret = ret && equalsLongNullValue(tag.id, test.getTag().getId());
		return ret;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + tag.hashCode();
		return hash;
	}
}
