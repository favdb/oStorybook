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

package storybook.model.hbn.entity;

import storybook.SbConstants.PreferenceKey;

public class Preference extends AbstractEntity {

	private String key;
	private String stringValue;
	private Integer integerValue;
	private Boolean booleanValue;
	private byte[] binValue;

	public Preference(){
	}

	public Preference(String key) {
		this.key = key;
	}

	public Preference(PreferenceKey key, String stringValue) {
		this(key.toString(), stringValue);
	}

	public Preference(PreferenceKey key, Integer integerValue) {
		this(key.toString(), integerValue);
	}

	public Preference(PreferenceKey key, Boolean booleanValue) {
		this(key.toString(), booleanValue);
	}

	public Preference(String key, String stringValue) {
		this.key = key;
		this.stringValue = stringValue;
	}

	public Preference(String key, Integer integerValue) {
		this.key = key;
		this.integerValue = integerValue;
	}

	public Preference(String key, Boolean booleanValue) {
		this.key = key;
		this.booleanValue = booleanValue;
	}

	public Preference(String key, byte[] binValue) {
		this.key = key;
		this.binValue = binValue;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Integer getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public byte[] getBinValue() {
		return binValue;
	}

	public void setBinValue(byte[] binValue) {
		this.binValue = binValue;
	}

	@Override
	public String toString() {
		return getKey() + ": '" + getStringValue() + "' / " + getIntegerValue() + " / " + getBooleanValue();
	}
}
