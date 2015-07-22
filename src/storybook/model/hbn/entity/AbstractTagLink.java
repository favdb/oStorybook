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

import storybook.toolkit.I18N;
import storybook.toolkit.Period;


/**
 * @hibernate.class
 *   table="TAG_LINK"
 *   discriminator-value="-1"
 * @hibernate.discriminator
 *   type="integer"
 *   column="type"
 */
public abstract class AbstractTagLink extends AbstractEntity {

	public static final int TYPE_TAG = 0;
	public static final int TYPE_ITEM = 1;

	protected Integer type;
	private Scene startScene;
	private Scene endScene;
	private Person person;
	private Location location;

	public AbstractTagLink() {
	}

	public AbstractTagLink(Integer type, Scene startScene, Scene endScene, Person person, Location location) {
		this.type = type;
		this.startScene = startScene;
		this.endScene = endScene;
		this.person = person;
		this.location = location;
	}

	/**
	 * @return the Id
	 * @hibernate.id
	 *   column="ID"
	 *   generator-class="increment"
	 *   unsaved-value="null"
	 */
	@Override
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the type
	 * @hibernate.property
	 *   insert="false"
	 *   update="false"
	 */
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * @return the starting scene
	 * @hibernate.many-to-one
	 *   column="start_scene_id"
	 */
	public Scene getStartScene() {
		return this.startScene;
	}

	public boolean hasStartScene() {
		return startScene != null;
	}

	public void setStartScene(Scene startScene) {
		this.startScene = startScene;
	}

	/**
	 * @return the ending scene
	 * @hibernate.many-to-one
	 *   column="end_scene_id"
	 */
	public Scene getEndScene() {
		return this.endScene;
	}

	public boolean hasEndScene() {
		return endScene != null;
	}

	public void setEndScene(Scene endScene) {
		this.endScene = endScene;
	}

	/**
	 * @return the person
	 * @hibernate.many-to-one
	 *   column="character_id"
	 */
	public Person getPerson() {
		return this.person;
	}

	public boolean hasPerson() {
		return person != null;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public void setPerson() {
		this.person = null;
	}

	/**
	 * @return the location
	 * @hibernate.many-to-one
	 *   column="location_id"
	 */
	public Location getLocation() {
		return this.location;
	}

	public boolean hasLocation() {
		return location != null;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public boolean hasOnlyScene() {
		return startScene != null && endScene == null && person == null && location == null;
	}

	public boolean hasPeriod() {
		return (this.getStartScene() != null && this.getEndScene() != null);
	}

	public Period getPeriod() {
		if (hasPeriod()) {
			return new Period(getStartScene().getSceneTs(), getEndScene()
					.getSceneTs());
		}
		if (hasStartScene()) {
			return new Period(getStartScene().getSceneTs(), getStartScene()
					.getSceneTs());
		}
		return null;
	}

	public boolean hasLocationOrPerson() {
		return hasLocation() || hasPerson();
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		if (hasPerson()) {
			buf.append(person.toString());
		}
		if (hasLocation()) {
			if (buf.length() > 0) {
				buf.append(", ");
			}
			buf.append(location.toString());
		}
		if (hasOnlyScene()) {
			buf.append(" ");
			buf.append(I18N.getMsg("msg.common.scene"));
			buf.append(" ");
			buf.append(startScene.getChapterSceneNo(false));
		} else {
			if (hasStartScene()) {
				if (buf.length() > 0) {
					buf.append(",");
				}
				buf.append(" ");
				buf.append(I18N.getMsg("msg.common.scene"));
				buf.append(" ");
				buf.append(startScene.getChapterSceneNo(false));
			}
			if (hasPeriod()) {
				buf.append(" - ");
			}
			if (hasEndScene()) {
				buf.append(endScene.getChapterSceneNo(false));
			}
			if (hasPeriod()) {
				buf.append(" (");
				buf.append(getPeriod().getShortString());
				buf.append(")");
			}
		}
		return buf.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		AbstractTagLink test = (AbstractTagLink) obj;
		if (type != test.getType()) {
			return false;
		}
		boolean ret = true;
		ret = ret
				&& equalsLongNullValue(startScene == null ? null
						: startScene.id, test.getStartScene() == null ? null
						: test.getStartScene().getId());
		ret = ret
				&& equalsLongNullValue(endScene == null ? null : endScene.id,
						test.getEndScene() == null ? null : test.getEndScene()
								.getId());
		ret = ret
				&& equalsLongNullValue(person == null ? null : person.id,
						test.getPerson() == null ? null : test.getPerson()
								.getId());
		ret = ret
				&& equalsLongNullValue(location == null ? null : location.id,
						test.getLocation() == null ? null : test.getLocation()
								.getId());
		return ret;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + type.hashCode();
		hash = hash * 31 + (startScene != null ? startScene.hashCode() : 0);
		hash = hash * 31 + (endScene != null ? endScene.hashCode() : 0);
		hash = hash * 31 + (person != null ? person.hashCode() : 0);
		hash = hash * 31 + (location != null ? location.hashCode() : 0);
		return hash;
	}
}
