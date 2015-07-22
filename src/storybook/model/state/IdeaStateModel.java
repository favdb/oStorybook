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
 * @author martin
 *
 */
public class IdeaStateModel extends AbstractStateModel {

	public IdeaStateModel() {
		super();
		states.add(new IdeaState(0, I18N.getMsg("msg.ideas.status.not_started")));
		states.add(new IdeaState(1, I18N.getMsg("msg.ideas.status.started")));
		states.add(new IdeaState(2, I18N.getMsg("msg.ideas.status.completed")));
		states.add(new IdeaState(3, I18N.getMsg("msg.ideas.status.abandoned")));
	}
}
