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

package storybook.ui.panel.tree;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.infonode.docking.View;
import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import storybook.SbConstants.ViewName;
import storybook.action.EditEntityAction;
import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.dao.CategoryDAOImpl;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.GenderDAOImpl;
import storybook.model.hbn.dao.IdeaDAOImpl;
import storybook.model.hbn.dao.ItemDAOImpl;
import storybook.model.hbn.dao.ItemLinkDAOImpl;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.dao.StrandDAOImpl;
import storybook.model.hbn.dao.TagDAOImpl;
import storybook.model.hbn.dao.TagLinkDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Category;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Gender;
import storybook.model.hbn.entity.Idea;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.ItemLink;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;
import storybook.model.hbn.entity.Tag;
import storybook.model.hbn.entity.TagLink;
import storybook.model.state.AbstractState;
import storybook.model.state.IdeaStateModel;
import storybook.model.stringcategory.AbstractStringCategory;
import storybook.model.stringcategory.CityCategory;
import storybook.model.stringcategory.CountryCategory;
import storybook.model.stringcategory.ItemCategory;
import storybook.model.stringcategory.TagCategory;
import storybook.toolkit.StringCategoryUtil;
import storybook.toolkit.swing.IconButton;
import storybook.toolkit.swing.SwingUtil;
import storybook.toolkit.swing.ToggleIconButton;
import storybook.toolkit.swing.TreeUtil;
import storybook.ui.MainFrame;
import storybook.ui.SbView;
import storybook.ui.panel.AbstractPanel;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class TreePanel extends AbstractPanel implements TreeSelectionListener, MouseListener {

	private JToolBar toolbar;
	private JTree tree;
	private JScrollPane scroller;
	private ToggleIconButton btTooglePersons;
	private ToggleIconButton btToogleLocations;
	private ToggleIconButton btToogleScenes;
	private ToggleIconButton btToogleTags;
	private ToggleIconButton btToogleItems;
	private ToggleIconButton btToogleStrands;
	private ToggleIconButton btToogleParts;
	private ToggleIconButton btToogleIdeas;
	private List<ToggleIconButton> toggleButtonList;

	private DefaultMutableTreeNode topNode;
	private EntityNode personsByCategoryNode;
	private EntityNode personsByGendersNode;
	private EntityNode locationsNode;
	private EntityNode scenesNode;
	private EntityNode tagsNode;
	private EntityNode itemsNode;
	private EntityNode strandsNode;
	private EntityNode partsNode;
	private EntityNode ideasNode;

	public TreePanel(MainFrame mainFrame) {
		super(mainFrame);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();
		String propName = evt.getPropertyName();

		if (BookController.CommonProps.REFRESH.check(propName)) {
			View newView = (View) newValue;
			View view = (View) getParent().getParent();
			if (view == newView) {
				refreshTree();
			}
			return;
		}

		if (BookController.CommonProps.SHOW_INFO.check(propName)) {
			return;
		}

		if (newValue instanceof AbstractEntity) {
			boolean ret = refreshNode((AbstractEntity) newValue,
					(AbstractEntity) oldValue);
			if (!ret) {
				refreshTree();
			}
			return;
		}
		if (oldValue instanceof AbstractEntity) {
			refreshTree();
//			return;
		}
	}

	private boolean refreshNode(AbstractEntity updEntity,AbstractEntity oldEntity) {
		TreePath[] paths = getPaths(tree, false);
		for (TreePath path : paths) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			if (node.isLeaf()) {
				Object o = node.getUserObject();
				if (o instanceof AbstractEntity) {
					AbstractEntity entity = (AbstractEntity) o;
					if (entity.getId().equals(updEntity.getId())) {
						if (EntityUtil.hasHierarchyChanged(oldEntity, updEntity)) {
							return false;
						}
						changeTreeNode(node);
						return true;
					}
				}
			}
		}
		return false;
	}

	public TreePath[] getPaths(JTree tree, boolean expanded) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		List<Object> list = new ArrayList<>();
		getPaths(tree, new TreePath(root), expanded, list);
		return (TreePath[]) list.toArray(new TreePath[list.size()]);
	}

	public void getPaths(JTree tree, TreePath parent, boolean expanded,
			List<Object> list) {
		if (expanded && !tree.isVisible(parent)) {
			return;
		}
		list.add(parent);
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				getPaths(tree, path, expanded, list);
			}
		}
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("wrap,fill,ins 0"));
		setMinimumSize(new Dimension(280, 180));

		toggleButtonList = new ArrayList<>();

		btTooglePersons = new ToggleIconButton("icon.small.person", "msg.tree.show.characters", getTogglePersonsAction());
		btTooglePersons.setSelected(true);
		toggleButtonList.add(btTooglePersons);

		btToogleLocations = new ToggleIconButton("icon.small.location", "msg.tree.show.locations", getToggleLocationsAction());
		btToogleLocations.setSelected(true);
		toggleButtonList.add(btToogleLocations);

		btToogleScenes = new ToggleIconButton("icon.small.chapter", "msg.tree.show.chapters", getToggleScenesAction());
		btToogleScenes.setSelected(true);
		toggleButtonList.add(btToogleScenes);

		btToogleTags = new ToggleIconButton("icon.small.tag", "msg.tree.show.tags", getToggleTagsAction());
		btToogleTags.setSelected(true);
		toggleButtonList.add(btToogleTags);

		btToogleItems = new ToggleIconButton("icon.small.item", "msg.tree.show.items", getToggleItemsAction());
		btToogleItems.setSelected(true);
		toggleButtonList.add(btToogleItems);

		btToogleIdeas = new ToggleIconButton("icon.small.bulb", "msg.tree.show.ideas", getToggleIdeaAction());
		btToogleIdeas.setSelected(true);
		toggleButtonList.add(btToogleIdeas);

		btToogleStrands = new ToggleIconButton("icon.small.strand", "msg.tree.show.strands", getToggleStrandsAction());
		toggleButtonList.add(btToogleStrands);

		btToogleParts = new ToggleIconButton("icon.small.part", "msg.tree.show.parts", getTogglePartsAction());
		toggleButtonList.add(btToogleParts);


		for (ToggleIconButton button : toggleButtonList) {
			button.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						toggle((ToggleIconButton) e.getSource());
					}
				}
			});
		}

		// top node
		topNode = new DefaultMutableTreeNode(mainFrame.getDbFile().getName());
		tree = new JTree(topNode);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new EntityTreeCellRenderer());
		scroller = new JScrollPane(tree);
		SwingUtil.setMaxPreferredSize(scroller);

		initToolbar();

		// layout
		add(toolbar, "growx");
		add(scroller, "grow");

		refreshTree();

		tree.addTreeSelectionListener(this);
		tree.addMouseListener(this);
	}

	private void initToolbar() {
		if (toolbar != null) {
			return;
		}
		toolbar = new JToolBar();
		MigLayout layout = new MigLayout("ins 0,gapx 2", "[][][][][][][][]push[][][][]", "");
		toolbar.setLayout(layout);
		toolbar.setFloatable(false);

		// toggle buttons
		for (ToggleIconButton button : toggleButtonList) {
			button.setSize22x22();
			toolbar.add(button);
		}

		// tree control buttons
		IconButton btShowAll = new IconButton("icon.small.all", "msg.tree.show.all", getShowAllAction());
		btShowAll.setControlButton();
		toolbar.add(btShowAll, "top,gap push");
		IconButton btShowNone = new IconButton("icon.small.none", "msg.tree.show.none", getShowNoneAction());
		btShowNone.setControlButton();
		toolbar.add(btShowNone, "top");
		IconButton btExpand = new IconButton("icon.small.expand", "msg.tree.expand.all", getExpandAction());
		btExpand.setControlButton();
		toolbar.add(btExpand, "top");
		IconButton btCollapse = new IconButton("icon.small.collapse", "msg.tree.collapse.all", getCollapseAction());
		btCollapse.setControlButton();
		toolbar.add(btCollapse, "top");
	}

	private void refreshTree() {
		String treeState = TreeUtil.getExpansionState(tree, 0);
		topNode.removeAllChildren();

		if (btTooglePersons.isSelected()) {
			personsByCategoryNode = new EntityNode("msg.tree.persons.by.category", new Person());
			topNode.add(personsByCategoryNode);
			refreshPersonsByCategory();
			personsByGendersNode = new EntityNode("msg.tree.persons.by.gender", new Gender());
			topNode.add(personsByGendersNode);
			refreshPersonsByGender();
		}
		if (btToogleLocations.isSelected()) {
			locationsNode = new EntityNode("msg.common.locations", new Location());
			topNode.add(locationsNode);
			refreshLocations();
		}
		if (btToogleScenes.isSelected()) {
			scenesNode = new EntityNode("msg.common.scenes", new Scene());
			topNode.add(scenesNode);
			refreshScenes();
		}
		if (btToogleTags.isSelected()) {
			tagsNode = new EntityNode("msg.tags", new Tag());
			topNode.add(tagsNode);
			refreshTags();
		}
		if (btToogleItems.isSelected()) {
			itemsNode = new EntityNode("msg.items", new Item());
			topNode.add(itemsNode);
			refreshItems();
		}
		if (btToogleStrands.isSelected()) {
			strandsNode = new EntityNode("msg.common.strands", new Strand());
			topNode.add(strandsNode);
			refreshStrands();
		}
		if (btToogleParts.isSelected()) {
			partsNode = new EntityNode("msg.common.parts", new Part());
			topNode.add(partsNode);
			refreshParts();
		}
		if (btToogleIdeas.isSelected()) {
			ideasNode = new EntityNode("msg.ideas.title", new Idea());
			topNode.add(ideasNode);
			refreshIdeas();
		}
		reloadTreeModel();
		TreeUtil.restoreExpanstionState(tree, 0, treeState);
	}

	private void changeTreeNode(TreeNode node) {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		model.nodeChanged(node);
	}

	private void reloadTreeModel() {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		model.reload();
	}

	private void refreshStrands() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		StrandDAOImpl dao = new StrandDAOImpl(session);
		List<Strand> strands = dao.findAll();
		for (Strand strand : strands) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(strand);
			strandsNode.add(node);
		}
		model.commit();
	}

	private void refreshParts() {
		Map<Part, DefaultMutableTreeNode> partMap = new HashMap<Part, DefaultMutableTreeNode>();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl dao = new PartDAOImpl(session);
		List<Part> parts = dao.findAll();
		for (Part part : parts) {
			createPartNode(partMap, part, partsNode);
		}
		model.commit();
	}
	
	private DefaultMutableTreeNode createPartNode(Map<Part, DefaultMutableTreeNode> partMap, Part part, DefaultMutableTreeNode root) {
		DefaultMutableTreeNode node = partMap.get(part);
		if (node == null) {
			DefaultMutableTreeNode supernode = root;
			if (part.hasSuperpart())
			{
			   Part superPart = part.getSuperpart();
			   supernode = createPartNode(partMap, superPart, root);
			}
			node = new DefaultMutableTreeNode(part);
			supernode.add(node);
			partMap.put(part, node);
		}
		return node;
	}

	private void refreshIdeas() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		IdeaDAOImpl dao = new IdeaDAOImpl(session);
		IdeaStateModel stateModel = new IdeaStateModel();
		for (AbstractState state : stateModel.getStates()) {
			DefaultMutableTreeNode stateNode = new DefaultMutableTreeNode(state.getName());
			ideasNode.add(stateNode);
			List<Idea> ideas = dao.findByStatus(state.getNumber());
			for (Idea idea : ideas) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(idea);
				stateNode.add(node);
			}
		}
		model.commit();
	}

	private void refreshPersonsByCategory() {
		Map<Category, DefaultMutableTreeNode> categoryMap = new HashMap<Category, DefaultMutableTreeNode>();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		CategoryDAOImpl categroyDao = new CategoryDAOImpl(session);
		List<Category> categories = categroyDao.findAll();
		PersonDAOImpl personDao = new PersonDAOImpl(session);
		for (Category category : categories) {
			getPersonsByCategoryNodeOwner(categoryMap, category);
		}
		for (Category category : categories) {
			DefaultMutableTreeNode categoryNode = categoryMap.get(category);
			List<Person> persons = personDao.findByCategory(category);
			for (Person person : persons) {
				DefaultMutableTreeNode personNode = new DefaultMutableTreeNode(person);
				categoryNode.add(personNode);
			}
		}
		model.commit();
	}
		
	private DefaultMutableTreeNode getPersonsByCategoryNodeOwner(
			Map<Category, DefaultMutableTreeNode> categoryMap, Category category) {
		DefaultMutableTreeNode categoryNode = categoryMap.get(category);
		if ( categoryNode == null ) {
			categoryNode = new DefaultMutableTreeNode(category);
			DefaultMutableTreeNode supCategoryNode = personsByCategoryNode;
			Category supCategory = category.getSup();
			if (supCategory != null) {
			    supCategoryNode = categoryMap.get(supCategory);
			    if (supCategoryNode == null) {
				   supCategoryNode = getPersonsByCategoryNodeOwner(categoryMap, supCategory);
			    }
			}
			supCategoryNode.add(categoryNode);
		    categoryMap.put(category, categoryNode);
		}
		return categoryNode;
	}

	private void refreshPersonsByGender() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		GenderDAOImpl genderDao = new GenderDAOImpl(session);
		List<Gender> genders = genderDao.findAll();
		for (Gender gender : genders) {
			DefaultMutableTreeNode genderNode = new DefaultMutableTreeNode(gender);
			personsByGendersNode.add(genderNode);
			List<Person> persons = genderDao.findPersons(gender);
			for (Person person : persons) {
				DefaultMutableTreeNode personNode = new DefaultMutableTreeNode(person);
				genderNode.add(personNode);
			}
		}
		model.commit();
	}

	private void refreshLocations() {
		Map<Location, DefaultMutableTreeNode> sites = new HashMap<Location, DefaultMutableTreeNode>();
		Map<String, DefaultMutableTreeNode> nodes = new HashMap<String, DefaultMutableTreeNode>();
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		LocationDAOImpl locationDao = new LocationDAOImpl(session);
		List<String> countries = locationDao.findCountries();
		for (String country : countries) {
			DefaultMutableTreeNode countryNode = locationsNode;
			if ((country != null && (! country.isEmpty()))) {
				if (nodes.get(country) != null) {
					countryNode = nodes.get(country);
				} else {
					CountryCategory cat1 = new CountryCategory(country);
					countryNode = new DefaultMutableTreeNode(cat1);
					locationsNode.add(countryNode);
					nodes.put(country, countryNode);
				}
			}
			List<String> cities = locationDao.findCitiesByCountry(country);
			for (String city : cities) {
				DefaultMutableTreeNode cityNode = countryNode;
				if (city != null && (! city.isEmpty())) {
					if (nodes.get(city) != null) {
						cityNode = nodes.get(city);
					} else {
					    CityCategory cat2 = new CityCategory(city);
					    cityNode = new DefaultMutableTreeNode(cat2);
					    countryNode.add(cityNode);
					    nodes.put(city, cityNode);
					}
				}
				List<Location> locations = locationDao.findByCountryCity(
						country, city);
				for (Location location : locations) {					
					DefaultMutableTreeNode node = insertLocation(location, cityNode, sites);
					nodes.put(location.getName(), node);
				}
			}
		}
		model.commit();
	}
	
	private DefaultMutableTreeNode insertLocation(Location location, DefaultMutableTreeNode cityNode,
			Map<Location, DefaultMutableTreeNode> sites) {
		// already inserted
		if (sites.get(location) != null)
			return sites.get(location);
		
		DefaultMutableTreeNode locationNode = new DefaultMutableTreeNode(location);
		if (location.hasSite()) {
			DefaultMutableTreeNode siteNode = sites.get(location.getSite());
			if (siteNode == null) {
				siteNode = insertLocation(location.getSite(), cityNode, sites);
			}
			siteNode.add(locationNode);
		} else {
		   cityNode.add(locationNode);
		}
		sites.put(location, locationNode);
		return locationNode;
	}

	private void refreshScenes() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();

		ChapterDAOImpl chapterDao = new ChapterDAOImpl(session);

		// unassigned scenes
		DefaultMutableTreeNode unassignedNode = new DefaultMutableTreeNode(new Chapter());
		scenesNode.add(unassignedNode);
		List<Scene> unassignedScenes = chapterDao.findUnassignedScenes();
		for (Scene scene : unassignedScenes) {
			DefaultMutableTreeNode sceneNode = new DefaultMutableTreeNode(scene);
			unassignedNode.add(sceneNode);
		}

		Map<Part, DefaultMutableTreeNode> partMap = new HashMap<Part, DefaultMutableTreeNode>();
		PartDAOImpl partDao = new PartDAOImpl(session);
		List<Part> parts = partDao.findAll();
		for (Part part : parts) {
			DefaultMutableTreeNode partNode = createPartNode(partMap, part, scenesNode);
			List<Chapter> chapters = partDao.findChapters(part);
			for (Chapter chapter : chapters) {
				DefaultMutableTreeNode chapterNode = new DefaultMutableTreeNode(chapter);
				partNode.add(chapterNode);
				List<Scene> scenes = chapterDao.findScenes(chapter);
				for (Scene scene : scenes) {
					DefaultMutableTreeNode sceneNode = new DefaultMutableTreeNode(scene);
					chapterNode.add(sceneNode);
				}
			}
		}
		model.commit();
	}

	private void refreshTags() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		TagDAOImpl tagDao = new TagDAOImpl(session);
		TagLinkDAOImpl tagLinkDao = new TagLinkDAOImpl(session);
		List<String> categories = tagDao.findCategories();
		for (String category : categories) {
			String categoryName = category;
			if (category == null || category.isEmpty()) {
				categoryName = "-";
			}
			TagCategory cat = new TagCategory(categoryName);
			DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(cat);
			tagsNode.add(categoryNode);
			List<Tag> tags = tagDao.findByCategory(category);
			for (Tag tag : tags) {
				DefaultMutableTreeNode tagNode = new DefaultMutableTreeNode(tag);
				categoryNode.add(tagNode);
				List<TagLink> links = tagLinkDao.findByTag(tag);
				for (TagLink link : links) {
					DefaultMutableTreeNode linkNode = new DefaultMutableTreeNode(link);
					tagNode.add(linkNode);
				}
			}
		}
		model.commit();
	}

	private void refreshItems() {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ItemDAOImpl itemDao = new ItemDAOImpl(session);
		ItemLinkDAOImpl itemLinkDao = new ItemLinkDAOImpl(session);
		List<String> categories = itemDao.findCategories();
		for (String category : categories) {
			String categoryName = category;
			if (category == null || category.isEmpty()) {
				categoryName = "-";
			}
			ItemCategory cat = new ItemCategory(categoryName);
			DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(cat);
			itemsNode.add(categoryNode);
			List<Item> items = itemDao.findByCategory(category);
			for (Item item : items) {
				DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(item);
				categoryNode.add(itemNode);
				List<ItemLink> links = itemLinkDao.findByItem(item);
				for (ItemLink link : links) {
					DefaultMutableTreeNode linkNode = new DefaultMutableTreeNode(link);
					itemNode.add(linkNode);
				}
			}
		}
		model.commit();
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (node == null) {
			return;
		}
		Object value = node.getUserObject();
		if (value == null) {
			return;
		}
		if (node.isRoot()) {
			BookController ctrl = mainFrame.getBookController();
			ctrl.showInfo(mainFrame.getDbFile());
		}
		if (!(value instanceof AbstractEntity)) {
			return;
		}
		SbView view = mainFrame.getView(ViewName.INFO);
		view.cleverRestoreFocus();
		AbstractEntity entity = (AbstractEntity) value;
		BookController ctrl = mainFrame.getBookController();
		ctrl.showInfo(entity);
	}

	private AbstractAction getTogglePersonsAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				toggleSingle(btTooglePersons);
			}
		};
	}

	private AbstractAction getToggleLocationsAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				toggleSingle(btToogleLocations);
			}
		};
	}

	private AbstractAction getToggleTagsAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				toggleSingle(btToogleTags);
			}
		};
	}

	private AbstractAction getToggleItemsAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				toggleSingle(btToogleItems);
			}
		};
	}

	private AbstractAction getToggleScenesAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				toggleSingle(btToogleScenes);
			}
		};
	}

	private AbstractAction getToggleStrandsAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				toggleSingle(btToogleStrands);
			}
		};
	}

	private AbstractAction getTogglePartsAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				toggleSingle(btToogleParts);
			}
		};
	}

	private AbstractAction getToggleIdeaAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				toggleSingle(btToogleIdeas);
			}
		};
	}

	private void toggleSingle(ToggleIconButton button) {
		// getShowNoneAction().actionPerformed(null);
		toggle(button);
	}

	private void toggle(ToggleIconButton button) {
		int count = 0;
		for (ToggleIconButton bt : toggleButtonList) {
			if (bt.isSelected()) {
				++count;
			}
		}
		refreshTree();
		if (count == 1) {
			getExpandAction().actionPerformed(null);
		}
		button.requestFocus();
	}

	private AbstractAction getShowAllAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				for (ToggleIconButton button : toggleButtonList) {
					button.setSelected(true);
				}
				refreshTree();
			}
		};
	}

	private AbstractAction getShowNoneAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				for (ToggleIconButton button : toggleButtonList) {
					button.setSelected(false);
				}
				refreshTree();
			}
		};
	}

	private AbstractAction getExpandAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				for (int i = 0; i < tree.getRowCount(); i++) {
					tree.expandRow(i);
				}
			}
		};
	}

	private AbstractAction getCollapseAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				DefaultMutableTreeNode currentNode = topNode.getNextNode();
				do {
					if (currentNode.getLevel() == 1) {
						tree.collapsePath(new TreePath(currentNode.getPath()));
					}
					currentNode = currentNode.getNextNode();
				} while (currentNode != null);
			}
		};
	}

	private void showPopupMenu(MouseEvent evt) {
		TreePath selectedPath = tree.getPathForLocation(evt.getX(), evt.getY());
		DefaultMutableTreeNode selectedNode = null;
		try {
			selectedNode = (DefaultMutableTreeNode) selectedPath
					.getLastPathComponent();
		} catch (Exception e) {
			// ignore
		}
		if (selectedNode == null) {
			return;
		}
		Object userObj = selectedNode.getUserObject();
		if (!(userObj instanceof AbstractEntity
			|| userObj instanceof AbstractStringCategory)) {
			return;
		}
		JPopupMenu menu = null;
		if (userObj instanceof AbstractStringCategory) {
			AbstractStringCategory cat = (AbstractStringCategory) userObj;
			menu = StringCategoryUtil.createPopupMenu(mainFrame, cat);
		}
		if (userObj instanceof AbstractEntity) {
			AbstractEntity entity = (AbstractEntity) userObj;
			menu = EntityUtil.createPopupMenu(mainFrame, entity);
		}
		if (menu == null) {
			return;
		}
		tree.setSelectionPath(selectedPath);
		JComponent comp = (JComponent) tree.getComponentAt(evt.getPoint());
		Point p = SwingUtilities.convertPoint(comp, evt.getPoint(), this);
		menu.show(this, p.x, p.y);
		evt.consume();
	}

	@Override
	public void mouseClicked(MouseEvent evt) {
		// see also valueChanged()
		if (evt.getClickCount() != 2) {
			return;
		}

		// double click
		TreePath selectedPath = tree.getPathForLocation(evt.getX(), evt.getY());
		DefaultMutableTreeNode selectedNode = null;
		try {
			selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
		} catch (Exception ex) {
			// ignore
		}
		if (selectedNode == null) {
			return;
		}
		// tree.setSelectionPath(selectedPath);
		if (selectedNode.isLeaf()) {
			Object value = selectedNode.getUserObject();
			if (value instanceof AbstractEntity) {
				AbstractEntity entity = (AbstractEntity) value;
				EditEntityAction act = new EditEntityAction(mainFrame, entity,false);
				act.actionPerformed(null);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			showPopupMenu(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			showPopupMenu(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
