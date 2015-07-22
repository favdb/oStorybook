/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package storybook.ui.chart;

import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.I18N;
import storybook.ui.MainFrame;
import storybook.ui.chart.jfreechart.ChartUtil;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import javax.swing.JPanel;
import org.hibernate.Session;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.Day;
import org.jfree.data.time.Week;
import org.jfree.ui.TextAnchor;

public class PersonsByDateChart extends AbstractPersonsChart {

	private ChartPanel chartPanel;
	private String domainAxisLabel = I18N.getMsg("msg.common.person");
	private String rangeAxisLabel = I18N.getMsg("msg.common.date");

	public PersonsByDateChart(MainFrame paramMainFrame) {
		super(paramMainFrame, "msg.menu.tools.charts.overall.character.date");
		this.partRelated = true;
	}

	protected void initChartUi() {
		IntervalCategoryDataset localIntervalCategoryDataset = createDataset();
		JFreeChart localJFreeChart = createChart(localIntervalCategoryDataset);
		this.chartPanel = new ChartPanel(localJFreeChart);
		this.panel.add(this.chartPanel, "grow");
	}

	private JFreeChart createChart(IntervalCategoryDataset paramIntervalCategoryDataset) {
		JFreeChart localJFreeChart = ChartFactory.createGanttChart(this.chartTitle, this.domainAxisLabel, this.rangeAxisLabel, paramIntervalCategoryDataset, true, true, false);
		CategoryPlot localCategoryPlot = (CategoryPlot) localJFreeChart.getPlot();
		GanttRenderer localGanttRenderer = (GanttRenderer) localCategoryPlot.getRenderer();
		ChartUtil.hideLegend(localCategoryPlot);
		StandardCategoryItemLabelGenerator localStandardCategoryItemLabelGenerator = new StandardCategoryItemLabelGenerator();
		localGanttRenderer.setBaseItemLabelGenerator(localStandardCategoryItemLabelGenerator);
		localGanttRenderer.setBaseItemLabelsVisible(true);
		ItemLabelPosition localItemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER);
		localGanttRenderer.setBasePositiveItemLabelPosition(localItemLabelPosition);
		ChartUtil.setNiceSeriesColors(paramIntervalCategoryDataset, localGanttRenderer);
		return localJFreeChart;
	}

	@SuppressWarnings("unchecked")
	private IntervalCategoryDataset createDataset() {
		TaskSeriesCollection localTaskSeriesCollection = new TaskSeriesCollection();
		try {
			Part localPart = this.mainFrame.getCurrentPart();
			BookModel localDocumentModel = this.mainFrame.getBookModel();
			Session localSession = localDocumentModel.beginTransaction();
			PersonDAOImpl localPersonDAOImpl = new PersonDAOImpl(localSession);
			List localList1 = localPersonDAOImpl.findByCategories(this.selectedCategories);
			ChapterDAOImpl localChapterDAOImpl = new ChapterDAOImpl(localSession);
			List localList2 = localChapterDAOImpl.findAll(localPart);
			TaskSeries localTaskSeries = new TaskSeries("Serie 1");
			Iterator localIterator = localList1.iterator();
			while (localIterator.hasNext()) {
				Person localPerson = (Person) localIterator.next();
				TreeSet localTreeSet = new TreeSet();
				Object localObject1 = localList2.iterator();
				Object localObject2;
				Object localObject3;
				Object localObject4;
				while (((Iterator) localObject1).hasNext()) {
					localObject2 = (Chapter) ((Iterator) localObject1).next();
					localObject3 = localChapterDAOImpl.findScenes((Chapter) localObject2);
					localObject4 = ((List) localObject3).iterator();
					while (((Iterator) localObject4).hasNext()) {
						Scene localScene = (Scene) ((Iterator) localObject4).next();
						if (localScene.hasSceneTs()) {
							List localList3 = localScene.getPersons();
							if ((!localList3.isEmpty()) && (localList3.contains(localPerson)))
								localTreeSet.add(localScene.getSceneTs());
						}
					}
				}
				if (!localTreeSet.isEmpty()) {
					localTreeSet = ChartUtil.correctDates(localTreeSet);
					localObject1 = new Task(localPerson.toString(), new Week((Date) localTreeSet.first()));
					localObject2 = localTreeSet.iterator();
					while (((Iterator) localObject2).hasNext()) {
						localObject3 = (Date) ((Iterator) localObject2).next();
						localObject4 = new Task(localPerson.toString(), new Day((Date) localObject3));
						((Task) localObject1).addSubtask((Task) localObject4);
					}
					localTaskSeries.add((Task) localObject1);
				}
			}
			localDocumentModel.commit();
			localTaskSeriesCollection.add(localTaskSeries);
		} catch (Exception localException) {
		}
		return localTaskSeriesCollection;
	}
}
