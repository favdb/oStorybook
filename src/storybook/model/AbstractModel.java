package storybook.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeEvent;

import org.hibernate.Session;
import storybook.SbApp;
import storybook.model.hbn.SbSessionFactory;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.ui.MainFrame;

public abstract class AbstractModel {

	protected PropertyChangeSupport propertyChangeSupport;

	protected SbSessionFactory sessionFactory;
	MainFrame mainFrame;

	public AbstractModel(MainFrame m) {
		mainFrame=m;
		propertyChangeSupport = new PropertyChangeSupport(this);
		sessionFactory = new SbSessionFactory();
	}

	public abstract void fireAgain();


	public void initSession(String dbName) {
		SbApp.trace("AbstractModel.initSession("+dbName+")");
		sessionFactory.init(dbName);
	}

	public void initDefault() {
		fireAgain();
	}

	public Session beginTransaction() {
		Session session = sessionFactory.getSession();
		session.beginTransaction();
		return session;
	}

	public Session getSession() {
		return sessionFactory.getSession();
	}

	public void commit() {
		Session session = sessionFactory.getSession();
		session.getTransaction().commit();
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		SbApp.trace("AbstractModel.addPropertyChangeListener("+l.toString()+")");
		propertyChangeSupport.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(l);
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		SbApp.trace("AbstractModel.firePropertyChange("+propertyName+","+"oldValue..."+","+"newValue..."+")");
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	public SbSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void editEntity(AbstractEntity entity) {
		mainFrame.showEditorAsDialog(entity);
	}
}
