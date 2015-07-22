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

package storybook.ui.dialog.edit;

import storybook.toolkit.I18N;

/**
 *
 * @author favdb
 */
public class DlgErrorMessage {

	public static String mandatoryField(String str) {
		return(I18N.getMsg(str)+":"+I18N.getMsg("msg.verifier.nonempty"));
	}

	public static String numberNotPositive(String str) {
		return(I18N.getMsg(str)+":"+I18N.getMsg("msg.verifier.integer.positive"));
	}

	public static String numberExists(String str) {
		return(I18N.getMsg(str)+":"+I18N.getMsg("msg.verifier.number.exists"));
	}

	public static String tooLong(String str,Integer val) {
		return(I18N.getMsg(str)+":"+I18N.getMsg("msg.verifier.too.long",val));
	}

	public static String wrongDate(String str) {
		return(I18N.getMsg(str)+":"+I18N.getMsg("msg.verifier.wrong.date"));
	}

	public static String wrongFormat(String str) {
		return(I18N.getMsg(str)+": "+I18N.getMsg("msg.verifier.wrong.format"));
	}

	public static String abbreviationExists(String str) {
		return(I18N.getMsg(str)+":"+I18N.getMsg("msg.verifier.number.exists"));
	}

}
