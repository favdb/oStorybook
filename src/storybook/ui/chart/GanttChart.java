/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package storybook.ui.chart;

import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.Gender;
import storybook.model.hbn.entity.Person;
import storybook.toolkit.I18N;
import storybook.ui.MainFrame;
import storybook.ui.chart.jfreechart.ChartUtil;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Session;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.ui.Layer;
import storybook.SbConstants;

public class GanttChart extends AbstractPersonsChart {

	private ChartPanel chartPanel;
	private JCheckBox cbShowPeriodsOfLife;
	private List<JCheckBox> personCbList;
	private JPanel personsPanel;

	public GanttChart(MainFrame paramMainFrame) {
		super(paramMainFrame, "msg.chart.gantt.characters.title");
	}

	@Override
	protected void initChart() {
		super.initChart();
		refreshPersonCbList();
		this.cbShowPeriodsOfLife = new JCheckBox(I18N.getMsg("msg.chart.gantt.periods.life"));
		this.cbShowPeriodsOfLife.setOpaque(false);
		this.cbShowPeriodsOfLife.setSelected(true);
		this.cbShowPeriodsOfLife.addActionListener(this);
	}

	@Override
	protected void initChartUi() {
		IntervalCategoryDataset localIntervalCategoryDataset = createDataset();
		JFreeChart localJFreeChart = createChart(localIntervalCategoryDataset);
		this.chartPanel = new ChartPanel(localJFreeChart);
		this.panel.add(this.chartPanel, "grow");
	}

	@Override
	protected void initOptionsUi() {
		super.initOptionsUi();
		this.optionsPanel.add(this.cbShowPeriodsOfLife, "right,gap push");
		this.personsPanel = new JPanel(new MigLayout("flowx"));
		this.personsPanel.setOpaque(false);
		this.panel.setOpaque(false);
		refreshPersonsPanel();
		this.optionsPanel.add(this.personsPanel, "newline,span");
	}

	@Override
	public void actionPerformed(ActionEvent paramActionEvent) {
		if ((paramActionEvent.getSource() instanceof JCheckBox)) {
			JCheckBox localJCheckBox = (JCheckBox) paramActionEvent.getSource();
			Object localObject = localJCheckBox.getClientProperty(SbConstants.ComponentName.CB_CATEGORY);
			if (localObject != null) {
				refreshPersonsPanel();
				return;
			}
			super.actionPerformed(paramActionEvent);
		}
	}

	private void refreshPersonCbList() {
		this.personCbList = EntityUtil.createPersonCheckBoxes(this.mainFrame, this.categoryCbList, this);
	}

	private void refreshPersonsPanel() {
		List localList = getSelectedPersons();
		refreshPersonCbList();
		this.personsPanel.removeAll();
		int i = 0;
		Iterator localIterator = this.personCbList.iterator();
		while (localIterator.hasNext()) {
			JCheckBox localJCheckBox = (JCheckBox) localIterator.next();
			Person localPerson = (Person) localJCheckBox.getClientProperty(SbConstants.ComponentName.CB_PERSON);
			if (localList.contains(localPerson)) {
				localJCheckBox.setSelected(true);
			}
			String str = "";
			if (i % 5 == 0) {
				str = "newline";
			}
			this.personsPanel.add(localJCheckBox, str);
			i++;
		}
		this.personsPanel.revalidate();
		this.personsPanel.repaint();
	}

	private JFreeChart createChart(IntervalCategoryDataset paramIntervalCategoryDataset) {
		JFreeChart localJFreeChart = ChartFactory.createGanttChart(I18N.getMsg("msg.chart.gantt.characters.title"), I18N.getMsg("msg.common.person"), I18N.getMsg("msg.common.date"), paramIntervalCategoryDataset, true, true, false);
		CategoryPlot localCategoryPlot = (CategoryPlot) localJFreeChart.getPlot();
		GanttRenderer localGanttRenderer = (GanttRenderer) localCategoryPlot.getRenderer();
		BookModel localDocumentModel = this.mainFrame.getBookModel();
		Session localSession = localDocumentModel.beginTransaction();
		SceneDAOImpl localSceneDAOImpl = new SceneDAOImpl(localSession);
		Date localDate1 = localSceneDAOImpl.findFirstDate();
		Date localDate2 = localSceneDAOImpl.findLastDate();
		localDocumentModel.commit();
		localCategoryPlot.addRangeMarker(ChartUtil.getDateIntervalMarker(localDate1, localDate2, I18N.getMsg("msg.chart.common.project.duration")), Layer.BACKGROUND);
		ChartUtil.setNiceSeriesColors(paramIntervalCategoryDataset, localGanttRenderer);
		return localJFreeChart;
	}

	private IntervalCategoryDataset createDataset() {
		BookModel model = this.mainFrame.getBookModel();
		Session session = model.beginTransaction();
		PersonDAOImpl personDAO = new PersonDAOImpl(session);
		List persons = personDAO.findByCategories(this.selectedCategories);
		SceneDAOImpl sceneDAO = new SceneDAOImpl(session);
		Date firstDate = sceneDAO.findFirstDate();
		Date lastDate = sceneDAO.findLastDate();
		model.commit();
		TaskSeries lifeTime = new TaskSeries(I18N.getMsg("msg.chart.gantt.lifetime"));
		TaskSeries childHood = new TaskSeries(I18N.getMsg("msg.chart.gantt.childhood"));
		TaskSeries adolescence = new TaskSeries(I18N.getMsg("msg.chart.gantt.adolescence"));
		TaskSeries adultHood = new TaskSeries(I18N.getMsg("msg.chart.gantt.adulthood"));
		TaskSeries retirement = new TaskSeries(I18N.getMsg("msg.chart.gantt.retirement"));
		List localList2 = getSelectedPersons();
		Object person = persons.iterator();
		while (((Iterator) person).hasNext()) {
			Person localPerson = (Person) ((Iterator) person).next();
			if (localList2.contains(localPerson)) {
				Date localDate3 = localPerson.getBirthday();
				if (localDate3 == null) {
					localDate3 = firstDate;
				}
				Date localDate4 = localPerson.getDayofdeath();
				if (localDate4 == null) {
					localDate4 = lastDate;
				}
				SimpleTimePeriod localSimpleTimePeriod1 = new SimpleTimePeriod(localDate3, localDate4);
				Task localTask = new Task(localPerson.toString(), localSimpleTimePeriod1);
				lifeTime.add(localTask);
				if (this.cbShowPeriodsOfLife.isSelected()) {
					Gender localGender = localPerson.getGender();
					Date localDate5 = DateUtils.addYears(localDate3, localGender.getChildhood().intValue());
					SimpleTimePeriod localSimpleTimePeriod2 = new SimpleTimePeriod(localDate3, localDate5);
					localTask = new Task(localPerson.toString(), localSimpleTimePeriod2);
					childHood.add(localTask);
					Date localDate6 = DateUtils.addYears(localDate5, localGender.getAdolescence().intValue());
					SimpleTimePeriod localSimpleTimePeriod3 = new SimpleTimePeriod(localDate5, localDate6);
					localTask = new Task(localPerson.toString(), localSimpleTimePeriod3);
					adolescence.add(localTask);
					Date localDate7 = DateUtils.addYears(localDate6, localGender.getAdulthood().intValue());
					SimpleTimePeriod localSimpleTimePeriod4 = new SimpleTimePeriod(localDate6, localDate7);
					localTask = new Task(localPerson.toString(), localSimpleTimePeriod4);
					adultHood.add(localTask);
					Date localDate8 = DateUtils.addYears(localDate7, localGender.getRetirement().intValue());
					SimpleTimePeriod localSimpleTimePeriod5 = new SimpleTimePeriod(localDate7, localDate8);
					localTask = new Task(localPerson.toString(), localSimpleTimePeriod5);
					retirement.add(localTask);
				}
			}
		}
		TaskSeriesCollection localObj = new TaskSeriesCollection();
		((TaskSeriesCollection) localObj).add(lifeTime);
		if (this.cbShowPeriodsOfLife.isSelected()) {
			((TaskSeriesCollection) localObj).add(childHood);
			((TaskSeriesCollection) localObj).add(adolescence);
			((TaskSeriesCollection) localObj).add(adultHood);
			((TaskSeriesCollection) localObj).add(retirement);
		}
		return localObj;
	}

	@SuppressWarnings("unchecked")
	private List<Person> getSelectedPersons() {
		ArrayList localArrayList = new ArrayList();
		Iterator localIterator = this.personCbList.iterator();
		while (localIterator.hasNext()) {
			JCheckBox localJCheckBox = (JCheckBox) localIterator.next();
			if (localJCheckBox.isSelected()) {
				Person localPerson = (Person) localJCheckBox.getClientProperty(SbConstants.ComponentName.CB_PERSON);
				localArrayList.add(localPerson);
			}
		}
		return localArrayList;
	}
}