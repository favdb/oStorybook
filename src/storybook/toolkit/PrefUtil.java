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

package storybook.toolkit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.list.SetUniqueList;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import storybook.SbApp;
import storybook.SbConstants.PreferenceKey;
import storybook.model.DbFile;
import storybook.model.PreferenceModel;
import storybook.model.hbn.dao.PreferenceDAOImpl;
import storybook.model.hbn.entity.Preference;

/**
 * @author martin
 *
 */
public class PrefUtil {

	public static Preference get(PreferenceKey prefKey, Object defaultVal) {
		String key = prefKey.toString();
		PreferenceModel model = SbApp.getInstance().getPreferenceModel();
		Session session = model.beginTransaction();
		PreferenceDAOImpl dao = new PreferenceDAOImpl(session);
		Preference pref = dao.findByKey(key);
		model.commit();
		if (pref == null) {
			if (defaultVal instanceof String) {
				pref = new Preference(prefKey, (String) defaultVal);
			} else if (defaultVal instanceof Integer) {
				pref = new Preference(prefKey, (Integer) defaultVal);
			} else if (defaultVal instanceof Boolean) {
				pref = new Preference(prefKey, (Boolean) defaultVal);
			}
		}
		return pref;
	}

	public static Preference get(String strPrefKey, Object defaultVal) {
		PreferenceModel model = SbApp.getInstance().getPreferenceModel();
		Session session = model.beginTransaction();
		PreferenceDAOImpl dao = new PreferenceDAOImpl(session);
		Preference pref = dao.findByKey(strPrefKey);
		model.commit();
		if (pref == null) {
			if (defaultVal instanceof String) {
				pref = new Preference(strPrefKey, (String) defaultVal);
			} else if (defaultVal instanceof Integer) {
				pref = new Preference(strPrefKey, (Integer) defaultVal);
			} else if (defaultVal instanceof Boolean) {
				pref = new Preference(strPrefKey, (Boolean) defaultVal);
			}
		}
		return pref;
	}

	public static void set(PreferenceKey prefKey, Object val) {
		PreferenceModel model = SbApp.getInstance().getPreferenceModel();
		Session session = model.beginTransaction();
		PreferenceDAOImpl dao = new PreferenceDAOImpl(session);
		dao.saveOrUpdate(prefKey.toString(), val);
		model.commit();
	}

	public static void set(String strPrefKey, Object val) {
		PreferenceModel model = SbApp.getInstance().getPreferenceModel();
		Session session = model.beginTransaction();
		PreferenceDAOImpl dao = new PreferenceDAOImpl(session);
		dao.saveOrUpdate(strPrefKey, val);
		model.commit();
	}

	public static void delete(String strPrefKey) {
		Preference pref = get(strPrefKey, null);
		if (pref == null) {
			return;
		}
		PreferenceModel model = SbApp.getInstance().getPreferenceModel();
		Session session = model.beginTransaction();
		PreferenceDAOImpl dao = new PreferenceDAOImpl(session);
		dao.remove(pref.getKey());
		model.commit();
	}

	public static List<DbFile> getDbFileList() {
		PreferenceModel model = SbApp.getInstance().getPreferenceModel();
		Session session = model.beginTransaction();
		PreferenceDAOImpl dao = new PreferenceDAOImpl(session);
		Preference pref = dao.findByKey(PreferenceKey.RECENT_FILES.toString());
		ArrayList<DbFile> list = new ArrayList<DbFile>();
		if(pref == null){
			return list;
		}
		String[] values = StringUtils.split(pref.getStringValue(), "#");
		for (String val : values) {
			DbFile dbFile = new DbFile(new File(val));
			list.add(dbFile);
		}
		return list;
	}

	public static void setDbFileList(List<DbFile> list) {
		PreferenceModel model = SbApp.getInstance().getPreferenceModel();
		Session session = model.beginTransaction();
		PreferenceDAOImpl dao = new PreferenceDAOImpl(session);
		@SuppressWarnings("unchecked")
		List<DbFile> uniqueList = (List<DbFile>) SetUniqueList.decorate(list);
		try {
			if (uniqueList.size() > 10) {
				uniqueList = uniqueList.subList(uniqueList.size() - 10,
						uniqueList.size());
			}
		} catch (IndexOutOfBoundsException e) {
			// ignore
		}
		String val = StringUtils.join(uniqueList, "#");
		dao.saveOrUpdate(PreferenceKey.RECENT_FILES.toString(), val);
		model.commit();
	}

	public static void dump() {
		PreferenceModel model = SbApp.getInstance().getPreferenceModel();
		Session session = model.beginTransaction();
		PreferenceDAOImpl dao = new PreferenceDAOImpl(session);
		List<Preference> preferences = dao.findAll();
		for (Preference preference : preferences) {
			System.out.println("PrefUtil.dump(): " + preference);
		}
		model.commit();
	}
}
