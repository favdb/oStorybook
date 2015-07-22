/*
Storybook: Open Source software for novelists and authors.
Copyright (C) 2015 FaVdB

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

public class Relationship extends AbstractEntity {

	private Person person1;
	private Person person2;
	String description;
	Scene startScene;
	Scene endScene;
	String notes;

	public Relationship() {
	}

	public Relationship(Person person1, Person person2, String description, Scene startScene, Scene endScene, String notes) {
		this.person1 = person1;
		this.person2 = person2;
		this.description = description;
		this.startScene = startScene;
		this.endScene = endScene;
		this.notes = notes;
	}

	@Override
	public Long getId() {return this.id;}

	public void setId(Long id) {this.id = id;}

	public Person getPerson1() {return this.person1;}

	public boolean hasPerson1() {return person1 != null;}

	public void setPerson1(Person person) {this.person1 = person;}

	public void setPerson1() {this.person1 = null;}

	public Person getPerson2() {return this.person2;}

	public boolean hasPerson2() {return person2 != null;}

	public void setPerson2(Person person) {this.person2 = person;}

	public void setPerson2() {this.person2 = null;}

	public String getDescription() {return this.description;}

	public void setDescription(String description) {this.description = description;}

	public Scene getStartScene() {return this.startScene;}

	public boolean hasStartScene() {return this.startScene != null;}

	public void setStartScene(Scene startScene) {this.startScene = startScene;}

	public Scene getEndScene() {return this.endScene;}

	public boolean hasEndScene() {return endScene != null;}

	public void setEndScene(Scene endScene) {this.endScene = endScene;}

	public boolean hasOnlyScene() {
		return startScene != null && endScene == null && person1 == null && person2 == null && description == null;
	}

	public boolean hasPeriod() {
		return (this.getStartScene() != null && this.getEndScene() != null);
	}

	public Period getPeriod() {
		if (hasPeriod()) {
			return new Period(getStartScene().getSceneTs(), getEndScene().getSceneTs());
		}
		if (hasStartScene()) {
			return new Period(getStartScene().getSceneTs(), getStartScene().getSceneTs());
		}
		return null;
	}

	public String getNotes() {return this.notes;}

	public void setNotes(String notes) {this.notes = notes;}

	@Override
	public String toString() {
		if (isTransient()) {
			return "";
		}
		StringBuilder buf = new StringBuilder();
		if (hasPerson1()) {
			buf.append(person1.toString());
		}
		if (hasPerson2()) {
			if (buf.length() > 0) {
				buf.append(", ");
			}
			buf.append(person2.toString());
		}
		if (this.description!=null) {
			if (buf.length() > 0) {
				buf.append(", ");
			}
			buf.append(description);
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
		return(this.toString().equals(obj.toString()));
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + (person1 != null ? person1.hashCode() : 0);
		hash = hash * 31 + (person2 != null ? person2.hashCode() : 0);
		hash = hash * 31 + (description != null ? description.hashCode() : 0);
		hash = hash * 31 + (startScene != null ? startScene.hashCode() : 0);
		hash = hash * 31 + (endScene != null ? endScene.hashCode() : 0);
		hash = hash * 31 + (notes != null ? notes.hashCode() : 0);
		return hash;
	}
}
