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

package storybook.model.oldModel;

/**
 *
 * @author favdb
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.h2.jdbc.JdbcSQLException;
import storybook.SbApp;
import storybook.model.oldModel.MigrationConstants.ProjectSetting;

//@Deprecated
public class InternalPeer {

	public static final String KEY_DB_MODEL_VERSION = "dbversion";

	public static void create() {
		try {
			createTable();
		} catch (Exception e) {
			SbApp.error("InternalPeer.create()",e);
		}
	}

	public static void createTable() throws Exception {
		String sql;
		Statement stmt;

		// create
		SbApp.trace("InternalPeer.createTable(" + Internal.TABLE_NAME+")");
		sql = "create table IF NOT EXISTS "
			+ Internal.TABLE_NAME
			+ " ("
			+ Internal.COLUMN_ID + " identity primary key,"
			+ Internal.COLUMN_KEY + " varchar(64),"
			+ Internal.COLUMN_STRING_VALUE + " varchar(64),"
			+ Internal.COLUMN_INTEGER_VALUE + " int,"
			+ Internal.COLUMN_BOOLEAN_VALUE + " bool"
			+ ")";

		stmt = ModelMigration.getInstance().getConnection().createStatement();
		stmt.execute(sql);
	}

	public static List<Internal> doSelectAll() {
		try {
			if(!ModelMigration.getInstance().isConnectionOpen()){
				return new ArrayList<>();
			}

			List<Internal> list = new ArrayList<>();
			StringBuilder sql = new StringBuilder("select * from " + Internal.TABLE_NAME);
			sql.append(" order by " + Internal.COLUMN_KEY);

			Statement stmt = ModelMigration.getInstance().getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				Internal internal = makeInternal(rs);
				list.add(internal);
			}
			return list;
		} catch (SQLException e) {
			SbApp.error("InternalPeer.doSelectAll()", e);
		}
		return null;
	}

	public static Internal doSelectById(int id) throws Exception {
		String sql = "select * from " + Internal.TABLE_NAME + " where "
				+ Internal.COLUMN_ID + " = ?";
		PreparedStatement stmt = ModelMigration.getInstance()
				.getConnection().prepareStatement(sql);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		Internal internal = null;
		int c = 0;
		while (rs.next() && c < 2) {
			internal = makeInternal(rs);
			++c;
		}
		if (c == 0) {
			return null;
		}
		if (c > 1) {
			throw new Exception("more than one record found");
		}
		return internal;
	}

	public static Internal doSelectByKey(ProjectSetting ps) throws Exception {
		return doSelectByKey(ps.toString());
	}

	public static Internal doSelectByKey(String key) throws Exception {
		String sql = "select * "
			+ "from " + Internal.TABLE_NAME
			+ " where " + Internal.COLUMN_KEY + " = ?";
		PreparedStatement stmt = ModelMigration.getInstance()
				.getConnection().prepareStatement(sql);
		stmt.setString(1, key);
		ResultSet rs = stmt.executeQuery();
		Internal internal = null;
		int c = 0;
		while (rs.next() && c < 2) {
			internal = makeInternal(rs);
			++c;
		}
		if (c == 0) {
			return null;
		}
		if (c > 1) {
			throw new Exception("more than one record found");
		}
		return internal;
	}

	public static int doCount() {
		try {
			String sql = "select count(" + Internal.COLUMN_ID + ") from "
					+ Internal.TABLE_NAME;
			Statement stmt = ModelMigration.getInstance().getConnection()
					.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			SbApp.error("InternalPeer.doCount()", e);
		}
		return 0;
	}

	private static Internal makeInternal(ResultSet rs) throws SQLException {
		Internal internal = new Internal(rs.getInt(Internal.COLUMN_ID));
		internal.setKey(rs.getString(Internal.COLUMN_KEY));
		internal.setStringValue(rs.getString(Internal.COLUMN_STRING_VALUE));
		internal.setIntegerValue(rs.getInt(Internal.COLUMN_INTEGER_VALUE));
		internal.setBooleanValue(rs.getBoolean(Internal.COLUMN_BOOLEAN_VALUE));
		return internal;
	}

	public static boolean doDelete(Internal internal) throws Exception {
		if (internal == null) {
			return false;
		}
		String sql = "delete from " + Internal.TABLE_NAME
			+ " where " + Internal.COLUMN_ID + " = " + internal.getId();
		Statement stmt = ModelMigration.getInstance().getConnection().createStatement();
		stmt.execute(sql);
		return true;
	}

	public static String getDbModelVersion() {
		try {
			Internal dbModel = doSelectByKey(KEY_DB_MODEL_VERSION);
			if (dbModel == null) {
				return null;
			}
			return dbModel.getStringValue();
		} catch (JdbcSQLException e) {
			try {
				// try to get the value from an old DB model
				String sql = "select * " + "from " + Internal.TABLE_NAME
						+ " where " + Internal.COLUMN_KEY + " = '"
						+ KEY_DB_MODEL_VERSION + "'";
				Statement stmt = ModelMigration.getInstance()
						.getConnection().createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				rs.next();
				return rs.getString(Internal.COLUMN_OLD_VALUE);
			} catch (SQLException se) {
				SbApp.error("InternalPeer.getDbModelVersion()", se);
				return null;
			}
		} catch (Exception e) {
			SbApp.error("InternalPeer.getDbModelVersion()", e);
			return null;
		}
	}

	public static void setDbModelVersion() {
		setDbModelVersion(MigrationConstants.Application.DB_MODEL_VERSION.toString());
	}

	public static void setDbModelVersion(String version) {
		try {
			Internal internal = doSelectByKey(KEY_DB_MODEL_VERSION);
			if (internal == null) {
				internal = new Internal();
				internal.setKey(KEY_DB_MODEL_VERSION);
			}
			internal.setStringValue(version);
			internal.save();
		} catch (Exception e) {
			SbApp.error("InternalPeer.setDbModelVersion()", e);
		}
	}

	public static void setScaleFactorDefaults(){
		setScaleFactorBook(MigrationConstants.DEFAULT_SCALE_FACTOR_BOOK);
		setScaleFactorChrono(MigrationConstants.DEFAULT_SCALE_FACTOR_CHRONO);
		setScaleFactorManage(MigrationConstants.DEFAULT_SCALE_FACTOR_MANAGE);
	}

	public static void setScaleFactorChrono(int scaleFactor) {
		try {
			Internal internal = doSelectByKey(MigrationConstants.ProjectSetting.SCALE_FACTOR_CHRONO);
			if (internal == null) {
				internal = new Internal();
				internal.setKey(MigrationConstants.ProjectSetting.SCALE_FACTOR_CHRONO);
			}
			internal.setIntegerValue(scaleFactor);
			internal.save();
		} catch (Exception e) {
			SbApp.error("InternalPeer.setScaleFactorChrono("+scaleFactor+")", e);
		}
	}

	public static int getScaleFactorChrono() {
		try {
			Internal internal = doSelectByKey(MigrationConstants.ProjectSetting.SCALE_FACTOR_CHRONO);
			if (internal == null) {
				return MigrationConstants.DEFAULT_SCALE_FACTOR_CHRONO;
			}
			return internal.getIntegerValue();
		} catch (Exception e) {
			SbApp.error("InternalPeer.getScaleFactorChrono()", e);
		}
		return MigrationConstants.DEFAULT_SCALE_FACTOR_CHRONO;
	}

	public static void setScaleFactorBook(int scaleFactor) {
		try {
			Internal internal = doSelectByKey(MigrationConstants.ProjectSetting.SCALE_FACTOR_BOOK);
			if (internal == null) {
				internal = new Internal();
				internal.setKey(MigrationConstants.ProjectSetting.SCALE_FACTOR_BOOK);
			}
			internal.setIntegerValue(scaleFactor);
			internal.save();
		} catch (Exception e) {
			SbApp.error("InternalPeer.setScaleFactorChrono("+scaleFactor+")", e);
		}
	}

	public static int getScaleFactorBook() {
		try {
			Internal internal = doSelectByKey(MigrationConstants.ProjectSetting.SCALE_FACTOR_BOOK);
			if (internal == null) {
				return MigrationConstants.DEFAULT_SCALE_FACTOR_BOOK;
			}
			return internal.getIntegerValue();
		} catch (Exception e) {
			SbApp.error("InternalPeer.getScaleFactorChrono()", e);
		}
		return MigrationConstants.DEFAULT_SCALE_FACTOR_BOOK;
	}

	public static void setScaleFactorManage(int scaleFactor) {
		try {
			Internal internal = doSelectByKey(MigrationConstants.ProjectSetting.SCALE_FACTOR_MANAGE);
			if (internal == null) {
				internal = new Internal();
				internal.setKey(MigrationConstants.ProjectSetting.SCALE_FACTOR_MANAGE);
			}
			internal.setIntegerValue(scaleFactor);
			internal.save();
		} catch (Exception e) {
			SbApp.error("InternalPeer.setScaleFactorManage("+scaleFactor+")", e);
		}
	}

	public static int getScaleFactorManage() {
		try {
			Internal internal = doSelectByKey(MigrationConstants.ProjectSetting.SCALE_FACTOR_MANAGE);
			if (internal == null) {
				return MigrationConstants.DEFAULT_SCALE_FACTOR_MANAGE;
			}
			return internal.getIntegerValue();
		} catch (Exception e) {
			SbApp.error("InternalPeer.getScaleFactorManage()", e);
		}
		return MigrationConstants.DEFAULT_SCALE_FACTOR_MANAGE;
	}

	public static void saveManageCols(int cols) {
		try {
			Internal internal = InternalPeer
					.doSelectByKey(MigrationConstants.ProjectSetting.MANAGE_VIEW_COLS);
			if (internal == null) {
				internal = new Internal();
				internal.setKey(MigrationConstants.ProjectSetting.MANAGE_VIEW_COLS);
			}
			internal.setIntegerValue(cols);
			internal.save();
		} catch (Exception e) {
			SbApp.error("InternalPeer.saveManaeGols("+cols+")", e);
		}
	}

	public static int getManageCols() {
		try {
			Internal internal = InternalPeer
					.doSelectByKey(MigrationConstants.ProjectSetting.MANAGE_VIEW_COLS);
			if (internal == null) {
				return 4;
			}
			return internal.getIntegerValue();
		} catch (Exception e) {
			SbApp.error("InternalPeer.getManageCols()", e);
		}
		return 4;
	}

	public static void saveManageViewTextLength(int textLength) {
		try {
			Internal internal = InternalPeer
					.doSelectByKey(MigrationConstants.ProjectSetting.MANAGE_VIEW_TEXT_LENGTH);
			if (internal == null) {
				internal = new Internal();
				internal.setKey(MigrationConstants.ProjectSetting.MANAGE_VIEW_TEXT_LENGTH);
			}
			internal.setIntegerValue(textLength);
			internal.save();
		} catch (Exception e) {
			SbApp.error("InternalPeer.saveManageViewTextLength("+textLength+")", e);
		}
	}

	public static int getManageViewTextLength() {
		try {
			Internal internal = InternalPeer
					.doSelectByKey(MigrationConstants.ProjectSetting.MANAGE_VIEW_TEXT_LENGTH);
			if (internal == null) {
				return 100;
			}
			return internal.getIntegerValue();
		} catch (Exception e) {
			SbApp.error("InternalPeer.getManageViewTextLength()", e);
		}
		return 100;
	}

	public static boolean getReadingView() {
		try {
			Internal internal = InternalPeer
					.doSelectByKey(MigrationConstants.ProjectSetting.READING_VIEW);
			if (internal == null) {
				// saveReadingView();
				return false;
			}
			return internal.getBooleanValue();
		} catch (Exception e) {
			SbApp.error("InternalPeer.getReadingView()", e);
		}
		return false;
	}

	public static void saveReadingView(boolean readingView) {
		try {
			Internal internal = InternalPeer
					.doSelectByKey(MigrationConstants.ProjectSetting.READING_VIEW);
			if (internal == null) {
				internal = new Internal();
				internal.setKey(MigrationConstants.ProjectSetting.READING_VIEW);
			}
			internal.setBooleanValue(readingView);
			internal.save();
		} catch (Exception e) {
			SbApp.error("InternalPeer.saveReadingView("+(readingView?"true":false)+")", e);
		}
	}
}
