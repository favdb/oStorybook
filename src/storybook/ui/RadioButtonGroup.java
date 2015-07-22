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

package storybook.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import storybook.model.hbn.entity.AbstractEntity;

/**
 * @author martin
 *
 */
public class RadioButtonGroup {
	private HashMap<Integer, ImmutablePair<String, String>> groupMap;

	public RadioButtonGroup() {
		groupMap = new HashMap<Integer, ImmutablePair<String, String>>();
	}

	public void add(int number, String attr, String name) {
		groupMap.put(number, new ImmutablePair<String, String>(attr, name));
	}

	public HashMap<Integer, ImmutablePair<String, String>> getGroupMap() {
		return groupMap;
	}

	public void removeAttr(AbstractEntity entity, Integer key) {
		String attrName = getAttrName(key);
		String methodName = "remove" + WordUtils.capitalize(attrName);
		Method method;
		try {
			method = entity.getClass().getMethod(methodName);
			method.invoke(entity);
		} catch (NoSuchMethodException
			| SecurityException
			| IllegalAccessException
			| IllegalArgumentException
			| InvocationTargetException e) {
		}
	}

	public Boolean hasAttr(AbstractEntity entity, Integer key) {
		String attrName = getAttrName(key);
		String methodName = "has" + WordUtils.capitalize(attrName);
		Method method;
		try {
			method = entity.getClass().getMethod(methodName);
			Object ret = method.invoke(entity);
			return (Boolean) ret;
		} catch (NoSuchMethodException
			| SecurityException
			| IllegalAccessException
			| IllegalArgumentException
			| InvocationTargetException e) {
		}
		return null;
	}

	private String getAttrName(Integer key) {
		return (String) groupMap.get(key).getLeft();
	}
}
