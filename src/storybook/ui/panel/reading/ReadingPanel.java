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

package storybook.ui.panel.reading;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.infonode.docking.View;
import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import storybook.SbConstants;
import storybook.SbConstants.BookKey;
import storybook.SbConstants.ViewName;
import storybook.controller.BookController;
import storybook.export.BookExporter;
import storybook.model.BookModel;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Internal;
import storybook.model.hbn.entity.Part;
import storybook.toolkit.BookUtil;
import storybook.toolkit.I18N;
import storybook.toolkit.ViewUtil;
import storybook.toolkit.html.HtmlUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;
import storybook.ui.options.ReadingOptionsDialog;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class ReadingPanel extends AbstractPanel implements HyperlinkListener {

	private JTextPane tpText;
	private JScrollPane scroller;
	private StrandPanel strandPanel;

	private int scrollerWidth;
	private int fontSize;

	public ReadingPanel(MainFrame mainFrame) {
		super(mainFrame);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		Object newValue = evt.getNewValue();

		if (BookController.SceneProps.INIT.check(propName)) {
			super.refresh();
			return;
		}

		if (BookController.CommonProps.REFRESH.check(propName)) {
			View newView = (View) evt.getNewValue();
			View view = (View) getParent().getParent();
			if (view == newView) {
				// super.refresh();
				refresh();
			}
			return;
		}

		if (BookController.ChapterProps.INIT.check(propName)
				|| BookController.ChapterProps.UPDATE.check(propName)
				|| BookController.SceneProps.UPDATE.check(propName)) {
			refresh();
			return;
		}

		if (BookController.CommonProps.SHOW_OPTIONS.check(propName)) {
			View view = (View) evt.getNewValue();
			if (!view.getName().equals(ViewName.READING.toString())) {
				return;
			}
			ReadingOptionsDialog dlg = new ReadingOptionsDialog(mainFrame);
			SwingUtil.showModalDialog(dlg, this);
			return;
		}

		if (BookController.ReadingViewProps.ZOOM.check(propName)) {
			setZoomedSize((Integer) newValue);
			scroller.setMaximumSize(new Dimension(scrollerWidth, 10000));
			scroller.getParent().invalidate();
			scroller.getParent().validate();
			scroller.getParent().repaint();
			return;
		}

		if (BookController.ReadingViewProps.FONT_SIZE.check(propName)) {
			setFontSize((Integer) newValue);
			refresh();
			return;
		}

		if (BookController.PartProps.CHANGE.check(propName)) {
			ViewUtil.scrollToTop(scroller);
			// super.refresh();
			refresh();
			return;
		}


		dispatchToStrandPanels(this, evt);

		if (BookController.StrandProps.UPDATE.check(propName)) {
			refresh();
//			return;
		}
	}

	private void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	private void setZoomedSize(int zoomValue) {
		scrollerWidth = zoomValue * 10;
	}

	@Override
	public void init() {
		strandPanel = new StrandPanel(mainFrame, this);
		strandPanel.init();

		try {
			Internal internal = BookUtil.get(mainFrame,
					BookKey.READING_ZOOM, SbConstants.DEFAULT_READING_ZOOM);
			setZoomedSize(internal.getIntegerValue());
			internal = BookUtil.get(mainFrame,
					BookKey.READING_FONT_SIZE,
					SbConstants.DEFAULT_READING_FONT_SIZE);
			setFontSize(internal.getIntegerValue());
		} catch (Exception e) {
			setZoomedSize(SbConstants.DEFAULT_READING_ZOOM);
			setFontSize(SbConstants.DEFAULT_READING_FONT_SIZE);
		}
	}

	@Override
	public void initUi() {
		MigLayout layout = new MigLayout(
				"flowx",
				"[][fill,grow]", // columns
				"" // rows
				);
		setLayout(layout);

		strandPanel.initUi();

		tpText = new JTextPane();
		tpText.setEditable(false);
		tpText.setContentType("text/html");
		tpText.addHyperlinkListener(this);

		scroller = new JScrollPane(tpText);
		scroller.setMaximumSize(new Dimension(scrollerWidth, Short.MAX_VALUE));
		SwingUtil.setMaxPreferredSize(scroller);

		// layout
		add(strandPanel, "aligny top");
		add(scroller, "growy");
	}

	@Override
	public void refresh() {
		Part currentPart = mainFrame.getCurrentPart();
		boolean isUsehtmlText = BookUtil.isUseHtmlScenes(mainFrame);
		boolean expPartTitles = BookUtil.isExportPartTitles(mainFrame);

		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		List<Chapter> chapters = dao.findAllOrderByChapterNoAndSceneNo(currentPart);
		model.commit();

		StringBuilder buf = new StringBuilder();
		buf.append(HtmlUtil.getHeadWithCSS(fontSize));

		// table of contents
		buf.append("<p style='font-weight:bold'>");
		buf.append("<a name='toc'>").append(I18N.getMsg("msg.table.of.contents")).append("</a></p>\n");
		for (Chapter chapter : chapters) {
			String no = chapter.getChapternoStr();
			buf.append("<p><a href='#").append(no).append("'>");
			buf.append(no).append(": ").append(chapter.getTitle()).append("</a>");
			String descr = chapter.getDescription();
			if (descr != null) {
				if (!descr.isEmpty()) {
					buf.append(": ").append(chapter.getDescription());
				}
			}
			buf.append("</p>\n");
		}

		if(!expPartTitles && !isUsehtmlText){
			buf.append("<p></p>\n");
		}

		// content
		BookExporter exp = new BookExporter(mainFrame);
		exp.setExportOnlyCurrentPart(true);
		exp.setExportTableOfContentsLink(true);
		exp.setStrandIdsToExport(strandPanel.getStrandIds());
		buf.append(exp.getContent());
		buf.append("<p>&nbsp;</p></body></html>\n");

		final int pos = scroller.getVerticalScrollBar().getValue();
		tpText.setText(buf.toString());
		final Action restoreAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scroller.getVerticalScrollBar().setValue(pos);
			}
		};
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				restoreAction.actionPerformed(null);
			}
		});
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent evt) {
		if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				if (!evt.getDescription().isEmpty()) {
					// anchor
					tpText.scrollToReference(evt.getDescription().substring(1));
				} else {
					// external links
					tpText.setPage(evt.getURL());
				}
			} catch (IOException e) {
			}
		}
	}

	private static void dispatchToStrandPanels(Container cont,
			PropertyChangeEvent evt) {
		List<Component> ret = new ArrayList<>();
		SwingUtil.findComponentsByClass(cont, StrandPanel.class, ret);
		for (Component comp : ret) {
			StrandPanel panel = (StrandPanel) comp;
			panel.modelPropertyChange(evt);
		}
	}
}
