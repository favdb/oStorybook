/*
 * SbApp: Open Source software for novelists and authors.
 * Original idea 2008 - 2012 Martin Mustun
 * Copyrigth (C) Favdb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package storybook.ui.memoria;

import storybook.model.EntityUtil;
import storybook.model.hbn.entity.AbstractEntity;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import javax.swing.JPopupMenu;

public class MemoriaGraphMouse extends AbstractPopupGraphMousePlugin
	implements MouseListener {

	public static final String ACTION_KEY_DB_OBECT = "DbObject";
	private MemoriaPanel parent;

	public MemoriaGraphMouse(MemoriaPanel parent) {
		this.parent = parent;
	}

	public MemoriaGraphMouse() {
		this(4);
	}

	public MemoriaGraphMouse(int i) {
		super(i);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void handlePopup(MouseEvent evt) {
		VisualizationViewer vv = (VisualizationViewer) evt.getSource();
		Point point = evt.getPoint();
		GraphElementAccessor accel = vv.getPickSupport();
		if (accel != null) {
			AbstractEntity entity = (AbstractEntity) accel.getVertex(vv.getGraphLayout(), point.getX(), point.getY());
			if (entity != null) {
				JPopupMenu localJPopupMenu = EntityUtil.createPopupMenu(this.parent.getMainFrame(), entity);
				localJPopupMenu.show(vv, evt.getX(), evt.getY());
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void mouseClicked(MouseEvent paramMouseEvent) {
		if (paramMouseEvent.getClickCount() == 2) {
			VisualizationViewer localVisualizationViewer = (VisualizationViewer) paramMouseEvent.getSource();
			Point localPoint = paramMouseEvent.getPoint();
			GraphElementAccessor localGraphElementAccessor = localVisualizationViewer.getPickSupport();
			if (localGraphElementAccessor != null) {
				AbstractEntity localAbstractEntity = (AbstractEntity) localGraphElementAccessor.getVertex(localVisualizationViewer.getGraphLayout(), localPoint.getX(), localPoint.getY());
				if (localAbstractEntity != null) {
					this.parent.refresh(localAbstractEntity);
				}
			}
		}
		super.mouseClicked(paramMouseEvent);
	}

	public MemoriaPanel getParent() {
		return this.parent;
	}
}