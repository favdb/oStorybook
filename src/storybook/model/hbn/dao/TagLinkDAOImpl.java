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
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Tag;
import storybook.model.hbn.entity.TagLink;

public class TagLinkDAOImpl extends SbGenericDAOImpl<TagLink, Long> implements
		TagLinkDAO {

	public TagLinkDAOImpl() {
		super();
	}

	public TagLinkDAOImpl(Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	public List<TagLink> findByTag(Tag tag) {
		Criteria crit = session.createCriteria(TagLink.class);
		crit.add(Restrictions.eq("tag", tag));
		List<TagLink> tagLinks = (List<TagLink>) crit.list();
		return tagLinks;
	}

	@SuppressWarnings("unchecked")
	public List<TagLink> findByScene(Scene scene) {
		Criteria crit = session.createCriteria(TagLink.class);
		crit.add(Restrictions.eq("startScene", scene));
		List<TagLink> tagLinks = (List<TagLink>) crit.list();
		return tagLinks;
	}

	@SuppressWarnings("unchecked")
	public List<TagLink> findByStartOrEndScene(Scene scene) {
		Criteria crit = session.createCriteria(TagLink.class);
		Criterion cr1 = Restrictions.eq("startScene", scene);
		Criterion cr2 = Restrictions.eq("endScene", scene);
		crit.add(Restrictions.or(cr1, cr2));
		List<TagLink> tagLinks = (List<TagLink>) crit.list();
		return tagLinks;
	}

	@SuppressWarnings("unchecked")
	public List<TagLink> findByPerson(Person person) {
		Criteria crit = session.createCriteria(TagLink.class);
		crit.add(Restrictions.eq("person", person));
		List<TagLink> tagLinks = (List<TagLink>) crit.list();
		return tagLinks;
	}

	@SuppressWarnings("unchecked")
	public List<TagLink> findByLocation(Location location) {
		Criteria crit = session.createCriteria(TagLink.class);
		crit.add(Restrictions.eq("location", location));
		List<TagLink> tagLinks = (List<TagLink>) crit.list();
		return tagLinks;
	}
}
