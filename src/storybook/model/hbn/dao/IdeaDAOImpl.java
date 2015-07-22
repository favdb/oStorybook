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
import storybook.model.hbn.entity.Idea;

public class IdeaDAOImpl extends SbGenericDAOImpl<Idea, Long> implements
		IdeaDAO {

	public IdeaDAOImpl() {
		super();
	}

	public IdeaDAOImpl(Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	public List<String> findCategories() {
		Query query = session
				.createQuery("select distinct(i.category) from Idea as i");
		return (List<String>) query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Idea> findAllOrderByState() {
		Criteria crit = session.createCriteria(Idea.class);
		crit.addOrder(Order.asc("status"));
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public List<Idea> findByStatus(Integer status) {
		Criteria crit = session.createCriteria(Idea.class);
		crit.add(Restrictions.eq("status", status));
		List<Idea> persons = (List<Idea>) crit.list();
		return persons;
	}
}
