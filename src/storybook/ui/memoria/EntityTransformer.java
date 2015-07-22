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
package storybook.ui.memoria;

import storybook.model.EntityUtil;
import storybook.model.hbn.entity.AbstractEntity;
import org.apache.commons.collections15.Transformer;

public class EntityTransformer
	implements Transformer<AbstractEntity, String> {

	@Override
	public String transform(AbstractEntity p) {
		return EntityUtil.getToolTip(p);
	}
}