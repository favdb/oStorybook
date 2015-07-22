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

package storybook.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import org.hibernate.Session;
import storybook.model.BookModel;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.I18N;
import storybook.toolkit.html.HtmlUtil;
import storybook.toolkit.langtool.LangToolMain;
import storybook.ui.MainFrame;

/**
 * @author martin
 *
 */
public class LangToolAction extends AbstractAction {

	private MainFrame mainFrame;
	private Scene scene;

	public LangToolAction(MainFrame mainFrame) {
		this(mainFrame, null);
	}

	public LangToolAction(MainFrame mainFrame, Scene scene) {
		super(I18N.getMsg("msg.langtool.title"), I18N
				.getIcon("icon.small.langtool"));
		this.mainFrame = mainFrame;
		this.scene = scene;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			final LangToolMain langTool = new LangToolMain(mainFrame);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						String text = "";
						if (scene != null) {
							BookModel model = mainFrame.getBookModel();
							Session session = model.beginTransaction();
							session.refresh(scene);
							text = HtmlUtil.htmlToText(scene.getText(), true);
							model.commit();
						}
						langTool.createGUI();
						langTool.showGUI();
						if (!text.isEmpty()) {
							langTool.setTextToCheck(text);
						}
					} catch (	Exception | NoClassDefFoundError e2) {
						System.err.println("LangToolAction.actionPerformed(?) Exception :"+e2.getMessage());
					}
				}
			});
		} catch (Exception e1) {
			System.err.println("LangToolAction.actionPerformed("+e.toString()+") Exception :"+e1.getMessage());
		}
	}
}
