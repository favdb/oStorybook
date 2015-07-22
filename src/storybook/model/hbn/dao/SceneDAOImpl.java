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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;
import storybook.model.state.SceneState;
import storybook.toolkit.LangUtil;
import storybook.toolkit.comparator.DateComparator;

public class SceneDAOImpl extends SbGenericDAOImpl<Scene, Long> implements
		SceneDAO {

	public SceneDAOImpl() {
		super();
	}

	public SceneDAOImpl(Session session) {
		super(session);
	}

	public Date findFirstDate() {
		List<Date> dates = findDistinctDates();
		if (dates.isEmpty()) {
			return new Date();
		}
		return dates.get(0);
	}

	public Date findLastDate() {
		List<Date> dates = findDistinctDates();
		if (dates.isEmpty()) {
			return new Date();
		}
		return dates.get(dates.size() - 1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Scene> findAll() {
		Query query = session
				.createQuery("select s from Scene as s left join s.chapter as ch"
						+ " order by ch.chapterno, s.sceneno");
		List<Scene> ret = (List<Scene>) query.list();
		return ret;
	}


	@SuppressWarnings("unchecked")
	public List<Scene> findByPart(Part part) {
		Query query = session.createQuery("select scene from Scene as scene"
				+ " inner join scene.chapter as chapter"
				+ " inner join chapter.part as part"
				+ " where chapter.part=:part"
				+ " order by part.number, chapter.chapterno, scene.sceneno");
		query.setEntity("part", part);
		List<Scene> ret = query.list();
		return ret;
	}

	public List<Scene> findBySceneState(SceneState state) {
		return findBySceneState(state.getNumber());
	}

	@SuppressWarnings("unchecked")
	public List<Scene> findBySceneState(int state) {
		if (state == 6) {
			// pseudo state "work in progress"
			return findStateInProgress();
		}
		if (state == 7) {
			// pseudo state "all"
			return findAll();
		}
		Criteria crit = session.createCriteria(Scene.class);
		crit.add(Restrictions.eq("status", state));
		List<Scene> scenes = (List<Scene>) crit.list();
		return scenes;
	}

	@SuppressWarnings("unchecked")
	public List<Scene> findStateInProgress() {
		Criteria crit = session.createCriteria(Scene.class);
		crit.add(Restrictions.ne("status", 5));
		List<Scene> scenes = (List<Scene>) crit.list();
		return scenes;
	}

	@SuppressWarnings("unchecked")
	public List<Scene> findScenesWithRelativeSceneId() {
		Criteria crit = session.createCriteria(Scene.class);
		crit.add(Restrictions.isNotNull("relativeSceneId"));
		List<Scene> scenes = (List<Scene>) crit.list();
		return scenes;
	}

	@SuppressWarnings("unchecked")
	public List<Scene> findScenesWithRelativeSceneId(Scene scene) {
		Criteria crit = session.createCriteria(Scene.class);
		crit.add(Restrictions.eq("relativeSceneId", scene.getId()));
		List<Scene> scenes = (List<Scene>) crit.list();
		return scenes;
	}

	public long countByPerson(Person person) {
		Query query = session.createQuery(
				"select count(s) from Scene as s"
				+ " join s.persons as p"
				+ " where p=:person");
		query.setEntity("person", person);
		return (Long) query.uniqueResult();
	}

	public long countByLocation(Location location) {
		Query query = session.createQuery(
				"select count(s) from Scene as s"
				+ " join s.locations as l"
				+ " where l=:location");
		query.setEntity("location", location);
		return (Long) query.uniqueResult();
	}

	public Scene getRelativeScene(Scene scene) {
		Scene s = (Scene) session.get(Scene.class, scene.getRelativeSceneId());
		return s;
	}

	public List<Date> findDistinctDates() {
		return findDistinctDates(null);
	}

	@SuppressWarnings("unchecked")
	public List<Date> findDistinctDatesByStrand(Strand strand) {
		// native SQL
		String sql = "select distinct cast(s.scene_ts as DATE), s.scene_ts"
				+ " from scene s"
				+ " where s.strand_id=:strand_id"
				+ " order by s.scene_ts";
		Query query = session.createSQLQuery(sql);
		query.setParameter("strand_id", strand.getId());
		List<Object> ret = query.list();
		List<Date> dates = new ArrayList<Date>();
		for (int i = 0; i < ret.size(); ++i) {
			Object[] oa = (Object[]) ret.get(i);
			dates.add((Date) oa[0]);
		}
		dates = LangUtil.removeNullAndDuplicates(dates);
		Collections.sort(dates, new DateComparator());
		return dates;
	}

	@SuppressWarnings("unchecked")
	public List<Date> findDistinctDates(Part part) {
		// native SQL
		String sql;
		if (part != null) {
			PartDAOImpl partDAO = new PartDAOImpl(session);
			String partIds = partDAO.getPartsIds(part);
			sql = "select distinct cast(s.scene_ts as DATE), s.scene_ts"
					+ " from scene s"
					+ " left outer join chapter cha on s.chapter_id = cha.id"
					+ " where cha.part_id in (" + partIds + ")"
					+ " or s.chapter_id is null"
					+ " order by s.scene_ts";
		} else {
			sql = "select distinct cast(s.scene_ts as DATE), s.scene_ts"
					+ " from scene s"
					+ " order by s.scene_ts";
		}
		Query query = session.createSQLQuery(sql);
		List<Object> ret = query.list();
		List<Date> dates = new ArrayList<Date>();
		for (int i = 0; i < ret.size(); ++i) {
			Object[] oa = (Object[]) ret.get(i);
			dates.add((Date) oa[0]);
		}

		// add dates of scenes with a relative date
		List<Scene> scenes = findScenesWithRelativeSceneId();
		for (Scene scene : scenes) {
			if (part != null) {
				Chapter chapter = scene.getChapter();
				if (chapter != null) {
					if (!chapter.getPart().getId().equals(part.getId())) {
						continue;
					}
				}
			}
			Scene relativeScene = (Scene) session.get(Scene.class, scene.getRelativeSceneId());
			dates.add(scene.getRelativeDate(relativeScene));
		}
		dates = LangUtil.removeNullAndDuplicates(dates);
		Collections.sort(dates, new DateComparator());
		return dates;
	}

	@SuppressWarnings("unchecked")
	public List<Scene> findByDate(Date date) {
		if (date == null) {
			return new ArrayList<Scene>();
		}
		Query query = session.createQuery("select s from Scene as s"
				+ " where s.sceneTs between :tsStart and :tsEnd"
				+ " order by s.sceneTs, s.sceneno");
		Timestamp tsStart = new Timestamp(date.getTime());
		date = DateUtils.addDays(date, 1);
		date = DateUtils.addMilliseconds(date, -1);
		Timestamp tsEnd = new Timestamp(date.getTime());
		query.setTimestamp("tsStart", tsStart);
		query.setTimestamp("tsEnd", tsEnd);
		return (List<Scene>) query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Scene> findByPersonLink(Person person) {
		Query query = session.createQuery("select s from Scene as s"
				+ " join s.persons as p"
				+ " where p=:person");
		query.setParameter("person", person);
		List<Scene> ret = query.list();
		return ret;
	}

	@SuppressWarnings("unchecked")
	public List<Scene> findByLocationLink(Location location) {
		Query query = session.createQuery("select s from Scene as s"
				+ " join s.locations as l"
				+ " where l=:location");
		query.setParameter("location", location);
		List<Scene> ret = query.list();
		return ret;
	}

	@SuppressWarnings("unchecked")
	public List<Scene> findByItemLink(Item item) {
		Query query = session.createQuery("select s from Scene as s"
				+ " join s.items as l"
				+ " where l=:item");
		query.setParameter("item", item);
		List<Scene> ret = query.list();
		return ret;
	}

	@SuppressWarnings("unchecked")
	public List<Scene> findByStrandLink(Strand strand) {
		Query query = session.createQuery("select s from Scene as s"
				+ " join s.strands as st"
				+ " where st=:strand");
		query.setParameter("strand", strand);
		List<Scene> ret = query.list();
		return ret;
	}

	@SuppressWarnings("unchecked")
	public List<Scene> findByStrand(Strand strand) {
		Query query = session.createQuery("select s from Scene as s"
				+ " where s.strand=:strand");
		query.setParameter("strand", strand);
		List<Scene> ret = query.list();
		return ret;
	}

	@SuppressWarnings("unchecked")
	public List<Scene> findByStrandAndDate(Strand strand, Date date) {
		if (date == null) {
			return new ArrayList<Scene>();
		}
		// OLD: Query query = session
		// .createQuery("from Scene s where s.sceneTs = :sceneTs and s.strand=:strand order by s.sceneTs");
		Query query = session
				.createQuery("select s from Scene as s"
						+ " left join s.chapter as ch"
						+ " where s.sceneTs between :tsStart and :tsEnd and s.strand=:strand"
						+ " order by s.sceneTs, ch.chapterno, s.sceneno");
		Timestamp tsStart = new Timestamp(date.getTime());
		Date date2 = DateUtils.addDays(date, 1);
		date2 = DateUtils.addMilliseconds(date2, -1);
		Timestamp tsEnd = new Timestamp(date2.getTime());
		query.setTimestamp("tsStart", tsStart);
		query.setTimestamp("tsEnd", tsEnd);
		query.setEntity("strand", strand);
		List<Scene> ret = query.list();
		// find scenes with relative date
		List<Scene> scenes = findScenesWithRelativeSceneId();
		for (Scene scene : scenes) {
			if (!scene.getStrand().getId().equals(strand.getId())) {
				continue;
			}
			Scene relativeScene = (Scene) session.get(Scene.class,
					scene.getRelativeSceneId());
			Date sceneDate = scene.getRelativeDate(relativeScene);
			if (sceneDate == null) {
				continue;
			}
			if (sceneDate.compareTo(date) != 0) {
				continue;
			}
			ret.add(scene);
		}
		return ret;
	}

	public List<Scene> findByChapter(Chapter chapter) {
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		return dao.findScenes(chapter);
	}

	@SuppressWarnings("unchecked")
	public List<Scene> findScenesToExport() {
		Query query = session.createQuery("select scene from Scene as scene"
				+ " inner join scene.chapter as chapter"
				+ " inner join chapter.part as part"
				+ " order by part.number, chapter.chapterno, scene.sceneno");
		List<Scene> ret = query.list();
		return ret;
	}

//	public int getMaxScenesByDate(Date date) {
//		StrandDAOImpl strandDao = new StrandDAOImpl(session);
//		List<Strand> strandList = strandDao.findAll();
//		int max = 0;
//		for (Strand strand : strandList) {
//			Criteria crit = session.createCriteria(Scene.class);
//			crit.setProjection(Projections.rowCount());
//			crit.add(Restrictions.eq("strand", strand));
//			crit.add(Restrictions.eq("date", date));
//			int count = (Integer) crit.uniqueResult();
//			if (count > max) {
//				max = count;
//			}
//		}
//		return max;
//	}
}
