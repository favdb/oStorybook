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
package storybook.toolkit.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;

import javax.swing.SwingUtilities;

import storybook.SbConstants;
import storybook.toolkit.I18N;
import storybook.toolkit.swing.SwingUtil;
import storybook.ui.net.BrowserDialog;

public class Updater {

	public static boolean checkForUpdate() {
		if (SbConstants.URL.DO_UPDATE.toString().equals("true")) {
			try {
				// get version
				URL url = new URL(SbConstants.URL.VERSION.toString());
				String versionStr;
				try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
					String inputLine = "";
					versionStr = "";
					int c = 0,nc=-1;
					while ((inputLine = in.readLine()) != null) {
						versionStr = inputLine;
						if (inputLine.contains("Versions")) {
							nc = c + 1;
						}
						if (c == nc) {
							break;
						}
						c++;
					}
				}

				// compare version
				int remoteVersion = calculateVersion(versionStr);
				int localVersion = calculateVersion(SbConstants.Storybook.PRODUCT_VERSION
					.toString());
				// for testing
//			remoteVersion = 4002000;
				if (localVersion < remoteVersion) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							String locale = Locale.getDefault().toString();
							String updateUrl = SbConstants.URL.UPDATE.toString() + locale;
							BrowserDialog dlg = new BrowserDialog(updateUrl, I18N.getMsg("msg.update.title"), 600, 300);
							SwingUtil.showModalDialog(dlg, null);
						}
					});
					return false;
				}
			} catch (SocketException | UnknownHostException e) {
				return true;
			} catch (Exception e) {
				System.err.println("Updater.checkForUpdate() Exception:" + e.toString());
			}
		}
		return true;
	}

	private static int calculateVersion(String str) {
		String[] s = str.split(".");
		if (s.length != 3) {
			return -1;
		}
		int ret = 0;
		ret += Integer.parseInt(s[0]) * 1000000;
		ret += Integer.parseInt(s[1]) * 1000;
		ret += Integer.parseInt(s[2]);
		return ret;
	}
}
