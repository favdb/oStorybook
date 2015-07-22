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

import java.awt.Dimension;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import storybook.toolkit.I18N;

/**
 *
 * @author favdb
 */
//@Deprecated
public class MigrationConstants {
/**
 * Defines application wide constants.
 *
 * @author Martin Mustun
 */

	public static int DEFAULT_SCALE_FACTOR_CHRONO = 30;
	public static int DEFAULT_SCALE_FACTOR_BOOK = 50;
	public static int DEFAULT_SCALE_FACTOR_MANAGE = 2;

	/**
	 * Application constants.
	 */
	public enum Application {
		/** The application name. */
		NAME("Storybook"),
		NAME_PRO("Storybook Pro"),
		/** The application slogan. */
		SLOGAN(NAME + " - Open Source Novel Writing Software"),
		SLOGAN_PRO(NAME_PRO + " - Open Source Novel Writing Software"),
		/** The application release date. */
		RELEASE_DATE("2011-12-05"),
		/** The application version. */
		VERSION("3.2.0"),
		/** The version for Storybook Pro. */
		VERSION_PRO("3.2.0"),
		/** pro version */
		IS_PRO_VERSION("true"),
		/** for journalistic review */
		IS_FOR_REVIEW("false"),
		/** The application slogan and version. */
		SLOGAN_AND_VERSION(SLOGAN + " - Version " + VERSION),
		SLOGAN_AND_VERSION_PRO(SLOGAN_PRO + " - Version " + VERSION_PRO),
		/** The application home page URL */
		URL("http://www.novelist.ch"),
		/** The application slogan and URL. */
		SLOGAN_AND_URL(SLOGAN + " - " + URL),
		/** The author used in the copyright. */
		COPYRIGHT_AUTHOR("Martin Mustun"),
		/** The year used in the copyright. */
		COPYRIGHT_YEAR("2008 - 2011"),
		/** The Database model version.
		 * @see DbTools#checkAndAlterModel() */
		DB_MODEL_VERSION("1.5"),
		// for recovery tool
//		DB_MODEL_VERSION("1.3"),
		/** The name of the XML file.
		 * @see Settings */
		SETTINGS_XML_FILE("configuration.xml"),
		/** The name of the log4j XML file. */
		LOG4J_XML_FILE("/log4j.xml"),
		GOPRO_URL("http://www.novelist.ch/gopro/3_1");

		final private String text;
		private Application(String text) { this.text = text; }
		@Override
		public String toString() { return text; }
		public boolean toBoolean(){ return Boolean.parseBoolean(this.text); }
		public int toInteger(){ return Integer.parseInt(this.text); }
	}

	// month is 0-based
	public static Calendar expireDate = new GregorianCalendar(2011, 10, 07);

	/**
	 * icon sizes
	 */
	public enum IconSize {
		SMALL(new Dimension(16, 16)),
		MEDIUM(new Dimension(32, 32)),
		MEDIUM_WIDE(new Dimension(32, 16)),
		LARGE(new Dimension(32, 32));
		final private Dimension dim;
		private IconSize(Dimension text) { this.dim = text; }
		@Override
		public String toString() { return dim.toString(); }
		public Dimension getDimension() { return dim; }
	}

	/**
	 * project sub-directories
	 */
	public enum ProjectDirectory {
		PROJECTS("projects"),
		BACKUP("backups"),
		USER_DICTS("dicts");
		final private String text;
		private ProjectDirectory(String text) { this.text = text; }
		@Override
		public String toString() { return text; }
	}

	/**
	 * program sub-directories
	 */
	public enum ProgramDirectory {
		DICTS("dict"),
		LIB("lib");
		final private String text;
		private ProgramDirectory(String text) { this.text = text; }
		@Override
		public String toString() { return text; }
	}

	/**
	 * common component names
	 */
	public enum ComponentName {
		OK_BUTTON("ok"),
		CANCEL_BUTTON("cancel"),
		COLOR_CHOOSER("colorchooser");
		final private String text;
		private ComponentName(String text) { this.text = text; }
		@Override
		public String toString() { return text; }
	}

	/**
	 * Project settings. Note key length must be <= 16 (DB field).
	 */
	public enum ProjectSetting {
		VIEW("view"),
		VIEW_BOOK("bookview"),
		VIEW_CHRONO("chronoview"),
		VIEW_MANAGE("manageview"),
		VIEW_POV("povview"),
		SCALE_CHRONO("chronoscale"),
		SCALE_BOOK("bookscale"),
		SCALE_MANAGE("managescale"),
		PART_ID("partid"),
		RAW_EXPORT_LAST_FILENAME("rawexplastfile"),
		EXPORT_LAST_DIRECTORY("exportlastdir"),
		MEMORIA_TREE("memoriaballoon"),
		SCALE_FACTOR_CHRONO("sfchrono"),
		SCALE_FACTOR_BOOK("sfbook"),
		SCALE_FACTOR_MANAGE("sfmanage"),
		/** Chrono view alignment, @see {@link NewChronoContentPanel.Alignment} */
		CHRONO_VIEW_ALIGNMENT("chronoalignment"),
		MANAGE_VIEW_COLS("manageviewcols"),
		MANAGE_VIEW_TEXT_LENGTH("manageviewlen"),
		READING_VIEW("readingview");
		final private String dbKey;
		private ProjectSetting(String dbKey) { this.dbKey = dbKey; }
		@Override
		public String toString() { return dbKey; }
		public static ProjectSetting valueOfText(String str) {
			for (ProjectSetting ps : ProjectSetting.values()) {
				if (ps.dbKey.equals(str)) {
					return ps;
				}
			}
			return null;
		}
	}

	//
	// settings stored in the XML configuration file
	//

	/**
	 * Setting keys used in the XML file
	 *
	 * @see Application#SETTINGS_XML_FILE
	 */
	public static enum Settings {
		/** Directory where the resources such as localization files are located */
		resourceDir
	}

	//
	// preferences stored in DB
	//

	/**
	 * Preference values used for languages.
	 *
	 * @see Preference#LANG
	 */
	public static enum Language {
		/** English */
		en_US,
		/** German */
		de_DE,
		/** Spanish */
		es_ES,
		/** Danish */
		da_DK,
		/** Brazilian Portuguese */
		pt_BR,
		/** Italian */
		it_IT,
		/** French */
		fr_FR,
		/** Dutch */
		nl_NL,
		/** Hebrew */
		iw_IL,
		/** Finnish */
		fi_FI,
		/** Swedish */
		sv_SE,
		/** Greek */
		el_GR,
		/** Japanese */
		ja_JP,
		/** Traditional Chinese (Hong Kong) */
		zh_HK,
		/** Russian */
		ru_RU,
		/** Czech */
		cs_CZ,
		/** Simplified Chinese */
		zh_CN,
		/** Polish */
		pl_PL;
		public String getI18N() {
			return I18N.getMsg("msg.common.language." + name());
		}
		public Locale getLocale() {
			Locale locale;
			switch (this) {
			case en_US: locale = Locale.US; break;
			case de_DE: locale = Locale.GERMANY; break;
			case es_ES: locale = new Locale("es", "ES"); break;
			case da_DK: locale = new Locale("da", "DK"); break;
			case pt_BR: locale = new Locale("pt", "BR"); break;
			case it_IT: locale = new Locale("it", "IT"); break;
			case fr_FR: locale = new Locale("fr", "FR"); break;
			case nl_NL: locale = new Locale("nl", "NL"); break;
			case iw_IL: locale = new Locale("iw", "IL"); break;
			case fi_FI: locale = new Locale("fi", "FI"); break;
			case sv_SE: locale = new Locale("sv", "SE"); break;
			case el_GR: locale = new Locale("el", "GR"); break;
			case ja_JP: locale = new Locale("ja", "JP"); break;
			case zh_HK: locale = new Locale("zh", "HK"); break;
			case ru_RU: locale = new Locale("ru", "RU"); break;
			case cs_CZ: locale = new Locale("cs", "CZ"); break;
			case zh_CN: locale = new Locale("zh", "CN"); break;
			case pl_PL: locale = new Locale("pl", "PL"); break;
			default: locale = Locale.US;
			}
			return locale;
		}
	};

	/**
	 * Preference values used for spell checker.
	 *
	 * @see Preference#SPELLING
	 */
	public static enum Spelling {
		none, en_US, de_DE, es_ES, it_IT, fr_FR, ru_RU, nl_NL, pl_PL;
		public String getI18N() {
			if (this == none) {
				return I18N.getMsg("msg.pref.spelling.no");
			}
			return I18N.getMsg("msg.common.language." + name());
		}
	}

	/**
	 * Preference values used for look and feel settings. Some Look and Feels
	 * are not a hundred percent supported. Only <b>System</b> and <b>Cross
	 * Platform</b> has to be supported fully.
	 *
	 * @see Preference#LAF
	 */
	public static enum LookAndFeel {
		/** Cross Platform Look and Feel */
		cross,
		/** System Look and Feel */
		system,
		/** Tiny Look and Feel, see <a href="http://www.muntjak.de/hans/java/tinylaf/index.html">Tiny LaF Homepage</a>. */
		tiny,
		/** Tonic Look and Feel, see <a href="http://www.digitprop.com/tonic/tonic.php">Tonic Homepage</a>. */
		tonic,
		/** Substance Look and Feel, see <a href="https://substance.dev.java.net/">Substance Homepage</a>. */
		substance;
		public String getI18N() {
			return I18N.getMsg("msg.pref.laf." + name());
		}
	}

	/**
	 * Preference values used for start options.
	 *
	 * @see Preference#START
	 */
	public static enum StartOption {
		/** Open the last opened project on start. */
		openproject,
		/** Do nothing on start. */
		donothing;
		public String getI18N() {
			return I18N.getMsg("msg.pref.start." + name());
		}
	}

	public enum Preference {
		/** Preference key used for language. */
		LANG("language"),
		/** Preference key used for spell checker. */
		SPELLING("spelling"),
		/** Preference key used for the last opened file. */
		LAST_OPENED_FILE("lastopenedfile"),
		/** Preference key used for last opened directory. */
		LAST_OPENED_DIRECTORY("lastopeneddirectory"),
		/** Recent opened files. */
		RECENT_FILES("recentfiles"),
		/** Preference key used for start options. */
		START("start"),
		/** Preference key used for look and feel. */
		LAF("lookandfeel"),
		/** Preference key used for the exit confirmation. */
		CONFIRM_EXIT("confirmexit"),
		/** Preference key used for the update check. */
		CHECK_UPDATES("checkupdates"),
		/** Preference key used for Google Map URL. */
		GOOGLE_MAP_URL("googlemapurl"),
		/** Preference key used for the default Google Map URL. */
		GOOGLE_MAP_DEFAULT_URL("http://maps.google.com"),
		/** Preference key used for the window width. */
		WINDOW_WIDTH("windowwidth"),
		/** Preference key used for the window height. */
		WINDOW_HEIGHT("windowheight"),
		/** Preference key used for the window X position. */
		WINDOW_X("windowx"),
		/** Preference key used for the window Y position. */
		WINDOW_Y("windowy"),
		/** Preference key used for the window maximize flag. */
		WINDOW_MAXIMIZE("windowmaximize"),
		/** Preference key used for the default font name. */
		FONT_DEFAULT_NAME("fontdefaultname"),
		/** Preference key used for the default font style. */
		FONT_DEFAULT_STYLE("fontdefaultstyle"),
		/** Preference key used for the default font size. */
		FONT_DEFAULT_SIZE("fontdefaultsize"),
		/** Preference key used if background gradient is shown or not. */
		SHOW_BG_GRADIENT("showbggradient"),
		/** Preference key used if date difference is shown or not.*/
		SHOW_DATE_DIFFERENCE("showdatedifference"),
		/** Memoria window width. */
		MEMORIA_WIDTH("memoriawidth"),
		/** Memoria window height. */
		MEMORIA_HEIGHT("memoriaheight");
		final private String text;
		private Preference(String text) { this.text = text; }
		@Override
		public String toString() { return text; }
	}

	public enum ActionKey {
		CHAPTER_OR_SCENE("chapter_or_scene"),
		FILE("file"),
		MEMORIA_DBOBJ("memoria_dbobj");
		final private String key;
		private ActionKey(String key) { this.key = key; }
		public String getKey() { return key; };
		@Override
		public String toString(){ return key; };
	}

	/**
	 * Default life cycles for men / women.
	 */
	public enum LifeCycle {
		CHILDHOOD_YEARS(12),
		ADOLESCENCE_YEARS(6),
		ADULTHOOD_YEARS(47),
		RETIREMENT_YEARS(14);
		final private int years;
		private LifeCycle(int years) { this.years = years; }
		public int getYears(){ return this.years; }
	}
}
