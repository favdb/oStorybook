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
package storybook.controller;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JMenuBar;

import storybook.SbApp;
import storybook.model.AbstractModel;
import storybook.model.EntityUtil;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.ui.MainFrame;
import storybook.ui.panel.AbstractPanel;

public abstract class AbstractController implements PropertyChangeListener {

	private final List<Component> attachedViews;
	private final List<AbstractModel> attachedModels;

	public AbstractController() {
		SbApp.trace("new AbstractController()");
		attachedViews = new CopyOnWriteArrayList<>();
		attachedModels = new ArrayList<>();
	}

	public void attachModel(AbstractModel model) {
		SbApp.trace("AbstractController.attachModel("+model.toString()+")");
		attachedModels.add(model);
		model.addPropertyChangeListener(this);
		printAttachedModels();
	}

	public void detachModel(AbstractModel model) {
		SbApp.trace("AbstractController.detachModel("+model.toString()+")");
		attachedModels.remove(model);
		model.removePropertyChangeListener(this);
		printAttachedModels();
	}

	public void attachView(Component view) {
		SbApp.trace("AbstractController.attachView(" + view.toString()+")");
		synchronized (attachedViews) {
			attachedViews.add(view);
		}
		if (SbApp.getTrace()) {
			printNumberOfAttachedViews();
			printAttachedViews();
		}
	}

	public void detachView(Component view) {
		SbApp.trace("AbstractController.detachView("+view.getName()+")");
		synchronized (attachedViews) {
			attachedViews.remove(view);
		}
		printAttachedViews();
	}

	public void printNumberOfAttachedViews() {
		SbApp.trace("AbstractController.printNumberOfAttachedViews():" + " attached views size: " + attachedViews.size());
	}

	public void printAttachedViews() {
		SbApp.trace("AbstractController.printAttachedViews()");
		synchronized(attachedViews) {
			SbApp.trace(getInfoAttachedViews());
		}
	}

	public String getInfoAttachedViews() {
		return getInfoAttachedViews(false);
	}

	public String getInfoAttachedViews(boolean html) {
		StringBuilder buf = new StringBuilder();
		int i = 0;
		int size = attachedViews.size();
		for (Component view : attachedViews) {
			buf.append("attached view ")
				.append(i).append("/")
				.append(size).append(": ")
				.append(view.getClass().getSimpleName());
			if (html) {
				buf.append("\n<br>");
			} else {
				buf.append("\n");
			}
			++i;
		}
		return buf.toString();
	}

	public void printAttachedModels() {
		if (SbApp.getTrace()) {
			SbApp.trace("AbstractController.printAttachedModels()");
			for (AbstractModel model : attachedModels) {
				SbApp.trace("Attached model ->" + model.toString());
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		SbApp.trace("AbstractController.propertyChange("+evt.toString()+")");
		synchronized (attachedViews) {
			// must be in synchronized block
			for (Component comp : attachedViews) {
				if (comp instanceof AbstractPanel) {
					SbApp.trace("---> AbstractPanel:"+((AbstractPanel) comp).toString());
					((AbstractPanel) comp).modelPropertyChange(evt);
				}
				else if (comp instanceof JMenuBar) {
					SbApp.trace("---> JMenuBar");
					JMenuBar mb = (JMenuBar) comp;
					PropertyChangeListener[] pcls = mb.getPropertyChangeListeners();
					for (PropertyChangeListener pcl : pcls) {
						pcl.propertyChange(evt);
					}
				}
				else if (comp instanceof SbApp) {
					SbApp.trace("---> SbApp");
					((SbApp) comp).modelPropertyChange(evt);
				}
			}
		}
	}

	public synchronized void fireAgain() {
		SbApp.trace("AbstractController.fireAgain()");
		for (AbstractModel model : attachedModels) {
			model.fireAgain();
		}
	}

	protected synchronized void setModelProperty(String propertyName,
			Object newValue) {
		if (newValue != null) {
			SbApp.trace("AbstractControler.setModelProperty(" + propertyName
					+ "," + newValue.toString() + ")");
			for (AbstractModel model : attachedModels) {
				Method method = null;
				Class<?>[] classes = null;
				try {
					if (newValue instanceof AbstractEntity) {
						classes = new Class[] { EntityUtil
								.getEntityClass((AbstractEntity) newValue) };
					} else if (newValue != null) {
						classes = new Class[] { newValue.getClass() };
					}
					method = model.getClass().getMethod("set" + propertyName,
							classes);
					if (newValue != null) {
						method.invoke(model, newValue);
					} else {
						method.invoke(model);
					}
				} catch (NoSuchMethodException | SecurityException
						| IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					System.err
							.println("*** : AbstractController.setModelProperty()"
									+ "\nmsg="
									+ e.getCause().toString()
									+ "\nmethod:"
									+ method.getName()
									+ "\nclasses:"
									+ classes.getClass().toString());
				}
			}
		}
	}

	protected synchronized void setModelProperty(String propertyName) {
		SbApp.trace("AbstractController.setModelProperty(" + propertyName + ")");
		for (AbstractModel model : attachedModels) {
			try {
				Method method = model.getClass().getMethod("set" + propertyName);
				method.invoke(model);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException ex) {
			}
		}
	}

	public int getNumberOfAttachedViews() {
		return attachedViews.size();
	}

	public ArrayList<Component> getAttachedViews() {
		return (ArrayList<Component>) attachedViews;
	}

	public List<AbstractModel> getAttachedModels() {
		return attachedModels;
	}
}
