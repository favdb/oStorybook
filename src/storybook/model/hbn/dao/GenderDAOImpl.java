/*
Storybook: Open Source software for novelists and authors.
Copyright (C) 2008 - 2012 Martin Mustun, 2015 FaVdB

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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import storybook.model.hbn.entity.Gender;
import storybook.model.hbn.entity.Person;

public class GenderDAOImpl extends SbGenericDAOImpl<Gender, Long> implements
		GenderDAO {

	public GenderDAOImpl() {
		super();
	}

	public GenderDAOImpl(Session session) {
		super(session);
	}

	public Gender findMale() {
		return (Gender) session.get(Gender.class, 1L);
	}

	public Gender findFemale() {
		return (Gender) session.get(Gender.class, 2L);
	}

	@SuppressWarnings("unchecked")
	public List<Person> findPersons(Gender gender) {
		Criteria crit = session.createCriteria(Person.class);
		crit.add(Restrictions.eq("gender", gender));
		crit.addOrder(Order.asc("firstname"));
		crit.addOrder(Order.asc("lastname"));
		List<Person> persons = (List<Person>) crit.list();
		return persons;
	}

}
