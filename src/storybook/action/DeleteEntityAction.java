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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JOptionPane;

import org.hibernate.Session;
import storybook.SbConstants.ViewName;
import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Part;
import storybook.toolkit.I18N;
import static storybook.toolkit.swing.SwingUtil.showModalDialog;
import storybook.ui.MainFrame;
import storybook.ui.dialog.dlgConfirmDelete;
import storybook.ui.edit.EntityEditor;

/**
 * @author martin
 *
 */
public class DeleteEntityAction extends AbstractEntityAction {

	public DeleteEntityAction(MainFrame mainFrame, AbstractEntity entity) {
		super(mainFrame, entity, I18N.getMsg("msg.common.delete"), I18N
				.getIcon("icon.small.delete"));
		this.mainFrame = mainFrame;
		this.entity = entity;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// check if read only
		List<Long> readOnlyIds = EntityUtil.getReadOnlyIds(entity);
		if (readOnlyIds.contains(entity.getId())) {
			JOptionPane.showMessageDialog(mainFrame,
					I18N.getMsg("msg.common.no.delete"),
					I18N.getMsg("msg.common.warning"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// check if an entity is loaded
		/*
		Component comp = mainFrame.getView(ViewName.EDITOR).getComponent();
		if (comp != null && comp instanceof EntityEditor) {
			EntityEditor editor = (EntityEditor) comp;
			if (editor.isEntityLoaded()) {
				System.out
						.println("DeleteEntityAction.actionPerformed(): entity:"
								+ editor.getEntity());
				mainFrame.showEditor();
				String str = EntityUtil.getEntityTitle(editor.getEntity())
						+ ": " + editor.getEntity().toString();
				JOptionPane.showMessageDialog(mainFrame,
						I18N.getMsg("msg.common.editor.delete.warning", str),
						I18N.getMsg("msg.common.warning"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}*/

		dlgConfirmDelete dlg = new dlgConfirmDelete(mainFrame, entity);
		showModalDialog(dlg, mainFrame, true);
		if (dlg.isCanceled()) {
			return;
		}
		BookController ctrl = mainFrame.getBookController();
		if (entity instanceof Part) {
			if (mainFrame.getCurrentPart().getId().equals(entity.getId())) {
				// current part will be delete, change to first part
				BookModel model = mainFrame.getBookModel();
				Session session = model.beginTransaction();
				PartDAOImpl dao = new PartDAOImpl(session);
				Part firstPart = dao.findFirst();
				model.commit();
				ChangePartAction act = new ChangePartAction("fsd",
						mainFrame.getActionController(), firstPart);
				act.actionPerformed(null);
			}
		}
		ctrl.deleteEntity(entity);
	}
}
