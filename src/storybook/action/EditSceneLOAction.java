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
/* vérification OK */

package storybook.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.swing.JOptionPane;

import org.jopendocument.dom.OOUtils;

import storybook.SbApp;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.BookUtil;
import storybook.toolkit.I18N;
import storybook.toolkit.odt.ODTUtils;
import storybook.ui.MainFrame;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class EditSceneLOAction extends AbstractEntityAction {

	public EditSceneLOAction(MainFrame mainFrame, AbstractEntity entity) {
		super(mainFrame, entity, I18N.getMsg("msg.common.editlo"), I18N
				.getIcon("icon.small.edit"));
		SbApp.trace("EditSceneLOAction(" + mainFrame.getName() + ","
				+ entity.toString() + ")");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SbApp.trace("EditSceneLOAction.actionPerformed(...) entity="
				+ entity.toString());
		String name = ODTUtils.getFilePath(mainFrame, (Scene) entity);
		File file = new File(name);
		if (!file.exists()) {
			if (JOptionPane.showConfirmDialog(null,
					I18N.getMsg("msg.libreoffice.filenotexist"),
					I18N.getMsg("msg.libreoffice.lauching"),
					JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				try {
					String source = "storybook/resources/Empty.odt";
					if (BookUtil.isUseSimpleTemplate(mainFrame)) {
						source = "storybook/resources/Simple.odt";
					}
					InputStream is = this.getClass().getClassLoader()
							.getResourceAsStream(source);
					Files.copy(is, file.toPath());
				} catch (IOException ex) {
					SbApp.error("EntityEditor.createOdtFile(...)", ex);
				}
				((Scene) entity).setOdf(file.getName());
			}
		}
		try {
			OOUtils.open(file);
		} catch (IOException ex) {
			SbApp.error("execLibreOffice(mainFrame,...)", ex);
		}
	}
}
