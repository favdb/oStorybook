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
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import storybook.model.hbn.entity.Tag;

public class TagDAOImpl extends SbGenericDAOImpl<Tag, Long> implements TagDAO {

	public TagDAOImpl() {
		super();
	}

	public TagDAOImpl(Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	public List<String> findCategories() {
		Query query = session
				.createQuery("select distinct(t.category) from Tag as t order by t.category");
		return (List<String>) query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Tag> findByCategory(String category) {
		Criteria crit = session.createCriteria(Tag.class);
		crit.add(Restrictions.eq("category", category));
		crit.addOrder(Order.asc("name"));
		List<Tag> tags = (List<Tag>) crit.list();
		return tags;
	}
}
