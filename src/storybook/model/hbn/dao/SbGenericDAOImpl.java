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

package storybook.model.hbn.dao;

import java.io.Serializable;

import org.hibernate.Session;

import com.googlecode.genericdao.dao.hibernate.GenericDAOImpl;

public abstract class SbGenericDAOImpl<T, ID extends Serializable> extends GenericDAOImpl<T, ID> {

	protected Session session;

	public SbGenericDAOImpl() {
	}

	public SbGenericDAOImpl(Session session) {
		this.session = session;
		setSessionFactory(session.getSessionFactory());
	}

	public void setSession(Session session) {
		this.session = session;
		setSessionFactory(session.getSessionFactory());
	}

	public Session getSession() {
		return session;
	}
}
