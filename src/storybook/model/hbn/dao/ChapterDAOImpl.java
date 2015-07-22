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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.DateUtil;
import storybook.toolkit.LangUtil;
import storybook.toolkit.comparator.DateComparator;

public class ChapterDAOImpl extends SbGenericDAOImpl<Chapter, Long> implements
		ChapterDAO {

	public ChapterDAOImpl() {
		super();
	}

	public ChapterDAOImpl(Session session) {
		super(session);
	}

	@Override
	public List<Chapter> findAll() {
		return findAll(null);
	}

	@SuppressWarnings("unchecked")
	public List<Chapter> findAll(Part part) {
		StringBuffer buf = new StringBuffer("from Chapter");
		if (part != null) {
			buf.append(" where part=:part");
		}
		buf.append(" order by chapterno");
		Query query = session.createQuery(buf.toString());
		if (part != null) {
			query.setEntity("part", part);
		}
		List<Chapter> ret = (List<Chapter>) query.list();
		return ret;
	}

	public List<Chapter> findAllOrderByChapterNoAndSceneNo() {
		return findAllOrderByChapterNoAndSceneNo(null);
	}

	@SuppressWarnings("unchecked")
	public List<Chapter> findAllOrderByChapterNoAndSceneNo(Part part) {
		Criteria crit = session.createCriteria(Chapter.class);
		crit.addOrder(Order.asc("chapterno"));
		if (part != null) {
			crit.add(Restrictions.eq("part", part));
		}
		return crit.list();
	}

	public List<Scene> findUnassignedScenes() {
		return findScenes(null);
	}

	public Scene findFirstScene(Chapter chapter) {
		List<Scene> scenes = findScenes(chapter);
		if (scenes == null || scenes.isEmpty()) {
			return null;
		}
		return scenes.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<Scene> findScenes(Chapter chapter) {
		Criteria crit = session.createCriteria(Scene.class);
		if (chapter != null) {
			crit.add(Restrictions.eq("chapter", chapter));
		} else {
			crit.add(Restrictions.isNull("chapter"));
		}
		crit.addOrder(Order.asc("sceneno"));
		List<Scene> scenes = (List<Scene>) crit.list();
		return scenes;
	}

	@SuppressWarnings("unchecked")
	public List<Scene> findScenesOrderByTimestamp(Chapter chapter) {
		Criteria crit = session.createCriteria(Scene.class);
		if (chapter != null) {
			crit.add(Restrictions.eq("chapter", chapter));
		} else {
			crit.add(Restrictions.isNull("chapter"));
		}
		crit.addOrder(Order.asc("sceneTs"));
		List<Scene> scenes = (List<Scene>) crit.list();
		return scenes;
	}

	@SuppressWarnings("unchecked")
	public List<Location> findLocations(Chapter chapter) {
		Query query = session
				.createQuery("select s.locations from Scene as s"
						+ " join s.chapter as ch"
						+ " where s.chapter=:chapter"
						+ " order by ch.chapterno, s.sceneno");
		query.setEntity("chapter", chapter);
		List<Location> locations = (List<Location>) query.list();
		locations = LangUtil.removeNullAndDuplicates(locations);
		return locations;
	}

	@SuppressWarnings("unchecked")
	public List<Date> findDates(Chapter chapter) {
		Query query = session
				.createQuery("select s.sceneTs from Scene as s"
						+ " join s.chapter as ch"
						+ " where s.chapter=:chapter"
						+ " order by ch.chapterno, s.sceneno");
		query.setEntity("chapter", chapter);
		List<Timestamp> tsList = (List<Timestamp>) query.list();
		List<Date> dates = new ArrayList<Date>();
		for(Timestamp ts:tsList){
			Date date=DateUtil.getZeroTimeDate(ts);
			dates.add(date);
		}
		dates = LangUtil.removeNullAndDuplicates(dates);
		Collections.sort(dates, new DateComparator());
		return dates;
	}

	public int getNextChapterNumber() {
		return getMaxChapterNumber() + 1;
	}

	public int getMaxChapterNumber() {
		Query query = session.createQuery("select max(chapterno) from Chapter");
		if (query.uniqueResult() == null) {
			return 0;
		}
		Integer ret = (Integer) query.uniqueResult();
		return ret;
	}

	@SuppressWarnings("unchecked")
	public boolean checkIfNumberExists(AbstractEntity entity) {
		try {
			Chapter newChapter = (Chapter) entity;
			Integer newChapterNo = newChapter.getChapterno();

			if (!entity.isTransient()) {
				// update
				ChapterDAOImpl dao = new ChapterDAOImpl(session);
				Chapter oldChapter = dao.find(entity.getId());
				Integer oldChapterNo = oldChapter.getChapterno();
				Criteria crit = session.createCriteria(Chapter.class);
				crit.add(Restrictions.eq("chapterno", newChapterNo));
				List<Chapter> chapters = (List<Chapter>) crit.list();
				Vector<Integer> numbers = new Vector<Integer>();
				for (Chapter chapter : chapters) {
					numbers.add(chapter.getChapterno());
				}
				if (newChapterNo.equals(oldChapterNo)) {
					numbers.remove(newChapterNo);
				}
				if (numbers.size() > 0) {
					return false;
				}
				return true;
			}

			// new
			Criteria crit = session.createCriteria(Chapter.class);
			crit.add(Restrictions.eq("chapterno", newChapterNo));
			List<Chapter> chapters = (List<Chapter>) crit.list();
			if (chapters.size() > 0) {
				return false;
			}

			return true;
		} catch (NonUniqueResultException e) {
			e.printStackTrace();
			return true;
		}
	}
}
