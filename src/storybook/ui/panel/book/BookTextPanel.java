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

package storybook.ui.panel.book;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.JTextComponent;

import storybook.SbConstants;
import storybook.SbConstants.BookKey;
import storybook.action.EditEntityAction;
import storybook.controller.BookController;
import storybook.model.hbn.entity.Internal;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.BookUtil;
import storybook.toolkit.I18N;
import storybook.toolkit.swing.FontUtil;
import storybook.toolkit.swing.SwingUtil;
import storybook.toolkit.swing.undo.UndoableTextArea;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;
import storybook.ui.label.SceneStateLabel;

import net.miginfocom.swing.MigLayout;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class BookTextPanel extends AbstractPanel implements FocusListener {

	private final static String CN_TITLE = "taTitle";
	private final static String CN_TEXT = "tcText";

	private Scene scene;
	private JLabel lbSceneNo;
	private SceneStateLabel lbStatus;
	private UndoableTextArea taTitle;
	private JTextComponent tcText;

	private int size;
	private int heightFactor;

	public BookTextPanel(MainFrame mainFrame, Scene scene) {
		super(mainFrame);
		this.scene = scene;
		init();
		initUi();
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		// Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();
		String propName = evt.getPropertyName();

		if (BookController.SceneProps.UPDATE.check(propName)) {
			Scene newScene = (Scene) newValue;
			if (!newScene.getId().equals(scene.getId())) {
				return;
			}
			lbSceneNo.setText(scene.getChapterSceneNo(false));
			lbStatus.setState(newScene.getSceneState());
			lbStatus.refresh();
			taTitle.setText(newScene.getTitle());
			taTitle.setCaretPosition(0);
			tcText.setText(newScene.getText());
			tcText.setCaretPosition(0);
			return;
		}

		if (BookController.BookViewProps.ZOOM.check(propName)) {
			setZoomedSize((Integer) newValue);
			refresh();
		} else if (BookController.BookViewProps.HEIGHT_FACTOR.check(propName)) {
			heightFactor = (Integer) newValue;
			refresh();
		}
	}

	private void setZoomedSize(int zoomValue) {
		size = zoomValue * 12;
	}

	@Override
	public void init() {
		try {
			Internal internal = BookUtil.get(mainFrame,
					BookKey.BOOK_ZOOM, SbConstants.DEFAULT_BOOK_ZOOM);
			setZoomedSize(internal.getIntegerValue());
			internal = BookUtil.get(mainFrame,
					BookKey.BOOK_HEIGHT_FACTOR,
					SbConstants.DEFAULT_BOOK_HEIGHT_FACTOR);
			heightFactor = internal.getIntegerValue();
		} catch (Exception e) {
			setZoomedSize(SbConstants.DEFAULT_BOOK_ZOOM);
			heightFactor = SbConstants.DEFAULT_BOOK_HEIGHT_FACTOR;
		}
	}

	@Override
	public void initUi() {
		refresh();
	}

	@Override
	public void refresh() {
		MigLayout layout = new MigLayout(
				"wrap,fill",
				"",
				"[][grow][grow]"
				);
		setLayout(layout);
		setBorder(SwingUtil.getBorderDefault());

		removeAll();

		// scene number
		lbSceneNo = new JLabel(scene.getChapterSceneNo(false));
		lbSceneNo.setFont(FontUtil.getBoldFont());

		// informational
		JLabel lbInformational = new JLabel("");
		if (scene.getInformative()) {
			lbInformational.setIcon(I18N.getIcon("icon.small.info"));
			lbInformational.setText(I18N.getMsg("msg.common.informative"));
		}

		// scene status
		lbStatus = new SceneStateLabel(scene.getSceneState(), false);

		// title
		taTitle = new UndoableTextArea();
		taTitle.setName(CN_TITLE);
		taTitle.setLineWrap(true);
		taTitle.setWrapStyleWord(true);
		taTitle.setText(scene.getTitle());
		taTitle.setCaretPosition(0);
		taTitle.setDragEnabled(true);
		taTitle.getUndoManager().discardAllEdits();
		JScrollPane titleScroller = new JScrollPane(taTitle);
		titleScroller.setPreferredSize(new Dimension(size, 40));
		taTitle.addFocusListener(this);
		SwingUtil.addCtrlEnterAction(taTitle, new EditEntityAction(mainFrame,
				scene,true));

		// text
		tcText = SwingUtil.createTextComponent(mainFrame);
		tcText.setName(CN_TEXT);
		tcText.setText(scene.getText());
		tcText.setCaretPosition(0);
		tcText.setDragEnabled(true);
		JScrollPane textScroller = new JScrollPane(tcText);
		textScroller.setPreferredSize(new Dimension(size, calculateHeight()));
		textScroller.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tcText.addFocusListener(this);
		SwingUtil.addCtrlEnterAction(tcText, new EditEntityAction(mainFrame,
				scene,true));

		add(lbSceneNo, "split 3,growx");
		add(lbInformational, "growx,al center");
		add(lbStatus);
		add(titleScroller, "grow");
		add(textScroller, "grow");

		revalidate();
		repaint();

		taTitle.setCaretPosition(0);
		tcText.setCaretPosition(0);
	}

	private int calculateHeight() {
		return (int) (100 + size * 0.4) * heightFactor / 10;
	}

	@Override
	public void focusGained(FocusEvent evt) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() instanceof JTextComponent) {
			JTextComponent tc = (JTextComponent) e.getSource();
			switch (tc.getName()) {
				case CN_TITLE:
					scene.setTitle(tc.getText());
					break;
				case CN_TEXT:
					scene.setSummary(tc.getText());
					break;
			}
			mainFrame.getBookController().updateScene(scene);
		}
	}
}
