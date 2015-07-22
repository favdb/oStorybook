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
import java.util.Vector;

import javax.persistence.NonUniqueResultException;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import storybook.SbApp;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Category;
import storybook.model.hbn.entity.Person;

public class CategoryDAOImpl extends SbGenericDAOImpl<Category, Long> implements
		CategoryDAO {

	public CategoryDAOImpl() {
		super();
	}

	public CategoryDAOImpl(Session session) {
		super(session);
	}

	public Category findCentral() {
		return (Category) session.get(Category.class, 1L);
	}

	public Category findMinor() {
		return (Category) session.get(Category.class, 2L);
	}

	@SuppressWarnings("unchecked")
	public List<Category> findAllOrderBySort() {
		Criteria crit = session.createCriteria(Category.class);
		crit.addOrder(Order.asc("sort"));
		return crit.list();
	}

	public void orderCategories() {
		int i = 0;
		for (Category c : findAllOrderBySort()) {
			c.setSort(i);
			session.update(c);
			++i;
		}
	}

	public void orderUpCatgory(Category category) {
		if (category.getSort() == 0) {
			// already on top
			return;
		}

		session.refresh(category);
		int categorySort = category.getSort();

		Criteria crit = session.createCriteria(Category.class);
		crit.add(Restrictions.eq("sort", category.getSort() - 1));
		Category upper = (Category) crit.uniqueResult();

		int upperSort = upper.getSort();

		category.setSort(upperSort);
		upper.setSort(categorySort);

		session.update(category);
		session.update(upper);
	}

	public void orderDownCategory(Category category) {
		session.refresh(category);
		int categorySort = category.getSort();
		SbApp.trace("CategoryDAOImpl.orderDownCategory(): categorySort:"+categorySort);

		Criteria crit = session.createCriteria(Category.class);
		crit.add(Restrictions.eq("sort", category.getSort() + 1));
		Category lower = (Category) crit.uniqueResult();
		if (lower == null) {
			// already on bottom
			return;
		}

		int lowerSort = lower.getSort();

		category.setSort(lowerSort);
		lower.setSort(categorySort);

		session.update(category);
		session.update(lower);
	}

	@SuppressWarnings("unchecked")
	public List<Person> findPersons(Category category) {
		Criteria crit = session.createCriteria(Person.class);
		crit.add(Restrictions.eq("category", category));
		List<Person> persons = (List<Person>) crit.list();
		return persons;
	}

	public int getNextSort() {
		return getMaxSort() + 1;
	}

	public int getMaxSort() {
		Query query = session.createQuery("select max(sort) from Category");
		Integer ret = (Integer) query.uniqueResult();
		return ret;
	}

	@SuppressWarnings("unchecked")
	public boolean checkIfNumberExists(AbstractEntity entity) {
		try {
			Category newCategory = (Category) entity;
			Integer newSort = newCategory.getSort();

			if (!entity.isTransient()) {
				// update
				CategoryDAOImpl dao = new CategoryDAOImpl(session);
				Category oldChapter = dao.find(entity.getId());
				Integer oldSort = oldChapter.getSort();
				Criteria crit = session.createCriteria(Category.class);
				crit.add(Restrictions.eq("sort", newSort));
				List<Category> categories = (List<Category>) crit.list();
				Vector<Integer> numbers = new Vector<Integer>();
				for (Category category : categories) {
					numbers.add(category.getSort());
				}
				if (newSort.equals(oldSort)) {
					numbers.remove(newSort);
				}
				if (numbers.size() > 0) {
					return false;
				}
				return true;
			}

			// new
			Criteria crit = session.createCriteria(Category.class);
			crit.add(Restrictions.eq("sort", newSort));
			List<Category> categories = (List<Category>) crit.list();
			if (categories.size() > 0) {
				return false;
			}

			return true;
		} catch (NonUniqueResultException e) {
			e.printStackTrace();
			return true;
		}
	}

}
