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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import storybook.model.hbn.entity.Internal;

public class InternalDAOImpl extends SbGenericDAOImpl<Internal, Long> implements
		InternalDAO {

	public InternalDAOImpl() {
		super();
	}

	public InternalDAOImpl(Session session) {
		super(session);
	}

	public Internal findByKey(String key) {
		// Filter filter = new Filter("key", key, Filter.OP_EQUAL);
		// Search search = new Search();
		// search.addFilter(filter);
		// return searchUnique(search);
		Criteria crit = session.createCriteria(Internal.class);
		crit.add(Restrictions.eq("key", key));
		return (Internal) crit.uniqueResult();
	}

	public void saveOrUpdate(String key, Object val) {
		if (val instanceof String) {
			saveOrUpdate(key, (String) val);
			return;
		}
		if (val instanceof Integer) {
			saveOrUpdate(key, (Integer) val);
			return;
		}
		if (val instanceof Boolean) {
			saveOrUpdate(key, (Boolean) val);
			return;
		}
		if (val instanceof byte[]) {
			saveOrUpdate(key, (byte[]) val);
			return;
		}
	}

	public void saveOrUpdate(String key, Boolean value) {
		Internal internal = findByKey(key);
		if (internal == null) {
			internal = new Internal(key, value);
			session.save(internal);
		} else {
			internal.setBooleanValue(value);
			session.update(internal);
		}
	}

	public void saveOrUpdate(String key, Integer value) {
		Internal internal = findByKey(key);
		if (internal == null) {
			internal = new Internal(key, value);
			session.save(internal);
		} else {
			internal.setIntegerValue(value);
			session.update(internal);
		}
	}

	public void saveOrUpdate(String key, String value) {
		Internal internal = findByKey(key);
		if (internal == null) {
			internal = new Internal(key, value);
			session.save(internal);
		} else {
			internal.setStringValue(value);
			session.update(internal);
		}
	}

	public void saveOrUpdate(String key, byte[] value) {
		Internal internal = findByKey(key);
		if (internal == null) {
			internal = new Internal(key, value);
			session.save(internal);
		} else {
			internal.setBinValue(value);
			session.update(internal);
		}
	}
}
