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

package storybook.model;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import storybook.SbApp;
import storybook.SbConstants.ViewName;
import storybook.controller.BookController;
import storybook.model.hbn.dao.CategoryDAOImpl;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.GenderDAOImpl;
import storybook.model.hbn.dao.IdeaDAOImpl;
import storybook.model.hbn.dao.InternalDAOImpl;
import storybook.model.hbn.dao.ItemDAOImpl;
import storybook.model.hbn.dao.ItemLinkDAOImpl;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.dao.RelationshipDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.dao.StrandDAOImpl;
import storybook.model.hbn.dao.TagDAOImpl;
import storybook.model.hbn.dao.TagLinkDAOImpl;
import storybook.model.hbn.dao.TimeEventDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Category;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Gender;
import storybook.model.hbn.entity.Idea;
import storybook.model.hbn.entity.Internal;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.ItemLink;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Relationship;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;
import storybook.model.hbn.entity.Tag;
import storybook.model.hbn.entity.TagLink;
import storybook.model.hbn.entity.TimeEvent;
import storybook.model.state.SceneState;
import storybook.toolkit.I18N;
import storybook.toolkit.swing.ColorUtil;
import storybook.ui.MainFrame;
import storybook.ui.SbView;
import storybook.ui.panel.book.BookPanel;
import storybook.ui.panel.chrono.ChronoPanel;
import storybook.ui.panel.manage.ManagePanel;
import storybook.ui.panel.reading.ReadingPanel;

/**
 * @author martin
 *
 */
public class BookModel extends AbstractModel {

	public BookModel(MainFrame m) {
		super(m);
	}

	public synchronized void initEntites() {
		SbApp.trace("BookModel.initEntities()");
		Session session = beginTransaction();

		// default strand
		Strand strand = new Strand();
		strand.setName(I18N.getMsg("db.init.strand.name"));
		strand.setAbbreviation(I18N.getMsg("db.init.strand.abbr"));
		strand.setSort(1);
		strand.setJColor(ColorUtil.getNiceBlue());
		strand.setNotes("");
		session.save(strand);

		// default part
		Part part = new Part(1, I18N.getMsg("db.init.part"), "", null,
				new Timestamp(new Date().getTime()), null, null);
		session.save(part);

		// first chapter
		Chapter chapter = new Chapter();
		chapter.setPart(part);
		chapter.setChapterno(1);
		chapter.setTitle(I18N.getMsg("msg.common.chapter") + " 1");
		chapter.setDescription("");
		chapter.setNotes("");
		chapter.setCreationTime(new Timestamp(new Date().getTime()));
		chapter.setObjectiveTime(null);
		chapter.setDoneTime(null);
		session.save(chapter);

		// first scene
		Scene scene = EntityUtil.createScene(strand, chapter);
		session.save(scene);

		// default genders
		Gender male = new Gender(I18N.getMsg("msg.dlg.person.gender.male"), 12, 6, 47, 14);
		session.save(male);
		Gender female = new Gender(I18N.getMsg("msg.dlg.person.gender.female"), 12, 6, 47, 14);
		session.save(female);

		// default categories
		Category major = new Category(1, I18N.getMsg("msg.category.central.character"), null);
		session.save(major);
		Category minor = new Category(2, I18N.getMsg("msg.category.minor.character"), null);
		session.save(minor);

		commit();
	}

	@Override
	public synchronized void initSession(String dbName) {
		SbApp.trace("BookModel.initSession("+dbName+")");
		try {
			super.initSession(dbName);
			Session session = beginTransaction();
			// test queries
			sessionFactory.query(new PartDAOImpl(session));
			sessionFactory.query(new ChapterDAOImpl(session));
			commit();
			SbApp.trace("test query OK");
		} catch (Exception e) {
			SbApp.trace("test query not OK");
		}
	}

	@Override
	public void fireAgain() {
		SbApp.trace("BookModel.fireAgain()");

		fireAgainScenes();
		fireAgainChapters();
		fireAgainParts();
		fireAgainLocations();
		fireAgainPersons();
		fireAgainRelationships();
		fireAgainGenders();
		fireAgainCategories();
		fireAgainStrands();
		fireAgainIdeas();
		fireAgainTags();
		fireAgainItems();
		fireAgainTagLinks();
		fireAgainItemLinks();
		fireAgainInternals();
	}

	public void fireAgain(SbView view) {
		SbApp.trace("BookModel.fireAgain("+view.getName()+")");
		if (ViewName.CHRONO.compare(view)) {
			fireAgainScenes();
		} else if (ViewName.BOOK.compare(view)) {
			fireAgainScenes();
		} else if (ViewName.READING.compare(view)) {
			fireAgainChapters();
		} else if (ViewName.MANAGE.compare(view)) {
			fireAgainChapters();
		} else if (ViewName.SCENES.compare(view)) {
			fireAgainScenes();
		} else if (ViewName.CHAPTERS.compare(view)) {
			fireAgainChapters();
		} else if (ViewName.PARTS.compare(view)) {
			fireAgainParts();
		} else if (ViewName.LOCATIONS.compare(view)) {
			fireAgainLocations();
		} else if (ViewName.PERSONS.compare(view)) {
			fireAgainPersons();
		} else if (ViewName.RELATIONSHIPS.compare(view)) {
			fireAgainRelationships();
		} else if (ViewName.GENDERS.compare(view)) {
			fireAgainGenders();
		} else if (ViewName.CATEGORIES.compare(view)) {
			fireAgainCategories();
		} else if (ViewName.STRANDS.compare(view)) {
			fireAgainStrands();
		} else if (ViewName.IDEAS.compare(view)) {
			fireAgainIdeas();
		} else if (ViewName.TAGS.compare(view)) {
			fireAgainTags();
		} else if (ViewName.ITEMS.compare(view)) {
			fireAgainItems();
		} else if (ViewName.TAGLINKS.compare(view)) {
			fireAgainTagLinks();
		} else if (ViewName.ITEMLINKS.compare(view)) {
			fireAgainItemLinks();
		} else if (ViewName.INTERNALS.compare(view)) {
			fireAgainInternals();
		} else if (ViewName.PLAN.compare(view)) {
			fireAgainPlan();
		} else if (ViewName.TIMEEVENT.compare(view)) {
			fireAgainTimeEvent();
		}
	}

	private void fireAgainScenes() {
		SbApp.trace("BookModel.fireAgainScenes()");
		Session session = beginTransaction();
		SceneDAOImpl sceneDao = new SceneDAOImpl(session);
		List<Scene> scenes = sceneDao.findAll();
		commit();
		firePropertyChange(BookController.SceneProps.INIT.toString(), null, scenes);
	}

	private void fireAgainChapters() {
		SbApp.trace("BookModel.fireAgainChapters()");
		Session session = beginTransaction();
		ChapterDAOImpl chapterDao = new ChapterDAOImpl(session);
		List<Chapter> chapters = chapterDao.findAll();
		commit();
		firePropertyChange(BookController.ChapterProps.INIT.toString(), null, chapters);
	}

	private void fireAgainParts() {
		SbApp.trace("BookModel.fireAgainParts()");
		Session session = beginTransaction();
		PartDAOImpl partDao = new PartDAOImpl(session);
		List<Part> parts = partDao.findAll();
		commit();
		firePropertyChange(BookController.PartProps.INIT.toString(), null, parts);
	}

	private void fireAgainLocations() {
		SbApp.trace("BookModel.fireAgainLocations()");
		Session session = beginTransaction();
		LocationDAOImpl locationDao = new LocationDAOImpl(session);
		List<Location> locations = locationDao.findAll();
		commit();
		firePropertyChange(BookController.LocationProps.INIT.toString(), null, locations);
	}

	private void fireAgainPersons() {
		SbApp.trace("BookModel.fireAgainPersons()");
		Session session = beginTransaction();
		PersonDAOImpl personDao = new PersonDAOImpl(session);
		List<Person> persons = personDao.findAll();
		commit();
		firePropertyChange(BookController.PersonProps.INIT.toString(), null, persons);
	}

	private void fireAgainRelationships() {
		SbApp.trace("BookModel.fireAgainRelationships()");
		Session session = beginTransaction();
		RelationshipDAOImpl rDao = new RelationshipDAOImpl(session);
		List<Relationship> relationships = rDao.findAll();
		commit();
		firePropertyChange(BookController.RelationshipProps.INIT.toString(), null, relationships);
	}

	private void fireAgainGenders() {
		SbApp.trace("BookModel.fireAgainGenders()");
		Session session = beginTransaction();
		GenderDAOImpl genderDao = new GenderDAOImpl(session);
		List<Gender> genders = genderDao.findAll();
		commit();
		firePropertyChange(BookController.GenderProps.INIT.toString(), null, genders);
	}

	private void fireAgainCategories() {
		SbApp.trace("BookModel.fireAgainCategories()");
		Session session = beginTransaction();
		CategoryDAOImpl categoryDao = new CategoryDAOImpl(session);
		List<Category> categories = categoryDao.findAll();
		commit();
		firePropertyChange(BookController.CategoryProps.INIT.toString(), null, categories);
	}

	private void fireAgainStrands() {
		SbApp.trace("BookModel.fireAgainStrands()");
		Session session = beginTransaction();
		StrandDAOImpl strandDao = new StrandDAOImpl(session);
		List<Strand> strands = strandDao.findAll();
		commit();
		firePropertyChange(BookController.StrandProps.INIT.toString(), null, strands);
	}

	private void fireAgainIdeas() {
		SbApp.trace("BookModel.fireAgainIdeas()");
		Session session = beginTransaction();
		IdeaDAOImpl ideaDao = new IdeaDAOImpl(session);
		List<Idea> ideas = ideaDao.findAll();
		commit();
		firePropertyChange(BookController.IdeaProps.INIT.toString(), null, ideas);
	}

	private void fireAgainTags() {
		SbApp.trace("BookModel.fireAgainTags()");
		Session session = beginTransaction();
		TagDAOImpl tagDao = new TagDAOImpl(session);
		List<Tag> tags = tagDao.findAll();
		commit();
		firePropertyChange(BookController.TagProps.INIT.toString(), null, tags);
	}

	private void fireAgainItems() {
		SbApp.trace("BookModel.fireAgainItems()");
		Session session = beginTransaction();
		ItemDAOImpl itemDao = new ItemDAOImpl(session);
		List<Item> items = itemDao.findAll();
		commit();
		firePropertyChange(BookController.ItemProps.INIT.toString(), null, items);
	}

	private void fireAgainTagLinks() {
		SbApp.trace("BookModel.fireAgainTagLinks()");
		Session session = beginTransaction();
		TagLinkDAOImpl tagLinkDao = new TagLinkDAOImpl(session);
		List<TagLink> tagLinks = tagLinkDao.findAll();
		commit();
		firePropertyChange(BookController.TagLinkProps.INIT.toString(), null, tagLinks);
	}

	private void fireAgainItemLinks() {
		SbApp.trace("BookModel.fireAgainItemLinks()");
		Session session = beginTransaction();
		ItemLinkDAOImpl itemLinkDao = new ItemLinkDAOImpl(session);
		List<ItemLink> itemLinks = itemLinkDao.findAll();
		commit();
		firePropertyChange(BookController.ItemLinkProps.INIT.toString(), null, itemLinks);
	}

	private void fireAgainInternals() {
		SbApp.trace("BookModel.fireAgainInternals()");
		Session session = beginTransaction();
		InternalDAOImpl internalDao = new InternalDAOImpl(session);
		List<Internal> internals = internalDao.findAll();
		commit();
		firePropertyChange(BookController.InternalProps.INIT.toString(), null, internals);
	}

	private void fireAgainPlan() {
	}

	private void fireAgainTimeEvent() {
		SbApp.trace("BookModel.fireAgainTimeEvent()");
		Session session = beginTransaction();
		TimeEventDAOImpl internalDao = new TimeEventDAOImpl(session);
		List<TimeEvent> internals = internalDao.findAll();
		commit();
		firePropertyChange(BookController.TimeEventProps.INIT.toString(), null, internals);
	}


	// common
	public void setRefresh(SbView view) {
		SbApp.trace("BookModel.setRefresh("+view.getName()+")");
		firePropertyChange(BookController.CommonProps.REFRESH.toString(), null, view);
		try {
			if (view.getComponentCount() == 0) {
				return;
			}
			Component comp = view.getComponent();
			if (comp instanceof ChronoPanel || comp instanceof BookPanel
					|| comp instanceof ManagePanel
					|| comp instanceof ReadingPanel) {
				// these views don't need a "fire again"
				return;
			}
			fireAgain(view);
		} catch (ArrayIndexOutOfBoundsException e) {
			// ignore
		}
	}

	public void setShowOptions(SbView view) {
		firePropertyChange(BookController.CommonProps.SHOW_OPTIONS.toString(), null,view);
	}

	public void setShowInfo(Scene scene) {
		setShowInfo((AbstractEntity)scene);
	}

	public void setShowInfo(Chapter chapter) {
		setShowInfo((AbstractEntity)chapter);
	}

	public void setShowInfo(Part part) {
		setShowInfo((AbstractEntity)part);
	}

	public void setShowInfo(Person person) {
		setShowInfo((AbstractEntity)person);
	}

	public void setShowInfo(Category category) {
		setShowInfo((AbstractEntity)category);
	}

	public void setShowInfo(Gender gender) {
		setShowInfo((AbstractEntity)gender);
	}

	public void setShowInfo(Location location) {
		setShowInfo((AbstractEntity)location);
	}

	public void setShowInfo(Tag tag) {
		setShowInfo((AbstractEntity)tag);
	}

	public void setShowInfo(TagLink tagLink) {
		setShowInfo((AbstractEntity)tagLink);
	}

	public void setShowInfo(Item item) {
		setShowInfo((AbstractEntity)item);
	}

	public void setShowInfo(ItemLink itemLink) {
		setShowInfo((AbstractEntity)itemLink);
	}

	public void setShowInfo(Strand strand) {
		setShowInfo((AbstractEntity) strand);
	}

	public void setShowInfo(Idea idea) {
		setShowInfo((AbstractEntity) idea);
	}

	public void setShowInfo(AbstractEntity entity) {
		firePropertyChange(BookController.CommonProps.SHOW_INFO.toString(), null, entity);
	}

	public void setShowInfo(TimeEvent event) {
		setShowInfo((AbstractEntity) event);
	}

	public void setShowInfo(DbFile dbFile) {
		firePropertyChange(BookController.CommonProps.SHOW_INFO.toString(), null, dbFile);
	}

	public void setShowInMemoria(Person person) {
		setShowInMemoria((AbstractEntity) person);
	}

	public void setShowInMemoria(Relationship p) {
		setShowInMemoria((AbstractEntity) p);
	}

	public void setShowInMemoria(Location location) {
		setShowInMemoria((AbstractEntity) location);
	}

	public void setShowInMemoria(Scene scene) {
		setShowInMemoria((AbstractEntity) scene);
	}

	public void setShowInMemoria(Tag tag) {
		setShowInMemoria((AbstractEntity) tag);
	}

	public void setShowInMemoria(Item item) {
		setShowInMemoria((AbstractEntity) item);
	}

	public void setShowInMemoria(AbstractEntity entity) {
		firePropertyChange(BookController.CommonProps.SHOW_IN_MEMORIA.toString(), null, entity);
	}

	public void setUnloadEditor() {
		firePropertyChange(BookController.CommonProps.UNLOAD_EDITOR.toString(), null, null);
	}

	public void setFilterScenes(SceneState state) {
		firePropertyChange(BookController.SceneProps.FILTER.toString(), null, state);
	}

	public void setFilterStrand(String strand) {
		firePropertyChange(BookController.SceneProps.FILTERSTRAND.toString(), null, strand);
	}

	public void setPrint(SbView view) {
		firePropertyChange(BookController.CommonProps.PRINT.toString(), null, view);
	}

	public void setExport(SbView view) {
		firePropertyChange(BookController.CommonProps.EXPORT.toString(), null, view);
	}

	// chrono view
	public void setChronoZoom(Integer val) {
		firePropertyChange(BookController.ChronoViewProps.ZOOM.toString(), null, val);
	}

	public void setChronoLayoutDirection(Boolean val) {
		firePropertyChange(BookController.ChronoViewProps.LAYOUT_DIRECTION.toString(), null, val);
	}

	public void setChronoShowDateDifference(Boolean val) {
		firePropertyChange(BookController.ChronoViewProps.SHOW_DATE_DIFFERENCE.toString(), null, val);
	}

	public void setChronoShowEntity(Scene scene) {
		firePropertyChange(BookController.ChronoViewProps.SHOW_ENTITY.toString(), null, scene);
	}

	public void setChronoShowEntity(Chapter chapter) {
		firePropertyChange(BookController.ChronoViewProps.SHOW_ENTITY.toString(), null, chapter);
	}

	// book view
	public void setBookZoom(Integer val) {
		firePropertyChange(BookController.BookViewProps.ZOOM.toString(), null, val);
	}

	public void setBookHeightFactor(Integer val) {
		firePropertyChange(BookController.BookViewProps.HEIGHT_FACTOR.toString(), null, val);
	}

	public void setBookShowEntity(Scene scene) {
		firePropertyChange(BookController.BookViewProps.SHOW_ENTITY.toString(), null, scene);
	}

	public void setBookShowEntity(Chapter chapter) {
		firePropertyChange(BookController.BookViewProps.SHOW_ENTITY.toString(), null, chapter);
	}

	// manage view
	public void setManageZoom(Integer val) {
		firePropertyChange(BookController.ManageViewProps.ZOOM.toString(), null, val);
	}

	public void setManageColumns(Integer val) {
		firePropertyChange(BookController.ManageViewProps.COLUMNS.toString(), null, val);
	}

	public void setManageShowEntity(Scene scene) {
		firePropertyChange(BookController.ManageViewProps.SHOW_ENTITY.toString(), null, scene);
	}

	public void setManageShowEntity(Chapter chapter) {
		firePropertyChange(BookController.ManageViewProps.SHOW_ENTITY.toString(), null, chapter);
	}

	// reading view
	public void setReadingZoom(Integer val) {
		firePropertyChange(BookController.ReadingViewProps.ZOOM.toString(), null, val);
	}

	public void setReadingFontSize(Integer val) {
		firePropertyChange(BookController.ReadingViewProps.FONT_SIZE.toString(), null, val);
	}

	// memoria view
	public void setMemoriaBalloon(Boolean val) {
		firePropertyChange(BookController.MemoriaViewProps.BALLOON.toString(), null, val);
	}

	// chapter
	public void setEditChapter(Chapter entity) {
		//firePropertyChange(BookController.ChapterProps.EDIT.toString(), null, entity);
		editEntity((AbstractEntity)entity);
	}

	public synchronized void setUpdateChapter(Chapter chapter) {
		Session session = beginTransaction();
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		Chapter old = dao.find(chapter.getId());
		commit();
		session = beginTransaction();
		session.update(chapter);
		commit();
		firePropertyChange(BookController.ChapterProps.UPDATE.toString(), old, chapter);
	}

	public synchronized void setNewChapter(Chapter chapter) {
		Session session = beginTransaction();
		session.save(chapter);
		commit();
		firePropertyChange(BookController.ChapterProps.NEW.toString(), null, chapter);
	}

	public synchronized void setDeleteChapter(Chapter chapter) {
		if (chapter.getId() == null) {
			return;
		}
		Session session = beginTransaction();
		// find scenes, set chapter to null
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		List<Scene> scenes = dao.findScenes(chapter);
		commit();
		for (Scene scene : scenes) {
			scene.setChapter();
			setUpdateScene(scene);
		}
		// delete chapter
		session = beginTransaction();
		session.delete(chapter);
		commit();
		firePropertyChange(BookController.ChapterProps.DELETE.toString(), chapter, null);
	}

	public synchronized void setDeleteMultiChapters(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			ChapterDAOImpl dao = new ChapterDAOImpl(session);
			Chapter old = dao.find(id);
			commit();
			session = beginTransaction();
			dao = new ChapterDAOImpl(session);
			dao.removeById(id);
			commit();
			firePropertyChange(BookController.ChapterProps.DELETE.toString(), old, null);
		}
	}

	// part
	public void setEditPart(Part entity) {
		//firePropertyChange(BookController.PartProps.EDIT.toString(), null, entity);
		editEntity((AbstractEntity)entity);
	}

	public synchronized void setUpdatePart(Part part) {
		Session session = beginTransaction();
		PartDAOImpl dao = new PartDAOImpl(session);
		Part old = dao.find(part.getId());
		commit();
		session = beginTransaction();
		session.update(part);
		commit();

		firePropertyChange(BookController.PartProps.UPDATE.toString(), old, part);
	}

	public synchronized void setNewPart(Part part) {
		Session session = beginTransaction();
		session.save(part);
		commit();
		firePropertyChange(BookController.PartProps.NEW.toString(), null, part);
	}

	public synchronized void setDeletePart(Part part) {
		if (part.getId() == null) {
			return;
		}
		Session session = beginTransaction();
		// delete chapters
		PartDAOImpl dao = new PartDAOImpl(session);
		List<Chapter> chapters = dao.findChapters(part);
		commit();
		for (Chapter chapter : chapters) {
			setDeleteChapter(chapter);
		}
		// delete part
		session = beginTransaction();
		session.delete(part);
		commit();
		firePropertyChange(BookController.PartProps.DELETE.toString(), part, null);
	}

	public synchronized void setDeleteMultiParts(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			PartDAOImpl dao = new PartDAOImpl(session);
			Part old = dao.find(id);
			commit();
			setDeletePart(old);
		}
	}

	public synchronized void setChangePart(Part part) {
		firePropertyChange(BookController.PartProps.CHANGE.toString(), null, part);
	}

	// location
	public void setEditLocation(Location entity) {
		//firePropertyChange(BookController.LocationProps.EDIT.toString(), null, entity);
		editEntity((AbstractEntity)entity);
	}

	public synchronized void setUpdateLocation(Location location) {
		Session session = beginTransaction();
		LocationDAOImpl dao = new LocationDAOImpl(session);
		Location old = dao.find(location.getId());
		commit();
		session = beginTransaction();
		session.update(location);
		commit();
		firePropertyChange(BookController.LocationProps.UPDATE.toString(), old, location);
	}

	public synchronized void setNewLocation(Location location) {
		Session session = beginTransaction();
		session.save(location);
		commit();
		firePropertyChange(BookController.LocationProps.NEW.toString(), null, location);
	}

	public synchronized void setDeleteLocation(Location location) {
		if (location.getId() == null) {
			return;
		}
		try {
			// delete scene links
			Session session = beginTransaction();
			SceneDAOImpl dao = new SceneDAOImpl(session);
			List<Scene> scenes = dao.findByLocationLink(location);
			for (Scene scene : scenes) {
				scene.getLocations().remove(location);
				session.update(scene);
			}
			commit();
			for (Scene scene : scenes) {
				setUpdateScene(scene);
			}
			// delete tag / item links
			EntityUtil.deleteTagAndItemLinks(this, location);
			// delete location
			session = beginTransaction();
			session.delete(location);
			commit();
		} catch (ConstraintViolationException e) {
			SbApp.error("BookModel.setDeleteLocation("+location.getName()+")", e);
		}
		firePropertyChange(BookController.LocationProps.DELETE.toString(),location, null);
	}

	public synchronized void setDeleteMultiLocations(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			LocationDAOImpl dao = new LocationDAOImpl(session);
			Location old = dao.find(id);
			commit();
			setDeleteLocation(old);
		}
	}

	// person
	public void setEditPerson(Person entity) {
		//firePropertyChange(BookController.PersonProps.EDIT.toString(),null, entity);
		editEntity((AbstractEntity)entity);
	}

	public synchronized void setUpdatePerson(Person person) {
		Session session = beginTransaction();
		PersonDAOImpl dao = new PersonDAOImpl(session);
		Person old = dao.find(person.getId());
		commit();
		session = beginTransaction();
		session.update(person);
		commit();
		firePropertyChange(BookController.PersonProps.UPDATE.toString(), old, person);
	}

	public synchronized void setNewPerson(Person person) {
		Session session = beginTransaction();
		session.save(person);
		commit();
		firePropertyChange(BookController.PersonProps.NEW.toString(), null, person);
	}

	public synchronized void setDeletePerson(Person person) {
		if (person.getId() == null) {
			return;
		}
		try {
			// delete scene links
			Session session = beginTransaction();
			SceneDAOImpl dao = new SceneDAOImpl(session);
			List<Scene> scenes = dao.findByPersonLink(person);
			for (Scene scene : scenes) {
				scene.getPersons().remove(person);
				session.update(scene);
			}
			commit();
			for (Scene scene : scenes) {
				setUpdateScene(scene);
			}
			// delete tag / item links
			EntityUtil.deleteTagAndItemLinks(this, person);
			// delete person
			session = beginTransaction();
			session.delete(person);
			commit();
		} catch (ConstraintViolationException e) {
			SbApp.error("BookModel.setDeletePerson("+person.getFullName()+")", e);
		}
		firePropertyChange(BookController.PersonProps.DELETE.toString(),person, null);
	}

	public synchronized void setDeleteMultiPersons(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			PersonDAOImpl dao = new PersonDAOImpl(session);
			Person old = dao.find(id);
			commit();
			setDeletePerson(old);
		}
	}

	// relationship
	public void setEditRelationship(Relationship entity) {
		//firePropertyChange(BookController.PersonProps.EDIT.toString(),null, entity);
		editEntity((AbstractEntity)entity);
	}

	public synchronized void setUpdateRelationship(Relationship relationship) {
		Session session = beginTransaction();
		RelationshipDAOImpl dao = new RelationshipDAOImpl(session);
		Relationship old = dao.find(relationship.getId());
		commit();
		session = beginTransaction();
		session.update(relationship);
		commit();
		firePropertyChange(BookController.RelationshipProps.UPDATE.toString(), old, relationship);
	}

	public synchronized void setNewRelationship(Relationship r) {
		Session session = beginTransaction();
		session.save(r);
		commit();
		firePropertyChange(BookController.RelationshipProps.NEW.toString(), null, r);
	}

	public synchronized void setDeleteRelationship(Relationship r) {
		if (r.getId() == null) {
			return;
		}
		try {
			// delete scene links
			// delete Relationship
			Session session = beginTransaction();
			session.delete(r);
			commit();
		} catch (ConstraintViolationException e) {
			SbApp.error("BookModel.setDeleteRelationship("+r.getPerson1()+"-"+r.getPerson2()+")", e);
		}
		firePropertyChange(BookController.RelationshipProps.DELETE.toString(),r, null);
	}

	public synchronized void setDeleteMultiRelationships(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			RelationshipDAOImpl dao = new RelationshipDAOImpl(session);
			Relationship old = dao.find(id);
			commit();
			setDeleteRelationship(old);
		}
	}

	// gender
	public void setEditGender(Gender entity) {
		//firePropertyChange(BookController.GenderProps.EDIT.toString(), null, entity);
		editEntity((AbstractEntity)entity);
	}

	public synchronized void setUpdateGender(Gender gender) {
		Session session = beginTransaction();
		GenderDAOImpl dao = new GenderDAOImpl(session);
		Gender old = dao.find(gender.getId());
		commit();

		session = beginTransaction();
		session.update(gender);
		commit();

		firePropertyChange(BookController.GenderProps.UPDATE.toString(), old, gender);
	}

	public synchronized void setNewGender(Gender gender) {
		Session session = beginTransaction();
		session.save(gender);
		commit();

		firePropertyChange(BookController.GenderProps.NEW.toString(), null, gender);
	}

	public synchronized void setDeleteGender(Gender gender) {
		if (gender.getId() == null) {
			return;
		}

		// set gender of affected persons to "male"
		Session session = beginTransaction();
		GenderDAOImpl dao = new GenderDAOImpl(session);
		Gender male = dao.findMale();
		List<Person> persons = dao.findPersons(gender);
		commit();
		for (Person person : persons) {
			person.setGender(male);
			setUpdatePerson(person);
		}
		// delete gender
		session = beginTransaction();
		session.delete(gender);
		commit();

		firePropertyChange(BookController.GenderProps.DELETE.toString(), gender, null);
	}

	public synchronized void setDeleteMultiGenders(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			GenderDAOImpl dao = new GenderDAOImpl(session);
			Gender old = dao.find(id);
			commit();

			session = beginTransaction();
			dao = new GenderDAOImpl(session);
			dao.removeById(id);
			commit();

			firePropertyChange(BookController.GenderProps.DELETE.toString(), old, null);
		}
	}

	// category
	public void setEditCategory(Category entity) {
		//firePropertyChange(BookController.CategoryProps.EDIT.toString(), null, entity);
		editEntity((AbstractEntity)entity);
	}

	public synchronized void setUpdateCategory(Category category) {
		Session session = beginTransaction();
		CategoryDAOImpl dao = new CategoryDAOImpl(session);
		Category old = dao.find(category.getId());
		commit();

		session = beginTransaction();
		session.update(category);
		commit();

		firePropertyChange(BookController.CategoryProps.UPDATE.toString(), old, category);
	}

	public synchronized void setNewCategory(Category category) {
		Session session = beginTransaction();
		session.save(category);
		commit();

		firePropertyChange(BookController.CategoryProps.NEW.toString(), null, category);
	}

	public synchronized void setDeleteCategory(Category category) {
		if (category.getId() == null) {
			return;
		}

		// set category of affected persons to "minor"
		Session session = beginTransaction();
		CategoryDAOImpl dao = new CategoryDAOImpl(session);
		Category minor = dao.findMinor();
		List<Person> persons = dao.findPersons(category);
		commit();
		for (Person person : persons) {
			person.setCategory(minor);
			setUpdatePerson(person);
		}
		// delete category
		session = beginTransaction();
		session.delete(category);
		commit();

		firePropertyChange(BookController.CategoryProps.DELETE.toString(), category, null);
	}

	public synchronized void setDeleteMultiCategories(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			CategoryDAOImpl dao = new CategoryDAOImpl(session);
			Category old = dao.find(id);
			commit();

			session = beginTransaction();
			dao = new CategoryDAOImpl(session);
			dao.removeById(id);
			commit();

			firePropertyChange(BookController.CategoryProps.DELETE.toString(), old, null);
		}
	}

	public synchronized void setOrderUpCategory(Category category) {
		firePropertyChange(BookController.CategoryProps.ORDER_UP.toString(), null, category);
	}

	public synchronized void setOrderDownCategory(Category category) {
		firePropertyChange(BookController.CategoryProps.ORDER_DOWN.toString(), null, category);
	}

	// strand
	public void setEditStrand(Strand entity) {
		//firePropertyChange(BookController.StrandProps.EDIT.toString(), null, entity);
		editEntity((AbstractEntity)entity);
	}

	public synchronized void setUpdateStrand(Strand strand) {
		Session session = beginTransaction();
		StrandDAOImpl dao = new StrandDAOImpl(session);
		Strand old = dao.find(strand.getId());
		commit();

		session = beginTransaction();
		session.update(strand);
		commit();

		firePropertyChange(BookController.StrandProps.UPDATE.toString(), old, strand);
	}

	public synchronized void setNewStrand(Strand strand) {
		Session session = beginTransaction();
		session.save(strand);
		commit();

		firePropertyChange(BookController.StrandProps.NEW.toString(), null, strand);
	}

	public synchronized void setDeleteStrand(Strand strand) {
		if (strand.getId() == null) {
			return;
		}
		try {
			// delete scene links
			Session session = beginTransaction();
			SceneDAOImpl sceneDao = new SceneDAOImpl(session);
			List<Scene> scenes = sceneDao.findByStrandLink(strand);
			for (Scene scene : scenes) {
				scene.getStrands().remove(strand);
				session.update(scene);
			}
			commit();
			for (Scene scene : scenes) {
				setUpdateScene(scene);
			}

			// delete scenes
			session = beginTransaction();
			StrandDAOImpl strandDao = new StrandDAOImpl(session);
			scenes = strandDao.findScenes(strand);
			commit();
			for (Scene scene : scenes) {
				setDeleteScene(scene);
			}

			// delete strand
			session = beginTransaction();
			session.delete(strand);
			commit();
		} catch (ConstraintViolationException e) {
			SbApp.error("BookModel.setDeleteStrand("+strand.getName()+")", e);
		}
		firePropertyChange(BookController.StrandProps.DELETE.toString(),strand, null);
	}

	public synchronized void setDeleteMultiStrands(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			StrandDAOImpl dao = new StrandDAOImpl(session);
			Strand old = dao.find(id);
			commit();
			setDeleteStrand(old);
		}
	}

	public synchronized void setOrderUpStrand(Strand strand) {
		firePropertyChange(BookController.StrandProps.ORDER_UP.toString(),null, strand);
	}

	public synchronized void setOrderDownStrand(Strand strand) {
		firePropertyChange(BookController.StrandProps.ORDER_DOWN.toString(), null,strand);
	}


	// idea
	public void setEditIdea(Idea entity) {
		//firePropertyChange(BookController.IdeaProps.EDIT.toString(), null, entity);
		editEntity((AbstractEntity)entity);
	}

	public synchronized void setUpdateIdea(Idea idea) {
		Session session = beginTransaction();
		IdeaDAOImpl dao = new IdeaDAOImpl(session);
		Idea old = dao.find(idea.getId());
		commit();

		session = beginTransaction();
		session.update(idea);
		commit();

		firePropertyChange(BookController.IdeaProps.UPDATE.toString(), old,idea);
	}

	public synchronized void setNewIdea(Idea idea) {
		Session session = beginTransaction();
		session.save(idea);
		commit();

		firePropertyChange(BookController.IdeaProps.NEW.toString(), null,idea);
	}

	public synchronized void setDeleteIdea(Idea idea) {
		if (idea.getId() == null) {
			return;
		}
		Session session = beginTransaction();
		session.delete(idea);
		commit();
		firePropertyChange(BookController.IdeaProps.DELETE.toString(),idea, null);
	}

	public synchronized void setDeleteMultiIdeas(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			IdeaDAOImpl dao = new IdeaDAOImpl(session);
			Idea old = dao.find(id);
			commit();

			session = beginTransaction();
			dao = new IdeaDAOImpl(session);
			dao.removeById(id);
			commit();

			firePropertyChange(BookController.IdeaProps.DELETE.toString(),old, null);
		}
	}

	// tags
	public void setEditTag(Tag entity) {
		//firePropertyChange(BookController.TagProps.EDIT.toString(), null, entity);
		editEntity((AbstractEntity)entity);
	}

	public synchronized void setUpdateTag(Tag tag) {
		Session session = beginTransaction();
		TagDAOImpl dao = new TagDAOImpl(session);
		Tag old = dao.find(tag.getId());
		commit();

		session = beginTransaction();
		session.update(tag);
		commit();

		firePropertyChange(BookController.TagProps.UPDATE.toString(), old, tag);
	}

	public synchronized void setNewTag(Tag tag) {
		Session session = beginTransaction();
		session.save(tag);
		commit();

		firePropertyChange(BookController.TagProps.NEW.toString(), null, tag);
	}

	public synchronized void setDeleteTag(Tag tag) {
		if (tag.getId() == null) {
			return;
		}
		// delete tag assignments
		Session session = beginTransaction();
		TagLinkDAOImpl dao = new TagLinkDAOImpl(session);
		List<TagLink> links = dao.findByTag(tag);
		commit();
		for (TagLink link : links) {
			setDeleteTagLink(link);
		}
		// delete tag
		session = beginTransaction();
		session.delete(tag);
		commit();
		firePropertyChange(BookController.TagProps.DELETE.toString(), tag, null);
	}

	public synchronized void setDeleteMultiTags(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			TagDAOImpl dao = new TagDAOImpl(session);
			Tag old = dao.find(id);
			commit();
			setDeleteTag(old);
		}
	}

	// items
	public void setEditItem(Item entity) {
		//firePropertyChange(BookController.ItemProps.EDIT.toString(), null, entity);
		editEntity((AbstractEntity)entity);
	}

	public synchronized void setUpdateItem(Item item) {
		Session session = beginTransaction();
		ItemDAOImpl dao = new ItemDAOImpl(session);
		Item old = dao.find(item.getId());
		commit();

		session = beginTransaction();
		session.update(item);
		commit();

		firePropertyChange(BookController.ItemProps.UPDATE.toString(), old, item);
	}

	public synchronized void setNewItem(Item item) {
		Session session = beginTransaction();
		session.save(item);
		commit();

		firePropertyChange(BookController.ItemProps.NEW.toString(), null, item);
	}

	public synchronized void setDeleteItem(Item item) {
		if (item.getId() == null) {
			return;
		}
		// delete item assignments
		Session session = beginTransaction();
		ItemLinkDAOImpl dao = new ItemLinkDAOImpl(session);
		List<ItemLink> links = dao.findByItem(item);
		commit();
		for (ItemLink link : links) {
			setDeleteItemLink(link);
		}
		// delete item
		session = beginTransaction();
		session.delete(item);
		commit();
		firePropertyChange(BookController.ItemProps.DELETE.toString(), item, null);
	}

	public synchronized void setDeleteMultiItems(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			ItemDAOImpl dao = new ItemDAOImpl(session);
			Item old = dao.find(id);
			commit();
			setDeleteItem(old);
		}
	}

	// tag links
	public void setEditTagLink(TagLink entity) {
		//firePropertyChange(BookController.TagLinkProps.EDIT.toString(), null, entity);
		editEntity((AbstractEntity)entity);
	}

	public synchronized void setUpdateTagLink(TagLink tagLink) {
		Session session = beginTransaction();
		TagLinkDAOImpl dao = new TagLinkDAOImpl(session);
		TagLink old = dao.find(tagLink.getId());
		commit();

		session = beginTransaction();
		session.update(tagLink);
		commit();

		firePropertyChange(BookController.TagLinkProps.UPDATE.toString(), old, tagLink);
	}

	public synchronized void setNewTagLink(TagLink tagLink) {
		Session session = beginTransaction();
		session.save(tagLink);
		commit();

		firePropertyChange(BookController.TagLinkProps.NEW.toString(), null, tagLink);
	}

	public synchronized void setDeleteTagLink(TagLink tagLink) {
		if (tagLink.getId() == null) {
			return;
		}
		Session session = beginTransaction();
		session.delete(tagLink);
		commit();
		firePropertyChange(BookController.TagLinkProps.DELETE.toString(), tagLink, null);
	}

	public synchronized void setDeleteMultiTagLinks(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			TagLinkDAOImpl dao = new TagLinkDAOImpl(session);
			TagLink old = dao.find(id);
			commit();
			setDeleteTagLink(old);
		}
	}

	// item links
	public void setEditItemLink(ItemLink entity) {
		//firePropertyChange(BookController.ItemLinkProps.EDIT.toString(), null, entity);
		editEntity((AbstractEntity)entity);
	}

	public synchronized void setUpdateItemLink(ItemLink itemLink) {
		Session session = beginTransaction();
		ItemLinkDAOImpl dao = new ItemLinkDAOImpl(session);
		ItemLink old = dao.find(itemLink.getId());
		commit();

		session = beginTransaction();
		session.update(itemLink);
		commit();

		firePropertyChange(BookController.ItemLinkProps.UPDATE.toString(), old, itemLink);
	}

	public synchronized void setNewItemLink(ItemLink itemLink) {
		Session session = beginTransaction();
		session.save(itemLink);
		commit();

		firePropertyChange(BookController.ItemLinkProps.NEW.toString(), null, itemLink);
	}

	public synchronized void setDeleteItemLink(ItemLink itemLink) {
		if (itemLink.getId() == null) {
			return;
		}
		Session session = beginTransaction();
		session.delete(itemLink);
		commit();
		firePropertyChange(BookController.ItemLinkProps.DELETE.toString(), itemLink, null);
	}

	public synchronized void setDeleteMultiItemLinks(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			ItemLinkDAOImpl dao = new ItemLinkDAOImpl(session);
			ItemLink old = dao.find(id);
			commit();
			setDeleteItemLink(old);
		}
	}

	// scenes
	public void setEditScene(Scene entity) {
		SbApp.trace("BookModel.setEditScene("+entity.toString()+")");
		//firePropertyChange(BookController.SceneProps.EDIT.toString(), null, editScene);
		editEntity((AbstractEntity)entity);
	}

	public synchronized void setUpdateScene(Scene scene) {
		// needed, see ChronoPanel.modelPropertyChange()
		Session session = beginTransaction();
		Scene old = (Scene) session.get(Scene.class, scene.getId());
		commit();
		try {
			session = beginTransaction();
			session.update(scene);
			commit();
		} catch (ConstraintViolationException e) {
			SbApp.error("BookModel.setUpdateScene("+scene.getTitle()+")", e);
		}
		firePropertyChange(BookController.SceneProps.UPDATE.toString(), old, scene);
	}

	public synchronized void setNewScene(Scene scene) {
		Session session = beginTransaction();
		session.save(scene);
		commit();

		firePropertyChange(BookController.SceneProps.NEW.toString(), null, scene);
	}

	public synchronized void setDeleteScene(Scene scene) {
		if (scene.getId() == null) {
			return;
		}
		// delete tag / item links
		EntityUtil.deleteTagAndItemLinks(this, scene);
		// remove relative scene of affected scenes
		Session session = beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		List<Scene> scenes = dao.findScenesWithRelativeSceneId(scene);
		commit();
		for (Scene scene2 : scenes) {
			scene2.removeRelativeScene();
			setUpdateScene(scene2);
		}
		// delete scene
		session = beginTransaction();
		session.delete(scene);
		commit();
		firePropertyChange(BookController.SceneProps.DELETE.toString(), scene, null);
	}

	public synchronized void setDeleteMultiScenes(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			SceneDAOImpl dao = new SceneDAOImpl(session);
			Scene old = dao.find(id);
			commit();
			setDeleteScene(old);
		}
	}

	// internals
	public void setEditInternal(Internal entity) {
		//firePropertyChange(BookController.InternalProps.EDIT.toString(), null, entity);
		editEntity((AbstractEntity)entity);
	}

	public synchronized void setUpdateInternal(Internal internal) {
		Session session = beginTransaction();
		InternalDAOImpl dao = new InternalDAOImpl(session);
		Internal old = dao.find(internal.getId());
		commit();

		session = beginTransaction();
		session.update(internal);
		commit();

		firePropertyChange(BookController.InternalProps.UPDATE.toString(), old, internal);
	}

	public synchronized void setNewInternal(Internal internal) {
		Session session = beginTransaction();
		session.save(internal);
		commit();

		firePropertyChange(BookController.InternalProps.NEW.toString(), null, internal);
	}

	public synchronized void setDeleteInternal(Internal internal) {
		if (internal.getId() == null) {
			return;
		}
		Session session = beginTransaction();
		session.delete(internal);
		commit();
		firePropertyChange(BookController.InternalProps.DELETE.toString(), internal, null);
	}

	public synchronized void setDeleteMultiInternals(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			InternalDAOImpl dao = new InternalDAOImpl(session);
			Internal old = dao.find(id);
			commit();
			setDeleteInternal(old);
		}
	}

	// chapter
	public void setEditTimeEvent(TimeEvent entity) {
		editEntity((AbstractEntity)entity);
	}

	public synchronized void setUpdateTimeEvent(TimeEvent chapter) {
		Session session = beginTransaction();
		TimeEventDAOImpl dao = new TimeEventDAOImpl(session);
		TimeEvent old = dao.find(chapter.getId());
		commit();
		session = beginTransaction();
		session.update(chapter);
		commit();
		firePropertyChange(BookController.TimeEventProps.UPDATE.toString(), old, chapter);
	}

	public synchronized void setNewTimeEvent(TimeEvent chapter) {
		Session session = beginTransaction();
		session.save(chapter);
		commit();
		firePropertyChange(BookController.TimeEventProps.NEW.toString(), null, chapter);
	}

	public synchronized void setDeleteTimeEvent(TimeEvent chapter) {
		if (chapter.getId() == null) {
			return;
		}
		Session session = beginTransaction();
		// delete chapter
		session = beginTransaction();
		session.delete(chapter);
		commit();
		firePropertyChange(BookController.TimeEventProps.DELETE.toString(), chapter, null);
	}
}
