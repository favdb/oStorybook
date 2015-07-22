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

package storybook.model.hbn.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Relationship;
import storybook.model.hbn.entity.Scene;

public class RelationshipDAOImpl extends SbGenericDAOImpl<Relationship, Long> implements RelationshipDAO {

	public RelationshipDAOImpl() {
		super();
	}

	public RelationshipDAOImpl(Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	public List<Relationship> findByScene(Scene scene) {
		Criteria crit = session.createCriteria(Relationship.class);
		crit.add(Restrictions.eq("startScene", scene));
		List<Relationship> relationships = (List<Relationship>) crit.list();
		return relationships;
	}

	@SuppressWarnings("unchecked")
	public List<Relationship> findByStartOrEndScene(Scene scene) {
		Criteria crit = session.createCriteria(Relationship.class);
		Criterion cr1 = Restrictions.eq("startScene", scene);
		Criterion cr2 = Restrictions.eq("endScene", scene);
		crit.add(Restrictions.or(cr1, cr2));
		List<Relationship> relationships = (List<Relationship>) crit.list();
		return relationships;
	}

	@SuppressWarnings("unchecked")
	public List<Relationship> findByPerson(Person person) {
		Criteria crit = session.createCriteria(Relationship.class);
		Criterion cr1 = Restrictions.eq("person1", person);
		Criterion cr2 = Restrictions.eq("person2", person);
		crit.add(Restrictions.or(cr1, cr2));
		List<Relationship> relationships = (List<Relationship>) crit.list();
		return relationships;
	}

}
