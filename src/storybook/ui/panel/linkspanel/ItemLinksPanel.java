package storybook.ui.panel.linkspanel;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import org.hibernate.UnresolvableObjectException;
import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.swing.label.CleverLabel;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;

@SuppressWarnings("serial")
public class ItemLinksPanel extends AbstractPanel {

	private Scene scene;
	private boolean vertical = false;

	public ItemLinksPanel(MainFrame mainFrame, Scene scene) {
		this(mainFrame, scene, false);
	}

	public ItemLinksPanel(MainFrame mainFrame, Scene scene, boolean vertical) {
		this.mainFrame = mainFrame;
		this.scene = scene;
		this.vertical = vertical;
		refresh();
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		// Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();
		String propName = evt.getPropertyName();

		if (BookController.SceneProps.UPDATE.check(propName)) {
			if (!((Scene) newValue).getId().equals(scene.getId())) {
				// not this scene
				return;
			}
			refresh();
			return;
		}

		if (BookController.PersonProps.UPDATE.check(propName)) {
			refresh();
			return;
		}
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		if (vertical) {
			setLayout(new MigLayout("wrap 2,insets 0"));
		} else {
			setLayout(new MigLayout("flowx,insets 0"));
			setMaximumSize(new Dimension(170, 50));
		}
		setOpaque(false);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		session.refresh(scene);
		List<Item> list = scene.getItems();
		if (list != null) {
			Collections.sort(list);
		}
		for (Item item : list) {
			try {
				session.refresh(item);
			} catch (UnresolvableObjectException e) {
				e.printStackTrace();
				continue;
			}
			//Color color = item.getJColor();
			JLabel lbName = new JLabel(item.getName());
			CleverLabel lbAbbr = new CleverLabel(item.getAbbr());
			lbAbbr.setToolTipText(EntityUtil.getToolTip(item, scene.getDate()));
			//if (color != null) {
			//	lbAbbr.setBackground(color);
			//} else {
				lbAbbr.setOpaque(false);
			//}
			if (vertical) {
				add(lbAbbr);
				add(lbName);
			} else {
				add(lbAbbr, "gap 0");
			}
		}
		model.commit();
	}

	public boolean isVertical() {
		return vertical;
	}

	public Scene getScene() {
		return scene;
	}
}
