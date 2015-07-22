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

package storybook.test.model;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import storybook.model.hbn.SbSessionFactory;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.GenderDAOImpl;
import storybook.model.hbn.dao.IdeaDAOImpl;
import storybook.model.hbn.dao.InternalDAOImpl;
import storybook.model.hbn.dao.ItemDAOImpl;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.dao.StrandDAOImpl;
import storybook.model.hbn.dao.TagDAOImpl;
import storybook.model.hbn.dao.TagLinkDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Gender;
import storybook.model.hbn.entity.Idea;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

public class Test01 {

	private static SbSessionFactory sessionFactory;

	public static void main(String[] args) {
		System.out.println("start...");

		Session session;

		// DB 1
		// sessionFactory = new SbSessionFactory();
		// sessionFactory.init("/home/martin/tmp/Mitternachts Picknick");
		// session = sessionFactory.getSession();
		// sessionFactory.query(new PersonDAOImpl(session));
		// session.getTransaction().commit();

		// DB 2
		sessionFactory = new SbSessionFactory();
		sessionFactory.init("/home/martin/tmp/Demo");
		session = sessionFactory.getSession();

		// query entities
		session.beginTransaction();
		System.out.println("session.isConnected(): " + session.isConnected());
		System.out.println("session.isOpen(): " + session.isOpen());
		sessionFactory.query(new InternalDAOImpl(session));
		sessionFactory.query(new IdeaDAOImpl(session));
		sessionFactory.query(new TagDAOImpl(session));
		sessionFactory.query(new ItemDAOImpl(session));
		sessionFactory.query(new TagLinkDAOImpl(session));
		sessionFactory.query(new GenderDAOImpl(session));
		sessionFactory.query(new PersonDAOImpl(session));
		sessionFactory.query(new ChapterDAOImpl(session));
		sessionFactory.query(new PartDAOImpl(session));
		sessionFactory.query(new LocationDAOImpl(session));
		sessionFactory.query(new StrandDAOImpl(session));
		sessionFactory.query(new SceneDAOImpl(session));
		session.getTransaction().commit();

		// thread test
		TestThread thread = new TestThread(sessionFactory);
		thread.start();
		TestThread thread2 = new TestThread(sessionFactory);
		thread2.start();

		// update entities
		updatePerson();

		// create entities
		// createIdea(session);
		createPerson();

		// delete entities
		deletePerson();

		System.out.println("finished.");
	}

	private static void queryChapterDAO() {
		ChapterDAOImpl chapterDAO = new ChapterDAOImpl(
				sessionFactory.getSession());
		chapterDAO.setSessionFactory(sessionFactory.getSessionFactory());
		List<Chapter> chapters = chapterDAO.findAll();
		for (Chapter chapter : chapters) {
			System.out.println("Test03.queryChapterDAO(): chapter:"
					+ chapter.getTitle());
		}
	}

	private static void deletePerson() {
		Session session = sessionFactory.getSession();
		session.beginTransaction();

		PersonDAOImpl personDAO = new PersonDAOImpl(session);
		personDAO.setSessionFactory(sessionFactory.getSessionFactory());
		// Person example = new Person();
		// example.setFirstname("new person");
		// Filter filter = personDAO.getFilterFromExample(example);
		Filter filter = new Filter("firstname", "new person", Filter.OP_EQUAL);
		Search search = new Search();
		search.addFilter(filter);
		List<Person> persons = personDAO.search(search);
		for (Person person : persons) {
			System.out.println("Test03.deletePerson(): person: "
					+ person.getFirstname());
			personDAO.remove(person);
		}

		session.getTransaction().commit();
	}

	private static void updatePerson() {
		Session session = sessionFactory.getSession();
		session.beginTransaction();
		PersonDAOImpl personDAO = new PersonDAOImpl(session);
		personDAO.setSessionFactory(sessionFactory.getSessionFactory());
		Person person = personDAO.find(1L);
		System.out.println("Test03.updatePerson(): person: "
				+ person.getFirstname());
		person.setDescription("new descr 02");
		person.setOccupation("new occupation 02");
		personDAO.save(person);
		session.getTransaction().commit();
	}

	private static void createPerson() {
		Session session = sessionFactory.getSession();
		session.beginTransaction();

		Gender gender = (Gender) session.get(Gender.class, 1L);
		Person person = new Person();
		person.setGender(gender);
		person.setAbbreviation("NP");
		person.setFirstname("new person");
		Calendar cal = Calendar.getInstance();
		cal.set(1880, 02, 23);
		person.setBirthday(cal.getTime());
		PersonDAOImpl personDAO = new PersonDAOImpl(session);
		personDAO.setSessionFactory(sessionFactory.getSessionFactory());
		personDAO.save(person);
		session.getTransaction().commit();
	}

	private static void createIdea(Session session) {
		Idea idea = new Idea();
		idea.setNote("new idea");
		idea.setCategory("cat");
		idea.setStatus(0);
		session.save(idea);
	}

	private static void queryScenes(Session session) {/*
		System.out.println("\nTest01.queryScenes(): ");
		Query query = session.createQuery("from Scene");
		List list = (List) query.list();
		for (Scene scene : (List<Scene>)list) {
			System.out.println("Scene: " + scene.getTitle());
			if (scene.getStrand() != null) {
				System.out.println("  Strand: " + scene.getStrand().getName());
			}
			if (scene.getStrands() != null) {
				for (Strand strand : scene.getStrands()) {
					if (strand == null) {
						continue;
					}
					System.out.println("    Strands: "
							+ strand.getAbbreviation());
				}
			}
			if (scene.getRelativeSceneId() != null) {
				System.out.println("  Relative Scene: "
						+ scene.getRelativeSceneId());
			}
			if (scene.getChapter() != null) {
				System.out.println("  Chapter: "
						+ scene.getChapter().getTitle());
			}
			List<Person> persons = scene.getPersons();
			for (Person person : persons) {
				if (person == null) {
					continue;
				}
				System.out.println("  Person: " + person.getFirstname() + " "
						+ person.getLastname());
			}
			List<Location> locations = scene.getLocations();
			for (Location location : locations) {
				if (location == null) {
					continue;
				}
				System.out.println("  Location: " + location.getName());
			}
		}
	*/}
}
