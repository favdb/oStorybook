package storybook.ui.panel.linkspanel;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import org.hibernate.UnresolvableObjectException;
import storybook.controller.BookController;
import storybook.model.BookModel;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Scene;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.panel.AbstractPanel;
import storybook.ui.MainFrame;

@SuppressWarnings("serial")
public class LocationLinksPanel extends AbstractPanel {

	private Scene scene;
	private boolean setSize;

	public LocationLinksPanel(MainFrame mainFrame, Scene scene, boolean setSize) {
		this.mainFrame = mainFrame;
		this.scene = scene;
		this.setSize = setSize;
		refresh();
	}

	public LocationLinksPanel(MainFrame mainFrame, Scene scene) {
		this(mainFrame, scene, true);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
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

		if (BookController.LocationProps.UPDATE.check(propName)) {
			refresh();
			return;
		}
	}

	@Override
	public void init() {
	}

	@Override
	public void initUi() {
		setLayout(new MigLayout("insets 0", "grow"));
		JTextArea ta = new JTextArea();
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.setEditable(false);
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		session.refresh(scene);
		List<Location> locations = scene.getLocations();
		if (locations != null) {
			Collections.sort(locations);
		}
		int c = 0;
		for (Location location : locations) {
			try {
				session.refresh(location);
			} catch (UnresolvableObjectException e) {
				e.printStackTrace();
				continue;
			}
			ta.append(location.toString());
			if (c < locations.size() - 1) {
				ta.append(", ");
			}
			++c;
		}
		model.commit();

		if (setSize) {
			JScrollPane scroller = new JScrollPane();
			scroller.setViewportView(ta);
			scroller.setBorder(null);
			if (setSize) {
				scroller.setMinimumSize(new Dimension(100, 30));
				scroller.setPreferredSize(new Dimension(170, 30));
			}
			SwingUtil.setUnitIncrement(scroller);
			scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ta.setCaretPosition(0);
			add(scroller, "grow");
		} else {
			add(ta, "grow");
		}
	}
}
