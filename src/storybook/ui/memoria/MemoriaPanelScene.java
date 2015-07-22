/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package storybook.ui.memoria;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ItemLinkDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.dao.TagLinkDAOImpl;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.ItemLink;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Tag;
import storybook.model.hbn.entity.TagLink;
import storybook.toolkit.I18N;

/**
 *
 * @author favdb
 */
public class MemoriaPanelScene {
	MemoriaPanel mp;
	
	MemoriaPanelScene(MemoriaPanel mp) {
		this.mp=mp;
	}
	
	@SuppressWarnings("unchecked")
	void createSceneGraph() {
		mp.graphIndex = 0L;;
		BookModel model = mp.getMainFrame().getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl daoScene = new SceneDAOImpl(session);
		Scene scene = (Scene) daoScene.find(Long.valueOf(mp.entityId));
		if (scene == null) {
			model.commit();
			return;
		}
		mp.graph.addVertex(scene);
		mp.labelMap.put(scene, scene.toString());
		mp.iconMap.put(scene, I18N.getIcon("icon.large.scene"));
		mp.sceneVertexTitle = I18N.getMsg("msg.graph.scenes.same.date");
		mp.initVertices(scene);
		HashSet listTags = new HashSet();
		HashSet listItems = new HashSet();
		TagLinkDAOImpl daoTagLink = new TagLinkDAOImpl(session);
		ItemLinkDAOImpl daoItemLink = new ItemLinkDAOImpl(session);
		if (!scene.hasNoSceneTs()) {
			//scènes liés à la scène via la même date
			Date date = new Date(scene.getSceneTs().getTime());
			long sceneId=scene.getId();
			List<Scene> listScenes = daoScene.findByDate(date);
			for (Scene lScene : listScenes) {
				if (!lScene.getId().equals(sceneId)) {
					mp.addToVertexScene(lScene);
					// tags et items impliqués via le lien de date entre scène
					mp.searchInvolvedTags(daoTagLink.findByScene(lScene));
					mp.searchInvolvedItems(daoItemLink.findByScene(lScene));
				}
			}
		} else {
			// tags et items impliqués
			mp.searchInvolvedTags(daoTagLink.findByScene(scene));
			mp.searchInvolvedItems(daoItemLink.findByScene(scene));
		}
		// liste des personnages liés à la scène
		List<Person> persons = scene.getPersons();
		for (Person person : persons) {
			mp.addToVertexPerson(person);
		}
		// liste des lieux liés à la scène
		List<Location> locations = scene.getLocations();
		for (Location location : locations) {
			mp.addToVertexLocation(location);
		}
		// liste des items liés directement à la scène
		List<Item> items = scene.getItems();
		for (Item item : items) {
			listItems.add(item);
		}
		// liste des tags si lien tagLink avec seulement la scene dans startScene
		List<TagLink> tagLinks = daoTagLink.findByScene(scene);
		for (TagLink tagLink : tagLinks) {
			if (tagLink.hasOnlyScene()) {
				listTags.add(tagLink.getTag());
			}
		}
		// liste des items si lien itemLink avec seulement la scene dans startScene
		List<ItemLink> itemLinks = daoItemLink.findByScene(scene);
		for (ItemLink itemLink : itemLinks) {
			if (itemLink.hasOnlyScene()) {
				listItems.add(itemLink.getItem());
			}
		}
		mp.removeDoublesFromInvolvedTags(listTags);
		mp.removeDoublesFromInvolvedItems(listItems);
		Iterator iterator = listTags.iterator();
		while (iterator.hasNext()) {
			mp.addToVertexTag((Tag) iterator.next());
		}
		iterator = listItems.iterator();
		while (iterator.hasNext()) {
			mp.addToVertexItem((Item) iterator.next());
		}
		mp.addToVertexInvolvedTags();
		mp.addToVertexInvolvedItems();
		model.commit();
	}
}
