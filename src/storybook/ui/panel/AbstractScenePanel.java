/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008 - 2011 Martin Mustun

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

package storybook.ui.panel;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import storybook.SbConstants.ViewName;
import storybook.action.DeleteEntityAction;
import storybook.action.EditEntityAction;
import storybook.controller.BookController;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.I18N;
import storybook.toolkit.swing.ColorUtil;
import storybook.toolkit.swing.IconButton;
import storybook.ui.MainFrame;

@SuppressWarnings("serial")
abstract public class AbstractScenePanel extends AbstractGradientPanel {
	private boolean trace=false;
	protected Scene scene;

	protected AbstractAction newAction;

	protected IconButton btNew;
	protected IconButton btEdit;
	protected IconButton btDelete;

	public AbstractScenePanel(MainFrame mainFrame, Scene scene) {
		super(mainFrame);
		this.scene = scene;
	}

	public AbstractScenePanel(MainFrame mainFrame, Scene scene,
			boolean showBgGradient, Color startBgcolor, Color endBgColor) {
		super(mainFrame, showBgGradient, scene.getInformative() ? Color.white
				: startBgcolor, scene.getInformative() ? Color.white
				: endBgColor);
		this.scene = scene;
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	protected AbstractAction getNewAction() {
		if (newAction == null) {
			newAction = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					BookController ctrl = mainFrame.getBookController();
					Scene newScene = new Scene();
					newScene.setStrand(scene.getStrand());
					newScene.setSceneTs(scene.getSceneTs());
					ctrl.setSceneToEdit(newScene);
					mainFrame.showView(ViewName.EDITOR);
				}
			};
		}
		return newAction;
	}

	protected IconButton getEditButton() {
		if (btEdit != null) {
			return btEdit;
		}
		btEdit = new IconButton("icon.small.edit", new EditEntityAction(mainFrame, scene,false));
		btEdit.setText("");
		btEdit.setSize32x20();
		btEdit.setToolTipText(I18N.getMsg("msg.common.edit"));
		return btEdit;
	}

	protected IconButton getDeleteButton() {
		if (btDelete != null) {
			return btDelete;
		}
		btDelete = new IconButton("icon.small.delete", new DeleteEntityAction(mainFrame, scene));
		btDelete.setText("");
		btDelete.setSize32x20();
		btDelete.setToolTipText(I18N.getMsg("msg.common.delete"));
		return btDelete;
	}

	protected IconButton getNewButton() {
		if (btNew != null) {
			return btNew;
		}
		btNew = new IconButton("icon.small.new", getNewAction());
		btNew.setSize32x20();
		btNew.setToolTipText(I18N.getMsg("msg.common.new"));
		return btNew;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof AbstractScenePanel)) {
			return false;
		}
		AbstractScenePanel asp = (AbstractScenePanel) other;
		if (asp.getScene() == null || scene == null) {
			return false;
		}
		return asp.getScene().getId().equals(scene.getId());
	}
}
