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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.persistence.NonUniqueResultException;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Part;

public class PartDAOImpl extends SbGenericDAOImpl<Part, Long> implements
		PartDAO {

	public PartDAOImpl() {
		super();
	}

	public PartDAOImpl(Session session) {
		super(session);
	}

	public Part findFirst() {
		List<Part> ret = findAll();
		return ret.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Part> findAll() {
		Query query = session.createQuery("from Part order by number");
		List<Part> ret = (List<Part>) query.list();
		return ret;
	}

	@SuppressWarnings("unchecked")
	public List<Part> findAllRoots() {
		String sql = "select id"
				+ " from part"
				+ " where part_id is NULL"
				+ " order by number";
		Query query = session.createSQLQuery(sql);
		List<Object> qret = query.list();
		List<Part> parts = new ArrayList<Part>();
		PartDAOImpl dao = new PartDAOImpl(session);
		for (int i = 0; i < qret.size(); ++i) {
			Object oa = (Object) qret.get(i);
			long subId = ((BigInteger) oa).longValue();
			Part subPart = dao.find(subId);
			parts.add(subPart);
		}
		return parts;
	}

	@SuppressWarnings("unchecked")
	public List<Chapter> findChapters(Part part) {
		Criteria crit = session.createCriteria(Chapter.class);
		crit.add(Restrictions.eq("part", part));
		crit.addOrder(Order.asc("chapterno"));
		List<Chapter> chapters = (List<Chapter>) crit.list();
		return chapters;
	}

	public List<Chapter> findAllChapters(Part part) {
		List<Chapter> chapters = findChapters(part);
		List<Part> subparts = getParts(part);
		for (Part subpart : subparts) {
			chapters.addAll(findAllChapters(subpart));
		}
		return chapters;
	}

	public int getNextPartNumber() {
		return getMaxPartNumber() + 1;
	}

	public int getMaxPartNumber() {
		Query query = session.createQuery("select max(number) from Part");
		Integer ret = (Integer) query.uniqueResult();
		return ret;
	}

	@SuppressWarnings("unchecked")
	public boolean checkIfNumberExists(AbstractEntity entity) {
		try {
			Part newPart = (Part) entity;
			Integer newNumber = newPart.getNumber();

			if (!newPart.getId().equals(new Long(-1))) {
				// update
				PartDAOImpl dao = new PartDAOImpl(session);
				Part oldPart = dao.find(newPart.getId());
				Integer oldNumber = oldPart.getNumber();
				Criteria crit = session.createCriteria(Part.class);
				crit.add(Restrictions.eq("number", newNumber));
				List<Part> parts = (List<Part>) crit.list();
				Vector<Integer> numbers = new Vector<Integer>();
				for (Part part : parts) {
					if (AbstractEntity.equalsObjectNullValue(part.getSuperpart(), newPart.getSuperpart())) {
					    numbers.add(part.getNumber());
					}
				}
				if (newNumber.equals(oldNumber)) {
					numbers.remove(newNumber);
				}
				if (numbers.size() > 0) {
					return false;
				}
				return true;
			}

			// new
			Criteria crit = session.createCriteria(Part.class);
			crit.add(Restrictions.eq("number", newNumber));
			List<Part> parts = (List<Part>) crit.list();
			if (parts.size() > 0) {
				return false;
			}

			return true;
		} catch (NonUniqueResultException e) {
			e.printStackTrace();
			return true;
		}
	}
	
	public String getPartsIds(Part part)
	{
		String ret = "" + part.getId();
		String sql = "select id"
				+ " from part"
				+ " where part_id=" + part.getId();
		Query query = session.createSQLQuery(sql);
		@SuppressWarnings("unchecked")
		List<Object> qret = query.list();
		PartDAOImpl dao = new PartDAOImpl(session);
		for (int i = 0; i < qret.size(); ++i) {
			Object oa = (Object) qret.get(i);
			long subId = ((BigInteger) oa).longValue();
			Part subPart = dao.find(subId);
			String subs = getPartsIds(subPart);
			if (!subs.isEmpty())
			{
				ret += "," + subs;
			}
		}
		return ret;
	}
	
	public List<Part> getParts(Part part)
	{
		String sql = "select id"
				+ " from part"
				+ " where part_id=" + part.getId();
		Query query = session.createSQLQuery(sql);
		@SuppressWarnings("unchecked")
		List<Object> qret = query.list();
		List<Part> parts = new ArrayList<Part>();
		PartDAOImpl dao = new PartDAOImpl(session);
		for (int i = 0; i < qret.size(); ++i) {
			Object oa = (Object) qret.get(i);
			long subId = ((BigInteger) oa).longValue();
			Part subPart = dao.find(subId);
			parts.add(subPart);
		}
		return parts;
	}
	
	public int getOverallSize() {
		List<Part> parts = findAll();
		int ret = 0;
		for (Part part : parts) {
			List<Chapter> chapters = findChapters(part);
			for (Chapter chapter : chapters) {
			   ret += chapter.getObjectiveChars();
			}
		}
		return ret;
	}
	
	public int getOverallSize(Part headpart) {
		int ret = 0;
		// get all children's size
		List<Part> parts = getParts(headpart);
		for (Part part : parts) {
			ret += getOverallSize(part);
		}
		
		// add all self-included chapters size
		List<Chapter> chapters = findChapters(headpart);
		for (Chapter chapter : chapters) {
			// TODO replace with real size
	        ret += chapter.getObjectiveChars();
		}
		return ret;
	}
	
	public int getObjectiveSize(Part headpart) {
		int headObjective = 0;
		int partsObjective = 0;
		
		if ((headpart.getObjectiveChars() != null) && (!headpart.getObjectiveChars().equals(new Integer(0)))) {
			headObjective = headpart.getObjectiveChars();
		}
		// get all children's objective size
		List<Part> parts = getParts(headpart);
		for (Part part : parts) {
			partsObjective += getObjectiveSize(part);
		}
		int ret = Math.max(headObjective, partsObjective);
		
		// add all self-included chapters size
		List<Chapter> chapters = findChapters(headpart);
		for (Chapter chapter : chapters) {
			ret += chapter.getObjectiveChars();
		}
		return ret;
	}
}
