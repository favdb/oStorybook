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

package storybook.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.hibernate.Session;
import storybook.SbConstants;
import storybook.SbConstants.PreferenceKey;
import storybook.SbApp;
import storybook.model.hbn.dao.PreferenceDAOImpl;
import storybook.toolkit.CommonTools;
import storybook.ui.MainFrame;

/**
 * @author martin
 *
 */
public class PreferenceModel extends AbstractModel {

	public PreferenceModel(MainFrame m) {
		super(m);
		init();
	}

	public final void init() {
		try {
			getPrefDir().mkdir();
			String dbName = getDbName();
			String configFile=getPrefDir().getAbsolutePath() + File.separator
					+ SbConstants.Storybook.PREFERENCE_DB_NAME
					+ SbConstants.Storybook.DB_CONFIG_EXT;
			File x=new File(configFile);
			if (!x.exists()) {
				createPreferenceFile(x);
			}
			initSession(dbName);

			// update application version
			Session session = beginTransaction();
			PreferenceDAOImpl dao = new PreferenceDAOImpl(session);
			dao.saveOrUpdate(PreferenceKey.STORYBOOK_VERSION.toString(), SbConstants.Storybook.PRODUCT_VERSION.toString());
			commit();
		} catch (Exception e) {
			SbApp.error("PreferenceModel.init()",e);
		}
	}

	private void createPreferenceFile(File x) throws FileNotFoundException {
		// create preference.cfg.xml
		try {
			x.createNewFile();
		} catch (IOException e) {
			SbApp.trace("Unable to create " + x.getAbsolutePath());
		}
		try {
			FileOutputStream f = new FileOutputStream(x);
			String s[]= {
					"<?xml version='1.0' encoding='utf-8'?>\n",
					"<!DOCTYPE hibernate-configuration PUBLIC\n",
					"        \"-//Hibernate/Hibernate Configuration DTD 3.0//EN\"\n",
					"        \"classpath://org/hibernate/hibernate-mapping-3.0.dtd\">\n",
					"\n",
					"<hibernate-configuration>\n",
					"   <session-factory>\n",
					"       <property name=\"hibernate.dialect\">org.hibernate.dialect.H2Dialect</property>\n",
					"       <property name=\"hibernate.connection.driver_class\">org.h2.Driver</property>\n",
					"       <!-- <property name=\"hibernate.connection.url\">jdbc:h2:/home/martin/tmp/Demo</property> -->\n",
					"       <property name=\"hibernate.connection.username\">sa</property>\n",
					"       <property name=\"hibernate.connection.password\"></property>\n",
					"\n",
					"       <!-- Automatic schema creation\n",
					"           validate: validate the schema, makes no changes to the database.\n",
					"           update: update the schema.\n",
					"           create: creates the schema, destroying previous data.\n",
					"           create-drop: drop the schema at the end of the session.\n",
					"       -->\n",
					"       <property name=\"hibernate.hbm2ddl.auto\">update</property>\n",
					"\n",
					"       <property name=\"connection.provider_class\">org.hibernate.connection.C3P0ConnectionProvider</property>\n",
					"       <property name=\"hibernate.c3p0.min_size\">0</property>\n",
					"       <property name=\"hibernate.c3p0.max_size\">1</property>\n",
					"       <property name=\"hibernate.c3p0.timeout\">5000</property>\n",
					"       <property name=\"hibernate.c3p0.max_statements\">100</property>\n",
					"       <property name=\"hibernate.c3p0.idle_test_period\">300</property>\n",
					"       <property name=\"hibernate.c3p0.acquire_increment\">2</property>\n",
					"\n",
					"       <property name=\"current_session_context_class\">thread</property>\n",
					"       <property name=\"hibernate.cache.provider_class\">org.hibernate.cache.HashtableCacheProvider</property>\n",
					"       <property name=\"hibernate.show_sql\">false</property>\n",
					"\n",
					"       <mapping resource=\"resources/hbn/Preference.hbm.xml\" />\n",
					"\n",
					"   </session-factory>\n",
					"</hibernate-configuration>"
			};
			for (int i=0; i<s.length; i++) {
				f.write(s[i].getBytes());
			}
			f.flush();
			try { f.close(); } catch (IOException e) { }
		} catch (IOException e) {
			SbApp.trace("Impossible de trouver le fichier");
		}


	}

	@Override
	public void fireAgain() {
	}

	private File getPrefDir() {
		File homeDir = CommonTools.getHomeDir();
		return new File(homeDir + File.separator + ".storybook5");
	}

	private String getDbName() {
		return getPrefDir() + File.separator
				+ SbConstants.Storybook.PREFERENCE_DB_NAME/*
				+ SbConstants.Storybook.DB_FILE_EXT*/;
	}
}
