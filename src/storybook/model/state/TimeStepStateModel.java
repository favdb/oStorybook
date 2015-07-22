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

package storybook.model.state;

import storybook.toolkit.I18N;

/**
 * @author jean
 *
 */
public class TimeStepStateModel extends AbstractStateModel {

	public enum State { MINUTE, HOUR, DAY, MONTH, YEAR };

	public TimeStepStateModel() {
		super();
		states.add(new TimeStepState(State.MINUTE.ordinal(),
				I18N.getMsg("msg.timeevent.combo.MINUTE"),
				null));
		states.add(new TimeStepState(State.HOUR.ordinal(),
				I18N.getMsg("msg.timeevent.combo.HOUR"),
				null));
		states.add(new TimeStepState(State.DAY.ordinal(),
				I18N.getMsg("msg.timeevent.combo.DAY"),
				null));
		states.add(new TimeStepState(State.MONTH.ordinal(), 
				I18N.getMsg("msg.timeevent.combo.MONTH"),
				null));
		states.add(new TimeStepState(State.YEAR.ordinal(),
				I18N.getMsg("msg.timeevent.combo.YEAR"),
				null));
	}
}
