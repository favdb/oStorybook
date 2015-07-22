package storybook.ui.memoria;

import storybook.SbConstants;
import storybook.SbApp;
import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ItemDAOImpl;
import storybook.model.hbn.dao.ItemLinkDAOImpl;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.dao.TagDAOImpl;
import storybook.model.hbn.dao.TagLinkDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.AbstractTag;
import storybook.model.hbn.entity.Attribute;
import storybook.model.hbn.entity.Internal;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.ItemLink;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Tag;
import storybook.model.hbn.entity.TagLink;
import storybook.toolkit.BookUtil;
import storybook.toolkit.EnvUtil;
import storybook.toolkit.I18N;
import storybook.toolkit.IOUtil;
import storybook.toolkit.Period;
import storybook.toolkit.filefilter.PngFileFilter;
import storybook.toolkit.swing.IconButton;
import storybook.toolkit.swing.ScreenImage;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;
import storybook.ui.combo.EntityTypeListCellRenderer;
import storybook.ui.interfaces.IRefreshable;
import storybook.ui.options.MemoriaOptionsDialog;

import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.DefaultVertexIconTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.decorators.VertexIconShapeTransformer;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.util.Animator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.infonode.docking.View;
import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import org.jdesktop.swingx.icon.EmptyIcon;

public class MemoriaPanel extends AbstractPanel implements ActionListener, IRefreshable {

    DelegateForest<AbstractEntity, Long> graph;
    private VisualizationViewer<AbstractEntity, Long> vv;
    private TreeLayout<AbstractEntity, Long> treeLayout;
    private BalloonLayout<AbstractEntity, Long> balloonLayout;
    private GraphZoomScrollPane graphPanel;
    Map<AbstractEntity, String> labelMap;
    Map<AbstractEntity, Icon> iconMap;
    private String entitySourceName;
    public long entityId;
    private AbstractEntity shownEntity;
    public long graphIndex;
    private ScalingControl scaler;
    //scenes
    private Scene sceneVertex;
    String sceneVertexTitle;
    private List<Long> sceneIds;
    //persons
    private Person personVertex;
    //locations
    private Location locationVertex;
    private String locationVertexTitle;
    //tag
    private boolean showTagVertex = true;
    private Tag tagVertex;
    private Tag involvedTagVertex;
    private Set<Tag> involvedTags;
    //items
    private Item itemVertex;
    private Item involvedItemVertex;
    private Set<Item> involvedItems;
    //panels and combos
    boolean showBalloonLayout = true;
    private JPanel controlPanel;
    private JPanel datePanel;
    private JComboBox entityTypeCombo;
    private JComboBox entityCombo;
    private Date chosenDate;
    private JComboBox dateCombo;
    private JCheckBox cbAutoRefresh;
    //icons
    private final Icon womanIconMedium = I18N.getIcon("icon.medium.woman");
    private final Icon womanIconLarge = I18N.getIcon("icon.large.woman");
    private final Icon manIconMedium = I18N.getIcon("icon.medium.man");
    private final Icon manIconLarge = I18N.getIcon("icon.large.man");
    private final Icon personIconMedium = I18N.getIcon("icon.medium.person");
    private final Icon personIconLarge = I18N.getIcon("icon.large.person");
    private final Icon locationIconMedium = I18N.getIcon("icon.medium.location");
    private final Icon locationIconLarge = I18N.getIcon("icon.large.location");
    private final Icon sceneIconMedium = I18N.getIcon("icon.medium.scene");
    private final Icon sceneIconLarge = I18N.getIcon("icon.large.scene");
    private final Icon itemIconMedium = I18N.getIcon("icon.medium.item");
    private final Icon itemIconLarge = I18N.getIcon("icon.large.item");
    private final Icon tagIconMedium = I18N.getIcon("icon.medium.tag");
    private final Icon tagIconLarge = I18N.getIcon("icon.large.tag");
    private final Icon emptyIcon = new EmptyIcon();
    //processAction
    private boolean processActionListener = true;

    public MemoriaPanel(MainFrame paramMainFrame) {
	super(paramMainFrame);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
	try {
	    String str = evt.getPropertyName();
	    if (str == null) {
		return;
	    }
	    if (BookController.CommonProps.REFRESH.check(str)) {
		View newView = (View) evt.getNewValue();
		View oldView = (View) getParent().getParent();
		if (oldView == newView) {
		    refresh();
		}
		return;
	    }
	    if (BookController.CommonProps.SHOW_IN_MEMORIA.check(str)) {
		AbstractEntity entity = (AbstractEntity) evt.getNewValue();
		refresh(entity);
		return;
	    }
	    if (BookController.CommonProps.SHOW_OPTIONS.check(str)) {
		View newView = (View) evt.getNewValue();
		View oldView = (View) getParent().getParent();
		if (!newView.getName().equals(SbConstants.ViewName.MEMORIA.toString())) {
		    return;
		}
		MemoriaOptionsDialog dlg = new MemoriaOptionsDialog(mainFrame);
		SwingUtil.showModalDialog(dlg, this);
		return;
	    }
	    if (BookController.MemoriaViewProps.BALLOON.check(str)) {
		showBalloonLayout = (Boolean) evt.getNewValue();
		makeLayoutTransition();
		return;
	    }
	    if ((str.startsWith("Update")) || (str.startsWith("Delete")) || (str.startsWith("New"))) {
		refresh();
		return;
	    }
	    if (BookController.CommonProps.EXPORT.check(str)) {
		View newView = (View) evt.getNewValue();
		View oldView = (View) getParent().getParent();
		if (newView == oldView) {
		    export();
		}
	    }
	} catch (Exception exc) {
	    SbApp.error("MemoriaPanel.modelPropertyChange(" + evt.toString() + ")", exc);
	}
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init() {
	try {
	    chosenDate = new Date(0L);
	    entitySourceName = "";
	    sceneIds = new ArrayList();
	    involvedTags = new HashSet();
	    involvedItems = new HashSet();
	    scaler = new CrossoverScalingControl();
	    try {
		Internal internal = BookUtil.get(mainFrame, SbConstants.BookKey.MEMORIA_BALLOON, Boolean.valueOf(true));
		showBalloonLayout = internal.getBooleanValue();
	    } catch (Exception exc) {
		showBalloonLayout = true;
	    }
	} catch (Exception exc2) {
	    SbApp.error("MemoriaPanel.init()", exc2);
	}
    }

    @Override
    public void initUi() {
	try {
	    MigLayout migLayout1 = new MigLayout("wrap,fill", "[]", "[][grow]");
	    setLayout(migLayout1);
	    setBackground(SwingUtil.getBackgroundColor());
	    controlPanel = new JPanel();
	    MigLayout migLayout2 = new MigLayout("flowx", "", "");
	    controlPanel.setLayout(migLayout2);
	    controlPanel.setOpaque(false);
	    refreshControlPanel();
	    initGraph();
	    add(controlPanel, "alignx center");
	    add(graphPanel, "grow");
	} catch (Exception exc) {
	    SbApp.error("MemoriaPanel.modelPropertyChange()", exc);
	}
    }

    @SuppressWarnings("unchecked")
    private void refreshEntityCombo(EntityTypeCbItem.Type type) {
	BookModel model = mainFrame.getBookModel();
	Session session = model.beginTransaction();
	List list;
	if (type == EntityTypeCbItem.Type.SCENE) {
	    SceneDAOImpl dao = new SceneDAOImpl(session);
	    list = dao.findAll();
	    refreshCombo(new Scene(), list, false);
	    datePanel.setVisible(false);
	} else if (type == EntityTypeCbItem.Type.PERSON) {
	    PersonDAOImpl dao = new PersonDAOImpl(session);
	    list = dao.findAll();
	    Person person = new Person();
	    ArrayList array = new ArrayList();
	    array.add(new Attribute("fd", "fds"));
	    person.setAttributes(array);
	    refreshCombo(person, list, false);
	    datePanel.setVisible(true);
	} else if (type == EntityTypeCbItem.Type.LOCATION) {
	    LocationDAOImpl dao = new LocationDAOImpl(session);
	    list = dao.findAll();
	    refreshCombo(new Location(), list, false);
	    datePanel.setVisible(true);
	} else if (type == EntityTypeCbItem.Type.TAG) {
	    TagDAOImpl dao = new TagDAOImpl(session);
	    list = dao.findAll();
	    refreshCombo(new Tag(), list, false);
	    datePanel.setVisible(true);
	} else if (type == EntityTypeCbItem.Type.ITEM) {
	    ItemDAOImpl dao = new ItemDAOImpl(session);
	    list = dao.findAll();
	    refreshCombo(new Item(), list, false);
	    datePanel.setVisible(true);
	}
	model.commit();
    }

    void addIconButton(String icon, String btString) {
	IconButton ib = new IconButton("icon.small." + icon);
	ib.setSize32x20();
	ib.setName(btString);
	ib.addActionListener(this);
	datePanel.add((Component) ib);
    }

    @SuppressWarnings("unchecked")
    private void refreshControlPanel() {
	BookModel model = mainFrame.getBookModel();
	Session session = model.beginTransaction();
	SceneDAOImpl dao = new SceneDAOImpl(session);
	List<Scene> scenes = dao.findAll();
	List<Date> dates = dao.findDistinctDates();
	dates.removeAll(Collections.singletonList(null));
	model.commit();
	Object entityTypeSelected = null;
	if (entityTypeCombo != null) {
	    entityTypeSelected = entityTypeCombo.getSelectedItem();
	}
	Object entityComboSelected = null;
	if (entityCombo != null) {
	    entityComboSelected = entityCombo.getSelectedItem();
	}
	Object dateComboSelected = null;
	if (dateCombo != null) {
	    dateComboSelected = dateCombo.getSelectedItem();
	}
	dateCombo = new JComboBox();
	dateCombo.setPreferredSize(new Dimension(100, 20));
	dateCombo.addItem(null);
	for (Date onedate : dates) {
	    dateCombo.addItem(onedate);
	}
	dateCombo.setName(SbConstants.ComponentName.COMBO_DATES.toString());
	dateCombo.setMaximumRowCount(15);
	if (dateComboSelected != null) {
	    dateCombo.setSelectedItem(dateComboSelected);
	}
	datePanel = new JPanel(new MigLayout("flowx,ins 0"));
	datePanel.setOpaque(false);
	datePanel.setVisible(false);
	datePanel.add(dateCombo);
	addIconButton("first", SbConstants.ComponentName.BT_FIRST.toString());
	addIconButton("next", SbConstants.ComponentName.BT_NEXT.toString());
	addIconButton("previous", SbConstants.ComponentName.BT_PREVIOUS.toString());
	addIconButton("last", SbConstants.ComponentName.BT_LAST.toString());
	entityTypeCombo = new JComboBox();
	entityTypeCombo.setPreferredSize(new Dimension(120, 20));
	entityTypeCombo.setName(SbConstants.ComponentName.COMBO_ENTITY_TYPES.toString());
	entityTypeCombo.setRenderer(new EntityTypeListCellRenderer());
	entityTypeCombo.addItem(new EntityTypeCbItem(EntityTypeCbItem.Type.SCENE));
	entityTypeCombo.addItem(new EntityTypeCbItem(EntityTypeCbItem.Type.PERSON));
	entityTypeCombo.addItem(new EntityTypeCbItem(EntityTypeCbItem.Type.LOCATION));
	entityTypeCombo.addItem(new EntityTypeCbItem(EntityTypeCbItem.Type.TAG));
	entityTypeCombo.addItem(new EntityTypeCbItem(EntityTypeCbItem.Type.ITEM));
	if (entityTypeSelected != null) {
	    entityTypeCombo.setSelectedItem(entityTypeSelected);
	}
	entityCombo = new JComboBox();
	entityCombo.setName(SbConstants.ComponentName.COMBO_ENTITIES.toString());
	entityCombo.setMaximumRowCount(15);
	if (entityComboSelected != null) {
	    EntityTypeCbItem cbbItem = (EntityTypeCbItem) entityTypeSelected;
	    refreshEntityCombo(cbbItem.getType());
	    entityCombo.setSelectedItem(entityComboSelected);
	} else {
	    refreshCombo(new Scene(), scenes, false);
	}
	controlPanel.removeAll();
	controlPanel.add(entityTypeCombo);
	controlPanel.add(entityCombo, "gapafter 32");
	controlPanel.add(datePanel);
	controlPanel.revalidate();
	controlPanel.repaint();
	entityTypeCombo.addActionListener(this);
	entityCombo.addActionListener(this);
	dateCombo.addActionListener(this);
    }

    private JPanel getThis() {
	return this;
    }

    @SuppressWarnings("unchecked")
    private void makeLayoutTransition() {
	if (vv == null) {
	    return;
	}
	LayoutTransition layout;
	if (showBalloonLayout) {
	    layout = new LayoutTransition(vv, treeLayout, balloonLayout);
	} else {
	    layout = new LayoutTransition(vv, balloonLayout, treeLayout);
	}
	Animator animator = new Animator(layout);
	animator.start();
	vv.repaint();
    }

    @SuppressWarnings("unchecked")
    private void clearGraph() {
	try {
	    if (graph == null) {
		graph = new DelegateForest();
		return;
	    }
	    Collection collections = graph.getRoots();
	    Iterator iCollection = collections.iterator();
	    while (iCollection.hasNext()) {
		AbstractEntity entity = (AbstractEntity) iCollection.next();
		if (entity != null) {
		    graph.removeVertex(entity);
		}
	    }
	} catch (Exception exc) {
	    graph = new DelegateForest();
	}
    }

    public void zoomIn() {
	scaler.scale(vv, 1.1F, vv.getCenter());
    }

    public void zoomOut() {
	scaler.scale(vv, 0.9090909F, vv.getCenter());
    }

    public void export() {
	try {
	    if (shownEntity == null) {
		return;
	    }
	    Internal internal = BookUtil.get(mainFrame, SbConstants.BookKey.EXPORT_DIRECTORY, EnvUtil.getDefaultExportDir(mainFrame));
	    File file1 = new File(internal.getStringValue());
	    JFileChooser chooser = new JFileChooser(file1);
	    chooser.setFileFilter(new PngFileFilter());
	    chooser.setApproveButtonText(I18N.getMsg("msg.common.export"));
	    String str = IOUtil.getEntityFileNameForExport(mainFrame, "Memoria", shownEntity);
	    chooser.setSelectedFile(new File(str));
	    int i = chooser.showDialog(getThis(), I18N.getMsg("msg.common.export"));
	    if (i == 1) {
		return;
	    }
	    File file2 = chooser.getSelectedFile();
	    if (!file2.getName().endsWith(".png")) {
		file2 = new File(file2.getPath() + ".png");
	    }
	    ScreenImage.createImage(graphPanel, file2.toString());
	    JOptionPane.showMessageDialog(getThis(), I18N.getMsg("msg.common.export.success") + "\n" + file2.getAbsolutePath(), I18N.getMsg("msg.common.export"), 1);
	} catch (IOException exc) {
	    SbApp.error("MemoriaPanel.export()", exc);
	}
    }

    @SuppressWarnings("unchecked")
    private void refreshCombo(AbstractEntity pEntity, List<? extends AbstractEntity> pList, boolean b) {
	try {
	    processActionListener = false;
	    DefaultComboBoxModel combo = (DefaultComboBoxModel) entityCombo.getModel();
	    combo.removeAllElements();
	    combo.addElement(pEntity);
	    for (AbstractEntity entity : pList) {
		combo.addElement(entity);
	    }
	    processActionListener = true;
	} catch (Exception exc) {
	    SbApp.error("MemoriaPanel.refreshCombo(" + pEntity.toString() + ", list<" + ">, " + b + ")", exc);
	}
    }

    @SuppressWarnings("unchecked")
    private void initGraph() {
	try {
	    labelMap = new HashMap();
	    iconMap = new HashMap();
	    graph = new DelegateForest();
	    treeLayout = new TreeLayout(graph);
	    balloonLayout = new BalloonLayout(graph);
	    vv = new VisualizationViewer(balloonLayout);
	    vv.setSize(new Dimension(800, 800));
	    refreshGraph();
	    vv.setBackground(Color.white);
	    vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
	    vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
	    vv.setVertexToolTipTransformer(new EntityTransformer());
	    graphPanel = new GraphZoomScrollPane(vv);
	    DefaultModalGraphMouse mouse = new DefaultModalGraphMouse();
	    vv.setGraphMouse(mouse);
	    mouse.add(new MemoriaGraphMouse(this));
	    // T O D O  MemoriaPanel compile error suppress 2 lines
	    //VertexStringerImpl localVertexStringerImpl = new VertexStringerImpl(labelMap);
	    //vv.getRenderContext().setVertexLabelTransformer(new VertexStringerImpl(localVertexStringerImpl));
	    VertexIconShapeTransformer transformer = new VertexIconShapeTransformer(new EllipseVertexShapeTransformer());
	    DefaultVertexIconTransformer iconTransformer = new DefaultVertexIconTransformer();
	    transformer.setIconMap(iconMap);
	    iconTransformer.setIconMap(iconMap);
	    vv.getRenderContext().setVertexShapeTransformer(transformer);
	    vv.getRenderContext().setVertexIconTransformer(iconTransformer);
	} catch (Exception exc) {
	    SbApp.error("MemoriaPanel.initGraph()", exc);
	}
    }

    private void refreshGraph() {
	refreshGraph(null);
    }

    @SuppressWarnings("unchecked")
    private void refreshGraph(AbstractEntity entity) {
	try {
	    clearGraph();
	    if (entity == null) {
		entity = (AbstractEntity) entityCombo.getItemAt(0);
	    }
	    //if ((!(entity instanceof Scene)) && (chosenDate == null)) {
	    //	return;
	    //}
	    if ((entity instanceof Scene)) {
		createSceneGraph();
	    } else if ((entity instanceof Person)) {
		createPersonGraph();
	    } else if ((entity instanceof Location)) {
		createLocationGraph();
	    } else if ((entity instanceof Tag)) {
		createTagGraph();
	    } else if ((entity instanceof Item)) {
		createItemGraph();
	    }
	    shownEntity = entity;
	    treeLayout = new TreeLayout(graph);
	    balloonLayout = new BalloonLayout(graph);
	    Dimension dimension = mainFrame.getSize();
	    balloonLayout.setSize(new Dimension(dimension.width / 2, dimension.height / 2));
	    balloonLayout.setGraph(graph);
	    if (showBalloonLayout) {
		vv.setGraphLayout(balloonLayout);
	    } else {
		vv.setGraphLayout(treeLayout);
	    }
	    vv.repaint();
	} catch (Exception exc) {
	    SbApp.error("MemoriaPanel.refreshGraph()", exc);
	    exc.printStackTrace();
	}
    }

    private boolean isNothingSelected() {
	return entityId <= -1L;
    }

    private void showMessage(String paramString) {
	Graphics2D graphics2D = (Graphics2D) vv.getGraphics();
	if (graphics2D == null) {
	    return;
	}
	Rectangle rectangle = vv.getBounds();
	int i = (int) rectangle.getCenterX();
	int j = (int) rectangle.getCenterY();
	graphics2D.setColor(Color.lightGray);
	graphics2D.fillRect(i - 200, j - 20, 400, 40);
	graphics2D.setColor(Color.black);
	graphics2D.drawString(paramString, i - 180, j + 5);
    }

    private void scaleToLayout(ScalingControl scalingControl) {
	Dimension dimension1 = vv.getPreferredSize();
	if (vv.isShowing()) {
	    dimension1 = vv.getSize();
	}
	Dimension dimension2 = vv.getGraphLayout().getSize();
	if (!dimension1.equals(dimension2)) {
	    scalingControl.scale(vv, (float) (dimension1.getWidth() / dimension2.getWidth()), new Point2D.Double());
	}
    }

    @SuppressWarnings("unchecked")
    private void createSceneGraph() {
	graphIndex = 0L;
	BookModel model = mainFrame.getBookModel();
	Session session = model.beginTransaction();
	SceneDAOImpl daoScene = new SceneDAOImpl(session);
	Scene localScene = (Scene) daoScene.find(Long.valueOf(entityId));
	if (localScene == null) {
	    model.commit();
	    return;
	}
	graph.addVertex(localScene);
	labelMap.put(localScene, localScene.toString());
	iconMap.put(localScene, sceneIconLarge);
	sceneVertexTitle = I18N.getMsg("msg.graph.scenes.same.date");
	initVertices(localScene);
	TagLinkDAOImpl daoTagLink = new TagLinkDAOImpl(session);
	ItemLinkDAOImpl daoItemLink = new ItemLinkDAOImpl(session);
	if (!localScene.hasNoSceneTs()) {
	    //scènes liés à la scène via la même date
	    Date date = new Date(localScene.getSceneTs().getTime());
	    long sceneId = localScene.getId();
	    List<Scene> listScenes = daoScene.findByDate(date);
	    if (!listScenes.isEmpty()) {
		for (Scene lScene : listScenes) {
		    if (!lScene.getId().equals(sceneId)) {
			addToVertexScene(lScene);
			// tags et items impliqués via le lien de date entre scène
			searchInvolvedTags(daoTagLink.findByScene(lScene));
			searchInvolvedItems(daoItemLink.findByScene(lScene));
		    }
		}
	    }
	} else {
	    // tags et items impliqués
	    searchInvolvedTags(daoTagLink.findByScene(localScene));
	    searchInvolvedItems(daoItemLink.findByScene(localScene));
	}
	// liste des personnages liés à la scène
	List<Person> persons = localScene.getPersons();
	for (Person person : persons) {
	    addToVertexPerson(person);
	}
	// liste des lieux liés à la scène
	List<Location> locations = localScene.getLocations();
	for (Location location : locations) {
	    addToVertexLocation(location);
	}
	// liste des items liés directement à la scène
	List<Item> items = localScene.getItems();
	for (Item item : items) {
	    addToVertexItem(item);
	}
	// liste des tags si lien tagLink avec seulement la scene dans startScene
	List<TagLink> tagLinks = daoTagLink.findByScene(localScene);
	for (TagLink tagLink : tagLinks) {
	    if (tagLink.hasOnlyScene()) {
		addToVertexTag(tagLink.getTag());
	    }
	}
	// liste des items si lien itemLink avec seulement la scene dans startScene
	List<ItemLink> itemLinks = daoItemLink.findByScene(localScene);
	for (ItemLink itemLink : itemLinks) {
	    if (itemLink.hasOnlyScene()) {
		addToVertexItem(itemLink.getItem());
	    }
	}
	model.commit();
    }

    @SuppressWarnings("unchecked")
    void removeDoublesFromInvolvedTags(Set<Tag> set1) {
	List<Tag> tagList = new ArrayList();
	for (Tag atag : involvedTags) {
	    for (Tag tag : set1) {
		if (tag.getId().equals(atag.getId())) {
		    tagList.add(atag);
		}
	    }
	}
	for (Tag atag : tagList) {
	    involvedTags.remove(atag);
	}
    }

    @SuppressWarnings("unchecked")
    void removeDoublesFromInvolvedItems(Set<Item> set2) {
	List<AbstractTag> tagList = new ArrayList();
	for (AbstractTag atag : involvedItems) {
	    for (Item item : set2) {
		if (item.getId().equals(atag.getId())) {
		    tagList.add(atag);
		}
	    }
	}
	for (AbstractTag atag : tagList) {
	    involvedItems.remove((Item) atag);
	}
    }

    @SuppressWarnings("unchecked")
    private void createTagGraph() {
	BookModel model = mainFrame.getBookModel();
	Session session = model.beginTransaction();
	TagDAOImpl localTagDAOImpl = new TagDAOImpl(session);
	Tag tag = (Tag) localTagDAOImpl.find(Long.valueOf(entityId));
	if (tag == null) {
	    model.commit();
	    return;
	}
	SceneDAOImpl localSceneDAOImpl = new SceneDAOImpl(session);
	TagLinkDAOImpl localTagLinkDAOImpl = new TagLinkDAOImpl(session);
	ItemLinkDAOImpl localItemLinkDAOImpl = new ItemLinkDAOImpl(session);
	graphIndex = 0L;
	graph.addVertex(tag);
	labelMap.put(tag, tag.toString());
	iconMap.put(tag, tagIconLarge);
	showTagVertex = false;
	initVertices(tag);
	List<TagLink> tagLinks = localTagLinkDAOImpl.findByTag(tag);
	for (TagLink tagLink : tagLinks) {
	    Period period = tagLink.getPeriod();
	    if ((chosenDate == null) || (period == null) || (period.isInside(chosenDate))) {
		if (tagLink.hasLocationOrPerson()) {
		    if (tagLink.hasPerson()) {
			addToVertexPerson(tagLink.getPerson());
			List<TagLink> TLbys = localTagLinkDAOImpl.findByPerson(tagLink.getPerson());
			if (!TLbys.isEmpty()) {
			    for (TagLink TLby : TLbys) {
				if (!TLby.getTag().getId().equals(tag.getId())) {
				    addToVertexInvolvedTag(TLby.getTag());
				}
			    }
			}
			List<ItemLink> ILbys = localItemLinkDAOImpl.findByPerson(tagLink.getPerson());
			if (!ILbys.isEmpty()) {
			    for (ItemLink ILby : ILbys) {
				addToVertexInvolvedItem(ILby.getItem());
			    }
			}
		    }
		    if (tagLink.hasLocation()) {
			addToVertexLocation(tagLink.getLocation());
			List<TagLink> TLbys = localTagLinkDAOImpl.findByLocation(tagLink.getLocation());
			if (!TLbys.isEmpty()) {
			    for (TagLink TLby : TLbys) {
				if (!TLby.getTag().getId().equals(tag.getId())) {
				    addToVertexInvolvedTag(TLby.getTag());
				}
			    }
			}
			List<ItemLink> ILbys = localItemLinkDAOImpl.findByLocation(tagLink.getLocation());
			if (!ILbys.isEmpty()) {
			    for (ItemLink ILby : ILbys) {
				addToVertexInvolvedItem(ILby.getItem());
			    }
			}
		    }
		} else {
		    Scene startScene = tagLink.getStartScene();
		    if (startScene != null) {
			List<Scene> scenes = localSceneDAOImpl.findByDate(chosenDate);
			if (!scenes.isEmpty()) {
			    for (Scene sc : scenes) {
				if (((tagLink.hasEndScene()) || (!sc.getId().equals(startScene.getId())))
					&& (sc.getStrand().getId().equals(startScene.getStrand().getId()))) {
				    addToVertexScene(sc);
				    List<TagLink> TLbys = localTagLinkDAOImpl.findByScene(sc);
				    if (!TLbys.isEmpty()) {
					for (TagLink tl : TLbys) {
					    if (!tag.getId().equals(tl.getId())) {
						addToVertexInvolvedTag(tl.getTag());
					    }
					}
				    }
				    List<ItemLink> ILbys = localItemLinkDAOImpl.findByScene(sc);
				    if (!ILbys.isEmpty()) {
					for (ItemLink ILby : ILbys) {
					    addToVertexInvolvedItem(ILby.getItem());
					}
				    }
				}
			    }
			}
		    }
		}
	    }
	}
	model.commit();
    }

    @SuppressWarnings("unchecked")
    private void createItemGraph() {
	BookModel model = mainFrame.getBookModel();
	Session session = model.beginTransaction();
	ItemDAOImpl localItemDAOImpl = new ItemDAOImpl(session);
	Item localItem = (Item) localItemDAOImpl.find(Long.valueOf(entityId));
	if (localItem == null) {
	    model.commit();
	    return;
	}
	SceneDAOImpl localSceneDAOImpl = new SceneDAOImpl(session);
	TagLinkDAOImpl localTagLinkDAOImpl = new TagLinkDAOImpl(session);
	ItemLinkDAOImpl localItemLinkDAOImpl = new ItemLinkDAOImpl(session);
	graphIndex = 0L;
	graph.addVertex(localItem);
	labelMap.put(localItem, localItem.toString());
	iconMap.put(localItem, itemIconLarge);
	showTagVertex = false;
	initVertices(localItem);
	List<ItemLink> itemLinks = localItemLinkDAOImpl.findByItem(localItem);
	for (ItemLink itemLink : itemLinks) {
	    Period period = itemLink.getPeriod();
	    if ((chosenDate == null) || (period == null) || (period.isInside(chosenDate))) {
		if (itemLink.hasPerson()) {
		    addToVertexPerson(itemLink.getPerson());
		}
		if (itemLink.hasLocation()) {
		    addToVertexLocation(itemLink.getLocation());
		}
		List<Scene> listScenes=localSceneDAOImpl.findAll();
		for (Scene scene : listScenes) {
		    if ((period != null) && ((!scene.hasNoSceneTs()) && (period.isInside(scene.getDate())))) {
			addToVertexScene(scene);
			List<Location> locations = scene.getLocations();
			if (!locations.isEmpty()) {
			    for (Location loc : locations) {
				addToVertexLocation(loc);
			    }
			}
			List<Person> persons = scene.getPersons();
			if (!persons.isEmpty()) {
			    for (Person person : persons) {
				addToVertexPerson(person);
			    }
			}
			List<Item> items = scene.getItems();
			if (!items.isEmpty()) {
			    for (Item item : items) {
				addToVertexInvolvedItem(item);
			    }
			}
			searchInvolvedTags(localTagLinkDAOImpl.findByScene(scene));
			searchInvolvedItems(localItemLinkDAOImpl.findByScene(scene));
		    } 
		}
	    }
	}
	List<Scene> scenes = localSceneDAOImpl.findAll();
	for (Scene scene : scenes) {
	    if (scene.getItems().contains(localItem)) {
		addToVertexScene(scene);
		List<Location> locations = scene.getLocations();
		if (!locations.isEmpty()) {
		    for (Location loc : locations) {
			addToVertexLocation(loc);
		    }
		}
		List<Person> persons = scene.getPersons();
		if (!persons.isEmpty()) {
		    for (Person person : persons) {
			addToVertexPerson(person);
		    }
		}
		List<Item> items = scene.getItems();
		if (!items.isEmpty()) {
		    for (Item item : items) {
			addToVertexInvolvedItem(item);
		    }
		}
		searchInvolvedTags(localTagLinkDAOImpl.findByScene(scene));
		searchInvolvedItems(localItemLinkDAOImpl.findByScene(scene));
	    }
	}
	model.commit();
    }

    void addToVertexScene(Scene scene) {
	graph.addVertex(scene);
	labelMap.put(scene, scene.toString());
	iconMap.put(scene, sceneIconMedium);
	graph.addEdge(graphIndex++, sceneVertex, scene);
    }

    void addToVertexScenes(Set<Scene> paramSet) {
	if (!paramSet.isEmpty()) {
	    for (Scene scene : paramSet) {
		addToVertexScene(scene);
	    }
	}
    }

    void addToVertexPerson(Person person) {
	graph.addVertex(person);
	labelMap.put(person, person.toString());
	iconMap.put(person, getPersonIcon(person, SbConstants.IconSize.MEDIUM));
	graph.addEdge(graphIndex++, personVertex, person);
    }

    void addToVertexPersons(Set<Person> paramSet) {
	if (!paramSet.isEmpty()) {
	    for (Person person : paramSet) {
		addToVertexPerson(person);
	    }
	}
    }

    void addToVertexLocation(Location location) {
	graph.addVertex(location);
	labelMap.put(location, location.toString());
	iconMap.put(location, locationIconMedium);
	graph.addEdge(graphIndex++, locationVertex, location);
    }

    void addToVertexLocations(Set<Location> paramSet) {
	if (!paramSet.isEmpty()) {
	    for (Location location : paramSet) {
		addToVertexLocation(location);
	    }
	}
    }

    private void createLocationGraph() {
	BookModel model = mainFrame.getBookModel();
	Session session = model.beginTransaction();
	LocationDAOImpl locationDao = new LocationDAOImpl(session);
	Location location = (Location) locationDao.find(Long.valueOf(entityId));
	if (location == null) {
	    model.commit();
	    return;
	}
	SceneDAOImpl sceneDao = new SceneDAOImpl(session);
	List<Scene> scenes = sceneDao.findAll();
	if ((scenes == null) || (scenes.isEmpty())) {
	    return;
	}
	TagLinkDAOImpl tagLinkDao = new TagLinkDAOImpl(session);
	ItemLinkDAOImpl itemLinkDao = new ItemLinkDAOImpl(session);
	graphIndex = 0L;
	graph.addVertex(location);
	labelMap.put(location, location.toString());
	iconMap.put(location, locationIconLarge);
	locationVertexTitle = I18N.getMsg("msg.graph.involved.locations");
	initVertices(location);
	List<TagLink> tagLinks = tagLinkDao.findByLocation(location);
	if (!tagLinks.isEmpty()) {
	    for (TagLink tagLink : tagLinks) {
		Period period = tagLink.getPeriod();
		if ((period == null) || (chosenDate == null) || (period.isInside(chosenDate))) {
		    addToVertexTag(tagLink.getTag());
		}
	    }
	}
	List<ItemLink> itemLinks = itemLinkDao.findByLocation(location);
	if (!itemLinks.isEmpty()) {
	    for (ItemLink itemLink : itemLinks) {
		Period period = itemLink.getPeriod();
		if ((period == null) || (chosenDate == null) || (period.isInside(chosenDate))) {
		    addToVertexItem(itemLink.getItem());
		}
	    }
	}
	for (Scene scene : scenes) {
	    boolean c = false;
	    if (chosenDate == null) {
		c = true;
	    } else if ((!scene.hasNoSceneTs()) && (chosenDate.compareTo(scene.getDate()) == 0)) {
		c = true;
	    }
	    if (c && scene.getLocations().contains(location)) {
		List<Location> sceneLocations = scene.getLocations();
		for (Location sceneLocation : sceneLocations) {
		    if (sceneLocation.equals(location)) {
			addToVertexScene(scene);
			sceneIds.add(scene.getId());
			searchInvolvedTags(tagLinkDao.findByScene(scene));
			searchInvolvedItems(itemLinkDao.findByScene(scene));
			List<Person> persons = scene.getPersons();
			if (!persons.isEmpty()) {
			    for (Person person : persons) {
				addToVertexPerson(person);
				searchInvolvedTags(tagLinkDao.findByPerson(person));
				searchInvolvedItems(itemLinkDao.findByPerson((Person) person));
			    }
			}
			List<Item> items = scene.getItems();
			if (!items.isEmpty()) {
			    for (Item item : items) {
				addToVertexInvolvedItem(item);
			    }
			}
		    }
		}
	    }
	}
	model.commit();
    }

    private void createPersonGraph() {
	BookModel model = mainFrame.getBookModel();
	Session session = model.beginTransaction();
	PersonDAOImpl personDAO = new PersonDAOImpl(session);
	Person localPerson = (Person) personDAO.find(Long.valueOf(entityId));
	if (localPerson == null) {
	    model.commit();
	    return;
	}
	SceneDAOImpl sceneDAO = new SceneDAOImpl(session);
	TagLinkDAOImpl tagLinkDAO = new TagLinkDAOImpl(session);
	ItemLinkDAOImpl itemLinkDAO = new ItemLinkDAOImpl(session);
	graphIndex = 0L;
	graph.addVertex(localPerson);
	labelMap.put(localPerson, localPerson.toString());
	iconMap.put(localPerson, getPersonIcon(localPerson, SbConstants.IconSize.LARGE));
	initVertices(localPerson);
	List<TagLink> tagLinks = tagLinkDAO.findByPerson(localPerson);
	if (!tagLinks.isEmpty()) {
	    for (TagLink tagLink : tagLinks) {
		if (tagLink.hasPeriod() && (chosenDate != null)) {
		    Period period = tagLink.getPeriod();
		    if ((period != null) && (!period.isInside(chosenDate))) {
			addToVertexTag(tagLink.getTag());
		    }
		} else {
		    addToVertexTag(tagLink.getTag());
		}
	    }
	}
	List<ItemLink> itemLinks = itemLinkDAO.findByPerson(localPerson);
	if (!itemLinks.isEmpty()) {
	    for (ItemLink itemLink : itemLinks) {
		if (itemLink.hasPeriod() && (chosenDate != null)) {
		    Period period = itemLink.getPeriod();
		    if ((period != null) && (!period.isInside(chosenDate))) {
			addToVertexItem(itemLink.getItem());
		    }
		} else {
		    addToVertexItem(itemLink.getItem());
		}
	    }
	}
	List<Scene> scenes = sceneDAO.findAll();
	if (!scenes.isEmpty()) {
	    for (Scene scene : scenes) {
		boolean c = false;
		if (chosenDate == null) {
		    c = true;
		} else if ((!scene.hasNoSceneTs()) && (chosenDate.compareTo(scene.getDate()) == 0)) {
		    c = true;
		}
		if (c && (scene.getPersons().contains(localPerson))) {
		    addToVertexScene(scene);
		    sceneIds.add(scene.getId());
		    List<TagLink> TLbys = tagLinkDAO.findByScene(scene);
		    if (!TLbys.isEmpty()) {
			for (TagLink TLby : TLbys) {
			    addToVertexInvolvedTag(TLby.getTag());
			}
		    }
		    List<ItemLink> ILbys = itemLinkDAO.findByScene(scene);
		    if (!ILbys.isEmpty()) {
			for (ItemLink ILby : ILbys) {
			    addToVertexInvolvedItem(ILby.getItem());
			}
		    }
		    List<Location> locations = scene.getLocations();
		    if (!locations.isEmpty()) {
			for (Location location : locations) {
			    addToVertexLocation(location);
			    TLbys = tagLinkDAO.findByLocation((Location) location);
			    if (!TLbys.isEmpty()) {
				for (TagLink TLby : TLbys) {
				    addToVertexInvolvedTag(TLby.getTag());
				}
			    }
			    ILbys = itemLinkDAO.findByLocation((Location) location);
			    if (!ILbys.isEmpty()) {
				for (ItemLink ILby : ILbys) {
				    addToVertexInvolvedItem(ILby.getItem());
				}
			    }
			}
		    }
		    List<Person> persons = scene.getPersons();
		    if (!persons.isEmpty()) {
			for (Person person : persons) {
			    if (!person.equals(localPerson)) {
				addToVertexPerson(person);
				TLbys = tagLinkDAO.findByPerson(person);
				if (!TLbys.isEmpty()) {
				    for (TagLink TLby : TLbys) {
					addToVertexInvolvedTag(TLby.getTag());
				    }
				}
				ILbys = itemLinkDAO.findByPerson(person);
				if (!ILbys.isEmpty()) {
				    for (ItemLink ILby : ILbys) {
					addToVertexInvolvedItem(ILby.getItem());
				    }
				}
			    }
			}
		    }
		    List<Item> items = scene.getItems();
		    if (!items.isEmpty()) {
			for (Item item : items) {
			    addToVertexInvolvedItem(item);
			}
		    }
		}
	    }
	}
	model.commit();
    }

    private boolean isTagInGraph(Tag tag) {
	if (tag == null) {
	    return false;
	}
	Collection collection = graph.getVertices();
	Iterator iterator = collection.iterator();
	while (iterator.hasNext()) {
	    AbstractEntity entity = (AbstractEntity) iterator.next();
	    if (((entity instanceof Tag)) && (entity.getId().equals(tag.getId()))) {
		return true;
	    }
	}
	return false;
    }

    private boolean isItemInGraph(Item item) {
	if (item == null) {
	    return false;
	}
	Collection collection = graph.getVertices();
	Iterator iterator = collection.iterator();
	while (iterator.hasNext()) {
	    AbstractEntity entity = (AbstractEntity) iterator.next();
	    if (((entity instanceof Item)) && (entity.getId().equals(item.getId()))) {
		return true;
	    }
	}
	return false;
    }

    @SuppressWarnings("unchecked")
    void initVertices(AbstractEntity entity) {
	initVertexScene(entity);
	initVertexPerson(entity);
	initVertexLocation(entity);
	if (showTagVertex) {
	    if (!(entity instanceof Tag)) {
		initVertexTag(entity);
	    }
	    if (!(entity instanceof Item)) {
		initVertexItem(entity);
	    }
	}
	initVertexInvoldedTag(entity);
	initVertexInvoldedItem(entity);
	sceneIds = new ArrayList();
	involvedTags = new HashSet();
	involvedItems = new HashSet();
	sceneVertexTitle = null;
	locationVertexTitle = null;
	showTagVertex = true;
    }

    private void initVertexTag(AbstractEntity entity) {
	tagVertex = new Tag();
	tagVertex.setName(I18N.getMsg("msg.tags"));
	graph.addVertex(tagVertex);
	labelMap.put(tagVertex, tagVertex.getName());
	iconMap.put(tagVertex, emptyIcon);
	graph.addEdge(graphIndex++, entity, tagVertex);
    }

    private void initVertexItem(AbstractEntity entity) {
	itemVertex = new Item();
	itemVertex.setName(I18N.getMsg("msg.items"));
	graph.addVertex(itemVertex);
	labelMap.put(itemVertex, itemVertex.getName());
	iconMap.put(itemVertex, emptyIcon);
	graph.addEdge(graphIndex++, entity, itemVertex);
    }

    private void initVertexInvoldedTag(AbstractEntity entity) {
	involvedTagVertex = new Tag();
	involvedTagVertex.setName(I18N.getMsg("msg.graph.involved.tags"));
	graph.addVertex(involvedTagVertex);
	labelMap.put(involvedTagVertex, involvedTagVertex.getName());
	iconMap.put(involvedTagVertex, emptyIcon);
	graph.addEdge(graphIndex++, entity, involvedTagVertex);
    }

    private void initVertexInvoldedItem(AbstractEntity entity) {
	involvedItemVertex = new Item();
	involvedItemVertex.setName(I18N.getMsg("msg.graph.involved.items"));
	graph.addVertex(involvedItemVertex);
	labelMap.put(involvedItemVertex, involvedItemVertex.getName());
	iconMap.put(involvedItemVertex, emptyIcon);
	graph.addEdge(graphIndex++, entity, involvedItemVertex);
    }

    void addToVertexTag(Tag tag) {
	if (tag != null) {
	    graph.addVertex(tag);
	    labelMap.put(tag, tag.toString());
	    iconMap.put(tag, tagIconMedium);
	    graph.addEdge(graphIndex++, tagVertex, tag);
	}
    }

    void addToVertexTags(Set<Tag> paramSet) {
	for (Tag tag : paramSet) {
	    addToVertexTag(tag);
	}
    }

    void addToVertexItem(Item item) {
	if (item != null) {
	    graph.addVertex(item);
	    labelMap.put(item, item.toString());
	    iconMap.put(item, itemIconMedium);
	    graph.addEdge(graphIndex++, itemVertex, item);
	}
    }

    void addToVertexItems(Set<Item> paramSet) {
	if (!paramSet.isEmpty()) {
	    for (Item item : paramSet) {
		addToVertexItem(item);
	    }
	}
    }

    void addToVertexInvolvedTag(Tag tag) {
	if (tag != null) {
	    if (!isTagInGraph(tag)) {
		graph.addVertex(tag);
		labelMap.put(tag, tag.toString());
		iconMap.put(tag, tagIconMedium);
		graph.addEdge(graphIndex++, involvedTagVertex, tag);
	    }
	}
    }

    void addToVertexInvolvedTags() {
	if (!involvedTags.isEmpty()) {
	    for (Tag tag : involvedTags) {
		addToVertexInvolvedTag(tag);
	    }
	}
    }

    void addToVertexInvolvedItem(Item item) {
	if (item != null) {
	    if (!isItemInGraph(item)) {
		graph.addVertex(item);
		labelMap.put(item, item.toString());
		iconMap.put(item, itemIconMedium);
		graph.addEdge(graphIndex++, involvedItemVertex, item);
	    }
	}
    }

    void addToVertexInvolvedItems() {
	if (!involvedItems.isEmpty()) {
	    for (Item item : involvedItems) {
		addToVertexInvolvedItem(item);
	    }
	}
    }

    void initVertexScene(AbstractEntity entity) {
	sceneVertex = new Scene();
	if (sceneVertexTitle != null) {
	    sceneVertex.setTitle(sceneVertexTitle);
	} else {
	    sceneVertex.setTitle(I18N.getMsg("msg.common.scenes"));
	}
	graph.addVertex(sceneVertex);
	labelMap.put(sceneVertex, sceneVertex.toString());
	iconMap.put(sceneVertex, emptyIcon);
	graph.addEdge(graphIndex++, entity, sceneVertex);
    }

    private void initVertexPerson(AbstractEntity entity) {
	personVertex = new Person();
	personVertex.setFirstname(I18N.getMsg("msg.common.persons"));
	graph.addVertex(personVertex);
	labelMap.put(personVertex, personVertex.getFullName());
	iconMap.put(personVertex, emptyIcon);
	graph.addEdge(graphIndex++, entity, personVertex);
    }

    private void initVertexLocation(AbstractEntity entity) {
	locationVertex = new Location();
	if (locationVertexTitle != null) {
	    locationVertex.setName(locationVertexTitle);
	} else {
	    locationVertex.setName(I18N.getMsg("msg.menu.locations"));
	}
	graph.addVertex(locationVertex);
	labelMap.put(locationVertex, locationVertex.toString());
	iconMap.put(locationVertex, emptyIcon);
	graph.addEdge(graphIndex++, entity, locationVertex);
    }

    private Icon getPersonIcon(Person person, SbConstants.IconSize sizeIcon) {
	if (sizeIcon == SbConstants.IconSize.MEDIUM) {
	    if (person.getGender().isMale()) {
		return manIconMedium;
	    }
	    if (person.getGender().isFemale()) {
		return womanIconMedium;
	    }
	    return personIconMedium;
	}
	if (sizeIcon == SbConstants.IconSize.LARGE) {
	    if (person.getGender().isMale()) {
		return manIconLarge;
	    }
	    if (person.getGender().isFemale()) {
		return womanIconLarge;
	    }
	    return personIconLarge;
	}
	return emptyIcon;
    }

    public void refresh(AbstractEntity entity) {
	try {
	    if (entity == null) {
		return;
	    }
	    entityId = entity.getId();
	    refreshGraph(entity);
	    updateControlPanel(entity);
	} catch (Exception exc) {
	    SbApp.error("MemoriaPanel.refresh(" + entity.toString() + ")", exc);
	}
    }

    private void updateControlPanel(AbstractEntity pEntity) {
	int i = 0;
	EntityTypeCbItem tobj;
	for (int j = 0; j < entityTypeCombo.getItemCount(); j++) {
	    tobj = (EntityTypeCbItem) entityTypeCombo.getItemAt(j);
	    if ((tobj.getType() == EntityTypeCbItem.Type.PERSON) && ((pEntity instanceof Person))) {
		i = j;
		break;
	    }
	    if ((tobj.getType() == EntityTypeCbItem.Type.LOCATION) && ((pEntity instanceof Location))) {
		i = j;
		break;
	    }
	    if ((tobj.getType() == EntityTypeCbItem.Type.SCENE) && ((pEntity instanceof Scene))) {
		i = j;
		break;
	    }
	    if ((tobj.getType() == EntityTypeCbItem.Type.TAG) && ((pEntity instanceof Tag))) {
		i = j;
		break;
	    }
	    if ((tobj.getType() == EntityTypeCbItem.Type.ITEM) && ((pEntity instanceof Item))) {
		i = j;
		break;
	    }
	}
	entityTypeCombo.setSelectedIndex(i);
	int j;
	if ((pEntity instanceof Scene)) {
	    for (j = 0; j < entityCombo.getItemCount(); j++) {
		Scene s = (Scene) entityCombo.getItemAt(j);
		if (s.getId().equals(pEntity.getId())) {
		    entityCombo.setSelectedIndex(j);
		    break;
		}
	    }
	} else {
	    entityCombo.setSelectedItem(pEntity);
	}
    }

    public boolean hasAutoRefresh() {
	return cbAutoRefresh.isSelected();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
	if ((evt.getSource() == null) || (!processActionListener)) {
	    return;
	}
	if (evt.getSource() instanceof JButton) {
	    String buttonString = ((JButton) evt.getSource()).getName();
	    int i = dateCombo.getSelectedIndex();
	    if (dateCombo.getSize().height == 0) {
		return;
	    }
	    if (SbConstants.ComponentName.BT_PREVIOUS.check(buttonString)) {
		i--;
		if (i < 0) {
		    return;
		}
	    } else if (SbConstants.ComponentName.BT_NEXT.check(buttonString)) {
		i++;
		if (i > dateCombo.getItemCount() - 1) {
		    return;
		}
	    } else if (SbConstants.ComponentName.BT_FIRST.check(buttonString)) {
		i = 0;
	    } else if (SbConstants.ComponentName.BT_LAST.check(buttonString)) {
		i = dateCombo.getItemCount() - 1;
	    }
	    dateCombo.setSelectedIndex(i);
	    return;
	}
	entitySourceName = ((JComponent) evt.getSource()).getName();
	if (entitySourceName.equals(SbConstants.ComponentName.COMBO_ENTITY_TYPES.toString())) {
	    EntityTypeCbItem eCombo = (EntityTypeCbItem) entityTypeCombo.getSelectedItem();
	    refreshEntityCombo(eCombo.getType());
	    return;
	}
	chosenDate = ((Date) dateCombo.getSelectedItem());
	refresh((AbstractEntity) entityCombo.getSelectedItem());
    }

    @Override
    public void refresh() {
	refreshControlPanel();
	refreshGraph();
    }

    @Override
    public MainFrame getMainFrame() {
	return mainFrame;
    }

    private boolean isInPeriod(Period period, Date date) {
	return (period == null) || (date == null) || (period.isInside(date));
    }
    /*
     @SuppressWarnings("unchecked")
     class VertexStringerImpl<V> implements Transformer<V, String> {

     Map<V, String> map = new HashMap();
     boolean enabled = true;

     public VertexStringerImpl() {
     Object localObject = null;
     map = (Map<V, String>) localObject;
     }

     private VertexStringerImpl(Map<AbstractEntity, String> labelMap) {
     map = (Map<V, String>) labelMap;
     }

     private VertexStringerImpl(VertexStringerImpl lvsi) {
     map=(Map<V, String>) lvsi;
     }

     @Override
     public String transform(V paramV) {
     if (isEnabled()) {
     return "<html><table width='100'><tr><td>" + (String) map.get(paramV) + "</td></tr></table>";
     }
     return "";
     }

     public boolean isEnabled() {
     return enabled;
     }

     public void setEnabled(boolean paramBoolean) {
     enabled = paramBoolean;
     }
     }
     */

    void searchInvolvedTags(List<TagLink> tagLinks) {
	if (!tagLinks.isEmpty()) {
	    for (TagLink tagLink : tagLinks) {
		if (tagLink.hasOnlyScene()) {
		    involvedTags.add(tagLink.getTag());
		}
	    }
	}
    }

    void searchInvolvedItems(List<ItemLink> itemLinks) {
	if (!itemLinks.isEmpty()) {
	    for (ItemLink itemLink : itemLinks) {
		if (itemLink.hasOnlyScene()) {
		    involvedItems.add(itemLink.getItem());
		}
	    }
	}
    }

}
