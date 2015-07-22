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

package storybook.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import storybook.model.hbn.entity.Location;
import storybook.toolkit.I18N;
import storybook.toolkit.net.NetUtil;

public class ShowInGoogleMapsAction extends AbstractAction {
	private Location location;

	public ShowInGoogleMapsAction(Location location) {
		super("Google Maps", I18N.getIcon("icon.small.map"));
		this.location = location;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		StringBuilder buf = new StringBuilder();
		buf.append(location.getCity())
			.append(",")
			.append(location.getAddress())
			.append(",")
			.append(location.getCountry());
		NetUtil.openGoogleMap(buf.toString());
	}
}
