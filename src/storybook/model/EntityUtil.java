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
package storybook.model;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.SqlTimestampConverter;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import storybook.SbApp;
import storybook.SbConstants;
import storybook.action.ChapterOrderByTimestampAction;
import storybook.action.ChapterReSortAction;
import storybook.action.DeleteEntityAction;
import storybook.action.EditEntityAction;
import storybook.action.EditSceneLOAction;
import storybook.action.LangToolAction;
import storybook.action.NewEntityAction;
import storybook.action.ShowInBookViewAction;
import storybook.action.ShowInChronoViewAction;
import storybook.action.ShowInGoogleMapsAction;
import storybook.action.ShowInManageViewAction;
import storybook.action.ShowInMemoriaAction;
import storybook.action.ShowInfoAction;
import storybook.controller.BookController;
import storybook.model.handler.AbstractEntityHandler;
import storybook.model.handler.CategoryEntityHandler;
import storybook.model.handler.ChapterEntityHandler;
import storybook.model.handler.GenderEntityHandler;
import storybook.model.handler.IdeaEntityHandler;
import storybook.model.handler.InternalEntityHandler;
import storybook.model.handler.ItemEntityHandler;
import storybook.model.handler.ItemLinkEntityHandler;
import storybook.model.handler.LocationEntityHandler;
import storybook.model.handler.PartEntityHandler;
import storybook.model.handler.PersonEntityHandler;
import storybook.model.handler.RelationshipEntityHandler;
import storybook.model.handler.SceneEntityHandler;
import storybook.model.handler.StrandEntityHandler;
import storybook.model.handler.TagEntityHandler;
import storybook.model.handler.TagLinkEntityHandler;
import storybook.model.handler.TimeEventEntityHandler;
import storybook.model.hbn.dao.AttributeDAOImpl;
import storybook.model.hbn.dao.CategoryDAOImpl;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.GenderDAOImpl;
import storybook.model.hbn.dao.IdeaDAOImpl;
import storybook.model.hbn.dao.ItemDAOImpl;
import storybook.model.hbn.dao.ItemLinkDAOImpl;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.dao.SbGenericDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.dao.StrandDAOImpl;
import storybook.model.hbn.dao.TagDAOImpl;
import storybook.model.hbn.dao.TagLinkDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.AbstractTag;
import storybook.model.hbn.entity.AbstractTagLink;
import storybook.model.hbn.entity.Attribute;
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
import storybook.toolkit.BookUtil;
import storybook.toolkit.DateUtil;
import storybook.toolkit.I18N;
import storybook.toolkit.TextUtil;
import storybook.toolkit.html.HtmlUtil;
import storybook.toolkit.swing.AutoCompleteComboBox;
import storybook.toolkit.swing.FontUtil;
import storybook.ui.MainFrame;
import storybook.ui.table.SbColumn;

/**
 * @author martin
 *
 */
public class EntityUtil {

	public static Boolean isPersonAlive(Person person, Date now) {
		if (person.getDayofdeath() == null)
			return true;
		return (!now.after(person.getDayofdeath()));
	}

	public static int calculatePersonAge(Person person, Date date) {
		Date birthday = person.getBirthday();
		if (birthday == null)
			return -1;

		// Create a calendar object with the date of birth
		Calendar dateOfBirth = new GregorianCalendar();
		dateOfBirth.setTime(birthday);

		// person already dead?
		if (!isPersonAlive(person, date)) {
			Calendar death = new GregorianCalendar();
			death.setTime(person.getDayofdeath());
			int age = death.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
			Calendar dateOfBirth2 = new GregorianCalendar();
			dateOfBirth2.add(Calendar.YEAR, age);
			if (death.before(dateOfBirth2))
				age--;
			return age;
		}

		// create a calendar object with today's date
		Calendar today = new GregorianCalendar();
		today.setTime(date);

		// get age based on year
		int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);

		// add the tentative age to the date of birth to get this year's
		// birthday
		dateOfBirth.add(Calendar.YEAR, age);

		// if this year's birthday has not happened yet, subtract one from age
		if (today.before(dateOfBirth))
			age--;
		return age;
	}

	public static void convertPlainTextToHtml(MainFrame mainFrame) {
		boolean useHtmlScenes = BookUtil.isUseHtmlScenes(mainFrame);
		boolean useHtmlDescr = BookUtil.isUseHtmlDescr(mainFrame);
		if (!useHtmlScenes && !useHtmlDescr)
			return;
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();

		// scenes
		SceneDAOImpl sceneDao = new SceneDAOImpl(session);
		List<Scene> scenes = sceneDao.findAll();
		for (Scene scene : scenes) {
			if (useHtmlScenes)
				scene.setSummary(HtmlUtil.textToHTML(scene.getSummary()));
			if (useHtmlDescr)
				scene.setNotes(HtmlUtil.textToHTML(scene.getNotes()));
			session.update(scene);
		}

		// chapters
		ChapterDAOImpl chapterDao = new ChapterDAOImpl(session);
		List<Chapter> chapters = chapterDao.findAll();
		for (Chapter chapter : chapters) {
			if (useHtmlDescr) {
				chapter.setDescription(HtmlUtil.textToHTML(chapter.getDescription()));
				chapter.setNotes(HtmlUtil.textToHTML(chapter.getNotes()));
			}
		}

		// persons
		PersonDAOImpl personDao = new PersonDAOImpl(session);
		List<Person> persons = personDao.findAll();
		for (Person person : persons) {
			if (useHtmlDescr) {
				person.setDescription(HtmlUtil.textToHTML(person.getDescription()));
				person.setNotes(HtmlUtil.textToHTML(person.getNotes()));
			}
		}

		// locations
		LocationDAOImpl locationDao = new LocationDAOImpl(session);
		List<Location> locations = locationDao.findAll();
		for (Location location : locations) {
			if (useHtmlDescr) {
				location.setDescription(HtmlUtil.textToHTML(location.getDescription()));
				location.setNotes(HtmlUtil.textToHTML(location.getNotes()));
			}
		}

		// tags
		TagDAOImpl tagDao = new TagDAOImpl(session);
		List<Tag> tags = tagDao.findAll();
		for (Tag tag : tags) {
			if (useHtmlDescr) {
				tag.setDescription(HtmlUtil.textToHTML(tag.getDescription()));
				tag.setNotes(HtmlUtil.textToHTML(tag.getNotes()));
			}
		}

		// items
		ItemDAOImpl itemDao = new ItemDAOImpl(session);
		List<Item> items = itemDao.findAll();
		for (Item item : items) {
			if (useHtmlDescr) {
				item.setDescription(HtmlUtil.textToHTML(item.getDescription()));
				item.setNotes(HtmlUtil.textToHTML(item.getNotes()));
			}
		}

		// ideas
		IdeaDAOImpl ideaDao = new IdeaDAOImpl(session);
		List<Idea> ideas = ideaDao.findAll();
		for (Idea idea : ideas) {
			if (useHtmlDescr)
				idea.setNotes(HtmlUtil.textToHTML(idea.getNotes()));
		}

		model.commit();

	}

	public static void convertHtmlToPlainText(MainFrame mainFrame) {
		boolean usePlainTextScenes = !BookUtil.isUseHtmlScenes(mainFrame);
		boolean usePlainTextDescr = !BookUtil.isUseHtmlDescr(mainFrame);
		if (!usePlainTextScenes && !usePlainTextDescr)
			return;
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();

		// scenes
		SceneDAOImpl sceneDao = new SceneDAOImpl(session);
		List<Scene> scenes = sceneDao.findAll();
		for (Scene scene : scenes) {
			if (usePlainTextScenes)
				scene.setSummary(HtmlUtil.htmlToText(scene.getSummary(), true));
			if (usePlainTextDescr)
				scene.setNotes(HtmlUtil.htmlToText(scene.getNotes()));
			session.update(scene);
		}

		// chapters
		ChapterDAOImpl chapterDao = new ChapterDAOImpl(session);
		List<Chapter> chapters = chapterDao.findAll();
		for (Chapter chapter : chapters) {
			if (usePlainTextDescr) {
				chapter.setDescription(HtmlUtil.htmlToText(
						chapter.getDescription(), true));
				chapter.setNotes(HtmlUtil.htmlToText(chapter.getNotes(), true));
			}
		}

		// persons
		PersonDAOImpl personDao = new PersonDAOImpl(session);
		List<Person> persons = personDao.findAll();
		for (Person person : persons) {
			if (usePlainTextDescr) {
				person.setDescription(HtmlUtil.htmlToText(
						person.getDescription(), true));
				person.setNotes(HtmlUtil.htmlToText(person.getNotes(), true));
			}
		}

		// locations
		LocationDAOImpl locationDao = new LocationDAOImpl(session);
		List<Location> locations = locationDao.findAll();
		for (Location location : locations) {
			if (usePlainTextDescr) {
				location.setDescription(HtmlUtil.htmlToText(
						location.getDescription(), true));
				location.setNotes(HtmlUtil.htmlToText(location.getNotes(), true));
			}
		}

		// tags
		TagDAOImpl tagDao = new TagDAOImpl(session);
		List<Tag> tags = tagDao.findAll();
		for (Tag tag : tags) {
			if (usePlainTextDescr) {
				tag.setDescription(HtmlUtil.htmlToText(tag.getDescription(),
						true));
				tag.setNotes(HtmlUtil.htmlToText(tag.getNotes(), true));
			}
		}

		// items
		ItemDAOImpl itemDao = new ItemDAOImpl(session);
		List<Item> items = itemDao.findAll();
		for (Item item : items) {
			if (usePlainTextDescr) {
				item.setDescription(HtmlUtil.htmlToText(item.getDescription(), true));
				item.setNotes(HtmlUtil.htmlToText(item.getNotes(), true));
			}
		}

		// ideas
		IdeaDAOImpl ideaDao = new IdeaDAOImpl(session);
		List<Idea> ideas = ideaDao.findAll();
		for (Idea idea : ideas) {
			if (usePlainTextDescr)
				idea.setNotes(HtmlUtil.htmlToText(idea.getNotes(), true));
		}

		model.commit();

	}

	public static List<Long> getReadOnlyIds(AbstractEntity entity) {
		ArrayList<Long> ret = new ArrayList<>();
		if (entity instanceof Category) {
			ret.add(1L);
			ret.add(2L);
		} else if (entity instanceof Gender) {
			ret.add(1L);
			ret.add(2L);
		} else if (entity instanceof Part)
			ret.add(1L);
		else if (entity instanceof Strand)
			ret.add(1L);
		return ret;
	}

	public static Date findFirstDate(MainFrame mainFrame) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		Date date = dao.findFirstDate();
		model.commit();
		return date;
	}

	public static Date findLastDate(MainFrame mainFrame) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		Date date = dao.findLastDate();
		model.commit();
		return date;
	}

	public static void printBeanProperties(AbstractEntity entity) {
		SbApp.trace("EntityUtil.printBeanProperties(" + entity.getClass().getName() + ")");
		try {
			BeanInfo bi = Introspector.getBeanInfo(entity.getClass());
			for (PropertyDescriptor propDescr : bi.getPropertyDescriptors()) {
				String name = propDescr.getName();
				Object val = propDescr.getReadMethod().invoke(entity);
				String isNull = "not null";
				if (val == null)
					isNull = "is null";
				System.out.println("EntityUtil.printProperties(): " + name
						+ ": '" + val + "' " + (val == null ? "isNull" : "not null"));
			}
		} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.err.println("EntityUtil.printBeanProperties(" + entity + ") Exception : " + e.getMessage());
		}
	}

	public static void deleteTagAndItemLinks(BookModel model, AbstractEntity entity) {
		Session session = model.beginTransaction();
		TagLinkDAOImpl tagLinkDao = new TagLinkDAOImpl(session);
		List<TagLink> tagLinks = null;
		if (entity instanceof Scene)
			tagLinks = tagLinkDao.findByStartOrEndScene((Scene) entity);
		else if (entity instanceof Person)
			tagLinks = tagLinkDao.findByPerson((Person) entity);
		else if (entity instanceof Location)
			tagLinks = tagLinkDao.findByLocation((Location) entity);
		session.close();
		if (tagLinks != null && !tagLinks.isEmpty())
			for (TagLink link : tagLinks) {
				model.setDeleteTagLink(link);
			}
		session = model.beginTransaction();
		ItemLinkDAOImpl itemLinkDao = new ItemLinkDAOImpl(session);
		List<ItemLink> itemLinks = null;
		if (entity instanceof Scene)
			itemLinks = itemLinkDao.findByStartOrEndScene((Scene) entity);
		else if (entity instanceof Person)
			itemLinks = itemLinkDao.findByPerson((Person) entity);
		else if (entity instanceof Location)
			itemLinks = itemLinkDao.findByLocation((Location) entity);
		session.close();
		if (itemLinks != null && !itemLinks.isEmpty())
			for (ItemLink link : itemLinks) {
				model.setDeleteItemLink(link);
			}

	}

	public static void copyEntityProperties(MainFrame mainFrame, AbstractEntity entity,
			AbstractEntity newEntity) {
		try {
			ConvertUtils.register(new DateConverter(null), Date.class);
			ConvertUtils.register(new SqlTimestampConverter(null), Timestamp.class);
			ConvertUtils.register(new NullConverter(), Integer.class);
			ConvertUtils.register(new NullConverter(), Long.class);
			BeanUtils.copyProperties(newEntity, entity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			System.err.println("EntityUtil.copyEntityProperties() Exception : " + e.getMessage());
		}
	}

	public static AbstractEntity cloneEntity(MainFrame mainFrame, AbstractEntity entity) {
		try {
			ConvertUtils.register(new DateConverter(null), Date.class);
			ConvertUtils.register(new SqlTimestampConverter(null), Timestamp.class);
			ConvertUtils.register(new NullConverter(), Integer.class);
			return (AbstractEntity) BeanUtils.cloneBean(entity);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			System.err.println("EntityUtil.cloneEntityProperties() Exception : " + e.getMessage());
		}
		return null;
	}

	public static void copyEntity(MainFrame mainFrame, AbstractEntity entity) {
		AbstractEntityHandler handler = getEntityHandler(mainFrame, entity);
		AbstractEntity newEntity = handler.createNewEntity();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		session.refresh(entity);
		copyEntityProperties(mainFrame, entity, newEntity);
//		AbstractEntity newEntity = cloneEntity(mainFrame, entity);
		markCopiedEntity(mainFrame, newEntity);

		List<Person> persons = new ArrayList<>();
		List<Location> locations = new ArrayList<>();
		List<Item> items = new ArrayList<>();
		List<Strand> strands = new ArrayList<>();
		if (entity instanceof Scene) {
			// correct date / relative scene
			Scene scene = (Scene) entity;
			Scene newScene = (Scene) newEntity;
			if (!scene.hasRelativeScene())
				newScene.removeRelativeScene();
			persons = scene.getPersons();
			locations = scene.getLocations();
			strands = scene.getStrands();
		}
		List<Attribute> attributes = new ArrayList<>();
		if (entity instanceof Person) {
			Person person = (Person) entity;
			attributes = person.getAttributes();
		}
		model.commit();
		BookController ctrl = mainFrame.getBookController();
		ctrl.newEntity(newEntity);

		// re-set "stolen" bag links
		if (entity instanceof Scene) {
			Scene scene = (Scene) entity;
			
			List<Person> copyPersons = new ArrayList<>();
			for (Person person : persons) {
				copyPersons.add(person);
			}
			scene.setPersons(copyPersons);
			
			List<Location> copyLocations = new ArrayList<>();
			for (Location location : locations) {
				copyLocations.add(location);
			}
			scene.setLocations(copyLocations);
			
			List<Strand> copyStrands = new ArrayList<>();
			for (Strand strand : strands) {
				copyStrands.add(strand);
			}
			scene.setStrands(copyStrands);
		}
		if (entity instanceof Person) {
			Person person = (Person) entity;
			List<Attribute> copyAttributes = new ArrayList<>();
			for (Attribute attribute : attributes) {
				copyAttributes.add(attribute);
			}
			person.setAttributes(copyAttributes);
		}
		ctrl.updateEntity(entity);

	}

	private static void markCopiedEntity(MainFrame mainFrame, AbstractEntity entity) {
		String copyStr = "(" + I18N.getMsg("msg.common.copy") + ") ";
		if (entity instanceof Scene) {
			Scene e = (Scene) entity;
			e.setTitle(copyStr + e.getTitle());
			return;
		}
		if (entity instanceof Chapter) {
			Chapter e = (Chapter) entity;
			e.setTitle(copyStr + e.getTitle());
			return;
		}
		if (entity instanceof Part) {
			Part e = (Part) entity;
			e.setName(copyStr + e.getName());
			return;
		}
		if (entity instanceof Strand) {
			Strand e = (Strand) entity;
			e.setName(copyStr + e.getName());
			return;
		}
		if (entity instanceof Person) {
			Person e = (Person) entity;
			e.setFirstname(copyStr + e.getFirstname());
			return;
		}
		if (entity instanceof Relationship) {
			// nothing to copy
			return;
		}
		if (entity instanceof Category) {
			Category e = (Category) entity;
			e.setName(copyStr + e.getName());
			return;
		}
		if (entity instanceof Gender) {
			Gender e = (Gender) entity;
			e.setName(copyStr + e.getName());
			return;
		}
		if (entity instanceof Location) {
			Location e = (Location) entity;
			e.setName(copyStr + e.getName());
			return;
		}
		if (entity instanceof AbstractTag) {
			AbstractTag e = (AbstractTag) entity;
			e.setName(copyStr + e.getName());
			return;
		}
		if (entity instanceof Idea) {
			Idea e = (Idea) entity;
			e.setNotes(copyStr + e.getNotes());
			return;
		}
		if (entity instanceof AbstractTagLink) {
			// no string value can be changed
			//return;
		}
	}

	public static boolean hasHierarchyChanged(AbstractEntity oldEntity, AbstractEntity updEntity) {
		if (oldEntity == null || updEntity == null)
			return false;
		if (oldEntity instanceof Idea) {
			Idea old = (Idea) oldEntity;
			Idea upd = (Idea) updEntity;
			return !Objects.equals(old.getStatus(), upd.getStatus());
		}
		if (oldEntity instanceof Scene) {
			Scene old = (Scene) oldEntity;
			Scene upd = (Scene) updEntity;
			if (old.getChapter() == null && upd.getChapter() != null)
				return true;
			if (old.getChapter() != null && upd.getChapter() == null)
				return true;
			return old.getChapter() != null && upd.getChapter() != null
					&& (!Objects.equals(old.getChapter().getId(), upd.getChapter().getId()));
		}
		if (oldEntity instanceof Person) {
			Person old = (Person) oldEntity;
			Person upd = (Person) updEntity;
			if (!Objects.equals(old.getCategory().getId(), upd.getCategory().getId()))
				return true;
			return !Objects.equals(old.getGender().getId(), upd.getGender().getId());
		}
		if (oldEntity instanceof Relationship) {
			return false;
		}
		if (oldEntity instanceof Location) {
			Location old = (Location) oldEntity;
			Location upd = (Location) updEntity;
			Location oldSite = (Location) old.getSite();
			Location updSite = (Location) upd.getSite();
			String oldCity = old.getCity();
			String updCity = upd.getCity();
			String oldCountry = old.getCountry();
			String updCountry = upd.getCountry();
			if (oldSite == null && updSite != null)
				return true;
			if (oldSite != null && updSite == null)
				return true;
			if (!oldSite.equals(updSite))
				return true;
			if (oldCity == null && updCity != null)
				return true;
			if (oldCity != null && updCity == null)
				return true;
			if (!oldCity.equals(updCity))
				return true;
			if (oldCountry == null && updCountry != null)
				return true;
			if (oldCountry != null && updCountry == null)
				return true;
			return !oldCountry.equals(updCountry);
		}
		if (oldEntity instanceof AbstractTag) {
			AbstractTag old = (AbstractTag) oldEntity;
			AbstractTag upd = (AbstractTag) updEntity;
			if (old.getCategory() == null && upd.getCategory() == null)
				return false;
			if (old.getCategory() != null || upd.getCategory() != null)
				return true;
			return !old.getCategory().equals(upd.getCategory());
		}
		return false;
	}

	public static JPopupMenu createPopupMenu(MainFrame mainFrame, AbstractEntity entity) {
		JPopupMenu menu = new JPopupMenu();
		if (entity == null)
			return null;
		if (entity.isTransient())
			return null;
		JLabel lbTitle = new JLabel("   " + entity.toString());
		lbTitle.setFont(FontUtil.getBoldFont());
		menu.add(lbTitle);
		menu.add(new JPopupMenu.Separator());
		menu.add(new EditEntityAction(mainFrame, entity, false));
		if (entity instanceof Scene) {
			if (BookUtil.isUseLibreOffice(mainFrame)) {
				menu.add(new EditSceneLOAction(mainFrame, entity));
			}
		}
		menu.add(new DeleteEntityAction(mainFrame, entity));
		menu.add(new JPopupMenu.Separator());
		if (entity instanceof Scene || entity instanceof Chapter) {
			menu.add(new ShowInChronoViewAction(mainFrame, entity));
			menu.add(new ShowInBookViewAction(mainFrame, entity));
			menu.add(new ShowInManageViewAction(mainFrame, entity));
		}
		menu.add(new ShowInfoAction(mainFrame, entity));
		if (isAvailableInMemoria(entity))
			menu.add(new ShowInMemoriaAction(mainFrame, entity));
		menu.add(new JPopupMenu.Separator());
		if (entity instanceof Scene) {
			menu.add(new LangToolAction(mainFrame, (Scene) entity));
			menu.add(new JPopupMenu.Separator());
		}
		if (entity instanceof Chapter) {
			menu.add(new ChapterOrderByTimestampAction(mainFrame,
					(Chapter) entity));
			menu.add(new ChapterReSortAction(mainFrame, (Chapter) entity));
			menu.add(new JPopupMenu.Separator());
		}
		menu.add(new NewEntityAction(mainFrame, entity));
		if (entity instanceof Location) {
			// google maps
			menu.add(new JPopupMenu.Separator());
			menu.add(new ShowInGoogleMapsAction((Location) entity));
		}
		if (menu.getComponents().length == 0)
			return null;
		return menu;
	}

	public static boolean isAvailableInMemoria(AbstractEntity entity) {
		return entity instanceof Person || entity instanceof Location
				|| entity instanceof Scene || entity instanceof Tag
				|| entity instanceof Item;
	}

	public static List<Attribute> getEntityAttributes(MainFrame mainFrame, AbstractEntity entity) {
		if (entity.isTransient())
			return new ArrayList<>();
		if (entity instanceof Person) {
			BookModel model = mainFrame.getBookModel();
			Session session = model.beginTransaction();
			Person person = (Person) entity;
			session.refresh(person);
			List<Attribute> ret = person.getAttributes();
			model.commit();
			return ret;
		}
		return new ArrayList<>();
	}

	public static void setEntityAttributes(MainFrame mainFrame, AbstractEntity entity, List<Attribute> attributes) {
		if (entity.isTransient())
			return;
		if (entity instanceof Person)
			try {
				Person person = (Person) entity;
				BookModel model = mainFrame.getBookModel();

				// delete attributes
				Session session = model.beginTransaction();
				person.setAttributes(new ArrayList<>());
				session.saveOrUpdate(person);

				// delete orphans
				AttributeDAOImpl dao = new AttributeDAOImpl(session);
				dao.deleteOrphans();

				// add attributes
				for (Attribute property : attributes) {
					session.save(property);
				}
				model.commit();

				// update person
				person.setAttributes(attributes);
				mainFrame.getBookController().updatePerson(person);
			} catch (HibernateException e) {
				SbApp.error("EntityUtil.copyEntityProperties()", e);
			}
	}

	public static AbstractEntityHandler getEntityHandler(MainFrame mainFrame,
			AbstractEntity entity) {
		if (entity instanceof Scene)
			return new SceneEntityHandler(mainFrame);
		if (entity instanceof Chapter)
			return new ChapterEntityHandler(mainFrame);
		if (entity instanceof Part)
			return new PartEntityHandler(mainFrame);
		if (entity instanceof Location)
			return new LocationEntityHandler(mainFrame);
		if (entity instanceof Person)
			return new PersonEntityHandler(mainFrame);
		if (entity instanceof Relationship)
			return new RelationshipEntityHandler(mainFrame);
		if (entity instanceof Gender)
			return new GenderEntityHandler(mainFrame);
		if (entity instanceof Category)
			return new CategoryEntityHandler(mainFrame);
		if (entity instanceof Strand)
			return new StrandEntityHandler(mainFrame);
		if (entity instanceof Idea)
			return new IdeaEntityHandler(mainFrame);
		if (entity instanceof Tag)
			return new TagEntityHandler(mainFrame);
		if (entity instanceof Item)
			return new ItemEntityHandler(mainFrame);
		if (entity instanceof TagLink)
			return new TagLinkEntityHandler(mainFrame);
		if (entity instanceof ItemLink)
			return new ItemLinkEntityHandler(mainFrame);
		if (entity instanceof Internal)
			return new InternalEntityHandler(mainFrame);
		if (entity instanceof TimeEvent)
			return new TimeEventEntityHandler(mainFrame);
		return null;
	}

	public static Class<?> getEntityClass(AbstractEntity entity) {
		// note: hibernate sometimes returns
		// "entity.Tag_$$_javassist_2", which matches to "instanceof Tag",
		// but cannot be used as a class "Tag" parameter in reflection
		if (entity instanceof Person) return Person.class;
		if (entity instanceof Relationship) return Relationship.class;
		if (entity instanceof Category) return Category.class;
		if (entity instanceof Gender) return Gender.class;
		if (entity instanceof Location) return Location.class;
		if (entity instanceof Scene) return Scene.class;
		if (entity instanceof Chapter) return Chapter.class;
		if (entity instanceof Part) return Part.class;
		if (entity instanceof Tag) return Tag.class;
		if (entity instanceof TagLink) return TagLink.class;
		if (entity instanceof Item) return Item.class;
		if (entity instanceof ItemLink) return ItemLink.class;
		if (entity instanceof Strand) return Strand.class;
		if (entity instanceof Idea) return Idea.class;
		if (entity instanceof Internal) return Internal.class;
		if (entity instanceof TimeEvent) return TimeEvent.class;
		return null;
	}

	public static List<JCheckBox> createCategoryCheckBoxes(MainFrame mainFrame,
			ActionListener comp) {
		List<JCheckBox> list = new ArrayList<>();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		CategoryDAOImpl dao = new CategoryDAOImpl(session);
		List<Category> categories = dao.findAllOrderBySort();
		model.commit();
		for (Category category : categories) {
			JCheckBox cb = new JCheckBox(category.getName());
			cb.putClientProperty(SbConstants.ComponentName.CB_CATEGORY,
					category);
			cb.setOpaque(false);
			cb.addActionListener(comp);
			cb.setSelected(true);
			list.add(cb);
		}
		return list;
	}

	public static List<JCheckBox> createCountryCheckBoxes(MainFrame mainFrame,
			ActionListener comp) {
		List<JCheckBox> list = new ArrayList<>();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		LocationDAOImpl dao = new LocationDAOImpl(session);
		List<String> countries = dao.findCountries();
		model.commit();
		for (String country : countries) {
			JCheckBox chb = new JCheckBox(country);
			chb.setOpaque(false);
			chb.addActionListener(comp);
			chb.setSelected(true);
			list.add(chb);
		}
		return list;
	}

	public static List<JCheckBox> createPersonCheckBoxes(MainFrame mainFrame,
			List<JCheckBox> cbCategoryList, ActionListener comp) {
		List<JCheckBox> list = new ArrayList<>();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PersonDAOImpl dao = new PersonDAOImpl(session);
		for (JCheckBox cb : cbCategoryList) {
			if (cb.isSelected()) {
				Category category = (Category) cb
						.getClientProperty(SbConstants.ComponentName.CB_CATEGORY);
				List<Person> persons = dao.findByCategory(category);
				for (Person person : persons) {
					JCheckBox cbPerson = new JCheckBox(person.getFullNameAbbr());
					cbPerson.setOpaque(false);
					cbPerson.putClientProperty(
							SbConstants.ComponentName.CB_PERSON, person);
					cbPerson.addActionListener(comp);
					list.add(cbPerson);
				}
			}
		}
		model.commit();
		return list;
	}

	public static List<JCheckBox> createItemCheckBoxes(MainFrame m, ActionListener comp) {
		List<JCheckBox> list = new ArrayList<>();
		BookModel model = m.getBookModel();
		Session session = model.beginTransaction();
		ItemDAOImpl dao = new ItemDAOImpl(session);
		List<Item> items = dao.findAll();
		for (Item item : items) {
			JCheckBox cbItem = new JCheckBox(item.getName());
			cbItem.setOpaque(false);
			cbItem.putClientProperty(SbConstants.ComponentName.CB_ITEM, item);
			cbItem.addActionListener(comp);
			list.add(cbItem);
		}
		model.commit();
		return list;
	}

	public static void renumberScenes(MainFrame mainFrame, Chapter chapter) {
		BookModel model = mainFrame.getBookModel();
		BookController ctrl = mainFrame.getBookController();
		Session session = model.beginTransaction();
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		List<Scene> scenes = dao.findScenes(chapter);
		session.close();
		int counter = 1;
		for (Scene scene : scenes) {
			scene.setSceneno(counter);
			ctrl.updateScene(scene);
			++counter;
		}
	}

	public static Scene createScene(Strand strand, Chapter chapter) {
		Scene scene = new Scene();
		scene.setStrand(strand);
		scene.setStatus(1);
		scene.setChapter(chapter);
		scene.setSceneno(1);
		scene.setDate(new Date());
		scene.setTitle("scene 1");
		scene.setSummary("scene text");
		scene.setNotes("");
		return scene;
	}

	public static void abandonEntityChanges(MainFrame mainFrame,
			AbstractEntity entity) {
		try {
			if (entity.isTransient())
				// nothing to do for a new entity
				return;
			BookModel model = mainFrame.getBookModel();
			Session session = model.getSession();
			if (session != null && session.isOpen()) {
				Transaction transaction = session.beginTransaction();
				session.refresh(entity);
				transaction.commit();
			}
		} catch (HibernateException e) {
			SbApp.error("EntityUtil.copyEntityProperties()", e);
		}
	}

	public static String getToolTip(AbstractEntity entity) {
		return getToolTip(entity, null);
	}

	public static String getToolTip(AbstractEntity entity, Date date) {
		StringBuffer buf = new StringBuffer();
		buf.append("<html>");
		buf.append("<table width='300'>");
		buf.append("<tr><td>");
		buf.append(HtmlUtil.getTitle(entity.toString()));
		if (entity instanceof Person)
			toolTipAppendPerson(buf, (Person) entity, date);
		else if (entity instanceof Scene)
			toolTipAppendScene(buf, (Scene) entity);
		else if (entity instanceof Location)
			toolTipAppendLocation(buf, (Location) entity);
		else if (entity instanceof AbstractTag)
			toolTipAppendTag(buf, (AbstractTag) entity);
		buf.append("</td></tr>");
		buf.append("</table>");
		buf.append("</html>");
		return buf.toString();
	}

	private static void toolTipAppendTag(StringBuffer buf, AbstractTag tag) {
		buf.append(TextUtil.truncateText(tag.getDescription()));
	}

	private static void toolTipAppendLocation(StringBuffer buf, Location location) {
		buf.append(I18N.getMsgColon("msg.dlg.location.city"));
		buf.append(" ");
		buf.append(location.getCity());
		buf.append("<br>");
		buf.append(I18N.getMsgColon("msg.dlg.location.country"));
		buf.append(" ");
		buf.append(location.getCountry());
		buf.append("<p style='margin-top:5px'>");
		buf.append(TextUtil.truncateText(location.getDescription()));
	}

	private static void toolTipAppendScene(StringBuffer buf, Scene scene) {
		buf.append(scene.getSummary(true, 600));
	}

	private static void toolTipAppendPerson(StringBuffer buf, Person person,
			Date date) {
		if (date != null && person.getBirthday() != null) {
			buf.append(I18N.getMsgColon("msg.dlg.person.age"));
			buf.append(" ");
			buf.append(calculatePersonAge(person, date));
			if (!isPersonAlive(person, date))
				buf.append("+");
			buf.append("<br>");
		}
		buf.append(I18N.getMsgColon("msg.dlg.mng.persons.gender"));
		buf.append(" ");
		buf.append(person.getGender());
		buf.append("<br>");
		buf.append(I18N.getMsgColon("msg.dlg.mng.persons.category"));
		buf.append(" ");
		buf.append(person.getCategory());
		buf.append("<p style='margin-top:5px'>");
		buf.append(TextUtil.truncateText(person.getDescription()));
	}

	public static String getDeleteInfo(MainFrame mainFrame,
			AbstractEntity entity) {
		StringBuffer buf = new StringBuffer();
		buf.append(HtmlUtil.getHeadWithCSS());
		boolean warnings = addDeletionInfo(mainFrame, entity, buf);
		if (warnings)
			buf.append(HtmlUtil.getHr());
		buf.append(getInfo(mainFrame, entity, true));
		return buf.toString();
	}

	public static String getInfo(MainFrame mainFrame, AbstractEntity entity) {
		StringBuilder buf = new StringBuilder();
		buf.append(HtmlUtil.getHeadWithCSS());
		buf.append(getInfo(mainFrame, entity, false));
		return buf.toString();
	}

	private static String getInfo(MainFrame mainFrame, AbstractEntity entity, boolean truncate) {
		StringBuffer buf = new StringBuffer();
		addInfo(mainFrame, entity, buf, truncate);
		return buf.toString();
	}

	private static boolean addDeletionInfo(MainFrame mainFrame, AbstractEntity entity, StringBuffer buf) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		boolean warnings = false;

		if (entity instanceof Category) {
			Category category = (Category) entity;
			CategoryDAOImpl dao = new CategoryDAOImpl(session);
			List<Person> persons = dao.findPersons(category);
			if (!persons.isEmpty()) {
				buf.append("<p style='padding-top:10px'>");
				buf.append(HtmlUtil.getWarning(I18N.getMsg("msg.category.delete.warning")));
				buf.append("</p><br>");
				buf.append(I18N.getMsgColon("msg.common.persons"));
				buf.append("<br><ul>");
				for (Person person : persons) {
					buf.append("<li>");
					buf.append(person);
					buf.append("</li>\n");
				}
				buf.append("</ul>");
				warnings = true;
			}
		} else if (entity instanceof Gender) {
			Gender gender = (Gender) entity;
			GenderDAOImpl dao = new GenderDAOImpl(session);
			List<Person> persons = dao.findPersons(gender);
			if (!persons.isEmpty()) {
				buf.append("<p style='padding-top:10px'>");
				buf.append(HtmlUtil.getWarning(I18N.getMsg("msg.gender.delete.warning")));
				buf.append("</p><br>");
				buf.append(I18N.getMsgColon("msg.common.persons"));
				buf.append("<br><ul>");
				for (Person person : persons) {
					buf.append("<li>");
					buf.append(person);
					buf.append("</li>\n");
				}
				buf.append("</ul>");
				warnings = true;
			}
		} else if (entity instanceof Chapter) {
			Chapter chapter = (Chapter) entity;
			ChapterDAOImpl dao = new ChapterDAOImpl(session);
			List<Scene> scenes = dao.findScenes(chapter);
			if (!scenes.isEmpty()) {
				buf.append("<p style='margin-top:10px'>");
				buf.append(HtmlUtil.getWarning(I18N
						.getMsg("msg.warning.chapter.has.assigned.scenes")));
				buf.append("</p><br>");
				buf.append(I18N.getMsgColon("msg.common.scenes"));
				buf.append("<br><ul>");
				for (Scene scene : scenes) {
					buf.append("<li>");
					buf.append(scene);
					buf.append("</li>\n");
				}
				buf.append("</ul>");
				warnings = true;
			}
		} else if (entity instanceof Scene) {
			Scene scene = (Scene) entity;
			SceneDAOImpl dao = new SceneDAOImpl(session);
			List<Scene> scenes = dao.findScenesWithRelativeSceneId(scene);
			if (!scenes.isEmpty()) {
				buf.append("<p style='padding-top:10px'>");
				buf.append(HtmlUtil.getWarning(I18N.getMsg("msg.relativedate.delete.warning")));
				buf.append("</p><br>");
				buf.append(I18N.getMsgColon("msg.common.scenes"));
				buf.append("<br><ul>");
				for (Scene scene2 : scenes) {
					buf.append("<li>");
					buf.append(scene2);
					buf.append("</li>\n");
				}
				buf.append("</ul>");
				warnings = true;
			}
		} else if (entity instanceof Part) {
			Part part = (Part) entity;
			PartDAOImpl dao = new PartDAOImpl(session);
			List<Chapter> chapters = dao.findChapters(part);
			if (!chapters.isEmpty()) {
				buf.append("<p style='padding-top:10px'>");
				buf.append(HtmlUtil.getWarning(I18N.getMsg("msg.dlg.mng.parts.delete.warning")));
				buf.append("</p><br>");
				buf.append(I18N.getMsgColon("msg.common.chapters"));
				buf.append("<br><ul>");
				for (Chapter chapter : chapters) {
					buf.append("<li>");
					buf.append(chapter);
					buf.append("</li>\n");
				}
				buf.append("</ul>");
				warnings = true;
			}
		} else if (entity instanceof Strand) {
			Strand strand = (Strand) entity;
			StrandDAOImpl dao = new StrandDAOImpl(session);
			List<Scene> scenes = dao.findScenes(strand);
			if (!scenes.isEmpty()) {
				buf.append("<p style='padding-top:10px'>");
				buf.append(HtmlUtil.getWarning(I18N.getMsg("msg.dlg.mng.strands.delete.warning")));
				buf.append("</p><br>");
				buf.append(I18N.getMsgColon("msg.common.scenes"));
				buf.append("<br><ul>");
				for (Scene scene : scenes) {
					buf.append("<li>");
					buf.append(scene);
					buf.append("</li>\n");
				}
				buf.append("</ul>");
				warnings = true;
			}
		} else if (entity instanceof Tag) {
			Tag tag = (Tag) entity;
			TagLinkDAOImpl dao = new TagLinkDAOImpl(session);
			List<TagLink> links = dao.findByTag(tag);
			if (!links.isEmpty()) {
				buf.append("<p style='padding-top:10px'>");
				buf.append(HtmlUtil.getWarning(I18N.getMsg("msg.tags.links.warning.delete.all")));
				buf.append("</p><br>");
				buf.append(I18N.getMsgColon("msg.tags.links"));
				buf.append("<br><ul>");
				for (TagLink link : links) {
					buf.append("<li>");
					buf.append(link);
					buf.append("</li>\n");
				}
				buf.append("</ul>");
				warnings = true;
			}
		} else if (entity instanceof Item) {
			Item item = (Item) entity;
			ItemLinkDAOImpl dao = new ItemLinkDAOImpl(session);
			List<ItemLink> links = dao.findByItem(item);
			if (!links.isEmpty()) {
				buf.append("<p style='padding-top:10px'>");
				buf.append(HtmlUtil.getWarning(I18N.getMsg("msg.item.links.warning.delete.all")));
				buf.append("</p><br>");
				buf.append(I18N.getMsgColon("msg.item.assignments"));
				buf.append("<br><ul>");
				for (ItemLink link : links) {
					buf.append("<li>");
					buf.append(link);
					buf.append("</li>\n");
				}
				buf.append("</ul>");
				warnings = true;
			}
		}
		model.commit();
		return warnings;
	}

	private static void addInfo(MainFrame mainFrame, AbstractEntity entity, StringBuffer buf, boolean truncate) {
		AbstractEntityHandler entityHandler = getEntityHandler(mainFrame, entity);
		Class<? extends AbstractEntity> clazz = entity.getClass();
		for (SbColumn col : entityHandler.getColumns()) {
			String methodName = "get" + col.getMethodName();
			if (methodName.equals("getId") || methodName.equals("getIcon"))
				continue;
			if (col.isHideOnInfo())
				continue;
			buf.append("<p>");
			buf.append(HtmlUtil.getBold(col.toString()));
			buf.append(": ");
			try {
				BookModel model = mainFrame.getBookModel();
				Session session = model.beginTransaction();
				session.refresh(entity);
				Method method = clazz.getMethod(methodName);
				Object ret = method.invoke(entity);
				if (ret != null) {
					String str = "";
					if (ret instanceof Boolean)
						str = ((Boolean) ret ? I18N.getMsg("msg.common.yes") : I18N.getMsg("msg.common.no"));
					else if (ret instanceof List<?>) {
						List<?> list = (List<?>) ret;
						if (!list.isEmpty()) {
							StringBuilder buf2 = new StringBuilder();
							buf2.append("<ul>");
							for (Object o : list) {
								session.refresh(o);
								buf2.append("<li>");
								buf2.append(o.toString());
								buf2.append("</li>");
							}
							buf2.append("</ul>");
							str = buf2.toString();
						}
					} else if (ret instanceof Color) {
						Color clr = (Color) ret;
						str = HtmlUtil.getColorSpan(clr);
					} else if (ret instanceof Date) {
						Date date = (Date) ret;
						DateFormat formatter;
						if (DateUtil.isZeroTimeDate(date)) {
							formatter = I18N.getLongDateFormatter();
							date = DateUtil.getZeroTimeDate(date);
						} else
							if (entity instanceof TimeEvent) {
								formatter = new SimpleDateFormat(((TimeEvent)entity).getStepFormat());
							} else {
							    formatter = I18N.getDateTimeFormatter();
							}
						str = formatter.format(date);
					} else {
						str = ret.toString();
						if (methodName.endsWith("Notes")
								|| methodName.endsWith("Description")
								|| methodName.endsWith("Summary"))
							str = HtmlUtil.getCleanHtml(str);
					}
					if (truncate)
						str = TextUtil.truncateString(HtmlUtil.htmlToText(str), 200);
					buf.append(str);
				}
				model.commit();
			} catch (HibernateException | NoSuchMethodException | SecurityException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				System.err.println("EntityUtil.copyEntityProperties() Exception : " + e.getMessage());
			}
			buf.append("</p>\n");
		}
	}

	public static AbstractEntity get(MainFrame mainFrame, Class<? extends AbstractEntity> c, Long entityId) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		AbstractEntity entity = (AbstractEntity) session.get(c, entityId);
		model.commit();
		return entity;
	}

	public static void refresh(MainFrame mainFrame, AbstractEntity entity) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		session.refresh(entity);
		model.commit();
	}

	public static AbstractEntityHandler getEntityHandler(MainFrame mainFrame,
			Object obj, Method method, AbstractEntity entity) {
		if (obj instanceof Scene || method.getReturnType() == Scene.class)
			return new SceneEntityHandler(mainFrame);
		if (obj instanceof Chapter || method.getReturnType() == Chapter.class)
			return new ChapterEntityHandler(mainFrame);
		if (obj instanceof Gender || method.getReturnType() == Gender.class)
			return new GenderEntityHandler(mainFrame);
		if (obj instanceof Part || method.getReturnType() == Part.class)
			return new PartEntityHandler(mainFrame);
		if (obj instanceof Category || method.getReturnType() == Category.class)
			return new CategoryEntityHandler(mainFrame);
		if (obj instanceof Strand || method.getReturnType() == Strand.class)
			return new StrandEntityHandler(mainFrame);
		if (obj instanceof Person || method.getReturnType() == Person.class)
			return new PersonEntityHandler(mainFrame);
		if (obj instanceof Relationship || method.getReturnType() == Relationship.class)
			return new RelationshipEntityHandler(mainFrame);
		if (obj instanceof Location || method.getReturnType() == Location.class)
			return new LocationEntityHandler(mainFrame);
		if (obj instanceof Tag || method.getReturnType() == Tag.class)
			return new TagEntityHandler(mainFrame);
		if (obj instanceof Item || method.getReturnType() == Item.class)
			return new ItemEntityHandler(mainFrame);
		if (obj instanceof TagLink || method.getReturnType() == TagLink.class)
			return new TagLinkEntityHandler(mainFrame);
		if (obj instanceof ItemLink || method.getReturnType() == ItemLink.class)
			return new ItemLinkEntityHandler(mainFrame);
		if (obj instanceof Internal || method.getReturnType() == Internal.class)
			return new InternalEntityHandler(mainFrame);
		if (obj instanceof TimeEvent || method.getReturnType() == TimeEvent.class)
			return new TimeEventEntityHandler(mainFrame);
		if ((obj != null && obj instanceof Integer) || method.getReturnType() == Integer.class)
			if (method.getName().contains("RelativeScene"))
				return new SceneEntityHandler(mainFrame);
		if ((obj != null && obj instanceof List) || method.getReturnType() == List.class) {
			if (method.getName().endsWith("Persons"))
				return new PersonEntityHandler(mainFrame);
			if (method.getName().endsWith("Items"))
				return new ItemEntityHandler(mainFrame);
			if (method.getName().endsWith("Locations"))
				return new LocationEntityHandler(mainFrame);
			if (method.getName().endsWith("Strands"))
				return new StrandEntityHandler(mainFrame);
		}
		if ((obj != null && obj instanceof String) || method.getReturnType() == String.class) {
			if (method.getName().endsWith("City") || method.getName().endsWith("Country"))
				return new LocationEntityHandler(mainFrame);
			if (method.getName().endsWith("Category")) {
				if (entity instanceof Tag)
					return new TagEntityHandler(mainFrame);
				if (entity instanceof Item)
					return new ItemEntityHandler(mainFrame);
				if (entity instanceof Idea)
					return new IdeaEntityHandler(mainFrame);
				if (entity instanceof TimeEvent)
					return new TimeEventEntityHandler(mainFrame);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static void fillAutoCombo(MainFrame mainFrame,
			AutoCompleteComboBox autoCombo,
			AbstractEntityHandler entityHandler, String text, String methodName) {
		try {
			JComboBox combo = autoCombo.getJComboBox();
			combo.removeAllItems();
			BookModel model = mainFrame.getBookModel();
			Session session = model.beginTransaction();
			SbGenericDAOImpl<?, ?> dao = entityHandler.createDAO();
			dao.setSession(session);
			Method m = dao.getClass().getMethod(methodName, (Class<?>[]) null);
			List<Object> items = (List<Object>) m.invoke(dao);
			model.commit();
			for (Object item : items) {
				if (item == null || ((item instanceof String) && (((String)item).isEmpty())))
					continue;
				combo.addItem(item);
			}
			combo.addItem("");
			combo.getModel().setSelectedItem(text);
			combo.revalidate();
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException 
				| IllegalArgumentException | InvocationTargetException e) {
			SbApp.error("EntityUtil.copyEntityProperties()",e);
		}
	}

	@SuppressWarnings("unchecked")
	public static void fillEntityCombo(MainFrame mainFrame, JComboBox combo,
			AbstractEntityHandler entityHandler, AbstractEntity entity,
			boolean isNew, boolean addEmptyItem) {
		combo.removeAllItems();
		ListCellRenderer renderer = entityHandler.getListCellRenderer();
		if (renderer != null) combo.setRenderer(renderer);
		int i = 0;
		if (addEmptyItem) {
			++i;
			combo.addItem("");
		}
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SbGenericDAOImpl<?, ?> dao = entityHandler.createDAO();
		dao.setSession(session);
		@SuppressWarnings("unchecked")
		List<AbstractEntity> entities = (List<AbstractEntity>) dao.findAll();
		for (AbstractEntity entity2 : entities) {
			session.refresh(entity2);
			combo.addItem(entity2);
			if (entity != null)
				if (entity.getId().equals(entity2.getId()))
					// don't use combo.setSelectedItem(entity) here
					// leads to a "no session" exception for tag links
					combo.setSelectedIndex(i);
			++i;
		}
		combo.revalidate();
		model.commit();
	}

	public static JPanel getEntityTitlePanel(AbstractEntity entity) {
		// GradientPanel panel = new
		// GradientPanel(GradientPanel.DIAGONAL_RIGHT);
		// panel.setForeground(new Color(0x6495ED));
		// panel.setBackground(new Color(255, 255, 255, 0));
		// panel.setLayout(new MigLayout("flowx,ins 2"));
		// panel.add(getEntityIconLabel(entity));
		// panel.add(getEntityTitleLabel(entity));
		JPanel panel = new JPanel(new MigLayout("flowx,ins 2"));
		panel.setOpaque(false);
		panel.add(getEntityIconLabel(entity));
		StringBuilder buf = new StringBuilder();
		buf.append("<html>\n")
				.append(getEntityFullTitle(entity))
				.append("\n");
		panel.add(new JLabel(buf.toString()));
		return panel;
	}

	public static String getEntityFullTitle(AbstractEntity entity) {
		StringBuilder buf = new StringBuilder();
		buf.append("<span ")
				.append(getCSSTitle1())
				.append(">\n")
				.append(getEntityTitle(entity))
				.append("</span>\n");
		if (!entity.isTransient())
			buf.append("&nbsp;&nbsp;")
				.append("<span ")
				.append(getCSSTitle2())
				.append("/>\n")
				.append(entity.toString())
				.append("</span>");
		return buf.toString();
	}

	public static JLabel getEntityIconLabel(AbstractEntity entity) {
		return new JLabel(getEntityIcon(entity));
	}

	public static Icon getEntityIcon(AbstractEntity entity) {
		if (entity instanceof Scene) return I18N.getIcon("icon.small.scene");
		if (entity instanceof Chapter) return I18N.getIcon("icon.small.chapter");
		if (entity instanceof Part) return I18N.getIcon("icon.small.part");
		if (entity instanceof Location) return I18N.getIcon("icon.small.location");
		if (entity instanceof Person) {
			if (entity.isTransient()) return I18N.getIcon("icon.small.person");
			return ((Person) entity).getIcon();
		}
		if (entity instanceof Relationship) return I18N.getIcon("icon.small.link");
		if (entity instanceof Gender) return I18N.getIcon("icon.small.gender");
		if (entity instanceof Category) return I18N.getIcon("icon.small.category");
		if (entity instanceof Strand) return I18N.getIcon("icon.small.strand");
		if (entity instanceof Idea) return I18N.getIcon("icon.small.idea");
		if (entity instanceof Tag) return I18N.getIcon("icon.small.tag");
		if (entity instanceof Item) return I18N.getIcon("icon.small.item");
		if (entity instanceof TagLink) return I18N.getIcon("icon.small.link");
		if (entity instanceof ItemLink) return I18N.getIcon("icon.small.link");
		if (entity instanceof Internal) return I18N.getIcon("icon.small.hammer");
		return new ImageIcon();
	}

	private static String getCSSTitle1() {
		return getCSSTitle1("");
	}

	private static String getCSSTitle1(String styles) {
		return "style='font-weight:bold;font-size:12px;" + styles + "'";
	}

	private static String getCSSTitle2() {
		return getCSSTitle2("");
	}

	private static String getCSSTitle2(String styles) {
		return "style='font-weight:bold;font-size:10px;" + styles + "'";
	}

	public static String getEntityTitle(AbstractEntity entity) {
		return getEntityTitle(entity, null);
	}

	public static String getEntityTitle(AbstractEntity entity,  Boolean setIsTransient) {
		boolean isTransient = entity.isTransient();
		if (setIsTransient != null) isTransient = setIsTransient;
		if (entity instanceof Scene) {
			if (isTransient) return I18N.getMsg("msg.common.scene.add");
			return I18N.getMsg("msg.common.scene");
		}
		if (entity instanceof Chapter) {
			if (isTransient) return I18N.getMsg("msg.common.chapter.add");
			return I18N.getMsg("msg.common.chapter");
		}
		if (entity instanceof Part) {
			if (isTransient) return I18N.getMsg("msg.common.part.new");
			return I18N.getMsg("msg.common.part");
		}
		if (entity instanceof Location) {
			if (isTransient) return I18N.getMsg("msg.common.location.new");
			return I18N.getMsg("msg.common.location");
		}
		if (entity instanceof Person) {
			if (isTransient) return I18N.getMsg("msg.common.person.new");
			return I18N.getMsg("msg.common.person");
		}
		if (entity instanceof Relationship) {
			if (isTransient) return I18N.getMsg("msg.relationship.new");
			return I18N.getMsg("msg.relationship");
		}
		if (entity instanceof Gender) {
			if (isTransient) return I18N.getMsg("msg.dlg.mng.persons.gender.new");
			return I18N.getMsg("msg.dlg.person.gender");
		}
		if (entity instanceof Category) {
			if (isTransient) return I18N.getMsg("msg.persons.category");
			return I18N.getMsg("msg.common.category");
		}
		if (entity instanceof Strand) {
			if (isTransient) return I18N.getMsg("msg.common.strand.new");
			return I18N.getMsg("msg.common.strand");
		}
		if (entity instanceof Idea) {
			if (isTransient) return I18N.getMsg("msg.idea.new");
			return I18N.getMsg("msg.idea.table.idea");
		}
		if (entity instanceof Tag) {
			if (isTransient) return I18N.getMsg("msg.tag.new");
			return I18N.getMsg("msg.tag");
		}
		if (entity instanceof Item) {
			if (isTransient) return I18N.getMsg("msg.item.new");
			return I18N.getMsg("msg.item");
		}
		if (entity instanceof TagLink) {
			if (isTransient) return I18N.getMsg("msg.new.tag.link");
			return I18N.getMsg("msg.common.link");
		}
		if (entity instanceof ItemLink) {
			if (isTransient) return I18N.getMsg("msg.new.item.link");
			return I18N.getMsg("msg.common.link");
		}
		if (entity instanceof Internal) {
			if (isTransient) return "New Internal";
			return I18N.getMsg("msg.internal");
		}
		return "";
	}

}
