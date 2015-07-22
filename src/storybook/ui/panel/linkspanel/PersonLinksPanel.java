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
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.swing.label.CleverLabel;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;

@SuppressWarnings("serial")
public class PersonLinksPanel extends AbstractPanel {

	private Scene scene;
	private boolean vertical = false;

	public PersonLinksPanel(MainFrame mainFrame, Scene scene) {
		this(mainFrame, scene, false);
	}

	public PersonLinksPanel(MainFrame mainFrame, Scene scene, boolean vertical) {
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
		List<Person> list = scene.getPersons();
		if (list != null) {
			Collections.sort(list);
		}
		for (Person person : list) {
			try {
				session.refresh(person);
			} catch (UnresolvableObjectException e) {
				e.printStackTrace();
				continue;
			}
			Color color = person.getJColor();
			JLabel lbName = new JLabel(person.getFullName());
			CleverLabel lbAbbr = new CleverLabel(person.getAbbreviation());
			lbAbbr.setToolTipText(EntityUtil.getToolTip(person, scene.getDate()));
			if (color != null) {
				lbAbbr.setBackground(color);
			} else {
				lbAbbr.setOpaque(false);
			}
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
