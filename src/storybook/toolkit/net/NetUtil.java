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

import java.awt.Desktop;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import storybook.SbApp;

import storybook.SbConstants;

public class NetUtil {

	private static String googleMapUrl;

	public static void openBrowser(String path) {
		SbApp.trace("NetUtil.openBrowser("+path+")");
		try {
			Desktop.getDesktop().browse(new URI(path));
		} catch (URISyntaxException | IOException e) {
			SbApp.error("NetUtil.openBrowser("+path+")", e);
		}
	}

	public static void openGoogleMap(String query) {
		SbApp.trace("NetUtil.openGoogleMap("+query+")");
		try {
			String queryEnc = URLEncoder.encode(query, "UTF-8");
			String path = getGoogleMapsUrl() + "/?q=" + queryEnc;
			openBrowser(path);
		} catch (UnsupportedEncodingException e) {
			SbApp.error("NetUtil.openGoogleMap("+query+")", e);
		}
	}

	public static String getGoogleMapsUrl() {
		SbApp.trace("NetUtil.getGoogleMapsUrl()");
		if (googleMapUrl == null || googleMapUrl.isEmpty()) {
			return SbConstants.DEFAULT_GOOGLE_MAPS_URL;
		}
		return googleMapUrl;
	}

	public static void setGoogleMapUrl(String url) {
		SbApp.trace("NetUtil.setGoogleMapUrl("+url+")");
		NetUtil.googleMapUrl = url;
	}

}
