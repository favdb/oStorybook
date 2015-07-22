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

import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import storybook.controller.BookController;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;
import storybook.toolkit.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.toolkit.swing.label.CleverLabel;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;
import storybook.ui.panel.chrono.StrandDateLabel;
import storybook.ui.panel.linkspanel.LocationLinksPanel;
import storybook.ui.panel.linkspanel.PersonLinksPanel;
import storybook.ui.panel.linkspanel.StrandLinksPanel;

import net.miginfocom.swing.MigLayout;
import storybook.ui.panel.linkspanel.ItemLinksPanel;

/**
 * @author martin
 *
 */
@SuppressWarnings("serial")
public class BookInfoPanel extends AbstractPanel {

	private Scene scene;
	private CleverLabel lbStrand;
	private PersonLinksPanel personLinksPanel;
	private ItemLinksPanel itemLinksPanel;
	private LocationLinksPanel locationLinksPanel;

	private StrandLinksPanel strandLinksPanel;
	private StrandDateLabel lbDate;

	public BookInfoPanel(MainFrame mainFrame, Scene scene) {
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

		if (BookController.StrandProps.UPDATE.check(propName)) {
			Strand newStrand = (Strand) newValue;
			if (newStrand.getId().equals(scene.getStrand().getId())) {
				lbStrand.setText(newStrand.toString());
				lbStrand.setBackground(newStrand.getJColor());
			}
			strandLinksPanel.refresh();
			return;
		}

		if (BookController.SceneProps.UPDATE.check(propName)) {
			Scene newScene = (Scene) newValue;
			if (!newScene.getId().equals(scene.getId())) {
				return;
			}
			lbDate.setDate(newScene.getSceneTs());
			lbDate.refresh();
			lbStrand.setText(newScene.getStrand().toString());
			lbStrand.setBackground(newScene.getStrand().getJColor());
			personLinksPanel.refresh();
			itemLinksPanel.refresh();
			locationLinksPanel.refresh();
			strandLinksPanel.refresh();
			return;
		}
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		LayoutManager layout = new MigLayout(
				"fillx,wrap,gapy 15",
				"",
				"");
		setLayout(layout);
		setOpaque(false);
		setBorder(SwingUtil.getBorderDefault());

		Strand strand = scene.getStrand();

		// strand
		lbStrand = new CleverLabel(strand.toString(), JLabel.CENTER);
		lbStrand.setBackground(strand.getJColor());

		// date
		lbDate = new StrandDateLabel(strand, scene.getSceneTs());
		lbDate.setOpaque(false);

		// person links
		personLinksPanel = new PersonLinksPanel(mainFrame, scene, true);

		// location links
		locationLinksPanel = new LocationLinksPanel(mainFrame, scene, false);

		// item links
		itemLinksPanel = new ItemLinksPanel(mainFrame, scene, false);

		// strand links
		JLabel lbStrandLinks = new JLabel(I18N.getMsgColon("msg.dlg.scene.strand.links"));
		strandLinksPanel = new StrandLinksPanel(mainFrame, scene, false);

		// layout
		add(lbStrand, "growx");
		add(lbDate, "");
		Icon personIcon = (ImageIcon) I18N.getIcon("icon.small.person");
		add(new JLabel(personIcon), "aligny top,split 2");
		add(personLinksPanel);
		Icon itemIcon = (ImageIcon) I18N.getIcon("icon.small.item");
		add(new JLabel(itemIcon), "aligny top,split 2");
		add(itemLinksPanel);
		Icon icon = (ImageIcon) I18N.getIcon("icon.small.location");
		add(new JLabel(icon), "aligny top,split 2");
		add(locationLinksPanel, "growx");
		add(lbStrandLinks, "split 2");
		add(strandLinksPanel);
	}
}
