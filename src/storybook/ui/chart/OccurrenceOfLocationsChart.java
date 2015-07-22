/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package storybook.ui.chart;

/**
 *
 * @author favdb
 */
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.entity.Location;
import storybook.toolkit.I18N;
import storybook.ui.MainFrame;
import storybook.ui.chart.jfreechart.ChartUtil;
import storybook.ui.chart.jfreechart.DbTableCategoryItemLabelGenerator;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
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

public class OccurrenceOfLocationsChart extends AbstractChartPanel {

	private ChartPanel chartPanel;
	private double average;
	private List<JCheckBox> countryCbList;
	protected List<String> selectedCountries;

	public OccurrenceOfLocationsChart(MainFrame paramMainFrame) {
		super(paramMainFrame, "msg.report.location.occurrence.title");
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void initChart() {
		this.countryCbList = EntityUtil.createCountryCheckBoxes(this.mainFrame, this);
		this.selectedCountries = new ArrayList();
		updateSelectedCountries();
	}

	@Override
	protected void initChartUi() {
		CategoryDataset localCategoryDataset = createDataset();
		JFreeChart localJFreeChart = createChart(localCategoryDataset);
		this.chartPanel = new ChartPanel(localJFreeChart);
		this.panel.add(this.chartPanel, "grow");
	}

	@Override
	protected void initOptionsUi() {
		JPanel localJPanel = new JPanel(new MigLayout("flowx"));
		localJPanel.setOpaque(false);
		JLabel localJLabel = new JLabel(I18N.getMsgColon("msg.dlg.location.country"));
		localJPanel.add(localJLabel);
		Iterator localIterator = this.countryCbList.iterator();
		while (localIterator.hasNext()) {
			JCheckBox localJCheckBox = (JCheckBox) localIterator.next();
			localJPanel.add(localJCheckBox);
		}
		this.optionsPanel.add(localJPanel);
	}

	@Override
	public void actionPerformed(ActionEvent paramActionEvent) {
		updateSelectedCountries();
		refreshChart();
	}

	private void updateSelectedCountries() {
		this.selectedCountries.clear();
		Iterator localIterator = this.countryCbList.iterator();
		while (localIterator.hasNext()) {
			JCheckBox localJCheckBox = (JCheckBox) localIterator.next();
			if (localJCheckBox.isSelected()) {
				this.selectedCountries.add(localJCheckBox.getText());
			}
		}
	}

	private JFreeChart createChart(CategoryDataset paramCategoryDataset) {
		JFreeChart localJFreeChart = ChartFactory.createBarChart(this.chartTitle, "", "", paramCategoryDataset, PlotOrientation.VERTICAL, true, true, false);
		CategoryPlot localCategoryPlot = (CategoryPlot) localJFreeChart.getPlot();
		ChartUtil.hideDomainAxis(localCategoryPlot);
		localCategoryPlot.addRangeMarker(ChartUtil.getAverageMarker(this.average), Layer.FOREGROUND);
		BarRenderer localBarRenderer = (BarRenderer) localCategoryPlot.getRenderer();
		DbTableCategoryItemLabelGenerator localDbTableCategoryItemLabelGenerator = new DbTableCategoryItemLabelGenerator();
		localBarRenderer.setBaseItemLabelGenerator(localDbTableCategoryItemLabelGenerator);
		localBarRenderer.setBaseItemLabelsVisible(true);
		ItemLabelPosition localItemLabelPosition = ChartUtil.getNiceItemLabelPosition();
		localBarRenderer.setBasePositiveItemLabelPosition(localItemLabelPosition);
		localBarRenderer.setPositiveItemLabelPositionFallback(localItemLabelPosition);
		ChartUtil.setNiceSeriesColors(paramCategoryDataset, localBarRenderer);
		return localJFreeChart;
	}

	private CategoryDataset createDataset() {
		DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
		try {
			BookModel localDocumentModel = this.mainFrame.getBookModel();
			Session localSession = localDocumentModel.beginTransaction();
			LocationDAOImpl localLocationDAOImpl = new LocationDAOImpl(localSession);
			SceneDAOImpl localSceneDAOImpl = new SceneDAOImpl(localSession);
			List localList = localLocationDAOImpl.findByCountries(this.selectedCountries);
			double d = 0.0D;
			Iterator localIterator = localList.iterator();
			while (localIterator.hasNext()) {
				Location localLocation = (Location) localIterator.next();
				long l = localSceneDAOImpl.countByLocation(localLocation);
				localDefaultCategoryDataset.addValue(l, localLocation, new Integer(1));
				d += l;
			}
			localDocumentModel.commit();
			this.average = (d / localList.size());
		} catch (Exception localException) {
		}
		return localDefaultCategoryDataset;
	}
}