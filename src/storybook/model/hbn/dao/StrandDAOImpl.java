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

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.NonUniqueResultException;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;

public class StrandDAOImpl extends SbGenericDAOImpl<Strand, Long> implements
		StrandDAO {

	public StrandDAOImpl() {
		super();
	}

	public StrandDAOImpl(Session session) {
		super(session);
	}

	public long countByDate(Date date, Strand strand) {
		// select count(id) from SCENE
		// where date='2008-03-17' and strand_id=1
		Criteria crit = session.createCriteria(Scene.class);
		crit.add(Restrictions.eq("sceneTs", new Timestamp(date.getTime())));
		crit.add(Restrictions.eq("strand", strand));
		crit.setProjection(Projections.rowCount());
		Long count = (Long) crit.uniqueResult();
		return count;
	}

	@SuppressWarnings("unchecked")
	public List<Strand> findAllOrderBySort() {
		Criteria crit = session.createCriteria(Strand.class);
		crit.addOrder(Order.asc("sort"));
		return crit.list();
	}

	public void orderStrands() {
		int i = 0;
		for (Strand s : findAllOrderBySort()) {
			s.setSort(i);
			session.update(s);
			++i;
		}
	}

	public void orderUpStrand(Strand strand) {
		if (strand.getSort() == 0) {
			// already on top
			return;
		}

		session.refresh(strand);
		int strandSort = strand.getSort();

		Criteria crit = session.createCriteria(Strand.class);
		crit.add(Restrictions.eq("sort", strand.getSort() - 1));
		Strand upper = (Strand) crit.uniqueResult();

		int upperSort = upper.getSort();

		strand.setSort(upperSort);
		upper.setSort(strandSort);

		session.update(strand);
		session.update(upper);
	}

	public void orderDownStrand(Strand strand) {
		session.refresh(strand);
		int strandSort = strand.getSort();

		Criteria crit = session.createCriteria(Strand.class);
		crit.add(Restrictions.eq("sort", strand.getSort() + 1));
		Strand lower = (Strand) crit.uniqueResult();
		if (lower == null) {
			// already on bottom
			return;
		}

		int lowerSort = lower.getSort();

		strand.setSort(lowerSort);
		lower.setSort(strandSort);

		session.update(strand);
		session.update(lower);
	}

	@SuppressWarnings("unchecked")
	public List<Scene> findScenes(Strand strand) {
		Query query = session
				.createQuery("select s from Scene as s left join s.chapter as ch"
						+ " where s.strand=:strand"
						+ " order by ch.chapterno, s.sceneno");
		query.setEntity("strand", strand);
		List<Scene> ret = (List<Scene>) query.list();
		return ret;
	}

	public int getNextSort() {
		return getMaxSort() + 1;
	}

	public int getMaxSort() {
		Query query = session.createQuery("select max(sort) from Strand");
		Integer ret = (Integer) query.uniqueResult();
		return ret;
	}

	@SuppressWarnings("unchecked")
	public boolean checkIfNumberExists(AbstractEntity entity) {
		try {
			Strand newStrand = (Strand) entity;
			Integer newSort = newStrand.getSort();

			if (!entity.isTransient()) {
				// update
				StrandDAOImpl dao = new StrandDAOImpl(session);
				Strand oldStrand = dao.find(entity.getId());
				Integer oldSort = oldStrand.getSort();
				Criteria crit = session.createCriteria(Strand.class);
				crit.add(Restrictions.eq("sort", newSort));
				List<Strand> strands = (List<Strand>) crit.list();
				Vector<Integer> numbers = new Vector<Integer>();
				for (Strand strand : strands) {
					numbers.add(strand.getSort());
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
			Criteria crit = session.createCriteria(Strand.class);
			crit.add(Restrictions.eq("sort", newSort));
			List<Strand> strands = (List<Strand>) crit.list();
			if (strands.size() > 0) {
				return false;
			}

			return true;
		} catch (NonUniqueResultException e) {
			e.printStackTrace();
			return true;
		}
	}

}
