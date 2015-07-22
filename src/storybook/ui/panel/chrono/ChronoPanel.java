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
package storybook.ui.panel.chrono;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseWheelListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.infonode.docking.View;
import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import storybook.SbConstants;
import storybook.SbConstants.BookKey;
import storybook.SbConstants.ViewName;
import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.dao.StrandDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Internal;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;
import storybook.toolkit.DateUtil;
import storybook.toolkit.BookUtil;
import storybook.toolkit.I18N;
import storybook.toolkit.ViewUtil;
import storybook.toolkit.swing.ColorUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.toolkit.swing.label.VerticalLabelUI;
import storybook.ui.panel.AbstractScrollPanel;
import storybook.ui.MainFrame;
import storybook.ui.SbView;
import storybook.ui.panel.linkspanel.LocationLinksPanel;
import storybook.ui.panel.linkspanel.PersonLinksPanel;
import storybook.ui.panel.linkspanel.StrandLinksPanel;
import storybook.ui.options.ChronoOptionsDialog;
import storybook.ui.panel.linkspanel.ItemLinksPanel;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class ChronoPanel extends AbstractScrollPanel implements Printable, MouseWheelListener {

	private static final String CLIENT_PROPERTY_STRAND_ID = "strand_id";
	private boolean layoutDirection;
	private boolean showDateDiff;
	private List<JLabel> strandLabels;

	public ChronoPanel(MainFrame mainFrame) {
		// don't call super constructor here!
		this.mainFrame = mainFrame;
		strandLabels = new ArrayList<JLabel>();
	}

	@Override
	protected void setZoomValue(int val) {
		BookUtil.store(mainFrame, BookKey.CHRONO_ZOOM, val);
		mainFrame.getBookController().chronoSetZoom(val);
	}

	@Override
	protected int getZoomValue() {
		Internal internal = BookUtil.get(mainFrame, BookKey.CHRONO_ZOOM, SbConstants.DEFAULT_CHRONO_ZOOM);
		return internal.getIntegerValue();
	}

	@Override
	protected int getMinZoomValue() {
		return SbConstants.MIN_CHRONO_ZOOM;
	}

	@Override
	protected int getMaxZoomValue() {
		return SbConstants.MAX_CHRONO_ZOOM;
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();
		String propName = evt.getPropertyName();

		if (BookController.SceneProps.INIT.check(propName)) {
			refresh();
			return;
		}

		if (BookController.CommonProps.REFRESH.check(propName)) {
			SbView newView = (SbView) newValue;
			SbView view = (SbView) getParent().getParent();
			if (view == newView)
				refresh();
			return;
		}

		if (BookController.CommonProps.PRINT.check(propName)) {
			View newView = (View) newValue;
			View view = (View) getParent().getParent();
			if (view == newView) {
				// PrintUtil.printComponent(this);
				PrinterJob pj = PrinterJob.getPrinterJob();
				pj.setPrintable(this);
				pj.printDialog();
				try {
					pj.print();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return;
		}

		if (BookController.ChronoViewProps.LAYOUT_DIRECTION.check(propName)) {
			layoutDirection = (Boolean) evt.getNewValue();
			refresh();
			return;
		}

		if (BookController.ChronoViewProps.SHOW_DATE_DIFFERENCE
				.check(propName)) {
			showDateDiff = (Boolean) evt.getNewValue();
			refresh();
			return;
		}

		if (BookController.CommonProps.SHOW_OPTIONS.check(propName)) {
			View view = (View) evt.getNewValue();
			if (!view.getName().equals(ViewName.CHRONO.toString()))
				return;
			ChronoOptionsDialog dlg = new ChronoOptionsDialog(mainFrame);
			SwingUtil.showModalDialog(dlg, this);
			return;
		}

		if (BookController.ChronoViewProps.SHOW_ENTITY.check(propName)) {
			if (newValue instanceof Scene) {
				Scene scene = (Scene) newValue;
				ViewUtil.scrollToScene(this, panel, scene);
				return;
			}
			if (newValue instanceof Chapter) {
				Chapter chapter = (Chapter) newValue;
				ViewUtil.scrollToChapter(this, panel, chapter);
				return;
			}
		}

// deleted strand, strand order changed
		if (BookController.StrandProps.DELETE.check(propName)
				|| BookController.StrandProps.ORDER_DOWN.check(propName)
				|| BookController.StrandProps.ORDER_UP.check(propName)) {
			refresh();
			return;
		}

// new scene, deleted scene
		if (BookController.SceneProps.NEW.check(propName)
				|| BookController.SceneProps.DELETE.check(propName)) {
			refresh();
			return;
		}

		if (BookController.PartProps.CHANGE.check(propName)
				|| BookController.PartProps.DELETE.check(propName)) {
			ViewUtil.scrollToTop(scroller);
			refresh();
			return;
		}

		dispatchToChronoScenePanels(this, evt);
		dispatchToStrandLinksPanels(this, evt);
		dispatchToPersonLinksPanels(this, evt);
		dispatchToLocationLinksPanels(this, evt);
		dispatchToItemLinksPanels(this, evt);
		dispatchToSpacePanels(this, evt);

		if (BookController.StrandProps.UPDATE.check(propName)) {
			for (JLabel lb : strandLabels) {
				long id = (Long) lb.getClientProperty(CLIENT_PROPERTY_STRAND_ID);
				Strand strand = (Strand) EntityUtil.get(mainFrame, Strand.class, id);
				lb.setBackground(strand.getJColor());
			}
			return;
		}

// edit scene
		if (BookController.SceneProps.EDIT.check(propName)) {
			// TODO handle text fields if same scene is edited in editor
			return;
		}

// scene update
		if (BookController.SceneProps.UPDATE.check(propName)) {
			if (oldValue == null)
				return;
			Scene newScene = (Scene) newValue;
			Scene oldScene = (Scene) oldValue;
			// strand changed
			if (!newScene.getStrand().getId().equals(oldScene.getStrand().getId())) {
				refresh();
				return;
			}
			// "informative" changed
			if (!newScene.getInformative().equals(oldScene.getInformative())) {
				refresh();
				return;
			}
			// date changed
			if (newScene.hasSceneTs() && oldScene.hasNoSceneTs()) {
				refresh();
				return;
			}
			if (oldScene.hasSceneTs() && newScene.hasNoSceneTs()) {
				refresh();
				return;
			}
			if (newScene.hasSceneTs() && oldScene.hasSceneTs()) {
				Date oldDate = oldScene.getDate();
				Date newDate = newScene.getDate();
				if (!oldDate.equals(newDate)) {
					refresh();
					return;
				}
				return;
			}
		}
	}

	@Override
	public void init() {
		try {
			Internal internal = BookUtil.get(mainFrame, BookKey.CHRONO_LAYOUT_DIRECTION, SbConstants.DEFAULT_CHRONO_LAYOUT_DIRECTION);
			layoutDirection = internal.getBooleanValue();
			internal = BookUtil.get(mainFrame, BookKey.CHRONO_SHOW_DATE_DIFFERENCE, SbConstants.DEFAULT_CHRONO_SHOW_DATE_DIFFERENCE);
			showDateDiff = internal.getBooleanValue();
		} catch (Exception e) {
			layoutDirection = SbConstants.DEFAULT_CHRONO_LAYOUT_DIRECTION;
			showDateDiff = SbConstants.DEFAULT_CHRONO_SHOW_DATE_DIFFERENCE;
		}
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("flowy, ins 0"));

		MigLayout layout = new MigLayout("", "", "[top]");
		panel = new JPanel(layout);
		panel.setBackground(SwingUtil.getBackgroundColor());
		scroller = new JScrollPane(panel);
		SwingUtil.setUnitIncrement(scroller);
		SwingUtil.setMaxPreferredSize(scroller);
		add(scroller, "grow");

		refresh();

		registerKeyboardAction();
		panel.addMouseWheelListener(this);
	}

	@Override
	public void refresh() {
		// must be done before the session is opened
		Part currentPart = mainFrame.getCurrentPart();

		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();

		SceneDAOImpl sceneDao = new SceneDAOImpl(session);
		List<Date> dates = sceneDao.findDistinctDates(currentPart);
		DateUtil.expandDatesToFuture(dates);
		StrandDAOImpl strandDao = new StrandDAOImpl(session);
		List<Strand> strands = strandDao.findAllOrderBySort();
		model.commit();

		panel.removeAll();
		if (dates.isEmpty()) {
			panel.add(new JLabel(I18N.getMsg("msg.warning.no.scenes")));
			panel.revalidate();
			panel.repaint();
			return;
		}

		if (layoutDirection == true) {
			// vertical layout
			Date lastDate = null;
			for (Date date : dates) {
				int i = 0;
				if (showDateDiff && lastDate != null)
					for (int j = 0; j < strands.size(); ++j) {
						DateDiffLabel lbDiff = new DateDiffLabel(lastDate, date);
						if (lbDiff.getDays() > 1) {
							String wrap = "";
							if (j == strands.size() - 1)
								wrap = "wrap,";
							panel.add(lbDiff, wrap + "growx,al center");
						}
					}
				for (Strand strand : strands) {
					JLabel lbStrand = new JLabel(" " + strand.toString() + " ");
					lbStrand.setMinimumSize(new Dimension(20, lbStrand
							.getHeight()));
					lbStrand.setBackground(ColorUtil.getPastel(strand
							.getJColor()));
					lbStrand.setOpaque(true);
					lbStrand.putClientProperty(CLIENT_PROPERTY_STRAND_ID,
							strand.getId());
					strandLabels.add(lbStrand);
					String wrap = "";
					if (i == strands.size() - 1)
						wrap = "wrap,";
					panel.add(lbStrand, wrap + "grow");
					++i;
				}
				i = 0;
				for (Strand strand : strands) {
					String wrap = "";
					if (i == strands.size() - 1)
						wrap = "wrap";
					ColumnPanel colPanel = new ColumnPanel(mainFrame, strand, date);
					panel.add(colPanel, wrap + ",grow");
					++i;
				}
				lastDate = date;
			}
		} else
			for (Strand strand : strands) {
				int i = 0;
				Date lastDate = null;
				for (Date date : dates) {
					if (showDateDiff && lastDate != null) {
						DateDiffLabel lbDiff = new DateDiffLabel(lastDate, date, true);
						lbDiff.setUI(new VerticalLabelUI(false));
						if (lbDiff.getDays() > 1)
							panel.add(lbDiff, "growy");
					}

					JLabel lbStrand = new JLabel(" " + strand.toString() + " ");
					lbStrand.setMinimumSize(new Dimension(20, lbStrand.getHeight()));
					lbStrand.setUI(new VerticalLabelUI(false));
					lbStrand.setBackground(ColorUtil.getPastel(strand.getJColor()));
					lbStrand.setOpaque(true);
					lbStrand.putClientProperty(CLIENT_PROPERTY_STRAND_ID, strand.getId());
					strandLabels.add(lbStrand);
					panel.add(lbStrand, "grow");
					String wrap = "";
					if (i == dates.size() - 1)
						wrap = ",wrap";
					RowPanel rowPanel = new RowPanel(mainFrame, strand, date);
					panel.add(rowPanel, "grow" + wrap);
					++i;

					lastDate = date;
				}
			}
		panel.revalidate();
		revalidate();
		repaint();
	}

	@Override
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);

		int fontHeight = g2.getFontMetrics().getHeight();
		int fontDesent = g2.getFontMetrics().getDescent();

		double pageHeight = pageFormat.getImageableHeight();
		double pageWidth = pageFormat.getImageableWidth();

		g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		// bottom center
		g2.drawString("Page: " + (pageIndex + 1), (int) pageWidth / 2 - 35, (int) (pageHeight + fontHeight - fontDesent));
		this.paint(g2);

		if (pageIndex < 4)
			return Printable.PAGE_EXISTS;
		return Printable.NO_SUCH_PAGE;
	}

	private static void dispatchToChronoScenePanels(Container cont, PropertyChangeEvent evt) {
		List<Component> ret = new ArrayList<Component>();
		SwingUtil.findComponentsByClass(cont, ChronoScenePanel.class, ret);
		for (Component comp : ret) {
			ChronoScenePanel panel = (ChronoScenePanel) comp;
			panel.modelPropertyChange(evt);
		}
	}

	private static void dispatchToPersonLinksPanels(Container cont, PropertyChangeEvent evt) {
		List<Component> ret = new ArrayList<Component>();
		SwingUtil.findComponentsByClass(cont, PersonLinksPanel.class, ret);
		for (Component comp : ret) {
			PersonLinksPanel panel = (PersonLinksPanel) comp;
			panel.modelPropertyChange(evt);
		}
	}

	private static void dispatchToLocationLinksPanels(Container cont, PropertyChangeEvent evt) {
		List<Component> ret = new ArrayList<Component>();
		SwingUtil.findComponentsByClass(cont, LocationLinksPanel.class, ret);
		for (Component comp : ret) {
			LocationLinksPanel panel = (LocationLinksPanel) comp;
			panel.modelPropertyChange(evt);
		}
	}

	private static void dispatchToItemLinksPanels(Container cont, PropertyChangeEvent evt) {
		List<Component> ret = new ArrayList<Component>();
		SwingUtil.findComponentsByClass(cont, ItemLinksPanel.class, ret);
		for (Component comp : ret) {
			ItemLinksPanel panel = (ItemLinksPanel) comp;
			panel.modelPropertyChange(evt);
		}
	}

	private static void dispatchToStrandLinksPanels(Container cont, PropertyChangeEvent evt) {
		List<Component> ret = new ArrayList<Component>();
		SwingUtil.findComponentsByClass(cont, StrandLinksPanel.class, ret);
		for (Component comp : ret) {
			StrandLinksPanel panel = (StrandLinksPanel) comp;
			panel.modelPropertyChange(evt);
		}
	}

	private static void dispatchToSpacePanels(Container cont, PropertyChangeEvent evt) {
		List<Component> ret = new ArrayList<Component>();
		SwingUtil.findComponentsByClass(cont, SpacePanel.class, ret);
		for (Component comp : ret) {
			SpacePanel panel = (SpacePanel) comp;
			panel.modelPropertyChange(evt);
		}
	}

	public JPanel getPanel() {
		return panel;
	}
}
