package storybook.ui.plan;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.ui.Layer;

import storybook.SbConstants.ViewName;
import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Scene;
import storybook.model.stringcategory.AbstractStringCategory;
import storybook.toolkit.BookUtil;
import storybook.toolkit.I18N;
import storybook.toolkit.StringCategoryUtil;
import storybook.toolkit.odt.ODTUtils;
import storybook.toolkit.swing.CircleProgressBar;
import storybook.ui.MainFrame;
import storybook.ui.SbView;
import storybook.ui.chart.jfreechart.ChartUtil;
import storybook.ui.panel.AbstractPanel;

/**
 * Panel for planfication vision.
 * 
 * @author Jean
 *
 */
@SuppressWarnings("serial")
public class PlanPanel extends AbstractPanel implements MouseListener {
	
	/** Pane to contain all. */
	private JTabbedPane tabbedPane;
	private JTree tree;

	/**
	 * Constructor.
	 * 
	 * @param mainframe
	 *            to include panel in.
	 */
	public PlanPanel(MainFrame mainframe) {
		super(mainframe);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see storybook.ui.panel.AbstractPanel#init()
	 */
	@Override
	public void init() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see storybook.ui.panel.AbstractPanel#initUi()
	 */
	@Override
	public void initUi() {
		// Create tabbed pane
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		MigLayout migLayout0 = new MigLayout("fill", "[grow]", "[grow]");
		setLayout(migLayout0);

		// add all subpanels to tabbed pane
		addGlobalPanel();
		addTimePanel();
		addProgressPanel();

		// add tabbed pane to global panel
		add(tabbedPane, "pos 0% 0% 100% 100%");
	}

	/**
	 * Generate panel for showing global information
	 */
	private void addGlobalPanel() {
		// get neded elements
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		int allScenes = dao.findAll().size();
		int[] nbScenesByState = new int[6];
		for (int i = 0; i < 6; i++) {
			nbScenesByState[i] = dao.findBySceneState(i).size();
		}
		session.close();

		// create panel
		JPanel globalPanel = new JPanel();
		MigLayout migLayout = new MigLayout("wrap 5,fill", "[center, sizegroup]",
				"[][grow]");
		globalPanel.setLayout(migLayout);
		tabbedPane.addTab(I18N.getMsg("msg.plan.title.global"), globalPanel);

		// Get labels
		String[] labels = new String[6];
		labels[0] = I18N.getMsg("msg.status.outline");
		labels[1] = I18N.getMsg("msg.status.draft");
		labels[2] = I18N.getMsg("msg.status.1st.edit");
		labels[3] = I18N.getMsg("msg.status.2nd.edit");
		labels[4] = I18N.getMsg("msg.status.done");
		labels[5] = I18N.getMsg("msg.status.outline");

		// add progress bars
		for (int i = 0; i < 5; i++) {
			CircleProgressBar bar = new CircleProgressBar(0, 100);
			bar.setPreferredSize(new Dimension(100, 100));
			bar.setBorderPainted(false);
			globalPanel.add(bar, "split 2, flowy");
			bar.setValue((nbScenesByState[i + 1] * 100)
					/ ((allScenes == 0) ? 1 : allScenes));
			globalPanel.add(new JLabel(labels[i], SwingConstants.CENTER));
		}
	}

	/**
	 * Generate panel for showing size progress information
	 */
	private void addProgressPanel() {
		SizedElement topSp = new SizedElement();
		topSp.setElement(mainFrame.getDbFile().getName());
		// create panel
		DefaultMutableTreeNode topNode = new DefaultMutableTreeNode(topSp);

		tree = new JTree(topNode);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new SizeTreeCellRenderer());
		JScrollPane scroller = new JScrollPane(tree);

		Map<Object, Integer> sizes = ODTUtils.getElementsSize(mainFrame);
		
		// get elements
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl partdao = new PartDAOImpl(session);
		List<Part> roots = partdao.findAllRoots();
		session.close();
		
		int topsize = 0;
		int topmaxsize = 0;
		for (Part part : roots) {
			createSubStructure(topNode, part, sizes);
			topmaxsize += part.getObjectiveChars();
			topsize += sizes.get(part);
		}
		if (topmaxsize == 0) {
			topSp.setSize(100);
		} else {
		    topSp.setSize((topsize * 100) / topmaxsize);
		}
		
		tree.expandRow(0);
		tabbedPane.addTab(I18N.getMsg("msg.plan.title.progress"), scroller);
		tree.addMouseListener(this);

	}
	
	private void createSubStructure(DefaultMutableTreeNode father, Part part,
			Map<Object, Integer> sizes) {
		SizedElement sp = new SizedElement();
		sp.setElement(part);
		sp.setSize(sizes.get(part));
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(sp);
		father.add(node);
		
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PartDAOImpl partdao = new PartDAOImpl(session);
		List<Part> subparts = partdao.getParts(part);
		List<Chapter> chapters = partdao.findChapters(part);
		session.close();

		for (Part subpart : subparts) {
			createSubStructure(node, subpart, sizes);
		}
		for (Chapter chapter : chapters) {
			createSubStructure(node, chapter, sizes);
		}
	}
	
	private void createSubStructure(DefaultMutableTreeNode father, Chapter chapter,
			Map<Object, Integer> sizes) {
		SizedElement sp = new SizedElement();
		sp.setElement(chapter);
		sp.setSize(sizes.get(chapter));
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(sp);
		father.add(node);
		
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl chapterdao = new ChapterDAOImpl(session);
		List<Scene> scenes = chapterdao.findScenes(chapter);
		session.close();

		for (Scene scene : scenes) {
			SizedElement ssp = new SizedElement();
			ssp.setElement(scene);
			ssp.setSize(sizes.get(scene));
			DefaultMutableTreeNode subnode = new DefaultMutableTreeNode(ssp);
			node.add(subnode);
		}

	}

	/**
	 * Generate panel for showing timeline information
	 */
	private void addTimePanel() {
		// create panel and scroller to contain it
		JPanel timePanel = new JPanel();
		JScrollPane scroller = new JScrollPane(timePanel);
		add(scroller, "grow");
		MigLayout migLayout1 = new MigLayout("wrap 5,fill", "[center]",
				"[][grow]");
		timePanel.setLayout(migLayout1);
		tabbedPane.add(I18N.getMsg("msg.plan.title.timeline"), scroller);

		// add chart
		JFreeChart chart = createChart();
		ChartPanel chartPanel = new ChartPanel(chart);
		timePanel.add(chartPanel, "wrap, grow");
	}

	/**
	 * Create timeline chart
	 * 
	 * @return the chart
	 */
	private JFreeChart createChart() {
		TaskSeriesCollection categoryDataset = createDataset();
		JFreeChart localJFreeChart = ChartFactory.createGanttChart("",
				I18N.getMsg("msg.common.parts"),
				I18N.getMsg("msg.common.date"), categoryDataset, true, true,
				false);
		CategoryPlot localCategoryPlot = (CategoryPlot) localJFreeChart
				.getPlot();
		GanttRenderer localGanttRenderer = (GanttRenderer) localCategoryPlot
				.getRenderer();

		Date localDate1 = BookUtil.getBookCreationDate(this.mainFrame);

		localCategoryPlot.addRangeMarker(
				ChartUtil.getDateIntervalMarker(localDate1, new Date(),
						I18N.getMsg("msg.chart.common.project.duration")),
				Layer.BACKGROUND);
		ChartUtil.setNiceSeriesColors(categoryDataset, localGanttRenderer);
		return localJFreeChart;
	}

	/**
	 * Create time dataset
	 * 
	 * @return the dataset
	 */
	private TaskSeriesCollection createDataset() {
		BookModel model = this.mainFrame.getBookModel();
		Session session = model.beginTransaction();
		TaskSeries lifeTime = new TaskSeries(
				I18N.getMsg("msg.chart.common.part.duration"));
		PartDAOImpl partDao = new PartDAOImpl(session);
		List<Part> parts = partDao.findAll();
		session.close();
		for (Part part : parts) {
			Date startTime = part.getCreationTime();
			if (startTime == null) {
				startTime = BookUtil.getBookCreationDate(this.mainFrame);
			}
			if (startTime == null) {
				startTime = new Date();
			}
			Date endTime = (part.hasDoneTime()) ? part.getDoneTime() : (part
					.hasObjectiveTime()) ? part.getObjectiveTime() : new Date();

			SimpleTimePeriod localSimpleTimePeriod1 = new SimpleTimePeriod(
					startTime, endTime);
			Task localTask = new Task(part.toString(), localSimpleTimePeriod1);
			lifeTime.add(localTask);
		}
		TaskSeriesCollection localObj = new TaskSeriesCollection();
		((TaskSeriesCollection) localObj).add(lifeTime);
		return localObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see storybook.ui.panel.AbstractPanel#modelPropertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		// Object oldValue = evt.getOldValue();
	    Object newValue = evt.getNewValue();
		String propName = evt.getPropertyName();

		if (BookController.SceneProps.INIT.check(propName)) {
			refresh();
			return;
		}

		else if (BookController.CommonProps.REFRESH.check(propName)) {
			if (newValue instanceof SbView) {
				if (ViewName.PLAN.compare(((SbView)newValue))) {
					refresh();
				}
			}
			return;
		}
	}
	
	private void showPopupMenu(JTree tree, MouseEvent evt) {
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
		Object eltObj = selectedNode.getUserObject();
		if (!(eltObj instanceof SizedElement)) {
			return;
		}
		JPopupMenu menu = null;
		Object userObj = ((SizedElement)eltObj).getElement();
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
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			showPopupMenu(tree, e);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}