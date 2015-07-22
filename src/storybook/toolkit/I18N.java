/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008 - 2011 Martin Mustun

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

import java.awt.Image;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import storybook.SbApp;


public class I18N {
	public final static String TIME_FORMAT = "HH:mm:ss";
	public final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static ResourceBundle iconResourceBundle = null;
	private static ResourceBundle messageResourceBundle = null;

	public static boolean isEnglish() {
		Locale locale = Locale.getDefault();
		Locale de = new Locale("en", "US");
		return locale.equals(de);
	}

	public static String getCountryLanguage(Locale locale){
		return locale.getLanguage() + "_" + locale.getCountry();
	}

	public static DateFormat getDateTimeFormatter() {
		return DateFormat.getInstance();
	}

	public static DateFormat getShortDateFormatter() {
		return DateFormat.getDateInstance(DateFormat.SHORT);
	}

	public static DateFormat getMediumDateFormatter() {
		return DateFormat.getDateInstance(DateFormat.MEDIUM);
	}

	public static DateFormat getLongDateFormatter() {
		return DateFormat.getDateInstance(DateFormat.LONG);
	}

	public static final String getMsg(String resourceKey, Object arg) {
		Object[] args = new Object[]{arg};
		return getMsg(resourceKey, args);
	}

	public static final String getMsg(String resourceKey, Object[] args) {
		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(Locale.getDefault());
		String pattern = getMessageResourceBundle().getString(resourceKey);
		formatter.applyPattern(pattern);
		return formatter.format(args);
	}

	public static final void setMnemonic(JMenuItem menuItem, int englishKey){
		setMnemonic(menuItem, englishKey, englishKey);
	}

	public static final void setMnemonic(JMenuItem menuItem, int englishKey, int germanKey){
		if(Locale.getDefault() == Locale.GERMANY){
			menuItem.setMnemonic(germanKey);
		} else {
			menuItem.setMnemonic(englishKey);
		}
	}

	public static final void setMnemonic(JMenu menu, int englishKey){
		setMnemonic(menu, englishKey, englishKey);
	}

	public static final void setMnemonic(JMenu menu, int englishKey, int germanKey){
		if(Locale.getDefault() == Locale.GERMANY){
			menu.setMnemonic(germanKey);
		} else {
			menu.setMnemonic(englishKey);
		}
	}

	public static final void initResourceBundles(Locale locale) {
		ResourceBundle.clearCache();
		messageResourceBundle = null;
		Locale.setDefault(locale);
		UIManager.getDefaults().setDefaultLocale(locale);
		SbApp.getInstance().setLocale(locale);
	}

	public static final ResourceBundle getMessageResourceBundle() {
		if (messageResourceBundle == null) {
			messageResourceBundle = ResourceBundle.getBundle("storybook.msg.messages", Locale.getDefault());
		}
		return messageResourceBundle;
	}

	public static String getMsg(String resourceKey) {
		ResourceBundle rb = getMessageResourceBundle();
		return rb.getString(resourceKey);
	}

	public static String getMsg(String resourceKey, boolean required) {
		ResourceBundle rb = getMessageResourceBundle();
		StringBuilder buf = new StringBuilder();
		if (required) {
			buf.append('*');
		}
		buf.append(rb.getString(resourceKey));
		return buf.toString();
	}

	public static String getMsgColon(String resourceKey) {
		return getMsgColon(resourceKey, false);
	}

	public static String getMsgDot(String resourceKey) {
		return getMsg(resourceKey) + "...";
	}

	public static String getMsgColon(String resourceKey, boolean required) {
		ResourceBundle rb = getMessageResourceBundle();
		StringBuilder buf = new StringBuilder();
		if (required) {
			buf.append('*');
		}
		buf.append(rb.getString(resourceKey));
		buf.append(':');
		return buf.toString();
	}

	public static final ResourceBundle getIconResourceBundle() {
		if (iconResourceBundle == null) {
			iconResourceBundle = ResourceBundle.getBundle("storybook.resources.icons.icons", Locale.getDefault());
		}
		return iconResourceBundle;
	}

	public static JLabel getIconLabel(String resourceKey) {
		return new JLabel(getIcon(resourceKey));
	}

	public static Icon getIcon(String resourceKey) {
		return getImageIcon(resourceKey);
	}

	public static ImageIcon getImageIcon(String resourceKey) {
		ResourceBundle rb = getIconResourceBundle();
		String name = rb.getString(resourceKey);
		ImageIcon icon = createImageIcon(SbApp.class, name);
		return icon;
	}

	public static ImageIcon createImageIcon(Class<?> c, String path) {
		java.net.URL imgURL = c.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public static Image getIconImage(String resourceKey) {
		ImageIcon icon = (ImageIcon) I18N.getIcon(resourceKey);
		return icon.getImage();
	}
}
