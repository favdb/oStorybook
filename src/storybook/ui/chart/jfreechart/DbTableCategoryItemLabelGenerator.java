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
package storybook.ui.chart.jfreechart;

import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.data.category.CategoryDataset;
import storybook.model.hbn.entity.AbstractEntity;

public class DbTableCategoryItemLabelGenerator
  implements CategoryItemLabelGenerator
{
	@Override
  public String generateColumnLabel(CategoryDataset paramCategoryDataset, int paramInt)
  {
    return "NOT USED";
  }

	@Override
  public String generateLabel(CategoryDataset paramCategoryDataset, int paramInt1, int paramInt2)
  {
    AbstractEntity localAbstractEntity = (AbstractEntity)paramCategoryDataset.getRowKey(paramInt1);
    return localAbstractEntity.toString();
  }

  public String generateRowLabel(CategoryDataset paramCategoryDataset, int paramInt)
  {
    return "NOT USED";
  }
}