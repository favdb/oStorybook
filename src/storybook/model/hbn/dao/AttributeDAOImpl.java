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

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import storybook.model.hbn.entity.Attribute;

public class AttributeDAOImpl extends SbGenericDAOImpl<Attribute, Long> implements
		AttributeDAO {

	public AttributeDAOImpl() {
		super();
	}

	public AttributeDAOImpl(Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Attribute> findAll() {
		Query query = session.createQuery("select from Attribute order by key");
		List<Attribute> ret = (List<Attribute>) query.list();
		return ret;
	}

	@SuppressWarnings("unchecked")
	public List<String> findKeys() {
		Query query = session.createQuery("select distinct key from Attribute order by key");
		List<String> ret = (List<String>) query.list();
		return ret;
	}

	public int deleteOrphans() {
		try {
			String sql = "delete from attribute where id in ("
					+ " select id from attribute as a"
					+ " left join person_attribute pa on a.id=pa.attribute_ID"
					+ " where pa.person_id is null" + " )";
			Query query = session.createSQLQuery(sql);
			return query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
