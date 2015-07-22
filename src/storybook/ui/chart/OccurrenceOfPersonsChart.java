/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package storybook.ui.chart;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;
import storybook.model.BookModel;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.Person;
import storybook.toolkit.swing.ColorUtil;
import storybook.ui.MainFrame;
import storybook.ui.chart.jfreechart.ChartUtil;
import storybook.ui.chart.jfreechart.DbTableCategoryItemLabelGenerator;

public class OccurrenceOfPersonsChart extends AbstractPersonsChart {

	private ChartPanel chartPanel;
	private double average;

	public OccurrenceOfPersonsChart(MainFrame paramMainFrame) {
		super(paramMainFrame, "msg.report.person.occurrence.title");
	}

	@Override
	protected void initChartUi() {
		CategoryDataset setCategory = createDataset();
		JFreeChart chart = createChart(setCategory);
		this.chartPanel = new ChartPanel(chart);
		this.panel.add(this.chartPanel, "grow");
	}

	private JFreeChart createChart(CategoryDataset setCategory) {
		JFreeChart chart = ChartFactory.createBarChart(this.chartTitle, "", "", setCategory, PlotOrientation.VERTICAL, true, true, false);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		ChartUtil.hideDomainAxis(plot);
		plot.addRangeMarker(ChartUtil.getAverageMarker(this.average), Layer.FOREGROUND);
		BarRenderer bar = (BarRenderer) plot.getRenderer();
		DbTableCategoryItemLabelGenerator item = new DbTableCategoryItemLabelGenerator();
		bar.setBaseItemLabelGenerator(item);
		bar.setBaseItemLabelsVisible(true);
		ItemLabelPosition position = ChartUtil.getNiceItemLabelPosition();
		bar.setBasePositiveItemLabelPosition(position);
		bar.setPositiveItemLabelPositionFallback(position);
		int i = 0;
		Color[] colors = ColorUtil.getDarkColors(ColorUtil.getPastelColors(), 0.35D);
		for (int j = 0; j < setCategory.getRowCount(); j++) {
			Person person = (Person) setCategory.getRowKey(j);
			Color color = person.getJColor();
			if (color != null) {
				color = ColorUtil.darker(color, 0.15D);
			} else {
				color = colors[(j % colors.length)];
			}
			bar.setSeriesPaint(j, color);
			if ((color != null) && (ColorUtil.isDark(color))) {
				bar.setSeriesItemLabelPaint(i, Color.white);
			}
			i++;
		}
		return chart;
	}

	private CategoryDataset createDataset() {
		DefaultCategoryDataset setCategory = new DefaultCategoryDataset();
		try {
			BookModel model = this.mainFrame.getBookModel();
			Session session = model.beginTransaction();
			PersonDAOImpl dao = new PersonDAOImpl(session);
			List categories = dao.findByCategories(this.selectedCategories);
			SceneDAOImpl scenes = new SceneDAOImpl(session);
			double d = 0.0D;
			Iterator localIterator = categories.iterator();
			while (localIterator.hasNext()) {
				Person person = (Person) localIterator.next();
				long l = scenes.countByPerson(person);
				setCategory.addValue(l, person, new Integer(1));
				d += l;
			}
			model.commit();
			this.average = (d / categories.size());
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return setCategory;
	}
}